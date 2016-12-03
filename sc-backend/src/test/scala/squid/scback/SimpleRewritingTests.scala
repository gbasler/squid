package squid.scback

import collection.mutable.ArrayBuffer
import ch.epfl.data.sc.pardis.ir.Constant

class SimpleRewritingTests extends PardisTestSuite {
  
  import Sqd.Predef._
  import Sqd.Quasicodes._
  
  
  test("Constants") {
    
    sameDefs(ir"666" rewrite {
      case ir"666" => ir"42"
    }, ir"42")
    
    sameDefs(ir"666" rewrite {
      case ir"666" => ir"42.0.toInt"
    }, ir"42.0.toInt")
    
    sameDefs(ir"666.0.toInt" rewrite {
      case ir"666.0" => ir"42.0"
    }, ir"42.0.toInt")
    
  }
  
  
  test("FunctionN") {
    
    sameDefs(ir"() => println(666)" rewrite {
      case ir"666" => ir"42"
    }, ir"() => println(42)")
    
    sameDefs(ir"val f = () => println(666); f()" rewrite {
      case ir"666" => ir"42"
    }, ir"val f = () => println(42); f()")
    
    sameDefs(ir"ArrayBuffer(1,2,3) map ((x: Int) => println(666))" rewrite {
      case ir"666" => ir"42"
    }, ir"ArrayBuffer(1,2,3) map ((x: Int) => println(42))")
    
    sameDefs(ir"(a:Int,b:Double,c:String) => ((a,b),Option[String](null))" rewrite {
      case ir"(($x:Int,$y:Double),Option[String](null))" => ir"val t = ($y.toInt, $x.toDouble); (t, Option(${Const("ok")}))"
    }, ir"""(a:Int,b:Double,c:String) => { ((b.toInt, a.toDouble), Option("ok")) }""")
    
  }
  

}