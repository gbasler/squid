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
package feature

import squid.quasi.{DefaultQuasiConfig, OpenCodeConfig}

class SameCompilationUnitConfig extends DefaultQuasiConfig

class QuasiConfigTests extends MyFunSuite {
  
  test("Open Code Inference") {
    
    val MyPredef = new TestDSL.Predef[OpenCodeConfig]
    import MyPredef._
    
    val c = code"42.toString"
    ofExactType [OpenCode[String]] (c)
    
    val cs = Set(code"42.toString")
    ofExactType [Set[OpenCode[String]]] (cs)
    
  }
  
  
  test("Class") {
    
    val MyPredef = new TestDSL.Predef[SameCompilationUnitConfig]
    import MyPredef._
    
    assertDoesNotCompile("""code"42.toString"""")
    // ^ Error: Cannot find quasi-config class `squid.feature.SameCompilationUnitConfig`; perhaps it was defined in the same compilation unit?
    
  }
  
}
