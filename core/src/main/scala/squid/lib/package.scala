package squid

package object lib {
  
  def IfThenElse[A](cond: Boolean, thn: => A, els: => A): A =
    if (cond) thn else els
  
  def While(cond: => Boolean, loop: => Unit): Unit =
    while (cond) loop
  
  def Imperative[A](effects: Any*)(result: A): A = result
  
  @inline def And(lhs: Boolean, rhs: => Boolean) = lhs && rhs
  @inline def Or(lhs: Boolean, rhs: => Boolean) = lhs || rhs
  
  
  final class ThunkParam private[lib]()
  private val ThunkParam = new ThunkParam
  def ThunkArg: ThunkParam = ThunkParam
  
  
  case class Var[A](var value: A) {
    def := (that: A) = value = that
    def ! = value
  }
  
  // More confusing than useful, especially since it seems to be automatically imported along with Var:
  //implicit def readVar[A](v: Var[A]): A = v!
  
  
  def uncurried0[b](f: => b): () => b =
    () => f
  def uncurried1[a, b](f: a => b): a => b =   // Not actually used, just here for syntactic completeness ^_^
    (x1) => f(x1)
  def uncurried2[a1, a2, b](f: a1 => a2 => b): (a1, a2) => b =
    (x1, x2) => f(x1)(x2)
  def uncurried3[a1, a2, a3, b](f: a1 => a2 => a3 => b): (a1, a2, a3) => b =
    (x1, x2, x3) => f(x1)(x2)(x3)
  def uncurried4[a1, a2, a3, a4, b](f: a1 => a2 => a3 => a4 => b): (a1, a2, a3, a4) => b =
    (x1, x2, x3, x4) => f(x1)(x2)(x3)(x4)
  def uncurried5[a1, a2, a3, a4, a5, b](f: a1 => a2 => a3 => a4 => a5 => b): (a1, a2, a3, a4, a5) => b  =
    (x1, x2, x3, x4, x5) => f(x1)(x2)(x3)(x4)(x5)
  
  
  
  final class DummyRecord
  
  import scala.annotation.{StaticAnnotation, compileTimeOnly}
  class ExtractedBinder extends StaticAnnotation
  
}
