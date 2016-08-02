package scp

import scala.language.experimental.macros
import reflect.macros.whitebox.Context

import lang2._
import ir2._
import quasi2._
import scp.utils.MacroUtils._
import utils.meta.RuntimeUniverseHelpers._

object MacroTesters {
  
  def embed(base: Base)(code: Any): base.Rep = macro embedImpl
  def embedImpl(c: Context)(base: c.Tree)(code: c.Tree) = {
    import c.universe._
    
    //val debug = { val mc = MacroDebugger(c); mc[NoDebug] }
    //debug("INPUT", showCode(code))
    
    val baseName = TermName("__b")
    
    object MBM extends MetaBases {
      val u: c.universe.type = c.universe
      def freshName(hint: String) = c.freshName(TermName(hint))
    }
    val MB = new MBM.MirrorBase( q"$baseName" )
    
    object ME extends ModularEmbedding[c.universe.type, MBM.MirrorBase](c.universe, MB,
      //str => debug(str)
      str => ()
    ){
      def className(cls: ClassSymbol): String = {
        //debug("CLS NAME",cls)
        srum.runtimeClass(imp.importSymbol(cls).asClass).getName
      }
    }
    
    val newCode = ME(code)
    
    val defs = q"val $baseName = $base" +: MB.mkSymbolDefs
    
    q"..$defs; $newCode"
  }
  
  
  def shallowAndDeep[A](base: Base)(code: A): (base.Rep, A) = macro shallowAndDeepImpl
  def shallowAndDeepImpl(c: Context)(base: c.Tree)(code: c.Tree) = {
    import c.universe._
    //val debug = { val mc = MacroDebugger(c); mc[NoDebug] }
    //debug("INPUT", showCode(code))
    q"scp.MacroTesters.embed($base)($code) -> $code"
  }
  
  
  def staticExecAsConst[Base <: IntermediateBase](code: Any): Any = macro staticExecAsConstImpl[Base]
  def staticExecAsConstImpl[AST: c.WeakTypeTag](c: Context)(code: c.Tree) = {
    import c.universe._
    
    //val debug = { val mc = MacroDebugger(c); mc[NoDebug] }
    val debug = { val mc = MacroDebugger(c); mc[ApplyDebug] } // will cut dbg unless MacroUtils.DebugLevel <: ApplyDebug
    
    val m: sru.Mirror = reflect.runtime.currentMirror
    val imp = scala.reflect.runtime.universe.internal.createImporter(c.universe)
    
    val AST = Class.forName(weakTypeOf[AST].typeSymbol.asClass.fullName).newInstance().asInstanceOf[IntermediateBase]
    object ME extends ModularEmbedding[c.universe.type, AST.type](c.universe, AST, str => debug(str)) {
      def className(cls: ClassSymbol): String = m.runtimeClass(imp.importSymbol(cls).asClass).getName
    }
    val newCode = ME(code)
    debug("AST: "+newCode)
    
    val EB = new BaseInterpreter
    
    val res = AST.reinterpret(newCode, EB)
    debug("EXEC", res)
    
    Literal(Constant(res))
    
  }
  
  def staticExecAndSource[Base](code: Any): (Any, Any) = macro staticExecWorksImpl[Base]
  def staticExecWorksImpl[AST: c.WeakTypeTag](c: Context)(code: c.Tree) = {
    import c.universe._
    q"${staticExecAsConstImpl[AST](c)(code)} -> $code"
  }
  
  
  
  
}
