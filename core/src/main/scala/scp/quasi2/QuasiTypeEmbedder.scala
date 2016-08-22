package scp
package quasi2

import utils._
import lang2._
import scp.quasi.EmbeddingException

abstract class QuasiTypeEmbedder[C <: scala.reflect.macros.whitebox.Context, B <: Base](val c: C, val base: B, debug: String => Unit) {
  import c.universe._
  
  val helper: meta.UniverseHelpers[c.universe.type]
  import helper._
  
  val baseTree: Tree
  
  class Impl extends ModularEmbedding[c.universe.type, B](c.universe, base, debug) {
    
    def className(cls: ClassSymbol): String = {
      //debug("CLS NAME",cls)
      //srum.runtimeClass(imp.importSymbol(cls).asClass).getName
      
      def tryAgainJerryJoe(bullets: Int): String =  // TODO try simple asInstanceOf instead of importer 
        try srum.runtimeClass(imp.importSymbol(cls).asClass).getName
        catch { case _: java.lang.IndexOutOfBoundsException => if (bullets > 0) tryAgainJerryJoe(bullets-1) else {
          System.err.println(s"Ay, I missed $cls again Billy Ben. Let's hope the Sherriff won't see it.")
          cls.fullName
        }}
      tryAgainJerryJoe(8)
    }
    
    override def unknownTypefallBack(tp: Type): base.TypeRep = {
      
      debug(s"Lifting unknown type $tp (${tp.widen.dealias})")
      
      if (tp.widen =:= typeOf[QuasiBase.`<extruded type>`] || tp.widen.contains(symbolOf[QuasiBase.`<extruded type>`])) {
        debug(s"Detected widened type hole: ${tp.widen}")
        val purged = tp.toString.replaceAll(typeOf[QuasiBase.`<extruded type>`].toString, "<extruded type>")
        throw EmbeddingException(s"Precise info for extracted type was lost, possibly because it was extruded from its defining scope, in: $purged")
      }
      
      
      val irType = c.typecheck(tq"$baseTree.IRType[$tp]", c.TYPEmode) // TODO use internal.typeRef
      c.inferImplicitValue(irType.tpe, withMacrosDisabled = true) match {
        case EmptyTree =>
        case impt => return q"$impt.rep".asInstanceOf[base.TypeRep] // FIXME
      }
      
      val vals = c.asInstanceOf[reflect.macros.runtime.Context].callsiteTyper.context.enclosingContextChain.flatMap {
        _.scope collect {
          case sym if sym.isVal
            && sym.isInitialized // If we look into the type of value being constructed (eg `val x = exp"42"`),
                                 // it will trigger a 'recursive value needs type' error
            && sym.name == tp.typeSymbol.name.toTermName
          =>
            //debug(sym, sym.isInitialized)
            sym -> sym.tpe
        }
      }.asInstanceOf[List[(TermSymbol, Type)]]
      
      val QTSym = symbolOf[QuasiBase#IRType[_]]
      
      vals foreach {
        case (sym, TypeRef(tpbase, QTSym, tp::Nil)) if tpbase =:= baseTree.tpe =>
          debug("FOUND QUOTED TYPE "+sym)
          return q"$sym.rep".asInstanceOf[base.TypeRep] // FIXME
        case _ =>
      }
      
      
      if (tp <:< typeOf[QuasiBase.`<extruded type>`] && !(tp <:< Nothing)) {
        
        throw EmbeddingException(s"Could not find type evidence associated with extracted type `$tp`.")
        
      } else {
        val tagType = c.typecheck(tq"_root_.scala.reflect.runtime.universe.TypeTag[$tp]", c.TYPEmode)
        c.inferImplicitValue(tagType.tpe) match {
          case EmptyTree =>
            
            if (tp.typeSymbol == symbolOf[Array[_]]) {
              // Note: srum.runtimeClass(sru.typeOf[Array[Int]]).toString == [I
              throw EmbeddingException.Unsupported("Arrays of unresolved type.")
            }
            
            throw EmbeddingException(s"Unknown type `$tp` does not have a TypeTag to embed it as uninterpreted.")
          case impt =>
            super.unknownTypefallBack(tp)
        }
      }
      
      
      
      
      
    }
    
  }
  
  
}
