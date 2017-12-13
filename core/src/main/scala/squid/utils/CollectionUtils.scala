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

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable

object CollectionUtils {
  
  /** Works, but not AnyVal */
  implicit class TraversableOnceHelper[A,Repr](private val repr: Repr)(implicit isTrav: Repr => TraversableOnce[A]) {
    def collectPartition[B,Left](pf: PartialFunction[A, B])
    (implicit bfLeft: CanBuildFrom[Repr, B, Left], bfRight: CanBuildFrom[Repr, A, Repr]): (Left, Repr) = {
      val left = bfLeft(repr)
      val right = bfRight(repr)
      val it = repr.toIterator
      while (it.hasNext) {
        val next = it.next
        if (!pf.runWith(left += _)(next)) right += next
      }
      left.result -> right.result
    }
    
    // This is probably the most useful version:
    def collectOr[B,C,Left,Right](pf: PartialFunction[A, B], f: A => C)
    (implicit bfLeft: CanBuildFrom[Repr, B, Left], bfRight: CanBuildFrom[Repr, C, Right]): (Left, Right) = {
      val left = bfLeft(repr)
      val right = bfRight(repr)
      val it = repr.toIterator
      while (it.hasNext) {
        val next = it.next
        if (!pf.runWith(left += _)(next)) right += f(next)
      }
      left.result -> right.result
    }
    
    def mapSplit[B,C,Left,Right](f: A => Either[B,C])
    (implicit bfLeft: CanBuildFrom[Repr, B, Left], bfRight: CanBuildFrom[Repr, C, Right]): (Left, Right) = {
      val left = bfLeft(repr)
      val right = bfRight(repr)
      val it = repr.toIterator
      while (it.hasNext) {
        f(it.next) match {
          case Left(next) => left += next
          case Right(next) => right += next
        }
      }
      left.result -> right.result
    }
    
    
    def zipAnd[B,C,NewRepr](other: TraversableOnce[B])(f: (A,B) => C)
    (implicit bf: CanBuildFrom[Repr, C, NewRepr]): NewRepr = {
      val res = bf(repr)
      val it0 = repr.toIterator
      val it1 = other.toIterator
      while (it0.hasNext && it1.hasNext) {
        val next = f(it0.next, it1.next)
        res += next
      }
      res.result
    }
    
  }
  
  implicit class MutBufferHelper[A](private val repr: mutable.Buffer[A]) {
    def filter_!(p: A => Boolean) = {
      var removed = 0
      var i = 0
      while (i < repr.size) {
        val e = repr(i)
        if (p(e)) {
          if (removed != 0)
            repr(i-removed) = e
        } else {
          removed += 1
        }
        i += 1
      }
      repr.trimEnd(removed)
    }
  }
  
  
  
  
}
