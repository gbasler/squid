package scp
package ir

class OfflineTransformerTest extends MyFunSuite {
  import DSL._
  
  test ("Basic Rewriting") {
    
    object Trans extends ir.OfflineTransformer(TestDSL) {
      rewrite { case dsl"666" => dsl"42" }
    }
    eqt( Trans bottomUp dsl"('answer, 666)", dsl"('answer, 42)" )
    
  }
  
  test ("Prevent Widened Base Type") {
    
    assertDoesNotCompile("""
      object Trans extends ir.OfflineTransformer[lang.Base](TestDSL) {
        rewrite { case dsl"42" => dsl"42" }
      }
    """) // Error:(10, 24) Embedding Error: Could not verify that `scp.ir.OfflineTransformerTest.Trans.base.type` is the same as `OfflineTransformerTest.this.DSL.type`
    
  }
  
  test ("Bottom Up Rewriting") {
    
    object Trans extends ir.OfflineTransformer(TestDSL) { rewrite {
      case dsl"(1,0)" => dsl"(0,1)"
      case dsl"(0,(0,1))" => dsl"(1,(0,0))"
      case dsl"(0,(1,0))" => dsl"(1,(1,1))"
    }}
    eqt( Trans bottomUp dsl"(0,(1,0))", dsl"(1,(0,0))" )
    
  }
  
  
}
