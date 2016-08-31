package scp
package functional

import utils.GenHelper

class PowRewrites extends MyFunSuite2 {
  import DSL.Predef._

  val d = ir".5"

  val e0 = ir"Math.pow($d, 0)".rep -> ir"1.0".rep
  val e1 = ir"Math.pow($d, 1)".rep -> ir"1.0 * $d".rep
  val e2 = ir"Math.pow($d, 2)".rep -> ir"1.0 * $d * $d".rep
  val e3 = ir"Math.pow($d, 3)".rep -> ir"1.0 * $d * $d * $d".rep
  
  val p0 = ir"println(Math.pow($d, 2) + 1)".rep -> ir"println((1.0 * $d * $d) + 1)".rep
  
  
  test("Pow 2") {
    
    object Trans extends ir2.SimpleRuleBasedTransformer with ir2.TopDownTransformer {
      val base: TestDSL2.type = TestDSL2
      
      rewrite {
        case ir"Math.pow($x, 2)" => ir"1.0 * $x * $x" // add `1.0 *` to reuse the same examples as for "Pow n"
      }
      
      assertDoesNotCompile(""" rewrite { case ir"Math.pow($x, 2)" => ??? } """) // Error:(31, 46) This rewriting does not produce a scp.TestDSL.type.Quoted type as a return.
      
      assertDoesNotCompile(""" rewrite { case _ => ir"42" } """) // Error:(31, 22) Could not determine extracted type for that case.
      
    }
    
    eqt( Trans.transform(e2._1) , e2._2 )
    eqt( Trans transformBottomUp e2._1 , e2._2 )
    eqt( Trans transformBottomUp p0._1 , p0._2 )
    //eqt( p0._1 bottomUpTransform Trans , p0._2 )
    
  }
  
  test("Pow n") {
    
    object Trans extends ir2.SimpleRuleBasedTransformer with ir2.TopDownTransformer { val base: TestDSL2.type = TestDSL2; rewrite {
      
      // Maybe we could make this work with a special-case in the rewrite rule macro
      /*
      // Error:(50, 57) Cannot rewrite a term of context [Unknown Context] to an unrelated context ctx
      case ir"Math.pow(${x: Q[Double,ctx]}, ${Const(n)})" if n.isValidInt && (0 to 32 contains n.toInt) =>
        (1 to n.toInt).foldLeft(ir"1.0": Q[Double,ctx]){ case (acc, n) => ir"$acc * $x" }
      */
      
      // Simplest workaround:
      case ir"Math.pow($x, ${Const(n)})" if n.isValidInt && (0 to 32 contains n.toInt) =>
        (1 to n.toInt).foldLeft(ir"1.0" withContextOf x){ case (acc, _) => ir"$acc * $x" }
        
    }}
    
    eqt( Trans.transform(e0._1) , e0._2 )
    eqt( Trans.transform(e1._1) , e1._2 )
    eqt( Trans.transform(e2._1) , e2._2 )
    eqt( Trans.transform(e3._1) , e3._2 )
    eqt( Trans transformBottomUp p0._1 , p0._2 )
    
  }
  
  
}







