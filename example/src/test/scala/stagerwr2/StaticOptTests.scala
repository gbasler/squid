package stagerwr2

//object StaticOptTestsOptimizer extends squid.StaticOptimizer[stagerwr2.compiler.Compiler]
//import StaticOptTestsOptimizer.{dbg_optimize, optimize, optimizeAs}
import stagerwr.MyOptimizer.{optimize, dbg_optimize, optimizeAs, dbg_optimizeAs}
import squid.statics._
import squid.DumpFolder

object StaticOptTests extends App {
//object StaticOptTests extends StaticOptTests with App
//class StaticOptTests {
  
  implicit val df = compileTime(DumpFolder("/tmp"))
  //implicit val df: DumpFolder = compileTime(DumpFolder("/tmp"))
  //implicit val df = DumpFolder("/tmp")
  // ^ Warning:(26, 34) A compile-time implicit value of type squid.DumpFolder was seeked by the 'optimize' macro,
  //                    but an expression was inferred from which a compile time value could not be retrieved: StaticOptTests.this.df
  
  val x = 0
  //def test(x:Int) = x // doesn't work (expected)
  //def test[A] = (x:A) => x // doesn't work (expected)
  def test = (x:Int) => x // works
  
  //val r = dbg_optimize {
  //val r = optimize {
  val r = optimizeAs('MyOptTest) {
  //val r = dbg_optimizeAs('MyOptTest) {
    //val f = (a:Int) => a + 42 + x
    val f = (a:Int) => test(a + 42 + x)
    f(0)
  }
  
  
  println(r)
  
}
