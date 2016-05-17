package scp
package feature

class VirtualizedConstructs extends MyFunSuite {
  import TestDSL._
  
  test("If-Then-Else") {
    
    val ite = dsl"if (1 > 2) 666 else 42"
    
    ite matches {
      //case dsl"if ($c) $t else $e" => fail // infers Nothing for the return type... generates a warning
      //case dsl"if ($c) $t: Nothing else $e: Nothing" => fail // still generates an annoying warning (because it's desugared to IfThenElse[Nothing](...)) ... so I commented
      
      //case dsl"scp.lib.IfThenElse[Nothing]($c, $t, $e)" => fail // Warning:(14, 12) Type inferred for hole 't' was Nothing. Ascribe the hole explicitly to remove this warning.
      case dsl"scp.lib.IfThenElse[Nothing]($c, $t:Nothing, $e:Nothing)" => fail
        
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
  
  test("Imperative") {
    import VirtualizedConstructs._
    
    setEv(0)
    
    val impure = dsl"setEv(getEv+1); setEv(getEv+1); getEv"
    
    impure match {
      case dsl"setEv(getEv+(${Constant(n)}:Int)); setEv(getEv+1); getEv" => assert(n == 1)
    }
    impure matches {
    // Oddity: with 'matches', the 'ConstQ' extraction fails with: Error:(48, 31) No TypeTag available for A
    //case dsl"setEv(getEv+(${ConstQ(n:Int)}:Int)); getEv" => assert(n == 1) // fixme ?!
      case dsl"setEv(getEv+1); setEv(getEv+1); getEv" =>
    } and {
      case dsl"lib.Imperative(setEv(getEv+1), setEv(getEv+1))(getEv)" =>
    }
    
    same(impure.run, 2)
    same(getEv, 2)
    
    same(dsl"ev = 0; ev".run, 0)
    same(getEv, 0)
    
    
    // Support for translation done by Scala:  nonUnitValue ~> { nonUnitValue; () }
    dsl"getEv; getEv; getEv" matches {
      case dsl"lib.Imperative(getEv,getEv)(getEv)" =>
    }
    
  }
  
  
}
object VirtualizedConstructs {
  var ev = 0
  def setEv(value: Int) = ev = value
  def getEv = ev
}










