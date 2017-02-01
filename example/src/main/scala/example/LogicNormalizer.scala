package example

import squid._
import utils._
import ir._
import utils.Debug.show

/**
  * Created by lptk on 01/02/17.
  */
trait LogicNormalizer extends SimpleRuleBasedTransformer { self =>
  import base.Predef._
  import self.base.InspectableIROps
  import self.base.IntermediateIROps
  
  import squid.lib.Var
  
  rewrite {
    
    case ir"!true" => ir"false"
    case ir"!false" => ir"true"
    case ir"!(!($x:Bool))" => ir"$x"
    case ir"($x:Bool) || false" => ir"$x"
    case ir"($x:Bool) || true" => ir"true"
    case ir"($x:Bool) && false" => ir"false"
    case ir"($x:Bool) && true" => ir"$x"
      
    case ir"if (true) $x else $y : $t" => ir"$x"
    case ir"if (false) $x else $y : $t" => ir"$y"
      
    case ir"assert(true)" => ir"()"
    case ir"require(true)" => ir"()"
    case ir"($x:$xt) ensuring true" => ir"$x"
      
    
  }
  
}

