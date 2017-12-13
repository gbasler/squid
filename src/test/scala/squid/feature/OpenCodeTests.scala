package squid
package feature

import utils._

/** Demonstrating the use of OpenCode when we don't want to track context dependencies. */
class OpenCodeTests extends MyFunSuite {
  import TestDSL.Predef.{OpenCode => Rep, _}
  import TestDSL.Quasicodes._
  
  def optim[A](pgrm: Rep[A]): Rep[A] = pgrm rewrite {
    case code"List[$t]($xs*).size" => Const(xs.size)
  }
  
  test("Simple Rep") {
    
    val t = optim(code{
      val lss = List(1,2,3).size
      lss+1
    })
    
    t eqt code{val lss_x = 3; lss_x+1}
    
    assert(t.unsafe_asClosedCode.run == 4)
    
  }
  
}
