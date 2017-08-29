package sfusion2

import squid.lib.transparencyPropagating
import squid.lib.transparent
import squid.quasi.overloadEncoding
import squid.utils._
import squid.quasi.{phase, embed, dbg_embed}

/**
  * Created by lptk on 13/06/17.
  */
@embed
object CPSOptions {
  
  type Option[A] = (A => Unit) => Unit
  def None: Option[Nothing] = _ => ()
  def Some[A](a: A): Option[A] = _(a)
  
  //implicit class A(a:Int) {
  //}
  
  //def optMap[A,B](opt: Option[A])(f: A => B) = opt(f)
  
}
//object Some {
//  
//}
//object None {
//  
//}
