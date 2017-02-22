package squid
package anf

import ir._

object SimpleANFTests {
  
  object DSL extends SimpleANF
    // with StandardEffects  // commented because some tests currently rely on things like Int ops to be effectful...
  
  object DSLWithEffects extends SimpleANF with StandardEffects
  
}
/**
  * TODO test `rewriteRep` with real term rewriting tests!
  * 
  * FIXME either force trivial expr in ret or forbid it; currently both are allowed... (cf Term Composition)
  * FIXME ascribing a block should move to the return (cf Term Composition)
  * TODO don't rebind already bound (cf Block Inlining)
  * TODO equate unused effectful bound val and simple effect (cf Inlined Argument Eval Order)
  * TODO impl flexible matching, with effect stuffing in holes (cf Matching)
  * 
  */
class SimpleANFTests extends MyFunSuite(SimpleANFTests.DSL) {
  import DSL.Predef._
  
  //case class bl[A,B](ir: DSL.IR[A,B]) {
  //  val r: DSL.Rep = ir.rep
  def bl[A,B](ir: DSL.IR[A,B]): bl = bl(ir.rep)
  case class bl(r: DSL.Rep) {
    lazy val (effects,ret) = r.asBlock
    override def toString: String = s"($effects, $ret)"
  }
  
  val ri = ir"readInt"
  
  
  test("Variables") {
    
    val f = ir"(x: Int) => {var a = x+1; a + a}"
    
    ir"$f(42)" eqt
      ir"""{
        val x_0: scala.Int = (42:Int) + 1;
        var v_1: scala.Int = x_0;
        v_1 + v_1
      }"""

  }
  
  
  test("Imperative Flattening") {
    
    val model = ir"print(1); print(2); print(3); print(4); 42"
    
    model eqt ir"print(1); { print(2); print(3) }; print(4); 42"
    
    val mid = ir"print(2); print(3)"
    model eqt ir"print(1); {{ $mid; print(4) }; 42}"
    
  }
  
  
  test("Term Composition") {
    
    val riri = ir"2 * ($ri + $ri)"
    riri eqt ir"val ri0 = $ri; val ri1 = $ri; val s = ri0 + ri1; 2 * s"
    assert(bl(riri)
      .effects.size == 3)  // FIXME? ret not trivial
    //println(bl(riri).ret)
    
    val ascr = ir"$riri : Any"
    // FIXME
    //println(ascr rep)
    //assert(bl(ascr) // [ (#2 = scala.Predef.readInt()); (#7 = #2.+(#2)); (#8 = 2.*(#7)); #8: Any ]
    //  .effects.size == 3)
    //println(bl(ascr).ret.dfn.getClass)
    //println(bl(bl(ascr).ret))
    //println(bl(bl(bl(ascr).ret).ret))
    
    val incr = ir"$riri + 1"
    incr eqt ir"val riri = $riri; riri + 1"
    //incr eqt ir"val riri = $riri; val r = riri + 1; r" // FIXME trivial ret
    
  }
  
  
  // FIXME when xting a lambda body, we replace the param by a hole but by doing so the body block registers new effects (that used to be captured in enclosing scopes...)
  test("Insertion Across Binder") {
    
    val a = ir"() => $ri+1"
    
    a eqt a
    
    val aEq = ir"() => { val r = $ri; r+1 }"
    
    aEq eqt aEq // FIXME
    aEq eqt a
    a eqt aEq
    
    assert(bl(a).effects.isEmpty)
    
  }
  
  
  test("FV Substitution") {
    
    val open = ir"println($$x)"
    
    val a = ir"val x = 42; $open"
    a eqt ir"println(42)"
    assert(bl(a) // [ (#16 = scala.Predef.println(42)); #16 ]
      .effects.size == 0) // FIXME?
    
    val b = ir"val x = 42; $open -> $open"
    b eqt ir"println(42) -> println(42)"
    assert(bl(b) // [ (#35 = scala.Predef.println(42)); (#36 = scala.Predef.ArrowAssoc[Unit](#35)); (#37 = scala.Predef.println(42)); (#38 = #36.->[Unit](#37)); #38 ]
      .effects.size == 3) // FIXME?
    
    
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
    
    ir"((x: Int, y: Int) => x + y)(1,2)" //transformWith (new SimpleANFTests.DSL.SelfTransformer with BindingNormalizer) eqt
      ir"val u = 1; val v = 2; u + v"
    
    ir"val f = (x: Int) => x; f(42)" eqt ir"42"
    
  }
  
  
  test("Simple Term Equivalence") {
    
    val lss = ir"List(1,2,3).sum"
    
    lss eqt ir"List(1,2,3).sum"
    
    //lss eqt ir"val a = 1; val ls = { val c = 3; List(a,{val tmp = 2; val b = tmp; b},c) }; val s = ls.sum; s" // FIXME
    lss eqt ir"val a = 1; val ls = { val c = 3; List(a,{val tmp = 2; val b = tmp; b},c) }; ls.sum"
    
  }
  
  
  test("Block Equivalence") {
    
    val rd = ir"() => readDouble"
    ir"$rd" eqt ir"$rd"
    ir"($rd)()" eqt ir"($rd)()"
    ir"($rd)() * ($rd)()" eqt ir"($rd)() * ($rd)()"
    ir"(() => 42)() * (() => 42)()" eqt ir"val f = () => 42; f() * f()"
    
  }
  
  
  test("Block Inlining") {
    
    val f = ir"() => readDouble"
    
    ir"val f = $f; (f, f)" eqt
      ir"(() => readDouble, () => readDouble)"
    
    ir"($f)() * ($f)()" eqt
      ir"val f = $f; f() * f()"
    
    ir"(() => readDouble)() * (() => readDouble)()" eqt
      ir"val f = $f; f() * f()"
    
    val fufu = ir"val f = (x:Unit) => readDouble; f(Unit) * f(Unit)"
    fufu eqt ir"readDouble * readDouble"
    fufu neqt ir"val r = readDouble; val f = (x:Unit) => r; f(Unit) * f(Unit)"
    
    {
      val a = ir"val f = (x:Unit) => readInt; f(Unit) * f(Unit)"
      val b = ir"val a = readInt; val b = readInt; a * b"
      a eqt b
    }
    
    // TODO implement alpha renaming when binding a BoundVal that is already bound, as in on second inlining:
    /*
    {
      val a = ir"val f = (x:Unit) => readInt+1; f(Unit) * f(Unit)"
      val b = ir"val a = readInt+1; val b = readInt+1; a * b"
      a dbg_eqt b
    }
    
    System.exit(0)
    
    ir"val r = readInt; val f = (x:Unit) => r + readInt; f(Unit) * f(Unit)" dbg_eqt
      ir"val r = readInt; val a = readInt; val x = r + a; val b = readInt; val y = r + b; x * y"
    
    //val x = ir"((x: Int) => { val r0 = readInt; (y: Unit) => r0 + readInt + x })(42)(Unit)" // FIXME
    //println(x)
    */
    
  }
  
  
  test("Inlined Argument Eval Order") {
    
    ir"((x: Int) => println(x))(readInt)" eqt
      ir"val x = readInt; println(x)"
    
    ir"((_: Int) => println)(readInt)" eqt ir"val _ = readInt; println"
    //ir"((_: Int) => println)(readInt)" eqt ir"readInt; println" // FIXME
    
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
    
    x match {
      case ir"(x: Int) => $bo" =>
        bo eqt ir"println($$x: Int)"
    }
    x match {
      case ir"($x: Int) => $bo" =>
        bo eqt ir"println($$x: Int)"
        bo match { case ir"println($$x)" => }
    }
    
    // We don't support multi-effect-holes matching (yet?)
    /*x match {
      case ir"(x: Int) => { $ef; $bo }" =>
      case ir"(x: Int) => { $ef: Unit; $bo }" =>
        println(ef rep)
        println(bo rep)
    }*/
    
    val init = ir"val x = readInt; x + x"
    
    //val xtor = ir"$$eff; ($$a:Int) + ($$a: Int)"
    ////val xtor = ir"$$eff; (readInt) + (readInt)"
    //base.debugFor(xtor extractRep init)
    
    // TODO
    /*
    init matches {
      case ir"($a:Int) + ($b: Int)" => // TODOmaybe? pack effects in non-effect holes?!
      case _ =>
    } and {
      //case ir"($a:Int) + a" => // TODO propagate type of repeated holes
      //case ir"($a:Int) + (a: Int)" => // pack?
      case ir"$eff; ($a:Int) + (a: Int)" =>
        eff eqt ir"readInt"
        //println(a rep)
        //println(eff rep)
        a eqt eff
        ir"$a + $a" eqt init
    } and {
      case ir"$eff: Int; ($a:Int) + (a: Int)" =>
        ir"$eff + $eff" eqt init
        ir"$a + $eff" eqt init
    }
    */

  }
  
  
  test("Matching & Reconstructing") {
    // TODO
    /*
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
    */
  }
  
  
  test("Extracted Holes in Effect Position") {
    // TODO
    /*
    
    val e = ir"$$x: Any; 42"
    assert((e extractRep ir"println; 42").get._1("x") =~= ir"println".rep)
    
    ir"println; 42" match {
      case ir"42" => fail
      case ir"$eff; 42" =>
        eff eqt ir"println"
    }
    
    ir"println; println; 42" matches {
      
      case ir"$effs; 42" => // We can pack effects in one effects hole
        effs eqt ir"println; println" 
        
    } and {
      //case ir"$effs: _*; 42" =>
      //case ir"${effs @ __*}; 42" =>
      //case ir"${effs:Seq[Any]}*; 42" =>
      case ir"($effs:Seq[Any]):_*; 42" => ??? // FIXME?
        
      case _ =>
        
    } /*and {  // Not supported (yet?)
      
      case ir"$ef0; $ef1; 42" =>
        ef0 eqt ef1
        ef1 eqt ir"println"
        
    }*/
    
    */
    
  }
  
  
  test("By-Name Behavior of Boolean && ||") {
    
    val c0 = ir"true && {println;true}"
    assert(c0.rep.asBlock._1.isEmpty)
    
    c0 match {
      case ir"($a:Boolean) && $b" =>
        a eqt ir"true"
        b eqt ir"println; true"
    }
    
    val c1 = ir"true || {println;true}"
    assert(c1.rep.asBlock._1.isEmpty)
    
    c1 match {
      case ir"($a:Boolean) && $b" => fail
      case ir"($a:Boolean) || $b" =>
        a eqt ir"true"
        b eqt ir"println; true"
    }
    
  }
  
  
  
  
}
