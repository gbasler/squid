package scp
package feature

import org.scalatest.FunSuite

class Embedding extends MyFunSuite {
  import Embedding._
  
  import TestDSL._
  
  test("Trivial") {
    
    dsl"42" matches {
      case dsl"42" =>
    }
    dsl"42.toDouble + .5" matches {
      case dsl"42.toDouble + 0.5" =>
    }
    
  }
  
  test("Methods") {
     import collection.mutable.Stack
    
    dsl"Stack[Int](1,42,2).push(0)" matches {
      case dsl"Stack(1,$n,2).push($m)" =>
        n eqt dsl"42"
        m eqt dsl"0"
    }
    
    dsl"Stack(42).map(_+1)" matches {
      case dsl"Stack[$ta]($n).map($f: ta => $tb)" => eqt(f.trep, typeRepOf[Int => Int])
    }
    
    dsl"Stack[Int](42).map(_+1).isEmpty" matches {
      case dsl"Stack[Int](42).map(_+1).isEmpty" => // TODO
    } and {
      case dsl"Stack[$ta]($n).map($f: ta => $tb).isEmpty" =>
        eqt(ta.rep, typeRepOf[Int])
        eqt(tb.rep, typeRepOf[Int])
        eqt(f.typ.rep, typeRepOf[Int => Int])
    }
    
  }
  
  test("Curried Functions") {
    
    val x = dsl"""foo(42)("ok")"""
    
    //x match { case dsl"($f: Int => String)($s)" => println(f) } // nope (normal)
    
    x match {
      case dsl"foo($n)($s)" =>
        assert(n =~= dsl"42")
        assert(s =~= dsl""" "ok" """)
    }
    
  }
  
  test("Array's & ClassTag's") {
    dsl"""Array.fill(3)("woof")""" match {
      case dsl"Array.fill($_)($_)($ev)" =>
        //assert(ev =~= dsl"scala.reflect.classTag[String]") // Toolbox classpath problem? >> Could not find 'classTag' in scala.reflect.package
        assert(ev =~= dsl"scala.reflect.ClassTag[String](${"".getClass})")
        //println(ev.run) // works!
    }
  }
  
  test("List, Option") {
    
    dsl"Some(1.2)" matches {
      case dsl"Some($_)" => fail
      case dsl"Some[Nothing]($_)" => fail
      //case dsl"Some[Any]($_)" => fail // method type args are seen as invariant (we maybe could do better but it'd require non-trivial analysis)
      case dsl"Some[Any]($_)" => // now method type args are seen as covariant
    } and {
      case dsl"Some[Double]($_)" =>
    }
    dsl"Option.empty: Option[Double]" match {
      //case dsl"Option.empty[Double]" => fail // FIXME?
      case dsl"$_: Option[Double]" => 
    }
    dsl"Option.empty".erase match {
      //case dsl"Option.empty[Double]" => fail // FIXME?
      case dsl"$_: Option[Double]" => 
    }
    dsl"Option(3.4)" match {
      case dsl"Option[Double]($_)" => 
    }
    
    val ls = dsl"List(Some(1.2),Option(3.4),Option.empty,None)"
    ls match {
      case dsl"List($_,$_,None,None)" => fail
      //case dsl"List(Some[Any]($_),Option[Any]($_),Option.empty[Any],None)" =>
      case dsl"List[Option[Double]](Some[Double]($_),Option[Double]($_),($_:Option[Double]),None)" =>
    }
    ls.erase match {
      case dsl"$_: List[Nothing]" => fail
      case dsl"$_: List[Any]" =>
    }
    ls.erase match {
      case dsl"$ls: List[$t]" => assert(t.rep =:= typeRepOf[Option[Double]])
    }
  }
  
}
object Embedding {
  
  def foo(n: Int)(s: String) = s * n
  
}






