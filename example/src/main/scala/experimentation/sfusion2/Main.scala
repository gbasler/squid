package sfusion2

import compiler.{Embedding,Compiler}

/**
  * Created by lptk on 11/06/17.
  */
object Main extends App {
  object C extends Compiler
  import Embedding.Predef._
  import Embedding.Quasicodes._
  
  
  //System exit 0
  
  
  //val c0 = ir{ Strm(11,22,33).flatMap(x => Strm("a","b","c").map(y => x -> y)).zip(Strm.iterate(0)(_+1)) } alsoApply println 
  //val c0 = ir{ Strm(11,22,33).flatMap(x => Strm("a","b","c").map(y => x + ":" + y)).zip(Strm.iterate(0)(_+1)).foreach(println) } alsoApply println
  //val c0 = ir{ Strm.fromIndexed(1 to 3).flatMap(x => Strm.fromIterable(Seq("a","b","c")).map(y => x + ":" + y)).foreach(println) } alsoApply println
  //val c0 = ir{ 
  //  val p = Strm.fromIndexed(1 to 3).flatMap(x => Strm.fromIterable(Seq("a","b","c")).map(y => x + ":" + y)).producer()
  //  p.remaining()
  //  println(p.step())
  //  p.remaining()
  //  println(p.step())
  //} alsoApply println
  
  //val c0 = ir{ Strm.fromIterable(Seq(11,22,33)).map(_ + ":").fold("")(_ + _) } alsoApply println 
  //val c0 = ir{ Strm.fromIterable(Seq(11,22,33)).map(_ + ":").take(2).fold("")(_ + _) } alsoApply println 
  //val c0 = ir{ Strm.fromIndexed(IndexedSeq(11,22,33)).map(_ + ":").fold("")(_ + _) } alsoApply println 
  
  //val c0 = ir{ println(IndexedSeq("42")); if (readInt>0)println(IndexedSeq("42")); "42" } alsoApply println // FIXME scheduling not fixed??! 
  
  
  // ---
  
  // TODO try using .flatten to mess with things by introducing $conforms...
  ///*
  import Strm._
  
  val xss = IndexedSeq(1 to 10, 100 to 200)
  val s0 = Vector(Vector("a","aa","aaa"),Vector("bbb","bb","bb"))
  val s1 = Vector(Vector("uuuu","vvv"),Vector("xxxxxx"))
  
  //val c0 = ir{ Strm.fromIndexed(1 to 5).map(_ + 1).take(2).foreach(println) } alsoApply println
  
  //val c0 = ir{ (xss: IndexedSeq[IndexedSeq[Int]]) => fromIndexed(xss).flatMap(fromIndexed).map(_ + 1).fold(0)(_ + _) } alsoApply println
  //val c = (xss: IndexedSeq[IndexedSeq[Int]]) => fromIndexed(xss).flatMap(fromIndexed).map(_ + 1).fold(0)(_ + _)
  //println(c(xss))
  
  //val c0 = ir{ Strm.fromIndexed(11 to 33).flatMap(x => Strm.fromIterable(Seq("a","b","c")).map(y => x + ":" + y)).zip(Strm.iterate(0)(_+1)).foreach(println) } alsoApply println
  
  //val c0 = ir{ (xs: IndexedSeq[Int], ys: IndexedSeq[String]) => 
  //  (fromIndexed(xs).take(3) zipWith fromIndexed(ys).map(_.toUpperCase))((x,y) => x+":"+y+", ").fold("")(_ + _) } alsoApply println
  
  //// From sfuscomp – works beautifully!
  //val c0 = ir{ (xs: IndexedSeq[Int], ys: IndexedSeq[Int]) => 
  //  (fromIndexed(xs).flatMap(x => fromIndexed(ys).map(_ + x)) zipWith fromIndexed(xs))(_ + _).fold(0)(_ + _) } alsoApply println
  
  val c0 = code{ (xs: IndexedSeq[IndexedSeq[String]], ys: IndexedSeq[IndexedSeq[String]]) => 
    (fromIndexed(xs).flatMap(fromIndexed(_).map(_.length)) zipWith fromIndexed(ys).flatMap(fromIndexed(_).map(_.length)))(_ + _).fold(0)(_ + _) } alsoApply println
  
  
  
  //println(c0.run.apply(1 to 10, 100 to 200))
  println(c0.run.apply(s0,s1))
  //println(c0.run)
  //println(c0.compile)
  val r = C.optimize(c0) //alsoApply println
  //println(r.run) // FIXME: expected a member of class Int, you provided method java.lang.Integer.equals
  //println(r.compile.apply(xss))
  //println(r.compile.apply(1 to 10, IndexedSeq("a","b","c","d")))
  //println(r.compile.apply(1 to 10, 100 to 200))
  println(r.compile.apply(s0,s1))
  //*/
  
  //println(ir{val s:Some[Int] = Some(1); s orElse s}) // works
  //println(ir{val x_7 = 1 to 3;
  //    val x_8 = scala.Some.apply[scala.collection.IndexedSeq[scala.Int]](x_7); x_8 orElse x_8})
  //println(ir{Some(1 to 3).get})
  //println(ir{Some(1 to 3)}.rep.effect)
  
  println("Done.")
}



/* --- OLD ---


println(ir{println(Some((x:Int) => readInt))})


val c0 = ir{ Strm(11,22,33).flatMap(x => Strm('a,'b,'c).map(y => x -> y)).zip(Strm.iterate(1)(_+1)) } alsoApply println

  //println(ir{Strm()})
  //val c0 = ir{ Strm.single(1).map(_ + 1).flatMap[Int](_ => Strm.none:Strm[Int]).filter(_ > 0) } alsoApply println

*/
