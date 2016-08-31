package scp
package feature

class VirtualizedConstructs extends MyFunSuite2 {
  import TestDSL2.Predef._
  
  test("If-Then-Else") {
    
    val ite = ir"if (1 > 2) 666 else 42"
    
    ite match {
      case ir"if ($c) $t else $e" => // scrutinee type Int is propagated to the branch holes 
    }
    
    ite matches { // scrutinee type erased by `matches`
      
      //case ir"if ($c) $t else $e" => fail // infers Nothing for the return type... generates a warning
      //case ir"if ($c) $t: Nothing else $e: Nothing" => fail // still generates an annoying warning (because it's desugared to IfThenElse[Nothing](...)) ... so I commented
      
      //case ir"scp.lib.IfThenElse[Nothing]($c, $t, $e)" => fail // Warning:(14, 12) Type inferred for hole 't' was Nothing. Ascribe the hole explicitly to remove this warning.
      case ir"scp.lib.IfThenElse[Nothing]($c, $t:Nothing, $e:Nothing)" => fail
        
      case ir"if ($c) $t else $e: Int" =>
        c eqt ir"1 > 2"
        t eqt ir"666"
        e eqt ir"42"
    } and {
      case ir"if ($c) $t else $e: $tp" => eqt(tp.rep, typeRepOf[Int])
    }
    
    assert(ite.run == 42)
    
  }
  
  test("While") {
    
    ir"while (readInt > 0) println('ok)" match {
      case ir"while ($cond) $loop" =>
        cond eqt ir"readInt > 0"
        loop eqt ir"println('ok)"
    }
    
    import lib.While
    eqt(ir"while(true) ()",
        ir"While(true, ())")
    eqt(ir"while(true) readInt",
        ir"While(true, readInt)")
    eqt(ir"(while(true) readInt): Unit",
        ir"While(true,  readInt): Unit")
    eqt(ir"(while(true) (readInt: Unit)): Unit",
        ir"While(true,   readInt: Unit):  Unit")
    
    ir"while (true) readInt" match {
      case ir"while (true) $x" =>
        x eqt ir"readInt; ()"
        x eqt ir"readInt: Unit"
    }
    
  }
  
  test("Imperative") {
    //import VirtualizedConstructs._  // FIXME class loading
    import Dummies.VirtualizedConstructs._
    
    setEv(0)
    
    val impure = ir"setEv(getEv+1); setEv(getEv+1); getEv"
    
    impure match {
      case ir"setEv(getEv+(${Const(n)}:Int)); setEv(getEv+1); getEv" => assert(n == 1)
    }
    impure matches {
    // Oddity: with 'matches', the 'ConstQ' extraction fails with: Error:(48, 31) No TypeTag available for A
    //case ir"setEv(getEv+(${ConstQ(n:Int)}:Int)); getEv" => assert(n == 1) // fixme ?!
      case ir"setEv(getEv+1); setEv(getEv+1); getEv" =>
    } and {
      case ir"lib.Imperative(setEv(getEv+1), setEv(getEv+1))(getEv)" =>
    }
    
    same(impure.run, 2)
    same(getEv, 2)
    
    same(ir"ev = 0; ev".run, 0)
    same(getEv, 0)
    
    
    // Support for translation done by Scala:  nonUnitValue ~> { nonUnitValue; () }
    ir"getEv; getEv; getEv" matches {
      case ir"lib.Imperative(getEv,getEv)(getEv)" =>
    }
    
    // Just checking virtualization also works here:
    eqt( ir"val x = {println; 42}; x",  ir"val x = lib.Imperative(println)(42); x" )
    
    val q = ir"readInt; readInt"
    q match { case ir"$eff; readInt" => eff eqt ir"readInt" }
    
  }
  
  test("Variables") {
    
    eqt( ir"var x = 0; x = 1; x",  ir"val x = lib.Var(0); x := 1; x!" )

    eqt( ir"var x = 0; x += 1",  ir"val x = lib.Var(0); x := x.! + 1" )
    
    same(ir"val lol = 42; var v = lol-1; v += 1; v.toDouble".run, 42.0)
    
  }
  
}
object VirtualizedConstructs {
  var ev = 0
  def setEv(value: Int) = ev = value
  def getEv = ev
}










