package stagerwr2

//object StaticOptTestsOptimizer extends squid.StaticOptimizer[stagerwr2.compiler.Compiler]
//import StaticOptTestsOptimizer.{dbg_optimize, optimize, optimizeAs}
import stagerwr.MyOptimizer.{optimize, dbg_optimize, optimizeAs}

import squid.DumpFolder

object StaticOptTests extends App {
//object StaticOptTests extends StaticOptTests with App
//class StaticOptTests {
  
  implicit val `/tmp` = DumpFolder
  
  val x = 0
  //def test(x:Int) = x // doesn't work (expected)
  //def test[A] = (x:A) => x // doesn't work (expected)
  def test = (x:Int) => x // works
  
  //val r = dbg_optimize {
  //val r = optimize {
  val r = optimizeAs('MyOptTest) {
    //val f = (a:Int) => a + 42 + x
    val f = (a:Int) => test(a + 42 + x)
    f(0)
  }
  
  
  println(r)
  
}
