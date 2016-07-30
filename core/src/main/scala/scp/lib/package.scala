package scp

package object lib {
  
  def IfThenElse[A](cond: Boolean, thn: => A, els: => A): A =
    if (cond) thn else els
  
  def While(cond: => Boolean, loop: => Unit): Unit =
    while (cond) loop
  
  def Imperative[A](effects: Any*)(result: A): A = result
  
  
  final class ThunkParam private[lib]()
  private val ThunkParam = new ThunkParam
  def ThunkArg: ThunkParam = ThunkParam
  
  
  case class Var[A](var value: A) {
    def := (that: A) = value = that
    def ! = value
  }
  implicit def readVar[A](v: Var[A]): A = v!
  
  
  final class DummyRecord
  
}
