package squid
package feature

import utils._

class TypeImplicits extends MyFunSuite {
  import TestDSL.Predef._
  
  test("Using Existential Type Representations") {
    
    val typs = List[IRType[_]](irTypeOf[Int], irTypeOf[Double], irTypeOf[String])
    
    assert((typs map {
      case typ: IRType[t] =>
        ir"Option.empty[($typ,t)]"
    }, List(
      ir"Option.empty[(Int,Int)]",
      ir"Option.empty[(Double,Double)]",
      ir"Option.empty[(String,String)]"
    )).zipped forall (_ =~= _))
    
    typs match {
      case (t0:IRType[t1]) :: (t1:IRType[t0]) :: _ :: Nil =>  // makes sure resolution is no more based on names
        ir"Map.empty[$t0,$t1]" eqt ir"Map.empty[Int,Double]"
        ir"Map.empty[ t0, t1]" eqt ir"Map.empty[Double,Int]"
      case _ => fail
    }
    
    // FIXME currently infers an existential type!
    ir"Option.empty[${typs.head}]" //alsoApply println
    
  }
  
  
}
