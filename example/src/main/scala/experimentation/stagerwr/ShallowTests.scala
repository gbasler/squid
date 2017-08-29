package stagerwr

/**
  * Created by lptk on 16/06/17.
  */
object ShallowTests extends App {
  //import Embedding.Predef._
  //import Embedding.Quasicodes._
  
  //ir{} alsoApply println
  
  val s0 = Strm.fromIndexed(0 to 7)
  
  val s1 = s0.map(_+1).take(3)
  //s1.foreach(println)
  s1.map(_+"; ").foreach(print); println
  println(s1.fold(0)(_+_))
  
  //println
  
  //s0.foreach(println)
  val s2 = s0.filter(_ % 2 == 1)
  //s2.foreach(println)
  s2.map(_+"; ").foreach(print); println
  
  //println
  
  //s1.zipWith(s2)((x,y) => println(x,y))
  s1.zip(s2).foreach(println)
  
  println
  
  {
    val s0 = Strm.range(0, 3)
    val s1 = Strm.range(111, 222)
    s0.flatMap(x => s0.map(_ -> x)).zip(s1).foreach(println)
  }
  
  
  
  println("\nDone.")
}
