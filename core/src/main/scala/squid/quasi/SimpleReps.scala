package squid
package quasi

import lang.Base

/** Mix this in and import SimplePredef._ to forget about contexts completely! */
trait SimpleReps {  // TODO remove this obsolete trait
self: Base =>
  
  object SimplePredef {
    
    //type Rep[T] = IR[T,Any] // Note: used to be `IR[T,Nothing]`, but it interacted badly with implicit classes (scalac bug)
    type Rep[T] = Code[T]
    
    // For backward compat of some older programs:
    implicit def unsound[T](r: Code[T]): IR[T,Any] = r.asInstanceOf[IR[T,Any]]
    implicit def unsound2[T](r: IR[T,_]): Rep[T] = r.asInstanceOf[Rep[T]]
    
  }
  
}
