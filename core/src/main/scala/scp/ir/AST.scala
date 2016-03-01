package scp
package ir

import lang._

/** Main language trait, encoding second order lambda calculus with records, let-bindings and ADTs */
trait AST extends Base {
  
  sealed trait Rep
  //sealed trait TypeRep
  
  
  def const[A: TypeEv](value: A): Rep = Const(value)
  def abs[A: TypeEv, B: TypeEv](fun: Rep => Rep): Rep = {
    val p = Symbol.fresh[A]
    Abs(p, fun(p))
  }
  def app[A: TypeEv, B: TypeEv](fun: Rep, arg: Rep): Rep = App(fun, arg)
  
  //def dslMethodApp[A,S](self: Option[SomeRep], mtd: DSLDef, targs: List[SomeTypeRep], args: List[List[SomeRep]], tp: TypeRep[A], run: Any): Rep[A,S]
  def dslMethodApp(self: Option[Rep], mtd: DSLDef, targs: List[TypeRep], argss: List[List[Rep]], tp: TypeRep): Rep =
    DSLMethodApp(self, mtd, targs, argss, tp)
  
  
  class Symbol(name: String)(val tp: TypeRep) extends Rep {
    override def toString = s"$name: $tp"
  }
  object Symbol {
    private[AST] var count = -1
    private[AST] def fresh[A: TypeEv] = {
      count += 1
      new Symbol("x$"+count)(typeRepOf[A])
    }
  }
  
  case class Const[A](value: A) extends Rep {
    override def toString = s"$value"
  }
  case class Abs(param: Symbol, body: Rep) extends Rep {
    def inline(arg: Rep): Rep = ??? //body withSymbol (param -> arg)
    override def toString = body match {
      case that: Abs => s"{ $param, ${that.print(false)} }"
      case _ => print(true)
    }
    def print(paren: Boolean) =
    if (paren) s"{ $param => $body }" else s"$param => $body"
  }
  case class App[A,B](fun: Rep, arg: Rep) extends Rep {
    override def toString = s"$fun $arg"
  }
  
  case class DSLMethodApp(self: Option[Rep], mtd: DSLDef, targs: List[TypeRep], argss: List[List[Rep]], tp: TypeRep) extends Rep {
    override def toString = self.fold(mtd.path.last+".")(_.toString+".") + mtd.shortName +
      (targs.mkString("[",",","]")*(targs.size min 1)) +
      (argss map (_ mkString("(",",",")")) mkString)
  }
  
  
  
  def repEq(a: Rep, b: Rep): Boolean = ???
  //def typEq(a: TypeRep, b: TypeRep): Boolean = ???
  
  //implicit def funType[A: TypeEv, B: TypeEv]: TypeEv[A => B] = ???
  
  
}















