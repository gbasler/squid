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
  
  pgrm0 alsoApply println
  
  //println(pgrm0.run)
  //println(pgrm0.compile)
  
  //pgrm alsoApply println
  val r = C.optimize(pgrm0)
  
  //println(r.run)
  println(r.compile)
  
  
}
