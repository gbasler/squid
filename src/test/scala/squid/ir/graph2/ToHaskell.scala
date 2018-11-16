
package squid
package ir
package graph2

import squid.utils._
import haskell.Builder
import haskell.Prelude._

import scala.collection.mutable

object ToHaskell {
  
  def apply(AST:SimpleAST)(rep: AST.Rep): String = {
    import AST.Predef._
    var curId = 0
    val valIds = mutable.Map.empty[AST.Val,Int]
    def name[A](v:Variable[A]): String = nameV(v.`internal bound`)
    def nameV(v:AST.Val): String = {
      val id = valIds.getOrElseUpdate(v, curId alsoDo (curId += 1))
      v.name+"_"+id
    }
    //implicitly[CodeType[Any]]
    def rec(cde: OpenCode[Any], parens: Bool = true): String = {
    //def rec[A:CodeType](cde: OpenCode[A]): String = cde match {
      val cdeStr = cde match {
        //case Const(v) => v.toString // why doesn't work??! "could not find implicit evidence for type Any"
        //case Const(v:String) => v.toString // doesn't work either...
        //case c"${Const(v)}:String" => '"'+v+'"' // doesn't work either...
        case AST.Code(AST.RepDef(AST.Constant(v))) => v match {
          case str: String => '"'+str+'"'
          case _ => v.toString
        }
        case c"downAux($n)" => s"down' ${rec(n)}"
        case c"downBuild($n)" => s"down' ${rec(n)}"
        case c"($b:Builder[$ta]).apply[$tb]($c)($n)" => s"${b|>(rec(_))} ${c|>(rec(_))} ${n|>(rec(_))}"
        case c"+" => s"+"
        case c"::" => s":"
        case c"val $x:$xt = $v; $body:$bt" =>
          s"let ${name(x)} = ${rec(v,false)} in ${rec(body,false)}"
        //case c"($f:$ta=>$tb)($a)" => rec(f) + " " + rec(a)
        case c"($f:$ta=>$tb)($a:$tc where (tc<:<ta))" => rec(f) + " " + rec(a) // seems necessary because we mess with types!
        case c"($x:$xt)=>($b:$bt)" => s"\\${name(x)} -> " + rec(b)
        case c"compose[$ta,$tb,$tc]($f)($g)" => s"${rec(f)} . ${rec(g)}"
        case AST.Code(AST.RepDef(v:AST.Val)) => nameV(v)
        case _ =>
          lastWords(s"Unsupported[${cde.rep.dfn.getClass}]: ${cde.show}")
      }
      if (parens) s"($cdeStr)" else cdeStr
    }
    rec(AST.Code(rep),false)
  }
  
}
