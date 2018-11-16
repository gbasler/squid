package haskell

// Scala does not have first-class polymorphic functions yet, so we have to do that
abstract class Builder[A] {
  def apply[B](cons: A => B => B)(nil: B): B
}

object Prelude {
  
  def foldr[A,B]: (A => B => B) => B => List[A] => B = f => b => ls => ls.foldRight(b)(f(_)(_))
  def build[A]: Builder[A] => List[A] = b => b(::)(Nil)
  
  def sum: List[Int] => Int = _.sum
  def + : Int => Int => Int = a => b => a + b
  
  def down: Int => List[Int] = 0 to _ toList
  //def downAux(n: Int): (Int => List[Int] => List[Int]) => List[Int] => List[Int] =
  //  co => ni => if (n == 0) ni else co(n)(downAux(n-1)(co)(ni))
  def downBuild(n: Int): Builder[Int] = new Builder[Int] {
    def apply[B](cons: Int => B => B)(nil: B): B = if (n == 0) nil else cons(n)(downBuild(n-1)(cons)(nil))
  }
  
  def :: [A] : A => List[A] => List[A] = a => ls => a :: ls
  
}
