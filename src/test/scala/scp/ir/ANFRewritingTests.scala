package scp
package ir

import ir2._

object ANFRewritingTests {
  
  trait Rewritings extends SimpleRuleBasedTransformer {
    import base.Predef._
    rewrite {
      case ir"Math.pow($x,2)" => ir"$x * $x"
      case ir"readDouble.toInt" => ir"readInt"
      case ir"readInt.toDouble" => ir"readDouble"
    }
  }
  
  object DSL extends ANF {
    val BN = new SelfTransformer with BindingNormalizer with TopDownTransformer
  }
  
  object OnlineOptDSL extends ANF with OnlineOptimizer with Rewritings
  
}

/** TODO test online optims */
class ANFRewritingTests extends MyFunSuite2(ANFRewritingTests.DSL) {
  import DSL.Predef._
  import Math.pow
  
  def bl(q: IR[_,_]) = q.rep.asInstanceOf[base.Block]
  
  object Trans extends DSL.SelfTransformer with ANFRewritingTests.Rewritings with TopDownTransformer
  object FixPointTrans extends DSL.SelfTransformer with ANFRewritingTests.Rewritings with TopDownTransformer with FixPointRuleBasedTransformer
  Trans
  
  test("Rewrites Result") {
    
    var x = ir"readInt:Double"
    x = x transformWith Trans
    x eqt ir"readDouble"
    
    var y = ir"val n: Double = readInt; println(n); n: Any"
    y = y transformWith Trans
    y eqt ir"val n = readDouble; println(n); n: Any"
    
  }
  test("Rewrites With Non-Trivial Subexpression") {
    
    var x = ir"Math.pow(readDouble,2)+1"
    x = x transformWith Trans
    x eqt ir"((d: Double) => d*d)(readDouble)+1"
    
  }
  test("Rewrites Effects") {
    var x = ir"val n = readInt:Double; println(n); n+42"
    x = x transformWith Trans
    x eqt ir"val r = readDouble; println(r); r + 42"
  }
  
  test("Binding Rewriting") {
    var x = ir"val r: Double = readInt; 42 + r * r * r"  // Note: Scala converts `readInt:Double` to `readDouble`
    x = x transformWith Trans
    x eqt ir"val r = readDouble; 42 + r * r * r"
    x neqt ir"readInt; val r = readDouble; r * r * r"
  }
  
  test("Effects") {
    
    var x = ir"pow(.5, 2)"
    x = x transformWith Trans
    x eqt ir"(.5:Double) * .5"
    
    x = ir"42 + pow(.5, 2)"
    x = x transformWith Trans
    x eqt ir"42 + ((.5:Double) * .5)"
    
    x = ir"42 + pow(readInt, 2)"
    x = x transformWith Trans
    assert(bl(x) // [ #67 = scala.Predef.readDouble(); #69 = java.lang.pow(#67,2.0); #70 = 42.+(#69); #70 ]
      .effects.size == 3)
    x eqt ir"val r = readDouble; 42 + r * r"
    /* ^ Interestingly, does not go through the phase: */
    //x eqt ir"val r = readDouble; 42 + pow(r, 2)"
    
    val nested = ir"42 + pow(pow(readInt, 2), 2)"
    
    x = nested
    x = x transformWith Trans
    x eqt ir"42 + { val r = readDouble; val r2 = r * r; pow(r2, 2) }"
    x = x transformWith Trans
    x eqt ir"42 + { val r = readDouble; val r2 = r * r; r2 * r2 }"
    
    x = nested
    x = x transformWith FixPointTrans
    x eqt ir"42 + { val r = readDouble; val r2 = r * r; r2 * r2 }"
    
  }
  
  test("Prevents Removal of Still Used Effects") {
    val init = ir"val n = readInt; val a = n.toDouble; val b = n+1; a + b"
    var x = init
    x = x transformWith Trans
    x eqt init
  }
  
  
  test("Normalizes Uncurried Function") {
    val fun = ir"(x: Int, y: Int) => x + y"
    val res = ir"(1:Int) + 2"
    
    var x = ir"$fun(1, 2)"
    x = x transformWith DSL.BN
    x eqt res
    
    x = ir"if(true) 42 else $fun(1, 2)"
    x = x transformWith DSL.BN
    x eqt ir"if(true) 42 else $res"
    
    var y = ir"while(true) { val c = $fun(1, 2); c }"
    y = y transformWith DSL.BN
    y eqt ir"while(true) { (1:Int) + 2 }"
    //y eqt ir"while(true) { $res }" // FIXME: insertion of () does not trigger here
    
    x = ir"while(true) { val c = $fun(1, 2); c }; 42"
    x = x transformWith DSL.BN
    x eqt ir"while(true) { $res; () }; 42"
    
    y = ir"while(true) { var c = $fun(1, 2); c += 1; println(c) }"
    y = y transformWith DSL.BN
    y eqt ir"while(true) { var c = $res; c += 1; println(c) }"
    
    y = ir"((y: Int) => println($fun(1,y)))(2)"
    y = y transformWith DSL.BN
    y eqt ir"println($res)"
  }
  
}

