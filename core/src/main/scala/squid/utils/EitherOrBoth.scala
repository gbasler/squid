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
