// Copyright 2018 EPFL DATA Lab (data.epfl.ch)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package squid
package ir
package graph2

import squid.ir.graph.CallId
import squid.lang.ScalaCore
import utils._

object ManualGraphTests extends Graph with ScalaCore

class ManualGraphTests extends MyFunSuite(ManualGraphTests) with MyFunSuiteTrait {
  import DSL.Predef._
  import DSL.{typeRepOf => _,_}
  
  implicit def toRep(d: Def): Rep = d.toRep
  implicit def toCond(cid: CallId): Condition = Condition(Nil,cid)
  val IntPlus = `scala.Int`.`method +:3`.value
  val IntToDouble = `scala.Int`.`method toDouble`.value
  val Int = typeRepOf[Int]
  val Bool = typeRepOf[Int]
  val Any = typeRepOf[Any]
  
  val ITESymbol = loadMtdSymbol(loadTypSymbol("squid.lib.package$"), "IfThenElse", None)
  def ITE(c:Rep,t:Rep,e:Rep) = methodApp(staticModule("squid.lib.package"), ITESymbol, t.typ::Nil,
    Args(c,byName(t),byName(e))::Nil, t.typ)
  val IntLT = `scala.Int`.`method <:3`.value
  def lt(lhs:Rep,rhs:Rep) = methodApp(lhs, IntLT, Nil, Args(rhs)::Nil,Bool)
  
  val c0,c1 = new CallId("α")
  val v = bindVal("v",Int,Nil)
  val w = bindVal("w",Int,Nil)
  val IntToInt = lambdaType(Int::Nil,Int)
  val f = bindVal("f",IntToInt,Nil)
  val IntToInt_to_IntToInt = lambdaType(IntToInt::Nil,IntToInt)
  val Yf = bindVal("Yf",IntToInt_to_IntToInt,Nil)
  
  test("A") {
    
    val shd = abs(v, Branch(c0, v, Branch(c1, methodApp(v,IntPlus,Nil,Args(const(1))::Nil,Int), v).mkRep).mkRep)
    val g = methodApp(Call(c0, Pass(c1, shd).mkRep).mkRep, IntPlus, Nil, Args(Call(c1, Pass(c0,shd).mkRep).mkRep)::Nil, Int)
    println(g)
    //println(g.iterator.toList)
    println(g.showGraph)
    //println(DSL.scalaTree(g))
    
    DSL.ScheduleDebug debugFor
    println(g.showRep)
    
  }
  
  test("B") {
    
    val shd = abs(v, Branch(c0, w, Branch(c1, methodApp(v,IntPlus,Nil,Args(const(1))::Nil,Int), v).mkRep).mkRep) |> (mapp(_,IntToDouble,Int)()())
    val g = methodApp(abs(w, Call(c0, shd).mkRep), IntPlus, Nil, Args(Call(c1, shd).mkRep)::Nil, Int)
    //println(g)
    println(g.showGraph)
    println(g.showRep)
    
  }
  
  test("OopsBox") {
    
    // FIXedME: this triggered the unimplemented case where a Pop node cannot pop anything!
    // FIXME now the readInt is duplicated, because of the duplication of continuation arguments bug
    
    val shd0 = Code[Int,Any](Box(c0, code"readInt".rep, Arg).mkRep)
    val shd1 = code"identity($shd0)"
    val g = Box(c0, code"$shd1 + $shd1".rep, Call).mkRep
    println(g.showGraph)
    //ScheduleDebug debugFor
    println(g.showRep)
    
  }
  
  /*
  test("Rebuild/Unbuild") {
    
    println(c0::c1::Nil)
    val r = rebuild(const(42),c0::c1::Nil,const(666))
    println(r)
    r.dfn |>? {
      case ArgSet(cs,t,e) => println(cs,t,e)
    }
    
  }
  
  test("Simplify") {
    
    val r = Call(c0, Arg(c0, v, w))
    println(r)
    println(r.simplify_!)
    
  }
  
  val Yx = bindVal("Yx",Any,Nil)
  val Yinner = abs(Yx, app(Yf, app(Yx, Yx)(Any))(Any))
  val Y = abs(Yf, app(Yinner, Yinner)(IntToInt))
  
  test("Recursion") {
    
    println(Y.showGraphRev)
    println(showRep(Y)) // looks like it 'unrolls' it once as part of schedulign (it applies f to the body of Y)
    
    val `v+1` = methodApp(v,IntPlus,Nil,Args(const(1))::Nil,Int)
    
    val recf = app(Y, abs(f, abs(v, app(f, `v+1`)(Int) )))(IntToInt)
    
    println(recf.showGraphRev)
    println(showRep(recf))
    //rw(Code[Any,Any](recf))() // assertion failed: nested call
    
    //println(EvalDebug debugFor eval(app(recf,const(0))(Int))) // SOF as expected
    
    val recg = app(Y, abs(f, abs(v, ITE(lt(v,const(42)),app(f, `v+1`)(Int),v) )))(IntToInt)
    
    println(recg.showGraphRev)
    println(showRep(recg))
    //rw(Code[Any,Any](recg))() // assertion failed: nested call
    
    //println(EvalDebug debugFor eval(app(recg,const(0))(Int))) // doesn't work... but IfTenElse's by-names are not correctly eval'd anyways
    
  }
  
  test("Unfounded Recursion") {
    
    val v = bindVal("v",Any,Nil)
    val poisonInner = abs(v, app(v, v)(Any))
    val poison = app(poisonInner,poisonInner)(Any)
    
    println(poison.showGraphRev)
    println(showRep(poison))
    rw(Code[Any,Any](poison))(doEval=false, maxCount = 8) // oopsie
    
  }
  */
  
}
