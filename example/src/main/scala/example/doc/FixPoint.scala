package example.doc

object FixPoint {
  def Y[S,T](f: (S => T) => (S => T)): (S => T) = f(Y(f))(_:S)
  // TODO by-name version
}
