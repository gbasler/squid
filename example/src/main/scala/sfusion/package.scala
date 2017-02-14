
import squid.utils._

/**
  * Created by lptk on 07/02/17.
  */
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

