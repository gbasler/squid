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

import utils.GenHelper

import scala.collection.mutable

class CompilationTests extends MyFunSuite {
  import DSL.Predef._
  import DSL.Quasicodes._
  
  test("Miscellaneous") {
    
    val q = code{
      val buff = new mutable.ArrayBuffer[Int]()
      for (i <- 1 to 11) buff += i * 2
      buff.reduce(_ + _)
    }
    val res = 132
    assert(q.run == res)
    assert(q.compile == res)
    
  }
  
}
