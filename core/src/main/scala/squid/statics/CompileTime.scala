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

import squid.ir.BaseInterpreter
import squid.quasi.{ModularEmbedding, QuasiMacros}
import squid.utils._

/** Standard class for Squid functionalities that require some values to be available at compile time; this type can be
  * to provide compile-time values implicitly, and lifts normal value to compile-time ones automatically. */
final class CompileTime[A](val get: A)

object CompileTime {
  
  import scala.language.experimental.macros
  
  /** `CompileTime(x)` is really just syntax sugar for `compileTime{new CompileTime(x)}` */
  implicit def apply[A](a: A): CompileTime[A] = macro CompileTimeMacros.CompileTimeImpl[A]
  
  // Q: is the implicit lifting actually useful? I doubt it. 
}


import scala.annotation.StaticAnnotation

/** Used to annotate types with a static tree representing the expression, so it can be evaluated at compile time (this
  * is related to what the Quill library does for its queries) */
/* this used to also contain a cache id to retrieve values previously computed in the same compilation run, but this was
 * causing too much complexity in the implementation for too little gain. */
class withStaticTree(reprTree: Any) extends StaticAnnotation


import scala.reflect.macros.{blackbox, whitebox}

//class CompileTimeMacros(override val c: blackbox.Context) extends QuasiBlackboxMacros(c) {
// ^ strangely, this behaves differently, and raises errors of the form "AbortMacroException: Non-static identifier 't' of type: String"
class CompileTimeMacros(override val c: whitebox.Context) extends QuasiMacros(c) {
  import c.universe._
  
  import Helpers._
  
  object Annot { def unapply(an: Annotation): Option[Tree] = an.tree optionIf (_.nonEmpty) }
  
  def compileTimeExecImpl(cde: Tree): Tree = {
    compileTimeEvalImpl[Unit](cde)
  }
  def compileTimeEvalImpl[A: WeakTypeTag](cde: Tree): Tree = wrapError {
    debug(s"Compile-time eval code: ${showCode(cde)}")
    val inlCde = cde |> inlineStaticParts
    debug(s"Inlined code: ${showCode(inlCde)}")
    val res = inlCde |> eval
    debug(s"Result: $res")
    res match {
      case _:Unit|_:Bool|_:Short|_:Int|_:Long|_:Float|_:Double|_:String => Literal(Constant(res))
      case _ => q"_root_.squid.utils.serial.deserialize(${serial.serialize(res)}).asInstanceOf[${weakTypeOf[A]}]"
    }
  }
  
  def eval(cde: Tree): Any = {
    val EB = new BaseInterpreter
    object ME extends ModularEmbedding[c.universe.type, EB.type](c.universe, EB, str => debug(str))
    val value = wrapSymbolLoadingErrors(EB, ME(cde))
    value
  }
  
  def inlineStaticParts(cde: Tree): Tree = {
    
    /*_*/
    val tree = cde transform {
      
      case CompileTimeAnnotatedTree(reprTree) => reprTree
        
      case id @ Ident(idname: TermName) =>
        assert(id.symbol =/= null, s"Identifier with 'null' symbol found: $id")
        
        if (id.symbol.isStatic) {
          
          if (id.symbol.fullName.contains('.'))
            // type-checked trees may still have not-fully-qualified identifiers, for some reason; this hack fully-qualifies them
            c.typecheck(c.parse(id.symbol.fullName))
          else id
          
        } else {
          
          // TODO better way of making sure type of `id` is 'thoroughly' static...
          //tp.foreach { case subtp: TypeRef =>
          //  if (!subtp.typeSymbol.isStatic) c.abort(pos, s"Non-static subtype '${subtp}' of type: $tp")
          //case _ => }
          
          val idtp = id.tpe.widen
          accessAnnotatedTree(idtp) getOrElse c.abort(id.pos,s"Non-static identifier '$id' of type: $idtp")
          
        }
        
      case id @ Ident(_: TypeName) if !id.symbol.isStatic =>
        c.abort(id.pos,s"Non-static type reference: $id")
        
    }
    
    // TODO somehow analyse all remaining typres and make sure that they are all statically-accessible...
    //tree.analyse { case tr @ TypeTree() => }
    
    debug(s"Inlined Tree: $tree")
    
    tree
    
  }
  
  def CompileTimeImpl[A: WeakTypeTag](a: Tree): Tree =
    q"_root_.squid.statics.compileTime(new _root_.squid.statics.CompileTime($a))"
  
  // TODO: handle the many possible sources of errors/exceptions more gracefully
  def compileTimeImpl[A: WeakTypeTag](a: Tree): Tree = wrapError {
    debug(s"Executing static{ ${showCode(a)} }")
    
    val tree = inlineStaticParts(a)
    
    q"$a : ${internal.annotatedType(Annotation(
      c.typecheck(q"new _root_.squid.statics.withStaticTree($tree)")
    ) :: Nil, weakTypeOf[A])}"
    
  }
  
  def accessAnnotatedTree(tp: Type): Option[Tree] = {
    
    tp match {
      case AnnotatedType(Annot(Apply(
          Select(New(tp @ TypeTree()), termNames.CONSTRUCTOR), List(an)
        )) :: Nil, undertp)
      if tp.symbol.fullName == "squid.statics.withStaticTree"
      =>
        debug(s"Ann tree: $an")
        Some(an)
      case _ =>
        //debug("Oops",tp,tp.getClass,tp.typeSymbol.annotations)
        None
    }
    
  }
  object CompileTimeAnnotatedTree {
    def unapply(x: Tree) =
      if (x.isTerm) accessAnnotatedTree(x.tpe.widen).orElse {
          if (x.symbol === null) None
          else x.symbol.typeSignature |>=? {
            case NullaryMethodType(typ) => typ
          } |> accessAnnotatedTree
      }
      else None
  }
  
  def resolveCompileTimeImplicit[T:WeakTypeTag](seeker: String) = {
    c.inferImplicitValue(c.weakTypeOf[T]) match {
      case EmptyTree => None
      case CompileTimeAnnotatedTree(annot) =>
        annot |> eval |> (_.asInstanceOf[T]) |> Some.apply
      case tree =>
        c.warning(tree.pos,
          s"A compile-time implicit value of type ${weakTypeOf[T]} was seeked by $seeker, " +
            s"but an expression was inferred from which a compile-time value could not be retrieved: ${showCode(tree)}")
        None
    }
  }
  
}
