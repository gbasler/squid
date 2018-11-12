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
package ir
package graph2

import utils._
import squid.lib.matching._
import squid.lib.const

object GraphRewritingTests extends Graph {
  object Tr extends SimpleRuleBasedTransformer with SelfTransformer {
    import Predef._
    
    rewrite {
      case c"666" => c"42"
      case code"(($x: $xt) => $body:$bt)($arg)" =>
        println(s"!>> SUBSTITUTE ${x.rep} with ${arg.rep} in ${body.rep.showGraph}")
        val res = body.subs(x) ~> arg
        println(s"!<< SUBSTITUTE'd ${res.rep.showGraph}")
        res
    }
    
  }
}

import GraphRewritingTests._

class GraphRewritingTests extends MyFunSuite(GraphRewritingTests) {
  import DSL.Predef._
  import DSL.Quasicodes._
  
  def doTest(cde: Code[Any,Nothing], expectedSize: Int = Int.MaxValue)(expectedResult: Any = null) = {
    println("\n-> "+cde.rep.showGraph+"\n-> "+cde.show)
    //println(DSL.edges)
    val ite = DSL.rewriteSteps(DSL.Tr)(cde.rep)
    while(ite.hasNext) {
      println("Rw "+ite.next)
      println(s"${Console.BOLD}~> Transformed:${Console.RESET} "+cde.rep.showGraph+"\n~> "+cde.show)
    }
  }
  
  test("A") {
    
    doTest(code"666.toDouble")()
    
  }
  
  test("Basic Cross-Boundary Rewriting") {
    
    val f = code"(x: Int) => x + x"
    
    doTest(code"val f = $f; f(11) + f(22)", 1)(66)
    
    doTest(code"val f = $f; f(f(22))", 1)(88)
    
  }
  
}
