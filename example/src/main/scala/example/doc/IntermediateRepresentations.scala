// Copyright 2017 EPFL DATA Lab (data.epfl.ch)
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

package example.doc

import squid.lib.transparencyPropagating
import squid.lib.transparent

// See: /doc/Intermediate_Representations.md
object IntermediateRepresentations extends App {
  
  import squid.ir.{SchedulingANF, StandardEffects}
  
  class Baz { @transparencyPropagating def baz: Baz = this }
  
  case class Foo() { def foo = 123 }
  case class Bar() { def bar = 456 }
  
  object Embedding extends SchedulingANF with StandardEffects {
    transparentMtds += methodSymbol[Foo]("foo")
    transparentMtds += methodSymbol[Foo.type]("apply")
    transparentTyps += typeSymbol[Bar]
    transparentTyps += typeSymbol[Bar.type]
  }
  {
    import Embedding.Predef._
    println(code"(new Foo).foo + 1") // let-binds
    println(code"Foo().foo + 1") // does not let-bind
    println(code"(new Bar).bar + 1") // does not let-bind
    println(code"(b:Baz) => b.baz.baz.baz") // does not let-bind
  }
  
}
