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

import squid.utils.meta.RuntimeUniverseHelpers
import utils.Debug._

class Analyse extends MyFunSuite {
  import DSL.Predef._
  
  test("Simple Analysis") {
    
    val q = code"println(1); var cur = 2; for (i <- 3 to 4) cur += 5; cur - 6"
    
    var sum = 0
    q analyse {
      case Const(n) => sum += n
    }
    
    same(sum, 1 + 2 + 3 + 4 + 5 + 6)
    
  }
  
}
