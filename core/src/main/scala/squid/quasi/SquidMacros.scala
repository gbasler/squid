package squid
package quasi

import utils._
import utils.CollectionUtils._
import utils.meta.RuntimeUniverseHelpers.{sru, srum}
import utils.MacroUtils.{MacroDebug, MacroDebugger, MacroSetting}
import lang._
import quasi._
import squid.ir._

import scala.language.experimental.macros
import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.collection.mutable

import scala.reflect.macros.whitebox
//import scala.reflect.macros.blackbox

@compileTimeOnly("Enable macro paradise to expand macro annotations.")
//class macroDef[B <: Base](base: B) extends StaticAnnotation {
class macroDef(base: IntermediateBase) extends StaticAnnotation {
  @MacroSetting(debug = true) def macroTransform(annottees: Any*): Any = macro SquidMacros.macroDef
}

/**
  * Created by lptk on 21/06/17.
  * 
  * TODO factor the modularEmbedding logic with StaticOptimizerMacros; handle free variables
  * 
  * TODO let-bind `base` or make sure it's simple
  * 
  * TODO make gen'd code catch embedding errors and fail gracefully
  * 
  */
class SquidMacros(override val c: whitebox.Context) extends QuasiMacros(c) {
  import c.universe._
  import Helpers._
  
  def macroDef(annottees: Tree*): Tree = wrapError {
    
    //annottees match {
    //  case Seq(DefDef(mods, name, tparams, vparamss, tpt, rhs)) =>
        //println(name)
    val Seq(DefDef(mods, name, tparams, vparamss, tpt, rhs)) = annottees // TODO handle match fail
    val q"new macroDef($base).macroTransform(..$_)" = c.macroApplication // TODO handle match fail
    
    val vparamssRenamed = vparamss.map(_.map{case ValDef(mods, name, tpt, default) => ValDef(mods, TermName(s"_$name"), tpt, default)})
    
    if (tpt.isEmpty) ??? // TODO
    
    val macroName = TermName(name+"Impl")
    
    //val macroImpl = q"macro $macroName"
    //val macroImpl = internal.
    //Ident
    //val macroImpl = q"${Ident(TermName("macro"))} $macroName"
    val macroDef = DefDef(Modifiers(Flag.MACRO), name, tparams, vparamssRenamed, tpt, Ident(macroName)) // TODO propagate other mods; Q: default values handled properly?
    val macroDefDbg = DefDef(Modifiers(Flag.MACRO,typeNames.EMPTY,q"new utils.MacroUtils.MacroSetting(debug = true)"::Nil), TermName(s"dbg_$name"), tparams, vparamssRenamed, tpt, Ident(macroName)) // TODO propagate other mods; Q: default values handled properly?
    
    val res =
    q"""
      import scala.language.experimental.macros
      import scala.reflect.macros.whitebox
      import scala.reflect.macros.blackbox
      def ${macroName}(c: blackbox.Context)(...${
        vparamssRenamed map(_ map (t => q"val ${t.name}: c.Tree")) // TODO handle varargs etc
      }): c.Tree = {
        import squid._
        import utils._
        import lang._
        import quasi._
        import utils.MacroUtils.{MacroSetting, MacroDebug, MacroDebugger}
        //val debug = { val mc = utils.MacroUtils.MacroDebugger(c.asInstanceOf[blackbox.Context]); mc[MacroDebug] }
        //val debug = { val mc = MacroDebugger[c.type](c); mc[MacroDebug] }
        val debug = { val mc = MacroDebugger(c.asInstanceOf[c.type with whitebox.Context]); mc[MacroDebug] }
        object ME extends ModularEmbedding[c.universe.type, $base.type](c.universe, $base, str => debug(str)) {
          // TODO...
        }
        
        //var newArgs = ME(code)
        
        ..${
          vparamssRenamed.flatMap { _ map {
            case ValDef(mods, name @ TermName(nameStr), tpt, default) => 
              val realName = TermName(nameStr.tail)
              //q"val $realName = $base.IR[$tpt,Any](ME($name))"
              //q"""val $realName = {
              //  debug("Embedding argument `"+c.universe.showCode($name)+"`...")
              //  val res = ME($name)
              //  debug("Embedded as: "+$base.showRep(res))
              //  $base.IR[$tpt,Any](res)
              //}"""
              q"""val $realName = {
                debug("Embedding argument `"+c.universe.showCode($name)+"`...")
                val res = $base.IR[$tpt,Any](ME($name))
                debug("Embedded as: "+res)
                res
              }"""
          }}
        }
        
        object MBM extends MetaBases {
          val u: c.universe.type = c.universe
          def freshName(hint: String) = c.freshName(c.universe.TermName(hint))
        }
        val MB = new MBM.ScalaReflectionBase
        
        //val res = Optim.base.scalaTreeIn(MBM)(MB, newCode)
        val res = $base.scalaTreeInWTFScala[MBM.type](MBM)(MB, $rhs.rep)
        
        // This works but is unnecessary, as currently holes in ScalaReflectionBase are just converted to identifiers
        //res = MB.substitute(res, varRefs.toMap)
        
        debug("Generated: ", c.universe.showCode(res))
        
        res : c.Tree
        
      }
      $macroDef
      $macroDefDbg
    """
    
    //debug(showRaw(q"def a = macro b"))
    
    /*
    val res =
    //q"""
    //  $mods def ${macroName}[..$tparams](...$vparamss): $tpt = $rhs
    //  $mods def $name[..$tparams](...$vparamss): $tpt = macro $macroName
    //"""
    q"""
      import scala.language.experimental.macros
      import scala.reflect.macros.blackbox
      $mods def ${macroName}(c: blackbox.Context): c.Tree = $rhs
      $mods def $name[..$tparams](...$vparamss): $tpt = macro $macroName
    """
    //q"""
    //  $mods def ${macroName}[..$tparams](...$vparamss): $tpt = $rhs
    //  $mods def $name[..$tparams](...$vparamss): $tpt = $macroImpl
    //"""
    */
    
    //val res = DefDef(Modifiers(Flag.MACRO), TermName("a"), List(), List(), TypeTree(), Ident(TermName("b")))
    
    debug("Generated: "+showCode(res))
    
    res
    
    //}
    
    //???
    
  }
  
}
