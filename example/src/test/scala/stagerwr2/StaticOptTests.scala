package stagerwr2

object StaticOptTestsOptimizer extends squid.StaticOptimizer[stagerwr2.compiler.Compiler]

import StaticOptTestsOptimizer.{optimize,dbg_optimize}

/**
  * Created by lptk on 30/06/17.
  */
object StaticOptTests extends App {
//object StaticOptTests extends StaticOptTests with App
//class StaticOptTests {
  
  val x = 0
  
  val r = dbg_optimize {
    val f = (a:Int) => a + 42 + x
    f(0)
  }
  
  
  
  
  
  println(r)
  
}
