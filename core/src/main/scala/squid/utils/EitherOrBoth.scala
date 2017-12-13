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
package utils

sealed abstract class EitherOrBoth[+A, +B] {
  def fold[T](f: A => T, g: B => T): Either[T, (T,T)]
  def first: Option[A]
  def second: Option[B]
}
object EitherOrBoth {
}
case class First[+A](value: A) extends EitherOrBoth[A, Nothing] {
  private[this] type B = Nothing
  override def fold[T](f: A => T, g: B => T): Either[T, (T,T)] = Left(f(value))
  def first: Option[A] = Some(value)
  def second: Option[B] = None
}
case class Second[+B](value: B) extends EitherOrBoth[Nothing, B] {
  private[this] type A = Nothing
  override def fold[T](f: A => T, g: B => T): Either[T, (T,T)] = Left(g(value))
  def first: Option[A] = None
  def second: Option[B] = Some(value)
}
case class Both[+A, +B](fst: A, snd: B) extends EitherOrBoth[A, B] {
  override def fold[T](f: A => T, g: B => T): Either[T, (T,T)] =
    Right(f(fst), g(snd))
  def first: Option[A] = Some(fst)
  def second: Option[B] = Some(snd)
}
