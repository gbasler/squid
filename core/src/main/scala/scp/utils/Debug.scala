package scp.utils

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

object Debug {
  
  def show[T](x: T): T = macro showImpl[T]
  
  def showImpl[T: c.WeakTypeTag](c: Context)(x: c.Tree) = {
    import c.universe._
    println("Tree: "+x+" : "+x.tpe)
    q"""val r = $x; println(${x.toString} + " = " + r  + " : " + ${x.tpe.toString}); r"""
  }
  
  
  
  //def showTyped[T](x: T): T = macro showTypedImpl[T]
  //
  //def showTypedImpl[T](c: Context)(x: c.Tree) = {
  //  import c.universe._
  //  val str = x match {
  //    case Literal(Constant(str: String)) => str
  //  }
  //  val tped = c.typecheck(c.parse(str))
  //  println("Typed: "+tped)
  //  q"""val r = $tped; println(${tped.toString} + " = " + r ); r"""
  //}
  
}

