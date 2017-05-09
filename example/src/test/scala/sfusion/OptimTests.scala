package sfusion

import org.scalatest.FunSuite
import squid.TestDSL
import squid.ir._
import squid.utils._

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
    
    val res = "Sequence(1,2,3,4,5,6,7,8,9,10)"
    assert(c0.run == res)
    
    val c1 = Compiler.wrapOptim("Basics") {
      Compiler.optimize(c0)
    }
    
    //assert(c1.run == res) // the hacky InterpreterBase has problems with by-name parameters due to Scala reflexion limitations... (here affecting squid.lib.And)
    assert(c1.compile == res)
    
  }
  
  test("Sieve") {
    
    val c0 = ir{algo.primeSum(100)}
    assert(c0.run == 101)
    
    val res = Compiler.wrapOptim("Sieve") {
      val r = Compiler.optimize(c0)
      //println(r.rep)
      //println(r.run)  // FIXME: expected a member of class Boolean, you provided method squid.lib.And
    }
    
  }
  
  
  test("Join Lines") {
    
    val ls = List("lol","okay","test")
    val res = "lol\nokay\ntest"
    
    val c0 = ir{algo.joinLinesSimple(_:Iterable[String])}
    val r0 = algo.joinLinesSimple(ls)
    
    assert(r0 == res)
    assert((ls |> c0.run) == r0)
    
    Compiler.wrapOptim("JoinLines") {
      
      // Note: for some reason, `transfo.IdiomsNormalizer` does not seem to eliminate extra `toString` calls,
      // despite that transformer working well on simple examples as in `NormalizationTests`.
      // This might indicate a subtle typing problem, to investigate...
      
      val r = Compiler.optimize(c0)
      assert((ls |> r.run) == r0)
      // ^ Note: when not inlined completely, `run` throws: scala.ScalaReflectionException: expected a member of class Boolean, you provided method squid.lib.And
    }
    
    val c1 = ir{algo.joinLinesComplex(_:Iterable[String])}
    val r1 = algo.joinLinesComplex(ls)
    
    assert(r1 == res)
    assert((ls |> c1.run) == r1)
    
    Compiler.wrapOptim("JoinLinesComplexs") {
      val r = Compiler.optimize(c1)
      //println(ls |> r.run)  // FIXME: java.lang.NullPointerException
    }
    
  }
  
  
  /* // TODO
  test("Avg Words") {
    
    val txt = "Hello there. This is a sentence. This is another, longer one."
    val res = 11.0/3
    
    val c0 = ir{algo.avgWordsPerSentence _}
    val r0 = algo.avgWordsPerSentence(txt)
    //println(r0)
    //println(c0)
    
    assert(r0 == res)
    assert((txt |> c0.run) == r0)
    
    Compiler.wrapOptim("AvgWords") {
      val r = Compiler.optimize(c0)
      //assert((txt |> r.run) == r0)
    }
    
  }
  */
  
  
  /*
  test("WAT") {
    
    val c0 = ir{algo.wat}
    println(c0)
    Compiler.wrapOptim("Wat") {
      val r = Compiler.optimize(c0)
    }
    
  }
  */
  
}

