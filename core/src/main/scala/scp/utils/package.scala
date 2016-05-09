package scp

package object utils {
  
  import scala.language.existentials
  type ? = t forSome{type t}
  
  
  implicit class Andable[T](val self: T) extends AnyVal {
    
    def and(f: T => Unit) = { f(self); self }
    
    def oh_and(f: => Unit) = { f; self }
    
    def but_before(f: => Unit) = { f; self }
    
  }
  
  implicit class SafeEq[T](val self: T) extends AnyVal {
    def === (that: T) = self == that
  }
  
  
}

