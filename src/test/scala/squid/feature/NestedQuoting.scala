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
package feature

class NestedQuoting extends MyFunSuite {
  
  import TestDSL.Predef._
  
  
  test("Simple Nesting") {
    
    assert(code"42.toString * 2" =~= code"42.toString * 2")
    
    assert(code"42.toString * 2" =~= code"${ code"42.toString" } * 2")
    
  }
  
  
  test("Block Nesting") {
    
    assert(code"42.toString * 2" =~= code"${ val n = code"42"; code"$n.toString" } * 2")
    
    assert(code"42.toDouble.toString * 2" =~= code"${ val n = code"42.toDouble"; code"$n.toString" } * 2")
    
  }
  
  
  test("Double Nesting") {
    
    assert(code"42.toDouble.toString * 2" =~= code"${ val str = code"${ val n = code"42"; code"$n.toDouble" }.toString"; str } * 2")
    
    assert(code"42.toDouble.toString * 2" =~= code"${ val n = code"42"; val str = code"${ code"$n.toDouble" }.toString"; str } * 2")
    
  }
  
  
  
  
}





