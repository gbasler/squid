package squid

import org.scalatest.FunSuite
import squid.ir.AST
import squid.ir.SimpleANF
import squid.ir.SimpleAST
import squid.lang.InspectableBase
import utils.meta.RuntimeUniverseHelpers.sru
import utils.GenHelper

class MyFunSuite[DSL <: AST](override val DSL: DSL = TestDSL) extends MyFunSuiteBase[DSL](DSL) { funs =>
  import DSL._
  
  def eqt(a: IRType[_], b: IRType[_]) = eqtBy(a,b)(_ =:= _)
  def eqt(a: TypeRep, b: TypeRep) = eqtBy(a,b)(_ =:= _)
  
}

/** The reason we currently have {{{DSL <: InspectableBase}}} is because otherwise the 'eqt' functions have the same erasure... */
//class MyFunSuiteBase[DSL <: InspectableBase](val DSL: DSL = TestDSL2) extends MyFunSuiteTrait[DSL.type]
//class MyFunSuiteBase[DSL <: InspectableBase](val DSL: DSL = TestDSL2) extends MyFunSuiteTrait[DSL]
class MyFunSuiteBase[DSL <: InspectableBase](val DSL: DSL = TestDSL) extends MyFunSuiteTrait
//abstract class MyFunSuiteTrait[DSL <: InspectableBase] extends FunSuite { funs =>
//trait MyFunSuiteTrait[DSL <: InspectableBase] extends FunSuite { funs =>
  //val DSL: DSL
trait MyFunSuiteTrait extends FunSuite { funs =>
  val DSL: InspectableBase
  import DSL._
  
  def hopefully(condition: Boolean) = assert(condition)
  def hopefullyNot(condition: Boolean) = assert(!condition)
  
  def sameScalaType[A: sru.TypeTag, B: sru.TypeTag] =
    if (!(sru.typeOf[A] =:= sru.typeOf[B])) fail(s"${sru.typeOf[A]} =/= ${sru.typeOf[B]}")
  def ofExactType[A: sru.TypeTag, B: sru.TypeTag](a: A) = sameScalaType[A,B]
  
  implicit class TypeHelper[A: sru.TypeTag](self: A) {
    def ofType[B: sru.TypeTag]() = { sameScalaType[A,B]; self }
  }
  
  private def showBest(x:Any) = x match {
    //case r: DSL.Rep => r |> DSL.showRep // this no longer works, because Rep is an erased type
    case (_: SimpleAST # Rep) | (_: SimpleANF # Rep) => // so for now we just special-case actual known Rep classes
      x.asInstanceOf[DSL.Rep] |> DSL.showRep
    case _ => x.toString
  }
  
  def same[T](a: T, b: T) = assert(a == b)
  def eqtBy[T](a: T, b: T, truth: Boolean = true)(r: (T,T) => Boolean) =
    //assert(r(a, b), s"=> $a and $b are not equivalent")
    if (r(a, b) != truth) {
      System.err.println(s"FAILURE: ${a|>showBest} and ${b|>showBest}")
      fail(s"$a and $b are ${if (truth) "not " else ""}equivalent")
    }
  
  
  def subt(a: IRType[_], b: IRType[_]) = eqtBy(a,b)(_ <:< _)
  def subt(a: TypeRep, b: TypeRep) = eqtBy(a,b)(_ <:< _)
  
  def eqt(a: Rep, b: Rep) = eqtBy(a,b)(_ =~= _)
  def eqt(a: Code[_], b: Code[_], truth: Boolean = true) = eqtBy(a,b,truth)(_ =~= _)
  
  def matches(a: Code[_])(pfs: PartialFunction[Code[_],Unit]*) = {
    for (pf <- pfs) pf(a)
    MatchesAnd(a)
  }
  case class MatchesAnd[T](a: Code[T]) {
    def and (pf: PartialFunction[Code[_],Unit]) = {
      pf(a)
      this
    }
  }
  
  implicit class Matches(self: Code[_]) {
    def matches(pfs: PartialFunction[Code[_],Unit]*) = funs.matches(self)(pfs: _*)
    def eqt (that: Code[_]) = funs.eqt(self.rep, that.rep)
    def dbg_eqt (that: Code[_]) = {
      DSL debugFor (self.rep extractRep that.rep)
      DSL debugFor (that.rep extractRep self.rep)
      funs.eqt(self.rep, that.rep)
    }
    def neqt (that: IR[_,_]) = funs.eqt(self, that, false)
  }
  
  def implicitTypeOf[A: IRType](x: IR[A,_]) = irTypeOf[A].rep
  
  
  type Q[+T,-C] = IR[T,C]
  type TypeEv[T] = IRType[T]
  type QuotedType[T] = IRType[T]
  
  def typeEv[T: TypeEv] = irTypeOf[T]
  
}

