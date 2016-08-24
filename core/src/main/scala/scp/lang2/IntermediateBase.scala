package scp
package lang2

import scp.utils.meta.RuntimeUniverseHelpers.sru

trait IntermediateBase extends Base {
  
  // TODO IR and IRType, irTypeOf, typeRepOf, repType, etc.
  
  
  def repType(r: Rep): TypeRep
  
  def reinterpret(r: Rep, newBase: Base): newBase.Rep
  
  
  implicit class IntermediateRepOps(private val self: Rep) {
    def typ = repType(self)
  }
  
  implicit class IntermediateIROps[Typ,Ctx](private val self: IR[Typ,Ctx]) {
    /* Note: making it `def typ: IRType[Typ]` woule probably be unsound! */
    def typ: IRType[_ <: Typ] = `internal IRType`(trep)
    def trep = repType(self.rep)
    
    import scala.language.experimental.macros
    def subs[T1,C1](s: (Symbol, IR[T1,C1])): Any = macro quasi2.QuasiMacros.subsImpl[T1,C1]
    
    def run(implicit ev: {} <:< Ctx): Typ = {
      val Inter = new ir2.BaseInterpreter
      reinterpret(self.rep, Inter).asInstanceOf[Typ]
    }
    
    def showScala: String = {
      sru.showCode( reinterpret(self.rep, quasi2.MetaBases.Runtime.ScalaReflectionBase) )
    }
    
  }
  
  
  
}


