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
package anf

import ir._
import squid.anf.analysis.ControlFlowBase
import squid.anf.analysis.BinOpBase
import squid.anf.analysis.BlockHelpers
import squid.anf.transfo.LogicNormalizer
import squid.anf.transfo.StandardNormalizer
import squid.anf.transfo.LogicFlowNormalizer
import squid.ir.BottomUpTransformer
import squid.ir.FixPointRuleBasedTransformer
import squid.ir.StandardEffects
import squid.ir.TopDownTransformer
import squid.ir.FixPointTransformer
import utils._

class BooleanFlowTests extends MyFunSuite(SimpleANFTests.DSLWithEffects) {
  import DSL.Predef._
  import DSL.Quasicodes._
  
  object Opt extends DSL.SelfTransformer with StandardNormalizer with LogicNormalizer with TopDownTransformer with FixPointTransformer {
    override val MAX_TRANSFORM_ITERATIONS = 16
  }
  
  object Flow extends DSL.SelfTransformer with LogicFlowNormalizer
  
  test("Options Stuff") {
    
    val a = code{Option(42) orElse Option(666) orElse Some(0) orElse Some(1)} //and println
    
    val b = a transformWith Opt
    
    b eqt code[Option[Int]]{ // Notice that if type param is ommitted, we get a difft result type for the ITE, which makes the code not equivalent!!
      if ((42).==(null).&&((42).==(null).&&((666).==(null).`unary_!`).`unary_!`))
        scala.Some.apply[scala.Int](0)
      else
        if ((42).==(null))
          if ((666).==(null))
            scala.None
          else
            scala.Some.apply[scala.Int](666)
        else
          if ((42).==(null))
            scala.None
          else
            scala.Some.apply[scala.Int](42)
    }
    
    val c = b transformWith Flow
    
    val d = c transformWith Opt
    
    d eqt code[Option[Int]]{
      if ((42).==(null).&&((666).==(null)))
        scala.Some.apply[scala.Int](0) : Option[Int] // also needed, otherwise this ITE is inferred of type Some[Int]...
      else
        if ((42).==(null))
          scala.Some.apply[scala.Int](666) : Option[Int]
        else
          scala.Some.apply[scala.Int](42)
    }
    
  }
  
}
