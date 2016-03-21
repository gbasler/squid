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
    
    /*
    // FIXME
    // horrible gen'd code for dsl"($$y: Int) + 1":
    {
      val +$macro$101: reflect.runtime.universe.MethodSymbol = scala.reflect.runtime.`package`.universe.typeOf[Int](
        (scala.reflect.runtime.`package`.universe.TypeTag.Int: reflect.runtime.universe.TypeTag[Int])).members.find(
        ((s: reflect.runtime.universe.Symbol) => s.name.toString().==("$plus").&&(
          scala.reflect.runtime.`package`.universe.showRaw(s.info.erasure, scala.reflect.runtime.`package`.universe.showRaw$default$2, scala.reflect.runtime.`package`.universe.showRaw$default$3,
            scala.reflect.runtime.`package`.universe.showRaw$default$4, scala.reflect.runtime.`package`.universe.showRaw$default$5, scala.reflect.runtime.`package`.universe.showRaw$default$6,
            scala.reflect.runtime.`package`.universe.showRaw$default$7).==("MethodType(List(TermName(\"x\")), TypeRef(ThisType(scala), scala.Int, List()))")))).get.asMethod;
      TestDSL.Quoted.apply[Int, Any{val y: Int}](TestDSL.dslMethodApp(scala.Some.apply[scp.TestDSL.Rep](
        TestDSL.ascribe[Int](TestDSL.hole[Int]("y")(
          (TestDSL.TypeEv.apply[Int](TestDSL.ScalaTypeRep.apply[Int]((scala.reflect.runtime.`package`.universe.TypeTag.Int: reflect.runtime.universe.TypeTag[Int]))): scp.TestDSL.TypeEv[Int]))
        )((TestDSL.TypeEv.apply[Int](TestDSL.ScalaTypeRep.apply[Int]((scala.reflect.runtime.`package`.universe.TypeTag.Int: reflect.runtime.universe.TypeTag[Int]))): scp.TestDSL.TypeEv[Int]))),
        +$macro$101, immutable.this.Nil, immutable.this.List.apply[List[scp.TestDSL.Rep]](immutable.this.List.apply[scp.TestDSL.Rep](TestDSL.const[Int](1)((TestDSL.TypeEv.apply[Int]
        (TestDSL.ScalaTypeRep.apply[Int]((scala.reflect.runtime.`package`.universe.TypeTag.Int: reflect.runtime.universe.TypeTag[Int]))): scp.TestDSL.TypeEv[Int])))),
          TestDSL.typeEv[Int]((TestDSL.TypeEv.apply[Int](TestDSL.ScalaTypeRep.apply[Int]((scala.reflect.runtime.`package`.universe.TypeTag.Int: reflect.runtime.universe.TypeTag[Int]))): scp.TestDSL.TypeEv[Int])).rep))
        ((TestDSL.TypeEv.apply[Int](TestDSL.ScalaTypeRep.apply[Int]((scala.reflect.runtime.`package`.universe.TypeTag.Int: reflect.runtime.universe.TypeTag[Int]))): scp.TestDSL.TypeEv[Int]))
    }
    */
    
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



