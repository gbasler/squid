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
  
  
  test("Explicit Empty Contexts and Inferred Contexts") {
    
    List[IR[Int,{}]](ir"1", ir"2", ir"3").foldLeft(ir"0") { (acc, exp) =>
      ir"if ($acc != 0) $acc else $exp"
    }
    
  }
  
  
}
