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

object MyGraph extends Graph

//object GraphTests {
//  def nextInt = scala.util.Random.nextInt
//}
//import GraphTests._
import scala.util.Random.nextInt

class GraphTests extends MyFunSuite(MyGraph) {
  import DSL.Predef._
  
  def process(c: ClosedCode[Any]) = {
    val cur = c
    do {
      println
      println(DSL.showGraph(c.rep))
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
  
  
  /*
  
    Note:
      rewriting as implemented is currently a little dangerous: it will rewrite things bound to variables and used
      several times, possibly duplicating some work
      in contrast, it seems GHC only rewrite variables used once... (is that true?)
     
  */
  
  
  //def rw(c: ClosedCode[Any]) = c also println rewrite {
  //  case code"readInt.toDouble" => code"readDouble"
  //} also println
  def rw(c: ClosedCode[Any]) = {
    var mod = true
    var cur = c
    println("\n-> "+cur.rep.showGraphRev)
    println(cur.show)
    while (mod) {
      mod = false
      cur = cur rewrite {
        case code"readInt.toDouble" => mod = true; code"readDouble"
        case code"(($x: $xt) => $body:$bt)($arg)" =>
          //println(s"! $arg")
          mod = true; body.subs(x) ~> arg
      } also (r => if (mod) println("~> "+r.rep.showGraphRev+"\n"+r.show))
    }
    println(" --- END ---\n")
  }
  
  test("Rw 1") {
    
    //code"readInt+1-1" also println rewrite {
    //  case code"readInt" => code"nextInt"
    //  //case code"($a:Int)-($b:Int)" => code"$a*$b" // FIXME better error (vararg)
    //  case code"($a:Int)-($b:Int)" => code"$a * $b"
    //} also println
    
    rw(code"readInt.toDouble+1-1")
    
    //base debugFor
    rw(code"val ri = (_:Unit) => readInt; ri(()).toDouble+ri(()).toDouble")
    
    
  }
  
  test("Rw 2") {
    
    //rw(code"val ri = (n:Int) => readInt+n; ri(nextInt*2).toDouble+ri(42).toDouble")
    rw(code"val ri = (n:Int) => 0.5+n.toDouble; ri(nextInt)+ri(readInt)")
    
  }
  
}
