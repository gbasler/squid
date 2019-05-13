package squid
package haskellopt
package ghcdump

import squid.utils._

object Printer extends Interpreter {
  
  type Expr = String
  type Lit = String
  def EVar(b: BinderId): Expr = b.str
  def EVarGlobal(ExternalName: ExternalName): Expr = s"${ExternalName.externalModuleName}.${ExternalName.externalName}"
  def ELit(Lit: Lit): Expr = Lit
  def EApp(e0: Expr, e1: Expr): Expr = e1 match { case "[ty]" => s"$e0$e1" case _ => s"($e0 $e1)" }
  def ETyLam(bndr: Binder, e0: Expr): Expr = s"(\\${bndr.str} => $e0)"
  def ELam(bndr: Binder, e0: => Expr): Expr = s"(\\${bndr.str} -> $e0)"
  def ELet(lets: Array[(Binder, () => Expr)], e0: => Expr): Expr = s"(let { ${lets.map(be => s"${be._1.str} = ${be._2()}").mkString(";")} } in $e0)"
  def EType(ty: Type): Expr = "[ty]"
  def ECase(e0: Expr, bndr: Binder, alts: Array[Alt]): Expr = s"case $e0 as $bndr of {...}"
  
  def LitInteger(n: Int): Lit = n.toString
  def MachInt(n: Int): Lit = n.toString
  def LitString(s: String): Lit = '"' + s + '"'
  
}
