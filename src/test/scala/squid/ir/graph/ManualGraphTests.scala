
package squid
package ir
package graph

import squid.lang.ScalaCore
import utils._

object ManualGraphTests extends Graph with ScalaCore

class ManualGraphTests extends MyFunSuite(ManualGraphTests) {
  import DSL.Predef._
  import DSL.{typeRepOf => _,_}
  
  implicit def toRep(d: Def): Rep = d.toRep
  val IntPlus = `scala.Int`.`method +:3`.value
  val IntToDouble = `scala.Int`.`method toDouble`.value
  val Int = typeRepOf[Int]
  
  val c0,c1 = new CallId
  val v = bindVal("v",Int,Nil)
  val w = bindVal("w",Int,Nil)
  
  test("A") {
    
    //val shd = Abs(v, Arg(c0, v, Some(Arg(c1, methodApp(v,IntPlus,Nil,Args(const(1))::Nil,Int), Some(v)))))(lambdaType(Int::Nil,Int)).toRep
    // ^ can't explain why, bt the above behaves differently; the scheduled code has an additional `val v_0 = squid.lib.unbound("v");` at the top...
    val shd = abs(v, Arg(c0, v, Some(Arg(c1, methodApp(v,IntPlus,Nil,Args(const(1))::Nil,Int), Some(v)))))
    val g = methodApp(Call(c0, shd), IntPlus, Nil, Args(Call(c1, shd))::Nil, Int)
    println(g)
    println(g.showGraph)
    println(g.showRep)
    
  }
  
  test("B") {
    
    val shd = abs(v, Arg(c0, w, Some(Arg(c1, methodApp(v,IntPlus,Nil,Args(const(1))::Nil,Int), Some(v)))) |> (mapp(_,IntToDouble,Int)()()))
    val g = methodApp(abs(w, Call(c0, shd)), IntPlus, Nil, Args(Call(c1, shd))::Nil, Int)
    println(g)
    println(g.showGraph)
    println(g.showRep)
    
  }
  
  
}
