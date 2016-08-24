package scp

import utils._
import utils.meta.RuntimeUniverseHelpers.{sru, srum}
import utils.MacroUtils.{MacroDebug, MacroDebugger, MacroSetting}
import ir2._
import quasi2._
import lang2.InspectableBase

import scala.language.experimental.macros

import scala.annotation.{StaticAnnotation, compileTimeOnly}

class Squid[Trans <: Transformer{val base: InspectableBase}] {
  
  def optimize[A](code: A): A = macro SquidMacros.optimizeImpl[Trans]
  @MacroSetting(debug = true) def dbg_optimize[A](code: A): A = macro SquidMacros.optimizeImpl[Trans]
  
}

/** TODO generate a macro that lifts passed arguments and compiles the body (caching/or inlining) -- like a static staging */
@compileTimeOnly("Enable macro paradise to expand macro annotations.")
class template(squid: Squid[_]) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ???
}

@compileTimeOnly("Enable macro paradise to expand macro annotations.")
class optimize(squid: Squid[_]) extends StaticAnnotation {
//class optimize(implicit squid: Squid[_]) extends StaticAnnotation { // Cannot get it to work properly
  //def macroTransform(annottees: Any*): Any = macro SquidMacros.optimizeAnnotImpl[Trans] // implementation restriction: macro annotation impls cannot have typetag context bounds (consider taking apart c.macroApplication and manually calling c.typecheck on the type arguments)
  def macroTransform(annottees: Any*): Any = macro SquidMacros.optimizeAnnotImpl
}

import scala.reflect.macros.whitebox
import scala.reflect.macros.blackbox

class SquidMacros(val c: blackbox.Context) {
  import c.universe._
  
  //def optimizeAnnotImpl[Trans: WeakTypeTag](annottees: Tree*) = {
  def optimizeAnnotImpl(annottees: Tree*) = {
    
    val squid = c.macroApplication match {
      case q"new optimize($squid).macroTransform(..$_)" => squid
      //case q"new optimize().macroTransform(..$_)" =>
      //  q"_root_.scala.Predef.implicitly[Squid[_]]"
      //  //val impt = c.inferImplicitValue(typeOf[Squid[_]]) // <empty>
    }
    
    annottees match {
      case DefDef(mods, name, tparams, vparamss, tpt, rhs) :: Nil =>
        DefDef(mods, name, tparams, vparamss, tpt, q"$squid.optimize { $rhs }")
    }
  }
  
  def optimizeImpl[Trans: WeakTypeTag](code: Tree) = {
    
    val debug = { val mc = MacroDebugger(c.asInstanceOf[whitebox.Context]); mc[MacroDebug] }
    
    val imp = scala.reflect.runtime.universe.internal.createImporter(c.universe)
    
    val Trans = Class.forName(weakTypeOf[Trans].typeSymbol.asClass.fullName).newInstance().asInstanceOf[ir2.Transformer{val base: InspectableBase}]
    val Base: Trans.base.type = Trans.base
    
    val varRefs = collection.mutable.Buffer[(String, Tree)]()
    
    object ME extends ModularEmbedding[c.universe.type, Base.type](c.universe, Base, str => debug(str)) {
      def className(cls: ClassSymbol): String = srum.runtimeClass(imp.importSymbol(cls).asClass).getName
      
      override def unknownFeatureFallback(x: Tree, parent: Tree) = x match {
          
        case Ident(TermName(name)) =>
          varRefs += name -> x
          base.hole(name, liftType(x.tpe))  // FIXME is it safe to introduce holes with such names?
          
        case _ =>
          super.unknownFeatureFallback(x, parent)
          
      }
      
    }
    
    val newCode = //Trans.TranformerDebug.debugFor
      ME(code)
    
    debug("AST: "+newCode)
    
    object MBM extends MetaBases {
      val u: c.universe.type = c.universe
      def freshName(hint: String) = c.freshName(TermName(hint))
    }
    val MB = new MBM.ScalaReflectionBase
    
    val res = //EB.debugFor
      { Trans.base.reinterpret(newCode, MB) }
    
    //debug("Optimized: ", showCode(res))
    
    // This works but is unnecessary, as currently holes in ScalaReflectionBase are just converted to identifiers
    //res = MB.substitute(res, varRefs.toMap)
    
    debug("Generated: ", showCode(res))
    
    res: Tree
  }
  
  
}
