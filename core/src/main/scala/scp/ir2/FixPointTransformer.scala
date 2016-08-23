package scp
package ir2

import utils._
import lang2._

/** Transformer that applies the rewrite rules repeatedly until a fixed point is reached or `MAX_TRANSFORM_ITERATIONS` is exceeded. */
trait FixPointTransformer extends SimpleTransformer {
  val base: InspectableBase
  
  import base._
  import TranformerDebug.debug
  
  val MAX_TRANSFORM_ITERATIONS = 32
  
  override def transform(rep: Rep) = {
    var matched = true
    var currentRep = rep
    var recNum = 0
    
    while (matched && recNum < MAX_TRANSFORM_ITERATIONS) {
      debug(s" --- ($recNum) --- ")
      
      recNum += 1
      matched = false
      
      rules foreach { case (xtor, code) =>
        debug(s"Matching xtor $xtor << $currentRep")
        
        extract(xtor, currentRep) foreach { ex =>
          debug(s"Got Extract: $ex")
          
          val resOpt = code(ex)
          debug(s"Got Code: $resOpt")
          
          resOpt foreach { res =>
            currentRep = res
            matched = true
          }
          
        }
      }
    }
    
    if (recNum == MAX_TRANSFORM_ITERATIONS) System.err.println("Online rewrite rules did not converge.")
    debug(" --- END --- ")
    
    currentRep
  }
  
}




