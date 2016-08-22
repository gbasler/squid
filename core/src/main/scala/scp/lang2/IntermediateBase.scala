package scp
package lang2

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
    
    def run(implicit ev: {} <:< Ctx): Typ = {
      val Inter = new ir2.BaseInterpreter
      reinterpret(self.rep, Inter).asInstanceOf[Typ]
    }
    
  }
  
  
  
}


