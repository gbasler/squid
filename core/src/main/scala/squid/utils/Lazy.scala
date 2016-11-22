package squid.utils

/** Cheap Lazy implementation for pure computations */
final class Lazy[+A <: AnyRef](vl: () => A, computeWhenShow: Boolean) {
  private[this] var computedValue: A = null.asInstanceOf[A]
  private[this] var computing = false
  def isComputing = computing
  def computed = computedValue != null
  def value = {
    if (computedValue == null) {
      val wasComputing = computing
      computing = true
      try computedValue = vl()
      finally computing = wasComputing
    }
    computedValue
  }
  override def toString = s"Lazy(${if (computed || computeWhenShow) value else "..."})"
}
object Lazy {
  def apply[A <: AnyRef](vl: => A, computeWhenShow: Boolean = true) =
    new Lazy(() => vl, computeWhenShow)
}

