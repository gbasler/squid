package scp
package feature

import org.scalatest.ShouldMatchers

import utils.Debug._

class TypeMatching extends MyFunSuite with ShouldMatchers {
  import TypeMatching._
  import TestDSL._
  
  test("Trivial") {
    dsl"42" matches {
      case dsl"$x: Any" => eqt(x, dsl"42")
    } and {
      case dsl"$x: $t" =>
        eqt(t.rep, typeRepOf[Int])
        eqt(t.rep, x.trep)
        
        val opt = dsl"Option.empty[$t]" // TODO
        eqt(opt, dsl"Option.empty[Int]")
        eqt(opt.trep, typeRepOf[Option[Int]])
    }
  }
  
  test("Matching Nothing Explicitly") {
    dsl"Seq()".erase match {
      //case dsl"Seq()" => // should generate a warning
      case dsl"Seq[Nothing]()" => // should NOT generate a warning
    }
  }
  
  val f = dsl"(x: Int) => x.toDouble".erase
  
  test("Lambda Parameter/Body Types") {
    f matches {
      case dsl"(_: $xt) => $body: $bt" =>
        eqt(xt.rep, typeRepOf[Int])
        eqt(bt.rep, typeRepOf[Double])
        eqt(body.trep, typeRepOf[Double])
    } and {
      case dsl"(y: Nothing) => $body: Double" =>
        eqt(body, dsl"($$y: Int).toDouble")
        assertDoesNotCompile(""" dsl"val y = 42; $body".run """)
    }
  }
  
  test("Function Types") {
    f match {
      case dsl"$_: (Double => Double)" => fail
      case dsl"$_: ($a => $b)" =>
        assert(a.rep =:= typeRepOf[Int])
        assert(b.rep =:= typeRepOf[Double])
    }
    f match {
      case dsl"$_: (Int => $b)" =>
        assert(b.rep =:= typeRepOf[Double])
    }
    f match {
      case dsl"$_: ($a => Double)" =>
        assert(a.rep =:= typeRepOf[Int])
    }
    f match {
      case dsl"$_: (Any => Double)" => fail
      case dsl"$_: (Int => Any)" =>
    }
    
    dsl"Some[Int=>Int](_+1)" matches {
      case dsl"Some($f: ($ta => $tb))" =>
        //println(ta,tb)
        eqt(ta.rep, typeRepOf[Int])
        eqt(tb.rep, typeRepOf[Int])
    }
    dsl"((x:Int)=>x+1,(x:Int)=>x-1)" matches {
      case dsl"($_: ($ta => $tb), $_: (ta => tb))" => eqt(ta.rep,tb.rep)
    } and {
      case dsl"($_: ($ta => tb), $_: (ta => $tb))" => eqt(ta.rep,tb.rep)
    }
  }
  
  test("Extracting from Nothing") (
    
    dsl"Option.empty" matches {
      case dsl"$y: Option[Int]" =>
    } and {
      case dsl"$y: Option[$t]" => assert(t.rep =:= typeRepOf[Nothing])
    } and {
      case dsl"$y: Option[Option[$t]]" => assert(t.rep =:= typeRepOf[Nothing])
    }
    and { case dsl"Option.empty[$t]" => eqt(t.rep, typeRepOf[Nothing]) }
    and { case dsl"Option.empty[Any]" => } // cf: covariant method type argument matching
    and { case dsl"Option.empty[Nothing]" => }
    
  )
  
  
  test("Type Params & Variance") {
    
    dsl"List(42)" matches ({
      case dsl"$x: List[Any]" => eqt(x, dsl"List(42)")
    }, {
      case dsl"$_: List[$t]" => eqt(t.rep, typeRepOf[Int])
    })
    
    dsl"List(List(42))" matches ({
      case dsl"$_: List[List[$t]]" => eqt(t.rep, typeRepOf[Int])
    }, {
      case dsl"$_: Seq[Seq[Any]]" =>
    })
    
    // TODO: test functions, contravariance and invariance...
    
  }
  
  // Note: defining 'Expr' and 'Appl' here used to work; no more since symbols are now loaded dynamically from the owner chain
  
  test("GADT") {
    //lang.ScalaTyping.debugTypes(
    
    val applid = dsl"Appl[Int,Double]".erase
    applid match {
      case dsl"$a: Appl[$s,$t]" =>
        assert(s.rep =:= typeRepOf[Int])
        assert(t.rep =:= typeRepOf[Double])
      //case _ => fail
    }
    applid match {
      case dsl"$a: Appl[Any,Any]" =>
        assert(a =~= applid)
      //case _ => fail
    }
    applid match {
      case dsl"$a: Expr[$t]" =>
        assert(t.rep =:= typeRepOf[Double])
    }
    
    val abstis = dsl"Abst[Int,String]".erase
    abstis match { case dsl"$_ : Expr[Int => String]" => }
    abstis match { case dsl"$_ : Expr[Nothing => Any]" => }
    abstis match { case dsl"$_ : Expr[Any]" => }
    
    val abstabst = dsl"Abst[Abst[Int,String], Abst[Int,String]]".erase
    abstabst match {
      case dsl"$_ : Expr[Abst[Int,String] => Abst[Int,String]]" =>
    }
    abstabst match {
      case dsl"$_ : Expr[Abst[Int, String] => Expr[Int => String]]" =>
    }
    abstabst match {
      case dsl"$_ : Expr[Abst[Any, Nothing] => Expr[Nothing => Any]]" =>
    }
    
    dsl"(x: Appl[Int,Double]) => ()".cast[_ => Unit] match {
      case dsl"$_: (Appl[Int,Double] => Unit)" =>
    }
    
    dsl"(x: Appl[Int,Int]) => Int".cast[_ => _] match {
      case dsl"$_: (Appl[Int,Nothing] => Any)" =>
    }
    
    dsl"(x: Appl[Int,Double]) => Appl[Double,Int]".cast[_ => _] match {
      case dsl"$_: (Expr[$t1] => $t2)" => fail
      case dsl"$_: (Appl[$a,$b] => Expr[$t])" =>
        assert(a.rep =:= typeRepOf[Int])
        assert(b.rep =:= typeRepOf[Double])
        assert(t.rep =:= typeRepOf[Int])
    }
    
    dsl"(x: Expr[Int]) => ()".cast[_ => _] match {
      case dsl"$_: (Appl[String,Int] => Any)" =>
    }
    dsl"(x: Expr[Int]) => ()".cast[_ => _] match {
      case dsl"$_: (Appl[String,$t] => Unit)" =>
        eqt(t.rep, typeRepOf[Int])
    }
    
  }
  
}

object TypeMatching {
  class Expr[+A]
  class Abst[-A,+B] extends Expr[A => B]
  object Abst { def apply[A,B] = new Abst[A,B] }
  class Appl[+A,+B] extends Expr[B]
  object Appl { def apply[A,B] = new Appl[A,B] }
}  


