package squid

package object lib {
  
  @transparent
  def IfThenElse[A](cond: Boolean, thn: => A, els: => A): A =
    if (cond) thn else els
  
  @transparent
  def While(cond: => Boolean, loop: => Unit): Unit =
    while (cond) loop
  
  @transparencyPropagating
  def Imperative[A](effects: Any*)(result: A): A = result
  
  @inline @transparent def And(lhs: Boolean, rhs: => Boolean) = lhs && rhs
  @inline @transparent def Or(lhs: Boolean, rhs: => Boolean) = lhs || rhs
  
  
  final class ThunkParam private[lib]()
  private val ThunkParam = new ThunkParam
  def ThunkArg: ThunkParam = ThunkParam
  
  
  case class Var[A](var value: A) {
    def := (that: A) = value = that
    def ! = value
  }
  
  // More confusing than useful, especially since it seems to be automatically imported along with Var:
  //implicit def readVar[A](v: Var[A]): A = v!
  
  
  @transparencyPropagating
  def uncurried0[b](f: => b): () => b =
    () => f
  
  @transparencyPropagating
  def uncurried1[a, b](f: a => b): a => b =   // Not actually used, just here for syntactic completeness ^_^
    (x1) => f(x1)
  
  @transparencyPropagating
  def uncurried2[a1, a2, b](f: a1 => a2 => b): (a1, a2) => b =
    (x1, x2) => f(x1)(x2)
  
  @transparencyPropagating
  def uncurried3[a1, a2, a3, b](f: a1 => a2 => a3 => b): (a1, a2, a3) => b =
    (x1, x2, x3) => f(x1)(x2)(x3)
  
  @transparencyPropagating
  def uncurried4[a1, a2, a3, a4, b](f: a1 => a2 => a3 => a4 => b): (a1, a2, a3, a4) => b =
    (x1, x2, x3, x4) => f(x1)(x2)(x3)(x4)
  
  @transparencyPropagating
  def uncurried5[a1, a2, a3, a4, a5, b](f: a1 => a2 => a3 => a4 => a5 => b): (a1, a2, a3, a4, a5) => b  =
    (x1, x2, x3, x4, x5) => f(x1)(x2)(x3)(x4)(x5)
  
  
  @transparent def nullValue[T] = null.asInstanceOf[T]
  
  /** Communicates the intention that this null value is never checked; mainly used to initialize variables with a value
    * that is never supposed to be accessed. */
  @transparent def uncheckedNullValue[T] = null.asInstanceOf[T]
  
  
  /** A dummy function to be used by internal compiler passes to hold temporary code; mainly used to avoid hygiene
    * problems arising from recursive extrusion (until hygienic context polymorphism is implemented). */
  @transparencyPropagating def placeHolder[T](id: String): T =
    throw new AssertionError(s"Tried to execute `$id` placeholder, which was not supposed to be compiled into final program.")
  
  
  final class DummyRecord
  
  import scala.annotation.{StaticAnnotation, compileTimeOnly}
  class ExtractedBinder extends StaticAnnotation
  
}
