package scp.paper

import scala.reflect.runtime.universe.MethodSymbol


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// Required Definitions
trait Base extends BaseHelper with Typing {
  type Rep[+A]
  type TypeRep[+A]
  type → [-A,+B]
  
  def repEq(a: Rep[_], b: Rep[_]): Boolean
  
  def abs[A:TypeRep,B:TypeRep](fun: Rep[A]=>Rep[B]): Rep[A → B]
  def app[A:TypeRep,B:TypeRep](fun: Rep[A → B],
                               arg: Rep[A]): Rep[B]
  def const[A:TypeRep](value: A): Rep[A]
  def unapply_const[A:TypeRep](x: Rep[A]): Option[A]
  
  def dslMethodApp[A:TypeRep](self: Option[Rep[_]], mtd: MethodSymbol,
    targs: List[TypeRep[_]], argss: List[List[Rep[_]]]): Rep[A]
  
  def transform(r: Rep[_])(f: Rep[_] => Rep[_]): Rep[_]
  def extract(xtor: Rep[_], t: Rep[_]): Option[Extract]
  protected def run(r: Rep[_]): Any
  
  def hole[A: TypeEv](name: String): Rep[_]
  def typeHole[A](name: String): TypeRep[_]
}
// Provided Definitions
class BaseHelper { self: Base =>
  import scala.reflect.runtime.{universe => ru}
  
  class Quoted[+Typ,-Ctx] private(rep: Rep[Typ]) {
    def subs[A,C](s: (Symbol, Q[A,C])) = ??? //macro ...
    def rename(s: (Symbol, Symbol)) = ??? //macro ...
    def run(implicit ev: {} <:< Ctx): Typ = ??? //...
  }
  type Q[+Typ,-Ctx] = Quoted[Typ,Ctx]
  type QuotedType[+A] = TypeRep[A]
  type Extract = (Map[String, Rep[_]], Map[String, TypeRep[_]])
  
  object Const {
    def apply[A: ru.TypeTag](v: A): Q[A, {}] = ??? //...
    def unapply[A: ru.TypeTag](x: Q[A,_]): Option[A] = ??? //...
  }
  //... // more (elided) helper definitions
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

trait Typing {
  
  type TypeEv[A]
  
  
  
}
