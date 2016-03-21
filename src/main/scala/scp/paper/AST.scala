package scp.paper

import scala.reflect.runtime.universe.MethodSymbol

/*

/**
  * Created by lptk on 21/03/16.
  */
class AST extends Base with DirectStyle { self: Typing =>
  
  trait Rep[+A]
  
  //case class const[A:TypeRep](value: A) extends Rep[A]
  //case class const[A: AST.this.TypeRep](value: A) extends Rep[A]
  
  def abs[A:TypeRep,B:TypeRep](fun: Rep[A]=>Rep[B]): Rep[A → B]
  def app[A:TypeRep,B:TypeRep](fun: Rep[A → B],
                               arg: Rep[A]): Rep[B]
  def const[A:TypeRep](value: A): Rep[A]
  def unapply_const[A:TypeRep](x: Rep[A]): Option[A]
  
  def dslMethodApp[A:TypeRep](self: Option[Rep[_]], mtd: MethodSymbol,
    targs: List[TypeRep[_]], argss: List[List[Rep[_]]]): Rep[A]
  
  
  case class Hole[+A: TypeEv](name: String) extends Rep[A] {
    val typ = typeEv[A].rep
  }
  
  
  
}
*/

