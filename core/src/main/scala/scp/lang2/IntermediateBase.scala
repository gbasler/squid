package scp
package lang2

import scp.quasi2.MetaBases
import utils.MacroUtils.MacroSetting
import scp.utils.meta.RuntimeUniverseHelpers.sru

trait IntermediateBase extends Base { ibase: IntermediateBase =>
  
  // TODO IR and IRType, irTypeOf, typeRepOf, repType, etc.
  
  
  def repType(r: Rep): TypeRep
  
  val DefaultExtrudedHandler = (bv: BoundVal) => throw ir2.IRException(s"Extruded bound variable cannot be reinterpreted: $bv")
  
  def reinterpret(r: Rep, newBase: Base)(extrudedHandle: (BoundVal => newBase.Rep) = DefaultExtrudedHandler): newBase.Rep
  
  //override def showRep(r: Rep) = showScala(r)
  private var showing = false
  override def showRep(r: Rep) = synchronized { if (showing) super.showRep(r) else try { showing = true; showScala(r) } finally { showing = false } }
  def showScala(r: Rep) = sru.showCode( scalaTree(r, bv => sru.Ident(sru.TermName(s"?${bv}?"))) )
  
  
  implicit class IntermediateRepOps(private val self: Rep) {
    def typ = repType(self)
  }
  
  implicit class IntermediateIROps[Typ,Ctx](private val self: IR[Typ,Ctx]) {
    /* Note: making it `def typ: IRType[Typ]` woule probably be unsound! */
    def typ: IRType[_ <: Typ] = `internal IRType`(trep)
    def trep = repType(self.rep)
    
    import scala.language.experimental.macros
    def subs[T1,C1](s: => (Symbol, IR[T1,C1])): IR[Typ,_ >: Ctx] = macro quasi2.QuasiMacros.subsImpl[T1,C1]
    @MacroSetting(debug = true) def dbg_subs[T1,C1](s: => (Symbol, IR[T1,C1])): IR[Typ,_ >: Ctx] = macro quasi2.QuasiMacros.subsImpl[T1,C1]
    
    def run(implicit ev: {} <:< Ctx): Typ = {
      val Inter = new ir2.BaseInterpreter
      reinterpret(self.rep, Inter)().asInstanceOf[Typ]
    }
    
    def showScala: String = ibase.showScala(self rep)
    
  }
  
  import quasi2.MetaBases.Runtime.ScalaReflectionBaseWithOwnNames
  
  def scalaTreeIn(MBM: MetaBases)(SRB: MBM.ScalaReflectionBase, rep: Rep, extrudedHandle: (BoundVal => MBM.u.Tree) = DefaultExtrudedHandler): MBM.u.Tree = {
    reinterpret(rep, SRB)(extrudedHandle)
  }
  /** For obscure but almost certainly unjustified reasons, Scala complains when calling the above in some contexts. This is a hack to get around it. */
  final def scalaTreeInWTFScala[MBM <: MetaBases](MBM: MBM)(SRB: MBM#ScalaReflectionBase, rep: Rep, extrudedHandle: (BoundVal => MBM#U#Tree) = DefaultExtrudedHandler): MBM#U#Tree =
    scalaTreeIn(MBM)(SRB.asInstanceOf[MBM.ScalaReflectionBase], rep, extrudedHandle.asInstanceOf[BoundVal => MBM.u.Tree])
  
  final def scalaTree(rep: Rep, extrudedHandle: (BoundVal => sru.Tree) = DefaultExtrudedHandler): sru.Tree =
    scalaTreeIn(quasi2.MetaBases.Runtime)(new ScalaReflectionBaseWithOwnNames, rep, extrudedHandle)
  
  
}


