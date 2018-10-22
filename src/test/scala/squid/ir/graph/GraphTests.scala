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
package graph

import utils._
import squid.lib.matching._
import squid.lib.const

object MyGraph extends Graph

object GraphTests {
  def nextInt = 123
  def nextInt(n: Int) = n*2
}
import GraphTests._

class GraphTests extends MyFunSuite(MyGraph) with GraphTestRewriter {
  import DSL.Predef._
  import DSL.Quasicodes._
  
  /*
  def process(c: ClosedCode[Any]) = {
    val cur = c
    do {
      println
      println(DSL.showGraphRev(c.rep))
      println(cur)
      println
    } while (DSL.reduceStep(cur.rep))
    println(" --- END ---")
  }
  
  test("A") {
    /*
    //val c = code"readInt+1"
    //val c = code"scala.util.Random.nextInt+1"
    val c = code"val f = (x: Int) => x+1; f(scala.util.Random.nextInt) * f(2)"
    
    //println(c.rep)
    println(c)
    //println(c.run)
    //println(c.compile)
    //println(c.rep.iterator.toList.mkString("\n"))
    println
    println(DSL.showGraph(c.rep))
    //println(DSL.reduceStep(c.rep))
    println
    println(c.rep.reduceStep.map(_.showGraph))
    println(c)
    //println(c.compile)
    */
    
    process(code"val f = (x: Int) => x+1; f(scala.util.Random.nextInt) * f(2)")
    
  }
  
  test("B") {
    
    process(code"val f = (x: Int) => (y: Int) => x+y; f(1)(2)")
    
    process(code"val f = (x: Int) => (y: Int) => x+y; f(1)(2) + f(3)(4)")
    
    process(code"val f = (x: Int) => (y: Int) => x+y; f(1)(f(3)(4))")
    
  }
  */
  
  test("Rw 1") {
    
    //code"readInt+1-1" also println rewrite {
    //  case code"readInt" => code"nextInt"
    //  //case code"($a:Int)-($b:Int)" => code"$a*$b" // FIXME better error (vararg)
    //  case code"($a:Int)-($b:Int)" => code"$a * $b"
    //} also println
    
    rw(code"nextInt(42).toDouble+1-1")(84.0)
    
    //base debugFor
    
    rw(code"val ri = (_:Unit) => nextInt(42); ri(()).toDouble+ri(()).toDouble")(168.0)
    
    rw(code"val ri = (n:Int) => n+1+nextInt(42); ri(42).toDouble")(127.0)
    
    
  }
  
  test("Rw 2") {
    
    //rw(code"val ri = (n:Int) => nextInt(42)+n; ri(nextInt*2).toDouble+ri(42).toDouble")()
    rw(code"val ri = (n:Int) => 0.5+n.toDouble; ri(nextInt)+ri(nextInt(42))")(208.0)
    
    rw(code"val ri = (n:Int) => n+1; ri(nextInt)+ri(42)")(167)
    
    rw(code"val ri = (n:Int) => n+1+nextInt(42); ri(0).toDouble+ri(1).toDouble")(171.0)
    
  }
  
  test("Simple Cross-Boundary Rewriting (Linear)") {
    
    rw(code"val ri = (n:Int) => n.toDouble; ri(nextInt).toInt",2)(123)
    
    rw(code"val ri = (n:Double) => n.toInt; ri(nextInt.toDouble)",2)(123)
    
    // TODO also with non-trivial leaves
    
  }
  test("Simple Cross-Boundary Rewriting") {
    // TODO several calls
  }
  test("Basic Cross-Boundary Rewriting") {
    // FIXME we get 'Possibly wrong' warnings from mapDef
    
    val f = code"(x: Int) => x + x"
    
    rw(code"val f = $f; f(11) + f(22)", 1)(66)
    
    rw(code"val f = $f; f(f(22))", 1)(88)
    
  }
  test("Complex Cross-Boundary Rewriting") {
    
    // FIXME: making this `f` a `val` makes stack overflow in mapDef when bottomUp order, and seems to never stop rewriting when topDown
    //val f = code"(x: Int) => (y: Int) => x+y"
    def f = code"(x: Int) => (y: Int) => x+y"
    
    rw(code"val f = $f; f(11)(22) + 1", 1)(34)
    
    rw(code"val f = $f; f(11)(22) + f(30)(40)", 1)(103)
    
    rw(code"val f = $f; f(11)(f(33)(40))"/*, 1*/)(84) // FIXME not opt: (11).+(73)
    
    rw(code"val f = $f; f(f(33)(40))")(174, _(101))
    
    rw(code"val f = $f; f(f(11)(22))(40)", 1)(73)
    
    //rw(code"val f = $f; val g = (z: Int) => f(f(11)(z))(f(z)(22)); g(30) + g(40)", 1)() // FIXME Variable was never initialized // FIXME SOF in mapDef when bottomUp order
    
    //rw(code"val g = (x: Int) => (y: Int) => x+y; val f = (y: Int) => (x: Int) => g(x)(y); f(11)(f(33)(44))")(88) // FIXME Variable was never initialized // FIXME probably not opt // FIXME SOF in mapDef when bottomUp order
    
  }
  
  test("My Tests") {
    val f = code"(x: Int) => (y: Int) => x+y"
    
    rw(code"val f = $f; f(11)(f(33)(40))")(84)
    
    //rw(code"val f = $f; f(f(33)(40))")(174, _(101)) // FIXME hygiene
    //rw(code"val f = $f; f(f(11)(22))(40)", 1)(73) // FIXME hygiene
    
    //rw(code"val f = $f; val g = (z: Int) => f(f(11)(z))(f(z)(22)); g(30) + g(40)")()
    //rw(code"val g = (x: Int) => (y: Int) => x+y; val f = (y: Int) => (x: Int) => g(x)(y); f(11)(f(33)(44))")(88)
    
  }
  
  
  val show = Variable[String => Option[String => String] => String]
  lazy val showDef = code{ (str: String) => (fmt0: Option[String => String]) => Match(fmt0)(
    Case[None](_ => str),
    Case[Some[String => String]](_.value(str)),
  )}
  lazy val fooDef = code{
    (aa: String) => (fmt1: Option[String => String]) =>
      ($(show))(big.E0(aa))(fmt1) + ($(show))(big.E1(aa))(fmt1)
  }.unsafe_asClosedCode // Q: why needed?
  
  test("Bigger Example (g7)") { // FIXME 'Found a free variable'
    
    rw(code{
      val $show = $(showDef)
      val foo = $(fooDef)
      (bb: String) =>
        foo(bb)(if (bb == "") None else Some(s => bb + ": " + s))
    })()
    
  }
  
  test("Motivating Example") {
    // TODO
  }
  
}

object big {
  def E0[T](x:T):T=x
  def E1[T](x:T):T=x
  def E2[T](x:T):T=x
  def E3[T](x:T):T=x
}
