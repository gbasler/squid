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


package squid
package ir
package graph3

import utils._
import squid.lib.matching._
import squid.lib
import squid.ir.graph.{SimpleASTBackend => AST}

object GraphRewritingTests extends Graph {
  override val aggressiveBoxPushing = true
}

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
    //do RewriteDebug.debugFor {
    do {
      //val ite = DSL.rewriteSteps(Tr)(cde.rep)
      val ite = DSL.rewriteSteps(Tr)(cde.rep).iterator
      mod = ite.hasNext
      while (ite.hasNext) {
        val n = ite.next
        val sch = DSL.treeInSimpleASTBackend(cde.rep)
        println(s"Rw ${cde.rep.bound} -> $n")
        //println(s"Rw ${cde.rep.bound} -> ${n.showGraph}")
        //println(s"Nota: ${showEdges}")
        
        println(s"${Console.BOLD}~> Transformed:${Console.RESET} "+cde.rep.showGraph+"\n~> "+AST.showRep(sch))
        //println(s"${Console.BOLD}~> Transformed:${Console.RESET} "+cde.rep)
        
        printCheckEval(sch)
        //Thread.sleep(100)
        //Thread.sleep(50)
      }
    } while (mod)
    
    // for debugging, just in case it changes anything...
    assert(!DSL.simplifyGraph(cde.rep, recurse = true))
    assert(!DSL.simplifyGraph(cde.rep, recurse = false))
    
    //while (RewriteDebug debugFor DSL.simplifyGraph(cde.rep, recurse = false)) {}
    
    assert(cde.rep.size <= expectedSize, s"for ${cde.rep}")
    printSep()
    cde.rep
  }
  def printSep() = println("\n--- --- ---\n")
}

/*

TODO try to trigger the unsoundness with matching things inside lambda bodies and doing substitution

*/
class GraphRewritingTests extends MyFunSuite(GraphRewritingTests) with GraphRewritingTester[GraphRewritingTests.type] {
  import DSL.Predef._
  import DSL.Quasicodes._
  import haskell.Prelude.compose
  
  object Tr extends SimpleRuleBasedTransformer with SelfTransformer {
    
    rewrite {
      case c"666" => c"42"
        
        // No longer done here: cf., current problems with matching the inside of lambdas
        /*
      case code"(($x: $xt) => $body:$bt)($arg)" =>
        println(s"!>> SUBSTITUTE ${x} with ${arg.rep} in ${body.rep.showGraph}")
        val res = body.subs(x) ~> arg
        println(s"!<< SUBSTITUTE'd ${res.rep.showGraph}")
        //println(s"Nota: ${showEdges}")
        res
        */
        
      case r @ code"lib.const[Int](${n0@Const(n)}) + lib.const[Int](${m0@Const(m)})" =>
        //println(s"!Constant folding ${r.rep}"); mod = true
        println(s"!Constant folding ${n0.rep.fullString} + ${m0.rep.fullString} from: ${r.rep.fullString}")
        Const(n+m)
        
        
      case r @ code"compose[$ta,$tb,$tc]($f)($g)" =>
        code"(a: $ta) => $f($g(a))"
      case r @ code"($opt:Option[$ta]).map[$tb]($f).map[$tc]($g)" =>
        code"$opt.map(compose($g)($f))"
      case r @ code"(Some[$ta]($v):Option[ta]).get" => v
        
      //case r @ code"(Some[$ta]($v):Option[ta]).fold[$tb]($d)($f)" =>  // 'd' passed by name: matches a lambda! TODO handle by-name matching
      //  ???
        
    }
    
  }
  
  // also test when f is a val
  //def f = code"val f = (x: Int) => x + x; f"
  
  test("Simple") {
    
    doTest(code"666.toDouble", 3)(true, res => (res == 42.0) || (res == 666.0))
    
    doTest(code"Option(1).map(x => x.toDouble).map(y => y+1)", 13)(Some(2.0))
    
    doTest(code"val f = (x: Int) => x + x; f(2)", 1)(4)
    
    doTest(code"val f = (x: Int) => (y: Int) => x + y; f(2)(3)", 1)(5)
    
  }
  
  test("Nested calls") {
    
    val f = code"val f = (x: Int) => x * x; f"
    
    //DSL.ScheduleDebug debugFor
    doTest(code"$f(11) + $f(22)", 7)(605)
    
    doTest(code"$f($f(22))", 9)(234256)
    
    doTest(code"$f(11) + $f($f(22))", 15)(234377)
    
    doTest(code"$f($f(11) + $f($f(22)))", 21)(-901996719)
    
    val g = code"val g = (x: Int) => (y: Int) => x * y; g"
    
    doTest(code"$g($g(2)(3))(4)", 10)(24)
    
    doTest(code"$g(4)($g(2)(3))", 17)(24) // note: 17 also accounts for some of the nodes of the above!
    
    doTest(code"$g($g(2)(3))($g(4)(5))", 27)(120)
    
  }
  
  test("Basic Cross-Boundary Rewriting") {
    
    val f = code"val f = (x: Int) => x + x; f"
    
    //DSL.ScheduleDebug debugFor
    doTest(code"$f(11) + $f(22)", 1)(66)
    
    //DSL.ScheduleDebug debugFor
    doTest(code"$f($f(22))", 1)(88)
    
    //DSL.ScheduleDebug debugFor
    doTest(code"$f(11) + $f($f(22))", 1)(110)
    
    //DSL.ScheduleDebug debugFor
    doTest(code"$f($f(11) * $f($f(22)))", 26)(3872)
    // ^ Note: we schedule '$9' which is used only once... this is because if the branch under which it appears could be
    //         resolved on-the-spot and not moved as a parameter, '$9' would have _actually_ been used twice!
    // ^ Note: used to diverges (in constant folding): kept rewriting a dead-code else clause...
    
    doTest(code"$f($f(11) + $f($f(22)))", 1)() // FIXME now SOF unless -Xss4m
    
  }
  
  test("My Tests") {
    //val f = code"val f = (x: Int) => x + x; f"
    //def g = code"(x: Int) => (y: Int) => x - y"
    
    //DSL.ScheduleDebug debugFor
    //doTest(code"val f = (x: Int) => (y: Int) => x + y; f(f(2)(3))(f(4)(5))")() // FIXME
    //doTest(code"val f = (x: Int) => (y: Int) => x + y; f(f(2)(3))(0)", 1)(5)
    //doTest(code"val f = (x: Int) => (y: Int) => x + y; f(f(2)(3))")() // FIXME not fully reduced
    
    val g = code"val g = (x: Int) => (y: Int) => x * y; g"
    //DSL.ScheduleDebug debugFor
    //doTest(code"$g($g(2)(3))($g(4)(5))")(120)
    
  }
  
  test("Nested Curried Calls") {
    
    val g = code"(x: Int) => (y: Int) => x - y"
    
    doTest(code"val f = $g; f(11)(22) + 1", 9)(-10)
    
    doTest(code"val f = $g; f(11)(22) + f(30)(40)")(-21)
    
    doTest(code"val f = $g; f(11)(f(33)(40))"/*, 1*/)(18)
    
    doTest(code"val f = $g; f(f(33)(40))")(-108, _(101))
    
    doTest(code"val f = $g; f(f(11)(22))(40)")(-51)
    
    doTest(code"val f = $g; val g = (z: Int) => f(f(11)(z))(f(z)(22)); g(30) + g(40)")(-74)
    
    doTest(code"val g = $g; val f = (y: Int) => (x: Int) => g(x)(y); f(11)(f(33)(44))")(0)
    
  }
  
  test("Complex Cross-Boundary Rewriting") {
    
    val g = code"(x: Int) => (y: Int) => x + y"
    
    doTest(code"val f = $g; f(11)(22) + 1", 1)(34)
    
    doTest(code"val f = $g; f(11)(22) + f(30)(40)", 1)(103)
    
    doTest(code"val f = $g; f(11)(f(33)(40))", 1)(84)
    
    doTest(code"val f = $g; f(f(33)(40))")(174, _(101))
    
    doTest(code"val f = $g; f(f(11)(22))(40)", 1)(73)
    
    doTest(code"val f = $g; val g = (z: Int) => f(f(11)(z))(f(z)(22)); g(30) + g(40)", 1)()
    
    doTest(code"val g = (x: Int) => (y: Int) => x + y; val f = (y: Int) => (x: Int) => g(x)(y); f(11)(f(33)(44))", 1)(88)
    
  }
  
  test("Higer-Order Functions 2") {
    
    val h0 = code"val h = (f: Int => Int) => f(2) - f(3) + f(4); h"
    
    doTest(code"$h0(x => x * 2)")(6) // FIXME not fully-reduced
    
  }
  test("Higer-Order Functions") {
    
    val h0 = code"val h = (f: Int => Int) => f(2) + f(3); h"
    
    doTest(code"$h0(x => x * 2)", 13)(10)
    
    doTest(code"$h0(x => x + 1)", /*13*/23)(7)
    
     // FIXME stopped working at some point with Haskell graph
    /*
    doTest(code"$h0(x => x + x)", 1)(10)
    
    doTest(code"$h0(x => x + 1) + $h0(x => x + 2)", 1)(16)
    
    doTest(code"$h0(x => x * $h0(y => x - y))", 43)(1)
    // ^ FIXME strange scheduling: ... ; x470_2.apply(x499_9.apply(2, 2, 2), x499_9.apply(3, 3, 3))
    
    doTest(code"$h0(x => x + $h0(y => x + y))", 1)(25)
    
    
    doTest(code"val g = (f: Int => Int) => f(2) + f(3); g(x => x * 2) + g(x => x - 1)", 21)(13)
    
    doTest(code"val g = (f: Int => Int) => f(2) + f(3); val h = (x: Int) => x * 2; g(h) + g(compose(h)(h))", 21)(30)
    
    
    val h1 = code"val h = (f: Int => Int) => (x: Int) => f(x) + f(3); h"
    
    doTest(code"$h1(x => x + 1)(2)", 1)(7)
    
    doTest(code"$h1(y => y + 1)($h1(z => z + 2)(3))", 1)(15) // FIXME stopped working at some point with Haskell graph: assertion error
    
    val h2 = code"val h = (f: (Int => Int) => Int) => (x: Int) => f(x => x + 1) + f(x => x * 2); h"
    
    doTest(code"$h2(f => f(11) + f(22))(66)",34)(101)
    
    doTest(code"$h2(f => f(11) + f(22))( $h2(f => f(11) + f(22))(66) )",76)(101) // FIXME stopped working at some point with Haskell graph: assertion error
    */
    
  }
  
  test("Complex") {
    
    // TODO test with by-names:
    //val f = code"val f = (opt: Option[Int]) => opt.fold(0)(x => x + 1); f"
    
    val f = code"val f = (opt: Option[Int]) => opt.get+1; f"
    
    //doTest(code"$f(Some(42))", 1)(43) // TODO
    
    doTest(code"(x: Int) => { val g = (a: Int => Option[Int]) => Some($f(a(x))); g(i => g(j => Some(i + j + x))).get }")(128, _(42))
    //doTest(code"(x: Int) => { val g = (a: Int => Option[Int]) => Some($f(a(x))); g(i => g(j => Some(i + j + x))).get + 2 }", 1)(128, _(42)) // FIXME doesn't beta reduce at all
    
  }
  
}
