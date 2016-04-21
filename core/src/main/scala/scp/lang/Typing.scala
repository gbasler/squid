package scp.lang

import reflect.api
import reflect.api.Universe
import annotation.unchecked.uncheckedVariance
import scala.language.higherKinds
import scala.reflect.runtime.universe.{Type => ScalaType, _}
import scala.reflect.runtime.{universe => sru}
import scala.collection.mutable
import scp.utils.MacroUtils._

//trait Typing { base: Base =>
//  
//  def typEq(a: TypeRep[_], b: TypeRep[_]): Boolean
//  
//  implicit def funType[A: TypeEv, B: TypeEv]: TypeEv[A => B]
//  
//}

object InternalAccessor {
  val iuniverse = scala.reflect.runtime.universe.asInstanceOf[scala.reflect.runtime.JavaUniverse]
  val imirror = scala.reflect.runtime.currentMirror.asInstanceOf[iuniverse.JavaMirror]
}
import InternalAccessor._

/**
  * TODO put TypeRep in companion object
  * 
  * 
  * Problem with dynamic Symbol loading:
  *   We don't always see all the symbols/packages
  *   `rootMirror.RootClass` has all base packages in IntelliJ, but NOT in sbt (where most are missing)
  *   We can try to 'add base packages' manually,
  *     but then again we may not see some symbols (the classes defined in Matching, used in Matching's QQs...)
  *       eg `Could not find type Int in package scala`
  *     and it seems very underterministic
  * Problem was solved by using a different API: internal's loadPackage 
  * 
  */
trait ScalaTyping extends Base {
  import ScalaTyping._
  
  //type TypeExt = TypeRep[_]
  
  type DSLSymbol = MethodSymbol
  
  private[this] val symbolCache = mutable.HashMap.empty[(String,String), DSLSymbol]
  private[this] val overloadedSymbolCache = mutable.HashMap.empty[(String,String,Int), DSLSymbol]
  
  
  final def loadSymbol(mod: Boolean, typ: String, symName: String): DSLSymbol = {
    symbolCache getOrElseUpdate ((typ, symName), {
      val tp = loadTypeSymbol(typ)
      (if (mod) tp.companion else tp).typeSignature.member(TermName(symName)).asMethod
    }) // TODO BE
  }
  /** Note: Assumes the runtime library has the same version of a class as the compiler;
    *   overloaded definitions are identified by their index (the order in which they appear) */
  final def loadOverloadedSymbol(mod: Boolean, typ: String, symName: String, index: Int): DSLSymbol = {
    overloadedSymbolCache getOrElseUpdate ((typ, symName, index), {
      //loadType(typ).typeSignature.members.find(s => s.name.toString == symName && ru.showRaw(s.info.erasure) == erasure).get.asMethod
      val tp = loadTypeSymbol(typ)
      (if (mod) tp.companion else tp).typeSignature.member(TermName(symName)).alternatives(index).asMethod
    }) // TODO BE
  }
  
  private[scp] def mkPath(fullName: String): Seq[String] = fullName.splitSane('.').view
  
  private[scp] def loadPackage(fullName: String): TermSymbol = {
    import iuniverse._
    imirror.getPackageIfDefined(fullName) match {
      case NoSymbol => throw new Exception(s"Could not find package $fullName")
      case pckg => pckg.asTerm.asInstanceOf[sru.TermSymbol]
    }
  }
  
  private[scp] def loadTypeSymbol(fullName: String): TypeSymbol = {
    val dotIndex = fullName.lastIndexOf('.')
    //val ir = internal.reificationSupport
    //ir.selectType(loadPackage(fullName take dotIndex), fullName drop (dotIndex+1)) // if dotIndex == -1, we drop 0
    val pack = loadPackage(fullName take dotIndex)
    val tpName = TypeName(fullName drop (dotIndex+1)) // if dotIndex == -1, we drop 0
    pack.typeSignature.member(tpName) match {
      case NoSymbol => throw new Exception(s"Could not find type ${tpName} in module $pack")
      case tp => tp.asType
    }
  }
  
  
  sealed trait TypeRep {
    val typ: ScalaType
    
    def =:= (that: TypeRep) = typ =:= that.typ
    def <:< (that: TypeRep) = typ <:< that.typ
    
    def extract(tp: TypeRep): Option[Extract] = {
      debug("Match "+tp+" with "+this)
      (this, tp) match {
        case (_, TypeHoleRep(_)) => throw new IllegalArgumentException // TODO BE
        case (TypeHoleRep(holeName), tp) => Some(Map() -> Map(holeName -> tp))
        //case (ScalaTypeRep(tag0, targs0 @ _*), ScalaTypeRep(tag1, targs1 @ _*)) =>
        case (tr0: ScalaTypeRep, tr1: ScalaTypeRep) =>
          extractTp(tp.typ)
      }
    }
    
    protected[ScalaTyping] def extractTp(xtyp: ScalaType): Option[Extract]
    
    override def toString = s"$typ"
  }
  
  trait ScalaTypeRep extends TypeRep {
    
    val typ: ScalaType
    def targs: Seq[TypeRep]
    
    protected[ScalaTyping] def extractTp(xtyp: ScalaType): Option[Extract] = {
      
      //if (!(xtyp <:< typ)) return None // Incorrect: subtyping may not hold because types with holes contain NoType's
      
      typ match {
        case RefinedType(typs, _) =>
          
          print(s"<< ${typs map (t => xtyp.baseType(t.typeSymbol))} >> ")
          
          ??? // TODO
          
        case _ =>
          val base = xtyp.baseType(typ.typeSymbol)
          
          if (base == NoType) {
            debug(s"$xtyp not an instance of ${typ.typeSymbol}")
            return None
          }
          
          debug(targs, base.typeArgs)
          
          //val extr = (targs zip base.typeArgs) map { case (a,b) => a.rep.extractTp(b) getOrElse (return None) }
          val extr = (targs zip base.typeArgs) map { case (a,b) => a.extractTp(b) getOrElse (return None) }
          
          Some(extr.foldLeft[Extract](Map() -> Map()){case(acc,a) => merge(acc,a) getOrElse (return None)})
      }
      
    }
    
    
    override def equals(that: Any) = that match {
      case str: ScalaTypeRep => typ =:= str.typ
      case _ => false
    }
    override def hashCode = typ.##
    
  }
  case class SimpleTypeRep(typ: ScalaType) extends ScalaTypeRep {
    def targs = typ.typeArgs map SimpleTypeRep
  }
  //case class ScalaTypeRep(fullName: String, targs: TypeEv[_]*) extends ScalaTypeRep {
  case class DynamicTypeRep(fullName: String, targs: TypeRep*) extends ScalaTypeRep {
    
    lazy val typ = {
      // TODO cache this operation:
      val tsym = loadTypeSymbol(fullName)
      //internal.typeRef(internal.thisType(tsym.owner), tsym, targs map (_.rep.typ) toList)
      internal.typeRef(internal.thisType(tsym.owner), tsym, targs map (_.typ) toList)
    }
    
  }
  
  //case class TypeHoleRep[A](name: String)(implicit val tag: TypeTag[A]) extends TypeRep[A] {
  //  protected[ScalaTyping] def extractTp(xtyp: ScalaType): Option[Extract] = {
  //    val tag = ScalaTyping.mkTag[A](xtyp)
  //    val rep = ScalaTypeRep(tag) // It is safe not to fill-in the targs because the targs are only used in extractors
  //    debug("Extr "+rep)
  //    Some(Map() -> Map(name -> rep))
  //  }
  //  
  //  override def equals(that: Any) = that match {
  //    case TypeHoleRep(name1) => name == name1
  //    case _ => false
  //  }
  //  override def hashCode = name.##
  //  
  //}
  case class TypeHoleRep[A](name: String)(implicit val tag: TypeTag[A]) extends TypeRep {
    val typ = tag.tpe
    protected[ScalaTyping] def extractTp(xtyp: ScalaType): Option[Extract] = {
      //val rep = ScalaTypeRep(xtyp.typeSymbol.fullName) // It is safe not to fill-in the targs because the targs are only used in extractors
      val rep = SimpleTypeRep(xtyp) // FIXME is it safe to get the type args from 'xtyp.typ' ?? (what about holes etc.?)
      debug("Extr "+rep)
      Some(Map() -> Map(name -> rep))
    }
    override def equals(that: Any) = that match {
      case TypeHoleRep(name1) => name == name1
      case _ => false
    }
    override def hashCode = name.##
  }
  
  
  def typEq(a: TypeRep, b: TypeRep): Boolean = a =:= b
  
  //def extractType(typ: TypeRep[_], xtor: TypeExt): Option[Extract] = xtor.extract(typ)
  //
  ////type Tag[A] = TypeTag[A]
  ////def typeHole[A: TypeTag](name: String): TypeRep[A] = TypeHoleRep[A](name)
  //def typeHole[A](name: String): TypeRep[A] = TypeHoleRep[A](name)(ScalaTyping.NoTypeTag[A])
  def typeHole[A](name: String): TypeRep = TypeHoleRep[A](name)(ScalaTyping.NoTypeTag[A])
  
  
  //implicit def funType[A: TypeEv, B: TypeEv]: TypeEv[A => B] = {
  //  implicit val A = typeEv[A].rep.tagAs[A]
  //  implicit val B = typeEv[B].rep.tagAs[B]
  //  TypeEv(ScalaTypeRep(typeTag[A => B], typeEv[A], typeEv[B]))
  //}
  
  
  //implicit def typeAll[A: TypeTag]: TypeEv[A] = TypeEv(ScalaTypeRep(typeTag[A]))
  
  import scala.language.experimental.macros
  implicit def typeEvImplicit[A]: TypeEv[A] = macro ScalaTypingMacros.typeEvImplicitImpl[A]
  
  
  /*
  import scala.language.experimental.macros
  //implicit def scope[A: TypeTag]: Scope[A] = null // TODO
  implicit def scope[A]: Scope[A] = macro ScalaTyping.scopeImpl[A]
  */
  
}
object ScalaTyping {
  
  //def debug(x: Any) = println(x)
  def debug(x: Any) = ()
  
  protected[ScalaTyping] def mkTag[A](tp: ScalaType) =
    TypeTag.apply[A](scala.reflect.runtime.currentMirror, new scala.reflect.api.TypeCreator {
    //TypeTag.apply[A](mirror, new scala.reflect.api.TypeCreator {
      def apply[U <: Universe with Singleton](m: api.Mirror[U]): U#Type = tp.asInstanceOf[U#Type]
    })
  private val NoTypeTagVal = ScalaTyping.mkTag(NoType)
  protected[ScalaTyping] def NoTypeTag[A]: TypeTag[A] = NoTypeTagVal.asInstanceOf[TypeTag[A]]
  
  //implicit def typTrans[B1 <: ScalaTyping, B2 <: ScalaTyping]: (B1#TypeRep[Any] ~> B2#TypeRep[Nothing]) = new (B1#TypeRep[Any] ~> B2#TypeRep[Nothing]) {
  //  def apply(v1: B1#TypeRep[Any]) = v1.asInstanceOf[B2#TypeRep[Nothing]]
  //}
  
  /*
  import reflect.macros.whitebox.Context
  def scopeImpl[A: c.WeakTypeTag](c: Context) = {
    import c.universe._
    //q""
    q"null" // TODO
  }
  */
  
  
  /*
  //import reflect.macros.whitebox.Context
  import reflect.macros.blackbox.Context
  def typeEvImplicitImpl[A: c.WeakTypeTag](c: Context) = {
    import c.universe._
    //q""
    
    //println(weakTypeOf[A])
    //println(c.macroApplication)
    
    val base = c.macroApplication match {
      case q"$b.typeEvImplicit[$_]" => b
    }
    
    val A = weakTypeOf[A]
    //println("TYP "+A)
    
    A match {
      case t if t <:< typeOf[Base.HoleType] =>
        debug("HOLE "+A.typeSymbol.name.toString)
        q"$base.TypeEv($base.typeHole[$A](${A.typeSymbol.name.toString}))"
      case TypeRef(_, sym, args) if args.nonEmpty => //q"TypeEv($base.ScalaTypeRep[$A](null))" // TODO
        
        val (evs, tags) = args.zipWithIndex.map { case (ta,i) =>
          val ename = TermName("_e_"+i)
          // : TypeTag[$ta] 
          q"val ${ename} = $base.typeEv[$ta]" -> (ta -> q"implicit val ${TermName("_t_"+i)} = $ename.rep.tagAs[$ta]")
        } unzip;
        val tagImpls = tags.toMap.values
        //  ..${ args map { ta => q"val " } }
        val res = q"""
          import scala.reflect.runtime.universe._
          ..$evs
          ..$tagImpls
          $base.TypeEv($base.ScalaTypeRep[$A](typeTag[$A], ..${evs map (_.name)}))
        """
        debug("Generated: "+res)
        res
        
      case _ =>
        debug("TYP?? "+A)
        c.inferImplicitValue(c.typecheck(tq"scala.reflect.runtime.universe.TypeTag[$A]", c.TYPEmode).tpe) match {
        //c.inferImplicitValue(c.typecheck(tq"scala.reflect.runtime.universe.TypeTag[$A]", c.TYPEmode).tpe, silent = false) match {
          case q"" =>
            debug("TYP "+A+" NOPE")
            //null // failure
            c.abort(c.enclosingPosition, "No type tag: "+A)
          case tag =>
            debug("TYP "+A+" TAG")
            q"$base.TypeEv($base.ScalaTypeRep($tag))"
        }
        //null // failure
    }
    
    
    //q"null" // TODO
    //null
  }
  */
}

import reflect.macros.blackbox
class ScalaTypingMacros(val c: blackbox.Context) {
  import ScalaTyping.debug
  
  type Ctx = c.type
  val Ctx: c.type = c
  import Ctx.universe._
  
  // TODO an API to aggregate all needed typereps, without repetition (for QQ code-gen)
  def typeRep(base: Tree, tp: Type): Tree = {
    
    tp.widen match {
      case t if t <:< typeOf[Base.HoleType] =>
        //debug("HOLE "+A.typeSymbol.name.toString)
        q"$base.typeHole[$tp](${tp.typeSymbol.name.toString})"
      case TypeRef(_, sym, args) =>
//        if (!sym.asType.isClass) c.abort(c.enclosingPosition, s"Unknown type! $sym")
//        q"$base.ScalaTypeRep(${sym.fullName.toString}, ..${args map (t => q"TypeEv(${typeRep(base,t)})")})"
        q"$base.DynamicTypeRep(${sym.fullName.toString}, ..${args map (t => typeRep(base,t))})"
      //case TypeRef(_, sym, args) =>
      //  ???
      case _ => c.abort(c.enclosingPosition, s"Cannot generate a type evidence for: $tp")
    }
    
  }
  
  def typeEvImplicitImpl[A: c.WeakTypeTag]: Tree = {
    
    val base = c.macroApplication match {
      case q"$b.typeEvImplicit[$_]" => b
    }
    val A = weakTypeOf[A]
    
    val ev = q"TypeEv(${typeRep(base, A)})"
    
    //debug(s"Evidence for $A: $ev")
    
    ev
  }
  
  
  
}

















