package haskell

// Scala does not have first-class polymorphic functions yet, so we have to do that
abstract class Builder[A] {
  def apply[B](cons: A => B => B)(nil: B): B
}

object Prelude {
  
  //def foldr[A,B]: (A => B => B) => B => List[A] => B = f => b => ls => ls.foldRight(b)(f(_)(_))
  def foldr[A,B]: (A => B => B) => B => List[A] => B = k => z => ls => ls match {
    case x::xs => k(x)(foldr(k)(z)(xs))
    case Nil => z
  }
  def build[A]: Builder[A] => List[A] = b => b(::)(Nil)
  
  def sum: List[Int] => Int = _.sum
  def + : Int => Int => Int = a => b => a + b
  
  def map[A,B]: (A => B) => List[A] => List[B] = f => _ map f
  //def mapBuild[A](f: A => B, xs: List[A]): Builder[List[A]] = new Builder[List[A]] {
  //  def apply[B](cons: List[A] => B => B)(nil: B): B = ???
  //}
  //def mapBuild[A,B](f: A => B, xs: List[A]): Builder[List[B]] = new Builder[List[B]] {
  def mapBuild[A,B](f: A => B, xs: List[A]): Builder[B] = new Builder[B] {
    //def apply[C](cons: B => C => C)(nil: C): C = foldr(cons compose f)(nil)(xs)
    def apply[C](cons: B => C => C)(nil: C): C = foldr(compose(cons)(f))(nil)(xs)
  }
  //def mapBuild[A,B](f: A => B, xs: List[A]): Builder[B] = ???
  
  ////def magicBuild[A](cons: A => Any => Any)(nil: Any): Any = ???
  //def magicBuild[A,B](f: ((A => Any => Any) => Any) => Any): List[B] = ???
  //def magicBuild[A](f: ((A => Nothing => Any) => Nothing) => Any): List[A] = ???
  def magicBuild[A,B](f: (A => B => B) => B => B): List[A] =
    f.asInstanceOf[(A => List[A] => List[A]) => List[A] => List[A]](::)(Nil)
  
  //def down: Int => List[Int] = 0 to _ toList
  def down: Int => List[Int] = _ until 0 by -1 toList
  
  //def downAux(n: Int): (Int => List[Int] => List[Int]) => List[Int] => List[Int] =
  //  co => ni => if (n == 0) ni else co(n)(downAux(n-1)(co)(ni))
  
  // down' :: Num t1 => t1 -> (t1 -> t2 -> t2) -> t3 -> t2
  def downAux(n: Int): (Any => Any => Any) => Any => Any =
    co => ni => if (n == 0) ni else co(n)(downAux(n-1)(co)(ni))
  
  def downBuild(n: Int): Builder[Int] = new Builder[Int] {
    def apply[B](cons: Int => B => B)(nil: B): B = if (n == 0) nil else cons(n)(downBuild(n-1)(cons)(nil))
  }
  
  def :: [A] : A => List[A] => List[A] = a => ls => a :: ls
  def compose[A,B,C](f:B=>C)(g:A=>B) = f compose g
  
  
  def loremipsum = "Lorem ipsum dolor sit amet".toList
  def ord: Char => Int = _.toInt
  def max: List[Int] => Int = _.max
  
}
