package scp
package functional

import org.scalatest.FunSuite

class FunBodyXion extends FunSuite {
  
  import TestDSL._
  
  val fun = dsl"(x: Int) => x + 1"
  val body = fun match {
    case dsl"(y: Int) => $b: Int" => b
  }
  
  test("Function Body Extraction") { // FIXME
    
    //println(body, dsl"($$y: Int) + 1")
    //println(body.rep extract dsl"($$y: Int) + 1".rep)
    //println(dsl"($$y: Int) + 1".rep extract body.rep)
    
    assert(body =~= dsl"($$y: Int) + 1")
    assert(!(body =~= dsl"($$x: Int) + 1"))
    
    val bodyPart = body match {
      case dsl"($x: Int) + 1" => x
    }
    assert(bodyPart =~= dsl"$$y: Int")
    assert(!(bodyPart =~= dsl"$$x: Int"))
    
  }
  
  test("Function Body Reconstruction") {
    
    val fun2 = dsl"(y: Int) => $body" : Q[Int => Int, {}]
    assert(fun =~= fun2)
    
  }
  
  
  
  
}



