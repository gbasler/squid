package squid
package anf

import ir._

/**
  * Created by lptk on 02/02/17.
  */
class RewritingTests extends MyFunSuite(SimpleANFTests.DSL) {
  import DSL.Predef._
  
  test("Rewriting simple expressions only once") {
    
    val a = ir"println((50,60))"
    val b = a rewrite {
      case ir"($x:Int,$y:Int)" =>
        ir"($y:Int,$x:Int)"
      case ir"(${Const(n)}:Int)" => Const(n+1)
    }
    b eqt ir"println((61,51))"
    
  }
  
  test("Rewriting Sequences of Bindings") {
    
    val a = ir"val aaa = readInt; val bbb = readDouble.toInt; aaa+bbb"  // FIXMElater: why isn't name 'bbb' conserved?
    val b = a rewrite {
      case ir"readDouble.toInt" =>
        ir"readInt"
    }
    b eqt ir"val aa = readInt; val bb = readInt; aa+bb"
    
  }
  
  test("Rewriting Sequences of Effects") { // FIXME
    
    val c0 = ir"print(1); print(2); print(3); print(4)"
    println(c0 rewrite {
      case ir"print(1); print(2)" => ir"print(12)"
    })
    println(c0 rewrite {
      case ir"print(2); print(3)" => ir"print(23)"
    })
    println(c0 rewrite {
      case ir"print(3); print(4)" => ir"print(34)"
    })
    
  }
  
  test("Rewriting Trivial Arguments") {
    
    val a = ir"val a = 0.toDouble; val b = 0.toDouble; val c = 0.toDouble; (a,b,c)"
    val b = a rewrite {
      case ir"(${Const(n)}:Int).toDouble" => ir"${Const(n+1)}.toDouble"
    }
    b eqt ir"val a = 1.toDouble; val b = 1.toDouble; val c = 1.toDouble; (a,b,c)"
    
  }
  
  
  
}
