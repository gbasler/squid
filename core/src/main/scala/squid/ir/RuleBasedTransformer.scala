package squid
package ir

import squid.lang.Base
import squid.utils.MacroUtils.{MacroSetting, MacroDebug, MacroDebugger}
import utils._
import utils.CollectionUtils._

import scala.language.experimental.macros
import utils.UnknownContext

case class RewriteAbort(msg: String = "") extends Exception

/** Type and context-safe interface for rewrite-rule-based program transformation.
  * It checks that rewrite rules rewrite things to the same type, and with a weaker or identical context.
  * Turns {{{ rewrite { case ir"<pat a,b..>" => <code a,b..> ... }}} expressions into expressions of the form:
  * {{{ registerRule(ir"<pat a,b..>", (x: Extract) => val a,b.. = x; <code a,b..>) }}}
  * 
  * TODO: better handling of situations where cases do not have the right shape
  * 
  * TODO: use standard errors rather than bare aborts
  * 
  **/
trait RuleBasedTransformer extends Transformer {
  import base._
  
  def rewrite(tr: IR[Any,UnknownContext] => IR[Any,_]): Unit = macro RuleBasedTransformerMacros.rewrite
  @MacroSetting(debug = true) def dbg_rewrite(tr: IR[Any,UnknownContext] => IR[Any,_]): Unit = macro RuleBasedTransformerMacros.rewrite
  
  /** Register a transformation where the pattern is enceded with an extractor ("xtor") Rep and an associated function
    * `code` that makes use of what is extracted. `code` may return None in case a condition for its application is not
    * met (eg: the case in a rewriting had a condition guard) */
  def registerRule(xtor: Rep, code: Extract => Option[Rep]): Unit
  
}

import reflect.macros.whitebox
class RuleBasedTransformerMacros(val c: whitebox.Context) {
  
  val debug = { val mc = MacroDebugger(c); mc[MacroDebug] }
  
  object Helpers extends {val uni: c.universe.type = c.universe} with quasi.ScopeAnalyser[c.universe.type]
  import Helpers._
  
  def termRewrite(tr: c.Tree) = {
    import c.universe._
    
    val (baseTree,termTree,typ,ctx) = c.macroApplication match {
      case q"$base.InspectableIROps[$t,$c]($term).rewrite($_)" =>
        (base, term, t.tpe, c.tpe)
      case q"$base.InspectableIROps[$t,$c]($term).dbg_rewrite($_)" =>
        (base, term, t.tpe, c.tpe)
    }
    val base = baseTree
    debug(s"Found base `${showCode(base)}` of type `${base.tpe}`")
    
    val transName = TermName(c.freshName("trans"))
    
    val (rwTree,ctxTrans) = rewriteImpl(tr, q"$transName", base)
    
    //debug("ctxTrans:",ctxTrans)
    
    val outputContext = {
      val (initBases, initVars) = bases_variables(ctx)
      val (newBases, newVars) = ctxTrans map {
        case (extractedCtx, constructedCtx, constructedPos) =>
          debug(s"XC $extractedCtx  CC $constructedCtx")
          val (bases,variables) = bases_variables(constructedCtx)
          debug(s"bases: $bases -- ${bases filterNot (_ <:< extractedCtx)}")
          (bases filterNot (_ <:< extractedCtx), variables)
      } unzip;
      mkContext(initBases :: newBases flatten, initVars :: newVars flatten)
    }
    
    debug("outputContext:",outputContext)
    
    //val $transName = new _root_.scp.ir.SimpleRuleBasedTransformer with _root_.scp.ir.TopDownTransformer {
    //val $transName: _root_.scp.ir.Transformer{val base: ${base.tpe} } = new _root_.scp.ir.SimpleTransformer {
    //object $transName extends _root_.scp.ir.SimpleRuleBasedTransformer with _root_.scp.ir.TopDownTransformer {  // Note: when erroneous, raises weird "no progress" compiler error that a `val _ = new _` would not
    // TODO give possibility to chose transformer (w/ implicit?)
    val res = q""" 
    object $transName extends _root_.squid.ir.FixPointRuleBasedTransformer with _root_.squid.ir.TopDownTransformer {  // Note: when erroneous, raises weird "no progress" compiler error that a `val _ = new _` would not
      val base: ${base.tpe} = $base
    }
    ${rwTree}
    $base.`internal IR`[$typ,$outputContext]($transName.optimizeRep($termTree.rep.asInstanceOf[$transName.base.Rep]).asInstanceOf[$base.Rep])
    """
    
    debug("Generated: " + showCode(res))
    
    //debug("Generated: " + (res))
    //debug("Generated: " + showCode(c.untypecheck(res)))
    //debug("Generated: " + showRaw(c.untypecheck(res)))
    
    //res // crashes
    c.untypecheck(res)
    //c.parse(showCode(res))
    
  }
  
  
  def rewrite(tr: c.Tree) = {
    import c.universe._
    
    val trans = c.macroApplication match {
      case q"$trans.rewrite($_)" => trans
      case q"$trans.dbg_rewrite($_)" => trans
    }
    var base = c.typecheck(q"$trans.base")
    base = internal.setType(base, c.typecheck(tq"$trans.base.type", c.TYPEmode).tpe)
    // ^ For some reason, when `trans` is of the form `SomeClass.this`, we get for `base.tpe` a widened type... so here we explicitly ask for the singleton type
    debug(s"Found base `${showCode(base)}` of type `${base.tpe}`")
    
    //val res = rewriteImpl(tr, trans, base)
    val (res,ctxTrans) = rewriteImpl(tr, trans, base)
    
    ctxTrans foreach {
      case (extractedCtx, constructedCtx, constructedPos) =>
        if (!(extractedCtx <:< constructedCtx))
          c.abort(constructedPos,
            if (constructedCtx <:< extractedCtx)
              s"Cannot rewrite a term of context $extractedCtx to a stricter context $constructedCtx"
            else s"Cannot rewrite a term of context $extractedCtx to an unrelated context $constructedCtx")
    }
    
    debug("Generated: " + showCode(res))
    
    c.untypecheck(res)
  }
  
  
  /** Returns the result rewrite-registering tree plus a list of tuples of the extracted context,
    * the constructed context, and the constructed context tree position */
  def rewriteImpl(tr: c.Tree, trans: c.Tree, base: c.Tree): (c.Tree, List[(c.Type,c.Type,c.Position)]) = {
    import c.universe._
    
    val cases = tr match {
      case Function(ValDef(_, name, _, _) :: Nil, q"$_ match { case ..$cases }") => cases
    }
    
    //debug(cases)
    
    var n = 0
    
    val (registrations, ctxTrans) = cases map {
      case cas @ CaseDef(pat, cond, expr) =>
        
        n += 1
        debug(s" --- CASE #$n --- ")
        
        def notFound(obj: String) = c.abort(cas.pos, s"Could not determine $obj for that case.")
        
        val extractedType = (pat.find {
          case td@TypeDef(_, TypeName("$ExtractedType$"), _, _) => true
          case _ => false
        } getOrElse notFound("extracted type")).symbol.asType.typeSignature
        val extractedCtx = (pat.find {
          case td@TypeDef(_, TypeName("$ExtractedContext$"), _, _) => true
          case _ => false
        } getOrElse notFound("extracted type")).symbol.asType.typeSignature
        
        val constructedPos = expr match {
          case Block(_, r) => r.pos
          case _ => expr.pos
        }
        val constructedCtx = expr.tpe.baseType(symbolOf[Base#IR[_, _]]) match {
          case tpe@TypeRef(tpbase, _, constructedType :: constructedCtx :: Nil) =>
            assert(tpbase =:= base.tpe, s"Base types `$tpbase` and `${base.tpe}` (type of ${showCode(base)}) are different.")
            
            if (constructedType <:< extractedType) {
              //debug("Rewriting " + extractedType)
            } else {
              c.abort(constructedPos, s"Cannot rewrite a term of type $extractedType to a different type $constructedType")
            }
            constructedCtx
            
          case NoType if Nothing <:< expr.tpe => // For example if the user ends a rewrite with a 'throw' or '???' for debugginh purposes
            // FIXME later we get: Error:(182, 13) value rep is not a member of Nothing
            Any
            
          case NoType =>
            c.abort(constructedPos, s"This rewriting does not produce a ${base.tpe}.IR[_,_] type as a return.")
            
        }
        
        debug("XC",extractedCtx,"CC",constructedCtx)
        
        //val baseBinding = pat.find {
        //  case q"val __b__ : $_ = $_" => true
        //  case _ => false
        //} get;
        
        val termTree = pat.find {
          case q"val _term_ : $_ = __b__.wrapExtract($termTree)" => true
          case _ => false
        } match { case Some(ValDef(_, _, _, rhs)) => rhs } // Note: we keep the `wrapExtract`
        //} match { case Some(ValDef(_, _, _, rhs)) => rhs  case _ => return q"???" -> Nil } // Note: we keep the `wrapExtract`
        //} match { case Some(ValDef(_, _, _, q"__b__.wrapExtract($termTree)")) => termTree }
        
        
        val (xtor, subPatterns) = pat match {
          case UnApply(fun, args) => fun -> args
        }
        
        /** List[(name of the map in the Extract artifact, pattern name)] */
        val patNames = xtor.collect {
          case q"_maps_.${tn @ TermName(tnStr)}.apply(${Literal(Constant(name: String))})" =>
            assert(tnStr.startsWith("_"))
            tn -> name
        }
        
        assert(subPatterns.size == patNames.size)
        
        /** Rewrites UnApply trees introduced by typechecking to normal trees recursively, so we can typecheck again later. */
        def patMat(scrut: Tree, pat: Tree, body: Tree): Tree = {
          pat match {
            case UnApply(fun, args) =>
              val unapp = TreeOps(fun) transform { case Ident(TermName("<unapply-selector>")) => scrut }
              if (fun.tpe <:< typeOf[Boolean]) {
                q"if ($unapp) $body else None"
              }
              else {
                val args_names = args.zipWithIndex map (ai => ai._1 -> TermName("__"+ai._2))
                q"$unapp flatMap { case (..${args_names map { case (arg, nam) => pq"$nam: ${arg.tpe}" }}) => ${
                  (args_names :\ body) {
                    case ((arg, nam), acc) => patMat(q"$nam", arg, acc)
                  }
                }}"
              }
            case Bind(name: TermName, pq"_") => q"val $name = $scrut; ..$body"
          }
        }
        
        val r = q"$trans.registerRule($termTree.asInstanceOf[$trans.base.Rep], ((__extr__ : $base.Extract) => ${
        //val r = q"$baseBinding; $trans.registerRule($termTree.asInstanceOf[$trans.base.Rep], (__extr__ : $base.Extract) => ${
          ((subPatterns zip patNames) :\ (if (cond.isEmpty) q"_root_.scala.Option($expr.rep).asInstanceOf[Option[$trans.base.Rep]]" else q"if ($cond) _root_.scala.Some($expr.rep) else _root_.scala.None")) {
            
            case ((pat, (mapName @ TermName("_1"), name)), acc) =>
              patMat(q"__b__.`internal IR`(__extr__.$mapName($name))", pat, acc)
              
            case ((pat, (mapName @ TermName("_2"), name)), acc) =>
              patMat(q"__b__.`internal IRType`(__extr__.$mapName($name))", pat, acc)
              
            case ((pat, (mapName @ TermName("_3"), name)), acc) =>
              patMat(q"(__extr__.$mapName($name)) map ((__rep__ : __b__.Rep) => __b__.`internal IR`(__rep__))", pat, acc)
              
          }
        }).asInstanceOf[$trans.base.Extract => _root_.scala.Option[$trans.base.Rep]])"
        
        //debug(r)
        
        r -> (extractedCtx, constructedCtx, constructedPos)
    } unzip;
    
    
    val res = q"val __b__ : ${base.tpe} = $base; ..$registrations"
    //val res = q"..$registrations"
    
    //debug("Result: "+showCode(res))
    
    // Resets previously resolved identifier symbols; necessary since we're defining a new binding for __b__
    val gen = TreeOps(res) transform { case Ident(name) => Ident(name) } // TODO properly fix owner chain corruptions
    //val gen = res
    
    //debug("Typed: "+c.typecheck(gen))
    //debug("Generated: "+(gen))
    //debug("Generated: "+showCode(gen))
    
    /*debug(" --- GENERATED ---\n"+showCode(gen))*/
    
    gen -> ctxTrans
    
  }
  
}






