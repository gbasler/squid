package squid
package ir

import squid.lang.InspectableBase

class OfflineTransformerTest extends MyFunSuite {
  import TestDSL.Predef._
  
  test ("Basic Rewriting") {
    
    object Trans extends TestDSL.SelfTransformer with SimpleRuleBasedTransformer with BottomUpTransformer {
      rewrite { case ir"666" => ir"42" }
    }
    eqt( ir"('answer, 666)" transformWith Trans, ir"('answer, 42)" )
    
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
      case ir"(1,0)" => ir"(0,1)"
      case ir"(0,(0,1))" => ir"(1,(0,0))"
      case ir"(0,(1,0))" => ir"(1,(1,1))"
    }}
    eqt( ir"(0,(1,0))" transformWith Trans, ir"(1,(0,0))" )
    
  }
  
  
}
