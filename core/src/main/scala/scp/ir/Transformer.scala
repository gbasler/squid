package scp
package ir

import scp.utils.MacroUtils.{MacroDebug, MacroDebugger, MacroSetting}
import utils._

import scala.language.dynamics
import scala.language.experimental.macros
import scala.collection.mutable
import utils.UnknownContext

/** Type and context-safe interface for program transformation.
  * It checks that rewrite rules rewrite things to the same type, and with a weaker or identical context
  * 
  * TODO: use different sets of transfo based on the top-level node; have a:  Map[DSLSymbol,Rewrite]
  * TODO: better handling of situations where cases do not have the right shape
  * TODO: take function arguments instead of partial functions,
  *   which generate a lot of boilerplate and DUPLICATE the code between orElse and isDefinedAt
  * 
  **/
trait Transformer {
  val base: lang.Base
  import base._
  
  type Rewrite = PartialFunction[Q[Any,Nothing],Q[Any,Nothing]]
  
  def rewrite(tr: PartialFunction[Q[Any,UnknownContext],Q[Any,_]]): Unit = macro TransformerMacros.rewrite
  @MacroSetting(debug = true) def dbgrewrite(tr: PartialFunction[Q[Any,UnknownContext],Q[Any,_]]): Unit = macro TransformerMacros.rewrite
  
  val `private rewrites` = mutable.Buffer[Rewrite]()
  
  // TODO a way to avoid the crazy object creation/destruction here, esp. when there are no rewrites...
  def applyTransform[T,C](q: Q[T,C]): Quoted[T,C] = applyTransform(q.rep).asInstanceOf[Quoted[T,C]]
  def applyTransform(r: Rep) = { // TODO use lifted partial functions instead...
    //println("Trans "+r.show)
    var currentQ = Quoted[Any,Nothing](r)
    `private rewrites` foreach { rw =>
      //println("T "+currentQ+" "+rw.isDefinedAt(currentQ))
      currentQ = rw.applyOrElse(currentQ, identity[Q[Any,Nothing]])
    }
    currentQ
  }
  
  private var online = false
  def isOnline = online
  
  /** calls to `wrapExtract` in a pattern passed to `rewrite` are rewritten to `wrapExtractOnline` */
  def wrapExtractOnline(r: => Rep) = {
    try {
      online = true
      wrapExtract(r)
    } finally online = false
  }
  
  def bottomUp[T,C](q: Quoted[T,C]) = Quoted[T,C](transform(q.rep)(r => applyTransform(r).rep))
  
}
class OfflineTransformer[B <: lang.Base](val base: B) extends Transformer

import reflect.macros.whitebox.Context
class TransformerMacros(val c: Context) extends MacroShared {
  type Ctx = c.type
  val Ctx: Ctx = c
  
  val debug = { val mc = MacroDebugger(c); mc[MacroDebug] }
  
  def rewrite(tr: c.Tree) = {
    import c.universe._
    
    val trans = c.macroApplication match {
      case q"$trans.rewrite($_)" => trans
      case q"$trans.dbgrewrite($_)" => trans
    }
    val base = c.typecheck(q"$trans.base")
    
    val cases = tr match {
      case q"{ ..${List(cas)} }" => cas match {
        case Typed(Block(List(ClassDef(mods, _, _, templ)), res), _) =>
          templ.body collectFirst {
            case DefDef(mods, TermName("applyOrElse"), tparams, paramss, typ, valu) =>
              valu match {
                case q"$_ match { case ..${cases2} }" => cases2.init // last is the default case, so we just take the initial elements
              }
          } get;
      }
    }
    
    cases foreach {
      case cas @ CaseDef(pat, cond, expr) =>
        //println("Case on line "+cas.pos.line)
        
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
        expr.tpe.baseType(symbolOf[lang.Base#Quoted[_, _]]) match {
          case tpe@TypeRef(tpbase, _, constructedType :: constructedCtx :: Nil) if tpbase =:= base.tpe =>
            
            if (extractedType =:= constructedType) {
              //println("Rewriting " + extractedType)
            } else {
              c.abort(constructedPos, s"Cannot rewrite a term of type $extractedType to a different type $constructedType")
            }
            
            if (!(extractedCtx <:< constructedCtx))
              c.abort(constructedPos,
                if (constructedCtx <:< extractedCtx)
                  s"Cannot rewrite a term of context $extractedCtx to a stricter context $constructedCtx"
                else s"Cannot rewrite a term of context $extractedCtx to an unrelated context $constructedCtx")
          case NoType =>
            c.abort(constructedPos, s"This rewriting does not produce a ${base.tpe}.Quoted type as a return.")
            
        }
  
    }
    
    val newTR = tr transform {
      case t @ q"$base.wrapExtract($arg)" => // if base.tpe == ...
        c.typecheck(q"$trans.base.wrapExtractOnline($arg)")
    }
    
    q"$trans.`private rewrites` += $newTR.asInstanceOf[$trans.Rewrite]"
  }
  
}






