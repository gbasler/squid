package scp
package feature

import org.scalatest.FunSuite

class RepEquality extends FunSuite {
  
  import TestDSL._
  
  test("Functions") {
    
    assert(dsl"(x: Int) => x" =~= dsl"(x: Int) => x")
    
    assert(dsl"(x: Int) => x" =~= dsl"(y: Int) => y")
    
    assert(dsl"val x = 42.toDouble; x + 1" =~= dsl"val y = 42.toDouble; y + 1")
    
  }
  
  
  
}
