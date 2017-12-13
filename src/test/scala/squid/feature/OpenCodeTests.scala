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

import utils._

/** Demonstrating the use of OpenCode when we don't want to track context dependencies. */
class OpenCodeTests extends MyFunSuite {
  import TestDSL.Predef.{OpenCode => Rep, _}
  import TestDSL.Quasicodes._
  
  def optim[A](pgrm: Rep[A]): Rep[A] = pgrm rewrite {
    case code"List[$t]($xs*).size" => Const(xs.size)
  }
  
  test("Simple Rep") {
    
    val t = optim(code{
      val lss = List(1,2,3).size
      lss+1
    })
    
    t eqt code{val lss_x = 3; lss_x+1}
    
    assert(t.unsafe_asClosedCode.run == 4)
    
  }
  
}
