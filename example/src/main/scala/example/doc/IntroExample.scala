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

package example.doc

import squid.utils._
import squid.ir.{SimpleANF, BottomUpTransformer, StandardEffects}
import squid.lang.Optimizer
import squid.quasi.{phase, embed}

@embed object Test { @phase('MyPhase) def foo[T](xs: List[T]) = xs.head }

// See: /README.md
object IntroExample extends App {
  
  object IR extends SimpleANF with StandardEffects {
    embed(Test)
  }
  import IR.Predef._
  import IR.Quasicodes._
  import IR.Lowering
  
  val pgrm0 = code"(Test.foo(1 :: 2 :: 3 :: Nil) + 1).toDouble"
  println(pgrm0)
  val pgrm1 = pgrm0 transformWith (new Lowering('MyPhase) with BottomUpTransformer)
  println(pgrm1)
  val pgrm2 = pgrm1 fix_rewrite {
    case code"($xs:List[$t]).::($x).head" => x
    case code"(${Const(n)}:Int) + (${Const(m)}:Int)" => Const(n+m)
  }
  println(pgrm2)
  println(pgrm2.compile)
  
  
  // Making a dedicated static optimizer –– see the usage example in test file `IntroExampleTest`:
  
  class TestOptimizer extends IR.SelfCodeTransformer {
    def transform[T,C](pgrm0: Code[T,C]): Code[T,C] = {
      // same transformation as above:
      val pgrm1 = pgrm0 transformWith (new Lowering('MyPhase) with BottomUpTransformer)
      val pgrm2 = pgrm1 fix_rewrite {
        case code"($xs:List[$t]).::($x).head" => x
        case code"(${Const(n)}:Int) + (${Const(m)}:Int)" => Const(n+m)
      }
      pgrm2
    }
  }
  
}
