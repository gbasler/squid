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

package squid.scback

import squid.utils._
import ch.epfl.data.sc._
import pardis._
import deep.scalalib._
import deep.scalalib.collection._
import PardisBinding._
import pardis.ir.InlineFunctions

trait TestDSLBinding {
  
  //object SC extends ir.Base
  //object SC extends ir.Base with SeqOps
  //object SC extends ir.Base with ArrayBufferOps
  //object SC extends ir.Base with StringOps with NoStringCtor
  //object SC extends ir.Base with ArrayBufferOps with NumericOps with ScalaPredefOps
  //object SC extends ir.Base with NumericOps
  //object SC extends ir.Base with ScalaCoreOps with NoStringCtor
  object SC extends ir.Base with ScalaCoreOps with DefaultPardisMixin with ContOps
  //object SC extends ir.Base with ScalaCoreOps with DefaultPardisMixin with ContOps with InlineFunctions
  
  /*_*/
  object Sqd extends AutoboundPardisIR(SC) with DefaultRedirections[SC.type] with PublicTraceDebug
  
  Sqd.ab = {
    import scala.collection.mutable.ArrayBuffer
    AutoBinder(SC, Sqd)
  }
  
}

