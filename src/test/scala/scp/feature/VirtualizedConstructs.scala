package scp
package feature

class VirtualizedConstructs extends MyFunSuite {
  import TestDSL._
  
  test("If-Then-Else") {
    
    val ite = dsl"if (1 > 2) 666 else 42"
    
    ite matches {
      //case dsl"if ($c) $t else $e" => fail // infers Nothing for the return type... generates a warning
      //case dsl"if ($c) $t: Nothing else $e: Nothing" => fail // still generates an annoying warning (because it's desugared to IfThenElse[Nothing](...)) ... so I commented
      case dsl"scp.lib.IfThenElse[Nothing]($c, $t, $e)" => fail
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



