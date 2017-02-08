package sfusion

import org.scalatest.FunSuite
import squid.TestDSL
import squid.ir._

/**
  * Created by lptk on 07/02/17.
  */
class OptimTests extends FunSuite {
  
  object Compiler extends compiler.TestCompiler
  import Compiler.Code.Predef._
  import Compiler.Code.Quasicodes._
  Compiler.Code.embed(algo.`package`)
  
  test("Basics") {
    
    //val c0 = ir"Sequence(1,2,3,4)" // FIXME handle varargs in @embed...
    
    val c0 = ir"Sequence.fromIndexed(1 to 10).toString"
    
    //println(Compiler.optimize(c0))
    Compiler.wrapOptim("Basics") {
      Compiler.optimize(c0)
    }
    
    
    
  }
  
  test("Sieve") {
    
    val c0 = ir{algo.primeSum(100)}
    //println(c0)
    Compiler.wrapOptim("Sieve") {
      Compiler.optimize(c0)
    }
    
  }
  
  
  
}

