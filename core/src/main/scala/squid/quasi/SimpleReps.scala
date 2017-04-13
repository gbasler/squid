package squid
package quasi

import lang.Base

/** Mix this in and import SimplePredef._ to forget about contexts completely! */
trait SimpleReps {
self: Base =>
  
  object SimplePredef {
    
    type Rep[T] = IR[T,Any] // Note: used to be `IR[T,Nothing]`, but it interacted badly with implicit classes (scalac bug)
    implicit def unsound[T](r: IR[T,_]): Rep[T] = r.asInstanceOf[Rep[T]]
    
  }
  
}
