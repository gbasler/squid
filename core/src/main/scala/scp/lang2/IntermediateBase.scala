package scp
package lang2

trait IntermediateBase extends Base {
  
  // TODO IR and IRType, irTypeOf, typeRepOf, repType, etc.
  
  
  def repType(r: Rep): TypeRep
  
  def reinterpret(r: Rep, newBase: Base): newBase.Rep
  
  
  
}


