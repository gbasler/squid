// Copyright 2019 EPFL DATA Lab (data.epfl.ch)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package squid
package classlift

import squid.quasi._

@lift
class MyClass {
  /*
  // TODO proper error when name clash and no possibility to define outer staging object
  //def foo(x: Int) = bar + x
  //val bar = 666
  
  @phase('Sugar)
  def foz(x: Int) = this.baz + x
  def foo(x: Int) = baz + x
  def fooRef(x: Int) = this.foo(x)
  val baz = MyClass.swap(1,2)('lol)._2._2

  @phase('Sugar)
  def varargFoo(xs: Int*): Int = xs.sum + 1

  @phase('Sugar)
  def argVarargFoo(s: String, xs: Int*): Int = s.length + xs.sum + 1

  @overloadEncoding
  def lol(a: Int = 0, b: Int, c: String, d: String = "3"): String = a + b + c + d
  */
  
  val baz = MyClass.swap(1,2)('lol)._2._2
  def foo(x: Int) = baz + x
  
}

object MyClass {
  
  //val reflect = lift.thisClass
  //val reflect = dbg_lift.thisClass
  def reflect(d: squid.lang.Definitions): d.TopLevel.Object[MyClass.type] = dbg_lift.thisClass(d)
  
  /*
  @phase('Sugar)
  def foo(x: Int): Int = MyClass.foo(x.toLong).toInt
  
  @phase('Sugar)
  def varargFoo(xs: Int*): Int = xs.sum + 1
  
  @phase('Sugar)
  def argVarargFoo(s: String, xs: Int*): Int = s.length + xs.sum + 1
  
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
  def foobarCurried(x: Int)(y: Double) = bar(foo(x))(y)
  */
  
  //def baz(x: Int, y: Int) = swap((x,y))('test)._2._2
  //def baz(x: Int, y: Int) = (x,y)
  def baz(u: Int, v: Int) = (u,v)
  
  @phase('Sugar)
  def swap[A](x: (A,A))(name: Symbol) = name -> x.swap
  
  @phase('Sugar)
  //val cst = 42
  var cst = 42
  
}
/*
@embed
object OrphanObject extends ir.SquidObject {
  def test[A](a: A) = (a,a)

  def varargFoo(xs: Int*): Int = xs.sum + 1

  def argVarargFoo(s: String, xs: Int*): Int = s.length + xs.sum + 1
  
  @doNotEmbed
  def oops = new { def unsupported = 123 } // Squid does not currently support local class definitions
  
}

@embed
class ClassWithParam(@phase('Sugar) val arg: Int)
*/
