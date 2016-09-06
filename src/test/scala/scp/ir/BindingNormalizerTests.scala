package scp
package ir

import ir2._

class BindingNormalizerTests extends MyFunSuite2(BindingNormalizerTests) {
  import DSL.Predef._
  
  test("Online Normalization") {
    
    ir"val a = 1; val b = 2; a + b" eqt
      ir"((a: Int) => (b: Int) => a + b)(1)(2)"
    
  }
  
}
object BindingNormalizerTests extends SimpleAST with OnlineOptimizer with BindingNormalizer
