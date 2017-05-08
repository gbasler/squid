package squid
package anf.transfo

import utils._
import ir._
import utils.Debug.show

/**
  * Created by lptk on 01/02/17.
  * TODO? move to anf.transfo.StandardNormalizer
  */
/* 
  TODO: have specialized treatment of booleans and logic in the IR
    Some things like `x>0 || ... || !(x>0) || ...` are not syntactic enough to optimize with these rules
    In general, we need to allow extension to, eg, make special support for numeric types and make `x==0` equivalent to `x>=0 && x<=0`
  ... actually can do this with a better analysing pass – see LogicFlowNormalizer
  
  Note: IF-THEN-ELSE returning Bool is normalized using `&&` and `!`
  
  TODO: normalize `false` to `!true`?
  
  TODO: normalize `while` loops to put all code in the condition? (it's more general than everything in the body)
  
*/
trait LogicNormalizer extends SimpleRuleBasedTransformer { self =>
  
  val base: anf.analysis.BlockHelpers
  import base.Predef._
  import base.{AsBlock,WithResult}
  
  rewrite {
    
    case ir"!true" => ir"false"
    case ir"!false" => ir"true"
    case ir"!(!($x:Bool))" => ir"$x"
      
    // Streamlining, so we don't have to deal with || but only &&
    case ir"($x:Bool) || $y" => ir"!(!$x && !$y)"
      
    case ir"($x:Bool) && false" => ir"false"
    case ir"($x:Bool) && true" => ir"$x"
    case ir"false && ($x:Bool)" => ir"false"
    case ir"true && ($x:Bool)" => ir"$x"
      
    // Generalization of the above (first half) for blocks. The above are kept because they do not generate useless if-statements.
    case ir"($x:Bool) && ${AsBlock(WithResult(b, ir"false"))}" => ir"if ($x) ${b.statements()}; false"
    case ir"($x:Bool) && ${AsBlock(WithResult(b, ir"true"))}" => ir"if ($x) ${b.statements()}; $x"
    // ^ Note: not just writing `ir"if ($x) ${b.original}; $x"` because that would make an IF-THEN-ELSE with result of
    // type Any, which makes the 'then' branch artificially look like it's using its result; here we use b.statements(), 
    // of return type Unit.
      
    case ir"if (true) $x else $y : $t" => ir"$x"
    case ir"if (false) $x else $y : $t" => ir"$y"
    case ir"if (!($cond:Bool)) $x else $y : $t" => ir"if ($cond) $y else $x"
      
    case ir"if ($cond) $thn else thn : $t" => ir"$thn" // this one might be expensive as it compares the two branches for equivalence as expressions
    //// more general, but too complex (would need crazy bindings manip):
    //case ir"if ($cond) ${Block(b0)} else ${Block(b1)} : $t" if b0.res == b1.res  =>  ir"if ($cond) ${b0.statements()} else ${b1.statements()}; ???"
      
    case ir"if ($cond) $thn else $els : Boolean" => 
      ir"val c = $cond; c && $thn || !c && $els" // TODO rm usage of ||
      
    // TODO later?:
    //case ir"($x:Bool) && ${AsBlock(WithResult(b, ir"false"))}" => ir"if ($x) ${b.statements()}; false"
      
    // these ones might be expensive as they compare the two branches for equivalence as expressions
    case ir"($x0:Bool) && ${AsBlock(b)}" if x0 =~= b.res  =>  ir"${b.statements()}; $x0"
    case ir"($x0:Bool) && ${AsBlock(WithResult(b, ir"!($x1:Boolean)"))}" if x0 =~= x1  =>  ir"false"
      
    case ir"assert(true)" => ir"()"
    case ir"require(true)" => ir"()"
    case ir"($x:$xt) ensuring true" => ir"$x"
      
    // Equivalent to `case ir"while(${Block(b)}){$body}" if b.ret =~= ir"true" => ...`
    case ir"while(${AsBlock(WithResult(b, ir"true" ))}) $body" => ir"while(true) { ${b.statements()}; $body }"
    case ir"while(${AsBlock(WithResult(b, ir"false"))}) $body" => b.statements()
      
    // Associatign on the left
    case ir"($x:Bool) && (($y:Bool) && $z)" => ir"$x && $y && $z"
      
  }
  
}

