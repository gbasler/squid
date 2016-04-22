package scp
package lib


object IfThenElse {
  
  def apply[A](cond: Boolean, thn: => A, els: => A) =
    if (cond) thn else els
  
}

object While {
  
  def apply(cond: => Boolean, loop: => Unit) =
    while (cond) loop
  
}





