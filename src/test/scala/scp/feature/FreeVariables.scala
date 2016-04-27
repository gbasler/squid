package scp
package feature

class FreeVariables extends MyFunSuite {
  
  import TestDSL._
  
  test("Explicit Free Variables") {
    
    val x: Q[Int,{val x: Int}] = dsl"$$x: Int"
    assert(x.rep match {
      case Ascribe(Hole("x")) => true
      case _ => false
    })
    
    val d = dsl"$x.toDouble" : Q[Double, {val x: Int}]
    
    val s = dsl"($$str: String) + $d" : Q[String, {val x: Int; val str: String}]
    
    val closed = dsl"(str: String) => (x: Int) => $s" : Q[String => Int => String, {}]
    val closed2 = dsl"(x: Int) => (str: String) => $s" : Q[Int => String => String, {}]
    
    assert(closed =~= dsl"(a: String) => (b: Int) => a + b.toDouble")
    assert(closed2 =~= dsl"(b: Int) => (a: String) => a + b.toDouble")
    
    
  }
  
  test("Rep extraction") {
    hopefully(dsl"Some($$x:Int)".rep extract dsl"Some(42)".rep isDefined)
    hopefully(dsl"Some(42)".rep extract dsl"Some($$x:Int)".rep isEmpty)
  }
  
  test("Term Equivalence") {
    
    //val a = dsl"($$x: Int)"
    //val b = dsl"($$x: Int):Int"
    //println(a.rep extract b.rep, b.rep extract a.rep)
    
    assert(dsl"($$x: Int)" =~= dsl"($$x: Int)")
    assert(!(dsl"($$x: Int)" =~= dsl"($$y: Int)"))
    
    assert(dsl"($$x: Int)" =~= dsl"($$x: Int):Int")
    assert(!(dsl"($$x: Int)" =~= dsl"($$y: Int)+1"))
    assert(!(dsl"($$x: Int)" =~= dsl"($$y: String)"))
    
    assert(dsl"($$x: Int) + ($$y: Int)" =~= dsl"($$x: Int) + ($$y: Int)")
    
    assert(!(dsl"($$x: Int) + ($$y: Int)" =~= dsl"($$y: Int) + ($$x: Int)"))
    
  }
  
  
}






















