// Copyright 2017 EPFL DATA Lab (data.epfl.ch)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package squid
package ir

// TODO port for SimpleANF

object ANFRewritingTests // to avoid SBT 1 warning

/*

import ir._

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
  
  object OnlineOptDSLNorm extends ANF with OnlineOptimizer {
    // FIXME NPE/sof
    val RW = new SelfTransformer with Rewritings
    val BN = new SelfTransformer with BindingNormalizer
    def pipeline = BN.pipeline andThen RW.pipeline
  }
  
}

/** TODO test online optims */
class ANFRewritingTests extends MyFunSuite2(ANFRewritingTests.DSL) {
  import DSL.Predef._
  import Math.pow
  
  def bl(q: IR[_,_]) = q.rep.asInstanceOf[base.Block]
  
  object Trans extends DSL.SelfTransformer with ANFRewritingTests.Rewritings with TopDownTransformer
  //object Trans extends DSL.SelfTransformer with ANFRewritingTests.Rewritings with BottomUpTransformer
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
  
  
  test("Nested Blocks") {
    // TODO test
    var w = ir"() => readInt.toDouble"
    ///_*_
    w = w transformWith Trans
    //println(w)
    //println(w rep)
    w eqt ir"() => readDouble"
    
    val wInit = ir"val ri = readInt; () => ri.toDouble"
    w = wInit
    //println(w rep)
    w = /*Trans.TranformerDebug.debugFor*/ (w transformWith Trans)
    //println(w)
    //println(w rep)
    w eqt wInit // rw should not apply
    
    println
    
    w = ir"val ri = readInt.toDouble; () => ri"
    //println(w rep)
    w = /*Trans.TranformerDebug.debugFor*/ (w transformWith Trans)
    //println(w)
    //println(w rep)
    //println(ir"val ri = readDouble; () => ri" rep)
    w eqt ir"val ri = readDouble; () => ri" // FIXedME nested block eqvce
    
    
    // A failed rewriting should not prevent later ones
    //w = ir"val ri = readInt; val ri2 = readInt.toDouble; () => ri.toDouble+ri2"
    w = ir"val ri = readInt; () => ri.toDouble + readInt.toDouble"
    //println(w rep)
    w = /*Trans.TranformerDebug.debugFor*/ (w transformWith Trans)
    //println(w rep)
    w eqt ir"val ri = readInt; () => ri.toDouble + readDouble"
    
    
    // One rw at a time...
    // TODOne
    var v = ir"readInt.toDouble + readInt.toDouble"
    v = /*Trans.TranformerDebug.debugFor*/ (v transformWith Trans)
    v eqt ir"readDouble + readInt.toDouble"
    v = /*Trans.TranformerDebug.debugFor*/ (v transformWith Trans)
    v eqt ir"readDouble + readDouble"
    
    
    //w = w transformWith Trans
    //println(w rep)
    
    //System exit 0
    //_*_/
    
    var x = ir"Math.pow(((x:Unit) => readInt.toDouble)(Unit), 2)"
    x = x transformWith Trans
    x eqt ir"val r = readDouble; r * r"
    
    x = ir"val r = (() => readInt.toDouble)(); r * r"
    x = x transformWith Trans
    x eqt ir"val r = (() => readDouble)(); r * r"
    
    x = ir"val r = () => readInt.toDouble; r() * r()"
    x = x transformWith Trans
    x eqt ir"val r = () => readDouble; r() * r()"
    x eqt ir"(() => readDouble)() * (() => readDouble)()"
    
    x = ir"Math.pow((() => 42.0)(), 2)"
    x = x transformWith Trans
    x eqt ir"val app = (() => 42.0)(); app * app"
    
    x = ir"Math.pow((() => readInt.toDouble)(), 2)"
    x = x transformWith Trans
    x eqt ir"val app = (() => readDouble)(); app * app"
    
    x = x transformWith DSL.BN
    x eqt ir"val rd = readDouble; rd * rd"
    
  }
  
  
  test("Removing Uncurry on Effectful Function") {
    
    var x = ir"(() => readDouble)()"
    x = x transformWith DSL.BN
    x eqt ir"readDouble"
    
    x = ir"val app = (() => readDouble)(); app * app"
    x = x transformWith DSL.BN
    x eqt ir"val n = readDouble; n * n"
    
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
    //println(x)
    //x eqt ir"while(true) { $res; () }; 42" // FIXME effect is not retained!
    x eqt ir"while(true) { val tmp = $res; () }; 42"
    
    y = ir"while(true) { var c = $fun(1, 2); c += 1; println(c) }"
    y = y transformWith DSL.BN
    y eqt ir"while(true) { var c = $res; c += 1; println(c) }"
    
    y = ir"((y: Int) => println($fun(1,y)))(2)"
    y = y transformWith DSL.BN
    y eqt ir"println($res)"
    
    
    val z = ir"scp.lib.uncurried2((acc_7: Unit) => (x_8: Int) => { (4, 5) }).apply(Unit,3)"
    println(z)
    println(z transformWith DSL.BN)
    
  }
  
  
}
*/

