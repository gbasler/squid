package aad

import squid.IR
import squid.IR.Predef._ // import the ‘Code’, ‘CodeType’ and ‘code’ functionalities

trait AAD[R] {
  def lit(d: Double): R

  def +(a: R, b: R): R

  def *(a: R, b: R): R

  def one: R

  def zero: R
}

class AADCode[T: CodeType, C](val sr: Code[AAD[T], C]) extends AAD[Code[T, C]] {

  def lit(d: Double): Code[T, C] = code"$sr.lit(${Const(d)})"

  def +(a: Code[T, C], b: Code[T, C]): Code[T, C] = code"$sr.+($a, $b)"

  def *(a: Code[T, C], b: Code[T, C]): Code[T, C] = code"$sr.*($a, $b)"

  def one: Code[T, C] = code"$sr.one"

  def zero: Code[T, C] = code"$sr.zero"
}
/*
object AADDouble extends AAD[Double] {

  def lit(d: Double): Double = d

  def +(a: Double, b: Double) = a + b

  def *(a: Double, b: Double) = a * b

  def one: Double = 1.0

  def zero: Double = 0.0

}
*/
object StagedSemantics {
  implicit def aadCode[T: CodeType, C](implicit cde: Code[AAD[T], C]): AAD[Code[T, C]] = new AADCode[T, C](cde)

}

object Ni extends App {
  import StagedSemantics._
  val pgrm = code"AAD[Double].lit(42)"
  val x = pgrm.compile

  println(x)
}