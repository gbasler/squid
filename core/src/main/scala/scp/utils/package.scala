package scp

package object utils {
  
  import scala.language.existentials
  type ? = t forSome{type t}
  
  /** Tag type to be interpreted by quasiquotes as an existential named [Unknown Context],
    * useful to ensure rewrite rules are fully parametric in the context of the terms they match */
  final class UnknownContext private()
  
  
  implicit class Andable[T](val self: T) extends AnyVal {
    
    def and(f: T => Unit) = { f(self); self }
    
    def oh_and(f: => Unit) = { f; self }
    
    def but_before(f: => Unit) = { f; self }
    
  }
  
  implicit class SafeEq[T](val self: T) extends AnyVal {
    def === (that: T) = self == that
  }
  
  
}

