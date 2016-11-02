package scp
package scback

import collection.mutable.ArrayBuffer
import DSLBindingTest.{SCDSL, SDSL}
import ch.epfl.data.sc.pardis.ir.Constant

class PardisIRExtractTests extends MyFunSuiteBase(SDSL) {
  
  DSLBindingTest
  
  import SDSL.Predef._
  import SDSL.Quasicodes._
  import SDSL.{block, pardisBlock}
  
  /*_*/
  
  test("Constants") {
    
    val q = ir{42}
    
    q match {
      case ir"${Const(x)}" =>
        x [Int]
    }
    
    q matches {
      case ir"42" =>
    } and {
      case ir"${Const(x)}:Double" => 
        x [Double]
        fail
      case ir"${Const(x)}:Int" =>
        x [Int]
        assert(x === 42)
    } and {
      case ir"$x:Double" => fail
      case ir"$x:Nothing" => fail
      case ir"$x:Int" =>
        assert(x === Const(42))
    }
    
  }
  
  
  test("Methods") {
    // TODO impl proper extraction
    
    //block(ir{ArrayBuffer(1,2,3)}) match {
    //  case ir"ArrayBuffer(1,2,3)" =>
    //}
    
  }
  
  
  test("Rewriting") {
    
    ///*{
      val q = block(ir{val arr = ArrayBuffer(1,2,3); arr append 1; arr.size})
      println(q)
      
      val q2 = /*SDSL.debugFor*/ { q rewrite {
        case ir"($arr: ArrayBuffer[$t]) append $x" =>
          println("Running rwr code!!")
          ir{ println("nope!") } 
      }}
      
      println(q2)
    //}*/
    
    {
      //val q = block(ir{val arr = new ArrayBuffer[Int](); arr append 1; arr.clear; arr.size})
      val q = block(ir{val arr = new ArrayBuffer[Int](); val lol = arr append 1; arr.clear; arr.size}) // FIXedME should apply  // TODO try to rm pure stmts referring to rm'd syms (trans clos)
      //val q = block(ir{val arr = new ArrayBuffer[Int](); val lol = arr append 1; arr.clear; println(lol); arr.size}) // TODO should not apply
      println(q)
      
      val q2 = /*SDSL.debugFor*/ { q rewrite {
        case ir"($arr: ArrayBuffer[$t]) append $x; (arr:ArrayBuffer[t]).clear" =>
          println("Running rwr code!!")
          ir{ $(arr).clear }
      }}
      
      println(q2)
      
    }
    
    
  }
  
  
  
  
  
  
  
  
  
  
  
}

