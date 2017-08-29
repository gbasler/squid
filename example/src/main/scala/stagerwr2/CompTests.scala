package stagerwr2

import compiler._

/**
  * Created by lptk on 30/06/17.
  * 
  * Comparing with SFTC
  * 
  */
object CompTests extends App {
  //object C extends Compiler
  object C extends DbgCompiler
  import Embedding.Predef._
  import Embedding.Quasicodes._
  import Strm._
  
  // FIXME: why do I get this? "Rewrite rules did not converge after 8 iterations."
  val filters = ir{ (xs : Array[Int]) =>
     //fromIndexed(xs)
     fromArray(xs)
       .filter(x => x > 1)
       .filter(x => x > 2)
       .filter(x => x > 3)
       .filter(x => x > 4)
       .filter(x => x > 5)
       .filter(x => x > 6)
       .filter(x => x > 7)
       .fold(0)((a,b) => a + b)
  }
  
  val A0 = Array(1,11,111,2,22,3,44)
  
  
  //println(Embedding.Norm.rules.size)
  //println(Embedding.Norm.rules)
  //System exit 0;
  
  
  ///*
  filters alsoApply println
  
  //println(filters.compile apply A0)
  
  val r = C.optimize(filters)
  
  println(r.compile apply A0)
  //*/
  
  //val x = ir{
  //  val a = readInt
  //  if (a.>(1))
  //      if (a.>(2)) {
  //        println()
  //      }
  //}
  //x alsoApply println
  
  
}
