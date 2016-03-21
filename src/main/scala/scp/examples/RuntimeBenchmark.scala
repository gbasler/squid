package scp
package examples


import lang._
import quasi._
import ir._

import utils.Debug._

object Timing {
  def time[A](a: => A, msg: String) = {
    val start = System.nanoTime
    val result = a
    val end = (System.nanoTime - start) / (1000 * 1000)
    System.out.println(s"$msg completed in ${Console.BLUE}$end${Console.RESET} milliseconds")
    result
  }
}

object RuntimeBenchmark extends App {
  
  import TestDSL._
  import Timing._
  
  time( {
    for(i <- 0 to 1000) {
    dsl"""
    val f = (x: Int) => {
      x * x + x / x.toDouble
    }
    f(2)
  """
  }
  }, "node reification")
  
  
}
