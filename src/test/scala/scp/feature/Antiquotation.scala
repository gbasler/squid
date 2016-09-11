package scp
package feature

class Antiquotation extends MyFunSuite2 {
  import TestDSL2.Predef._
  import TestDSL2.Quasicodes._
  
  val n = ir"42"
  
  test("Term Unquote") {
    
    eqt(ir"$n.toDouble + $n : Double", ir"42.toDouble + 42")
    
  }
  
  test("Type Unquote") {
    
    val t = TestDSL2.`internal IRType`[Int](typeRepOf[Int])
    ir"(42,43)".erase match {
      case ir"($x: $$t, $y:$s)" =>
        subt(x.trep, typeRepOf[Int])
        subt(x.trep, s.rep)
    }
    
  }
  
  test("Escaped Term Unquote") {
    
    ir"(42,42)".erase match {
      case ir"($$n, $m: Int)" => eqt(m, n)
    }
    ir"println(42)" match {
      case ir"println($$n)" =>
    }
    
    assertDoesNotCompile(""" ir"$$" """) // scp.quasi.EmbeddingException: Empty escaped unquote name: '$$'
    
  }
  
  test("Escaped Type Unquote") {
    
    eqt(ir"Option.empty[String]", ir"scala.Option.empty[String]")
    
    ir"scala.Option.empty[Int]".erase match {
      case ir"scala.Option.empty[$t]" =>
        eqt(t.rep, typeRepOf[Int])
        ir"Option.empty[String]" matches {
          case ir"scala.Option.empty[$$t]" => fail
          case ir"scala.Option.empty[$t]" => eqt(t.rep, typeRepOf[String])
        }
        ir"Option.empty[Int]" matches {
          case ir"scala.Option.empty[$$t]" =>
        }
    }
    
  }
  
  test("Alternative Unquote Syntax") { // Note: probably never useful; rm syntax?
    
    val (x,y) = (ir"1", ir"2")
    
    assertDoesNotCompile(""" ir"println($$(x,y))" """) // Error:(68, 5) Quasiquote Error: Vararg splice unexpected in that position: $(x, y)
    
    assertDoesNotCompile("""
      ir"println(1,2)" match {
        case ir"println($$(x,y))" =>
      }
    """) // Error:(64, 12) Quasiquote Error: Vararg splice unexpected in that position: $(x, y)
    
    eqt(ir{List(${Seq(x,y):_*})}, ir"List(1,2)")
    eqt(ir"List($$(x,y))", ir"List(1,2)")
    
    val seq = Seq(x,y)
    
    assertDoesNotCompile(""" ir{println($(seq:_*))} """) // Error:(87, 7) Quasiquote Error: Vararg splice unexpected in that position: ((seq): _*)
    assertDoesNotCompile(""" ir"println(${seq:_*})" """) // Error:(87, 7) Quasiquote Error: Vararg splice unexpected in that position: ((seq): _*)
    
    eqt(ir"($$x:Int)+$x", ir"($$x:Int)+1")
    
    assert(ir"($$x:Int, $$y:Int)".rep.extractRep(ir"(1,2)".rep).get._1 === Map("x" -> ir"1".rep, "y" -> ir"2".rep))
    assert(ir"( $x,      $y    )".rep.extractRep(ir"(1,2)".rep).get._1 === Map())
    
    ir"List(1,2)" match { case ir"List($$(x,y))" => }
    
    
    val p = ir"println(1,2)"
    assertDoesNotCompile(""" p match { case ir"println($$(x,y))" => } """) // Error:(147, 12) Quasicode Error: Vararg splice unexpected in that position: $(x, y)
    assertDoesNotCompile(""" p match { case ir"println($$(seq:_*))" => } """)
    p match { case ir"println($$x,$$y)" => }
    
    var count = 0
    
    ir"$${count += 1; ir{42}}" matches {
      case ir"$${count += 1; x}" => fail
      case ir"$${count += 1; n}" =>
    } and {
      case ir"$${count += 1; ir{42}}" =>
    }
    
    assertDoesNotCompile(""" (??? : IR[Int,_]) match { case ir"$${count += 1; $m}" => } """) // Error:(159, 36) Quasiquote Error: Illegal hole position for: $m
    
    assert(count == 4)
    
  }
  
}



