package scp
package feature

import org.scalatest.FunSuite
import org.scalatest.ShouldMatchers

class Run extends FunSuite with ShouldMatchers {
  
  import TestDSL._
  
  test("Functions") {
    
    assert(dsl"((x: Int) => x + 1)(42)".run == 43)
    
  }
  
  test("Compile Error On Open Terms") {
    
    """ dsl"($$x: Int) + 1".run """ shouldNot compile
    
    val x = dsl"42": Q[Int, {val x: Int}]
    """ x.run """ shouldNot compile
    
  }
  
}
