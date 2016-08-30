package scp
package feature

import utils._

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
    assertDoesNotCompile(""" ir"42" subs 'a -> ir"42" """) // Error:(9, 10) This term does not have a free variable named 'a' to substitute.
    assertDoesNotCompile(""" a2b subs 'b -> ir"4.2" """) // Error:(27, 20) Cannot substitute free variable `b: Int` with term of type `Double`
    
  }
  
  test("subs Context") {
    
    (a2b subs 'a -> ir"42" : IR[Int, {val b: Int}])
    
    assertDoesNotCompile(""" (a2b subs 'a -> ir"42" : IR[Int,{}]) """) // Error:(34, 11) type mismatch; found: scp.TestDSL2.Predef.base.IR[Int,Any with Any{val b: Int}]; required: scp.TestDSL2.Predef.IR[Int,AnyRef]
    assertDoesNotCompile(""" (a2b subs 'a -> ir"42" run) """) // Error:(35, 29) Cannot prove that AnyRef <:< Any with Any{val b: Int}.
    assertDoesNotCompile(""" (ir"($$a: Int)+1" subs 'a -> ir"$$a: Int" run) """) // Error:(39, 47) Cannot prove that AnyRef <:< Any{val a: Int} with Any.
    
    assert( (a2b subs 'a -> ir"42" subs 'b -> ir"666" run) == 42 * 2 + 666 )
    a2b subs 'a -> ir"$$a: Int" : Int IR {val a: Int; val b: Int}
    a2b subs 'a -> ir"$$a: Int" withContextOf a2b
    
    val t0 = a2b subs 'a -> ir"$$x: Int"
    t0 [ Int IR Any{val x: Int; val b: Int} ] // Note: Any{...} is more general than {...}, so we need Any here
    
    val t1 = a2b subs 'a -> ir"($$b: Double) toInt"
    t1 [ Int IR Any{val b: Int with Double} ]
    
  }
  
  test("subs Abstracted Context") {
    
    def foo[C](x: Double IR C) = {
      val q = ir"$x.toInt + ($$z: Symbol).toString.length" : Int IR C{val z:Symbol}
      var r: Int IR C{val a: Int; val z: Symbol} = null
      r = a2b subs 'b -> q
      r
    }
    
    var x: Double IR Any{val b: Boolean} = null
    x = ir"if ($$b: Boolean) 0.0 else 1.0"
    var f: Int IR {val b: Boolean; val a: Int; val z: Symbol} = null
    f = foo(x)
    
    val r = f subs 'b -> ir"true" subs 'a -> ir"42" subs 'z -> ir" 'ok "
    eqt(r: Int IR {}, ir" (42:Int) * 2 + ((if (true) 0.0 else 1.0).toInt + 'ok.toString.length) ")
    same(r.run, 42 * 2 + 3)
    
  }
  
  test("subs with Complex Replacement") {
    
    // FIXME: properly untypecheck patmat in macro or find another solution
    //a2b dbg_subs 'a -> { ir"42" match { case ir"42" => ir"42" } }
    
  }
  
}
