package scp
package feature

import org.scalatest.FunSuite
import org.scalatest.ShouldMatchers

import utils.Debug._

class Genericity extends FunSuite with ShouldMatchers {
  
  import TestDSL._
  
  test("List") {
    
    val ls = dsl"List(1.0, 2.0)"
    println(ls.rep.typ)
    
    
    //def foo[A: TypeEv](x: Q[A,{}]) = x match {
    def foo[A: TypeEv](x: Q[List[A],{}]) = x match {
      case dsl"List[A]($x,$y)" =>
        //println(typeEv[A])
        dsl"List($y,$x)"
    }
    
    println(foo(ls))
    
    (ls: Q[_,{}]) match {
      case dsl"$ls: List[Int]" => ???
      case dsl"$ls: List[Double]" =>
    }
    
    
    /*
    // FIXME
    (ls: Q[_,{}]) match {
      case dbgdsl"$ls: List[$t]" => println(t)
    }
    */
    
    /*
    // SCALA PROBLEM!!
    { trait t
      import scala.reflect.runtime.universe._
      implicit val _t_0: TypeTag[t] = ???
      typeTag[t]
      typeTag[List[t]]
    }
    */
    
    
    /*
({
  trait t extends _root_.scp.lang.Base.HoleType;
  {
    final class $anon {
      def unapply(_t_ : SomeQ): _root_.scala.Option[scala.Tuple2[Quoted[List[t], AnyRef], QuotedType[t]]] = {
        val _term_ = ascribe[scala.`package`.List[t]](hole[List[t]]("ls")({
  import scala.reflect.runtime.universe._;
  val _e_0 = TestDSL.typeEv[t];
  implicit val _t_0: TypeTag[t] = _e_0.rep.tagAs[t];
  TypeEv(TestDSL.ScalaTypeRep[List[t]](typeTag[List[t]], _e_0))
}));
        TestDSL.extract(_term_, _t_.rep).map(((_maps_ ) => {
          assert(_maps_._1.keySet.==(Set("ls")), "Extracted value keys ".+(_maps_._1.keySet).+(" do not correspond to specified keys ").+(Set("ls")));
          assert(_maps_._2.keySet.==(Set("t")), "Extracted type keys ".+(_maps_._2.keySet).+(" do not correspond to specified keys ").+(Set("t")));
          scala.Tuple2(Quoted[List[t], AnyRef](_maps_._1("ls")), QuotedType[t](_maps_._2("t")))
        }))
      }
    };
    new $anon()
  }
}).unapply(ls)
    
  */
    
    
    
    
  }
  
}



