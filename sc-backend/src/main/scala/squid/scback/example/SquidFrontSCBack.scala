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
package scback
package example

import squid.utils._
import ch.epfl.data.sc._
import pardis._
import deep.scalalib._
import deep.scalalib.collection._
import _root_.squid.ir.SimpleANF

import scala.collection.mutable.ArrayBuffer

/**
  * Created by lptk on 08/11/16.
  */
object SquidFrontSCBack extends App {
  /*_*/
  
  object Frontend extends SimpleANF
  
  object SC extends ir.Base with ScalaCoreOps with PardisBinding.DefaultPardisMixin
  object Backend extends AutoboundPardisIR(SC) with PardisBinding.DefaultRedirections[SC.type]
  def init = Backend.ab = AutoBinder(SC, Backend)  // this is going to generate a big binding structure; put it in a separate file so it's not always recomputed and recompiled!
  init
  
  import Frontend.Predef._
  
  val pgrm = code"val a = ArrayBuffer(1,2,3); a append 1; a.size"
  
  println(pgrm)
  println(pgrm.run)
  
  println(pgrm reinterpretIn Backend)
  
}
