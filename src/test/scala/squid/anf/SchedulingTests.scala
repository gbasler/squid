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
  
  def foo(x: => Int, y: => Int) = x + y
  
}
class SchedulingTests extends MyFunSuite(TestDSL) {
  import SchedulingTests.DSL.Predef._
  
  def scheduled(expectedResult: Any = null)(cde: ClosedCode[Any]) = {
    if (expectedResult =/= null) same(cde.run, expectedResult)
    cde reinterpretIn TestDSL
  }
  
  test("Basic Scheduling") {
    
    scheduled("ok'ok")(code"'ok.name+(if (true) 'ok.toString else 'ok.name+1)") eqt {
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
    
    scheduled("okok1")(code"'ok.name+(if ('ok.name.isEmpty) 'ok.toString else 'ok.name+1)") eqt {
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
  
    scheduled()(code"{(a:Int->(Int->(Int->Int))) => println(a._2._2._1); identity((x:Int) => a._2._2._1 + x)}") eqt {
      import TestDSL.Predef._
      code"""(a_0: Int->(Int->(Int->Int))) => {
        val sch_1 = a_0._2._2._1;
        scala.Predef.println(sch_1);
        scala.Predef.identity[scala.Function1[scala.Int, scala.Int]](((x_2: scala.Int) => sch_1.+(x_2)))
      }"""
    }
    
    scheduled(2)(code"(List(1).toIndexedSeq:IndexedSeq[Int]).size+(List(1).toIndexedSeq:IndexedSeq[Int]).size") eqt {
      import TestDSL.Predef._
      code"""{
        val sch_0 = ((scala.collection.immutable.List.apply[scala.Int](1).toIndexedSeq): scala.collection.IndexedSeq[scala.Int]);
        val x_1 = sch_0.size;
        val x_2 = sch_0.size;
        x_1.+(x_2)
      }"""
      // Note that scala.collection.Seq#size is not pure in general, because of mutable collections
    }
    
  }
  
  test("Special Constructs") { // TODO a way to generalize them... base them on annotations?
    
    // TODO special-handling of if/while:
    //scheduled(code"if (true) 'ok.toString else 'ok.name") alsoApply println
    //scheduled(code"while ('ok.name.isEmpty) println('ok.name)") alsoApply println
    
  }
  
  test("Lazy") {
    import SchedulingTests.foo
    
    // TODO option to aggressively schedule out of unknown lambdas even when not shared...
    
    val model0 = {
      import TestDSL.Predef._
      code"""{
        val lsch_0 = squid.utils.Lazy.apply(scala.Symbol.apply("ok").name);
        squid.anf.SchedulingTests.foo(lsch_0.value.length(), lsch_0.value.hashCode())
      }"""
    }
    val cde0 = code"foo('ok.name.length, 'ok.name.hashCode)"
    cde0 |> scheduled() eqt model0
    (cde0 reinterpretIn SchedulingTests.DSL) |> scheduled() eqt model0
    // ^ test idempotence in the presence of Lazy scheduling; works because Lazy is transparency-propagating and ANF reduces Lazy(x).value
    
    code"'ok.name.length + (if ('ok.name.isEmpty) foo('ok.name.length, 'ok.name.hashCode) else 0)" |> scheduled(2) eqt {
      import TestDSL.Predef._
      code"""{
        val sch_0 = scala.Symbol.apply("ok").name;
        val sch_1 = sch_0.length();
        val x_2 = if (sch_0.isEmpty())
          squid.anf.SchedulingTests.foo(sch_1, sch_0.hashCode())
        else
          0;
        sch_1.+(x_2)
      }"""
    }
    
    code"'ok.name.length + foo('ok.name.length, 'ok.name.hashCode)" |> scheduled(2+2+'ok.name.hashCode) eqt {
      import TestDSL.Predef._
      code"""{
        val sch_0 = scala.Symbol.apply("ok").name;
        val sch_1 = sch_0.length();
        val x_2 = squid.anf.SchedulingTests.foo(sch_1, sch_0.hashCode());
        sch_1.+(x_2)
      }"""
    }
    
  }
  
}
