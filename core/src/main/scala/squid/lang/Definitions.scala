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
  
  // TODO monomorphization
  // TODO for monomorphized classes,
  //      We should ideally generate a match type dispatching to the correct impl...
  //      But since that's impossible in Scala 2, we need to transform all the type references everywhere...
  
  
  //object TopLevel extends Scope[Any] {
  object TopLevel extends Scope {
    type Scp = Any
  }
  
  //trait Scope[+ParentScp] {
  //  type Scp <: ParentScp
  trait Scope { outerScope =>
    type Scp
    
    sealed abstract class Member
    val members: List[Member] = Nil
    
    trait ClassWithObject[C] extends ClassOrObject[C] {
      val companion: Some[Class[_]]
    }
    trait ClassWithoutObject[C] extends Class[C] {
      val companion: Some[Object[_]]
    }
    trait ObjectWithoutClass[C] extends Object[C] {
      val companion: None.type = None
    }
    
    abstract class Object[C](val name: String) extends ClassOrObject[C] {
      val companion: Option[Class[_]]
    }
    // FIXME typeParams should be a TypSymbol?
    //abstract class Class[C](val name: String, val typeParams: List[CodeType[_]]) extends ClassOrObject[C] {
    abstract class Class[C](val name: String, val typeParams: List[TypParam]) extends ClassOrObject[C] {
      val companion: Option[Object[_]]
    }
    //abstract class Object[C](val name: String) extends ClassOrObject[C] {
    //}
    //abstract class ClassOrObject[C] extends Member with Scope[Scp] {
    abstract class ClassOrObject[C] extends Member with Scope {
      type Scp <: outerScope.Scp
      val name: String
      
      //println(scala.reflect.runtime.universe.weakTypeOf[Scp])
      //println(implicitly[WeakTypeTag[Scp]].asInstanceOf[TypeTag[_]])
      
      //val typeParams: List[TypSymbol] = Nil
      def unprocessed = this
      
      val fields: List[Field[_]]
      val methods: List[Method[_,_]]
      
      abstract class FieldOrMethod[A] extends Member {
        val symbol: MtdSymbol
      }
      
      class Field[A0: CodeType](val name: String, val get: FieldGetter, val set: Option[FieldSetter], val init: Code[A0,Scp]) extends FieldOrMethod[A0] {
        type A = A0
        val A: CodeType[A] = implicitly[CodeType[A]]
        val symbol: MtdSymbol = get
      }
      //class Method[A0: CodeType, S <: Scp](val symbol: MtdSymbol, val tparams: List[CodeType[_]], val vparams: List[List[Variable[_]]], val body: Code[A0,S]) extends FieldOrMethod[A0] {
      class Method[A0: CodeType, S <: Scp](val symbol: MtdSymbol, val tparams: List[TypParam], val vparams: List[List[Variable[_]]], val body: Code[A0,S]) extends FieldOrMethod[A0] {
        type Scp = S
        type A = A0
        val A: CodeType[A] = implicitly[CodeType[A]]
        
        //println(s"METHOD ${symbol} = ${body}")
        println(s"METHOD $this")
        
        //override def toString = s"def ${symbol}[${tparams.map(_.rep).mkString(",")}]${vparams.map(vps => vps.map(vp =>
        override def toString = s"def ${symbol}[${tparams.mkString(",")}]${vparams.map(vps => vps.map(vp =>
          //s"${vp.`internal bound`}: ${vp.Typ.rep}"
          s"$vp"
        ).mkString("(",",",")")).mkString}: ${A.rep} = ${showRep(body.rep)}"
      }
      
      //sealed abstract class MethodTransformation
      sealed abstract class MethodTransformation[-A]
      case object Remove extends MethodTransformation[Any]
      case class Rewrite[A](newBody: Code[A,Scp]) extends MethodTransformation[A]
      
      //def transform(trans: Map[Method[_],MethodTransformation])
      def transform(trans: List[MethodTransformation[_]]) = ???
      
      //def mkField[A](get: OpenCode[A], set: Option[OpenCode[Unit]]): Field[A] = {
      //  println(get, set)
      //  //Field[Unit](null,null,None,null)(Predef.implicitType[Unit])
      //  null
      //  //???
      //}
      def mkField(name: String, get: MtdSymbol, set: Option[MtdSymbol], init: Rep)(typ: TypeRep): Field[_] =
        new Field[Any](name, get.asInstanceOf[FieldGetter],
          set.map(_.asInstanceOf[FieldSetter]),Code(init))(CodeType[Any](typ))
      
    }
    
  }
  
  
}
