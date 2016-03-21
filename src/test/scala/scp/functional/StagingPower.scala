package scp
package functional

import org.scalatest.FunSuite

class StagingPower extends FunSuite {
  
  import TestDSL._
  
  def power[C](n: Int)(q: Q[Double,C]): Q[Double,C] =
    if (n == 0) dsl"1.0" else dsl"$q * ${power(n-1)(q)}"
  
  
  test("power(3)(42)") {
    
    val n = dsl"42.0"
    
    val p3 = power(3)(n)
    
    assert(p3 =~= dsl"$n * ($n * ($n * 1.0))")
    
  }
  
  test("x => power(3)(x)") {
    
    val p3f = dsl"(x: Double) => ${power(3)(dsl"$$x:Double")}" // TODO look at what this generates...
    
    assert(p3f =~= dsl"(x: Double) => x * (x * (x * 1.0))")
    assert(p3f =~= dsl"(y: Double) => y * (y * (y * 1.0))")
    
  }
  
  
}






















