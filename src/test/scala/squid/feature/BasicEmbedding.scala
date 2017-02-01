package squid
package feature

import utils.Bool

object BasicEmbedding {
  
  case class MC[A](x:Int)(syms: A*)
  
  def foo(n: Int)(s: String) = s * n
  
  
  class ClassA { outer =>
    class ClassA {
      import TestDSL.Predef._
      
      def foo1 = ir"outer.bar"
      def foo2 = ir"bar"
      def bar = 0
      //def runFoo = foo.run // TODO
      
    }
    def bar = 1
  }
  
  //val clA = new ClassA // Note: does not work (widens the type, since it is not a module...)
  object clA extends ClassA
  
  object clAclA extends clA.ClassA
  
  
}
class BasicEmbedding extends MyFunSuite {
  import BasicEmbedding._
  
  import TestDSL.Predef._
  
  test("Trivial") {
    
    ir"42" matches {
      case ir"42" =>
    }
    ir"42.toDouble + .5" matches {
      case ir"42.toDouble + 0.5" =>
    }
    
  }
  
  test("Static objects and classes (java.lang.Math, String)") {
    
    val pow23 = ir"Math.pow(2, 3)"
    
    pow23 match {
      case ir"java.lang.Math.pow(2,3)" =>
    }
    pow23 match {
      case ir"java.lang.Math.pow(${Const(2)}, ${Const(3)})" =>
    }
    pow23 match {
      case ir"java.lang.Math.pow(${Const(m)}, ${Const(n)})" =>
        same(m,2)
        same(n,3)
    }
    pow23 match {
      case ir"java.lang.Math.pow($m, $n)" =>
        eqt(m,ir"2.0")
        eqt(n,ir"3.0")
    }
    
    ir"String valueOf true" matches {
      case ir"java.lang.String.valueOf(true)" =>
    }
    
  }
  
  test("New") {
    
    ir"new MC[Nothing](42)(???)" match {
      case ir"new MC(42)()" => fail // warns NO MORE (TODO?)
      case ir"new MC[Nothing](42)()" => fail
      //case ir"new MC[Nothing](42)($x)" => // warns
      case ir"new MC(42)($x:Nothing)" => // warns NO MORE (TODO?)
      case ir"new MC[Nothing](42)($x:Nothing)" => // ok
    }
    
    //dbg_ir"new MC(42)('ok, 'ko)" // TODO look at gen'd code and reduce
    val mc = ir"new MC(42)('ok, 'ko)"
    
    mc matches {
      case ir"new MC(42)('ok, 'ko)" =>
    } and {
      case ir"new MC($n)('ok, Symbol($str))" =>
        eqt(n, ir"42")
        eqt(str, ir"${Const("ko")}")
    } and {
      //case ir"new MC($n)($a, $b)" => fail // should generate a warning
      case ir"new MC[$t]($n)($a, $b)" =>
    } and {
      case ir"$mc: MC[$t]" =>
        eqt(t.rep, typeRepOf[Symbol])
        eqt(mc.trep, typeRepOf[MC[Symbol]])
    }
    
    assertTypeError(""" mc match { case ir"new $ab" => } """) // Embedding Error: trait ab is abstract; cannot be instantiated
    
  }
  
  test("This References") {
    
    val aa = new clA.ClassA
    
    import base.Quasicodes._
    
    //aa.foo1 eqt ir{$$[clA.type](Symbol("ClassA.this")).bar} // ir"`ClassA.this`.bar" and ir"`ClassA.this`.bar" are not equivalent
    /* ^ the type for foo1's hole as seen from inside ClassA is `ClassA.this.type`;
     * however, when used from here apparently becomes `scp.feature.BasicEmbedding.ClassA`... */
    
    aa.foo1 eqt ir{$$[ClassA](Symbol("ClassA.this")).bar}
    
    intercept[NullPointerException](aa.foo1 subs Symbol("ClassA.this") -> ir"null" run)
    same(aa.foo1 subs Symbol("ClassA.this") -> ir"clA" run, 1)
    
    aa.foo2 eqt ir{$$[clA.ClassA](Symbol("ClassA.this")).bar}
    
    assertDoesNotCompile(""" same(aa.foo2 subs Symbol("ClassA.this") -> ir"clAclA" run, 0) """) // Error:(115, 48) Cannot substitute free variable `ClassA.this: aa.type` with term of type `scp.feature.BasicEmbedding.clAclA.type`
    same(clAclA.foo2 subs Symbol("ClassA.this") -> ir"clAclA" run, 0)
    
  }
  
  test("Methods") {
    import collection.mutable.Stack
    
    ir"Stack[Int](1,42,2).push(0)" matches {
      case ir"Stack(1,$n,2).push($m)" =>
        n eqt ir"42"
        m eqt ir"0"
    }
    
    ir"Stack(42).map(_+1)" matches {
      case ir"Stack[$ta]($n).map($f: ta => $tb)" => eqt(f.trep, typeRepOf[Int => Int])
    }
    
    ir"Stack[Int](42).map(_+1).isEmpty" matches {
      case ir"Stack[Int](42).map(_+1).isEmpty" => // TODO
    } and {
      case ir"Stack[$ta]($n).map($f: ta => $tb).isEmpty" =>
        eqt(ta.rep, typeRepOf[Int])
        eqt(tb.rep, typeRepOf[Int])
        eqt(f.typ.rep, typeRepOf[Int => Int])
    }
    
  }
  
  test("Curried Functions") {
    
    val x = ir"""foo(42)("ok")"""
    
    //x match { case ir"($f: Int => String)($s)" => println(f) } // nope (normal)
    
    x match {
      case ir"foo($n)($s)" =>
        assert(n =~= ir"42")
        assert(s =~= ir""" "ok" """)
    }
    
  }
  
  test("Array's & ClassTag's") {
    ir"""Array.fill(3)("woof")""".erase match {
      case ir"Array.fill[String]($_)($_)($ev)" =>
        assert(ev =~= ir"scala.reflect.ClassTag[String](${ Const(classOf[String]) })")
        assert(ev.run === scala.reflect.classTag[String]) // works!
        
        import scala.reflect.ClassTag /* Note: weirdly, when acribing the pattern with the scrutinee type,
          Scala does _not_ prefix ClassTag, which leads to an incompatible scrutinee type warning if this import is not made... */
        ev match {
          case ir"scala.reflect.ClassTag($cls:Class[String])" => // Note: ClassTag[String] not necessary...
            eqt(cls.typ, irTypeOf[Class[String]])
            same(cls.run, classOf[String])
        }
        
      //case ir"Array.fill[$t]($_)($_)($ev)" =>
      // ^ Note: used to throw: Error:(131, 12) Embedding Error: Unknown type `Array[t]` does not have a TypeTag to embed it as uninterpreted.
      //   Made it:             Error:(132, 12) Embedding Error: Unsupported feature: Arrays of unresolved type.
    }
    same(ir"classOf[Int]" run, classOf[Int])
    same(ir"classOf[base.Quasiquotes[_]]" run, classOf[base.Quasiquotes[_]])
    // ^ Note: this generates `classOf[scp.quasi.QuasiBase$Quasiquotes]`, and funnily it seems to be valid Scala.
  }
  
  test("List, Option") {
    
    ir"Some(1.2)" matches {
      //case ir"Some($_)" => fail  // warns... twice..
      case ir"Some[Nothing]($_:Nothing)" => fail // equivalent to the one above, but does not generate a warning
      //case ir"Some[Any]($_)" => fail // method type args are seen as invariant (we maybe could do better but it'd require non-trivial analysis)
      case ir"Some[Any]($_)" => // now method type args are seen as covariant
    } and {
      case ir"Some[AnyVal]($_)" => // now method type args are seen as covariant
    } and {
      case ir"Some[Double]($_)" =>
    }
    ir"Option.empty: Option[Double]" match {
      //case ir"Option.empty[Double]" => fail // FIXME?
      case ir"$_: Option[Double]" => 
    }
    ir"Option.empty".erase match {
      //case ir"Option.empty[Double]" => fail // FIXME?
      case ir"$_: Option[Double]" => 
    }
    ir"Option(3.4)" match {
      case ir"Option[Double]($_)" => 
    }
    
    val ls = ir"List(Some(1.2),Option(3.4),Option.empty,None)"
    ls match {
      case ir"List($_,$_,None,None)" => fail
      //case ir"List(Some[Any]($_),Option[Any]($_),Option.empty[Any],None)" =>
      case ir"List[Option[Double]](Some[Double]($_),Option[Double]($_),($_:Option[Double]),None)" =>
    }
    ls.erase match {
      case ir"$_: List[Nothing]" => fail
      case ir"$_: List[AnyVal]" => fail
      case ir"$_: List[Any]" =>
    }
    ls.erase match {
      case ir"$ls: List[$t]" => assert(t.rep =:= typeRepOf[Option[Double]])
    }
  }
  
  
  test("Imports") {
    
    ir"import scala._; List(1,2,3)" eqt ir"List(1,2,3)"
    
    ir"import scala.collection.mutable; mutable.ArrayBuffer(1,2,3)" eqt
           ir"scala.collection.mutable         .ArrayBuffer(1,2,3)"
    
    //ir"{import scala._}; 42" eqt ir"(); 42" // warns: Warning: a pure expression does nothing in statement position; you may be omitting necessary parentheses
    ir"val a = {import scala._}; 42" eqt ir"42"
    
    assertDoesNotCompile(""" ir"import scala._" """) // Error:(234, 13) Embedding Error: Embedded tree has no type: import scala._
    
  }
  
  
  test("Null/Default Values") {
    
    assert(nullValue[Unit] == ir"()")
    assert(nullValue[Bool] == ir"false")
    assert(nullValue[Char] == ir"'\u0000'")
    assert(nullValue[Byte] == Const(0:Byte))
    assert(nullValue[Short] == Const(0:Short))
    assert(nullValue[Int] == ir"0")
    assert(nullValue[Long] == ir"0L")
    assert(nullValue[Float] == ir"0F")
    assert(nullValue[Double] == ir"0D")
    assert(nullValue[Null] == ir"null")
    assert(nullValue[String] == ir"null")
    
  }
  
  
}






