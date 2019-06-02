package squid.utils

trait CachedHashCode { self: Product =>
  private[this] var cachedHashCode = Int.MinValue
  override def hashCode = if (cachedHashCode == Int.MinValue) {
    //val h = super.hashCode()
    // ^ This is wrong! when CachedHashCode was applied on case classes, this referred to Object's hashCode!
    //   See also https://stackoverflow.com/questions/39775202/scala-case-class-with-cached-hashcode
    val h = scala.util.hashing.MurmurHash3.productHash(this)
    cachedHashCode = h
    h
  } else cachedHashCode
}
