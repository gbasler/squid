package stagerwr

import compiler._

/**
  * Created by lptk on 21/06/17.
  */
object DeepTests extends App {
  object C extends Compiler
  import Embedding.Predef._
  import Embedding.Quasicodes._
  import Strm._
  
  //val pgrm0 = ir{
  //  //val s0 = Strm.fromIndexed(0 to 7)
  //  (xs:IndexedSeq[Int]) => val s0 = Strm.fromIndexed(xs)
  //  val s1 = s0.map(_+1).take(3)
  //  //val s1 = s0
  //  val s2 = s0.filter(_ % 2 == 1)
  //  s1.zip(s2).foreach(println)
  //}
  
  // Simple example for paper
  //val pgrm0 = ir{ (xs:IndexedSeq[Int]) => 
  //  val s0 = Strm.fromIndexed(xs)
  //  s0.zip(s0.filter(_ % 2 == 0)).foreach(println)
  //}
  
  // Manohar's paper example
  // simpler version also proposed in Hermit paper as: concatMapS (λx → enumFromToS 1 x) (enumFromToS 1 n)
  //val pgrm0 = ir{ identity {
  //  (a: Int, b: Int) => 
  //    val fld = range(a, b)
  //    val flatMapped = fld flatMap {
  //      i => range(1, i)
  //    }
  //    //val filtered = flatMapped filter (_ % 2 == 1)
  //    //filtered.map(_ * 3).fold(0)((acc, x) => acc + x)
  //    flatMapped.fold(0)((acc, x) => acc + x)
  //  } apply (2,3)
  //}
  
  // Typical flatMap blunder
  val pgrm0 = code{ 
    val s0 = range(0,3).map(n => range(0,n)).flatMap(identity)
    //s0.foreach(println)
    s0.zip(range(0,100)).foreach(println) // with zip
    //s0.zip(s0).foreach(println) // with hard zip
  }
  
  // FIXME Dynamic flatMap
  //val pgrm0 = ir{
  //  Strm.fromIndexed(0 to 3).flatMap(x =>
  //    if (x % 2 == 0) Strm.range(1, x)
  //    else Strm.range(1, x+1)
  //  ).foreach(println)
  //}
  
  //val pgrm0 = ir{
  //  val s0 = Strm.fromIndexed(0 to 7)
  //  s0.filter(_ % 2 == 1).foreach(println)
  //}
  
  //val pgrm0 = ir{
  //  val s0 = Strm.range(1, 3)
  //  val s1 = Strm.range(111, 222)
  //  s0.flatMap(x => s0.map(_ -> x)).zip(s1).foreach(println)
  //}
  //val pgrm0 = ir{
  //  val s0 = Strm.range(1, 3)
  //  s0.flatMap(x => Strm.range(x, x*2)).foreach(println)
  //}
  //val pgrm0 = ir{
  //  val s0 = Strm.range(1, 3)
  //  s0.flatMap(x => Strm.range(x, x*2)).map(_+1).foreach(println)
  //}
  
  pgrm0 alsoApply println
  
  //println(pgrm0.run)
  //println(pgrm0.compile)
  
  //pgrm alsoApply println
  val r = C.optimize(pgrm0)
  
  //println(r.run)
  println(r.compile)
  
  
}
