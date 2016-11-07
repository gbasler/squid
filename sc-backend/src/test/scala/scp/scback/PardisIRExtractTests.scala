package scp
package scback

import ch.epfl.data.sc.pardis.deep.scalalib.ScalaPredefIRs

import collection.mutable.ArrayBuffer

class PardisIRExtractTests extends PardisTestSuite {
  
  import Sqd.Predef._
  import Sqd.Quasicodes._
  
  
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
  
  
  test("Rewriting Consecutive Statements") {
    
    assert(SC.OptionApplyObject(null)(SC.typeInt).isPure) // we have the right SC version
    
    
    val a0 = ir{ val arr = ArrayBuffer(1,2,3); arr append 1; arr.size }
    
    val a1 = /*SDSL.debugFor*/ { a0 rewrite {
      case ir"($arr: ArrayBuffer[$t]) append $x" =>
        //println("Running rwr code!!")
        ir{ println("nope!") }
    }}
    
    //println(a1) // ir"{ val arr1 = ArrayBuffer.apply(1, 2, 3); val x10 = println("nope!"); val x7 = arr1.size; x7 }"
    
    assert(stmts(a1)(2).rhs == ScalaPredefIRs.Println(SC.unit("nope!")(SC.typeString)))
    // another way to check:
    stmts(a1) match {
      case liftedSeqShit :: _ :: p :: _ :: Nil =>
        assert(p.rhs == dfn(ir{ println("nope!") }))
      case _ => fail
    }
    
    val b0 = ir{ val arr = new ArrayBuffer[Int](); Option(arr append 1); arr.clear; arr.size } // TODO try to rm pure stmts referring to rm'd syms (trans clos)
    //val b0 = block(ir{val arr = new ArrayBuffer[Int](); val lol = arr append 1; arr.clear; println(lol); arr.size}) // TODO should not apply
    //println(b0)
    
    val q2 = /*SDSL.debugFor*/ { b0 rewrite {
      case ir"($arr: ArrayBuffer[$t]) append $x; (arr:ArrayBuffer[t]).clear" =>
        //println("Running rwr code!!")
        ir{ $(arr).clear }
    }}
    
    //println(q2) // ir"{ val arr19 = new ArrayBuffer[Int](); val x26 = Option.apply(x22); val x34 = arr19.clear(); val x28 = arr19.size; x28 }"
    
    
  }
  
  
  
  
  
  
  
  
  
  
  
}

