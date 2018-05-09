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
package anf

import ir._
import squid.utils._
import squid.anf.analysis.BlockHelpers

object SchedulingTests {

  object DSL extends SchedulingANF with lang.ScalaCore with StandardEffects
    with BlockHelpers
  
}
class SchedulingTests extends MyFunSuite(TestDSL) {
  import SchedulingTests.DSL.Predef._
  
  def scheduled(cde: OpenCode[Any]) = cde reinterpretIn TestDSL
  
  test("Basic Scheduling") {
    
    scheduled(code"'ok.name+(if (true) 'ok.toString else 'ok.name+1)") eqt {
      import TestDSL.Predef._
      code"""{  
        val sch_0 = scala.Symbol.apply("ok");
        val sch_1 = sch_0.name;
        sch_1.+(if (true)
          sch_0.toString()
        else
          sch_1.+(1))
      }"""
    }
    
    scheduled(code"'ok.name+(if ('ok.name.isEmpty) 'ok.toString else 'ok.name+1)") eqt {
      import TestDSL.Predef._
      code"""{
        val sch_0 = scala.Symbol.apply("ok");
        val sch_1 = sch_0.name;
        sch_1.+(if (sch_1.isEmpty())
          sch_0.toString()
        else
          sch_1.+(1))
      }"""
    }
  
    scheduled(code"{(a:Int->(Int->(Int->Int))) => println(a._2._2._1); identity((x:Int) => a._2._2._1 + x)}") eqt {
      import TestDSL.Predef._
      code"""(a_0: Int->(Int->(Int->Int))) => {
        val sch_1 = a_0._2._2._1;
        scala.Predef.println(sch_1);
        scala.Predef.identity[scala.Function1[scala.Int, scala.Int]](((x_2: scala.Int) => sch_1.+(x_2)))
      }"""
    }
    
    // TODO special-handling of if/while:
    //scheduled(code"if (true) 'ok.toString else 'ok.name") alsoApply println
    //scheduled(code"while ('ok.name.isEmpty) println('ok.name)") alsoApply println
    
  }
  
}
