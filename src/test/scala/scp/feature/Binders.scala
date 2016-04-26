package scp
package feature

import scp.lang.ScalaTyping

class Binders extends MyFunSuite {
  import TestDSL._
  
  test("Val Defs") {
    // TODO test type-ascribed val; test scope extrusion check
    
    val ap1 = dsl"val a = 0; a+1"
    
    assertDoesNotCompile(""" ap1 match { case dsl"val x = $init; $body" => } """) // Error:(13, 22) Embedding Error: No type info for hole 'init', in: { ...
    
    val b = ap1 match {
      case dsl"val x = $init: Int; $body" =>
        eqt(init: Q[Int,{}], dsl"0")
        eqt(init.trep, typeRepOf[Int])
        eqt(body: Q[Int,{val x: Int}], dsl"($$x:Int)+1")
        eqt(body.trep, typeRepOf[Int])
        body
    }
    
    val b2 = dsl"$b * 2"
    eqt(b2, dsl"(($$x:Int)+1)*2")
    
    assertDoesNotCompile(""" b2.run """) // Error:(26, 8) Cannot prove that AnyRef <:< Any{val x: Int}.
    
    same(dsl"val x = 42; $b2".run, 86)
    
  }
  
  test("Nested Val Defs") {
    
    dsl"val a = String.valueOf(123456789); val b = 3; a take b" matches {
      case dsl"val a: $at = $a; val b: $bt = $b; $body: $bodyt" =>
        eqt(at.rep, typeRepOf[String])
        eqt(bt.rep, typeRepOf[Int])
        eqt(bodyt.rep, typeRepOf[String])
        eqt(a, dsl"String.valueOf(123456789)")
        eqt(b, dsl"3")
        //eqt(body, dsl"($$a:String) take $$b") // FIXME: typEv f 'at'
    }
    
  }
  
  test("Lambdas") {
    
    val lam = dsl"(arg: Int) => arg * 2"
    
    lam match { case dsl"(_ * 2)" => }
    lam match { case dsl"(_ * (${ConstQ(2)}: Int))" => }
    lam match { case dsl"(x => x * 2)" => }
    lam match { case dsl"(x => x * ($n: Int))" => eqt(n: Q[Int, {val x: Int}], dsl"2") }
    lam match { case dsl"((x: Int) => x * 2)" => }
    //lam match { case dsl"(x => $body)" => eqt(body, dsl"($$x:Int)*2") } // TODO make it work
    val b = lam match { case dsl"(x => $body: Int)" => eqt(body: Q[Int, {val x: Int}], dsl"($$x:Int)*2"); body }
    
    val b2 = dsl"$b * 2"
    
    assertDoesNotCompile(""" b2.run """)
    
  }
  
  test("Nested Lambdas") {
    
    val lamlam = dsl"(str: String) => (n: Int) => str take n"
    
    lamlam match {
      case dsl"(a: String) => (b: Int) => a take b" =>
    }
    val b = lamlam match {
      case dsl"(a => b => $body: String)" =>
        body match {
          case dsl"($a:String) take $b" =>
        }
        body: Q[String, {val a: String; val b: Int}]
    }
    
    assertDoesNotCompile(""" b2.run """)
    assertDoesNotCompile(""" dsl"val a = String.valueOf(123456789); $b".run """)
    
    same(dsl"val a = String.valueOf(123456789); val b = 3; $b".run, "123")
    
  }
  
  
}
