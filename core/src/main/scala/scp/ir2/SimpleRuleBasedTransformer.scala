package scp
package ir2

import utils._
import lang2._

import collection.mutable

trait SimpleRuleBasedTransformer extends RuleBasedTransformer {
  val base: InspectableBase
  import base._
  import TranformerDebug.debug
  
  val rules = mutable.ArrayBuffer[(Rep, Extract => Option[Rep])]()
  override def registerRule(xtor: Rep, code: Extract => Option[Rep]): Unit = rules += xtor -> code
  
  def transform(rep: Rep): Rep = {
    debug(s"Processing $rep")
    var currentRep = rep
    
    rules foreach { case (xtor, code) =>
      try rewriteRep(xtor, currentRep, code) foreach { res => currentRep = res }
      catch {
        case RewriteAbort(msg) =>
          debug(s"Rewrite aborted. " + (if (msg isEmpty) "" else s"Message: $msg"))
      }
    }
    
    currentRep
  }
  
  
}

