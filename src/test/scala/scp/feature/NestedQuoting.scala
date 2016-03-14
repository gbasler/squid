package scp
package feature

import org.scalatest.FunSuite

class NestedQuoting extends FunSuite {
  
  import TestDSL._
  
  
  test("Simple Nesting") {
    
    assert(dsl"42.toString * 2" =~= dsl"42.toString * 2")
    
    assert(dsl"42.toString * 2" =~= dsl"${ dsl"42.toString" } * 2")
    
  }
  
  
  test("Block Nesting") {
    
    assert(dsl"42.toString * 2" =~= dsl"${ val n = dsl"42"; dsl"$n.toString" } * 2")
    
    assert(dsl"42.toDouble.toString * 2" =~= dsl"${ val n = dsl"42.toDouble"; dsl"$n.toString" } * 2")
    
  }
  
  
  test("Double Nesting") {
    
    assert(dsl"42.toDouble.toString * 2" =~= dsl"${ val str = dsl"${ val n = dsl"42"; dsl"$n.toDouble" }.toString"; str } * 2")
    
    assert(dsl"42.toDouble.toString * 2" =~= dsl"${ val n = dsl"42"; val str = dsl"${ dsl"$n.toDouble" }.toString"; str } * 2")
    
  }
  
  
  
  
}





