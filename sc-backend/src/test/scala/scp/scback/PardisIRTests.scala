package scp.scback

import org.scalatest.FunSuite

import collection.mutable.ArrayBuffer
import DSLBindingTest.{DSL, SDSL}
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
  
  
  test("While Loop") {} // TODO
  
  
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
          case DSL.ArrayBufferNew1(Constant(42)) =>
        }
    }
  }
  
  
  test("Companion Object Methods") {} // TODO
  
  
  test("Blocks & Bindings") {
    // TODO remove extra bindings?
    
    val q = block(ir{ val n = 0; val a = new ArrayBuffer[Int](n); val b = 1; val c = a append 1; println(c) })
    //println(q)
    
  }
  
  
  
  
}
