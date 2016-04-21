package scp
package functional

import org.scalatest.FunSuite

class MultiStage extends FunSuite {
  import TestDSL._
  
  
  test("Nested Quoted Types") {
    
    val reprep = dsl"MultiStage.rep" : Q[Q[Double, {}], {}]
    val rep = reprep.run
    
    assert(rep == MultiStage.rep)
    
  }
  
  test("Nested dsl expressions") { // FIXME
    
    //show(dbgdsl""" dbgdsl"42" """)
    
    lazy val dsldsl = dsl""" dsl"42" """ // FIXME: Could not find type DynamicTypeRep in module object ScalaTyping
    
  }
  
}
object MultiStage {
  import TestDSL._
  
  val rep = dsl"42.toDouble"
  
}




