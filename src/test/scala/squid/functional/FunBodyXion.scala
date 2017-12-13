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
package functional

class FunBodyXion extends MyFunSuite {
  
  import TestDSL.Predef._
  
  val fun = code"(x: Int) => x + 1"
  val body = fun match {
    case code"(y: Int) => $b: Int" => b
  }
  
  test("Function Body Extraction") {
    
    //println(body, ir"($$y: Int) + 1")
    //println(body.rep extract ir"($$y: Int) + 1".rep)
    //println(ir"($$y: Int) + 1".rep extract body.rep)
    
    assert(body =~= code"(?y: Int) + 1")
    assert(!(body =~= code"(?x: Int) + 1"))
    
    val bodyPart = body match {
      case code"($x: Int) + 1" => x
    }
    assert(bodyPart =~= code"?y: Int")
    assert(!(bodyPart =~= code"?x: Int"))
    
  }
  
  test("Function Body Reconstruction") {
    
    val fun2 = code"(y: Int) => $body" : Q[Int => Int, {}]
    assert(fun =~= fun2)
    
  }
  
  
  
  
}



