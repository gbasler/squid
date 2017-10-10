package squid
package feature

import utils._

class HigherOrderPatternVariables extends MyFunSuite {
  import TestDSL.Predef._
  
  test("Matching lambda bodies") {
    
    val id = code"(z:Int) => z"
    
    code"(a: Int) => a + 1" matches {
      case code"(x: Int) => $body(x):Int" =>
        body eqt code"(_:Int)+1"
    } and {
      case code"(x: Int) => $body(x):$t" =>
        body eqt code"(_:Int)+1"
        eqt(t, codeTypeOf[Int])
    } and {
      case code"(x: Int) => ($exp(x):Int)+1" =>
        exp eqt id
    }
    
    code"(a: Int, b: Int) => a + 1" matches {
      case code"(x: Int, y: Int) => $body(y):Int" => fail
      case code"(x: Int, y: Int) => $body(x):Int" =>
    } and {
      case code"(x: Int, y: Int) => $body(x,y):Int" =>
    }
    
    code"(a: Int, b: Int) => a + b" matches {
      case code"(x: Int, y: Int) => $body(x):Int" => fail
      case code"(x: Int, y: Int) => $body(x,y):Int" =>
        body eqt code"(_:Int)+(_:Int)"
    } and {
      case code"(x: Int, y: Int) => ($lhs(y):Int)+($rhs(y):Int)" => fail
      case code"(x: Int, y: Int) => ($lhs(x):Int)+($rhs(y):Int)" =>
        lhs eqt id
        rhs eqt id
    }
    
  }
  
  test("Matching let-binding bodies") {
    
    code"val a = 0; val b = 1; a + b" matches {
      case code"val x: Int = $v; $body(x):Int" =>
        v eqt code"0"
        body matches {
          case code"(y:Int) => { val x: Int = $w; $body(x,y):Int }" =>
            w eqt code"1"
            body eqt code"(u:Int,v:Int) => (v:Int)+(u:Int)"
        }
    }
    
  }
  
}
