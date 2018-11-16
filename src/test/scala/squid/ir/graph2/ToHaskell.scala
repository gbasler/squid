
package squid
package ir
package graph2

import squid.utils._

import haskell.Builder
import haskell.Prelude._

object ToHaskell {
  
  def apply(AST:SimpleAST)(rep: AST.Rep): String = {
    import AST.Predef._
    //implicitly[CodeType[Any]]
    def rec(cde: OpenCode[Any]): String = {
    //def rec[A:CodeType](cde: OpenCode[A]): String = cde match {
      val cdeStr = cde match {
        //case Const(v) => v.toString // why doesn't work??! "could not find implicit evidence for type Any"
        //case Const(v:String) => v.toString // doesn't work either...
        //case c"${Const(v)}:String" => '"'+v+'"' // doesn't work either...
        case AST.Code(AST.RepDef(AST.Constant(v))) => v match {
          case str: String => '"'+str+'"'
          case _ => v.toString
        }
        case c"($f:$ta=>$tb)($a)" => rec(f) + " " + rec(a)
        case c"downBuild($n)" => s"down' ${rec(n)}"
        case c"($b:Builder[$ta]).apply[$tb]($c)($n)" => s"${b|>rec} ${c|>rec} ${n|>rec}"
        case c"+" => s"+"
        case c"::" => s":"
      }
      s"($cdeStr)"
    }
    rec(AST.Code(rep))
  }
  
}
