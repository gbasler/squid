package squid
package functional

class FunBodyXion extends MyFunSuite {
  
  import TestDSL.Predef._
  
  val fun = ir"(x: Int) => x + 1"
  val body = fun match {
    case ir"(y: Int) => $b: Int" => b
  }
  
  test("Function Body Extraction") {
    
    //println(body, ir"($$y: Int) + 1")
    //println(body.rep extract ir"($$y: Int) + 1".rep)
    //println(ir"($$y: Int) + 1".rep extract body.rep)
    
    assert(body =~= ir"($$y: Int) + 1")
    assert(!(body =~= ir"($$x: Int) + 1"))
    
    val bodyPart = body match {
      case ir"($x: Int) + 1" => x
    }
    assert(bodyPart =~= ir"$$y: Int")
    assert(!(bodyPart =~= ir"$$x: Int"))
    
  }
  
  test("Function Body Reconstruction") {
    
    val fun2 = ir"(y: Int) => $body" : Q[Int => Int, {}]
    assert(fun =~= fun2)
    
  }
  
  
  
  
}



