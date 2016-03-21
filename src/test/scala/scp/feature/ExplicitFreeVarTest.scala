package scp
package feature

import org.scalatest.FunSuite

class ExplicitFreeVarTest extends FunSuite {
  
  import TestDSL._
  
  test("Explicit Free Variables") {
    
    val x: Q[Int,{val x: Int}] = dsl"$$x: Int"
    assert(x.rep match {
      case Ascribe(HoleExtract("x")) => true
      case _ => false
    })
    
    val d = dsl"$x.toDouble" : Q[Double, {val x: Int}]
    
    val s = dsl"($$str: String) + $d" : Q[String, {val x: Int; val str: String}]
    
    val closed = dsl"(str: String) => (x: Int) => $s" : Q[String => Int => String, {}]
    val closed2 = dsl"(x: Int) => (str: String) => $s" : Q[Int => String => String, {}]
    
    assert(closed =~= dsl"(a: String) => (b: Int) => a + b.toDouble")
    assert(closed2 =~= dsl"(b: Int) => (a: String) => a + b.toDouble")
    
    
  }
  
  
  
}



