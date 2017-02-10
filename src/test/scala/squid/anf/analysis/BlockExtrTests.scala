package squid
package anf

import ir._
import squid.anf.analysis.BlockHelpers

object BlockExtrTests {

  object DSL extends SimpleANF with BlockHelpers

}

/**
  * Created by lptk on 10/02/17.
  * 
  * TODO in RwR, xtor should also try to match the whole pgrm? -> cf Block xtor
  * 
  */
class BlockExtrTests extends MyFunSuite(BlockExtrTests.DSL) {
  import DSL.Predef._
  import base.{Block,WithResult}
  
  
  test("Basics") {
    
    val c = ir"println(readInt); 42"
    
    c match {
      case Block(b) =>
        b eqt c
        b.res eqt ir"42"
    }
    c match {
      case Block(WithResult(b,ir"43")) => fail
      case Block(WithResult(b,ir"42")) =>
    }
    
    def f(q:IR[_,{}]) = q rewrite {
      case ir"while(${Block(b)}){}" => ir"val c = $b; while(c){}"  // Stupid rewriting just to test the feature
    }
    ir"while(readInt>0){}" |> f eqt ir"val c = readInt>0; while(c){}"
    
  }
  
  
}
