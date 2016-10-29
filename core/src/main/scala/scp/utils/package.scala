package scp

package object utils {
  
  type Bool = Boolean
  
  
  import scala.language.existentials
  type ? = t forSome{type t}
  
  /** Tag type to be interpreted by quasiquotes as an existential named [Unknown Context],
    * useful to ensure rewrite rules are fully parametric in the context of the terms they match */
  final class UnknownContext private() // TODO extend <extruded type>
  
  
  implicit final class Andable[T](private val self: T) extends AnyVal {
    
    @inline def and(f: T => Unit) = { f(self); self }
    
    @inline def oh_and(effect: Unit) = self
    
    @inline def but_before(f: => Unit) = { f; self }
    
    @inline def before[A](x: A) = x
    @inline def !> [A](x: A) = x
    
  }
  
  
  implicit final class GenHelper[A](private val __self: A) extends AnyVal {
    
    @inline def into [B] (rhs: A => B): B = rhs(__self)
    
    @inline def |> [B] (rhs: A => B): B = rhs(__self)
    @inline def |>? [B] (rhs: PartialFunction[A, B]): Option[B] = rhs andThen Some.apply applyOrElse (__self, Function const None)
    
    @inline def >> (rhs: A => A): A = rhs(__self)
    @inline def >>? (rhs: PartialFunction[A, A]): A = rhs.applyOrElse(__self, Function const __self)
    
    /**A lesser precedence one! */
    @inline def /> [B] (rhs: A => B): B = rhs(__self)
    
    /** 
     * A helper to write left-associative applications, mainly used to get rid of paren hell
     * Example:
     *   println(Id(Sym(f(chars))))
     *   println(Id <|: Sym.apply <|: f <|: chars)  // `Sym` needs `.apply` because it's overloaded
     */
    @inline def <|: [B] (lhs: A => B): B = lhs(__self)
    
    def withTypeOf[T >: A](x: T) = __self: T
    
    @inline def If (cond: Bool) = if (cond) Some(__self) else None
    @inline def If (cond: A => Bool) = if (cond(__self)) Some(__self) else None
    @inline def IfNot (cond: Bool) = if (!cond) Some(__self) else None
    @inline def IfNot (cond: A => Bool) = if (!cond(__self)) Some(__self) else None
    
  }
  
  
  implicit class OptionHelper[A](private val __self: Option[A]) extends AnyVal {
    def Else[B >: A](x: => B) = __self getOrElse x
  }
  @inline def some[A](x: A): Option[A] = Some(x)
  @inline def none = None
  
  implicit class FunHelper[A,B](val __self: A => B) extends AnyVal {
    def <| (rhs: A): B = __self(rhs)
    def |>: (lhs: A): B = __self(lhs)
  }
  /*
  implicit class FunHelper2[A,B](val __self: (=>A) => B) extends AnyVal {
    def <| (rhs: => A): B = __self(rhs)
  }
  */
  
  type |>[A, F[_]] = F[A]
  
  def ignore = (_: Any) => ()
  def pairWith[A,B](f: A => B)(x: A) = x -> f(x)
  
  
  implicit class SafeEq[T](val self: T) extends AnyVal {
    def === (that: T) = self == that
    def =/= (that: T) = self != that
  }
  
  
  implicit class StringOps(private val self: String) extends AnyVal {
    import collection.mutable
    def splitSane(Sep: Char) = {
      val buf = mutable.ArrayBuffer(new StringBuilder)
      for (c <- self) if (c == Sep) buf += new StringBuilder else buf.last append c
      buf.map(_.toString)
    }
    def mapLines(f: String => String) = splitSane('\n') map f mkString "\n"
    def indent(pre: String) = mapLines(pre + _)
    def indent: String = indent("\t")
  }
  
  type -> [+A,+B] = (A,B)
  object -> {
    def unapply[A,B](ab: (A,B)) = Some(ab)
  }
  
  
  def If[A](cond: Boolean)(thn: A) = if (cond) Some(thn) else None
  
  
  def wtf = wth("Program reached and unexpected state.")
  def wth(msg: String) = throw new Exception(s"Internal Error: $msg")
  
  object oh {
    def wait(msg: String) = wth(msg)
  }
  
  
  
}





