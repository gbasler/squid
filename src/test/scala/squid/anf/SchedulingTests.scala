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
        val sch_0 = "ok";
        val sch_1 = scala.Symbol.apply(sch_0);
        val sch_2 = sch_1.name;
        sch_2.+(if (true)
          sch_1.toString()
        else
          sch_2.+(1))
      }"""
    }
    
    scheduled(code"'ok.name+(if ('ok.name.isEmpty) 'ok.toString else 'ok.name+1)") eqt {
      import TestDSL.Predef._
      code"""{
        val sch_0 = scala.Symbol.apply("ok").name;
        sch_0.+({
          val sch_1 = "ok";
          val sch_2 = scala.Symbol.apply(sch_1);
          if (sch_0.isEmpty())
            sch_2.toString()
          else
            sch_0.+(1)
        })
      }"""
    }
  
    // TODO better than this...
    scheduled(code"{(a:Int->(Int->(Int->Int))) => println(a._2._2._1); identity((x:Int) => a._2._2._1 + x)}") eqt {
      import TestDSL.Predef._
      code"""(a_0: Int->(Int->(Int->Int))) => {
        val sch_1 = a_0._2;
        val sch_2 = sch_1._2;
        val sch_3 = sch_2._1;
        scala.Predef.println(sch_3);
        scala.Predef.identity[scala.Function1[scala.Int, scala.Int]](((x_4: scala.Int) => sch_3.+(x_4)))
      }"""
    }
    
    // TODO special-handling of if/while:
    //scheduled(code"if (true) 'ok.toString else 'ok.name") alsoApply println
    //scheduled(code"while ('ok.name.isEmpty) println('ok.name)") alsoApply println
    
  }
  
}
