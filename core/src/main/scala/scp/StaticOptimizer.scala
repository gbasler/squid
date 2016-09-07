package scp

import utils._
import utils.meta.RuntimeUniverseHelpers.{sru, srum}
import utils.MacroUtils.{MacroDebug, MacroDebugger, MacroSetting}
import lang2._
import quasi2._

import scala.language.experimental.macros
import scala.annotation.{StaticAnnotation, compileTimeOnly}

class StaticOptimizer[Optim <: Optimizer] {
  
  def optimize[A](code: A): A = macro StaticOptimizerMacros.optimizeImpl[Optim]
  @MacroSetting(debug = true) def dbg_optimize[A](code: A): A = macro StaticOptimizerMacros.optimizeImpl[Optim]
  
}

/** TODO generate a macro that lifts passed arguments and compiles the body (caching/or inlining) -- like a static staging */
@compileTimeOnly("Enable macro paradise to expand macro annotations.")
class template(stopt: StaticOptimizer[_]) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ???
}

@compileTimeOnly("Enable macro paradise to expand macro annotations.")
class optimize(stopt: StaticOptimizer[_]) extends StaticAnnotation {
//class optimize(implicit squid: Squid[_]) extends StaticAnnotation { // Cannot get it to work properly
  //def macroTransform(annottees: Any*): Any = macro StaticOptimizerMacros.optimizeAnnotImpl[Trans] // implementation restriction: macro annotation impls cannot have typetag context bounds (consider taking apart c.macroApplication and manually calling c.typecheck on the type arguments)
  def macroTransform(annottees: Any*): Any = macro StaticOptimizerMacros.optimizeAnnotImpl
}

import scala.reflect.macros.whitebox
import scala.reflect.macros.blackbox

class StaticOptimizerMacros(val c: blackbox.Context) {
  import c.universe._
  
  
  def optimizeAnnotImpl(annottees: Tree*) = {
    
    val stopt = c.macroApplication match {
      case q"new optimize($stopt).macroTransform(..$_)" => stopt
      //case q"new optimize().macroTransform(..$_)" =>
      //  q"_root_.scala.Predef.implicitly[StaticOptimizer[_]]"
      //  //val impt = c.inferImplicitValue(typeOf[Squid[_]]) // <empty>
    }
    
    annottees match {
      case DefDef(mods, name, tparams, vparamss, tpt, rhs) :: Nil =>
        DefDef(mods, name, tparams, vparamss, tpt, q"$stopt.optimize { $rhs }")
    }
  }
  
  
  def optimizeImpl[Comp: WeakTypeTag](code: Tree) = {
    
    val debug = { val mc = MacroDebugger(c.asInstanceOf[whitebox.Context]); mc[MacroDebug] }
    
    val Optim = {
      val Comp = weakTypeOf[Comp]
      val inst = try Class.forName(Comp.typeSymbol.asClass.fullName).newInstance()
      catch {
        case e: Throwable =>
          c.error (c.enclosingPosition, s"The type parameter `$Comp` you passed to StaticOptimizer could not be instantiated without parameters: "+e.getMessage)
          throw e
      }
      try inst.asInstanceOf[Optimizer]
      catch {
        case e: ClassCastException => c.abort(c.enclosingPosition, "The type parameter you passed to StaticOptimizer does not conform: "+e.getMessage)
      }
    }
    val Base: Optim.base.type = Optim.base
    
    val varRefs = collection.mutable.Buffer[(String, Tree)]()
    
    object ME extends ModularEmbedding[c.universe.type, Base.type](c.universe, Base, str => debug(str)) {
      
      override def unknownFeatureFallback(x: Tree, parent: Tree) = x match {
          
        case Ident(TermName(name)) =>
          varRefs += name -> x
          base.hole(name, liftType(x.tpe))  // FIXME is it safe to introduce holes with such names?
          
        case _ =>
          super.unknownFeatureFallback(x, parent)
          
      }
      
    }
    
    var newCode = //Optim.TranformerDebug.debugFor
      ME(code)
    
    debug("Code: "+Base.showRep(newCode))
    
    newCode = Optim.optimizeRep(newCode)
    
    debug("Optimized Code: "+Base.showRep(newCode))
    
    object MBM extends MetaBases {
      val u: c.universe.type = c.universe
      def freshName(hint: String) = c.freshName(TermName(hint))
    }
    val MB = new MBM.ScalaReflectionBase
    
    //val res = Optim.base.scalaTreeIn(MBM)(MB, newCode)
    val res = Optim.base.scalaTreeInWTFScala[MBM.type](MBM)(MB, newCode)
    
    // This works but is unnecessary, as currently holes in ScalaReflectionBase are just converted to identifiers
    //res = MB.substitute(res, varRefs.toMap)
    
    debug("Generated: ", showCode(res))
    
    /** The Scala compiler used to crash with a StackOverflow at scala.tools.nsc.Global$Run.compiles(Global.scala:1402)!!
      * unless we used c.parse & showCode. It seems this was because we were splicing symbols directly into QQs */
    res: Tree
    //c.untypecheck(res)
    //c.parse(showCode(res))
  }
  
  
}
