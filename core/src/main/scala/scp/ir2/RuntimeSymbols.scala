package scp
package ir2

import utils._
import collection.mutable

object RuntimeSymbols extends RuntimeSymbols with TraceDebug { // TODO move this to general helpers
  
  private val typSymbolCache = mutable.HashMap[String, TypSymbol]()
  
}
trait RuntimeSymbols {
  import RuntimeSymbols._
  import meta.RuntimeUniverseHelpers._
  
  protected def ensureDefined(name: String, sym: sru.Symbol) = sym match {
    case sru.NoSymbol => 
      throw new Exception(s"Could not find $name")
    case _ => sym
  }
  
  type TypSymbol = sru.TypeSymbol
  //type TypSymbol = sru.ClassSymbol
  type MtdSymbol = sru.MethodSymbol
  
  def loadTypSymbol(fullName: String): TypSymbol = typSymbolCache.synchronized { typSymbolCache.getOrElseUpdate(fullName, {
    val className = fullName match {
      case "boolean" => "scala.Boolean"
      case "byte" => "scala.Byte"
      case "char" => "scala.Char"
      case "short" => "scala.Short"
      case "int" => "scala.Int"
      case "long" => "scala.Long"
      case "float" => "scala.Float"
      case "double" => "scala.Double"
      case "void" => "scala.Unit"
      case n => n
    }
    debug(s"Loading type from class name $className")
    srum.classSymbol(Class.forName(className)) //and (x => debug(s"Loaded: $x"))
  })}
  
  def loadMtdSymbol(typ: TypSymbol, symName: String, index: Option[Int], static: Boolean = false): MtdSymbol = {
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


















