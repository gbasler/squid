package scp
package feature

import org.scalatest.FunSuite

class Embedding extends FunSuite {
  import Embedding._
  
  import TestDSL._
  
  test("Curried Functions") {
    
    val x = dsl"""foo(42)("ok")"""
    
    //x match { case dsl"($f: Int => String)($s)" => println(f) } // nope (normal)
    
    x match {
      case dsl"foo($n)($s)" =>
        assert(n =~= dsl"42")
        assert(s =~= dsl""" "ok" """)
    }
    
  }
  
}
object Embedding {
  
  def foo(n: Int)(s: String) = s * n
  
}






