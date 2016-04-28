package scp
package feature

import org.scalatest.FunSuite
import org.scalatest.ShouldMatchers

import utils.Debug._

class Matching extends FunSuite with ShouldMatchers {
  import TestDSL._
  
  test("Type Ascription") {
    
    (dsl"42": Q[_,_]) match {
      case dsl"$x: Double" => ???
      case dsl"$x: Int" =>
        assert(x === dsl"42")
    }
    
    val n = dsl"42"
    val m = dsl"42:Int"
    assert(n =~= m)
    assert(dsl"$n * $m" =~= dsl"$m * $n")
    
    assert(dsl"(x: Int) => x + $n" =~= dsl"(x: Int) => x + $m")
    
  }
  
  test("Methods") {
    
    dsl"42.toDouble" match {
      case dsl"($x: Int).toDouble" =>
        assert(x =~= dsl"42")
    }
    
    val t = dsl"42.toDouble"
    val s = dsl".5 * $t"
    
    s match {
      case dsl"($a: Double) * ($b: Double)" =>
        assert(a =~= dsl"0.5")
        assert(b =~= dsl"42.toDouble")
    }
    
    
  }
  
  test("Free Variables") {
    
    //assert(dsl"$$x" =~= dsl"$$x")
    assert(dsl"$$x:Int" =~= dsl"$$x:Int")
    assert(dsl"($$x:Int)+1" =~= dsl"($$x:Int)+1")
    
  }
  
  test("Construction Unquotes in Extractors") {
    
    val x = dsl"42"
    dsl"(42, 666)" match {
      case dsl"($$x, 666)" => 
    }
    
    /*
    // Note: the following syntax is not special-cased (but could be):
    val xs = Seq(dsl"1", dsl"2")
    dsl"List(1, 2)" match {
      case dsl"($$xs*)" => 
    }
    */
  }
  
  
}














