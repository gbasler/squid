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

@compileTimeOnly("Enable macro paradise to expand macro annotations.")
class embed extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ClassEmbedding.embedImpl
}
@compileTimeOnly("Enable macro paradise to expand macro annotations.")
class dbg_embed extends StaticAnnotation {
  @MacroSetting(debug = true) def macroTransform(annottees: Any*): Any = macro ClassEmbedding.embedImpl
}

/** Encode a method with default arguments into a set of overloaded methods without default arguments */
@compileTimeOnly("Enable macro paradise to expand macro annotations.")
class overloadEncoding extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ClassEmbedding.overloadEncoding
}

class phase(name: Symbol) extends StaticAnnotation

import scala.reflect.macros.whitebox
import scala.reflect.macros.blackbox

/** TODO generate public accessor for private fields, so we can lower them...
  * TODO don't generate vararg lambdas... which aren't valid Scala
  * TODO have a @reflects[] annot to provide bodies for existing classes
  * TODO also facilities to convert object to a tuple of its fields while inlining methods -- if possible...
  *   would be nice if the IR did some flow tracking to also specialize other parametrized things accordingle (like containers and functions) if they are known to only use objects that can be converted
  * TODO use a fix combinator for recursive functions that are not polymorphically recursive */
class ClassEmbedding(val c: whitebox.Context) {
  import c.universe._
  
  val debug = { val mc = MacroDebugger(c); mc[MacroDebug] }
  
  object Helpers extends {val uni: c.universe.type = c.universe} with meta.UniverseHelpers[c.universe.type]
  import Helpers._
  
  lazy val SUGAR_PHASE = q"new _root_.squid.quasi.phase('Sugar)"
  
  
  def rmDefault(vd: ValDef): ValDef = 
    if (vd.rhs.isEmpty) vd else
    ValDef(Modifiers(FlagUtils.rmFlagsIn(vd.mods.flags, Flag.DEFAULTPARAM), 
      vd.mods.privateWithin, vd.mods.annotations),vd.name,vd.tpt,EmptyTree)
  
  
  def overloadEncoding(annottees: c.Tree*) = q"..${overloadEncodingImpl(annottees: _*)}"
  
  def overloadEncodingImpl(annottees: c.Tree*) = try {
    annottees match {
      case Seq(DefDef(mods, name, tparams, vparamss, tpt, rhs))
      if vparamss exists (_ exists (_.rhs nonEmpty)) =>
        
        type Params = List[ValDef]
        type Paramss = List[Params]
        
        def rec(pss: Paramss): List[Paramss -> Tree] = pss match {
          case ps :: pss =>
            def rec2(ps: Params,rhs2:Tree): List[Params -> Tree] = ps match {
              case vd :: tl => 
                for {
                  (ps2,rh2) <- rec2(tl,rhs2)
                  given = (rmDefault(vd) :: ps2) -> rh2 :: Nil
                  (ps3,rh3) <- (if (vd.rhs.isEmpty) given else ps2 -> q"$vd; ..$rh2" :: given)
                } yield (ps3,rh3)
              case Nil => Nil -> rhs2 :: Nil
            }
            for {
              (pss2,rhs2) <- rec(pss)
              (ps,rhs3) <- rec2(ps,rhs2)
            } yield (ps::pss2,rhs3)
          //case Nil => Nil -> rhs :: Nil
          case Nil => Nil -> EmptyTree :: Nil
        }
        
        rec(vparamss) map {
          case (vparamss2,rhs2) if vparamss2.map(_.size).sum == vparamss.map(_.size).sum =>
            DefDef(mods, name, tparams, vparamss2, tpt, rhs)
          case (vparamss2,rhs2) =>
            //DefDef(mods, name, tparams, vparamss2, tpt, rhs2)
            val mods2 = Modifiers(mods.flags, mods.privateWithin, SUGAR_PHASE :: mods.annotations)
            DefDef(mods2, name, tparams, vparamss2, tpt, q"..$rhs2; $name[..$tparams](...${vparamss map (_ map (_ name))})")
        } //and (ds => println("DS:\n"+ds.map(showCode(_)).mkString("\n")))
        
      case x => throw EmbeddingException(s"Illegal annottee for @overloadEncoding")
        
    }
  } catch {
    case EmbeddingException(msg) => c.abort(c.enclosingPosition, s"Embedding error: $msg")
  }
  
  
  /** TODO catch excepts and properly abort */
  def embedImpl(annottees: c.Tree*) = try {
    
    def rmDefaults(cls:ClassDef): ClassDef = ClassDef(cls.mods, cls.name, cls.tparams, 
      Template(cls.impl.parents, cls.impl.self, cls.impl.body.flatMap {
        case d @DefDef(mods, name, tparams, vparamss, tpt, rhs)
        if vparamss.exists(_.exists(_.rhs.nonEmpty)) =>
          val filan = mods.annotations.filter {
            case q"new overloadEncoding()" => false
            case _ => true
          }
          if (filan.size == mods.annotations.size) {
            c.warning(vparamss.flatMap(_.find(_.rhs.nonEmpty)).head.pos, 
              "Default arguments in @embed methods can cause problems because they generate public fields not accessible manually. " +
                "Use @overloadEncoding to turn a method with default arguments into a set of overloaded methods.")
            d :: Nil
          } else {
            overloadEncodingImpl(DefDef(Modifiers(mods.flags, mods.privateWithin, filan), name, tparams, vparamss, tpt, rhs))
          }
        case x => x :: Nil
      })
    )
    
    val (clsDefOpt0: Option[ClassDef], objDef: ModuleDef) = annottees match {
      case (cls: ClassDef) :: (obj: ModuleDef) :: Nil => Some(cls) -> obj
      case (obj: ModuleDef) :: (cls: ClassDef) :: Nil => Some(cls) -> obj // Never actually happens -- if the object is annotated, the class doesn't get passed!
      case (cls: ClassDef) :: Nil => Some(cls) -> q"object ${cls.name.toTermName}"
      case (obj: ModuleDef) :: Nil => None -> obj
      case _ => throw EmbeddingException(s"Illegal annottee for @embed")
    }
    val clsDefOpt = clsDefOpt0 map (rmDefaults(_))
    val clsDef = clsDefOpt Else q"class ${objDef.name.toTypeName}"
    
    val objName = objDef.name
    val clsName = objName.toTypeName
    
    var mangledCount = 0
    def mangledName(name: TermName) = TermName(s"_${mangledCount}_") alsoDo (mangledCount += 1)
    
    val overloadingOrder = mutable.Map[(TermName,Boolean),Int]()
    
    val scal = q"_root_.scala"
    val pred = q"$scal.Predef"
    val squid = q"_root_.squid"
    val sru = q"_root_.scala.reflect.runtime.universe"
    
    val allDefs = (objDef.impl.body map (false -> _)) ++ (clsDefOpt.toList flatMap (_.impl.body map (true -> _)))
    
    def mkImplicits(tps: List[TypeDef], build: TypeName => Tree = tn => tq"__b__.IRType[$tn]") =
      tps map (tp => q"val ${tp.name.toTermName}: ${tp.name|>build}")
    
    def stripVariance(tp: TypeDef) = TypeDef(Modifiers(), tp.name, tp.tparams, tp.rhs) // TODO keep other flags
    val clsDefTparams = clsDef.tparams map (stripVariance(_))
    
    // Makes the parameters acceptable for being lambda parameters: removes the `rhs`
    def fixValDef(vd: ValDef) = ValDef(vd.mods, vd.name, vd.tpt, EmptyTree) // TODO handle by-name and repeated
    
    val (objDefs, objMirrorDefs, objRefs) = allDefs collect {
      case inClass -> ValOrDefDef(mods, name, tparams, vparamss, tpt, rhs) if name != termNames.CONSTRUCTOR && rhs.nonEmpty => // TODO sthg about parameter accessors?
        val ind = {
          val key = name -> inClass
          overloadingOrder.getOrElseUpdate(key, -1) + 1  alsoApply (overloadingOrder(key) = _)
        }
        
        val cleanRhs = if (!inClass) rhs else q"import __self._; ${
          rhs transform {
            case x @ This(tp) =>
              assert(tp == typeNames.EMPTY || tp == clsName, s"Unexpected `this` reference: ${showCode(x)}") // TODO B/E
              q"__self"
          }
        }"
        
        var fullVparams = vparamss
        if (inClass) fullVparams = (q"val __self : ${clsDef.name}[..${clsDef.tparams map (tp => tq"${tp.name}")}]"::Nil) :: vparamss
        
        val body = (fullVparams map {_ map (fixValDef(_))}).foldRight(cleanRhs){case (ps, acc) => q"(..$ps) => $acc" }
        
        val mName = mangledName(name)
        val parent = if (inClass) q"Class" else q"Object"
        val ref = q"$parent.__Defs__.$mName"
        
        // This is necessary to only make mirror defs which names won't clash with methods already defined:
        val AnyRefNames = typeOf[AnyRef].members.iterator.collect{case s if s.name.isTermName => s.name.toTermName}.toSet
        val uniqueName = if (ind == 0 && !(AnyRefNames contains name)) name else TermName(s"${name}_$ind")
        
        val typ = (fullVparams foldRight tpt){ (ps, acc) => tq"(..${ps map (_.tpt)}) => $acc" }  optionIf  tpt.nonEmpty
        val irBody = q"__b__.Quasicodes.ir[..${typ toList}]{$body}"
        val symbol = q"$parent.__typ__[..${
          (clsDefOpt retainIf inClass).toList flatMap (_.tparams map (_ => tq"Any"))
        }].decls.filter(d => d.name.toString == ${name.toString} && d.isMethod).iterator.drop($ind).next.asMethod"
        
        var fullTparams = tparams
        if (inClass) fullTparams = clsDefTparams ++ fullTparams
        
        if (fullTparams isEmpty) (
          inClass -> q"val $mName = $squid.utils.Lazy($irBody)",
          inClass -> q"def $uniqueName = $ref",
          Left(q"$symbol -> $ref")
        ) else { val implicits = mkImplicits(fullTparams); (
          inClass -> q"""def $mName[..$fullTparams](implicit ..$implicits) = $irBody""",
          inClass -> q"def $uniqueName[..$fullTparams](implicit ..$implicits) = $ref(..${implicits map (_.name)})",
          Right(q"$symbol -> ((tps: Seq[__b__.TypeRep]) => $ref(..${fullTparams.indices map (i => q"__b__.`internal IRType`(tps($i))")}))")
        )}
    } unzip3;
    
    val defs -> paramDefs = objRefs.mapSplit(identity)
    
    val classDefs -> moduleDefs = objDefs.mapSplit{case true->d => Left(d) case false->d => Right(d)}
    val classMirrorDefs -> moduleMirrorDefs = objMirrorDefs.mapSplit{case true->d => Left(d) case false->d => Right(d)}
    
    val newModuleBody = q"""
    def embedIn(base: $squid.lang.Base) = EmbeddedIn[base.type](base)""" :: q"""
    case class EmbeddedIn[B <: $squid.lang.Base](override val base: B) extends $squid.ir.EmbeddedClass[B](base) {
      import base.Predef.implicitType
      val __b__ : base.type = base
      object Class {
        ..${ if (clsDefOpt isEmpty) Nil else {
          if (clsDef.tparams isEmpty) q"val __typ__ = $sru.typeOf[${objName.toTypeName}]"::Nil
          else q"def __typ__[..${clsDefTparams}](implicit ..${
            mkImplicits(clsDefTparams,tn=>tq"_root_.scala.reflect.runtime.universe.TypeTag[$tn]")
          }) = $sru.typeOf[${objName.toTypeName}[..${clsDef.tparams map (tp => tq"${tp.name}")}]]"::Nil }}
        object __Defs__ {..$classDefs}
        object Defs { ..$classMirrorDefs }
      }
      object Object {
        val __typ__ = $sru.typeOf[${objName}.type]
        object __Defs__ {..$moduleDefs}
        object Defs { ..$moduleMirrorDefs }
      }
      lazy val defs = $pred.Map[$sru.MethodSymbol, $squid.utils.Lazy[base.SomeIR]](..$defs)
      lazy val parametrizedDefs = $pred.Map[$sru.MethodSymbol, $scal.Seq[__b__.TypeRep] => base.SomeIR](..$paramDefs)
    }""" :: Nil
    
    val newObjDef = ModuleDef(objDef.mods, objDef.name, Template(objDef.impl.parents :+ tq"squid.ir.EmbeddedableClass", objDef.impl.self, objDef.impl.body ++ newModuleBody))
    
    val gen = q"${clsDefOpt getOrElse q""}; $newObjDef"
    
    //debug("Generated: "+(gen))
    debug("Generated: "+showCode(gen))
    
    gen
    
  } catch {
    case EmbeddingException(msg) => c.abort(c.enclosingPosition, s"Embedding error: $msg")
  }
  
  
}


