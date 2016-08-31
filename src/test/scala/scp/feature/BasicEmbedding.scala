package scp
package feature

object BasicEmbedding {
  
  case class MC[A](x:Int)(syms: A*)
  
  def foo(n: Int)(s: String) = s * n
  
}
class BasicEmbedding extends MyFunSuite2 {
  //import BasicEmbedding._ // FIXME class loading
  import Dummies.BasicEmbedding._
  
  import TestDSL2.Predef._
  
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
  
  //test("New") { // FIXME new
  //  
  //  
  //  ir"new MC[Nothing](42)(???)" match {
  //    //case ir"new MC(42)()" => // warns
  //    case ir"new MC[Nothing](42)()" => fail
  //    //case ir"new MC[Nothing](42)($x)" => // warns
  //    //case ir"new MC(42)($x:Nothing)" => // warns
  //    case ir"new MC[Nothing](42)($x:Nothing)" => // ok
  //  }
  //  
  //  //dbgir"new MC(42)('ok, 'ko)" // TODO look at gen'd code and reduce
  //  val mc = ir"new MC(42)('ok, 'ko)"
  //  
  //  mc matches {
  //    case ir"new MC(42)('ok, 'ko)" =>
  //  } and {
  //    case ir"new MC($n)('ok, Symbol($str))" =>
  //      eqt(n, ir"42")
  //      eqt(str, ir"${"ko"}")
  //  } and {
  //    //case ir"new MC($n)($a, $b)" => fail // should generate a warning
  //    case ir"new MC[$t]($n)($a, $b)" =>
  //  } and {
  //    case ir"$mc: MC[$t]" =>
  //      eqt(t.rep, typeRepOf[Symbol])
  //      eqt(mc.trep, typeRepOf[MC[Symbol]])
  //  }
  //  
  //  assertTypeError(""" mc match { case ir"new $ab" => } """) // Embedding Error: trait ab is abstract; cannot be instantiated
  //  
  //}
  
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
        
      //case ir"Array.fill[$t]($_)($_)($ev)" =>
      // ^ Note: used to throw: Error:(131, 12) Embedding Error: Unknown type `Array[t]` does not have a TypeTag to embed it as uninterpreted.
      //   Made it:             Error:(132, 12) Embedding Error: Unsupported feature: Arrays of unresolved type.
    }
  }
  
  test("List, Option") {
    
    ir"Some(1.2)" matches {
      //case ir"Some($_)" => fail  // warns... twice..
      case ir"Some[Nothing]($_:Nothing)" => fail // equivalent to the one above, but does not generate a warning
      //case ir"Some[Any]($_)" => fail // method type args are seen as invariant (we maybe could do better but it'd require non-trivial analysis)
      //case ir"Some[Any]($_)" => // now method type args are seen as covariant // FIXME Any
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
  
}






