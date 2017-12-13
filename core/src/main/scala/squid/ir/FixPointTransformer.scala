// Copyright 2017 EPFL DATA Lab (data.epfl.ch)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package squid
package ir

import utils._
import lang._
import squid.lang.InspectableBase

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
    
    if (recNum == MAX_TRANSFORM_ITERATIONS) 
      System.err.println(s"Rewrite rules did not converge after $MAX_TRANSFORM_ITERATIONS iterations.\nFor rep: ${currentRep|>showRep}")
    //debug(" --- END --- ")
    
    currentRep
  }
  
}

