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
import squid.lib
import squid.ir.graph.{SimpleASTBackend => AST}

import haskell.Builder
import haskell.Prelude._

object ListFusionTests extends Graph

class ListFusionTests extends MyFunSuite(ListFusionTests) with GraphRewritingTester[ListFusionTests.type] {
  import DSL.Predef._
  import DSL.Quasicodes._
  
  object Tr extends SimpleRuleBasedTransformer with DSL.SelfTransformer {
    
    rewrite {
      case c"666" => c"42"
        
      case c"sum" => c"foldr(+)(0)"
      case c"down($n)" => c"build(downBuild($n))"
        
      case c"foldr[$ta,$tb]($k)($z)(build[ta]($g))" =>
      // TODO add condition that the build node not be shared...
        println(s">>> FUSION of $k $z $g")
        c"$g($k)($z)"
      
    }
    
  }
  
  test("A") {
  override def doTest[A](cde: ClosedCode[A], expectedSize: Int = Int.MaxValue)
      (expectedResult: Any = null, preprocess: A => Any = id[A], doEval: Bool = true) = {
    val rep = super.doTest(cde,expectedSize)(expectedResult, preprocess, doEval)
    
    val tree = DSL.treeInSimpleASTBackend(rep)
    println("Haskell:\n"+ToHaskell(AST)(tree))
    
    rep
  }
  
  
    
    doTest(c"666.toDouble")()
    doTest(c"sum(Nil)")()
    doTest(c"down(666)")()
    
  }
  
  test("B") {
    
    doTest(c"sum(down(5))")(0 to 5 sum)
    
  }
  
  
  
}
