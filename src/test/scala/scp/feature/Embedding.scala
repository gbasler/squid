package scp
package feature

import org.scalatest.FunSuite

class Embedding extends FunSuite {
  import Embedding._
  
  import TestDSL._
  
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
    
    dsl"Some(1.2)".erase match {
      case dsl"Some[Double]($_)" =>
    }
    dsl"Some(1.2)".erase match {
      case dsl"Some($_)" => fail
      case dsl"Some[Nothing]($_)" => fail
      case dsl"Some[Any]($_)" =>
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
      case dsl"List(Some[Any]($_),Option[Any]($_),Option.empty[Any],None)" =>
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






