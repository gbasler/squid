package squid.utils

package object typing {
  
  // Scala does not have a built-in way to go from, e.g., List[A] to List[B] when implicitly[A <:< B]; this corrects that
  
  // cf. related: https://github.com/scalaz/scalaz/blob/d90a5b568588e44acd35a9d6ae0a4a1baae1aaeb/core/src/main/scala/scalaz/Tag.scala#L7
  def substCo    [A,B,F[+_]](x: F[A])(implicit ev: A <:< B): F[B] = x.asInstanceOf[F[B]]
  def substContra[A,B,F[-_]](x: F[A])(implicit ev: B <:< A): F[B] = x.asInstanceOf[F[B]]
  def subst      [A,B,F[ _]](x: F[A])(implicit ev: A =:= B): F[B] = x.asInstanceOf[F[B]]
  
  def substBoundedCo    [Bnd,A<:Bnd,B<:Bnd,F[+_<:Bnd]](x: F[A])(implicit ev: B <:< A): F[B] = x.asInstanceOf[F[B]]
  def substBoundedContra[Bnd,A<:Bnd,B<:Bnd,F[-_<:Bnd]](x: F[A])(implicit ev: A <:< B): F[B] = x.asInstanceOf[F[B]]
  def substBounded      [Bnd,A<:Bnd,B<:Bnd,F[ _<:Bnd]](x: F[A])(implicit ev: A =:= B): F[B] = x.asInstanceOf[F[B]]
  
  //implicit def singletonIsSingleton[A<:Singleton,B<:A]: A =:= B =
  // ^ this is less general than the following:
  /** Scala fail to see that when `T <: x.type`, then `T == x.type` */
  implicit def singletonIsSingleton[A<:Singleton,B](implicit ev: B <:< A): A =:= B = 
    =:=.tpEquals[A].asInstanceOf[A =:= B]
  
}
