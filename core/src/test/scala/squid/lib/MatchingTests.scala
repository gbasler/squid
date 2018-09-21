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

package squid.lib
import matching._
import squid.utils._

import org.scalatest.FunSuite

class MatchingTests extends FunSuite {
  
  val x = Option(1)
  
  test("Basic matchign tests") {
    
    assert( Match(x)(Case[Some[Int]](_.value + 1)) == 2 )
    assertThrows[MatchError]( Match(x)(Case[None](_ => 0)) )
    
    val m = Match(x)(
      Case[None.type](_ => 0),
      Case[Some[Int]](_.value + 1),
    )
    assert(m == 2)
    
    assertDoesNotCompile("Match(Some(1))(Case[None.type](_ => 0))")
    // ^ found   : squid.lib.matching.Case[None.type,Int]; required: squid.lib.matching.Case[Some[Int],?]
    
    assert( Match(Some(1))(Case[Some[Int]](_.value + 1)) == 2 )
    
  }
  
}
