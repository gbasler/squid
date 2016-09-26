package scp
package ir2

import utils._
import lang2._

/** Transformer that applies the rewrite rules repeatedly until a fixed point is reached or `MAX_TRANSFORM_ITERATIONS` is exceeded. */
trait FixPointTransformer extends Transformer {
  val base: InspectableBase
  
  import base._
  import TranformerDebug.debug
  
  val MAX_TRANSFORM_ITERATIONS = 8
  
  abstract override def transform(rep: Rep) = {
    debug(s"Processing $rep")
    var matched = true
    var currentRep = rep
    var recNum = 0
    
    while (matched && recNum < MAX_TRANSFORM_ITERATIONS) {
      //debug(s" --- ($recNum) --- ")
      
      recNum += 1
      matched = false
      
      val newRep = super.transform(currentRep)
      if (!(newRep eq currentRep)) {
        matched = true
        currentRep = newRep
      }
      
    }
    
    if (recNum == MAX_TRANSFORM_ITERATIONS) System.err.println(s"Online rewrite rules did not converge after $MAX_TRANSFORM_ITERATIONS iterations.")
    //debug(" --- END --- ")
    
    currentRep
  }
  
}

