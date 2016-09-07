package scp
package ir2

import utils._
import meta.RuntimeUniverseHelpers._

import collection.mutable

object RuntimeSymbols extends RuntimeSymbols with PublicTraceDebug { // TODO move this to general helpers
  
  private val typSymbolCache = mutable.HashMap[String, ScalaTypeSymbol]() // FIXME not used???
  
}
//trait RuntimeSymbols extends TraceDebug {
trait RuntimeSymbols {
  import RuntimeSymbols._
  
  /*
  // TODO migrate to use these instead
  def classSymbol(rtcls: Class[_]): sru.ClassSymbol = srum.classSymbol(rtcls)
  def moduleSymbol(rtcls: Class[_]): sru.ModuleSymbol = {
    val clsSym = srum.classSymbol(rtcls)
    srum.moduleSymbol(rtcls)  oh_and  ScalaReflectSurgeon.cache.enter(rtcls, clsSym) 
  }
  */
  
  /*protected*/ def ensureDefined(name: String, sym: sru.Symbol) = sym match {
    case sru.NoSymbol => 
      throw new Exception(s"Could not find $name")
    case _ => sym
  }
  
  //type TypSymbol = sru.ClassSymbol
  //type TypSymbol = sru.TypeSymbol
  type ScalaTypeSymbol = sru.TypeSymbol
  //private[this] type TypSymbol = sru.TypeSymbol
  type MtdSymbol = sru.MethodSymbol
  
  def loadTypSymbol(fullName: String): ScalaTypeSymbol = {
    val path = fullName.splitSane('#')
    val root = if (path.head endsWith "$") srum.staticModule(path.head.init) else srum.staticClass(path.head)
    val res = path.tail.foldLeft(root) {
      case (s, name) if name.endsWith("$") => s.asType.toType.member(sru.TermName(name.init))
      case (s, name) => s.asType.toType.member(sru.TypeName(name))
    }
    (if (res.isType) res.asType else res.typeSignature.typeSymbol.asType) and (x => debug(s"Loaded: $x"))
  }
  
  def loadMtdSymbol(typ: ScalaTypeSymbol, symName: String, index: Option[Int], static: Boolean = false): MtdSymbol = { // TODO cache!!
    debug(s"Loading method $symName from $typ"+(if (static) " (static)" else ""))
    
    /** Because of a 2-ways caching problem in the impl of JavaMirror,
      * a Java class name like "java.lang.String" can return either 'object String' or 'class String'... */
    val tp = if (typ.isJava && (static ^ typ.isModuleClass))
      typ.companion.typeSignature else typ.toType
    
    val sym = ensureDefined(s"'$symName' in $typ", tp.member(sru.TermName(symName)))
    if (sym.alternatives.nonEmpty) debug("Alts: "+sym.alternatives.map(_.typeSignature).map("\n\t"+_))
    
    sym.alternatives(index getOrElse 0).asMethod
  }
  
}


















