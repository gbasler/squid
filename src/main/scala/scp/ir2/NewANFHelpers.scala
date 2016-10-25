package scp
package ir2

import utils._
import utils.Debug.show

import scala.collection.mutable
import scala.{collection => gen}

import lang2._
import scp.quasi2.MetaBases
import utils.CollectionUtils._
import utils.meta.{RuntimeUniverseHelpers => ruh}
import ruh.sru

/**
  * Created by lptk on 29/09/16.
  */
trait NewANFHelpers extends AST { anf: NewANF =>
  
  
  
  override def isPure(d: Def) = d match {
    case MethodApp(s,m,ts,ass,r) => false // TODO
    case _ => super.isPure(d)
  }
  
  
  
  
  
  //protected def immediateValRefs(d: Def) = d match {
  //  case 
  //}
  
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
    
    //val repCache = mutable.Map[Int, newBase.Rep]()
    
    override def apply(r: Rep) = r.processed match {
      
      case SimpleRep(v: Val) =>
        //println("!!ACCESS!! "+v)
        //???
        apply(v) // FIXME?
        
      case b @ anf.Block(es,r,ds) => inContext(ds) {
        es match {
          case Seq() => apply(r)
          case es :+ el =>
            
            //Debug.show(es)
            
            // TODO inline used-once vars (when possible)
            
            //val effs -> res = el match {
            //  case v: Val if r === v => es -> el
            //  case _ => es -> r
            //}
            //val effs -> res = if (r === el) es -> el else b.effs -> r
            val effs -> res = if (r === el) es -> (() => el |> getValDef |> (_ get) |> apply) else b.effs -> (() => r |> apply and println)
            
            //Debug.show(es,el)
            //Debug.show(b.effs,r)
            //Debug.show(effs -> res)
            //Debug.show(ds)
            //println(res >>? { case v: Val => v |> getValDef get })
            //println(res >>? { case v: Val => ds(v) })
            
            val typ = rect(b.typ)
            
            //Debug show(b.pureDefs)
            val allDefs = {
              /*
              //val effOrd = effs.iterator.zipWithIndex.map(_ swap).toMap
              val effOrd = effs.iterator.zipWithIndex.toMap
              val buckets = b.pureDefs.toSeq map { case v_d @ (v,d) =>
                var ord = 0
                val pureDefsRefs = mutable.Set[Val]()
                //val pureRefs = 
                SimpleRep(d) |> (traverse { d =>
                  d match {
                    case SimpleRep(v: Val) =>
                      b.pureDefs get v foreach (pureDefsRefs += _)
                      effOrd get v foreach (x => ord = x max ord)
                    //case MethodApp(s,m,ts,ass,r) => 
                  }
                  //d.is
                  true
                })
                //ord -> v_d
                ord -> (v -> pureDefsRefs)
              //} groupBy (_._1) mapValues (_._2);
              } groupBy (_._1) mapValues { ids => //case (_, ds) =>
                val ds = ids.unzip._2.toMap
                /*
                val ordered = mutable.Buffer[Val]()
                //ds:Int
                ds foreach { case (v, d)
                  
                  ordered += v
                }
                ordered.distinct
                */
                
              }
              buckets(0) ++ (effs flatMap {
                
              })
              */
              val refEdges = b.pureDefs.iterator flatMap { case v_d @ (v,d) =>
                val refs = mutable.Set[Val]()
                SimpleRep(d) |> traversePartial {
                  case SimpleRep(v: Val) if b.normDefs isDefinedAt v => refs += v; true
                }
                //refs.iterator map (v -> _)
                refs.iterator map (_ -> v)
              }
              val imperativeEdges = effs.sliding(2) collect {
                case Seq(a,b) => a -> b
              }
              
              //Debug show(refEdges toList)
              //Debug show(imperativeEdges toList)
              //Debug show(b.effs.sliding(2) toList)
              
              algo.tsort(edges = (refEdges ++ imperativeEdges) toSeq)
              
              //???
              //effs
            }
            
            //Debug show(allDefs)
            
            //val constr = effs.foldRight(() => apply(res)) {
            //val constr = effs.foldRight(() => apply(res >>? { case v: Val => v |> getValDef get })) { // FIXME if v is not the last sym...
            //val constr = effs.foldRight(() => res()) {
            val constr = allDefs.foldRight(() => res()) {
            //val constr = (b.pureDefs.keysIterator ++ effs).foldRight(() => res()) {
              case (v, bod) => () =>
                //newBase.letin(v |> recv, ds(v), bod(), b.typ)
                //newBase.letin(v |> recv, apply(v), bod(), typ)
                //Debug show(v)
                //Debug show(new EffectRep(v) |> apply)
                //newBase.letin(v |> recv, /*v |> */new EffectRep(v) |> apply, bod(), typ)
                
                //println("rec "+(v |> getValDef get))
                newBase.letin(v |> recv, apply(v |> getValDef get), bod(), typ)
            }
            
            /*
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
                    //newBase.letin(bound, eff, bod(), if (ascribeUselessTypes) rect(b.typ) else EmptyTree)
                  }
                } //and (r => println(s"Rewrote $e => ${r}"))
                
            }
            constr()
            */
            
            constr()
            
            //???
            
        }
      }
        
      //case RepDef(MethodApp(_, Var.Apply.Symbol, _, _, _)) =>
      //  println(r.uniqueId)
      //  ???
        
        // TODO
        /*
      case RepDef(MethodApp(v, Var.Bang.Symbol, _, _, _)) =>
        apply(v)
        
      case RepDef(MethodApp(v, Var.ColonEqual.Symbol, _, Args(x)::Nil, _)) =>
        q"${apply(v)} = ${apply(x)}"
        */

      //case Delayed(d) => ???
        
      case _ => super.apply(r)
    }
    
  }
  
  
  def prettyPrint(r: Rep) = (new DefPrettyPrinter)(r)
  override def prettyPrint(d: Def) = (new DefPrettyPrinter)(d)
  class DefPrettyPrinter extends super.DefPrettyPrinter {
    val namePrefix = "#"
    val defShown = mutable.Set[Int]()
    //override def apply(r: Rep): String = r.processed match {
    override def apply(r: Rep): String = r match {
      case b @ Block(effs, res, defs) =>  // TODO display pure defs in a `where` clause
        val effsStrs = effs map { bv =>
          //case (bv, dfn) =>
          //val dfn = defs(bv)
          val dfn = b.impureDefs(bv)
          s"${bv.name} = ${apply(dfn)}"
        }
        val defsStrs = b.pureDefs map { case (v,d) => s"${v.name} = ${apply(d)}" }
        val resStr = apply(res)
        val line = s"[ ${effsStrs mkString "; "}; ${resStr} ]"
        //(if (line.length > 80 && effs.size > 1) s"[ ${effsStrs map (_ + "\n  ") mkString}${resStr} ]"
        val body = (if (line.length > 80 && effs.size > 1) s"[\n${effsStrs map ("  " + _ + "\n") mkString}  ${resStr}\n]"
        else line) //+ s"<${b.associatedBoundValues mkString ";"}>"
        body + (if (defsStrs nonEmpty) " where " + (defsStrs mkString "; ") else "")
      case SimpleRep(bv) => apply(bv)
      case Delayed(d) => s"Delayed(${d |> apply})"
    }
  }
  
}
