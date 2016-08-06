package scp
package ir2

import lang2._
import utils._
import utils.meta.RuntimeUniverseHelpers.sru

import scala.reflect.runtime.universe.TypeTag

/** 
  * TODO complete (cf: old version of ScalaTyping)
  * 
  */
trait ScalaTyping extends TypingBase {
//self: Base =>
self: quasi2.QuasiBase => // QuasiBase required here essentially for `Extract` ...
  
  def uninterpretedType[A: TypeTag]: TypeRep =
    UninterpretedType[A](sru.typeTag[A])
  
  def typeApp(self: Rep, typ: TypSymbol, targs: List[TypeRep]): TypeRep = TypeApp(self, typ, targs)
  
  def recordType(fields: List[(String, TypeRep)]): TypeRep = ??? // TODO
  
  sealed trait TypeRep
  case class TypeApp(self: Rep, typ: TypSymbol, targs: List[TypeRep]) extends TypeRep
  case class UninterpretedType[A]
    (tag: TypeTag[A]) extends TypeRep
  
  
  
  def typEq(a: TypeRep, b: TypeRep): Boolean = ??? // TODO
  
  
  lazy val (funModule, funSym) = {
    val sym = sru.symbolOf[Any => Any]
    moduleObject(sym.owner.fullName, sym.owner.isPackage) -> loadTypSymbol(sym.fullName) // TODO mk function to get mod and typ directly from symbol..?
  }
  def funType(paramt: TypeRep, bodyt: TypeRep): TypeRep = typeApp(funModule, funSym, paramt :: bodyt :: Nil)
  
  
  def extractType(self: TypeRep, other: TypeRep, va: Variance): Option[Extract] = {
    //debug("Match "+tp+" with "+this)
    //(self, other) match {
    //  case (_, TypeHoleRep(_)) => throw new IllegalArgumentException // TODO BE
    //  case (TypeHoleRep(holeName), tp) => Some(Map(), Map(holeName -> tp), Map())
    //  //case (ScalaTypeRep(tag0, targs0 @ _*), ScalaTypeRep(tag1, targs1 @ _*)) =>
    //  case (tr0: ScalaTypeRep, tr1: ScalaTypeRep) =>
    //    extractTp(tp.typ, va)
    //}
    //??? // TODO
    Some(EmptyExtract) // TODO
  }

  
  
}







