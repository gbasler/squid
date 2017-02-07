package squid
package classembed

import quasi.{embed, phase}
import quasi.{dbg_embed}

@embed
class MyGenClass[A](val a: A) {
  
  @phase('Sugar) def foo: A = a
  
  @phase('Sugar) def bar[B](f: A => B): B = f(foo)
  
}
object MyGenClass

