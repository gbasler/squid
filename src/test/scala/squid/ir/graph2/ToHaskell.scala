
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
      v.name.replaceAll("\\$","'") +"_"+id
    }
    val Prelude = code"haskell.Prelude"
    //implicitly[CodeType[Any]]
    def rec(cde: OpenCode[Any], parens: Bool = true): String = {
    //def rec[A:CodeType](cde: OpenCode[A]): String = cde match {
      var really_parens_? : Opt[Bool] = None
      def par = really_parens_? = Some(true)
      def nopar = really_parens_? = Some(false)
      val cdeStr = cde match {
        //case Const(v) => v.toString // why doesn't work??! "could not find implicit evidence for type Any"
        //case Const(v:String) => v.toString // doesn't work either...
        //case c"${Const(v)}:String" => '"'+v+'"' // doesn't work either...
        case AST.Code(AST.RepDef(AST.Constant(v))) => nopar; v match {
          case str: String => '"'+str+'"'
          case _ => v.toString
        }
        case c"Nil" => s"[]"
        case c"downAux($n)" => s"down' ${rec(n)}"
        case c"downBuild($n)" => s"down' ${rec(n)}"
        case c"($b:Builder[$ta]).apply[$tb]($c)($n)" => s"${b|>(rec(_))} ${c|>(rec(_))} ${n|>(rec(_))}"
        case c"($a:$ta,$b:$tb)" => par; s"${rec(a)},${rec(b)}"
        case c"($n:Int)*($m:Int)" => s"${rec(n)} * ${rec(m)}"
        case c"($n:Int)+($m:Int)" => s"${rec(n)} + ${rec(m)}"
        case c"(+)($n:Int)($m:Int)" => s"${rec(n)} + ${rec(m)}"
        case c"(+)($n:Int)" => par; s"${rec(n)}+"
        case c"+" => par; s"+"
        case c"::" => par; s":"
          
        case c"val $x:$xt = $v; $body:$bt" =>
          s"let ${name(x)} = ${rec(v,false)} in\n${rec(body,false)}"
        //case c"($f:$ta=>$tb)($a)" => rec(f) + " " + rec(a)
          
        case c"($f:$ta=>$tb)($a:$tc where (tc<:<ta))" => // seems necessary because we mess with types!
          //rec(f) + " " + rec(a)
          //rec(f,false) + " $ " + rec(a)
          rec(f) + " " + rec(a)
        case c"($x:$xt)=>($b:$bt)" => par; s"\\${name(x)} -> " + rec(b,false)
          
        case c"($f:($ta,$tb)=>$bt)($a:$ta0 where (ta0<:<ta),$b:$tb0 where (tb0<:<tb))" =>
          //rec(f,false) + " $ " + rec(a) + " $ " + rec(b)
          rec(f) + " " + rec(a) + " " + rec(b)
        case c"($x:$xt,$y:$yt)=>($b:$bt)" => par; s"\\${name(x)} ${name(y)} -> " + rec(b,false)
          
        case c"($f:($ta,$tb,$tc)=>$bt)($a:$ta0 where (ta0<:<ta),$b:$tb0 where (tb0<:<tb),$c:$tc0 where (tc0<:<tc))" =>
          //rec(f,false) + " $ " + rec(a) + " $ " + rec(b) + " $ " + rec(c)
          rec(f) + " " + rec(a) + " " + rec(b) + " " + rec(c)
        case c"($x:$xt,$y:$yt,$z:$zt)=>($b:$bt)" => par; s"\\${name(x)} ${name(y)} ${name(z)} -> " + rec(b,false)
          
        case c"compose[$ta,$tb,$tc]($f)($g)" => s"${rec(f)} . ${rec(g)}"
        case AST.Code(AST.RepDef(v:AST.Val)) => nopar; nameV(v)
        case AST.Code(AST.RepDef(AST.MethodApp(s,sym,targs,argss,rett))) if s === Prelude.rep =>
          val args = argss.flatMap(_.reps)
          val nameStr = sym.name.toString
          val name = if (nameStr.head.isLetter) nameStr else s"($nameStr)"
          if (args.isEmpty) { nopar; name }
          else s"${name}${args.map(r => " $ "+rec(AST.Code(r),false)).mkString}"
        case AST.Code(AST.RepDef(AST.MethodApp(s,sym,targs,argss,rett))) =>
          lastWords(s"Unsupported method ($s:${s.dfn.typ}).$sym[${sym.owner}] $argss")
        case _ =>
          lastWords(s"Unsupported[${cde.rep.dfn.getClass}]: ${cde.show}")
          //lastWords(s"Unsupported[${cde.rep.dfn.getClass}]: ${cde.rep.dfn}")
      }
      if (parens && really_parens_?.forall(id) || really_parens_?.exists(id)) s"($cdeStr)" else cdeStr
      //s"($cdeStr)"
    }
    rec(AST.Code(rep),false)
  }
  
}
