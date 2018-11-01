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
import squid.lib.const

object MyGraph extends Graph

object GraphTests {
  def nextInt = 123
  def nextInt(n: Int) = n*2
}
import GraphTests._

class BasicGraphTests extends MyFunSuite(MyGraph) with MyFunSuiteTrait {
  import DSL.Predef._
  import DSL.Quasicodes._
  
  test("A") {
    
    println(code"nextInt+1")
    println(code"nextInt+1".run)
    
    println(code"(n:Int) => nextInt+n")
    println(code"((n:Int) => nextInt+n)(1)".run)
    
  }
  
}

