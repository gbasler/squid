package squid
package anf.transfo

import utils._
import ir._
import utils.Debug.show

/**
  * Created by lptk on 01/02/17.
  * TODO move to anf.transfo.StandardNormalizer
  */
trait LogicNormalizer extends SimpleRuleBasedTransformer { self =>
  
  val base: anf.analysis.BlockHelpers
  import base.Predef._
  import base.{Block,WithResult}
  
  rewrite {
    
    case ir"!true" => ir"false"
    case ir"!false" => ir"true"
    case ir"!(!($x:Bool))" => ir"$x"
      
    case ir"($x:Bool) || false" => ir"$x"
    case ir"($x:Bool) || true" => ir"true"
    case ir"($x:Bool) && false" => ir"false"
    case ir"($x:Bool) && true" => ir"$x"
    case ir"false || ($x:Bool)" => ir"$x"
    case ir"true || ($x:Bool)" => ir"true"
    case ir"false && ($x:Bool)" => ir"false"
    case ir"true && ($x:Bool)" => ir"$x"
      
    // Generalization of the above (first half) for blocks. The above are kept because they do not generate useless if-statements.
    case ir"($x:Bool) && ${Block(WithResult(b, ir"false"))}" => ir"if ($x) ${b.original}; false"
    case ir"($x:Bool) && ${Block(WithResult(b, ir"true"))}" => ir"if ($x) ${b.original}; $x"
    case ir"($x:Bool) || ${Block(WithResult(b, ir"false"))}" => ir"if (!$x) ${b.original}; $x"
    case ir"($x:Bool) || ${Block(WithResult(b, ir"true"))}" => ir"if (!$x) ${b.original}; true"
      
    case ir"if (true) $x else $y : $t" => ir"$x"
    case ir"if (false) $x else $y : $t" => ir"$y"
    case ir"if (!($cond:Bool)) $x else $y : $t" => ir"if ($cond) $y else $x"
      
    case ir"assert(true)" => ir"()"
    case ir"require(true)" => ir"()"
    case ir"($x:$xt) ensuring true" => ir"$x"
      
    // Equivalent to `case ir"while(${Block(b)}){$body}" if b.ret =~= ir"true" => ...`
    case ir"while(${Block(WithResult(b, ir"true" ))}) $body" => ir"${b.original}; $body"
    case ir"while(${Block(WithResult(b, ir"false"))}) $body" => ir"${b.original}; ()"
      
      
  }
  
}

