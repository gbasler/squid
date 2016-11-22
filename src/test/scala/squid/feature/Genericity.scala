package squid
package feature

import utils.Debug._

class Genericity extends MyFunSuite {
  import TestDSL.Predef._
  
  test("List") {
    
    val ls = ir"List(1.0, 2.0)"
    //println(ls.rep.typ)
    
    
    //def foo[A: TypeEv](x: Q[A,{}]) = x match {
    def foo[A: TypeEv](x: Q[List[A],{}]) = x match {
      case ir"List[A]($x,$y)" =>
        //println(typeEv[A])
        ir"List($y,$x)"
    }
    
    //println(foo(ls))
    
    (ls: Q[_,{}]) match {
      case ir"$ls: List[Int]" => ???
      case ir"$ls: List[Double]" =>
    }
    
    
    (ls: Q[_,{}]) match {
      case ir"$ls: List[$t]" => eqt(t, irTypeOf[Double])
    }
    
    /*
    // Note: Scala weirdness:
    { trait t
      import scala.reflect.runtime.universe._
      implicit val _t_0: TypeTag[t] = ???
      typeTag[t]
      typeTag[List[t]]
    }
    */
    
    
    
  }
  
}



