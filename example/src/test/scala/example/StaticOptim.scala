package example

import scp._
import scp.ir2.{BindingNormalizer, TopDownTransformer}
import utils._

/**
  * Created by lptk on 15/09/16.
  */
object StaticOptim extends App {
  
  object Stopt extends StaticOptimizer[ListOptimizer]
  import Stopt._
  
  
  def vanilla = {
    val f = (x: List[Long]) => x.foldLeft(0L)(_ + _)
    val ls = List.range(0, 66666L)
    for (i <- 0 until 1000) f(ls)
    f(ls)
  }
  
  def opt = optimize {
    val f = (x: List[Long]) => x.foldLeft(0L)(_ + _)
    val ls = List.range(0, 66666L)
    for (i <- 0 until 1000) f(ls)
    f(ls)
  }
  
  def time[A](name: String)(f: => A) = {
    val t0 = System.nanoTime()
    val result = f
    val t1 = System.nanoTime()
    println(s"$name took: " + (t1 - t0) + "ns")
    result
  }
  
  def test = {
    assert( time("Vanilla  ")(vanilla) == 
            time("Optimized")(opt) )
  }
  
  for (i <- 0 until 5) test
  println
  
  time("Optimized")(opt) |> println
  time("Vanilla  ")(vanilla) |> println
  //time("Optimized")(opt) |> println
  
}
