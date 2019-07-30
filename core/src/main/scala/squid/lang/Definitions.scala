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

package squid.lang

import squid.quasi
import squid.utils._

trait Definitions extends Base {
  
  type FieldGetter <: MtdSymbol
  type FieldSetter  <: MtdSymbol
  
  abstract class Class(name: String) {
    type Scp
    
    val fields: List[Field[_]]
    val methods: List[Method[_]]
    
    case class Field[A0: CodeType](name: String, get: FieldGetter, set: Option[FieldSetter], init: Code[A0,Scp]) {
      type A = A0
      val A: CodeType[A] = implicitly[CodeType[A]]
    }
    case class Method[A0: CodeType](symbol: MtdSymbol, body: Code[A0,Scp]) {
      type A = A0
      val A: CodeType[A] = implicitly[CodeType[A]]
    }
    
    //sealed abstract class MethodTransformation
    sealed abstract class MethodTransformation[-A]
    case object Remove extends MethodTransformation[Any]
    case class Rewrite[A](newBody: Code[A,Scp]) extends MethodTransformation[A]
    
    //def transform(trans: Map[Method[_],MethodTransformation])
    def transform(trans: List[MethodTransformation[_]]) = ???
    
    def mkField[A](get: OpenCode[A], set: Option[OpenCode[Unit]]): Field[A] = {
      println(get, set)
      ???
    }
    
  }
  
  
}
