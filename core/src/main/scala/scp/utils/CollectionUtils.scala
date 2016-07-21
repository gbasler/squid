package scp.utils

import scala.collection.generic.CanBuildFrom

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
    
  }
  
  
}
