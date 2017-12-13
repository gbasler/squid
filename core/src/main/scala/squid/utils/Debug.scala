// Copyright 2017 EPFL DATA Lab (data.epfl.ch)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package squid.utils

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

object Debug {
  
  import Console._
  val GREY = "\u001B[37m"
  
  def show[T](x: T): T = macro showImpl[T]
  
  def showImpl[T: c.WeakTypeTag](c: Context)(x: c.Tree) = {
    import c.universe._
    //println("Tree: "+x+" : "+x.tpe)
    //c.warning(c.enclosingPosition, "Tree: "+x+" : "+x.tpe)
    
    //val code = x.toString
    val code = showCode(x)
    
    //q"""val r = $x; println(${x.toString} + " = " + r  + " : " + ${x.tpe.toString}); r"""
    //q"""val r = $x; println(${x.toString} + ": " + ${x.tpe.toString} + " =\n\t\t" + r); r"""
    
    //q"""val r = $x; println(${x.toString} + $GREY + ": " + ${x.tpe.toString} + $RESET + " \n=\t" + r); r"""
    q"""val r = $x; println($BOLD + ${code} + $RESET + " =\t" + r + $GREY + " : " + ${x.tpe.toString} + $RESET); r"""
  }
  
  
  def showLine[T](x: T): T = macro showLineImpl[T]
  
  def showLineImpl[T: c.WeakTypeTag](c: Context)(x: c.Tree) = {
    import c.universe._
    q"""val r = $x; println($BOLD + "[" + ${c.enclosingPosition.line.toString} + "]:" + $RESET + "\t" + r + $GREY + " : " + ${x.tpe.toString} + $RESET); r"""
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

