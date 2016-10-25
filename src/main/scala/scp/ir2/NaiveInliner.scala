package scp
package ir2

import utils._

/**
  * TODO inline even with holes in ction mode?
  * (in ction -- and also xtion?, holes can be seen as effects...)
  * 
  * */
trait NaiveInliner extends AST {
  val inlineVariables = false
  
  override def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep = {
    self -> mtd match {
      case RepDef(Abs(p,b)) -> Apply.Symbol if (isConstructing || !hasHoles(b)) && (inlineVariables || p.typ.tpe.typeSymbol != Var.ClassSymbol) =>
        val Args(arg)::Nil = argss
        inline(p, b, arg)
      case _ => super.methodApp(self, mtd, targs, argss, tp)
    }
  }
  /*
  //def inline(param: BoundVal, body: Rep, arg: Rep) = transformRep(body) { // does not work with ANF.. why?
  def inline(param: BoundVal, body: Rep, arg: Rep) = bottomUp(body) {
    case RepDef(`param`) => arg
    case r => r
  }
  */
}
