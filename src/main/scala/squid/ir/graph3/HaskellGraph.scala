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

import scala.collection.mutable

private final class UnboxedMarker // for Haskell gen purposes

/** Dummy class for encoding Haskell pat-mat in the graph. */
private class HaskellADT {
  def `case`(cases: (String -> Any)*): Any = ???
  def get(ctorName: String, fieldIdx: Int): Any = ???
}

abstract class HaskellGraph extends Graph with HaskellGraphScheduling {
  
  // Uncomment for nicer names in the graph, but mapped directly to the Haskell version (which becomes less stable):
  //override protected def freshNameImpl(n: Int) = "_"+n.toHexString
  
  // Otherwise creates a stack overflow while LUB-ing to infinity
  override def branchType(lhs: => TypeRep, rhs: => TypeRep): TypeRep = Any
  
  val Any = Predef.implicitType[Any].rep
  val UnboxedMarker = Predef.implicitType[UnboxedMarker].rep
  
  val HaskellADT = loadTypSymbol("squid.ir.graph3.HaskellADT")
  val CaseMtd = loadMtdSymbol(HaskellADT, "case")
  val GetMtd = loadMtdSymbol(HaskellADT, "get")
  
  var ctorArities = mutable.Map.empty[String, Int]
  def mkCase(scrut: Rep, alts: Seq[(String, Int, () => Rep)]): Rep = {
    methodApp(scrut, CaseMtd, Nil, ArgsVarargs(Args(),Args(alts.map{case(con,arity,rhs) =>
      ctorArities.get(con) match {
        case Some(a2) => assert(a2 === arity)
        case None => ctorArities += con -> arity
      }
      Tuple2(con |> staticModule, rhs())
    }:_*))::Nil, Any)
  }
  
  object Tuple2 {
    //val TypSymbol = loadTypSymbol("scala.Tuple2")
    val ModTypSymbol = loadTypSymbol("scala.Tuple2$")
    val Mod = staticModule("scala.Tuple2")
    val ApplySymbol = loadMtdSymbol(ModTypSymbol, "apply")
    def apply(x0: Rep, x1: Rep): Rep = methodApp(Mod, ApplySymbol, Nil, Args(x0,x1)::Nil, Any)
  }
  
  case class PgrmModule(modName: String, modPhase: String, lets: Map[String, Rep]) {
    val letReps = lets.valuesIterator.toList
    lazy val toplvlRep = {
      val mv = bindVal(modName, Any, Nil)
      Rep.withVal(mv, Imperative(letReps.init, letReps.last, allowEmptyEffects = true))
    }
    def showGraph = toplvlRep.showGraph
    def show = "module " + showGraph
  }
  
  
  
  
  override def prettyPrint(d: Def) = (new HaskellDefPrettyPrinter)(d)
  class HaskellDefPrettyPrinter(showInlineNames: Bool = false, showInlineCF:Bool = true) extends DefPrettyPrinter(showInlineNames, showInlineCF) {
    override def apply(d: Def): String = d match {
      case Apply(lhs,rhs) => s"${apply(lhs)} @ ${apply(rhs)}" // FIXME?
      case _ => super.apply(d)
    }
  }
  override def printNode(n: Node) = (new HaskellDefPrettyPrinter)(n)
  
  
}
