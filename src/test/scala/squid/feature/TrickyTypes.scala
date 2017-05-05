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
  
  
  test("Lambda with Expected Type") {
    import base.Quasicodes._
    
    val c1: IR[Int => Int => Bool => Bool,_] = ir{ (s:Int) =>
      val n = 42
      k => { b => b }
    }
    
    eqt(c1.typ, irTypeOf[Int => Int => Bool => Bool])
    
  }
  
  
  def foo[T](x:IR[T,{}]) = ir"$x==0"
  
  test("Any method called on type parameter") {
    foo(ir"42") eqt ir"${ir"42:Any"} == 0"
    foo(ir"42") neqt ir"${ir"42"} == 0" // Note: Int.== and Any.== have two different method symbols!!
  }
  
  
}
