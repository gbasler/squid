package scp
package ir2

import lang2._
import utils._



class SimpleAST extends AST {
  
  case class Rep(dfn: Def) {
    override def toString = s"$dfn"
  }
  def rep(dfn: Def) = new Rep(dfn)
  def dfn(r: Rep): Def = r.dfn
  
  def repType(r: Rep): TypeRep = r.dfn.typ
  
  def reinterpret(r: Rep, newBase: Base): newBase.Rep =
    Reinterpreter(newBase){(r, f) => f(r.dfn)}(r)
  
  
  object Const extends ConstAPI {
    import meta.RuntimeUniverseHelpers.sru
    def unapply[T: sru.TypeTag](ir: IR[T,_]): Option[T] = ir.rep.dfn match {
      case cst @ Constant(v) if cst.typ <:< sru.typeTag[T].tpe => Some(v.asInstanceOf[T])
      case _ => None
    }
  }
  
}




















