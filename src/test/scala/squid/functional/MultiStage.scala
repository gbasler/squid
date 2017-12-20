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

import squid.utils.meta.RuntimeUniverseHelpers.sru

class MultiStage extends MyFunSuite {
  import TestDSL.Predef._
  import TestDSL.Quasicodes._
  
  test("Nested Quoted Types") {

    val reprep = code"MultiStage.rep" : Q[ Q[Double, {}], {} ]
    val rep = reprep.run

    assert(rep == MultiStage.rep)

  }
  
  test("Nested ir expressions") {
    
    {
      val ir2 = code{ code{42} } : Code[Code[Int,{}],{}]
      val ir1 = ir2.run : Code[Int,{}]
      assert(ir1 =~= code"42")
      assert(ir1.run == 42)
    }
    
    {
      val ir2 = code{ code{scala.None} } : Code[Code[Option[Int],{}],{}]
      val ir1 = ir2.run : Code[Option[Int],{}]
      assert(ir1 =~= code"scala.None")
      assert(ir1.run == scala.None)
    }
    
    {
      val ir2 = code{ code{ List(1,2,3) map (_ + 1 toDouble) } } : Code[Code[List[Double],{}],{}]
      val ir1 = ir2.run : Code[List[Double],{}]
      assert(ir1 =~= code"List(1,2,3) map (_ + 1 toDouble)")
      assert(ir1.run == { List(1,2,3) map (_ + 1 toDouble) })
    }
    
    /* NOTE: in commit e1370f61f4072fe6a69bbff3890dc78152c56c10, a bug made that we did not try to dealias types and so
     * no type tag could be found for `base.MtdSymbol`.
     * Curiously, if we fell back on `unknownTypefallBack` even though no TypeTag was found,
     * the generated __b__.uninterpretedType[__b__.MtdSymbol], which was looking for a TypeTag[MtdSymbol],
     * made the compiler loop indefinitely -- but only when in the middle of the big gen'd code (not in isolation)!
     * (See cod eat end of file.)
     */
    
    assert(codeTypeOf[base.MtdSymbol].rep == base.uninterpretedType[sru.MethodSymbol])
    
    // Note:
    //ir{ ir{ ir{ List(1,2,3) map (_ + 1 toDouble) } } }  // Error:(49, 7) Embedding Error: Unsupported feature: TypeTag construction (for scp.utils.meta.RuntimeUniverseHelpers.sru.TypeSymbol)
    
  }
  
  test("Doubly-Nested ir expressions") {
    
    {
      val ir3 = code{ code{ code{scala.None} } } : Code[Code[Code[Option[Int],{}],{}],{}]
      val ir2 = ir3.run : Code[Code[Option[Int],{}],{}]
      val ir1 = ir2.run : Code[Option[Int],{}]
      //assert(ir2 =~= code""" code"scala.None" """)
      // ^ Note: this one is false because ir2, which uses QC, refers to `TestDSL2.Quasicodes.qcbase` while the other,
      // which uses QQ, refers to `TestDSL2.Predef.base`, which are semantically equivalent but different trees.
      assert(ir2 =~= code""" code{scala.None} """)
      assert(ir1 =~= code"scala.None")
      assert(ir1.run == scala.None)
    }
    
    // Note:
    // ir{ ir{ ir{ ir{scala.None} } } }  // Error:(55, 17) Embedding Error: Unsupported feature: TypeTag construction (for scp.utils.meta.RuntimeUniverseHelpers.sru.TypeSymbol)
    
  }
  
}
object MultiStage {
  import TestDSL.Predef._
  
  val rep = code"42.toDouble"
  
}
