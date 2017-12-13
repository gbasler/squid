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
import squid.lang.Optimizer


trait OnlineOptimizer extends Optimizer with InspectableBase {
  val base: this.type = this
  
  protected var transformExtractors = false
  
  private var enabled = true
  override def disableRewritingsFor[A](r: => A): A = {
    val old = enabled
    enabled = false
    try super.disableRewritingsFor(r) finally enabled = old
  }
  
  /** It is important not to try and optimize extractors, as it may result in unexpected behavior and the loss of extraction holes.
    * For example, beta reduction on a redex pattern where the body  isextracted will "eat" the argument value
    * (it won't find any occurence of the binder in the body, which is just a hole!) */
  protected def processOnline(r: Rep) = if (!enabled || isExtracting && !transformExtractors) r else optimizeRep(r)
  /*
  abstract override def readVal(v: BoundVal): Rep = processOnline(super.readVal(v))
  abstract override def const(value: Any): Rep = processOnline(super.const(value))
  abstract override def lambda(params: List[BoundVal], body: => Rep): Rep = processOnline(super.lambda(params, body))
  
  abstract override def staticModule(fullName: String): Rep = processOnline(super.staticModule(fullName))
  abstract override def module(prefix: Rep, name: String, typ: TypeRep): Rep = processOnline(super.module(prefix, name, typ))
  abstract override def newObject(tp: TypeRep): Rep = processOnline(super.newObject(tp))
  abstract override def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep =
    processOnline(super.methodApp(self, mtd, targs, argss, tp))
  
  abstract override def byName(arg: => Rep): Rep = processOnline(super.byName(arg))
  */
  
  //override def wrapConstruct(r: => Rep) = processOnline(super.wrapConstruct(r)) // TODO rm; use a `process` or `postProcess` method instead
  //override def wrapExtract  (r: => Rep) = { val old = extracting; extracting = true;  try r finally { extracting = old } }
  
  //abstract override def postProcess(r: Rep) = processOnline(super.postProcess(r))
  override def postProcess(r: Rep) = processOnline(super.postProcess(r))
  
}

