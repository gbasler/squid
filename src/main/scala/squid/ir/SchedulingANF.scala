// Copyright 2017 EPFL DATA Lab (data.epfl.ch)
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

package squid
package ir

import utils._
import collection.mutable

/**
  * ANF IR similar to SimpleANF except it additionally factors pure common subexpressions so as to minimize recomputation.
  * 
  * TODO make Def cache its hashCode... (potentially big perf improvement)
  * 
  * TODO better scheduling algo that uses Lazy and thunks
  * 
  */
class SchedulingANF extends SimpleANF {
  
  /*
  caching all closed terms is possible, because of the huge qty of duplicated-code with different bound variable names
  (see for example what is produced by the reinterpreter-based pretty-printer).
  CONCLUSION: not feasible to save closed code as is; have to save equivalence classes of it modulo bound variables!!
  probably best done using some kind of de Bruijn encoding – but is it even worth it?
  */
  //import mutable.{WeakHashMap => MutMap}
  //val closedCache = MutMap[Def,Rep]()
  //override def rep(dfn: Def) = { // TODO more efficient: don't even construct the nodes -- use and override mkX methods
  //  if (dfn.isClosed) closedCache.getOrElseUpdate(dfn, super.rep(dfn))
  //  else super.rep(dfn)
  //}
  
  
  // scheduling 
  
  import squid.quasi.MetaBases
  import utils.meta.{RuntimeUniverseHelpers => ruh}
  import ruh.sru
  import squid.lang._
  
  
  override def reinterpret(r: Rep, NewBase: Base)(ExtrudedHandle: (BoundVal => NewBase.Rep) = DefaultExtrudedHandler): NewBase.Rep =
    (new SchedulingReinterpreter {
      val newBase: NewBase.type = NewBase
      override val extrudedHandle = ExtrudedHandle
    })(r)
  
  trait SchedulingReinterpreter extends Reinterpreter {
    
    val scheduled = mutable.Map[BoundVal,newBase.BoundVal]()
    
    def apply(r: Rep): newBase.Rep = 
      (r |>? {case RepDef(bv:BoundVal) => scheduled get bv} flatten) map newBase.readVal getOrElse {
        var bestFreq = 0
        var bestFreqHi = 0
        var best = Option.empty[Rep]
        r.occurrences.foreach {
          case (k,rge) if rge.start >= 1 && rge.end > 1 => //&& !scheduled.contains(k) => 
            //println(s"T ${k.dfn}  | ${rge.start} | ${k.dfn.size}")
            if (rge.start > bestFreq || rge.start == bestFreq && rge.end > bestFreqHi) {
              bestFreq = rge.start
              bestFreqHi = rge.end
              best = Some(k)
            }
            else if (rge.start == bestFreq && rge.end == bestFreqHi && k.dfn.size > best.get.dfn.size) best = Some(k)
            // ^ if two expressions have the same number of occurrences, we pick the one with the bigger size because
            //   otherwise we may schedule subexpressions of that bigger expression that are used only in it
          case _ =>
        }
        best map { e =>
          assert(!(e eq r), s"Expression is not supposed to contain an occurrence of itself: $e")
          val bv0 = bindVal("sch",e.typ,Nil)
          val bv = bv0 |> recv
          // ^ this is needed to let the Reinterpreter base class know we've defined a variable symbol – we can't just
          //   directly create it via newBase.bindVal, or else we may get an extruded variable exception
          val e2 = e|>apply
          scheduled.put(bv0,bv)
          val r2 = bottomUpPartial(r) { case `e` => readVal(bv0) }
          newBase.letin(bv,e2,r2|>apply,r2.typ|>rect) alsoDo scheduled.remove(bv0)
        } getOrElse apply(r.dfn)
      }

  }
  
  
  // Need to override these so the new SchedulingReinterpreter is mixed in:
  
  override def scalaTreeIn(MBM: MetaBases)(SRB: MBM.ScalaReflectionBase, rep: Rep, ExtrudedHandle: (BoundVal => MBM.u.Tree)): MBM.u.Tree = ANFDebug.muteFor { muteFor {
    new ReinterpreterToScala {
      val MetaBases: MBM.type = MBM
      val newBase: SRB.type = SRB
      override val extrudedHandle = ExtrudedHandle
    } apply rep
  }}
  abstract class ReinterpreterToScala extends super.ReinterpreterToScala with SchedulingReinterpreter {
    override def apply(d: Rep) = super.apply(d) // to fix conflicting inherited member
  }
  
  
}
