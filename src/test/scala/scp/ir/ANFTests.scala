package scp
package ir

import ir2._

object ANFTests {
  
  object DSL extends ANF
  
}
class ANFTests extends MyFunSuite2(ANFTests.DSL) {
  import DSL.Predef._
  
  def bl(q: IR[_,_]) = q.rep.asInstanceOf[base.Block]
  
  val ri = ir"readInt"
  
  
  test("Variables") {
    
    val f = ir"(x: Int) => {var a = x+1; a + a}"
    
    // FIXME: printing of Var.$bang
    //println(f)
    
    ir"$f(42)" eqt
      ir"""{
        val x_0: scala.Int = (42:Int) + 1;
        var v_1: scala.Int = x_0;
        v_1 + v_1
      }"""
    
  }
  
  
  test("Term Composition") {
    
    val riri = ir"2 * ($ri + $ri)"
    riri eqt ir"val ri = $ri; val s = ri + ri; 2 * s"
    assert(bl(riri) // [ (#2 = scala.Predef.readInt()); (#7 = #2.+(#2)); (#8 = 2.*(#7)); #8 ]
      .effects.size == 3)
    
    val ascr = ir"$riri : Any"
    assert(bl(ascr) // [ (#2 = scala.Predef.readInt()); (#7 = #2.+(#2)); (#8 = 2.*(#7)); #8: Any ]
      .effects.size == 3)
    
  }
  
  
  test("Insertion Across Binder") {
    
    val a = ir"$ri; () => $ri"
    a eqt ir"val ri = $ri; () => ri"
    assert(bl(a).effects.size == 1)
    
    val b = ir"() => $ri"
    b eqt ir"() => $ri"
    assert(bl(b).effects.size == 0)
    
  }
  
  
  test("FV Substitution") {
    
    val open = ir"println($$x)"
    
    val a = ir"val x = 42; $open"
    a eqt ir"println(42)"
    assert(bl(a) // [ (#16 = scala.Predef.println(42)); #16 ]
      .effects.size == 1)
    
    val b = ir"val x = 42; $open -> $open"
    b eqt ir"println(42) -> println(42)"
    // TODO change this behavior: cache inserted terms with set of subs'd FVs
    assert(bl(b) // [ (#35 = scala.Predef.println(42)); (#36 = scala.Predef.ArrowAssoc[Unit](#35)); (#37 = scala.Predef.println(42)); (#38 = #36.->[Unit](#37)); #38 ]
      .effects.size == 4)
    
    
    val c = ir"() => ($$x: Unit)"
    assert(bl(c).effects.isEmpty)
    
    val d = c subs 'x -> ir"println(2.toDouble)"
    d eqt ir"() => println(2.toDouble)"
    d eqt ir"() => { val x = 2.toDouble; println(x) }"
    assert(bl(d).effects.isEmpty)
    
  }
  
  
  test("Correct Inlining") {
    
    ir"((x: Int) => x + x)(42)" eqt
      ir"val u = 42; u + u"
    
    ir"println( {(x: Int) => x + x} apply readInt )" eqt
      ir"val u = readInt; val p = u + u; println(p)"
    
    ir"((x: Int, y: Int) => x + y)(1,2)" transformWith (new ANFTests.DSL.SelfTransformer with BindingNormalizer) eqt
      ir"val u = 1; val v = 2; u + v"
    
    ir"val f = (x: Int) => x; f(42)" eqt ir"42"
    
  }
  
  
  test("Simple Term Equivalence") {
    
    val lss = ir"List(1,2,3).sum"
    
    lss eqt ir"List(1,2,3).sum"
    
    lss eqt ir"val a = 1; val ls = { val c = 3; List(a,{val tmp = 2; val b = tmp; b},c) }; val s = ls.sum; s"
    
  }
  
  
  test("Effectful Term Equivalence") {
    
    
    ir"println(42)" eqt ir"println(42)"
    
    //ir"println(42)" eqt ir"println(42); ()"  // TODO Unit normalization
    
    ir"println(42); println" eqt ir"println(42); println"
    
    // Don't ignore effects:
    eqt(ir"println(42); println", ir"println", false)
    eqt(ir"println; 42", ir"42", false)
    
    // Don't conflate distinct effects
    eqt(ir"println -> println", ir"val x = println; x -> x", false)
    
    // Don't gloss over closure scopes
    eqt(ir"() => {val x = readInt; x+x}", ir"val x = readInt; () => x+x", false)
    eqt(ir"val x = readInt; (y: Int) => x+y", ir"(y: Int) => {val x = readInt; x+y}", false)
    
    // Don't assimilate different values -- remember assignments
    eqt(ir"val a = readInt; val b = readDouble; a + b", ir"readInt + readDouble")
    eqt(ir"val a = readInt; val b = readInt; a + b", ir"val a = readInt; val b = a; a + b", false)
    
    // Don't gloss over eval order
    eqt(ir"val a = readInt; val b = readDouble; b + a", ir"readDouble + readInt", false)
    
    
  }
  
  
  test("Matching") {
    
    val x = ir"(x: Int) => println(x)"
    
    //base debugFor
    {x match {
      //case ir"(x: Int) => $bo" => // FIXME pack effects in holes
      //case ir"(x: Int) => $bo: $t" =>
      case ir"(x: Int) => { $ef; $bo }" =>
    }
    x match {
      case ir"(x: Int) => { $ef: Unit; $bo }" =>
        //println(ef rep)
        //println(bo rep)
    }}
    
    //base debugFor
    {ir"val x = readInt; x + x" matches {
      case ir"($a:Int) + ($b: Int)" => // FIXME
      case _ =>
    } and {
      //case ir"($a:Int) + a" => // FIXME type of repeated holes
      //case ir"($a:Int) + (a: Int)" => // FIXME
      case ir"$eff; ($a:Int) + (a: Int)" =>
      //case _ =>
    }}
    
  }
  
  
  test("Matching & Reconstructing") {
    
    val x = ir"val x = readInt; x + x"
    
    x match {
      case ir"$eff; ($a:Int) + ($b:Int)" => // Note: two different holes can extract the same Rep
        ir"$a + $b" eqt x  // Note that the effect is pulled in by the reps, but not duplicated!
        ir"$eff; $a + $b" eqt x 
    }
    
    x match {
      case ir"$eff; ($a:Int) + (a:Int)" =>
        ir"$a + $a" eqt x
    }
    
  }
  
  
  test("Extracted Holes in Effect Position") {
    
    val e = ir"$$x: Any; 42"
    assert((e extractRep ir"println; 42").get._1("x") =~= ir"println".rep)
    
    ir"println; 42" match {
      case ir"42" => fail
      case ir"$eff; 42" =>
        eff eqt ir"println"
    }
    
    ir"println; println; 42" matches {
        
      case ir"$effs; 42" => ??? // FIXME pack effects in effect holes
        effs eqt ir"println; println"
        
      //case ir"$effs: _*; 42" =>
      //case ir"${effs @ __*}; 42" =>
      //case ir"${effs:Seq[Any]}*; 42" =>
      case ir"($effs:Seq[Any]):_*; 42" => ??? // FIXME?
      
      case _ =>
      
    } and {
        
      case ir"$ef0; $ef1; 42" =>
        ef0 eqt ef1
        ef1 eqt ir"println"
        
    }
    
  }
  
  
}
  