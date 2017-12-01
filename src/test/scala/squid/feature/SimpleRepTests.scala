package squid
package feature

import utils._

class SimpleRepTests extends MyFunSuite {
  import TestDSL.Predef._
  import TestDSL.Quasicodes._
  import TestDSL.SimplePredef._
 
  def optim[A](pgrm: Rep[A]): Rep[A] = pgrm rewrite {
    case code"List[$t]($xs*).size" => Const(xs.size)
  }
  
  test("Simple Rep") {
    
    val t = optim(code{
      val lss = List(1,2,3).size
    })
    
    t eqt code{val lss_0 = 3}
    
  }
  
}