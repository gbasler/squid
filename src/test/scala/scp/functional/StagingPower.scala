package scp
package functional

class StagingPower extends MyFunSuite2 {
  
  import TestDSL2.Predef._
  
  def power[C](n: Int)(q: Q[Double,C]): Q[Double,C] =
    if (n == 0) ir"1.0" else ir"$q * ${power(n-1)(q)}"
  
  
  test("power(3)(42)") {
    
    val n = ir"42.0"
    
    val p3 = power(3)(n)
    
    assert(p3 =~= ir"$n * ($n * ($n * 1.0))")
    
    assert(p3.run == 42*42*42)
    
  }
  
  test("x => power(3)(x)") {
    
    val p3f = ir"(x: Double) => ${power(3)(ir"$$x:Double")}" // TODO look at what this generates...
    
    assert(p3f =~= ir"(x: Double) => x * (x * (x * 1.0))")
    assert(p3f =~= ir"(y: Double) => y * (y * (y * 1.0))")
    
    assert((p3f.run apply 2) == 8)
    
  }
  
  
}






















