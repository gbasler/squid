package scp

package object utils {
  
  type Bool = Boolean
  
  
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
  
  
  implicit class GenHelper[A](val __self: A) extends AnyVal {
    
    def |> [B] (rhs: A => B): B = rhs(__self)
    
    /**A lesser precedence one! */
    def /> [B] (rhs: A => B): B = rhs(__self)
    
    /** 
     * A helper to write left-associative applications, mainly used to get rid of paren hell
     * Example:
     *   println(Id(Sym(f(chars))))
     *   println(Id <|: Sym.apply <|: f <|: chars)  // `Sym` needs `.apply` because it's overloaded
     */
    def <|: [B] (lhs: A => B): B = lhs(__self)
    
    def withTypeOf[T >: A](x: T) = __self: T
    
  }
  implicit class FunHelper[A,B](val __self: A => B) extends AnyVal {
    def <| (rhs: A): B = __self(rhs)
    def |>: (lhs: A): B = __self(lhs)
  }
  /*
  implicit class FunHelper2[A,B](val __self: (=>A) => B) extends AnyVal {
    def <| (rhs: => A): B = __self(rhs)
  }
  */
  
  implicit class SafeEq[T](val self: T) extends AnyVal {
    def === (that: T) = self == that
  }
  
  
}

