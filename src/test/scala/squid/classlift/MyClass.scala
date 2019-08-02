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
  
  def foz(x: Int) = this.baz + x
  def foo(str: String) = bar(str.length)
  def bar(x: Int) = x + 1
  def fooRef(x: Int) = this.foo(x.toString)
  val baz = MyClass.swap(1,2)('lol)._2._2
  def lol(a: Int = 0, b: Int, c: String, d: String = "3"): String = a + b + c + d
  
  // TODO special support for varargs:
  //def varargFoo(xs: Int*): Int = xs.sum + 1
  //def argVarargFoo(s: String, xs: Int*): Int = s.length + xs.sum + 1
  
  var bazzz = MyClass.cst
  
  def oops = Ooops.oopsy(this)
  
}
/* An external object to exercise mutual type dependencies with lifted definitions. */
object Ooops {
  def oopsy(cls: MyClass) = cls.bazzz
  def oopsy2[A](cls: MyClass2, a: A) = (cls.mut,a)
  def oopsy2A[A](cls: MyClass2.A, a: A) = (cls.mat,a)
}
object MyClass {
  
  var cst = 42
  
  def x = MyClass.this.cst
  
  def foo(x: Int): Int = MyClass.foo(x.toLong).toInt
  def foo(x: Long): Long = x + 1
  def foo(x: Float): Float = x + 1
  def foo(x: String): String = x + 1
  def foo(x: Double): Double = x + 1
  
  def recLol(x: Int): Int = if (x <= 0) 0 else recLol(x-1)+1
  def recLolParam[A](x: Int)(r: A): A = if (x <= 0) r else recLolParam(x-1)(r)
  
  def bar(x: Int)(y: Double) = x * y
  
  def foobar(x: Int, y: Double) = bar(foo(x))(y)
  def foobarCurried(x: Int)(y: Double) = bar(foo(x))(y)
  
  def baz(x: Int, y: Int) = swap((x,y))('test)._2._2
  def swap[A](x: (A,A))(name: Symbol) = name -> x.swap
  
  def get(c: MyClass): Int = c.bar(0)
  def getMut(c: MyClass): Int = c.bazzz
  
}

@lift
object OrphanObject {
  
  def test[A](a: A) = (a,a)

  def varargFoo(xs: Int*): Int = xs.sum + 1

  def argVarargFoo(s: String, xs: Int*): Int = s.length + xs.sum + 1
  
  // TODO implement
  //@doNotLift
  //def oops = new { def unsupported = 123 } // Squid does not currently support local class definitions
  
}

@lift
class ClassWithParam(val arg: Int)
