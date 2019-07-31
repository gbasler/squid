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
//import scala.reflect.runtime.universe.TypeTag
import scala.reflect.runtime.universe.{TypeTag,WeakTypeTag}

trait Definitions extends Base {
  
  type FieldGetter <: MtdSymbol
  type FieldSetter  <: MtdSymbol
  
  sealed abstract class ClassTemplate {
    val name: String
    val typeParams: List[TypSymbol]
    def unprocessed: Class
  }
  
  // TODO a way to adapt existing code to use the monomorphized version... though it would not work if it's used with abstract type args...
  // TODO for monomorphized classes,
  //      We should ideally generate a match type dispatching to the correct impl...
  //      But since that's impossible in Scala 2, we need to transform all the type references everywhere...
  
  case class PolyClass(name: String, typeParams: List[WeakTypeTag[_]], monomorphize: List[CodeType[_]] => Class) {
    // TODOne way to genrate the non-monomorphized version?
    lazy val unprocessed: Class = monomorphize(typeParams.map(tag => CodeType(uninterpretedType(tag.asInstanceOf[TypeTag[_]]))))
  }
  case class MonomorphizationError(reason: String)
  abstract class MonomorphizedClass(name: String) extends Class(name) {
    def adaptCode[T,C](cde: Code[T,C]): Either[MonomorphizationError, Code[T,C]] = ???
  }
  
  // TODO MonoClass[C]?
  abstract class Class(val name: String) extends ClassTemplate {
    type Scp
    
    //println(scala.reflect.runtime.universe.weakTypeOf[Scp])
    //println(implicitly[WeakTypeTag[Scp]].asInstanceOf[TypeTag[_]])
    
    val typeParams: List[TypSymbol] = Nil
    def unprocessed = this
    
    val fields: List[Field[_]]
    val methods: List[Method[_]]
    
    case class Field[A0: CodeType](name: String, get: FieldGetter, set: Option[FieldSetter], init: Code[A0,Scp]) {
      type A = A0
      val A: CodeType[A] = implicitly[CodeType[A]]
    }
    case class Method[A0: CodeType](symbol: MtdSymbol, body: Code[A0,Scp]) {
      type A = A0
      val A: CodeType[A] = implicitly[CodeType[A]]
      
      println(s"METHOD ${symbol} = ${body}")
    }
    
    //sealed abstract class MethodTransformation
    sealed abstract class MethodTransformation[-A]
    case object Remove extends MethodTransformation[Any]
    case class Rewrite[A](newBody: Code[A,Scp]) extends MethodTransformation[A]
    
    //def transform(trans: Map[Method[_],MethodTransformation])
    def transform(trans: List[MethodTransformation[_]]) = ???
    
    def mkField[A](get: OpenCode[A], set: Option[OpenCode[Unit]]): Field[A] = {
      println(get, set)
      //Field[Unit](null,null,None,null)(Predef.implicitType[Unit])
      null
      //???
    }
    
  }
  
  
}
