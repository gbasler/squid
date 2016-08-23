package scp
package ir2

import utils._
import lang2._

import collection.mutable

trait SimpleTransformer extends Transformer {
  val base: InspectableBase
  import base._
  import TranformerDebug.debug
  
  val rules = mutable.ArrayBuffer[(Rep, Extract => Option[Rep])]()
  override def registerRule(xtor: Rep, code: Extract => Option[Rep]): Unit = rules += xtor -> code
  
  def transform(rep: Rep): Rep = {
    debug(s"Processing $rep")
    var currentRep = rep
    
    rules foreach { case (xtor, code) =>
      extract(xtor, currentRep) foreach { ex =>
        code(ex) foreach { res =>
          currentRep = res
        }
      }
    }
    
    currentRep
  }
  
  def transformBottomUp(rep: Rep): Rep = (base bottomUp rep)(transform)
  
}

