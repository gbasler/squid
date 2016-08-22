package scp
package feature

import scp.utils.meta.RuntimeUniverseHelpers
import utils.Debug._

class TypeMatching extends MyFunSuite2 {
  //import TypeMatching._  // FIXME class loading
  import Dummies.TypeMatching._
  import TestDSL2.Predef._
  
  test("Trivial") {
    ir"42" matches {
      //case ir"$x: Any" => eqt(x, ir"42") // FIXME Any
      case ir"$x: AnyVal" => eqt(x, ir"42")
    } and {
      case ir"$x: $t" =>
        eqt(t.rep, base.constType(42))
        eqt(t.rep, x.trep)
        
        val opt = ir"Option.empty[$t]" // TODO
        eqt(opt, ir"Option.empty[Int]")
        assert(!(typeRepOf[Option[Int]] <:< opt.trep))
        subt(opt.trep, typeRepOf[Option[Int]])
    }
  }
  
  test("Matching Nothing Explicitly") {
    ir"Seq()".erase match {
      //case ir"Seq()" => // should generate a warning
      case ir"Seq[Nothing]()" => // should NOT generate a warning
    }
  }
  
  val f = ir"(x: Int) => x.toDouble".erase
  
  test("Lambda Parameter/Body Types") {
    f matches {
      case ir"(_: $xt) => $body: $bt" =>
        eqt(xt.rep, typeRepOf[Int])
        eqt(bt.rep, typeRepOf[Double])
        eqt(body.trep, typeRepOf[Double])
    } and {
      case ir"(y: Nothing) => $body: Double" =>
        assert(!(body =~= ir"(${base.`internal IR`(base.hole("y", typeRepOf[Nothing]))}: Int).toDouble"))
        eqt(body, ir"${base.`internal IR`[Int,{}](base.hole("y", typeRepOf[Int]))}.toDouble")
        eqt(body, ir"($$y: Int).toDouble")
        
        // Note: `body` has type `IR[Double,{val y: Nothing}]` but internally has a hole typed `Int`.
        // This is okay and sound, as the following shows:
        ir"($$y:Int).toDouble" : IR[Double,{val y: Nothing}]
        
        assertDoesNotCompile(""" ir"val y = 42; $body".run """) // Error:(48, 9) Embedding Error: Captured variable `y: Int` has incompatible type with free variable `y: Nothing` found in inserted IR $(body)
    }
  }
  
  test("Function Types") {
    f match {
      case ir"$_: (Double => Double)" => fail
      case ir"$_: ($a => $b)" =>
        assert(a.rep =:= typeRepOf[Int])
        assert(b.rep =:= typeRepOf[Double])
    }
    f match {
      case ir"$_: (Int => $b)" =>
        assert(b.rep =:= typeRepOf[Double])
    }
    f match {
      case ir"$_: ($a => Double)" =>
        assert(a.rep =:= typeRepOf[Int])
    }
    f match {
      case ir"$_: (Any => Double)" => fail
      case ir"$_: (AnyVal => Double)" => fail
      //case ir"$_: (Int => Any)" => // FIXME Any
      case ir"$_: (Int => AnyVal)" =>
    }
    
    ir"Some[Int=>Int](_+1)" matches {
      case ir"Some($f: ($ta => $tb))" =>
        //println(ta,tb)
        eqt(ta.rep, typeRepOf[Int])
        eqt(tb.rep, typeRepOf[Int])
    }
    ir"((x:Int)=>x+1,(x:Int)=>x-1)" matches {
      case ir"($_: ($ta => $tb), $_: (ta => tb))" => eqt(ta.rep,tb.rep)
    } and {
      case ir"($_: ($ta => tb), $_: (ta => $tb))" => eqt(ta.rep,tb.rep)
    }
  }
  
  test("Extracting from Nothing") (
    
    ir"Option.empty" matches {
      case ir"$y: Option[Int]" =>
    } and {
      case ir"$y: Option[$t]" => assert(t.rep =:= typeRepOf[Nothing])
    } and {
      case ir"$y: Option[Option[$t]]" => assert(t.rep =:= typeRepOf[Nothing])
    }
    and { case ir"Option.empty[$t]" => eqt(t.rep, typeRepOf[Nothing]) }
    //and { case ir"Option.empty[Any]" => } // cf: covariant method type argument matching // FIXME Any
    and { case ir"Option.empty[AnyVal]" => } // cf: covariant method type argument matching
    and { case ir"Option.empty[Nothing]" => }
    
  )
  
  
  test("Type Params & Variance") {
    
    ir"List(42)" matches ({
      //case ir"$x: List[Any]" => eqt(x, ir"List(42)") // FIXME Any
      case ir"$x: List[AnyVal]" => eqt(x, ir"List(42)")
    }, {
      case ir"$_: List[$t]" => eqt(t.rep, typeRepOf[Int])
    })
    
    ir"List(List(42))" matches ({
      case ir"$_: List[List[$t]]" => eqt(t.rep, typeRepOf[Int])
    }, {
      //case ir"$_: Seq[Seq[Any]]" => // FIXME Any
      case ir"$_: Seq[Seq[AnyVal]]" =>
    })
    
    // TODO: test functions, contravariance and invariance...
    
  }
  
  
  val AnyType = base.`internal IRType`[Any](base.TypeRep(RuntimeUniverseHelpers.sru.typeOf[Any])) // FIXME Any
  
  
  // Note: defining 'Expr' and 'Appl' here used to work; no more since symbols are now loaded dynamically from the owner chain
  
  test("GADT") {
    //lang.ScalaTyping.debugTypes(
    
    val applid = ir"Appl[Int,Double]".erase
    applid match {
      case ir"$a: Appl[$s,$t]" =>
        assert(s.rep =:= typeRepOf[Int])
        assert(t.rep =:= typeRepOf[Double])
      //case _ => fail
    }
    applid match {
      //case ir"$a: Appl[Any,Any]" => // FIXME Any
      case ir"$a: Appl[AnyVal,AnyVal]" =>
        assert(a =~= applid)
      //case _ => fail
    }
    applid match {
      case ir"$a: Expr[$t]" =>
        assert(t.rep =:= typeRepOf[Double])
    }
    
    val abstis = ir"Abst[Int,String]".erase
    abstis match { case ir"$_ : Expr[Int => String]" => }
    abstis match { case ir"$_ : Expr[Nothing => Any]" => }
    abstis match { case ir"$_ : Expr[Any]" => }
    
    val abstabst = ir"Abst[Abst[Int,String], Abst[Int,String]]".erase
    abstabst match {
      case ir"$_ : Expr[Abst[Int,String] => Abst[Int,String]]" =>
    }
    abstabst match {
      case ir"$_ : Expr[Abst[Int, String] => Expr[Int => String]]" =>
    }
    abstabst match {
      //case ir"$_ : Expr[Abst[Any, Nothing] => Expr[Nothing => Any]]" => // FIXME Any
      case ir"$_ : Expr[Abst[AnyVal, Nothing] => Expr[Nothing => Any]]" =>
    }
    
    ir"(x: Appl[Int,Double]) => ()".cast[_ => Unit] match {
      case ir"$_: (Appl[Int,Double] => Unit)" =>
    }
    
    ir"(x: Appl[Int,Int]) => Int".cast[_ => _] match {
      case ir"$_: (Appl[Int,Nothing] => Any)" =>
    }
    
    ir"(x: Appl[Int,Double]) => Appl[Double,Int]".cast[_ => _] match {
      case ir"$_: (Expr[$t1] => $t2)" => fail
      case ir"$_: (Appl[$a,$b] => Expr[$t])" =>
        assert(a.rep =:= typeRepOf[Int])
        assert(b.rep =:= typeRepOf[Double])
        assert(t.rep =:= typeRepOf[Int])
    }
    
    ir"(x: Expr[Int]) => ()".cast[_ => _] match {
      //case ir"$_: (Appl[String,Int] => Any)" => // FIXME Any
      case ir"$_: (Appl[String,Int] => AnyVal)" =>
    }
    
    ir"(x: Expr[Int]) => ()".cast[_ => _] match {
      case ir"$_: (Appl[String,$t] => Unit)" =>
        eqt(t.rep, typeRepOf[Int])
    }
    
    ir"(x: Expr[Int]) => ()".cast[_ => _] match {
      case ir"$_: (Appl[$t,Int] => Unit)" =>
        eqt(t, AnyType)
    }
    
  }
  
  
  test("Extracted Type Variance & Unification") {
    
    val a = ir"HPair(List(1,2,3),List('a','b'))"
    eqt(a, ir"HPair[List[AnyVal]](List(1,2,3),List('a','b'))")
    
    a matches {
      case ir"HPair[$t]($a,$b)" => eqt(t, irTypeOf[List[AnyVal]])
    } and {
      case ir"HPair($a:$ta,$b)" => // What happens: the pattern is typed:  ir"HPair[ta]($a:$ta,$b)"  so 'ta' is also matched against b, thence is unified to +List[AnyVal]
        assert(!(ta =:= irTypeOf[List[Int]]))
        eqt(ta, irTypeOf[List[AnyVal]])
    }
    
    assertDoesNotCompile(""" a match { case ir"HPair($a:$ta,$b:$tb)" => } """) // Error:(197, 12) Embedding Error: Precise extracted type was lost, possibly because extruded from defining scope, in: <extruded type>
    
    
    val f = ir"(x: List[Any]) => x.asInstanceOf[List[Int]]" : IR[List[Any] => List[Int], {}]
    
    f matches {
      case ir"$g: ($t => t)" => // Our matcher can assign to 't' anything between 'List[Int]' and 'List[Any]'. Currently, it arbitrarily picks the lower bound.
        eqt(t, irTypeOf[List[Int]])
    } and {
      case ir"(_: $t) => $b: t" =>
        
        show(b) // FIXME '_' binding xtion
        
        eqt(t, irTypeOf[List[Int]])
    } and {
      case ir"$g: ($t0 => $t1)" =>
        eqt(t0, irTypeOf[List[Any]])
        eqt(t1, irTypeOf[List[Int]])
    } and {
      case ir"$_: (List[List[$t0]] => Any)" =>
        //show(t0)
        //subt(t0, irTypeOf[Any]) // FIXME Any
        subt(t0, AnyType)
        subt(irTypeOf[Nothing], t0)
    }
    
    // FIXME: use interval types
    //ir"($f,42)" matches { // Could not merge types =Int(42) and =Int
    //  case ir"($_: (List[List[$t0]] => Any), $_: t0)" =>
    //    eqt(t0, irTypeOf[Int])
    //}
    
    ir"(x: Any) => 'ok" matches {
      case ir"$_: ((List[$t0] => Int) => t0)" =>
        eqt(t0, irTypeOf[Symbol])
    } and {
      case ir"$_: ((List[$t0] => Int) => Symbol)" =>
        eqt(t0, AnyType)
    } and {
      case ir"$_: (($t0 => Int) => Symbol)" =>
        eqt(t0, AnyType)
    } and {
      case ir"$_: (($t0 => Int) => t0)" =>
        eqt(t0, irTypeOf[Symbol])
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


