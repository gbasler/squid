package scp.paper

//import scala.reflect.runtime.universe.MethodSymbol
//import scala.reflect.runtime.universe.{MethodSymbol => Mtd}

/*

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// Required Definitions:
trait Base extends BaseHelper with Typing {
  import scala.reflect.runtime.universe.{MethodSymbol => Mtd}
  type Rep[+A]; type TypeRep[+A]; type → [-A,+B]
  case class TypEv[A](rep: TypeRep[_])
  def abs[A:TypEv,B:TypEv](name: String, fun: Rep[A] => Rep[B]): Rep[A → B]
  def app[A:TypEv,B:TypEv](fun: Rep[A → B], arg: Rep[A]): Rep[B]
  def const[A:TypEv](value: A): Rep[A]
  def unapply_const[A:TypEv](x: Rep[A]): Option[A]
  def dslMethodApp[A:TypEv](self: Option[Rep[_]], mtd: Mtd,
    targs: List[TypEv[_]], argss: List[List[Rep[_]]]): Rep[A]
  def transform(r: Rep[_])(f: Rep[_] => Rep[_]): Rep[_]
  def extract(xtor: Rep[_], t: Rep[_]): Option[Extract]
  def extractType(xtor: TypeRep[_], t: TypeRep[_]): Option[Extract]
  def freeVar[A: TypEv](name: String): Rep[A]
  def hole[A: TypEv](name: String): Rep[A]
  def typeHole[A](name: String): TypeRep[A]
  protected def run(r: Rep[_]): Any
}
// Provided Definitions:
class BaseHelper { self: Base =>
  def typeRepOf[A:TypEv]: TypeRep[A] = ??? //...
  class Quoted[+Typ,-Ctx] private(rep: Rep[Typ]) {
    def subs[A,C](s: (Symbol, Q[A,C])) = ??? //macro ...
    def rename(s: (Symbol, Symbol)) = ??? //macro ...
    def run(implicit ev: {} <:< Ctx): Typ = ??? //...
  }
  type Q[+Typ,-Ctx] = Quoted[Typ,Ctx]
  type QuotedType[+A] = TypeRep[A]
  type Extract = (Map[String, Rep[_]], Map[String, TypEv[_]])
  
  // ... more helper definitions elided ...
  
  
  protected def merge(a: Extract, b: Extract): Option[Extract] = ???
  
  implicit def tev[A]: TypEv[A] = null
  
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

trait DirectStyle extends Base {
  type → [-A,+B] = A => B
}

trait Typing {
  
  //trait TypeEv[A]
  
  
  
  
  
}
*/


// OLD
/*

// Required Definitions
trait Base extends BaseHelper with Typing {
  type Rep[+A]
  type TypeRep[+A]
  type → [-A,+B]
  
  case class TypEv[A](rep: TypeRep[_])
  
  def repEq(a: Rep[_], b: Rep[_]): Boolean
  
  def abs[A:TypEv,B:TypEv](name: String, fun: Rep[A]=>Rep[B]): Rep[A → B]
  def app[A:TypEv,B:TypEv](fun: Rep[A → B],
                           arg: Rep[A]): Rep[B]
  def const[A:TypEv](value: A): Rep[A]
  def unapply_const[A:TypEv](x: Rep[A]): Option[A]
  
  def dslMethodApp[A:TypEv](self: Option[Rep[_]], mtd: MethodSymbol,
                            targs: List[TypEv[_]], argss: List[List[Rep[_]]]): Rep[A]
  
  def transform(r: Rep[_])(f: Rep[_] => Rep[_]): Rep[_]
  def extract(xtor: Rep[_], t: Rep[_]): Option[Extract]
  def extractType(xtor: TypeRep[_], t: TypeRep[_]): Option[Extract]
  protected def run(r: Rep[_]): Any
  
  def freeVar[A: TypEv](name: String): Rep[A]
  def hole[A: TypEv](name: String): Rep[A]
  def typeHole[A](name: String): TypeRep[A]
}
// Provided Definitions
class BaseHelper { self: Base =>
  import scala.reflect.runtime.{universe => ru}
  
  def typeRepOf[A:TypEv]: TypeRep[A] = ???
  
  class Quoted[+Typ,-Ctx] private(rep: Rep[Typ]) {
    def subs[A,C](s: (Symbol, Q[A,C])) = ??? //macro ...
    def rename(s: (Symbol, Symbol)) = ??? //macro ...
    def run(implicit ev: {} <:< Ctx): Typ = ??? //...
  }
  type Q[+Typ,-Ctx] = Quoted[Typ,Ctx]
  type QuotedType[+A] = TypeRep[A]
  type Extract = (Map[String, Rep[_]], Map[String, TypEv[_]])
  
  object Const {
    def apply[A: ru.TypeTag](v: A): Q[A, {}] = ??? //...
    def unapply[A: ru.TypeTag](x: Q[A,_]): Option[A] = ??? //...
  }
  //... // more (elided) helper definitions
  
  
  protected def merge(a: Extract, b: Extract): Option[Extract] = ???
  
  implicit def tev[A]: TypEv[A] = null
  
}

*/

