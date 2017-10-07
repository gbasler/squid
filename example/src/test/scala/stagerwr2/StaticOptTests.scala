package stagerwr2

object StaticOptTestsOptimizer extends squid.StaticOptimizer[stagerwr2.compiler.Compiler]

import StaticOptTestsOptimizer.{dbg_optimize, optimize, optimizeAs}
import squid.DumpFolder
import squid.quasi.dbg_embed
import squid.quasi.embed

/**
  * Created by lptk on 30/06/17.
  */
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

//@embed
//@dbg_embed
//object StoptTest {
//  
//  def foo = optimize { 123 }
//  
//}
//object StoptTestApp extends App {
//  println(StoptTest.foo)
//}
