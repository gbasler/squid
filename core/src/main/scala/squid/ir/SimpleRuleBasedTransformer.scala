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

import collection.mutable

trait SimpleRuleBasedTransformer extends RuleBasedTransformer {
  val base: InspectableBase
  import base._
  import TranformerDebug.{debug, nestDbg}
  
  val rules = mutable.ArrayBuffer[(Rep, Extract => Option[Rep])]()
  override def registerRule(xtor: Rep, code: Extract => Option[Rep]): Unit = rules += xtor -> code
  
  def transform(rep: Rep): Rep = {
    debug(s"Processing $rep")
    var currentRep = rep
    
    nestDbg(rules foreach { case (xtor, code) =>
      //debug(s"Matching xtor ${Console.BOLD}${xtor.show}${Console.RESET} << ${currentRep.show}")
      
      //try rewriteRep(xtor, currentRep, code) foreach { res => currentRep = res }
      nestDbg(try rewriteRep(xtor, currentRep, code) alsoApply { resOpt =>
        //debug(s"Got Code: ${resOpt map (_ show) map (s => s"${Console.GREEN+Console.BOLD}$s${Console.RESET}")}")
      } foreach { res =>
        //debug(s"Matched xtor ${Console.BOLD}${xtor.show}${Console.RESET} << ${currentRep.show}")
        debug(s"Matched xtor ${Console.BOLD}${xtor}${Console.RESET} << ${currentRep}")
        debug(s"Got Code: ${res |> (s => s"${Console.GREEN+Console.BOLD}$s${Console.RESET}")}")
        //debug(s"Got Code: ${res.show |> (s => s"${Console.GREEN+Console.BOLD}$s${Console.RESET}")}")
        currentRep = res }
      catch {
        case RewriteAbort(msg) =>
          debug(s"Rewrite aborted. " + (if (msg isEmpty) "" else s"Message: $msg"))
      })
    })
    
    currentRep
  }
  
  
}

