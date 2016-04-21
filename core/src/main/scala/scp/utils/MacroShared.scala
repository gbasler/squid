package scp.utils

import scala.reflect.macros.whitebox.Context

trait MacroShared {
  type Ctx <: Context
  val Ctx: Ctx
  import Ctx.universe._
  
  
  
  def typeToTree(tpe: Type): Tree = {
    val r = tpe match {
      case TypeRef(pre, sym, Nil) =>
        TypeTree(tpe)
      case TypeRef(pre, sym, args) =>
        //AppliedTypeTree(Ident(sym.name),
        //  args map { x => typeToTree(x) })
        TypeTree(tpe)
      case AnnotatedType(annotations, underlying) =>
        typeToTree(underlying)
      case _ => TypeTree(tpe)
    }
    //println(s"typeToTree($tpe) = ${showCode(r)}")
    r
  }
  
  
  object ScalaRepeated {
    
    private val q"def f(x:$stupidFuck[Int])" = q"def f(x:Int*)"
    
    def apply(tp: Tree): Tree = tq"$stupidFuck[$tp]"
    
    def unapply(tp: Tree): Option[Tree] = q"def f(x:$tp)" match {
      case q"def f(x:$t*)" => Some(t)
      case _ => None
    }
    
  }
  
  
  implicit class TreeOps(private val self: Tree) {
    def transform(pf: PartialFunction[Tree, Tree]) =  {
      new Transformer {
        override def transform(x: Tree) =
          if (pf isDefinedAt x) pf(x)
          else super.transform(x)
      } transform self
    }
  }
  
  
}
