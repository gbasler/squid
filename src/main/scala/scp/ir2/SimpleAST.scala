package scp
package ir2

import utils._

class SimpleAST extends AST with CurryEncoding { ast =>
  
  case class Rep(dfn: Def) {
    override def toString = s"$dfn"
  }
  def rep(dfn: Def) = postProcess(new Rep(dfn))
  
  def dfn(r: Rep): Def = r.dfn
  
  def repType(r: Rep): TypeRep = r.dfn.typ
  
}




















