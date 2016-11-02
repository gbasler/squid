package scp.scback

import org.scalatest.FunSuite

import collection.mutable.ArrayBuffer
import DSLBindingTest.{SCDSL, SDSL}
import ch.epfl.data.sc.pardis.ir.Constant

class PardisIRTests extends FunSuite {
  
  DSLBindingTest
  
  import SDSL.Predef._
  import SDSL.Quasicodes._
  import SDSL.{block, pardisBlock}
  
  
  test("If Then Else") {
    val q = pardisBlock(ir{ if (1.5.toInt > 0) println("ok") else "ko" })
    assert(q.stmts.size == 3)
  }
  
  
  //test("While Loop") {} // TODO
  
  
  test("Code Insertion") {
    
    def f(a: IR[Int,{}], b: IR[Int,{}]) = ir{ $(a) + $(a) + $(b) }
    
    val q = pardisBlock(ir{ $( f(ir{1.5.toInt}, ir{666}) ) + 1 })
    
    //println(q)
    assert(q.stmts.size == 4)
    
  }
  
  
  test("Lambdas & Applications") {
    
    def f = ir{ (a: Int) => a+1 }
    
    val q0 = block(f)
    val q1 = block(ir{ $(f)(42) })
    
    //println(q0)
    //println(q1)
    
  }
  
  
  test("Methods") {
    
    val q = block(ir{ println(new ArrayBuffer[Int](0) append 1) })
    //println(q)
    
  }
  
  
  test("Constructors") {
    
    val q = block(ir{ new ArrayBuffer(42) })
    
    q.rep match {
      case SDSL.TopLevelBlock(b) =>
        assert(b.stmts.size == 1)
        b.res.correspondingNode match {
          case SCDSL.ArrayBufferNew1(Constant(42)) =>
        }
    }
  }
  
  
  test("Companion Object Methods") {
    // Note: both `apply` methods mirrored in MirrorSeq and MirrorArrayBuffer refer to the _same_ method defined in GenericCompanion
    // so one will overwrite the other; to avoid that we'll have to manually make the binding by overriding `methodApp`
    
    val q0 = block(ir{ Seq(1,2,3) })         // ir"{ val x23 = ArrayBuffer.apply(1, 2, 3) x23 }"
    val q1 = block(ir{ ArrayBuffer(1,2,3) }) // ir"{ val x23 = ArrayBuffer.apply(1, 2, 3) x23 }"
    //println(q0)
    //println(q1)
    
  }
  
  
  test("Blocks & Bindings") {
    // TODO remove extra bindings?
    
    val q = block(ir{ val n = 0; val a = new ArrayBuffer[Int](n); val b = 1; val c = a append 1; println(c) })
    //println(q)
    
  }
  
  
  test("String") {
    
    val q = block(ir{ println(((x: String) => x.length)("ok")) })
    //println(q)
    
  }
  
  
  //test("Variables") {}  // TODO
  
  
  
  
  
  
}
