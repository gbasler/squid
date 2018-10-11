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
  
}
