package squid
package feature

import squid.ir.RewriteAbort
import utils._

class TrickyTypes extends MyFunSuite {
  import TestDSL.Predef._
  
  test("Local Type Synonym") {
    
    ir"Option.empty[String]" eqt {
      type String = java.lang.String
      ir"Option.empty[String]"
    }
    
  }
  
  test("Local Type With Evidence") {
    
    class Lol
    
    assertDoesNotCompile("""
      ir"Option.empty[Lol]"
    """)
    
    ir"Option.empty[Int]" eqt {
      implicit val LolImpl = irTypeOf[Int].asInstanceOf[IRType[Lol]]
      ir"Option.empty[Lol]"
    }
    
  }
  
  
}
