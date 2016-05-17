package scp

package object lib {
  
  def IfThenElse[A](cond: Boolean, thn: => A, els: => A) =
    if (cond) thn else els
  
  def While(cond: => Boolean, loop: => Unit) =
    while (cond) loop
  
  def Imperative[A](effect: Any)(result: A) = result  // TODO make effect a vararg
  
  
  final class ThunkParam private[lib]()
  private val ThunkParam = new ThunkParam
  def ThunkArg: ThunkParam = ThunkParam
  
  
  final class DummyRecord
  
}
