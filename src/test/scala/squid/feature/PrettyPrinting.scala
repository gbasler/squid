package squid
package feature

import utils._

class PrettyPrinting extends MyFunSuite {
  import TestDSL.Predef._
  import TestDSL.Quasicodes._
  import TestDSL.showRep
  
  test("Basic Types") {
    
    same(ir"Option.empty"
      .rep |> showRep, "scala.Option.empty[scala.Nothing]")
    same(ir"Option.empty[Int]"
      .rep |> showRep, "scala.Option.empty[scala.Int]")
    same(ir"Option.empty[AnyRef]"
      .rep |> showRep, "scala.Option.empty[java.lang.Object]")
    
  }
  
  test("Null variables") {
    
    same(ir"var x: Int = ${nullValue[Int]}; x+1"
      .rep |> showRep, """{
      |  var x_0: scala.Int = 0;
      |  x_0.+(1)
      |}""".stripMargin)
    same(ir"var x: Int = null.asInstanceOf[Int]; x+1"
      .rep |> showRep, """{
      |  var x_0: scala.Int = null.asInstanceOf[scala.Int];
      |  x_0.+(1)
      |}""".stripMargin)
    same(ir"var x: Int = ${ir"null".asInstanceOf[IR[Int,Any]]}; x+1"
    // ^ Warning: variable `[x:squid.lib.package.Var[Int]]` of type `Int` (not a subtype of `AnyRef`) is assigned `null`.
      .rep |> showRep, """{
      |  var x_0: scala.Int = null;
      |  x_0.+(1)
      |}""".stripMargin)
    same(ir"var x: String = ${nullValue[String]}; x+1"
      .rep |> showRep, """{
      |  var x_0: java.lang.String = null;
      |  x_0.+(1)
      |}""".stripMargin)
    same(ir"var x: String = null; x+1"
      .rep |> showRep, """{
      |  var x_0: java.lang.String = null;
      |  x_0.+(1)
      |}""".stripMargin)
    
  }
  
  test("Free variables") {
    
    same(ir"(x?:Int)+1".toString, """ir"?x.+(1)"""")
    
  }
  
}
