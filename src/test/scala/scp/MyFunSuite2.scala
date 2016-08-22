package scp

import org.scalatest.FunSuite
import scp.lang.Base
import scp.ir2.AST

/** The reason we currently have {{{DSL <: AST}}} is because otherwise the 'eqt' functions have the same erasure... */
class MyFunSuite2[DSL <: AST](val DSL: DSL = TestDSL2) extends FunSuite { funs =>
  import DSL._
  
  def hopefully(condition: Boolean) = assert(condition)
  def hopefullyNot(condition: Boolean) = assert(!condition)
  
  def same[T](a: T, b: T) = assert(a == b)
  def eqtWith[T](a: T, b: T)(r: (T,T) => Boolean) =
    //assert(r(a, b), s"=> $a and $b are not equivalent")
    if (!r(a, b)) fail(s"$a and $b are not equivalent")
  
  
  def subt(a: IRType[_], b: IRType[_]) = eqtWith(a,b)(_ <:< _)
  def subt(a: TypeRep, b: TypeRep) = eqtWith(a,b)(_ <:< _)
  
  def eqt(a: IRType[_], b: IRType[_]) = eqtWith(a,b)(_ =:= _)
  def eqt(a: TypeRep, b: TypeRep) = eqtWith(a,b)(_ =:= _)
  def eqt(a: Rep, b: Rep) = eqtWith(a,b)(_ =~= _)
  def eqt(a: IR[_,_], b: IR[_,_]) = eqtWith(a,b)(_ =~= _)
  
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
  }
  
  def implicitTypeOf[A: IRType](x: IR[A,_]) = irTypeOf[A].rep
  
  
  type Q[+T,-C] = IR[T,C]
  type TypeEv[T] = IRType[T]
  type QuotedType[T] = IRType[T]
  
  def typeEv[T: TypeEv] = irTypeOf[T]
  
}

