// Copyright 2019 EPFL DATA Lab (data.epfl.ch)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package squid.ir
package graph3

import squid.utils._
import squid.utils.CollectionUtils.MutSetHelper
import squid.utils.meta.{RuntimeUniverseHelpers => ruh}

import squid.utils.CollectionUtils._

import scala.collection.mutable
import scala.collection.immutable.ListMap
import squid.ir.graph.SimpleASTBackend

trait RecGraphScheduling extends AST { graph: Graph =>
  
  import squid.quasi.MetaBases
  import squid.utils.meta.{RuntimeUniverseHelpers => ruh}
  import ruh.sru
  
  //import mutable.{Map => M}
  import mutable.{ListMap => M}
  
  class RecScheduler(nb: squid.lang.Base) {
    type TrBranch = Either[(Control,Branch),ScheduledRep]
    
    val scheduledReps = M.empty[Rep,ScheduledRep]
    
    class ScheduledRep private(val rep: Rep) {
      var backEdges: mutable.Buffer[ScheduledRep] = mutable.Buffer.empty
      scheduledReps += rep -> this
      val children = rep.node.children.map(c => scheduledReps.getOrElseUpdate(c, new ScheduledRep(c))).toList
      children.foreach(_.backEdges += this)
      
      val branches: M[(Control,Branch),(TrBranch,Val)] = rep.node match {
        case br: Branch => M((Id,br) -> (Left(Id,br),rep.bound))
        case _ => M.empty
      }
      
      var usages = 0
      
      def shouldBeScheduled = rep.node match {
        case _: Branch => false
        case ConcreteNode(d) => !d.isSimple
        case _ => true
      }
      
      def printDef(dbg: Bool): String = rep.node match {
      case _: Branch => rep.bound.toString
      case _ =>
        new DefPrettyPrinter(showInlineCF = false) {
          override def apply(n: Node): String = if (dbg) super.apply(n) else n match {
            case Box(_, body) => apply(body)
            case ConcreteNode(d) => apply(d)
            case _: Branch => die
          }
          override def apply(r: Rep): String = {
            val sr = scheduledReps(r)
            def printArg(cb: (Control,Branch), pre: String): String = branches.get(cb).map{
                case (Left(_),v) => v.toString
                case (Right(r),v) => pre + apply(r.rep)
              }.getOrElse("?")
            sr.rep.node match {
              case ConcreteNode(d) if d.isSimple => super.apply(d)
              case ConcreteNode(ByName(body)) if !dbg => apply(body)
              case b: Branch =>
                assert(sr.branches.size === 1)
                printArg((Id,b),"")
              case _ if !dbg && (sr.usages <= 1) =>
                assert(sr.usages === 1)
                sr.printDef(dbg)
              case _ =>
                s"${sr.rep.bound}(${sr.branches.valuesIterator.collect{
                  case (Left(cb),v) => printArg(cb,s"$v=")
                }.mkString(",")})"
            }
          }
        } apply rep.node
      }
      
      def params = branches.valuesIterator.collect{case (Left(cb),v) => v}
      def args =
        children ++ branches.valuesIterator.collect{case (Right(r),v) => r}.filter(_.shouldBeScheduled)
      
      override def toString =
        s"${rep.bound}(${params.mkString(",")}) = ${printDef(true)}"
    }
    object ScheduledRep {
      def apply(rep: Rep): ScheduledRep = new ScheduledRep(rep)
    }
  }
  
  def scheduleRec(rep: Rep): Unit = {
    val sch = new RecScheduler(SimpleASTBackend)
    val root = sch.ScheduledRep(rep)
    var workingSet = sch.scheduledReps.valuesIterator.filter(_.branches.nonEmpty).toList
    //println(workingSet)
    while (workingSet.nonEmpty) {
      val sr = workingSet.head
      workingSet = workingSet.tail
      //println(sr, sr.branches)
      sr.backEdges.foreach { sr2 =>
        sr.branches.valuesIterator.foreach {
        case (Left(cb @ (c,b)), v) =>
          if (!sr2.branches.contains(cb)) {
            def addBranch(cb2: sch.TrBranch) = {
              if (cb2.isLeft) workingSet ::= sr2
              sr2.branches += cb -> (cb2, v)
            }
            val nde = sr2.rep.node match {
              case ConcreteNode(abs: Abs) if ByName.unapply(abs).isEmpty => Box(Push(DummyCallId, Id, Id),abs.body)
              case n => n
            }
            nde match {
              case _: Branch => // can't bubble up to a branch!
              case Box(ctrl, _) =>
                val newCtrl = ctrl `;` c
                mayHaveCid(newCtrl `;` b.ctrl, b.cid)(Id) match {
                  case Some(c) =>
                    val r2 = sch.scheduledReps(if (c) b.lhs else b.rhs)
                    r2.backEdges += sr2
                    workingSet ::= r2
                    addBranch(Right(r2))
                  case None => addBranch(Left(newCtrl,b))
                }
              case ConcreteNode(_) => addBranch(Left(cb))
            }
            
          }
        case (Right(_),_) =>
        }
      }
    }
    val reps = sch.scheduledReps.valuesIterator.toList.sortBy(_.rep.bound.name)
    reps.foreach { sr =>
      if (sr.shouldBeScheduled) {
        sr.args.foreach(_.usages += 1)
      }
    }
    // Note: the root will probably always have 0 usage
    reps.foreach { sr =>
      if (sr.shouldBeScheduled) {
        println(s"[${sr.usages}] $sr")
        //println(sr.args.map(_.rep.bound).toList)
      }
    }
    
    val defs = for (
      sr <- reps
      if sr.shouldBeScheduled && (sr.usages > 1 || (sr eq root))
    ) yield s"def ${
        if (sr eq root) "main" else sr.rep.bound
      }(${sr.params.map{ p =>
          s"$p: ${p.typ}"
        }.mkString(",")
      }): ${sr.rep.typ} = ${sr.printDef(false)}"
    
    println(defs.mkString("\n"))
    
    
  }
  
  
}

