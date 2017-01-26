package squid
package feature

import utils.Debug._

class Matching extends MyFunSuite {
  import TestDSL.Predef._
  
  test("Holes") {
    val q = ir"0 -> 'ok"
    q match {
      //case ir"0 -> $b" => fail // Warning:(11, 12) Type inferred for hole 'b' was Nothing. Ascribe the hole explicitly to remove this warning.
      case ir"0 -> ($b: Symbol)" => eqt(b, ir"'ok")
    }
    
    ir"$q -> $q "
    assertDoesNotCompile(""" ir"$q // $q " """) // Error:(15, 5) Quasiquote Error: Illegal hole position for: $q
    assertDoesNotCompile(""" ir"0 -> 'ok // $q " """) // Error:(15, 5) Quasiquote Error: Illegal hole position for: $q
    assertDoesNotCompile(""" ir"0 -> 'ok // ${Const(42)} " """) // Error:(15, 5) Quasiquote Error: Illegal hole position for: ${TestDSL2.Predef.Const.apply[Int](42)(...)}
    assertDoesNotCompile(""" q match { case ir"0 -> 'ok // $x " => } """) // Error:(15, 20) Quasiquote Error: Illegal hole position for: $x
  }
  
  test("Shadowing") {
    
    val q = ir"(x:Int) => (x:String) => x"
    q.erase match {
      case ir"(y:Int) => $b: $t" =>
        eqt(t.rep, typeRepOf[String => String])
        eqt(b: Q[_, {val y: Int}], ir"(z: String) => z")
    }
    
  }
  
  test("Type Ascription") {
    
    (ir"42": Q[_,_]) match {
      case ir"$x: Double" => ???
      case ir"$x: Int" =>
        assert(x === ir"42")
    }
    
    val n = ir"42"
    val m = ir"42:Int"
    assert(n =~= m)
    assert(ir"$n * $m" =~= ir"$m * $n")
    
    assert(ir"(x: Int) => x + $n" =~= ir"(x: Int) => x + $m")
    
    ir"42" eqt ir"42: Int"
    ir"42" eqt ir"42: Any"
    
  }
  
  test("Methods") {
    
    ir"42.toDouble" match {
      case ir"($x: Int).toDouble" =>
        assert(x =~= ir"42")
    }
    
    val t = ir"42.toDouble"
    val s = ir".5 * $t"
    
    s match {
      case ir"($a: Double) * ($b: Double)" =>
        assert(a =~= ir"0.5")
        assert(b =~= ir"42.toDouble")
    }
    
    ir" ??? " match { case ir" ??? " => }
    
    ir"((x: Int) => println(x): Any)(0)" eqt ir"((x: Int) => println(x): Any)(0)"
    
  }
  
  test("Free Variables") {
    
    //assert(ir"$$x" =~= ir"$$x") // Warning:(66, 12) Type inferred for hole 'x' was Nothing. Ascribe the hole explicitly to remove this warning.
    assert(ir"$$x:Nothing" =~= ir"$$x:Nothing")
    assert(ir"$$x:Int" =~= ir"$$x:Int")
    assert(ir"($$x:Int)+1" =~= ir"($$x:Int)+1")
    
  }
  
  test("Construction Unquotes in Extractors") {
    
    val x = ir"42"
    ir"(42, 666)" match {
      case ir"($$x, 666)" => 
    }
    
    /*
    // Note: the following syntax is not special-cased (but could be):
    val xs = Seq(ir"1", ir"2")
    ir"List(1, 2)" match {
      case ir"($$xs*)" => 
    }
    */
  }
  
  test("Extracted Binders") {
    
    ir"val x = 42; x + 1" match {
      case ir"val $y: Int = $v; $b" =>
        
        // (???) Note:
        //y [ Int IR Any{val y: Int} ] // Error:(95, 19) No TypeTag available for scp.TestDSL2.IR[Int,Any{val y: Int}]
        
        var yt : Int IR Any{val y: Int} = null
        yt = y // 'y' has ^ this exact type
        
        eqt(y, ir"$$y: Int", false) // 'y' is not a mere hole/FV, it's a term that will only extract exactly the corresponding original binder
        eqt(b, ir"$y + 1", false) // 'b' really has a FV instead of a bound reference to 'y'
        eqt(b, ir"($$y:Int) + 1")
        b match { case ir"$$y + 1" => } // but the FV in 'b' remebers its original binder 
        b match { case ir"$$y + ($c: Int)" => eqt(c, ir"1") }
        ir"readInt + 1" match { case ir"$$y + 1" => fail  case ir"($y:Int) + 1" => eqt(y, ir"readInt") }
        
        val newB = ir"$y + 2"
        newB match { case ir"$$y + 2" => }
        
    }
    
    ir"(x: Int) => x + 1" match {
      case ir"($y: Int) => $b" =>
        b match { case ir"$$y + 1" => }
    }
    
    assertDoesNotCompile(""" (??? : IR[Int,{}]) match { case ir"val _ = $v: Int; $b" => } """) // Error:(129, 38) Quasiquote Error: All extracted bindings should be named.
    //assertDoesNotCompile(""" (??? : IR[Int=>Int,{}]) match { case ir"(_: Int) => $b: Int" => } """) // Error:(129, 42) Quasiquote Error: All extracted bindings should be named.
    
  }
  
  test("Advanced Extracted Binders") {
    import base._
    
    val q = ir"val x = 42; x + 1"
    
    q match {
      case ir"val ${y @ IR(RepDef(bv @ BoundVal(name)))}: $t = $v; $b" =>
        same(name, "x")
        //eqt(bv.typ, constType(42)) // No more the case -- now the type of let bindings is not narrowed from the value in ModularEmbedding
        eqt(bv.typ, typeRepOf[Int])
    }
    
    //assertDoesNotCompile(""" q match { case ir"val ${IR(t)}: Int = $v; $b" => } """) // Error:(132, 20) Quasiquote Error: All extracted bindings must be named. In: ${IR((t @ _))}
    
  }
  
  /*test("Extracted Binders for Multi-Parameter Lambdas") { // TODO
    
    ir"(a: Int, b: Int) => a+b" match {
      case ir"($x: Int, $y: Int) => $b" =>
        show(x,y)
    }
    
  }*/
  
  test("Extraction with null and ???") {
    import base._
    
    ir"val str: String = null; str.length" matches {
      case ir"val str: String = $v; $body: Int" =>
        v eqt ir"null"
    } and {
      case ir"val str: $t = $v; $body: Int" =>
        //eqt(t, irTypeOf[String])
        // ^ Not working because of mergign of -String with +Null(null) from the arg; cf. ScalaTyping 
        eqtBy(t, irTypeOf[String], false)(_ =:= _)
    } and {
      case ir"val ${x @ IR(RepDef(bv:BoundVal))}: $t = $v; $body: Int" =>
        eqt(bv.typ, typeRepOf[String]) // Not working because of mergign of -String with +Null(null) from the arg; cf. ScalaTyping 
    }
    
    ir"val str: String = ???; str.length" matches {
      case ir"val str: String = $v; $body: Int" =>
        v eqt ir"???"
    } and {
      case ir"val str: $t = $v; $body: Int" =>
        //eqt(t, irTypeOf[String])
        // ^ Not working because of mergign of -String with +Null(null) from the arg; cf. ScalaTyping 
        eqtBy(t, irTypeOf[String], false)(_ =:= _)
    } and {
      case ir"val ${x @ IR(RepDef(bv:BoundVal))}: $t = $v; $body: Int" =>
        eqt(bv.typ, typeRepOf[String]) // Not working because of mergign of -String with +Null(null) from the arg; cf. ScalaTyping 
    }
    
  }
  
}














