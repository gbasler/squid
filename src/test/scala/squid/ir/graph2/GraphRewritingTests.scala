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

object GraphRewritingTests extends Graph {
  object Tr extends SimpleRuleBasedTransformer with SelfTransformer {
    import Predef.{Const=>_,_}
    
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
}

import GraphRewritingTests._

/*

TODO try to trigger the unsoundness with matching things inside lambda bodies and doing substitution



*/
class GraphRewritingTests extends MyFunSuite(GraphRewritingTests) {
  import DSL.Predef._
  import DSL.Quasicodes._
  
  def doTest[A](cde: ClosedCode[A], expectedSize: Int = Int.MaxValue)
      (expectedResult: Any = null, preprocess: A => Any = (a:A) => a, doEval: Bool = true) = {
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
      val ite = DSL.rewriteSteps(DSL.Tr)(cde.rep)
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
  }
  
  test("A") {
    
    doTest(code"666.toDouble")()
    
  }
  
  def f = code"(x: Int) => x + x"
  
  // TODO also test when f is new each time
  test("Basic Cross-Boundary Rewriting") {
    
    val f = code"(x: Int) => x + x" // TODO also try as val again
    
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
    
  }
  
  test("My Tests") {
    
    DSL.ScheduleDebug debugFor
    doTest(code"val f = $f; f(f(11) * f(f(22)))", 1)(3872)
    // ^ FIXME diverges: keeps rewriting a dead-code else clause...
    //  it doesn't seem essential that it be dead though
    //  it's just an occurrence of the usual problem of re-application of already applied rewrites
    
  }
  
  def g = code"(x: Int) => (y: Int) => x+y"
  
  test("Complex Cross-Boundary Rewriting") {
    
    // TODO: make this `f` a `val`
    //val f = code"(x: Int) => (y: Int) => x+y"
    
    doTest(code"val f = $g; f(11)(22) + 1", 1)(34)
    
    //doTest(code"val f = $g; f(11)(22) + f(30)(40)", 1)(103)
    // ^ FIXME: extracts a lambda under a box, which triggers the known bug
    
    //doTest(code"val f = $g; f(11)(f(33)(40))"/*, 1*/)(84) // FIXME not opt: (11).+(73)
    // ^ FIXME SOF in scheduling's analysis
    //   Note: used to make CCtx's hashCode SOF! but I chanegd it to a lazy val...
    
    //doTest(code"val f = $g; f(f(33)(40))")(174, _(101))
    // ^ FIXME: lambda body bug
    
    //doTest(code"val f = $g; f(f(11)(22))(40)", 1)(73)
    // ^ FIXME: wrong value!
    
    //doTest(code"val f = $g; val g = (z: Int) => f(f(11)(z))(f(z)(22)); g(30) + g(40)", 1)()
    // ^ FIXME SOF in scheduling's analysis
    
    //doTest(code"val g = (x: Int) => (y: Int) => x+y; val f = (y: Int) => (x: Int) => g(x)(y); f(11)(f(33)(44))")(88)
    // ^ FIXME SOF in scheduling's analysis
    
  }
  
  
}
