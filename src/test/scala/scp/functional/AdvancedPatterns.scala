package scp
package functional

// TODO port Pattern Alternation
/*

import ir._
import PatternAlternation._

class AdvancedPatterns extends MyFunSuite(AdvancedPatterns.DSL) {
  import DSL._
  import DSL.Quasi.QuasiContext
  
  test("Pattern Alternation") {
    
    intercept[IllegalArgumentException](dsl".5 | 1.5")
    
    dsl".5" match {
      case dsl".6 | 1.5" => fail
      case dsl".5 | 1.5" =>
    }
    dsl"1.5" match {
      case dsl".5 | 1.5" =>
    }
    
    dsl"42.toDouble + 1" match {
      case dsl"(($x: Int).toDouble + 1) | x" =>
        eqt(x, dsl"42")
    }
    dsl"42.toDouble + 2" match {
      case dsl"(($x: Int).toDouble + 1) | x" =>
        eqt(x, dsl"42.toDouble + 2")
    }
    dsl"42.toDouble + 2" match {
      //case dsl"(x.toDouble + 1) | ($x: Int)" => // nope
      //case dsl"(($x: Int).toDouble + 1) | x.toDouble" => // nope
      case dsl"(($x: Int).toDouble + 1) | (x: Int).toDouble + 2" =>
        eqt(x, dsl"42")
    }
    
    /*
    // TODO: better, earlier error
    dsl"42.toDouble" match {
      case dsl"(($x: Int).toDouble) | ($y: Double)" =>
        println(y)
    }
    */
    
    
    
    
    
  }
  
  
  
  
}
object AdvancedPatterns {
  
  object DSL extends AST with MyDSL with lang.ScalaTyping with PatternAlternation
  
}
*/


