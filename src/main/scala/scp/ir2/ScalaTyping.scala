package scp
package ir2

import lang2._
import utils.meta.RuntimeUniverseHelpers
import utils._

import scala.reflect.runtime.universe.TypeTag

/**
  * Created by lptk on 02/08/16.
  * 
  * TODO complete (cf: old version of ScalaTyping)
  * 
  */
trait ScalaTyping extends TypingBase { self: Base =>
  
  def uninterpretedType[A: TypeTag]: TypeRep =
    UninterpretedType[A](RuntimeUniverseHelpers.sru.typeTag[A])
  
  def typeApp(self: Rep, typ: TypSymbol, targs: List[TypeRep]): TypeRep = TypeApp(self, typ, targs)
  
  def recordType(fields: List[(String, TypeRep)]): TypeRep = ??? // TODO
  
  sealed trait TypeRep
  case class TypeApp(self: Rep, typ: TypSymbol, targs: List[TypeRep]) extends TypeRep
  case class UninterpretedType[A]
    (tag: TypeTag[A]) extends TypeRep
  
  
  
}
