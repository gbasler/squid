package squid
package ir

import utils._
import lang._
import squid.lang.InspectableBase

/** Transformer that applies the rewrite rules repeatedly until a fixed point is reached or `MAX_RULE_BASED_TRANSFORM_ITERATIONS` is exceeded.
  * Note: this shares a lot of similar code as `FixPointTransformer`; they are not merged mainly for performance reasons. */
trait FixPointRuleBasedTransformer extends SimpleRuleBasedTransformer {
  val base: InspectableBase
  
  import base._
  //override lazy val TranformerDebug = base.asInstanceOf[PublicTraceDebug]
  import TranformerDebug.debug
  
  /** Note: renamed from `MAX_TRANSFORM_ITERATIONS` to make `FixPointRuleBasedTransformer` compatible with
    * `FixPointTransformer`, so one can mit it as in:
    *   {{{ ... with FixPointRuleBasedTransformer with TopDownTransformer with FixPointTransformer }}} 
    *  which is transforms each expressions until a fixed point is reached, in a top-down manner that is itself applied
    *  over and over until a fixed point is reached. */
  protected val MAX_RULE_BASED_TRANSFORM_ITERATIONS = 8
  
  override def transform(rep: Rep) = {
  //abstract override def transform(rep: Rep) = {
    debug(s"Processing $rep")
    var matched = true
    var currentRep = rep
    var recNum = 0
    
    while (matched && recNum < MAX_RULE_BASED_TRANSFORM_ITERATIONS) {
      //debug(s" --- ($recNum) --- ")
      
      recNum += 1
      matched = false
      
      rules foreach { case (xtor, code) =>
        //debug(s"Matching xtor ${Console.BOLD}${xtor.show}${Console.RESET} << ${currentRep.show}")
        //debug(s"Matching xtor ${Console.BOLD}${xtor}${Console.RESET} << ${currentRep}")
        
        try rewriteRep(xtor, currentRep, { ex => debug(s"Got Extract: $ex"); code(ex) }) match { case resOpt =>
          
          //debug(s"Got Code: ${resOpt map (_ show) map (s => s"${Console.GREEN+Console.BOLD}$s${Console.RESET}")}")
          //debug(s"Got Code: ${resOpt map (s => s"${Console.GREEN+Console.BOLD}$s${Console.RESET}")}")
          
          resOpt foreach { res =>
            debug(s"Matched xtor ${Console.BOLD}${xtor}${Console.RESET} << ${currentRep}")
            debug(s"Got Code: ${res |> (s => s"${Console.GREEN+Console.BOLD}$s${Console.RESET}")}")
            
            currentRep = res
            matched = true
          }
        } catch {
          case RewriteAbort(msg) =>
            debug(s"Rewrite aborted. " + (if (msg isEmpty) "" else s"Message: $msg"))
        }
        
      }
    }
    
    if (recNum == MAX_RULE_BASED_TRANSFORM_ITERATIONS)
      System.err.println(s"Rewrite rules did not converge after $MAX_RULE_BASED_TRANSFORM_ITERATIONS iterations.\nFor rep: ${currentRep|>showRep}")
    //debug(" --- END --- ")
    
    currentRep
  }
  
}




