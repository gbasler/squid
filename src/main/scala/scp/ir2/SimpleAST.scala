package scp
package ir2

import utils._

class SimpleAST extends AST { ast =>
  
  case class Rep(dfn: Def) {
    override def toString = s"$dfn"
  }
  def rep(dfn: Def) = new Rep(dfn)
  def dfn(r: Rep): Def = r.dfn
  
  def repType(r: Rep): TypeRep = r.dfn.typ
  
  object Const extends ConstAPI {
    import meta.RuntimeUniverseHelpers.sru
    def unapply[T: sru.TypeTag](ir: IR[T,_]): Option[T] = ir.rep.dfn match {
      case cst @ Constant(v) if cst.typ <:< sru.typeTag[T].tpe => Some(v.asInstanceOf[T])
      case _ => None
    }
  }
  
}




















