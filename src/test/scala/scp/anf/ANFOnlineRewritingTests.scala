package scp
package ir

// TODO port for SimpleANF

/*

import utils._
import ir2._

/*

  //object DSL extends ANF with OnlineOptimizer with BindingNormalizer // FIXME dups the while
  //object DSL extends ANF with OnlineOptimizer with BindingNormalizer with TopDownTransformer // FIXME dups the while

*/

class ANFOnlineRewritingTests extends MyFunSuite2(ANFRewritingTests.OnlineOptDSL) {
  import DSL.Predef._
  
  test("Rewrites Result") {
    val x =
      //base.TranformerDebug debugFor 
      ir"readInt:Double"
    //println(x)
    x eqt ir"readDouble"
    
    val y = ir"val n: Double = readInt; println(n); n: Any";
    y eqt ir"val n = readDouble; println(n); n: Any"
    
  }
  
  test("Rewrites Effect") {
    
    val x = ir"val n = readInt:Double; println(n); n+42"
    x eqt ir"val r = readDouble; println(r); r + 42"
    
  }
  
  test("Rewrites Combinations") {
    
    //val x = base.TranformerDebug debugFor ir"42 + Math.pow(readInt, 2)"
    //val x = base.ANFDebug debugFor ir"42 + Math.pow(readInt, 2)"
    val x = ir"42 + Math.pow(readInt, 2)"
    //println(x)
    //println(x rep)
    x eqt ir"val r = readDouble; 42 + r * r" // FIXedME!
    
  }
  
  test("Rewrites ?") {
    
    ///_*_
    //TODO test
    //val x = ir"val r = readInt.toDouble; () => r+1" // ok
    //val x = ir"val r = readInt.toDouble; ((x:Unit) => r+1)(())" // ok
    
    val x = ir"Math.pow(((x:Unit) => readInt.toDouble)(Unit), 2)" // FIXME
    //val x = ir"((x:Unit) => readInt.toDouble)(Unit)" // FIXME
    //val x = DSL.TranformerDebug debugFor dbg_ir"((x:Unit) => readInt.toDouble)(Unit)" // FIXME why is it transforming `readInt.toDouble` twice??
//val x = DSL.TranformerDebug debugFor {
//  val __b__ : ANFOnlineRewritingTests.this.DSL.Predef.base.type = ANFOnlineRewritingTests.this.DSL.Predef.base;
//  ANFOnlineRewritingTests.this.DSL.Predef.base.`internal IR`[Double, Any](__b__.wrapConstruct({
//    val _0_DoubleSym = __b__.loadTypSymbol("scala.Double");
//    val _1_Double = __b__.staticTypeApp(_0_DoubleSym, scala.Nil);
//    val _2_UnitSym = __b__.loadTypSymbol("scala.Unit");
//    val _3_Unit = __b__.staticTypeApp(_2_UnitSym, scala.Nil);
//    val _4_IntSym = __b__.loadTypSymbol("scala.Int");
//    val _5_Int = __b__.staticTypeApp(_4_IntSym, scala.Nil);
//    val _6_Predef = __b__.staticModule("scala.Predef");
//    val _7_Predef_Sym = __b__.loadTypSymbol("scala.Predef$");
//    val _8_readInt = __b__.loadMtdSymbol(_7_Predef_Sym, "readInt", scala.None, false);
//    val _9_toDouble = __b__.loadMtdSymbol(_4_IntSym, "toDouble", scala.None, false);
//    val _10_Unit = __b__.staticModule("scala.Unit");
//    val _11_package_Sym = __b__.loadTypSymbol("scp.lib.package$");
//    val _12_Imperative = __b__.loadMtdSymbol(_11_package_Sym, "Imperative", scala.None, false);
//    val _13_package = __b__.staticModule("scp.lib.package");
//    val _14_Function1Sym = __b__.loadTypSymbol("scala.Function1");
//    val _15_apply = __b__.loadMtdSymbol(_14_Function1Sym, "apply", scala.None, false);
//    __b__.mapp({
//      val _$x = __b__.bindVal("x", _3_Unit, scala.Nil);
//      __b__.lambda(scala.List(_$x),
//        
//        println("Mking body") before
//        __b__.mapp(__b__.mapp(_6_Predef, _8_readInt, _5_Int)()(__b__.Args()), _9_toDouble, _1_Double)()() and (b => println("Made! "+b))
//      )
//    }, _15_apply, _1_Double)()(__b__.Args(__b__.mapp(_13_package, _12_Imperative, _3_Unit)(_3_Unit)(__b__.ArgsVarargs(__b__.Args(), __b__.Args(_10_Unit)), __b__.Args(__b__.const(())))))
//  }))
//}
    
    //val x = ir"Math.pow(((x:Unit) => readDouble)(Unit), 2)" // ok
    //println(x rep)
    //System exit 0
    
    x eqt ir"val a = readDouble; a * a"
    //_*_/
    
    val y =
      base.TranformerDebug debugFor 
      ir"(x:Unit) => Math.pow(readInt.toDouble, 2)"
    println(y) // TODO we have to online-process blocks too!!
    
    y eqt ir"(x:Unit) => { val r = readDouble; r * r }"
    
    
    // TODO test rewrite on insertion with capture
    
  }
  
}

class ANFOnlineNormRewritingTests extends MyFunSuite2(ANFRewritingTests.OnlineOptDSLNorm) {
  import DSL.Predef._
  
  test("Rewrites Combinations") {
    
    val x = ir"42 + Math.pow(readInt, 2)"
    //println(x)
    x eqt ir"val r = readDouble; 42 + r * r"
    
  }
  
  //test("Normalizes Uncurried Function") {
  test("Normalizes Curried Function") {
    
    val x = ir"((x: Int) => (y: Int) => x + y)(1)(2)"
    x eqt ir"(1:Int) + 2"
    
    val y = ir"((x: Int) => (y: Int) => (z: Int) => x + y - z)(1)(2)(3)"
    y eqt ir"(1:Int) + 2 - 3"
    
  }
  test("Normalizes Uncurried Function") {
    // TODO test:
    
    // works:
    //println(ir"((x: Int, y: Int) => x + y)(1, 2)")
    
    // works
    //println(ir"val f = ((x: Int, y: Int) => x + y); f(1, 2)")
    
    //println(ir"() => { val c = ((x: Int, y: Int) => x + y)(1, 2); c }")
    //println(ir"() => { ((x: Int, y: Int) => x + y)(1, 2) }")
    println(ir"(x:Unit) => { ((x: Int, y: Int) => x + y)(1, 2) }")
    
    // works but keeps the uncurried.apply!:
    // doesn't work anymore
    //println(ir"while(true) { val c = ((x: Int, y: Int) => x + y)(1, 2); c }")
    
    //println(base.BN.TranformerDebug debugFor ir"while(true) { var c = ((x: Int, y: Int) => x + y)(1, 2); c }")
    
    println(ir"scp.lib.uncurried2((acc_7: Unit) => (x_8: Int) => { (4, 5) }).apply(Unit,3)")
    
  }
    
  
}
*/
