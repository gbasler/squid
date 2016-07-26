package scp
package gen

import lang._
import quasi._
import ir._

object TestDSLExp extends AST with MyDSL with ScalaTyping {
  case class Exp[+A](rep: Rep)
  //implicit def lam[T: TypeEv, S: TypeEv](f: Exp[T] => Exp[S]): Exp[T => S] =
  implicit def lam[T: _root_.scp.gen.TestDSLExp.TypeEv, S: _root_.scp.gen.TestDSLExp.TypeEv](f: Exp[T] => Exp[S]): Exp[T => S] = {
    // used to be:  Exp[T => S](abs[T, S]("lambdaInput", y => f(Exp(y)).rep))
    val v = freshBoundVal(typeRepOf[T])
    Exp[T => S](lambda(Seq(v), f(Exp(v)).rep))
  }
}


