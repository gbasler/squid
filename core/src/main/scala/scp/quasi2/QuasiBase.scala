package scp
package quasi2

import lang2._

import scp.utils.MacroUtils.MacroSetting


/** Maybe QQ/QC should be accessible to all bases (with necessary feature restrictions)?
  *   Would make Liftable instances nicer since we'd be able to assume QQ/QC can be used on all bases */
trait QuasiBase extends Base {
  
  
  /** Should substitute holes for the given definitions */
  def substitute(r: Rep, defs: Map[String, Rep]): Rep
  
  def hole(name: String, typ: TypeRep): Rep
  def splicedHole(name: String, typ: TypeRep): Rep
  
  def typeHole[A](name: String): TypeRep = ??? // TODO
  
  
  
  
  
  
  /* --- --- --- Provided defs --- --- --- */
  
  
  final def substitute(r: Rep, defs: (String, Rep)*): Rep = if (defs isEmpty) r else substitute(r, defs.toMap)
  
  sealed case class IR[+T, -C] private[quasi2] (rep: Rep) extends TypeErased with ContextErased {
    type Typ <: T
    type Ctx >: C
    def typ: IRType[_ <: Typ] = ???
    override def equals(that: Any): Boolean = that match {
      case that: IR[_,_] => rep =~= that.rep
      case _ => false
    }
    override def toString: String = s"""ir"$rep""""
  }
  def `internal IR`[Typ,Ctx](rep: Rep) = IR[Typ,Ctx](rep) // mainly for macros
  type SomeIR = IR[_, _] // shortcut; avoids showing messy existentials
  
  
  sealed case class IRType[T] private[quasi2] (trep: TypeRep) extends TypeErased { type Typ = T }
  def `internal IRType`[Typ](trep: TypeRep) = IRType[Typ](trep) // mainly for macros
  
  trait TypeErased { type Typ }
  trait ContextErased { type Ctx }
  
  /** Allows things like TypeOf[ir.type] and TypeOf[irTyp.type], useful when extracted values have non-denotable types  */
  type TypeOf[QT <: TypeErased] = QT#Typ
  
  /** Allows things like ContextOf[ir.type], useful when extracted values have non-denotable contexts  */
  type ContextOf[QT <: ContextErased] = QT#Ctx
  
  
  
  /** Artifact of a term extraction: map from hole name to terms, types and spliced term lists */
  type Extract = (Map[String, Rep], Map[String, TypeRep], Map[String, Seq[Rep]])
  val EmptyExtract: Extract = (Map(), Map(), Map())
  
  protected def mergeOpt(a: Option[Extract], b: => Option[Extract]): Option[Extract] = for { a <- a; b <- b; m <- merge(a,b) } yield m
  protected def merge(a: Extract, b: Extract): Option[Extract] = {
    b._1 foreach { case (name, vb) => (a._1 get name) foreach { va => if (!(va =~= vb)) return None } }
    b._3 foreach { case (name, vb) => (a._3 get name) foreach { va =>
      if (va.size != vb.size || !(va zip vb forall {case (vva,vvb) => vva =~= vvb})) return None } }
    val typs = a._2 ++ b._2.map {
      case (name, t) =>
        // Note: We could do better than =:= and infer LUB/GLB, but that would probably mean embedding the variance with the type...
        if (a._2 get name forall (_ =:= t)) name -> t 
        else return None
    }
    val splicedVals = a._3 ++ b._3
    val vals = a._1 ++ b._1
    Some(vals, typs, splicedVals)
  }
  protected def mergeAll(as: Option[Extract]*): Option[Extract] = mergeAll(as)
  protected def mergeAll(as: TraversableOnce[Option[Extract]]): Option[Extract] = {
    if (as isEmpty) return Some(EmptyExtract)
    as.reduce[Option[Extract]] { case (acc, a) => for (acc <- acc; a <- a; m <- merge(acc, a)) yield m }
  }
  
  
  
  def wrapConstruct(r: => Rep) = r
  def wrapExtract(r: => Rep) = r
  
  type __* = Seq[IR[_,_]] // used in insertion holes, as in:  ${xs: __*}
  
  
  
  /** TODO make it more generic: use Liftable! */
  def $[T,C](q: IR[T,C]*): T = ??? // TODO B/E  -- also, rename to 'unquote'?
  def $$[T](name: Symbol): T = ???
  
  
  
  import scala.language.experimental.macros
  
  class Quasiquotes[QC <: QuasiConfig/*[_]*/] {
    val base: QuasiBase.this.type = QuasiBase.this
    
    implicit class QuasiContext(private val ctx: StringContext) {
      
      object ir { // TODO try to refine the types..?
        //def apply(t: Any*): Any = macro QuasiMacros.applyImpl[QC]
        //def unapply(t: Any): Any = macro QuasiMacros.unapplyImpl[QC]
        def apply(inserted: Any*): SomeIR = macro QuasiMacros.applyImpl[QC]
        def unapply(scrutinee: SomeIR): Any = macro QuasiMacros.unapplyImpl[QC]
      }
      
      object dbg_ir { // TODO try to refine the types..?
        @MacroSetting(debug = true) def apply(inserted: Any*): Any = macro QuasiMacros.applyImpl[QC]
        @MacroSetting(debug = true) def unapply(scrutinee: Any): Any = macro QuasiMacros.unapplyImpl[QC]
      }
      
    }
    
  }
  class Quasicodes[QC <: QuasiConfig] {
    val base: QuasiBase.this.type = QuasiBase.this
    
    def ir[T](tree: T): IR[T, _] = macro QuasiMacros.quasicodeImpl[QC]
    @MacroSetting(debug = true) def dbg_ir[T](tree: T): IR[T, _] = macro QuasiMacros.quasicodeImpl[QC]
    
    def $[T,C](q: IR[T,C]*): T = macro QuasiMacros.forward$ // to conserve the same method receiver as for QQ (the real `Base`)
    def $$[T](name: Symbol): T = macro QuasiMacros.forward$$
    
    type $[QT <: TypeErased] = TypeOf[QT]
    
  }
  object Quasicodes extends Quasicodes[DefaultQuasiConfig]
  object Quasiquotes extends Quasiquotes[DefaultQuasiConfig]
  
}






