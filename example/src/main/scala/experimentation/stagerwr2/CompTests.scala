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
  
  //println(Embedding.Norm.rules.size)
  //println(Embedding.Norm.rules)
  //System exit 0;
  
  val A0 = Array(1,11,111,2,22,3,44)
  val A1 = Array.tabulate(42)(identity)
  
  
  /*
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
  
  filters alsoApply println
  
  //println(filters.compile apply A0)
  
  val r = C.optimize(filters)
  
  println(r.compile apply A0)
  */
  
  /*
  // original from benchmarks:
  val zipWith_after_flatMap = ir{ (xs : Array[Int], ys : Array[Int]) =>
    Strm.fromArray[Int](xs)
      .flatMap(x => Strm.fromArray[Int](ys).map(y => (x + y)))
      .zipWith(Strm.fromArray[Int](xs))((a,b) => a + b)
      .fold(0)((a,b) => a + b)
  } alsoApply println
  
  println(zipWith_after_flatMap.compile apply (A0,A1)) // 215
  val r = C.optimize(zipWith_after_flatMap)
  println(r.compile apply (A0,A1))
  */
  
  // original from benchmarks:
  //val flatMap_take_rewritten = ir{ (xs : Array[Int], ys : Array[Int]) =>
  //  Strm.fromArray[Int](xs)
  //    .flatMap(x => Strm.fromArray[Int](ys).map(y => (x * y)))
  //    .take(20000000)
  //    .fold(0)((a,b) => a + b)
  //} alsoApply println
  //
  //println(flatMap_take_rewritten.compile apply (A0,A1)) // 167034
  //val r = C.optimize(flatMap_take_rewritten)
  //println(r.compile apply (A0,A1))
  
  // original from benchmarks:
  val zip_flat_flat_rewritten = code{ (xs : Array[Int], ys : Array[Int]) =>
      Strm.fromArray[Int](xs)
      .flatMap(x => Strm.fromArray[Int](ys).map(y => (x * y)))
      .zipWith(
        Strm.fromArray[Int](ys)
          .flatMap(x => Strm.fromArray[Int](xs).map(y => (x * y)))
      )(_ + _)
      .take(20000000)
      .fold(0)((a,b) => a + b)
  }

  println(zip_flat_flat_rewritten.compile apply (A0,A1)) // 167034
  val r = C.optimize(zip_flat_flat_rewritten)
  println(r.compile apply (A0,A1))
  
  
  
  // FIXME why does dotProduct not stop as soon as the inner array is out?
  
  
  
  //val x = ir{
  //  val a = readInt
  //  if (a.>(1))
  //      if (a.>(2)) {
  //        println()
  //      }
  //}
  //x alsoApply println
  
  
}
