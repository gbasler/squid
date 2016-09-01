package scp
package feature

class Run extends MyFunSuite2 {
  
  import TestDSL2.Predef._
  
  test("New") {
    import BasicEmbedding._
    val mc = ir"new MC(42)('ok, 'ko)"
    assert(mc.run == new MC(42)('ok, 'ko))
  }
  
  test("Modules") {
    same(ir"List".run, List)
    same(ir"Nil".run, Nil)
    same(ir"Dummies.Run".run, Dummies.Run)
    same(ir"scp.TestDSL2.Predef".run.base, TestDSL2)
  }
  
  test("Nullary Methods") {
    same(ir"List()".run, List())
    same(ir"List.empty".run, List.empty)
    same(ir"List.canBuildFrom[Int]".run, List.canBuildFrom[Int])
  }
  
  test("Imperative stuff") {
    same(ir"var ls: List[Int] = Nil; ls ::= 1; ls ::= 2; ls ::= 3; ls.reverse.mkString".run, "123")
    same(ir"val bf = List.canBuildFrom[Int](); bf += 1; bf += 2; bf += 3; bf.result".run, List(1,2,3))
  }
  
  test("Functions") {
    
    assert(ir"((x: Int) => x + 1)(42)".run == 43)
    
    val f = ir"(x: Int) => x+1"
    
    assert((f.run apply 42) == 43)
    
    //assert(ir"Run.f(42)".run == 43)  // FIXME class loading
    assert(ir"Dummies.Run.f(42)".run == 43)
    
  }
  
  test("Compile Error On Open Terms") {
    
    assertDoesNotCompile(""" ir"($$x: Int) + 1".run """)
    
    val x = ir"42": Q[Int, {val x: Int}]
    assertDoesNotCompile(""" x.run """)
    
  }
  
  test("n until m") {
    val t = ir"0 until 42"
    assert(t.run == (0 until 42))
  }
  
  // FIXME more principled Constants handling
  //test("Constant Options, Seqs, Sets") {
  //  assert(ir"${Seq(Seq(1,2,3))}".run == Seq(Seq(1,2,3)), None)
  //  assert(ir"${Seq(Some(Set(1,2,3)), None)}".run == Seq(Some(Set(1,2,3)), None))
  //}
  
  test("Array and ClassTag") {
    assert(ir"""Array.fill(3)("woof").toSeq""".run == Array.fill(3)("woof").toSeq)
  }
  
  test("Java Methods") {
    same(ir"""String.valueOf("ok")""".run, String.valueOf("ok"))
    same(ir""" "a" + "b" """.run, "ab")
    same(ir""" "a" + 42 """.run, "a42")
    same(ir""" "ok".length """.run, 2)
    same(ir""" "ok".size """.run, 2)
  }
  
  
}
object Run {
  
  val f = (x: Int) => x + 1
  
}














