package scp
package feature

class FreeVariables extends MyFunSuite2 {
  
  import TestDSL2.Predef._
  
  test("Explicit Free Variables") {
    
    val x: Q[Int,{val x: Int}] = ir"$$x: Int"
    assert(x.rep match {
      case base.RepDef(base.Hole("x")) => true  // Note: no `base.Ascribe` node because ascriptions to the same type are removed
      case _ => false
    })
    
    val d = ir"$x.toDouble" : Q[Double, {val x: Int}]
    
    val s = ir"($$str: String) + $d" : Q[String, {val x: Int; val str: String}]
    
    val closed = ir"(str: String) => (x: Int) => $s" : Q[String => Int => String, {}]
    val closed2 = ir"(x: Int) => (str: String) => $s" : Q[Int => String => String, {}]
    
    assert(closed =~= ir"(a: String) => (b: Int) => a + b.toDouble")
    assert(closed2 =~= ir"(b: Int) => (a: String) => a + b.toDouble")
    
    
    assertDoesNotCompile(""" ir"42: $$t" """) // scp.quasi.EmbeddingException: Free type variables are not supported: '$$t'
    
  }
  
  test("Rep extraction") {
    hopefully(ir"Some($$x:Int)".rep extract ir"Some(42)".rep isDefined)
    hopefully(ir"Some(42)".rep extract ir"Some($$x:Int)".rep isEmpty)
  }
  
  test("Term Equivalence") {
    
    //val a = ir"($$x: Int)"
    //val b = ir"($$x: Int):Int"
    //println(a.rep extract b.rep, b.rep extract a.rep)
    
    assert(ir"($$x: Int)" =~= ir"($$x: Int)")
    assert(!(ir"($$x: Int)" =~= ir"($$y: Int)"))
    
    assert(ir"($$x: Int)" =~= ir"($$x: Int):Int")
    assert(!(ir"($$x: Int)" =~= ir"($$y: Int)+1"))
    assert(!(ir"($$x: Int)" =~= ir"($$y: String)"))
    
    assert(ir"($$x: Int) + ($$y: Int)" =~= ir"($$x: Int) + ($$y: Int)")
    
    assert(!(ir"($$x: Int) + ($$y: Int)" =~= ir"($$y: Int) + ($$x: Int)"))
    
  }
  
  test("Ascription and Hole Types are Checked") {
    import base.hole
    
    val N = typeRepOf[Nothing]
    
    hopefullyNot(ir"$$str:String" =~=  ir"$$str:Any")
    hopefullyNot(ir"$$str:String" =~= base.`internal IR`(hole("str", N)))
    
    hopefully(hole("str", N) =~=  hole("str", N))
    eqt( (hole("str", typeRepOf[Any]) extract hole("str", N)).get._1("str"), hole("str", N) )
    hopefullyNot(hole("str", N) =~=  hole("str", typeRepOf[Int]))
    hopefullyNot(hole("str", typeRepOf[String]) =~=  hole("str", typeRepOf[Int]))
    
  }
  
  
}






















