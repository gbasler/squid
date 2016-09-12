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
  
  object DSL extends ANF
  
  object OnlineOptDSL extends ANF with OnlineOptimizer with Rewritings
  
}

/** TODO test online optims */
class ANFRewritingTests extends MyFunSuite2(ANFRewritingTests.DSL) {
  import DSL.Predef._
  
  def bl(q: IR[_,_]) = q.rep.asInstanceOf[base.Block]
  
  object Trans extends DSL.SelfTransformer with ANFRewritingTests.Rewritings with TopDownTransformer
  Trans
  
  test("Rewrites Effects") { // FIXME correct rewriteRep
    
    var x = ir"val n = readInt:Double; println(n); n+42"
    //println(x rep)
    //println(x)
    
    //base.ANFDebug debugFor 
    {x = x transformWith Trans} // FIXME dup: readInt and readDouble*2 !!
    //println(x rep)
    //println(x)
    
    //x = ir"println(readInt:Double)" transformWith Trans // FIXME dup
    
  }
  
  test("Binding Rewriting") {
    // FIXME correct rewriteRep
    /*
    var x = ir"val r: Double = readInt; 42 + r * r * r"  // Scala converts `readInt:Double` to `readDouble`
    x = x transformWith Trans
    x eqt ir"val r = readDouble; 42 + r * r * r"
    //eqt(x, ir"readInt; val r = readDouble; r * r * r", false) // TODO fix =~= and properly rm old readInt
    */
  }
  
  test("Effects") {
    
    var x = ir"Math.pow(.5, 2)"
    x = x transformWith Trans
    x eqt ir"(.5:Double) * .5"
    
    // FIXME correct rewriteRep
    /*
    x = ir"42 + Math.pow(.5, 2)"
    x = x transformWith Trans
    x dbg_eqt ir"42 + ((.5:Double) * .5)"
    
    x = ir"42 + Math.pow(readInt, 2)"
    x = x transformWith Trans
    println(bl(x)) // FIXME nested block!!
    assert(bl(x) // [ (#36 = scala.Predef.readInt()); (#44 = scala.Predef.readDouble()); (#50 = #44.*(#44)); (#52 = 42.+([ #44; #50; #50 ])); #52 ]
      //.effects.size == 3) // FIXME
      .effects.size == 4)
    x eqt ir"val r = readDouble; 42 + r * r"
    
    x = ir"42 + Math.pow(Math.pow(readInt, 2), 2)"
    x = x transformWith Trans
    //println(bl(x))
    x eqt ir"42 + { val r = readDouble; val r2 = r * r; r2 * r2 }"
    */
    
  }
  
}

