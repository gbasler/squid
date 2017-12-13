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

object ControlFlowTests {
  
  object DSL extends SimpleANF with ControlFlowBase
  
}
class ControlFlowTests extends MyFunSuite(ControlFlowTests.DSL) {
  import DSL.Predef._
  import base.{OneOf}
  
  test("OneOf") {
    
    def f(q:Code[_,{}]) = q rewrite {
      //case base.OneOf(oo) =>  // Error:(23, 12) Could not determine extracted type for that case.
      case code"${OneOf(oo)}:$t" =>
        //println(oo)
        
        val a = oo.alt rewrite {
          case code"${Const(n)}:Int" => Const(n+1)
        }
        val _ : Code[t.Typ,oo.C1] = a
        
        oo.rebuild(oo.main, a)
        //oo.rebuild(oo.main, a.asInstanceOf[IR[t.Typ,Any]]) // used to be necessary, cf. RwR path-dep type problems
        
    }
    
    code"println(if (readInt>0) 1 else 2)" |> f eqt
      code"println(if (readInt>0) 1 else 3)"
    
    code"Option(readInt).filter(_ > 0).fold(0)(_ + 1)" |> f eqt
      code"Option(readInt).filter(_ > 0).fold(0)(_ + 2)"
    
  }
  
  
}
