package scp
package feature

class Subs extends MyFunSuite2 {
  import TestDSL2.Predef._
  
  val a2b = ir"($$a: Int) * 2 + ($$b: Int)"
  
  test("subs Basics") {
    
    eqt(ir"$$a: Int" subs 'a -> ir"42",  ir"42")
    
    eqt(a2b subs 'b -> ir"$$a: Int",  ir"($$a: Int) * 2 + ($$a: Int)")
    eqt(a2b subs 'b -> ir"$$a: Int" subs 'a -> ir"42",  ir"(42: Int) * 2 + (42: Int)")
    
    val f = ir"(x: Int) => x + 1"
    f match {
      case ir"(y: Int) => $body" =>
        val r = ir"(z: Int) => ${ body subs 'y -> ir"$$z: Int" }"
        eqt(r, f)
    }
    
    assertDoesNotCompile(""" a2b subs ('a, ir"42") """) // Error:(26, 9) Illegal syntax for `subs`; Expected: `term0.subs 'name -> term1`
    
  }
  
  test("subs Context") {
    
    (a2b subs 'a -> ir"42" : IR[Int, {val b: Int}])
    
    assertDoesNotCompile(""" (a2b subs 'a -> ir"42" : IR[Int,{}]) """) // Error:(34, 11) type mismatch; found: scp.TestDSL2.Predef.base.IR[Int,Any with Any{val b: Int}]; required: scp.TestDSL2.Predef.IR[Int,AnyRef]
    assertDoesNotCompile(""" (a2b subs 'a -> ir"42" run) """) // Error:(35, 29) Cannot prove that AnyRef <:< Any with Any{val b: Int}.
    assertDoesNotCompile(""" (ir"($$a: Int)+1" subs 'a -> ir"$$a: Int" run) """) // Error:(39, 47) Cannot prove that AnyRef <:< Any{val a: Int} with Any.
    
    assert( (a2b subs 'a -> ir"42" subs 'b -> ir"666" run) === 42 * 2 + 666 )
    a2b subs 'a -> ir"$$a: Int" : IR[Int, {val a: Int; val b: Int}]
    a2b subs 'a -> ir"$$a: Int" withContextOf a2b
    
    a2b subs 'a -> ir"$$x: Int" : IR[Int, {val x: Int; val b: Int}]
    a2b subs 'a -> ir"$$b: Double" : IR[Int, {val b: Int with Double}]
    
  }
  
  
}
