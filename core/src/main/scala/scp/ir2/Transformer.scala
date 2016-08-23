package scp
package ir2

import scp.utils.MacroUtils.{MacroDebug, MacroDebugger, MacroSetting}
import utils._
import utils.CollectionUtils._

import scala.language.experimental.macros
import utils.UnknownContext

/** Type and context-safe interface for program transformation.
  * It checks that rewrite rules rewrite things to the same type, and with a weaker or identical context.
  * Turns {{{ rewrite { case ir"<pat a,b..>" => <code a,b..> ... }}} expressions into expressions of the form:
  * {{{ registerRule(ir"<pat a,b..>", (x: Extract) => val a,b.. = x; <code a,b..>) }}}
  * 
  * TODO: better handling of situations where cases do not have the right shape
  * 
  * TODO: use standard errors rather than bare aborts
  * 
  **/
trait Transformer {
  val base: lang2.Base
  import base._
  
  object TranformerDebug extends PublicTraceDebug
  
  def rewrite(tr: IR[Any,UnknownContext] => IR[Any,_]): Unit = macro TransformerMacros.rewrite
  @MacroSetting(debug = true) def dbg_rewrite(tr: IR[Any,UnknownContext] => IR[Any,_]): Unit = macro TransformerMacros.rewrite
  
  /** Register a transformation where the pattern is enceded with an extractor ("xtor") Rep and an associated function
    * `code` that makes use of what is extracted. `code` may return None in case a condition for its application is not
    * met (eg: the case in a rewriting had a condition guard) */
  def registerRule(xtor: Rep, code: Extract => Option[Rep]): Unit
  
}

import reflect.macros.whitebox
class TransformerMacros(val c: whitebox.Context) {
  
  val debug = { val mc = MacroDebugger(c); mc[MacroDebug] }
  
  object Helpers extends {val uni: c.universe.type = c.universe} with meta.UniverseHelpers[c.universe.type]
  import Helpers._
  
  /** TODO infer _correct_ type and context */
  def termRewrite(tr: c.Tree) = {
    import c.universe._
    
    val (baseTree,termTree,typ,ctx) = c.macroApplication match {
      case q"$base.InspectableIROps[$t,$c]($term).rewrite($_)" =>
        (base, term, t.tpe, c.tpe)
      case q"$base.InspectableIROps[$t,$c]($term).dbg_rewrite($_)" =>
        (base, term, t.tpe, c.tpe)
    }
    val base = baseTree
    
    val transName = TermName(c.freshName("trans"))
    val termName = TermName(c.freshName("term"))
    val res = q"""
    val $termName = $termTree 
    object $transName extends _root_.scp.ir2.SimpleTransformer {  // Note: when erroneous, raises weird "no progress" compiler error that a `val _ = new _` would not
      val base: ${base.tpe} = $base
    }
    ${rewriteImpl(tr, q"$transName", base)}
    $base.`internal IR`[$typ,$ctx]($transName.transformBottomUp($termName.rep))
    """
    
    debug("Generated: " + showCode(res))
    
    //res
    c.untypecheck(res)
  }
  
  
  def rewrite(tr: c.Tree) = {
    import c.universe._
    
    
    val trans = c.macroApplication match {
      case q"$trans.rewrite($_)" => trans
      case q"$trans.dbg_rewrite($_)" => trans
    }
    val base = c.typecheck(q"$trans.base")
    
    val res = rewriteImpl(tr, trans, base)
    c.untypecheck(res)
    
  }
    
  def rewriteImpl(tr: c.Tree, trans: c.Tree, base: c.Tree) = {
    import c.universe._
    
    val cases = tr match {
      case Function(ValDef(_, name, _, _) :: Nil, q"$_ match { case ..$cases }") => cases
    }
    
    //debug(cases)
    
    var n = 0
    
    val registrations = cases map {
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
        expr.tpe.baseType(symbolOf[lang2.Base#IR[_, _]]) match {
          case tpe@TypeRef(tpbase, _, constructedType :: constructedCtx :: Nil) if tpbase =:= base.tpe =>
            
            if (extractedType =:= constructedType) {
              //debug("Rewriting " + extractedType)
            } else {
              c.abort(constructedPos, s"Cannot rewrite a term of type $extractedType to a different type $constructedType")
            }
            
            if (!(extractedCtx <:< constructedCtx))
              c.abort(constructedPos,
                if (constructedCtx <:< extractedCtx)
                  s"Cannot rewrite a term of context $extractedCtx to a stricter context $constructedCtx"
                else s"Cannot rewrite a term of context $extractedCtx to an unrelated context $constructedCtx")
          case NoType =>
            c.abort(constructedPos, s"This rewriting does not produce a ${base.tpe}.IR[_,_] type as a return.")
            
        }
        
        val termTree = pat.find {
          case q"val _term_ : $_ = __b__.wrapExtract($termTree)" => true
          case _ => false
        } match { case Some(ValDef(_, _, _, rhs)) => rhs } // Note: we keep the `wrapExtract`
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
            case Bind(name: TermName, pq"_") => q"val $name = $scrut; $body"
          }
        }
        
        val r = q"$trans.registerRule($termTree, (__extr__ : $base.Extract) => ${
          ((subPatterns zip patNames) :\ (if (cond.isEmpty) q"_root_.scala.Option($expr.rep)" else q"if ($cond) _root_.scala.Some($expr.rep) else _root_.scala.None")) {
            
            case ((pat, (mapName @ TermName("_1"), name)), acc) =>
              patMat(q"__b__.`internal IR`(__extr__.$mapName($name))", pat, acc)
              
            case ((pat, (mapName @ TermName("_2"), name)), acc) =>
              patMat(q"__b__.`internal IRType`(__extr__.$mapName($name))", pat, acc)
              
            case ((pat, (mapName @ TermName("_3"), name)), acc) =>
              patMat(q"(__extr__.$mapName($name)) map ((__rep__ : __b__.Rep) => __b__.`internal IR`(__rep__))", pat, acc)
              
          }
        })"
        
        //debug(r)
        
        r
    }
    
    
    val res = q"val __b__ : ${base.tpe} = $base; ..$registrations"
    
    //debug("Result: "+showCode(res))
    
    // Resets previously resolved identifier symbols; necessary since we're defining a new binding for __b__
    val gen = TreeOps(res) transform { case Ident(name) => Ident(name) }
    
    //debug("Typed: "+c.typecheck(gen))
    //debug("Generated: "+(gen))
    //debug("Generated: "+showCode(gen))
    debug(" --- GENERATED ---\n"+showCode(gen))
    
    gen
    
  }
  
}






