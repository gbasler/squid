package scp
package functional

import scp.lang.Base
import scp.utils.Debug
import utils.GenHelper

class PowRewrites extends MyFunSuite {
  import DSL._
  
  val d = dsl".5"
  
  val e0 = dsl"Math.pow($d, 0)" -> dsl"1.0"
  val e1 = dsl"Math.pow($d, 1)" -> dsl"1.0 * $d"
  val e2 = dsl"Math.pow($d, 2)" -> dsl"1.0 * $d * $d"
  val e3 = dsl"Math.pow($d, 3)" -> dsl"1.0 * $d * $d * $d"
  
  val p0 = dsl"println(Math.pow($d, 2) + 1)" -> dsl"println((1.0 * $d * $d) + 1)"
  
  
  test("Pow 2") {
    
    object Trans extends ir.OfflineTransformer(TestDSL) {
      
      rewrite {
        case dsl"Math.pow($x, 2)" => dsl"1.0 * $x * $x" // add `1.0 *` to reuse the same examples as for "Pow n"
      }
      
      assertDoesNotCompile(""" rewrite { case dsl"Math.pow($x, 2)" => ??? } """) // Error:(31, 46) This rewriting does not produce a scp.TestDSL.type.Quoted type as a return.
      
      assertDoesNotCompile(""" rewrite { case _ => dsl"42" } """) // Error:(31, 22) Could not determine extracted type for that case.
      
    }
    
    eqt( Trans.applyTransform(e2._1) , e2._2 )
    eqt( Trans bottomUp e2._1 , e2._2 )
    eqt( Trans bottomUp p0._1 , p0._2 )
    eqt( p0._1 bottomUpTransform Trans , p0._2 )
    
  }
  
  test("Pow n") {
    
    object Trans extends ir.OfflineTransformer(TestDSL) { dbgrewrite {
      
      // Maybe we could make this work with a special-case in the rewrite rule macro
      /*
      // Error:(50, 57) Cannot rewrite a term of context [Unknown Context] to an unrelated context ctx
      case dsl"Math.pow(${x: Q[Double,ctx]}, ${Constant(n)})" if n.isValidInt && (0 to 32 contains n.toInt) =>
        (1 to n.toInt).foldLeft(dsl"1.0": Q[Double,ctx]){ case (acc, n) => dsl"$acc * $x" }
      */
      
      // Simplest workaround:
      case dsl"Math.pow($x, ${Constant(n)})" if n.isValidInt && (0 to 32 contains n.toInt) =>
        (1 to n.toInt).foldLeft(dsl"1.0" withContextOf x){ case (acc, _) => dsl"$acc * $x" }
        
    }}
    
    eqt( Trans.applyTransform(e0._1) , e0._2 )
    eqt( Trans.applyTransform(e1._1) , e1._2 )
    eqt( Trans.applyTransform(e2._1) , e2._2 )
    eqt( Trans.applyTransform(e3._1) , e3._2 )
    eqt( Trans bottomUp p0._1 , p0._2 )
    
  }
  
  
    
}







