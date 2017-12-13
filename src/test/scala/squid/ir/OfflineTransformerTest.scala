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
package ir

import squid.lang.InspectableBase

class OfflineTransformerTest extends MyFunSuite {
  import TestDSL.Predef._
  
  test ("Basic Rewriting") {
    
    object Trans extends TestDSL.SelfTransformer with SimpleRuleBasedTransformer with BottomUpTransformer {
      rewrite { case code"666" => code"42" }
    }
    eqt( code"('answer, 666)" transformWith Trans, code"('answer, 42)" )
    
  }
  
  test ("Prevent Widened Base Type") {
    
    //assertDoesNotCompile("""
      object Trans extends SimpleRuleBasedTransformer with BottomUpTransformer {
        val base: InspectableBase = TestDSL
        //rewrite { case ir"42" => ir"42" }  // lol, scalac stack overflow...
      }
    //""") //Used to get: // Error:(10, 24) Embedding Error: Could not verify that `scp.ir.OfflineTransformerTest.Trans.base.type` is the same as `OfflineTransformerTest.this.DSL.type`
    
  }
  
  test ("Bottom Up Rewriting") {
    
    object Trans extends TestDSL.SelfTransformer with SimpleRuleBasedTransformer with BottomUpTransformer { rewrite {
      case code"(1,0)" => code"(0,1)"
      case code"(0,(0,1))" => code"(1,(0,0))"
      case code"(0,(1,0))" => code"(1,(1,1))"
    }}
    eqt( code"(0,(1,0))" transformWith Trans, code"(1,(0,0))" )
    
  }
  
  
}
