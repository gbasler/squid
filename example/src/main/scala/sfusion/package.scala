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

import squid.utils._

package object sfusion {
  
  /** If finite, Left(finiteSize), otherwise Right(isBounded) where !isBounded means the stream can be infinite */
  type SizeInfo = Either[Int,Bool]
  final val Bounded = Right(true)
  final val Unbounded = Right(false)
  
  def minSize(lhs: SizeInfo, rhs: SizeInfo): SizeInfo = lhs -> rhs match {
    case Left(n) -> Left(m) => Left(n min m)
    case Left(n) -> _ => Left(n)
    case _ -> Left(n) => Left(n)
    case Right(a) -> Right(b) => Right(a || b)
  }
  def addToSize(lhs: SizeInfo, rhs: Int): SizeInfo = lhs match {
    case Left(n) => Left(n+rhs)
    case x => x
  }
  def addSizes(lhs: SizeInfo, rhs: SizeInfo): SizeInfo = lhs -> rhs match {
    case Left(n) -> Left(m) => Left(n + m)
    case Left(n) -> x => x
    case x -> Left(n) => x
    case Right(a) -> Right(b) => Right(a && b)
  }
  
}

