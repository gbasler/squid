package scp
package ir2

import lang2._
import scp.quasi2.MetaBases
import utils._
import utils.CollectionUtils._
import utils.meta.{RuntimeUniverseHelpers => ruh}
import ruh.sru

import scala.collection.mutable

trait ANFHelpers extends AST { anf: ANF =>
  
  
  override def scalaTreeIn(MBM: MetaBases)(SRB: MBM.ScalaReflectionBase, rep: Rep, ExtrudedHandle: (BoundVal => MBM.u.Tree)): MBM.u.Tree = ANFDebug.muteFor { muteFor {
    new ReinterpreterToScala {
      val MetaBases: MBM.type = MBM
      val newBase: SRB.type = SRB
      override val extrudedHandle = ExtrudedHandle
    } apply rep
  }}
  
  /** Would probably make more sense to make a proper clean-up pass (inline order-safe used-once vals, rm redundant variable reads, etc) generating SimpleAST and then use scalaTreeIn on it */
  abstract class ReinterpreterToScala extends super.ReinterpreterToScala {
    val MetaBases: MetaBases
    import MetaBases.u._
    val newBase: MetaBases.ScalaReflectionBase
    
    val repCache = mutable.Map[Int, newBase.Rep]()
    
    override def apply(r: Rep) = repCache.getOrElseUpdate(r.uniqueId, r match { // Q: cache update useful here? (will it ever miss?)
        
      case b: anf.Block =>
        b.effects match {
          case Seq() => apply(b.result)
          case es :+ el =>
            
            // TODO inline used-once vars (when possible)
            
            val (effs, res) = if (
              el.uniqueId == b.result.uniqueId
              //el === b.result  // Q: makes a difference?
            ) (es, el) else (b.effects, b.result)
            
            val constr = effs.foldRight(() => apply(res)) {
              case (e, bod) =>
                
                () => {
                  //println(s"Eff[${e.uniqueId}] "+e)
                  val tpe = e.typ.tpe.asInstanceOf[Type]
                  if (repCache isDefinedAt e.uniqueId) { // Effect has already occurred; no need to refer to its id
                    bod()
                  }
                  else if (tpe =:= typeOf[Unit]) { // TODO make sure no further usages! -- do this subs at the IR level
                    val eff = apply(e)
                    repCache += e.uniqueId -> q"()"
                    q"$eff; ..${bod()}"
                  }
                  //else if (tpe.widen.typeSymbol == Var.ClassSymbol) { // TODO use ScalaVar xtor?
                  else if (e.dfn.isInstanceOf[MethodApp] && e.dfn.asInstanceOf[MethodApp].sym === Var.Apply.Symbol) { // TODO use ScalaVar xtor?
                    val RepDef(MethodApp(_, Var.Apply.Symbol, tp::Nil, Args(v)::Nil, _)) = e
                    val nameHint = b.associatedBoundValues get e.uniqueId map (_.name) getOrElse "v"
                    //val varName = newBase.freshName("v") // TODO try to retain names; on inlining, save an `originalBinding`? (cannot be used for boundVar bookkeeping though, as we may inline several times into the same block)
                    val varName = newBase.freshName(nameHint)
                    q"var $varName: ${rect(tp)} = ${apply(v)}; ..${
                      //boundVars += bv -> varName  // No `bv`, the var has been inlined!
                      //println(s"VAR $varName "+e.uniqueId)
                      repCache += e.uniqueId -> q"$varName"
                      bod()
                    }"
                  }
                  else
                  {
                    //val nameHint = b.associatedBoundValues getOrElse (e.uniqueId, "x")
                    val nameHint = b.associatedBoundValues get e.uniqueId map (_.name) getOrElse "x"
                    //val bound = newBase.bindVal(s"x", rect(e.typ), Nil)
                    val bound = newBase.bindVal(nameHint, rect(e.typ), Nil)
                    val eff = apply(e)
                    repCache += e.uniqueId -> newBase.readVal(bound)
                    newBase.letin(bound, eff, bod(), rect(b.typ))
                  }
                } //and (r => println(s"Rewrote $e => ${r}"))
                
            }
            constr()
        }
        
      //case RepDef(MethodApp(_, Var.Apply.Symbol, _, _, _)) =>
      //  println(r.uniqueId)
      //  ???
        
      case RepDef(MethodApp(v, Var.Bang.Symbol, _, _, _)) =>
        apply(v)
        
        
      case _ => super.apply(r)
    })
    
  }
  
  
  def prettyPrint(r: Rep) = (new DefPrettyPrinter)(r)
  override def prettyPrint(d: Def) = (new DefPrettyPrinter)(d)
  class DefPrettyPrinter extends super.DefPrettyPrinter {
    //val showBlockIds = false
    val showBlockIds = true
    
    val namePrefix = "#"
    val defShown = mutable.Set[Int]()
    override def apply(r: Rep): String = apply(r, false)
    def apply(r: Rep, inBlock: Boolean): String = r match {
      case b: Block =>
        val effsStrs = b.effects map (apply(_, true))
        val id = if (showBlockIds) r.uniqueId.toString else ""
        val line = s"$id[ ${effsStrs mkString "; "}; ${apply(b.result)} ]"
        (if (line.length > 80 && b.effects.size > 1) s"$id[ ${effsStrs map (_ + "\n  ") mkString}${apply(b.result, true)} ]"
        else line) + s"<${b.associatedBoundValues mkString ";"}>"
      case _ =>
      if (r.isPure) super.apply(r)
      else if (defShown(r.uniqueId)) s"$namePrefix${r.uniqueId}"
      else {
        defShown += r.uniqueId
        val str = s"$namePrefix${r.uniqueId} = ${super.apply(r)}"
        if (inBlock) str else s"($str)"
      }
    }
  }
  
  
}
