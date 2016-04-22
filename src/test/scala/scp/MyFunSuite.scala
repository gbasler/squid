package scp

import org.scalatest.FunSuite

class MyFunSuite extends FunSuite { funs =>
  import TestDSL._
  
  def hopefully(condition: Boolean) = assert(condition)
  def hopefullyNot(condition: Boolean) = assert(!condition)
  
  def same[T](a: T, b: T) = assert(a == b)
  def eqtWith[T](a: T, b: T)(r: (T,T) => Boolean) =
    //assert(r(a, b), s"=> $a and $b are not equivalent")
    if (!r(a, b)) fail(s"$a and $b are not equivalent")
  
  def eqt(a: TypeRep, b: TypeRep) = eqtWith(a,b)(_ =:= _)
  def eqt(a: Rep, b: Rep) = eqtWith(a,b)(_ =~= _)
  def eqt(a: Q[_,_], b: Q[_,_]) = eqtWith(a,b)(_ =~= _)
  
  //def matches[A](a: A)(pfs: PartialFunction[A,Unit]*) = {
  def matches(a: Q[_,_])(pfs: PartialFunction[Q[_,_],Unit]*) = {
    for (pf <- pfs) pf(a)
    MatchesAnd(a)
  }
  case class MatchesAnd[T,C](a: Q[T,C]) {
    def and (pf: PartialFunction[Q[_,_],Unit]) = {
      pf(a)
      this
    }
  }
  
  implicit class Matches(self: Q[_,_]) {
    def matches(pfs: PartialFunction[Q[_,_],Unit]*) = funs.matches(self)(pfs: _*)
    def eqt (that: Q[_,_]) = funs.eqt(self, that)
  }
  
}

