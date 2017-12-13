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

package squid.lang

trait Optimizer {
  val base: InspectableBase
  import base._
  protected def pipeline: Rep => Rep
  //final def optimizeRep(pgrm: Rep): Rep = pipeline(pgrm)
  final def optimizeRep(pgrm: Rep): Rep = { // TODO do this Transformer's `pipeline`......?
    val r = pipeline(pgrm)
    if (!(r eq pgrm)) substitute(r) else r  // only calls substitute if a transformation actually happened
  }
  final def optimize[T,C](pgrm: Code[T,C]): Code[T,C] = `internal Code`[T,C](optimizeRep(pgrm.rep))
  
  def wrapOptim[A](id: String)(code: => A) = code
  
  
  def setContext(src:String) = ()
  
}
