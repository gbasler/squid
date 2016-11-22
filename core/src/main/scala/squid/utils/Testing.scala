package squid.utils

object Testing {
  
  implicit class TestOps[A](val __self: A) extends AnyVal {
    def as[T >: A] = __self
  }
  
}
