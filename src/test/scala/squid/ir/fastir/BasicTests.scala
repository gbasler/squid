package squid
package ir.fastir

import utils._
import squid.ir.FastANF

class BasicTests extends MyFunSuiteBase(BasicTests.Embedding) {
  import BasicTests.Embedding.Predef._
  
  // TODO make proper tests...
  
  test("Basics") {
    
    println(ir"(x:Int) => x.toDouble.toInt + 42.0.toInt")
    
  }
  
  test("Arguments") {
    import squid.ir.fastanf._
    
    val c0 = Constant(0)
    val c1 = Constant(1)
    val c2 = Constant(2)
    
    assert(c1 ~: c2 ~: NoArguments == ArgumentCons(c1, c2))
    assert(c0 ~~: (c1 ~: c2) ~~: NoArguments == ArgumentListCons(c0, ArgumentListCons(ArgumentCons(c1, c2), NoArguments)))
    assert(c0 ~~: (c1 ~: c2 ~: NoArguments) ~~: NoArgumentLists == ArgumentListCons(c0, ArgumentCons(c1, c2)))
    
  }
  
}
object BasicTests {
  object Embedding extends FastANF
}
