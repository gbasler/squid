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
  * ANF IR similar to SimpleANF except it additionally factors (deduplicates) pure common subexpressions so as to
  * minimize recomputation and code size.
  * 
  * TODO make Def cache its hashCode... (potentially big perf improvement)
  * 
  * Note: could extend the algorithm to use thunks so as to deduplicate effectful expressions
  * 
  * Note: the presence of bindings due to imperative code will minimize the advantages of SchedulingANF even for the
  *       pure parts of the code. A nameless representation such as de Bruijn could help to *some* extent as it would
  *       equate alpha-equivalent terms and give them the same hashCode.
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
  
  val scheduleAggressively = false
  
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
    
    val scheduled = mutable.Map[Rep,newBase.Rep]()
    var scheduledAsSubexpr: Set[Rep] = Set()
    
    lazy val LazyApplyN = newBase.loadMtdSymbol(newBase.loadTypSymbol("squid.utils.Lazy$"), "apply")
    lazy val LazyTypN = newBase.loadTypSymbol("squid.utils.Lazy")
    lazy val LazyValueN = newBase.loadMtdSymbol(LazyTypN, "value")
    lazy val LazyModN = newBase.staticModule("squid.utils.Lazy")
    
    def apply(r: Rep): newBase.Rep =
      scheduled.getOrElse(r, {
        
        val es = r.execStats.iterator.filter { case r -> e => (
             e.srcOccurreces > 1
          && e.shared || !e.atLeastOnce && !e.atMostOnce && scheduleAggressively // hack: '!alo && !amo' to detect if it's within a lambda...
          && !scheduledAsSubexpr(r)
        )}.toList sorted (Ordering by ((re: Rep -> ExecStats) => re._2.srcOccurreces -> re._1.dfn.size) reverse)
        
        val rt = r.typ |> rect
        
        def rec(es: List[Rep -> ExecStats]): newBase.Rep = es match {
          case (schr -> e) :: rest if !scheduled.isDefinedAt(schr) && !scheduledAsSubexpr(schr) =>
            val bv0 = if (e.atLeastOnce) bindVal("sch", schr.typ, Nil) else bindVal("lsch", staticTypeApp(LazyTyp, schr.typ :: Nil), Nil)
            val bv = bv0 |> recv
            val schr2 = schr |> apply
            val schrt = Lazy(schr.typ |> rect)
            val oldSAS = scheduledAsSubexpr
            scheduledAsSubexpr = oldSAS ++ schr.execStats.unzip._1
            newBase.letin(bv,
              if (e.atLeastOnce) schr2 else
                newBase.methodApp(LazyModN, LazyApplyN, schrt.value::Nil, newBase.Args(newBase.byName(schr2)) :: Nil, newBase.staticTypeApp(LazyTypN, schrt.value :: Nil)),
              { // Note: it's important noy to call `newBase.readVal` out of this by-name argument, or it would crash bases like BaseInterpreter
                val read = newBase.readVal(bv) |> { rv => if (e.atLeastOnce) rv else newBase.methodApp(rv, LazyValueN, Nil, Nil, schrt.value) }
                scheduled.put(schr, read)
                rec(rest) alsoDo { scheduled.remove(schr); scheduledAsSubexpr = oldSAS }
              }, rt)
          case _ :: rest => rec(rest)
          case Nil => apply(r.dfn)
        }
        
        rec(es)
        
      })
    
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
