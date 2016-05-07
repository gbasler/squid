package scp
package feature

import org.scalatest.Assertions

class Antiquotation extends MyFunSuite {
  import TestDSL._
  
  val n = dsl"42"
  
  test("Term Unquote") {
    
    eqt(dsl"$n.toDouble + $n : Double", dsl"42.toDouble + 42")
    
  }
  
  test("Type Unquote") {
    
    val t = QuotedType[Int](typeRepOf[Int])
    dsl"(42,43)".erase match {
      case dsl"($x: $$t, $y:$s)" =>
        eqt(x.trep, typeRepOf[Int])
        eqt(s.rep, x.trep)
    }
    
  }
  
  test("Escaped Term Unquote") {
    
    dsl"(42,42)".erase match {
      case dsl"($$n, $m: Int)" => eqt(m, n)
    }
    dsl"println(42)" match {
      case dsl"println($$n)" =>
    }
    
    assertDoesNotCompile(""" dsl"$$" """) // scp.quasi.EmbeddingException: Empty escaped unquote name: '$$'
    
  }
  
  test("Escaped Type Unquote") {
    
    eqt(dsl"Option.empty[String]", dsl"scala.Option.empty[String]")
    
    dsl"scala.Option.empty[Int]".erase match {
      case dsl"scala.Option.empty[$t]" =>
        eqt(t.rep, typeRepOf[Int])
        dsl"Option.empty[String]" matches {
          case dsl"scala.Option.empty[$$t]" => fail
          case dsl"scala.Option.empty[$t]" => eqt(t.rep, typeRepOf[String])
        }
        dsl"Option.empty[Int]" matches {
          case dsl"scala.Option.empty[$$t]" =>
        }
    }
    
  }
  
  test("Alternative Unquote Syntax") { // Note: probably never useful; TODO rm
    
    val (x,y) = (dsl"1", dsl"2")
    
    dsl"println(1,2)" match {
      case dsl"println($$(x,y))" =>
    }
    
    var count = 0
    
    dsl"$${count += 1; 42}" matches {
      case dsl"$${count += 1; x}" => fail
      case dsl"$${count += 1; n}" =>
    } and {
      case dsl"$${count += 1; 42}" =>
    }
    
    // Note: this does not work: Error:(60, 12) Embedding Error: not found: value m$macro$50
    //and { case dsl"$${count += 1; $m}" => }
    
    assert(count == 4)
    
  }
  
}



