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
package classlift

import squid.ir._
import squid.utils.meta.RuntimeUniverseHelpers
import squid.utils._

class ClassLiftingTests extends MyFunSuite {
  import TestDSL.Predef._
  import TestDSL.Class
  
  test("A") {
    
    val cls = MyClass.reflect(TestDSL)
    println(cls)
    println(cls.asInstanceOf[Class].methods
      .map(_.symbol == TestDSL.methodSymbol[MyClass.type]("swap")))
    
  }
  
}
