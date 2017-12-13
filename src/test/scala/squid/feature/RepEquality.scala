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

class RepEquality extends MyFunSuite {
  
  import TestDSL.Predef._
  
  test("Functions") {
    
    assert(code"(x: Int) => x" =~= code"(x: Int) => x")
    
    assert(code"(x: Int) => x" =~= code"(y: Int) => y")
    
    assert(code"val x = 42.toDouble; x + 1" =~= code"val y = 42.toDouble; y + 1")
    
  }
  
  
  
}
