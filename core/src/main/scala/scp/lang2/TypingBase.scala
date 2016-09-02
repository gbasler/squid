package scp
package lang2

import scala.reflect.runtime.universe.TypeTag

trait TypingBase { self: Base =>
  
  type TypeRep
  
  def uninterpretedType[A: TypeTag]: TypeRep
  def typeApp(self: TypeRep, typ: TypSymbol, targs: List[TypeRep]): TypeRep // make targs a vararg?
  def staticTypeApp(typ: TypSymbol, targs: List[TypeRep]): TypeRep 
  def recordType(fields: List[(String, TypeRep)]): TypeRep
  def constType(value: Any, underlying: TypeRep): TypeRep
  
  type TypSymbol
  def loadTypSymbol(fullName: String): TypSymbol 
  
  def typLeq(a: TypeRep, b: TypeRep): Boolean
  def typEq(a: TypeRep, b: TypeRep): Boolean = typLeq(a,b) && typLeq(b,a)
  
  
  
  
  final def staticModuleType(fullName: String): TypeRep = staticTypeApp(loadTypSymbol(fullName+"$"), Nil)
  
  implicit class RepOps(private val self: TypeRep) {
    def <:< (that: TypeRep) = typLeq(self, that)
    def =:= (that: TypeRep) = typEq(self, that)
  }
  
  
}
