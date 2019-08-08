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

package squid.classlift

import squid.quasi._

@lift
class MyClass2 {
  def test = List(1,2,3)
  var mut = 42
  def oops = Ooops.oopsy2(this,'hi)
  def foo(mc: MyClass3) = mc.met + mut
}
object MyClass2 {
  
  var mit = 123
  
  class A {
    def oops = Ooops.oopsy2A(this,'hi)
    var mat = 123
  }
  object A
  
  def testo(x: Int) = {
    val mc = new MyClass2
    Some(mc.mut + x)
  }
  
}

@lift
class MyClass3 {
  var met = 345
  def getMet(els: Int) = if (met < 0) els else met
  def test = (new MyClass2).foo(this)
}
object MyClass3 {
}
