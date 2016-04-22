package scp
package feature

class VirtualizedConstructs extends MyFunSuite {
  import TestDSL._
  
  test("If-Then-Else") {
    
    val ite = dsl"if (1 > 2) 666 else 42"
    
    ite matches {
      case dsl"if ($c) $t else $e" => fail // infers Nothing for the return type...
      case dsl"if ($c) $t else $e: Int" =>
        c eqt dsl"1 > 2"
        t eqt dsl"666"
        e eqt dsl"42"
    } and {
      case dsl"if ($c) $t else $e: $tp" => eqt(tp.rep, typeRepOf[Int])
    }
    
    assert(ite.run == 42)
    
  }
  
  test("While") {
    
    dsl"while (readInt > 0) println('ok)" match {
      case dsl"while ($cond) $loop" =>
        cond eqt dsl"readInt > 0"
        loop eqt dsl"println('ok)"
    }
    
  }
  
  
}



