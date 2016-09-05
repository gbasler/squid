package scp
package quasi2

import utils._
import utils.CollectionUtils._
import utils.meta.RuntimeUniverseHelpers.{sru, srum}
import utils.MacroUtils.{MacroDebug, MacroDebugger, MacroSetting}
import lang2._
import quasi2._
import scp.ir2._
import scp.quasi.EmbeddingException

import scala.language.experimental.macros
import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.collection.mutable

@compileTimeOnly("Enable macro paradise to expand macro annotations.")
class embed extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ClassEmbedding.embedImpl
}

class phase(name: Symbol) extends StaticAnnotation

import scala.reflect.macros.whitebox
import scala.reflect.macros.blackbox

/** TODO class methods with 'this' references */
object ClassEmbedding {
  
  /** TODO catch excepts and properly abort */
  def embedImpl(c: whitebox.Context)(annottees: c.Tree*) = {
    import c.universe._
    
    val (clsDefOpt, objDef) = annottees match {
      case (cls: ClassDef) :: (obj: ModuleDef) :: Nil => Some(cls) -> obj
      case (obj: ModuleDef) :: (cls: ClassDef) :: Nil => Some(cls) -> obj // Never actually happens -- if the object is annotated, the class doesn't get passed!
      case (cls: ClassDef) :: Nil => Some(cls) -> q"object ${cls.name.toTermName}"
      case (obj: ModuleDef) :: Nil =>
        Some(ClassDef(Modifiers(), obj.name.toTypeName, Nil, Template(Nil, ValDef(Modifiers(), termNames.WILDCARD, EmptyTree, EmptyTree), Nil))) -> obj
      case _ => throw EmbeddingException(s"Illegal annottee for @embed")
    }
    
    var mangledCount = 0
    def mangledName(name: TermName) = TermName(s"_${mangledCount}_") oh_and (mangledCount += 1)
    
    val overloadingOrder = mutable.Map[TermName,Int]()
    
    val scal = q"_root_.scala"
    val pred = q"$scal.Predef"
    val scp = q"_root_.scp"
    val sru = q"_root_.scala.reflect.runtime.universe"
    
    val (objDefs, objMirrorDefs, objRefs) = objDef.impl.body collect {
      case DefDef(mods, name, tparams, vparamss, tpt, rhs) if name != termNames.CONSTRUCTOR =>
        var ind = overloadingOrder.getOrElseUpdate(name, -1) + 1
        overloadingOrder(name) = ind
        val body = vparamss.foldRight(rhs){case (ps, acc) => q"(..$ps) => $acc" }
        val mName = mangledName(name)
        val ref = q"Object.__Defs__.$mName"
        val typ = vparamss.foldRight(tpt){ (ps, acc) => tq"(..${ps map (_.tpt)}) => $acc" } If (tpt.nonEmpty)
        val irBody = q"__b__.Quasicodes.ir[..${typ toList}]{$body}"
        val uniqueName = if (ind == 0) name else TermName(s"${name}_$ind")
        val symbol = q"Object.__typ__.decls.filter(d => d.name.toString == ${name.toString} && d.isMethod).iterator.drop($ind).next.asMethod"
        if (tparams isEmpty) (
          q"val $mName = $scp.utils.Lazy($irBody)",
          q"def $uniqueName = $ref",
          Left(q"$symbol -> $ref")
        ) else {
          val implicits  = tparams map (tp => q"val ${tp.name.toTermName}: __b__.IRType[${tp.name}]")
          (
            q"""def $mName[..$tparams](implicit ..$implicits) = $irBody""",
            q"def $uniqueName[..$tparams](implicit ..$implicits) = $ref(..${implicits map (_.name)})",
            Right(q"$symbol -> ((tps: Seq[__b__.TypeRep]) => $ref(..${tparams.indices map (i => q"__b__.`internal IRType`(tps($i))")}))")
          )
        }
    } unzip3;
    
    val defs -> paramDefs = objRefs.mapSplit(identity)
    
    val newModuleBody = q"""
    def embedIn(base: $scp.lang2.Base) = EmbeddedIn[base.type](base)""" :: q"""
    case class EmbeddedIn[B <: $scp.lang2.Base](override val base: B) extends $scp.ir2.EmbeddedClass[B](base) {
      import base.Predef.implicitType
      val __b__ : base.type = base
      object Class { object Defs }
      object Object {
        val __typ__ = $sru.typeOf[${objDef.name}.type]
        object __Defs__ {..$objDefs}
        object Defs { ..$objMirrorDefs }
      }
      lazy val defs = $pred.Map[$sru.MethodSymbol, $scp.utils.Lazy[base.SomeIR]](..$defs)
      lazy val parametrizedDefs = $pred.Map[$sru.MethodSymbol, $scal.Seq[__b__.TypeRep] => base.SomeIR](..$paramDefs)
    }""" :: Nil
    
    val newObjDef = ModuleDef(objDef.mods, objDef.name, Template(objDef.impl.parents :+ tq"scp.ir2.EmbeddedableClass", objDef.impl.self, objDef.impl.body ++ newModuleBody))
    
    val gen = q"${clsDefOpt getOrElse q""}; $newObjDef"
    
    println("Generated: "+showCode(gen))
    
    gen
  }
  
}


