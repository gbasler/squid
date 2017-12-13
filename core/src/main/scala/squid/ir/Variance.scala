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
import meta.RuntimeUniverseHelpers.sru

sealed abstract class Variance(val asInt: Int) {
  def * (that: Variance) = Variance(asInt * that.asInt) //(this, that) match {}
  def symbol = this match {
    case Invariant => "="
    case Covariant => "+"
    case Contravariant => "-"
  }
  override def toString = s"[$symbol]"
}
object Variance {
  def apply(asInt: Int) = asInt match {
    case 0 => Invariant
    case 1 => Covariant
    case -1 => Contravariant
  }
  def of (s: sru.TypeSymbol) =
    if (s.isCovariant) Covariant else if (s.isContravariant) Contravariant else Invariant
}
case object Invariant extends Variance(0)
case object Covariant extends Variance(1)
case object Contravariant extends Variance(-1)



