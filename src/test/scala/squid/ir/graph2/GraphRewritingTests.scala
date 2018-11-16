// Copyright 2018 EPFL DATA Lab (data.epfl.ch)
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
package graph2

import utils._
import squid.lib.matching._
import squid.lib
import squid.ir.graph.{SimpleASTBackend => AST}

object GraphRewritingTests extends Graph

import GraphRewritingTests._

trait GraphRewritingTester[DSL <: Graph] extends MyFunSuite[DSL] {
  import DSL.Predef._
  import DSL.Quasicodes._
  import DSL.IntermediateCodeOps
  
  val Tr: SimpleRuleBasedTransformer with DSL.SelfTransformer
  
  def doTest[A](cde: ClosedCode[A], expectedSize: Int = Int.MaxValue)
      (expectedResult: Any = null, preprocess: A => Any = id[A], doEval: Bool = true): DSL.Rep = {
    val cdes = cde.show
    println("\n-> "+cde.rep.showGraph+"\n-> "+cdes)
    //println(DSL.edges)
    def printCheckEval(rep: AST.Rep): Unit = if (doEval) {
      val value = //DSL.EvalDebug.debugFor
        cde.run
      if (expectedResult =/= null) assert(preprocess(value) == expectedResult, s"for ${cdes}")
      //println(s"== ${value}\n== ${Console.RED}${try DSL.scheduleAndRun(cde.rep) catch{case e:Throwable=>e}}${Console.RESET}")
      println(s"== ${value}\n== ${Console.RED}${try AST.runRep(rep) catch{case e:Throwable=>e}}${Console.RESET}")
    }
    printCheckEval(DSL.treeInSimpleASTBackend(cde.rep))
    var mod = false
    do {
      val ite = DSL.rewriteSteps(Tr)(cde.rep)
      mod = ite.hasNext
      while (ite.hasNext) {
        val n = ite.next
        val sch = DSL.treeInSimpleASTBackend(cde.rep)
        println(s"Rw ${cde.rep.bound} -> $n")
        //println(s"Rw ${cde.rep.bound} -> ${n.showGraph}")
        //println(s"Nota: ${showEdges}")
        println(s"${Console.BOLD}~> Transformed:${Console.RESET} "+cde.rep.showGraph+"\n~> "+AST.showRep(sch))
        printCheckEval(sch)
        //Thread.sleep(100)
      }
    } while (mod)
    println("\n--- --- ---\n")
    cde.rep
  }
}

/*

TODO try to trigger the unsoundness with matching things inside lambda bodies and doing substitution

*/
class GraphRewritingTests extends MyFunSuite(GraphRewritingTests) with GraphRewritingTester[GraphRewritingTests.type] {
  import DSL.Predef._
  import DSL.Quasicodes._
  
  object Tr extends SimpleRuleBasedTransformer with SelfTransformer {
    
    rewrite {
      case c"666" => c"42"
      case code"(($x: $xt) => $body:$bt)($arg)" =>
        println(s"!>> SUBSTITUTE ${x.rep} with ${arg.rep} in ${body.rep.showGraph}")
        val res = body.subs(x) ~> arg
        println(s"!<< SUBSTITUTE'd ${res.rep.showGraph}")
        //println(s"Nota: ${showEdges}")
        res
      case r @ code"lib.const[Int](${n0@Const(n)})+lib.const[Int](${m0@Const(m)})" =>
        //println(s"!Constant folding ${r.rep}"); mod = true; Const(n+m)
        println(s"!Constant folding ${n0.rep.fullString} + ${m0.rep.fullString} from: ${r.rep.fullString}"); Const(n+m)
    }
    
  }
  
  test("A") {
    
    doTest(code"666.toDouble")()
    
  }
  
  // TODO also test when f is new each time
  def f = code"(x: Int) => x + x"
  
  test("Nested calls") {
    
    val f = code"(x: Int) => x * x" // TODO also try as def or val again
    
    doTest(code"val f = $f; f(11) + f(22)", 1)(605)
    
    doTest(code"val f = $f; f(f(22))", 1)(234256)
    
    doTest(code"val f = $f; f(11) + f(f(22))", 1)(234377)
    
    doTest(code"val f = $f; f(f(11) + f(f(22)))", 1)(-901996719)
    
  }
  
  test("Basic Cross-Boundary Rewriting") {
    
    val f = code"(x: Int) => x + x" // TODO also try as def or val again
    
    //DSL.ScheduleDebug debugFor
    doTest(code"val f = $f; f(11) + f(22)", 1)(66)
    
    //DSL.ScheduleDebug debugFor
    doTest(code"val f = $f; f(f(22))", 1)(88)
    
    //DSL.ScheduleDebug debugFor
    doTest(code"val f = $f; f(11) + f(f(22))", 1)(110)
    
    //DSL.ScheduleDebug debugFor
    //doTest(code"val f = $f; f(f(11) * f(f(22)))", 1)(3872)
    // ^ Note: we schedule '$9' which is used only once... this is because if the branch under which it appears could be
    //         resolved on-the-spot and not moved as a parameter, '$9' would have _actually_ been used twice!
    // ^ FIXME now diverges (in constant folding): keeps rewriting a dead-code else clause...
    //  it doesn't seem essential that it be dead though
    //  it's just an occurrence of the usual problem of re-application of already applied rewrites
    
  }
  
  test("My Tests") {
    //def g = code"(x: Int) => (y: Int) => x - y"
    
    //DSL.ScheduleDebug debugFor
    //doTest(code"val f = $f; f(f(11) * f(f(22)))", 1)(3872)
    //doTest(code"val f = $g; f(11)(22) + f(30)(40)", 1)(-21)
    
  }
  
  def g = code"(x: Int) => (y: Int) => x+y"
  
  test("Nested Curried Calls") {
    
    // TODO: try making `g` a `val` here
    def g = code"(x: Int) => (y: Int) => x - y"
    
    //DSL.ScheduleDebug debugFor
    doTest(code"val f = $g; f(11)(22) + 1", 1)(-10)
    
    doTest(code"val f = $g; f(11)(22) + f(30)(40)", 1)(-21)
    
    doTest(code"val f = $g; f(11)(f(33)(40))"/*, 1*/)(18)
    
    doTest(code"val f = $g; f(f(33)(40))")(-108, _(101))
    
    //DSL.ScheduleDebug debugFor
    doTest(code"val f = $g; f(f(11)(22))(40)", 1)(-51)
    
    //doTest(code"val f = $g; val g = (z: Int) => f(f(11)(z))(f(z)(22)); g(30) + g(40)", 1)()
    // ^ FIXME never finishes; accumulates tons of control-flow
    
    //doTest(code"val g = (x: Int) => (y: Int) => x+y; val f = (y: Int) => (x: Int) => g(x)(y); f(11)(f(33)(44))")(88)
    // ^ FIXME never finishes
    
  }
  
  test("Complex Cross-Boundary Rewriting") {
    
    // TODO: try making `g` a `val` here
    //val g = code"(x: Int) => (y: Int) => x+y"
    
    doTest(code"val f = $g; f(11)(22) + 1", 1)(34)
    
    doTest(code"val f = $g; f(11)(22) + f(30)(40)", 1)(103)
    
    doTest(code"val f = $g; f(11)(f(33)(40))"/*, 1*/)(84)
    // ^ Note: used to SOF in scheduling's analysis
    //   Note: used to make CCtx's hashCode SOF! but I chanegd it to a lazy val...
    //   Note: was another manifestation of the lambda bug, since the reason for the cycle was there were no pass nodes
    
    //doTest(code"val f = $g; f(f(33)(40))")(174, _(101))
    // ^ FIXME: never finishes; loops on: "Constant folding $9 = 73 + $8 = 40"
    
    doTest(code"val f = $g; f(f(11)(22))(40)", 1)(73)
    
    //doTest(code"val f = $g; val g = (z: Int) => f(f(11)(z))(f(z)(22)); g(30) + g(40)", 1)()
    // ^ FIXME never finishes; accumulates tons of control-flow, such as:
    // !Constant folding $9190 = ⟦α1⟧ $9189:⟦α8 $9188:⟦α7⟧ $9187:⟦α5⟧ $7:11⟧ + $9202 = ⟦α1⟧ $9201:⟦α8 $9200:⟦α7⟧ $9199:⟦α5⟧ $16:30⟧ from: $3882 = ⟦α1⟧ $2165:(↑α8;|α5;|α7;α9 ? $3888:(↑α8;|α5;|α7;α0 ? $3890:(↑α8;|α5;|α7;α1 ? $3892:(↑α8;|α5;|α7;|α5;|α7;↑α8;|α1;↑α2;↑α9;α0 ? $3894:⟦α8 $2281:71⟧ ¿ [...]
    
    //doTest(code"val g = (x: Int) => (y: Int) => x+y; val f = (y: Int) => (x: Int) => g(x)(y); f(11)(f(33)(44))")(88)
    // ^ FIXME also accumulates tons of nodes
    
  }
  
  
}
