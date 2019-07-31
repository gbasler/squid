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

package squid.lang

import scala.reflect.runtime.universe.TypeTag

/** The base trait defining, in the tagless final style, the core type language of Squid:
  *   type application, constant type and uninterpreted types if supported for those that do not fall into these categories. */
trait TypingBase { self: Base =>
  
  /** Internal, untype representation of code type */
  type TypeRep <: AnyRef  // AnyRef bound so it can be used in squid.utils.Lazy (see EmbeddedType.asStaticallyAppliedType)
  
  /** A type application with prefix `self`; for example:
    *     typeApp('scala.Predef.type', 'DummyImplicit', Nil) ~~> 'scala.Predef.DummyImplicit'
    *     typeApp('squid.feature.TrickyTypes.ModularMetaprog', 'Typ', Nil) ~~> 'squid.feature.TrickyTypes.ModularMetaprog#Typ' */
  def typeApp(self: TypeRep, typ: TypSymbol, targs: List[TypeRep]): TypeRep
  
  /** A type application where the type symbol is static (so no prefix needed); for example:
    *     typeApp('scala.collection.immutable.List', 'Int') => 'scala.collection.immutable.List[Int]'*/
  def staticTypeApp(typ: TypSymbol, targs: List[TypeRep]): TypeRep
  
  /** The singleton type of a constant literal. */
  def constType(value: Any, underlying: TypeRep): TypeRep
  
  /** The singleton type of a stable `val` definition; the `self` type here is usually required/assumed to be a simple
    * path made of calls to `staticModuleType` and `valType`. */
  def valType(self: TypeRep, valName: String): TypeRep
  
  /** A fallback function to embed a type usign its Scala-reflection type tag. */
  def uninterpretedType[A: TypeTag]: TypeRep
  
  type TypSymbol
  def loadTypSymbol(fullName: String): TypSymbol 
  def loadMtdTypParamSymbol(mtd: MtdSymbol, name: String): TypSymbol = ???
  
  /** These special type symbols are supposed to be turned into types using staticTypeApp only. */
  type TypParam <: TypSymbol
  // FIXME type:
  def typeParam(name: String): TypeRep = ???
  
  def typLeq(a: TypeRep, b: TypeRep): Boolean
  def typEq(a: TypeRep, b: TypeRep): Boolean = typLeq(a,b) && typLeq(b,a)
  
  
  final def staticModuleType(fullName: String): TypeRep = staticTypeApp(loadTypSymbol(fullName+"$"), Nil)
  
  implicit class TypingRepOps(private val self: TypeRep) {
    def <:< (that: TypeRep) = typLeq(self, that)
    def =:= (that: TypeRep) = typEq(self, that)
  }
  
  
}
