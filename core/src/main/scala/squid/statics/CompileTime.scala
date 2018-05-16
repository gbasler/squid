// Copyright 2018 EPFL DATA Lab (data.epfl.ch)
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

package squid.statics

import squid.utils.MacroUtils.{MacroDebugger,MacroDebug}
import squid.utils._

import scala.annotation.StaticAnnotation
import scala.collection.mutable


/** Standard class for Squid functionalities that require some values to be available at compile time; this type can be
  * to provide compile-time values implicitly, and lifts normal value to compile-time ones automatically. */
final class CompileTime[A](val get: A)

object CompileTime {
  
  import scala.language.experimental.macros
  
  /** `CompileTime(x)` is really just syntax sugar for `compileTime{new CompileTime(x)}` */
  implicit def apply[A](a: A): CompileTime[A] = macro StaticMacros.CompileTimeImpl[A]
  
  // Q: is the implicit lifting actually useful? I doubt it. 
}


/** Used to annotate types with a static tree representing the expression, so it can be evaluated at compile time (this
  * is related to what the Quill library does for its queries), along with a cache id to retrieve values previously-
  * computed in the same compilation run. */
class withStaticTree(t: Any, cacheUUID: String) extends StaticAnnotation


import scala.reflect.macros.whitebox

object StaticMacros {
  val staticCache = mutable.Map[String,Any]()
}
class StaticMacros(val c: whitebox.Context) {
  import c.universe._
  
  object Helpers extends {val uni: c.universe.type = c.universe} with meta.UniverseHelpers[c.universe.type]
  import Helpers._
  val debug = { val mc = MacroDebugger(c); mc[MacroDebug] }
  
  val staticCache = StaticMacros.staticCache
  
  object Annot { def unapply(an: Annotation): Option[Tree] = an.tree optionIf (_.nonEmpty) }
  
  def CompileTimeImpl[A: WeakTypeTag](a: Tree): Tree =
    q"_root_.squid.statics.compileTime(new _root_.squid.statics.CompileTime($a))"
  
  // TODO: handle the many possible sources of errors/exceptions more gracefully
  def compileTimeImpl[A: WeakTypeTag](a: Tree): Tree = {
    debug(s"Executing static{ ${showCode(a)} }")
    
    /*_*/
    
    var wrapInContext: Tree => Tree = identity
    var args: List[Any] = Nil
    // ^ each identifier found which value is already in the cache result in wrapping the final tree in one more lambda
    
    val tree = a transform {
      
      case id @ Ident(idname: TermName) if {
        assert(id.symbol =/= null, s"Identifier with 'null' symbol found: $id")
        !id.symbol.isStatic
      } =>
        
        val idtp = id.tpe.widen
        
        // TODO better way to make sure type of `id` is 'thoroughly' static...
        idtp.foreach { case subtp: TypeRef =>
          if (!subtp.typeSymbol.isStatic) c.abort(id.pos, s"Non-static subtype '${subtp}' of type: $idtp")
                       case _ => }
        
        idtp match {
          case AnnotatedType(Annot(Apply(Select(New(tp @ TypeTree()), termNames.CONSTRUCTOR),
          List(an,Literal(Constant(uuid:String)))))::Nil,undertp) if tp.symbol.fullName == "squid.statics.withStaticTree"
          =>
            debug(s"Ann tree: $an")
            staticCache get uuid match {
              case Some(value) =>
                val nme = c.freshName(idname)
                val oldWIC = wrapInContext
                wrapInContext = t =>
                  q"($nme: $undertp) => ${t |> oldWIC}"
                  // ^ make sure undertp is static?
                args ::= value
                Ident(nme)
              case None => an
            }
          case _ =>
            debug("Oops",idtp.getClass)
            c.abort(id.pos,s"Non-static identifier '$id' of type: $idtp")
        }
        
      case id @ Ident(_: TypeName) if !id.symbol.isStatic =>
        c.abort(id.pos,s"Non-static type reference: $id")
        
    }
    
    // TODO somehow analyse all remaining typres and make sure that they are all statically-accessible...
    //tree.analyse { case tr @ TypeTree() => }
    
    debug(s"Tree: $tree")
    
    val ctxTree = tree |> wrapInContext
    debug(s"CtxTree: $ctxTree")
    
    val cdeStr = showCode(ctxTree)
    debug(s"Code string $cdeStr")
    
    val toolBox = squid.lang.IntermediateBase.toolBox
    val cde = toolBox.parse(cdeStr)
    debug(s"Code $cde")
    
    val lambda = toolBox.eval(cde)
    val value = args.foldLeft(lambda){case(newTree,arg)=>newTree.asInstanceOf[Any=>Any](arg)}
    debug(s"Value $value")
    
    val uuid = java.util.UUID.randomUUID().toString()
    staticCache += uuid -> value
    
    q"$a : ${internal.annotatedType(Annotation(
      c.typecheck(q"new _root_.squid.statics.withStaticTree($a,$uuid)")
    ) :: Nil, weakTypeOf[A])}"
    
  }
  
}
