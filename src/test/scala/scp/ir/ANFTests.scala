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
  
  
  test("Imperative Removal") {
    
    
    println(ir"print(1); print(2); print(3)" rep)
    
    println(ir"print(1); { print(2); print(3) }; print(4); 42" rep) // FIXME eval order
    
    val mid = ir"print(2); print(3)"
    println(ir"print(1); $mid; print(4); 42" rep)
    
  }
  
  
  test("Term Composition") {
    
    val riri = ir"2 * ($ri + $ri)"
    riri eqt ir"val ri = $ri; val s = ri + ri; 2 * s"
    println(riri rep)
    assert(bl(riri) // [ (#2 = scala.Predef.readInt()); (#7 = #2.+(#2)); (#8 = 2.*(#7)); #8 ]
      .effects.size == 3)
    
    val ascr = ir"$riri : Any"
    println(ascr rep)
    assert(bl(ascr) // [ (#2 = scala.Predef.readInt()); (#7 = #2.+(#2)); (#8 = 2.*(#7)); #8: Any ]
      .effects.size == 3)
    
  }
  
  
  // FIXME when xting a lambda body, we replace the param by a hole but by doing so the body block registers new effects (that used to be captured in enclosing scopes...)
  test("Insertion Across Binder") {
    
    val a = ir"$ri; () => $ri"
    /*
    val a = {
  val __b__ : ANFTests.this.DSL.Predef.base.type = ANFTests.this.DSL.Predef.base;
  ANFTests.this.DSL.Predef.base.`internal IR`[() => Int, Any](__b__.wrapConstruct({
    val _0_package_Sym = __b__.loadTypSymbol("scp.lib.package$");
    val _1_Imperative = __b__.loadMtdSymbol(_0_package_Sym, "Imperative", scala.None, false);
    val _2_Function0Sym = __b__.loadTypSymbol("scala.Function0");
    val _3_IntSym = __b__.loadTypSymbol("scala.Int");
    val _4_Int = __b__.staticTypeApp(_3_IntSym, scala.Nil);
    val _5_Function0 = __b__.staticTypeApp(_2_Function0Sym, scala.List(_4_Int));
    val _6_package = __b__.staticModule("scp.lib.package");
    __b__.mapp(_6_package, _1_Imperative, _5_Function0)(_5_Function0)(__b__.ArgsVarargs(__b__.Args(), 
      __b__.Args(__b__.substitute(ANFTests.this.ri.rep))), 
      __b__.Args(__b__.lambda(scala.Nil, __b__.substitute(ANFTests.this.ri.rep))))
  }))
}
    */
    
    a eqt a
    
    val aEq = ir"val ri = $ri; () => ri"
    
    //println(a rep)
    //println(aEq rep)
    
    aEq eqt aEq // FIXME
    //aEq dbg_eqt aEq // FIXME
    
    /* Problems: terms eq adds spurious effects + `a` inner body has effects registered too early... */
    
    //a eqt ir"val ri = $ri; () => ri" // FIXME
    //a dbg_eqt ir"val ri = $ri; () => ri" // FIXME
    a eqt aEq
    
    //System exit 0
    
    assert(bl(a).effects.size == 1)
    
    val b = ir"() => $ri"
    b eqt ir"() => $ri"
    assert(bl(b).effects.size == 0)
    
    // TODO w/ var capture
    
  }
  
  
  test("FV Substitution") {
    
    val open = ir"println($$x)"
    
    val a = ir"val x = 42; $open"
    a eqt ir"println(42)"
    assert(bl(a) // [ (#16 = scala.Predef.println(42)); #16 ]
      .effects.size == 1)
    
    val b = ir"val x = 42; $open -> $open"
    println(b rep)
    println(ir"println(42) -> println(42)" rep)
    //System exit 0
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
  
  
  test("Block Equivalence") {
    
    ir"() => readDouble" eqt ir"() => readDouble"
    ir"(() => readDouble)()" eqt ir"(() => readDouble)()"
    ir"(() => readDouble)() * (() => readDouble)()" eqt ir"(() => readDouble)() * (() => readDouble)()"
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
    
    ir"val r = readInt; val f = (x:Unit) => r + readInt; f(Unit) * f(Unit)" eqt
      ir"val r = readInt; val a = readInt; val x = r + a; val b = readInt; val y = r + b; x * y"
    
    
    //val x = ir"((x: Int) => { val r0 = readInt; (y: Unit) => r0 + readInt + x })(42)(Unit)" // FIXME
    //println(x)
    
  }
  
  
  test("Inlined Argument Eval ORder") {
    
    ir"((x: Int) => println(x))(readInt)" eqt
      ir"val x = readInt; println(x)"
    
    ir"((_: Int) => println)(readInt)" eqt
      ir"readInt; println"
    
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
    println(ir"val a = readInt; val b = readDouble; a + b" rep)
    println(ir"readInt + readDouble" rep)
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
    
    ///*
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
    //*/
    
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
    
  }
  
  
}
  