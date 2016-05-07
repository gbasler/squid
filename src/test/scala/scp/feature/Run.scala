package scp
package feature

import org.scalatest.FunSuite
import org.scalatest.ShouldMatchers

class Run extends FunSuite with ShouldMatchers {
  
  import TestDSL._
  
  test("New") {
    import BasicEmbedding._
    val mc = dsl"new MC(42)('ok, 'ko)"
    assert(mc.run == new MC(42)('ok, 'ko))
  }
  
  test("Functions") {
    
    assert(dsl"((x: Int) => x + 1)(42)".run == 43)
    
    val f = dsl"(x: Int) => x+1"
    
    assert((f.run apply 42) == 43)
    
    assert(dsl"Run.f(42)".run == 43)
    
  }
  
  test("Compile Error On Open Terms") {
    
    """ dsl"($$x: Int) + 1".run """ shouldNot compile
    
    val x = dsl"42": Q[Int, {val x: Int}]
    """ x.run """ shouldNot compile
    
  }
  
  test("n until m") {
    
    val t = dsl"0 until 42"
    
    assert(t.run == (0 until 42))
    
  }
  
  test("Constant Options, Seqs, Sets") {
    assert(dsl"${Seq(Seq(1,2,3))}".run == Seq(Seq(1,2,3)), None)
    assert(dsl"${Seq(Some(Set(1,2,3)), None)}".run == Seq(Some(Set(1,2,3)), None))
  }
  
  test("Array and ClassTag") {
    assert(dsl"""Array.fill(3)("woof").toSeq""".run == Array.fill(3)("woof").toSeq)
  }
  
  
}
object Run {
  
  val f = (x: Int) => x + 1
  
}







