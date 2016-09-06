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

/** TODO generate oublic accessor for private fields, so we can lower them...
  * TODO don't generate vararg lambdas... which aren't valid Scala */
object ClassEmbedding {
  
  /** TODO catch excepts and properly abort */
  def embedImpl(c: whitebox.Context)(annottees: c.Tree*) = {
    import c.universe._
    
    object Helpers extends {val uni: c.universe.type = c.universe} with meta.UniverseHelpers[c.universe.type]
    import Helpers._
    
    val (clsDefOpt: Option[ClassDef], objDef: ModuleDef) = annottees match {
      case (cls: ClassDef) :: (obj: ModuleDef) :: Nil => Some(cls) -> obj
      case (obj: ModuleDef) :: (cls: ClassDef) :: Nil => Some(cls) -> obj // Never actually happens -- if the object is annotated, the class doesn't get passed!
      case (cls: ClassDef) :: Nil => Some(cls) -> q"object ${cls.name.toTermName}"
      case (obj: ModuleDef) :: Nil =>
        Some(ClassDef(Modifiers(), obj.name.toTypeName, Nil, Template(Nil, ValDef(Modifiers(), termNames.WILDCARD, EmptyTree, EmptyTree), Nil))) -> obj
      case _ => throw EmbeddingException(s"Illegal annottee for @embed")
    }
    
    val objName = objDef.name
    val clsName = objName.toTypeName
    
    var mangledCount = 0
    def mangledName(name: TermName) = TermName(s"_${mangledCount}_") oh_and (mangledCount += 1)
    
    val overloadingOrder = mutable.Map[(TermName,Boolean),Int]()
    
    val scal = q"_root_.scala"
    val pred = q"$scal.Predef"
    val scp = q"_root_.scp"
    val sru = q"_root_.scala.reflect.runtime.universe"
    
    val allDefs = (objDef.impl.body map (false -> _)) ++ (clsDefOpt.toList flatMap (_.impl.body map (true -> _)))
    
    val (objDefs, objMirrorDefs, objRefs) = allDefs collect {
      case inClass -> ValOrDefDef(mods, name, tparams, vparamss, tpt, rhs) if name != termNames.CONSTRUCTOR && rhs.nonEmpty => // TODO sthg about parameter accessors?
        val ind = {
          val key = name -> inClass
          overloadingOrder.getOrElseUpdate(key, -1) + 1  and (overloadingOrder(key) = _)
        }
        
        val cleanRhs = if (!inClass) rhs else q"import __self._; ${
          rhs transform {
            case x @ This(tp) =>
              assert(tp == typeNames.EMPTY || tp == clsName, s"Unexpected `this` reference: ${showCode(x)}") // TODO B/E
              q"__self"
          }
        }"
        
        var fullVparams = vparamss
        if (inClass) fullVparams = (q"val __self : ${objName.toTypeName}"::Nil) :: vparamss
        
        val body = fullVparams.foldRight(cleanRhs){case (ps, acc) => q"(..$ps) => $acc" }
        
        val mName = mangledName(name)
        val parent = if (inClass) q"Class" else q"Object"
        val ref = q"$parent.__Defs__.$mName"
        
        // This is necessary to only make mirror defs which names won't clash with methods already defined:
        val AnyRefNames = typeOf[AnyRef].members.iterator.collect{case s if s.name.isTermName => s.name.toTermName}.toSet
        val uniqueName = if (ind == 0 && !(AnyRefNames contains name)) name else TermName(s"${name}_$ind")
        
        val typ = (fullVparams foldRight tpt){ (ps, acc) => tq"(..${ps map (_.tpt)}) => $acc" }  If  tpt.nonEmpty
        val irBody = q"__b__.Quasicodes.ir[..${typ toList}]{$body}"
        val symbol = q"$parent.__typ__.decls.filter(d => d.name.toString == ${name.toString} && d.isMethod).iterator.drop($ind).next.asMethod"
        
        if (tparams isEmpty) (
          inClass -> q"val $mName = $scp.utils.Lazy($irBody)",
          inClass -> q"def $uniqueName = $ref",
          Left(q"$symbol -> $ref")
        ) else { val implicits  = tparams map (tp => q"val ${tp.name.toTermName}: __b__.IRType[${tp.name}]"); (
          inClass -> q"""def $mName[..$tparams](implicit ..$implicits) = $irBody""",
          inClass -> q"def $uniqueName[..$tparams](implicit ..$implicits) = $ref(..${implicits map (_.name)})",
          Right(q"$symbol -> ((tps: Seq[__b__.TypeRep]) => $ref(..${tparams.indices map (i => q"__b__.`internal IRType`(tps($i))")}))")
        )}
    } unzip3;
    
    val defs -> paramDefs = objRefs.mapSplit(identity)
    
    val classDefs -> moduleDefs = objDefs.mapSplit{case true->d => Left(d) case false->d => Right(d)}
    val classMirrorDefs -> moduleMirrorDefs = objMirrorDefs.mapSplit{case true->d => Left(d) case false->d => Right(d)}
    
    val newModuleBody = q"""
    def embedIn(base: $scp.lang2.Base) = EmbeddedIn[base.type](base)""" :: q"""
    case class EmbeddedIn[B <: $scp.lang2.Base](override val base: B) extends $scp.ir2.EmbeddedClass[B](base) {
      import base.Predef.implicitType
      val __b__ : base.type = base
      object Class {
        ..${if (clsDefOpt isDefined) q"val __typ__ = $sru.typeOf[${objName.toTypeName}]"::Nil else Nil}
        object __Defs__ {..$classDefs}
        object Defs { ..$classMirrorDefs }
      }
      object Object {
        val __typ__ = $sru.typeOf[${objName}.type]
        object __Defs__ {..$moduleDefs}
        object Defs { ..$moduleMirrorDefs }
      }
      lazy val defs = $pred.Map[$sru.MethodSymbol, $scp.utils.Lazy[base.SomeIR]](..$defs)
      lazy val parametrizedDefs = $pred.Map[$sru.MethodSymbol, $scal.Seq[__b__.TypeRep] => base.SomeIR](..$paramDefs)
    }""" :: Nil
    
    val newObjDef = ModuleDef(objDef.mods, objDef.name, Template(objDef.impl.parents :+ tq"scp.ir2.EmbeddedableClass", objDef.impl.self, objDef.impl.body ++ newModuleBody))
    
    val gen = q"${clsDefOpt getOrElse q""}; $newObjDef"
    
    //println("Generated: "+showCode(gen))
    
    gen
  }
  
}


