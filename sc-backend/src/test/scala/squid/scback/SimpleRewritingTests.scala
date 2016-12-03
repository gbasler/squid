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
  
  
}