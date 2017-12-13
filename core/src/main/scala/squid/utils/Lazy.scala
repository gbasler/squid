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

package squid.utils

/** Cheap Lazy implementation for pure computations */
final class Lazy[+A <: AnyRef](vl: () => A, computeWhenShow: Boolean) {
  private[this] var computedValue: A = null.asInstanceOf[A]
  private[this] var computing = false
  def isComputing = computing
  def computed = computedValue != null
  def value = {
    if (computedValue == null) {
      val wasComputing = computing
      computing = true
      try computedValue = vl()
      finally computing = wasComputing
    }
    computedValue
  }
  override def toString = s"Lazy(${if (computed || computeWhenShow) value else "..."})"
}
object Lazy {
  def apply[A <: AnyRef](vl: => A, computeWhenShow: Boolean = true) =
    new Lazy(() => vl, computeWhenShow)
}

