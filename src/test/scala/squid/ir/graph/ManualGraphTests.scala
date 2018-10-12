
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
  val Int = typeRepOf[Int]
  
  test("A") {
    
    val c0,c1 = new CallId
    val v = bindVal("v",Int,Nil)
    val shd = Abs(v, Arg(c0, v, Some(Arg(c1, methodApp(v,IntPlus,Nil,Args(const(1))::Nil,Int), Some(v)))))(lambdaType(Int::Nil,Int)).toRep
    val g = methodApp(Call(c0, shd), IntPlus, Nil, Args(Call(c1, shd))::Nil, Int)
    println(g)
    println(g.showGraph)
    println(g.showRep)
    
  }
  
  
}
