package scp
package ir2

import lang2._
import utils._



class SimpleAST extends AST {
  
  case class Rep(dfn: Def)
  def rep(dfn: Def) = new Rep(dfn)
  def dfn(r: Rep): Def = r.dfn
  
  def repType(r: Rep): TypeRep = r.dfn.typ
  
  def reinterpret(r: Rep, newBase: Base): newBase.Rep =
    Reinterpreter(newBase){(r, f) => f(r.dfn)}(r)
  
  
}




















