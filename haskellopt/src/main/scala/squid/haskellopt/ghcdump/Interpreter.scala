package squid
package haskellopt
package ghcdump

import squid.utils._
import io.bullet.borer.Dom._

/** Decoding the CBOR emitted by the GhcDump plugin for GHC. */
abstract class Interpreter {
  
  type Expr
  type Lit
  
  // TODO:
  type Alt = Element
  type Type = Element
  
  case class ExternalName(externalModuleName: String, externalName: String, externalUnique: Element)
  
  case class Binder(binderName: String, binderId: BinderId) {
    def str = s"[$binderName]${binderId.str}"
  }
  case class BinderId(name: String, id: Int) {
    def str =s"${name}_$id"
  }
  object BinderId {
    def unapply(elt: Element): Opt[BinderId] = elt match {
      case Arr(IntElem(0),StringElem(s),IntElem(n)) => Some(BinderId(s,n))
      case _ => None
    }
  }
  
  /* See: https://github.com/bgamari/ghc-dump/blob/85b0e783d0a952cf370096d74ee833e2926074a4/ghc-dump-core/GhcDump/Ast.hs#L172
  type Expr = Expr' Binder Binder
  data Expr' bndr var
    = EVar var
    | EVarGlobal ExternalName
    | ELit Lit
    | EApp (Expr' bndr var) (Expr' bndr var)
    | ETyLam bndr (Expr' bndr var)
    | ELam bndr (Expr' bndr var)
    | ELet [(bndr, Expr' bndr var)] (Expr' bndr var)
    | ECase (Expr' bndr var) bndr [Alt' bndr var]
    | EType (Type' bndr var)
    | ECoercion
  */
  //def EVar(_var: Binder): Expr
  def EVar(b: BinderId): Expr
  def EVarGlobal(ExternalName: ExternalName): Expr
  def ELit(Lit: Lit): Expr
  def EApp(e0: Expr, e1: Expr): Expr
  def ETyLam(bndr: Binder, e0: Expr): Expr
  def ELam(bndr: Binder, e0: => Expr): Expr
  def ELet(lets: Array[(Binder, () => Expr)], e0: => Expr): Expr
  def ECase(e0: Expr, bndr: Binder, alts: Array[Alt]): Expr
  def EType(ty: Type): Expr
  def ECoercion(): Expr = ???
  
  def LitInteger(n: Int): Lit
  def LitString(s: String): Lit
  
  def ExternalName(e: Element): ExternalName = e match {
    case Arr(IntElem(0), StringElem(externalModuleName), StringElem(externalName), externalUnique) =>
      ExternalName(externalModuleName, externalName, externalUnique)
  }
  def Lit(e: Element): Lit = e match {
    case Arr(IntElem(1), ByteArrayElem(s)) => LitString(s.map(_.toChar).mkString)
    case Arr(IntElem(10), IntElem(n)) => LitInteger(n)
    case Arr(xs@_*) =>
      println(xs.size)
      println(xs(0))
      println(xs)
      ???
  }
  def Expr(elt: Element): Expr = elt match {
    case Arr(IntElem(0), BinderId(v)) => EVar(v)
    case Arr(IntElem(1), e) => EVarGlobal(ExternalName(e))
    case Arr(IntElem(2), e) => ELit(Lit(e))
    case Arr(IntElem(3), e0, e1) => EApp(Expr(e0), Expr(e1))
    case Arr(IntElem(4), b, e) => ETyLam(Binder(b), Expr(e))
    case Arr(IntElem(5), b, e) => ELam(Binder(b), Expr(e))
    case Arr(IntElem(6), lets: ArrayElem, e) => ELet(lets.elements.map {
      case Arr(b, e) => (Binder(b), () => Expr(e))
    }.toArray, Expr(e))
    case Arr(IntElem(7), e, b, alts: ArrayElem) => ECase(Expr(e), Binder(b), alts.elements.toArray)
    case Arr(IntElem(8), ty) => EType(ty)
    case Arr(xs@_*) => 
      println(xs.size)
      println(xs)
      ???
  }
  def Binder(elt: Element): Binder = {
    elt match {
      case Arr(IntElem(0), Arr(IntElem(0), StringElem(binderName), BinderId(binderId), binderIdInfo, binderIdDetails, binderType)) => // term binders
        Binder(binderName, binderId)
      case Arr(IntElem(0), Arr(IntElem(1), StringElem(binderName), BinderId(binderId), binderKind)) => // type binders
        Binder(binderName, binderId) // TODO something else for type bindings...
      case Arr(xs@_*) =>
        println(xs.size)
        println(xs)
        ???
    }
  }
  
  case class TopBinding(bndr: Binder, /*CoreStats: Element,*/ expr: Expr) {
    def str = s"${bndr.str} = $expr"
  }
  case class Module(moduleName: String, modulePhase: String, moduleTopBindings: List[TopBinding])
  
  def topBinding(elt: Element): TopBinding = elt match {
    case Arr(IntElem(0), b, _, e) => TopBinding(Binder(b), Expr(e))
    case Arr(IntElem(1), _ @ _*) => ??? // TODO rec top-level bindings
  }
  
  def apply(elt: Element): Module = elt match {
    case Arr(IntElem(0), StringElem(moduleName), StringElem(modulePhase), moduleTopBindings: ArrayElem) =>
      Module(moduleName, modulePhase, moduleTopBindings.elements.iterator.map(topBinding).toList)
  }
  
}

private object Arr {
  def unapplySeq(e: Element) = e match {
    case a: ArrayElem => Some(a.elements)
    case _ => None
  }
}
