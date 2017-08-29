package stagerwr2

import compiler._

/**
  * Created by lptk on 21/06/17.
  */
object DeepTests2 extends App {
  object C extends Compiler
  import Embedding.Predef._
  import Embedding.Quasicodes._
  import Strm._
  
  
  // Simple example for paper
  //val pgrm0 = ir{ (xs:IndexedSeq[Int]) => 
  //  val s0 = Strm.fromIndexed(xs)
  //  s0.zip(s0.filter(_ % 2 == 0)).foreach(println)
  //}
  //val pgrm0 = ir{ (xs:IndexedSeq[Int]) => 
  //  val s0 = Strm.fromIndexed(xs)
  //  Strm.unfold(0)(i => Some(i,i+1)).zip(s0.filter(_ % 2 == 0)).foreach(println)
  //}
  //val pgrm0 = ir{ (n:Int) => 
  //  val s0 = Strm.unfold(0)(i => Some(i,i+1)).take(n).filter(_ % 2 == 0).foreach(println)
  //}
  
  // More complex paper example
  //val pgrm0 = ir{ (n:Int,m:Int) => 
  //  fromRange(0,n) zip(
  //    fromRange(0,m).map(i => fromRange(0,i)).flatMap(x => x)
  //  ) filter {x => x._1 % 2 == 0} foreach println
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
  val pgrm0 = ir{ 
    val s0 = range(0,3).map(n => range(0,n)).flatMap(identity)
    //s0.foreach(println)
    //s0.zip(range(0,100)).foreach(println) // with zip
    s0.zip(s0).foreach(println) // with hard zip
  }
  
  // Two filtered streams fused (not done in SFTC)
  //val pgrm0 = ir{ 
  //  val s0 = range(0,10).filter(_ % 2 == 0)
  //  val s1 = range(10,15).filter(_ % 2 == 1)
  //  s0.zip(s1).foreach(println)
  //}
  
  // Dynamic if-then-else + flatMap, fuses like a charm!
  //val pgrm0 = ir{
  //  Strm.range(0, 3).flatMap(x =>
  //    if (x % 2 == 0) Strm.range(1, x)
  //    else Strm.range(1, x+1)
  //  ).foreach(println)
  //}
  
  // FIXME LogicNorm probably makes it diverge
  // Dynamic zip
  //val pgrm0 = base debugFor (ir{
  //  val s0 = Strm.range(0, 3)
  //  val s1 = Strm.range(-3, 0)
  //  var cur = 0
  //  (if (cur > 0) s0 else s1).zipWith(if (cur % 2 == 0) s0 else s1)(_ + _).foreach { e => cur += e; println(e,cur) }
  //})
  
  
  pgrm0 alsoApply println
  
  //println(pgrm0.run)
  //println(pgrm0.compile)
  //println(pgrm0.compile.apply(42))
  
  //pgrm alsoApply println
  val r = C.optimize(pgrm0)
  
  //println(r.run)
  println(r.compile)
  //println(r.compile.apply(42))
  
  
}
