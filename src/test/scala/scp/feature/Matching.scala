package scp
package feature

import utils.Debug._

class Matching extends MyFunSuite2 {
  import TestDSL2.Predef._
  
  test("Holes") {
    ir"0 -> 'ok" match {
      //case ir"0 -> $b" => fail // Warning:(11, 12) Type inferred for hole 'b' was Nothing. Ascribe the hole explicitly to remove this warning.
      case ir"0 -> ($b: Symbol)" => eqt(b, ir"'ok")
    }
  }
  
  test("Shadowing") {
    
    val q = ir"(x:Int) => (x:String) => x"
    q.erase match {
      case ir"(y:Int) => $b: $t" =>
        eqt(t.rep, typeRepOf[String => String])
        eqt(b: Q[_, {val y: Int}], ir"(z: String) => z")
    }
    
  }
  
  test("Type Ascription") {
    
    (ir"42": Q[_,_]) match {
      case ir"$x: Double" => ???
      case ir"$x: Int" =>
        assert(x === ir"42")
    }
    
    val n = ir"42"
    val m = ir"42:Int"
    assert(n =~= m)
    assert(ir"$n * $m" =~= ir"$m * $n")
    
    assert(ir"(x: Int) => x + $n" =~= ir"(x: Int) => x + $m")
    
  }
  
  test("Methods") {
    
    ir"42.toDouble" match {
      case ir"($x: Int).toDouble" =>
        assert(x =~= ir"42")
    }
    
    val t = ir"42.toDouble"
    val s = ir".5 * $t"
    
    s match {
      case ir"($a: Double) * ($b: Double)" =>
        assert(a =~= ir"0.5")
        assert(b =~= ir"42.toDouble")
    }
    
    ir" ??? " match { case ir" ??? " => }
    
  }
  
  test("Free Variables") {
    
    //assert(ir"$$x" =~= ir"$$x") // Warning:(66, 12) Type inferred for hole 'x' was Nothing. Ascribe the hole explicitly to remove this warning.
    assert(ir"$$x:Nothing" =~= ir"$$x:Nothing")
    assert(ir"$$x:Int" =~= ir"$$x:Int")
    assert(ir"($$x:Int)+1" =~= ir"($$x:Int)+1")
    
  }
  
  test("Construction Unquotes in Extractors") {
    
    val x = ir"42"
    ir"(42, 666)" match {
      case ir"($$x, 666)" => 
    }
    
    /*
    // Note: the following syntax is not special-cased (but could be):
    val xs = Seq(ir"1", ir"2")
    ir"List(1, 2)" match {
      case ir"($$xs*)" => 
    }
    */
  }
  
  
}














