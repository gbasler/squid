package squid
package feature

class FreeVariablesNewSyntax extends MyFunSuite {
  
  import TestDSL.Predef._
  
  test("Simple") {
    
    import TestDSL.Quasicodes._
    
    val model = ir"${base.Code[Int](base.freeVar("x",typeRepOf[Int]))} + 1"
    ir"(?x : Int) + 1" eqt model
    ir{(?x : Int) + 1} eqt model
    
    assertDoesNotCompile("ir{ println(?) }") // Error: Quasiquote Error: Unknown use of free variable syntax operator `?`.
    assertDoesNotCompile("ir{ println(?.selectDynamic(42.toString)) }") // Error:(18, 7) Embedding Error: Free variable introduced with `?` should have a constant literal name.
    
    // Note: old FV syntax currently still works:
    ir"(x? : Int) + 1" eqt model
    ir"(x?:Int)+1"     eqt model
    
  }
  
  test("Explicit Free Variables") {
    
    val x: Q[Int,{val x: Int}] = ir"?x : Int"
    assert(x.rep match {
      case base.RepDef(base.Hole("x")) => true  // Note: no `base.Ascribe` node because ascriptions to the same type are removed
      case _ => false
    })
    
    val d = ir"$x.toDouble" : Q[Double, {val x: Int}]
    
    val s = ir"(?str : String) + $d" : Q[String, {val x: Int; val str: String}]
    
    val closed = ir"(str: String) => (x: Int) => $s" : Q[String => Int => String, {}]
    val closed2 = ir"(x: Int) => (str: String) => $s" : Q[Int => String => String, {}]
    
    assert(closed =~= ir"(a: String) => (b: Int) => a + b.toDouble")
    assert(closed2 =~= ir"(b: Int) => (a: String) => a + b.toDouble")
    
    assertDoesNotCompile(""" ir"42: $$t" """) // Error:(26, 5) Embedding Error: Free type variables are not supported: '$$t'
    assertDoesNotCompile(""" ir"42: t?" """)  // Error:(26, 5) Failed to parse DSL code: identifier expected but eof found.
    assertDoesNotCompile(""" ir"42: ?t" """)  // Error:(40, 5) Failed to parse DSL code: identifier expected but eof found.
    
  }
  
  test("Rep extraction") {
    hopefully(ir"Some(?x:Int)".rep extractRep ir"Some(42)".rep isDefined)
    hopefully(ir"Some(42)".rep extractRep ir"Some(?x:Int)".rep isEmpty)
  }
  
  test("Term Equivalence") {
    
    assert(ir"(?x: Int)" =~= ir"(?x: Int)")
    assert(!(ir"(?x: Int)" =~= ir"(?y: Int)"))
    
    assert(ir"(?x: Int)" =~= ir"(?x: Int):Int")
    assert(!(ir"(?x: Int)" =~= ir"(?y: Int)+1"))
    assert(!(ir"(?x: Int)" =~= ir"(?y: String)"))
    
    assert(ir"(?x: Int) + (?y: Int)" =~= ir"(?x: Int) + (?y: Int)")
    
    assert(!(ir"(?x: Int) + (?y: Int)" =~= ir"(?y: Int) + (?x: Int)"))
    
  }
  
  test("Term Equivalence With Bindings And Free Variables") {
    
    ir"val x = readInt; x + (?x: Int)" eqt ir"val y = readInt; y+(?x: Int)"
    ir"val x = readInt; x + (?x: Int)" neqt ir"val y = readInt; (?x: Int)+(?x: Int)"
    ir"val x = readInt; (?x: Int) + (?x: Int)" eqt ir"val y = readInt; (?x: Int) + (?x: Int)"
    ir"val x = readInt; (?x: Int) + (?x: Int)" neqt ir"val y = readInt; y+(?x: Int)"
    ir"val x = readInt; (?x: Int) + (?x: Int)" neqt ir"val x = readInt; x+(?x: Int)"
    
  }
  
  test("Ascription and Hole Types are Checked") {
    import base.hole
    
    val N = typeRepOf[Nothing]
    
    hopefullyNot(ir"?str:String" =~=  ir"?str:Any")
    hopefullyNot(ir"?str:String" =~= base.`internal IR`(hole("str", N)))
    
    hopefully(hole("str", N) =~=  hole("str", N))
    eqt( (hole("str", typeRepOf[Any]) extractRep hole("str", N)).get._1("str"), hole("str", N) )
    hopefullyNot(hole("str", N) =~=  hole("str", typeRepOf[Int]))
    hopefullyNot(hole("str", typeRepOf[String]) =~=  hole("str", typeRepOf[Int]))
    
  }
  
  test("Syntax: Sticking the Semi") { // These cases test the previous "new" FV syntax `x?`
    
    ir"x?: Int" eqt ir"$$x: Int"
    ir"x?: List[Int]" eqt ir"$$x: List[Int]"
    ir"$$x: Int Map String" eqt
      ir"x? : Int Map String"
    // Expected failure:
    //  ir"x?: Int Map String"
    // ^ this yields:
    // Error:(79, 13) Embedding Error: Quoted expression does not type check: value Map is not a member of Int
    // Warning:(79, 13) It seems you tried to annotate a free variable with `:`, but this was interpreted as operator `?:` -- use a space to remove this ambiguity.
    
    // Should raise warning: Warning:(81, 5) It seems you tried to annotate free variable `x` with `:`, which may have been interpreted as operator `?:` -- use a space to remove this ambiguity.
    assertDoesNotCompile(""" ir"x?: Int Map String" """)
    assertDoesNotCompile(""" ir"x?:Int Map String" """)
    
  }
  
  test("Free Variables in Patterns") {
    
    // TODO implement proper distinction between FVs and extraction holes!
    //ir"x?:Int" matches {
    //  case ir"y?: Int" => fail
    //  case ir"x?: Int" =>
    //} and {
    //  case ir"(${ir"y? : Int"}:Int)+1" => fail
    //  case ir"(${ir"x? : Int"}:Int)+1" =>
    //}
    
    val X = ir"?x : Int"
    val Y = ir"?y : Int"
    
    ir"(?x:Int)+1" matches {
      case ir"($Y:Int)+1" => fail
      case ir"($X:Int)+1" => 
    } and {
      case ir"(${`X`}:Int)+1" =>
    }
    
  }
  
  
}

