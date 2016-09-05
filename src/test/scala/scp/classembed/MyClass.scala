package scp
package classembed

import quasi2.{embed, phase}

@embed
class MyClass {
  
}

object MyClass extends App with ir2.SquidObject {
  
  @phase('Sugar)
  def foo(x: Int): Int = MyClass.foo(x.toLong).toInt
  def foo(x: Long): Long = x + 1
  def foo(x: Float): Float = x + 1
  def foo(x: String): String = x + 1
  def foo(x: Double): Double = x + 1
  
  @phase('Sugar)
  def recLol(x: Int): Int = if (x <= 0) 0 else recLol(x-1)+1
  @phase('Sugar)
  def recLolParam[A](x: Int)(r: A): A = if (x <= 0) r else recLolParam(x-1)(r)
  
  //@phase('Main)
  def bar(x: Int)(y: Double) = x * y
  
  @phase('Sugar)
  def foobar(x: Int, y: Double) = bar(foo(x))(y)
  
  @phase('Sugar)
  def swap[A](x: (A,A))(name: Symbol) = name -> x.swap
  
}










