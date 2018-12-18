package squid.utils

trait CachedHashCode { 
  private[this] var cachedHashCode = Int.MinValue
  override def hashCode = if (cachedHashCode == Int.MinValue) {
    val h = super.hashCode()
    cachedHashCode = h
    h
  } else cachedHashCode
}
