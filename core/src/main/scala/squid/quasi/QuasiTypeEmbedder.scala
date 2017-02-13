package squid
package quasi

import utils._
import squid.lang.Base

/* TODO: make it work with intermediate bases (eg: `ViaASTQuasiConfig`) */
abstract class QuasiTypeEmbedder[C <: scala.reflect.macros.whitebox.Context, B <: Base](val c: C, val base: B, debug: String => Unit) {
  import c.universe._
  
  val helper: meta.UniverseHelpers[c.universe.type]
  import helper._
  
  val baseTree: Tree
  
  class Impl extends ModularEmbedding[c.universe.type, B](c.universe, base, debug) {
    
    def insertTypeEvidence(ev: base.TypeRep): base.TypeRep = ev
    
    override def unknownTypefallBack(tp: Type): base.TypeRep = {
      
      debug(s"Lifting unknown type $tp (${tp.widen.dealias})")
      
      if (tp.widen =:= typeOf[QuasiBase.`<extruded type>`] || tp.widen.contains(symbolOf[QuasiBase.`<extruded type>`])) {
        debug(s"Detected widened type hole: ${tp.widen}")
        val purged = tp.toString.replaceAll(typeOf[QuasiBase.`<extruded type>`].toString, "<extruded type>")
        throw EmbeddingException(s"Precise info for extracted type was lost, possibly because it was extruded from its defining scope, in: $purged")
      }
      
      
      val irType = c.typecheck(tq"$baseTree.IRType[$tp]", c.TYPEmode) // TODO use internal.typeRef
      debug(s"Searching for an `$irType` implicit")
      c.inferImplicitValue(irType.tpe, withMacrosDisabled = true) match {
        case EmptyTree =>
        case impt =>
          debug(s"Found: "+showCode(impt))
          return (q"$impt.rep".asInstanceOf[base.TypeRep] // FIXME
            |> insertTypeEvidence)
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
          return (q"$sym.rep".asInstanceOf[base.TypeRep] // FIXME
            |> insertTypeEvidence)
        case _ =>
      }
      
      
      if (tp <:< typeOf[QuasiBase.`<extruded type>`] && !(tp <:< Null)) { // Note that: tp <:< Nothing ==> tp <:< Null so no need for the test
        
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
            
            // We used to do the following, which helped to let-bind (using the caching mechanism of `ModularEmbedding`,
            // since this would call the base's `uninterpretedType`). However, now `uninterpretedType` is overridden and
            // inserted types are aggregated and named, so that caching can index on them, so this is no more necessary:
            /*
            // Note: not using `impt`! cf. below... (we just ensure it exists to give the better error message above)
            super.unknownTypefallBack(tp) // unnecessary, as it just generates a call to uninterpretedType without the implicit resolved
            // ^ actually useful to cache the tag!
            */
            
            q"$baseTree.uninterpretedType($impt)".asInstanceOf[base.TypeRep] |> insertTypeEvidence
            
            // Older:
            /* Does not help, as it bypasses the underlying base:
             * although the tree creation is cached, it will NOT get let-bound, and will be duplicated all over the place */
            //return typeCache.getOrElseUpdate(tp, q"$baseTree.uninterpretedType[$tp]($impt)".asInstanceOf[base.TypeRep])
            
        }
        
      }
      
      
      
      
      
    }
    
  }
  
  
}
