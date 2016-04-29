package scp
package feature

/**
  * Note: Because of the implementation, we have the same restrictions as Scala's _*
  * For example, dsl"List(0, $args*)" is illegal the same way 'List(1, args:_*)' is illegal
  * It will generate the same error (in the case of QQ, *after* type-checking the deep embedding!):
  *   Error:(13, 14) no ': _*' annotation allowed here
  *   (such annotations are only allowed in arguments to *-parameters)
  *  
  *  With more effort (and support from the IR!), we could introduce the same flexibility as Scala QQ splicing, as in: q"List(0, ..$args)"
  */
class Varargs extends MyFunSuite {
  import TestDSL._
  
  val args = Seq(dsl"1", dsl"2", dsl"3")
  
  val list123 = dsl"List(1,2,3)"
  
  test("No splices in type position") {
    assertDoesNotCompile(""" dsl"Map[Int,Int]()" match { case dsl"Map[$ts*]()" => } """)
  }
  
  test("Simple Vararg Usage") {
    dsl"Seq(1,2,3)" match {
      case dsl"Seq[Int]($a,$b,$c)" => same(Seq(a,b,c), args)
    }
    import Embedding._
    dsl"new MC(42)('ok, 'ko)" match {
      case dsl"new MC(42)($a, $b)" =>
        eqt(a, dsl"'ok")
        eqt(b, dsl"'ko")
    }
  }
  
  test("Vararg Construction and Extraction") {
    dsl"List(Seq(1,2,3): _*)" matches {
      case dsl"List[Int]($xs*)" => fail
      case dsl"List[Int](${xs @ __*})" => fail
      case dsl"List[Int]($xs: _*)" => assert(xs == dsl"Seq(1,2,3)")
    }
    eqt(dsl"List(List(1,2,3): _*)", dsl"List($list123: _*)")
  }
  
  test("Spliced Unquote, Construction") {
    
    val a = dsl"List($args*)"
    eqt(a, list123)
    
    val b = dsl"List(${args: _*})"
    eqt(a, b)
    eqt(b, list123)
    
    val c = dsl"List(${args: __*})"
    eqt(a, c)
    eqt(c, list123)
    
    // Note: same limitations as Scala
    //val a = dbgdsl"List(0,$args*)" // Error:(13, 14) no ': _*' annotation allowed here
    
    // Note: cannot splice free variables (good)
    // dsl"$$xs*" // Error:(31, 6) Embedding Error: value * is not a member of Nothing
    
    val ls = List(1,2,3)
    assert(a.run == ls)
    assert(b.run == ls)
    assert(c.run == ls)
    
  }
  
  test("Spliced Unquote, Extraction") { // TODO
    
    list123 matches {
      case dsl"List[Int]($xs*)" =>
        assert((xs: Seq[Q[Int,_]]) == args)
    } and {
      case dsl"List[Int](${xs @ __*})" =>
        assert((xs: Seq[Q[Int,_]]) == args)
    } and {
      case dsl"List[Int](${Seq(dsl"1", xs @ _*)}*)" =>
        assert((xs: Seq[Q[Int,_]]) == args.tail)
    } and {
      case dsl"List[Int]($xs: _*)" =>
        val seq = dsl"Seq(1,2,3)"
        eqt(xs, seq)
        eqt(xs.trep, seq.trep)
        eqt(xs.trep, typeRepOf[Seq[Int]])
    }
    
    dsl"List(Seq(1,2,3):_*)" match {
      case dsl"List[Int](($xs:Seq[Int]): _*)" =>
        eqt((xs: Q[Seq[Int],{}]), dsl"Seq(1,2,3)")
      case dsl"List[Int]($xs: _*)" =>
        eqt((xs: Q[Seq[Int],{}]), dsl"Seq(1,2,3)")
    }
    dsl"List(Seq(1,2,3):_*)" match {
      case dsl"List[Int](Seq($xs*):_*)" => // FIXME should emit a warning!
      case dsl"List(Seq[Int]($xs*):_*)" =>
        assert((xs: Seq[Q[Int,{}]]) == args)
    }
    
  }
  
}

