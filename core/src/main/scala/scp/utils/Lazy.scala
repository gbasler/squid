package scp.utils

/** Cheap Lazy implementation for pure computations */
final class Lazy[+A <: AnyRef](vl: () => A) {
  private[this] var computedValue: A = null.asInstanceOf[A]
  private[this] var computing = false
  def isComputing = computing
  def value = {
    if (computedValue == null) {
      val wasComputing = computing
      computing = true
      try computedValue = vl()
      finally computing = wasComputing
    }
    computedValue
  }
}
object Lazy {
  def apply[A <: AnyRef](vl: => A) = new Lazy(() => vl)
}

