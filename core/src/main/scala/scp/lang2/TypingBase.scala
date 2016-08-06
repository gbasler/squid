package scp
package lang2

import scala.reflect.runtime.universe.TypeTag

trait TypingBase { self: Base =>
  
  type TypeRep
  
  def uninterpretedType[A: TypeTag]: TypeRep
  def typeApp(self: Rep, typ: TypSymbol, targs: List[TypeRep]): TypeRep
  def recordType(fields: List[(String, TypeRep)]): TypeRep
  
  type TypSymbol
  def loadTypSymbol(fullName: String): TypSymbol 
  
  def typEq(a: TypeRep, b: TypeRep): Boolean
  
  
  
  implicit class RepOps(private val self: TypeRep) {
    def =:= (that: TypeRep) = typEq(self, that)
  }
  
  
}
