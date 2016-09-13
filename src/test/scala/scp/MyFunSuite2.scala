package scp

import org.scalatest.FunSuite
import scp.lang.Base
import scp.ir2.AST
import utils.meta.RuntimeUniverseHelpers.sru

/** The reason we currently have {{{DSL <: AST}}} is because otherwise the 'eqt' functions have the same erasure... */
class MyFunSuite2[DSL <: AST](val DSL: DSL = TestDSL2) extends FunSuite { funs =>
  import DSL._
  
  def hopefully(condition: Boolean) = assert(condition)
  def hopefullyNot(condition: Boolean) = assert(!condition)
  
  def sameScalaType[A: sru.TypeTag, B: sru.TypeTag] =
    if (!(sru.typeOf[A] =:= sru.typeOf[B])) fail(s"${sru.typeOf[A]} =/= ${sru.typeOf[B]}")
  def ofExactType[A: sru.TypeTag, B: sru.TypeTag](a: A) = sameScalaType[A,B]
  
  implicit class TypeHelper[A: sru.TypeTag](self: A) {
    def apply [B: sru.TypeTag] = { sameScalaType[A,B]; self }
  }
  
  def same[T](a: T, b: T) = assert(a == b)
  def eqtBy[T](a: T, b: T, truth: Boolean = true)(r: (T,T) => Boolean) =
    //assert(r(a, b), s"=> $a and $b are not equivalent")
    if (r(a, b) != truth) fail(s"$a and $b are ${if (truth) "not " else ""}equivalent")
  
  
  def subt(a: IRType[_], b: IRType[_]) = eqtBy(a,b)(_ <:< _)
  def subt(a: TypeRep, b: TypeRep) = eqtBy(a,b)(_ <:< _)
  
  def eqt(a: IRType[_], b: IRType[_]) = eqtBy(a,b)(_ =:= _)
  def eqt(a: TypeRep, b: TypeRep) = eqtBy(a,b)(_ =:= _)
  def eqt(a: Rep, b: Rep) = eqtBy(a,b)(_ =~= _)
  def eqt(a: IR[_,_], b: IR[_,_], truth: Boolean = true) = eqtBy(a,b,truth)(_ =~= _)
  
  //def matches[A](a: A)(pfs: PartialFunction[A,Unit]*) = {
  def matches(a: IR[_,_])(pfs: PartialFunction[IR[_,_],Unit]*) = {
    for (pf <- pfs) pf(a)
    MatchesAnd(a)
  }
  case class MatchesAnd[T,C](a: IR[T,C]) {
    def and (pf: PartialFunction[IR[_,_],Unit]) = {
      pf(a)
      this
    }
  }
  
  implicit class Matches(self: IR[_,_]) {
    def matches(pfs: PartialFunction[IR[_,_],Unit]*) = funs.matches(self)(pfs: _*)
    def eqt (that: IR[_,_]) = funs.eqt(self, that)
    def dbg_eqt (that: Q[_,_]) = {
      DSL debugFor (self.rep extractRep that.rep)
      DSL debugFor (that.rep extractRep self.rep)
      funs.eqt(self, that)
    }
    def neqt (that: IR[_,_]) = funs.eqt(self, that, false)
  }
  
  def implicitTypeOf[A: IRType](x: IR[A,_]) = irTypeOf[A].rep
  
  
  type Q[+T,-C] = IR[T,C]
  type TypeEv[T] = IRType[T]
  type QuotedType[T] = IRType[T]
  
  def typeEv[T: TypeEv] = irTypeOf[T]
  
}

