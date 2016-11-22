package squid
package feature

class Binders extends MyFunSuite {
  import TestDSL.Predef._
  
  test("Val Defs") {
    // TODO test type-ascribed val; test scope extrusion check
    
    val ap1 = ir"val a = 0; a+1"
    
    assertDoesNotCompile(""" ap1 match { case ir"val x = $init; $body" => } """) // Error:(13, 22) Embedding Error: No type info for hole 'init', in: { ...
    
    val b = ap1 match {
      case ir"val x = $init: Int; $body" =>
        eqt(init: Q[Int,{}], ir"0")
        assert(init.trep <:< typeRepOf[Int])
        eqt(body: Q[Int,{val x: Int}], ir"($$x:Int)+1")
        eqt(body.trep, typeRepOf[Int])
        body
    }
    
    /*
    // interestingly, this compiles but assumes type Any for 'body' (which should be safe)
    // ^ no more: removed ascription of the matched term when it was ascribing Any
    ap1.erase match {
      case ir"val x = $init: Int; $body" =>
        eqt(body: Q[Any,{val x: Int}], ir"($$x:Int)+1")
        eqt(implicitTypeOf(body), typeRepOf[Any])
    }
    */
    
    val b2 = ir"$b * 2"
    eqt(b2, ir"(($$x:Int)+1)*2")
    
    assertDoesNotCompile(""" b2.run """) // Error:(26, 8) Cannot prove that AnyRef <:< Any{val x: Int}.
    
    same(ir"val x = 42; $b2".run, 86)
    
  }
  
  test("Nested Val Defs") {
    
    ir"val a = String.valueOf(123456789); val b = 3; a take b" matches {
      case ir"val a: $at = $a; val b: $bt = $b; $body: $bodyt" =>
        eqt(at.rep, typeRepOf[String])
        assert(bt <:< irTypeOf[Int])
        eqt(bodyt.rep, typeRepOf[String])
        eqt(a, ir"String.valueOf(123456789)")
        eqt(b, ir"3")
        eqt(body, ir"($$a:String) take $$b")
    } and {
      case ir"val a: $at = $a; val b: Int = $b; $body: $bodyt" =>
        eqt(body, ir"($$a:String) take $$b")
    }
    
    
  }
  
  test("Lambdas") {
    
    val lam = ir"(arg: Int) => arg * 2"
    
    eqt(lam, ir"(x: Int) => x * 2")
    
    lam match { case ir"(_ * 2)" => }
    lam match { case ir"(_ * (${Const(2)}: Int))" => }
    lam match { case ir"(x => x * 2)" => }
    lam match { case ir"(x => x * ($n: Int))" => eqt(n: Q[Int, {val x: Int}], ir"2") }
    lam match { case ir"((x: Int) => x * 2)" => }
    lam match { case ir"(x => $body)" => eqt(body, ir"($$x:Int)*2") }
    val b = lam match { case ir"(x => $body: Int)" => eqt(body: Q[Int, {val x: Int}], ir"($$x:Int)*2"); body }
    
    val b2 = ir"$b * 2"
    
    assertDoesNotCompile(""" b2.run """)
    
  }
  
  test("Nested Lambdas") {
    
    val lamlam = ir"(str: String) => (n: Int) => str take n"
    
    lamlam match {
      case ir"(a: String) => (b: Int) => a take b" =>
    }
    val b = lamlam match {
      case ir"(a => b => $body: String)" =>
        body match {
          case ir"($a:String) take $b" =>
        }
        body: Q[String, {val a: String; val b: Int}]
    }
    
    assertDoesNotCompile(""" b2.run """)
    assertDoesNotCompile(""" ir"val a = String.valueOf(123456789); $b".run """)
    
    same(ir"val a = String.valueOf(123456789); val b = 3; $b".run, "123")
    
  }
  
  
}
