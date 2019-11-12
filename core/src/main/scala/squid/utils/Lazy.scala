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

import squid.lib.{transparencyPropagating, transparent}

/** Cheap Lazy implementation for pure computations */
// Note: `+A <: AnyRef` should actually be `+A >: Null`, which would avoid the casts
final class Lazy[+A <: AnyRef](vl: () => A, computeWhenShow: Boolean) {
  private[this] var computedValue: A = null.asInstanceOf[A]
  private[this] var computing = false
  def isComputing = computing
  def isComputed = computedValue != null
  def compute(): Unit = value
  @transparent
  def value = {
    if (computedValue == null) {
      val wasComputing = computing // I'm not sure this is very helpful... maybe just throw if `computing == true`?
      computing = true
      try computedValue = vl()
      finally computing = wasComputing
    }
    computedValue
  }
  def valueOption: Option[A] = Option(valueOrElse(null.asInstanceOf[A]))
  def valueOrElse[B >: A](default: => B): B = if (!computing) value else default
  def valueIfComputed: Option[A] = value optionIf isComputed
  
  override def equals(that: Any): Bool = that match {
    case that: Lazy[_] => that.value == value
    case _ => that == value
  }
  
  @transparencyPropagating
  def `internal pure value` = value // TODO doc
  def internal_pure_value = value // TODO doc
  override def toString = s"Lazy(${if (isComputed || computeWhenShow) value else "..."})"
}
object Lazy {
  @transparencyPropagating
  def apply[A <: AnyRef](vl: => A): Lazy[A] = mk(vl, true)
  @transparencyPropagating
  def mk[A <: AnyRef](vl: => A, computeWhenShow: Bool) =
    new Lazy(() => vl, computeWhenShow)
  def unapply[A <: AnyRef](self: Lazy[A]): Some[A] = Some(self.value)
}

