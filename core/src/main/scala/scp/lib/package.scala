package scp

package object lib {
  
  def IfThenElse[A](cond: Boolean, thn: => A, els: => A) =
    if (cond) thn else els
  
  def While(cond: => Boolean, loop: => Unit) =
    while (cond) loop
  
  def Imperative[A](effect: => Unit)(result: => A) =
    { effect; result }
  
}
