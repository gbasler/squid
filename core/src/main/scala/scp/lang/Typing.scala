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

object ScalaTyping {
  
  private var debugEnabled = false
  private[ScalaTyping] def debug(x: => Any) = if (debugEnabled) println(x)
  def debugTypes[T](x: => T): T = {
    debugEnabled = true
    try x
    finally debugEnabled = false
  }
  
  val Nothing = typeOf[Nothing]
  val Any = typeOf[Any]
  val AnyRef = typeOf[AnyRef]
  val Function1 = typeOf[Any => Any].typeSymbol
  
  /*
  protected[ScalaTyping] def mkTag[A](tp: ScalaType) =
    TypeTag.apply[A](scala.reflect.runtime.currentMirror, new scala.reflect.api.TypeCreator {
    //TypeTag.apply[A](mirror, new scala.reflect.api.TypeCreator {
      def apply[U <: Universe with Singleton](m: api.Mirror[U]): U#Type = tp.asInstanceOf[U#Type]
    })
  private val NoTypeTagVal = ScalaTyping.mkTag(NoType)
  protected[ScalaTyping] def NoTypeTag[A]: TypeTag[A] = NoTypeTagVal.asInstanceOf[TypeTag[A]]
  */
  
  sealed abstract trait TypeHoleMarker[A]
  
  sealed abstract class Variance(val asInt: Int) {
    def * (that: Variance) = Variance(asInt * that.asInt) //(this, that) match {}
    def symbol = this match {
      case Invariant => "="
      case Covariant => "+"
      case Contravariant => "-"
    }
    override def toString = s"[$symbol]"
  }
  object Variance {
    def apply(asInt: Int) = asInt match {
      case 0 => Invariant
      case 1 => Covariant
      case -1 => Contravariant
    }
    def of (s: TypeSymbol) =
      if (s.isCovariant) Covariant else if (s.isContravariant) Contravariant else Invariant
  }
  case object Invariant extends Variance(0)
  case object Covariant extends Variance(1)
  case object Contravariant extends Variance(-1)
}

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
  import Variance._
  
  //type TypeExt = TypeRep[_]
  
  type DSLSymbol = MethodSymbol
  
  private[this] val symbolCache = mutable.HashMap.empty[(String,String), DSLSymbol]
  private[this] val overloadedSymbolCache = mutable.HashMap.empty[(String,String,Int), DSLSymbol]
  
  private def ensureDefined(name: String, sym: Symbol) = sym match {
    case NoSymbol => throw new Exception(s"Could not find $name")
    case _ => sym
  }
  
  /** For a package object type:
    *   + mod, isModuleClass, isClass
    *   - isPackageClass
    *   Can't access members directly (it has no typeSignature and no companion); have to use tp.owner.typeSignature
    * 
    * TODO factor logic with loadOverloadedSymbol
    */
  final def loadSymbol(mod: Boolean, typ: String, symName: String): DSLSymbol = {
    symbolCache getOrElseUpdate ((typ, symName), {
      val tp = loadTypeSymbol(typ)
      //println(mod, tp.isModuleClass, tp.isClass, tp.isPackageClass)
      val sign = (if (mod) if (tp.isModuleClass) tp.owner else tp.companion else tp).typeSignature
      //debug(s"Loaded $tp, sign: "+sign)
      ensureDefined(s"'$symName' in $typ", sign.member(TermName(symName))).asMethod
    }) // TODO BE
  }
  /** Note: Assumes the runtime library has the same version of a class as the compiler;
    *   overloaded definitions are identified by their index (the order in which they appear) */
  final def loadOverloadedSymbol(mod: Boolean, typ: String, symName: String, index: Int): DSLSymbol = {
    overloadedSymbolCache getOrElseUpdate ((typ, symName, index), {
      //loadType(typ).typeSignature.members.find(s => s.name.toString == symName && ru.showRaw(s.info.erasure) == erasure).get.asMethod
      val tp = loadTypeSymbol(typ)
      ensureDefined(s"'$symName' in $typ", (if (mod) if (tp.isModuleClass) tp.owner else tp.companion else tp).typeSignature.member(TermName(symName))).alternatives(index).asMethod
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
    
    def extract(tp: TypeRep, va: Variance): Option[Extract] = {
      debug("Match "+tp+" with "+this)
      (this, tp) match {
        case (_, TypeHoleRep(_)) => throw new IllegalArgumentException // TODO BE
        case (TypeHoleRep(holeName), tp) => Some(Map(), Map(holeName -> tp), Map())
        //case (ScalaTypeRep(tag0, targs0 @ _*), ScalaTypeRep(tag1, targs1 @ _*)) =>
        case (tr0: ScalaTypeRep, tr1: ScalaTypeRep) =>
          extractTp(tp.typ, va)
      }
    }
    
    protected[ScalaTyping] def extractTp(xtyp: ScalaType, va: Variance): Option[Extract]
    
    //override def toString = s"$typ"
  }
  
  trait ScalaTypeRep extends TypeRep {
    
    val typ: ScalaType
    def targs: Seq[TypeRep]
    
    //protected[ScalaTyping] def extractTp(xtyp: ScalaType, va: Variance = Invariant): Option[Extract] = {
    protected[ScalaTyping] def extractTp(xtyp: ScalaType, va: Variance): Option[Extract] = {
      
      //debug(s"$va Match $xtyp with $typ")
      debug(s"$va Match $xtyp with $this")
      
      //if (!(xtyp <:< typ)) return None // Incorrect: subtyping may not hold because types with holes contain NoType's
      
      typ match {
        case RefinedType(typs, _) =>
          
          print(s"<< ${typs map (t => xtyp.baseType(t.typeSymbol))} >> ")
          
          ??? // TODO

        // TODO put this at the right place!
        case TypeRef(_, sym, arg :: Nil) if sym == symbolOf[TypeHoleMarker[_]] =>
          arg match {
            case ConstantType(Constant(name: String)) =>
              Some(Map(), Map(name -> SimpleTypeRep(xtyp)), Map())
          }
          
        case _ =>
          
          // TODO *va
          
          if (va == Contravariant && xtyp.typeSymbol != typ.typeSymbol) {
            
            //debug(s"[Contra] is $typ an instance of ${xtyp.typeSymbol}?")
            
            val baseTargs = typ.baseType(xtyp.typeSymbol) match {
              case NoType =>
                debug(s"$va $typ is not an instance of ${xtyp.typeSymbol}")
                if (typ <:< xtyp) {
                  debug(s"... but $typ is still somehow a subtype of ${xtyp}")
                  //assert(Any <:< xtyp,
                  //  s"$typ <:< $xtyp but !($xtyp >:> Any) and ${xtyp.typeSymbol} is not a base type of $typ")
                assert(typ <:< Nothing,
                  s"$typ <:< $xtyp but !($typ <:< Nothing) and ${xtyp.typeSymbol} is not a base type of $typ")
                  Stream continually Nothing
                }
                else return None
              case base =>
                //debug(base)
                //debug(s"[Contra] $typ is an instance of ${xtyp.typeSymbol} as '$base'")
                debug(s"$va $typ is an instance of ${xtyp.typeSymbol} as '$base'")
                base.typeArgs.toStream
            }
            
            //debug(s"$va ZIP: $targs | ${baseTargs.toSeq}")
            debug(s"$va Extr Targs(inv case): " +
              //(targs zip baseTargs zip (xtyp.typeSymbol.asType.typeParams map (Variance of _.asType) map (_ * va)) mkString " "))
              (baseTargs zip xtyp.typeArgs zip (xtyp.typeSymbol.asType.typeParams map (Variance of _.asType) map (_ * va)) mkString " "))
            
            assert(!baseTargs.hasDefiniteSize || xtyp.typeArgs.size == baseTargs.size)
            
            //val extr = (targs zip baseTargs zip xtyp.typeSymbol.asType.typeParams) map {
            //  //case ((a,b), p) => a.extractTp(b, (Variance of p.asType) * va) getOrElse (return None) }
            //  case ((a,b), p) => SimpleTypeRep(b).extractTp(a.typ, (Variance of p.asType) * va) getOrElse (return None) }
            val extr = (baseTargs zip xtyp.typeArgs zip xtyp.typeSymbol.asType.typeParams) map {
              case ((a,b), p) => SimpleTypeRep(a).extractTp(b, (Variance of p.asType) * va) getOrElse (return None) }
            
            Some(extr.foldLeft[Extract](Map(), Map(), Map()){case(acc,a) => merge(acc,a) getOrElse (return None)})
            
            //???
            
          //} else {
          //  if (va == Invariant && xtyp.typeSymbol != typ.typeSymbol) {
          } else if (va == Invariant && xtyp.typeSymbol != typ.typeSymbol) {
            debug(s"${xtyp.typeSymbol} and ${typ.typeSymbol} cannot be compared invariantly")
            //return
            None
          } else {
            
            val base = xtyp.baseType(typ.typeSymbol)
            
            val baseTargs = if (base == NoType) {
              debug(s"$xtyp not an instance of ${typ.typeSymbol}")
              
              if (xtyp <:< typ) {
                debug(s"... but $xtyp is still somehow a subtype of ${typ}")
                assert(xtyp <:< Nothing,
                  s"$xtyp <:< $typ but !($xtyp <:< Nothing) and ${typ.typeSymbol} is not a base type of $xtyp")
                
                Stream continually Nothing
              }
              else return None
            }
            else base.typeArgs.toStream
            
            //debug("Extr Targs: " + (targs zip baseTargs mkString " "))
            debug(s"$va Extr Targs: " +
              (targs zip baseTargs zip (typ.typeSymbol.asType.typeParams map (Variance of _.asType) map (_ * va)) mkString " "))
            
            assert(!baseTargs.hasDefiniteSize || targs.size == baseTargs.size)
            
            //val extr = (targs zip base.typeArgs) map { case (a,b) => a.rep.extractTp(b) getOrElse (return None) }
            //val extr = (targs zip baseTargs) map { case (a,b) => a.extractTp(b) getOrElse (return None) }
            val extr = (targs zip baseTargs zip typ.typeSymbol.asType.typeParams) map {
              case ((a,b), p) => a.extractTp(b, (Variance of p.asType) * va) getOrElse (return None) }
            
            Some(extr.foldLeft[Extract](Map(), Map(), Map()){case(acc,a) => merge(acc,a) getOrElse (return None)})
        }
      }
      
    }
    
    //override def toString = s"$typ"
    //override def toString = s"${typ.typeSymbol.name}[]"  
    override def toString = {
      assert(typ.typeArgs.size == targs.size)
      typ.typeSymbol match {
        case Function1 => s"${targs(0)} => ${targs(1)}"
        case s => s.name + (if (typ.typeArgs isEmpty) "" else s"[${targs mkString ","}]")
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
  //case class TypeHoleRep[A](name: String)(implicit val tag: TypeTag[A]) extends TypeRep {
  case class TypeHoleRep[A](name: String) extends TypeRep {
    //val typ = tag.tpe
    //val typ = internal.typeRef(???,???,Nil)
    //lazy val typ = {
    //  val owner = AnyRef.typeSymbol.owner
    //  internal.refinedType(AnyRef :: Nil, owner, internal.newScopeWith(
    //    internal.newTermSymbol(owner, TermName("<hole>"+name))))
    //}
    lazy val typ = {
      val ms = symbolOf[TypeHoleMarker[_]]
      internal.typeRef(internal.thisType(symbolOf[ScalaTyping.type]), ms, internal.constantType(Constant(name)) :: Nil)
    }
    //println(typ)
    
    //protected[ScalaTyping] def extractTp(xtyp: ScalaType, va: Variance = Invariant): Option[Extract] = {
    protected[ScalaTyping] def extractTp(xtyp: ScalaType, va: Variance): Option[Extract] = {
      //println(xtyp, xtyp==NoType)
      //val rep = ScalaTypeRep(xtyp.typeSymbol.fullName) // It is safe not to fill-in the targs because the targs are only used in extractors
      
      val rep = SimpleTypeRep(xtyp) // FIXME is it safe to get the type args from 'xtyp.typ' ?? (what about holes etc.?)
      
      //val rep = xtyp match {
      //  case NoType => 
      //    //typeHole()
      //    ???
      //  case _ => SimpleTypeRep(xtyp) // FIXME is it safe to get the type args from 'xtyp.typ' ?? (what about holes etc.?)
      //}
      debug(s"Extr $name -> $rep")
      Some(Map(), Map(name -> rep), Map())
    }
    override def equals(that: Any) = that match {
      case TypeHoleRep(name1) => name == name1
      case _ => false
    }
    override def hashCode = name.##
    override def toString = s"$$$name"
  }
  
  def typEq(a: TypeRep, b: TypeRep): Boolean = a =:= b
  
  //def extractType(typ: TypeRep[_], xtor: TypeExt): Option[Extract] = xtor.extract(typ)
  //
  ////type Tag[A] = TypeTag[A]
  ////def typeHole[A: TypeTag](name: String): TypeRep[A] = TypeHoleRep[A](name)
  //def typeHole[A](name: String): TypeRep[A] = TypeHoleRep[A](name)(ScalaTyping.NoTypeTag[A])
  //def typeHole[A](name: String): TypeRep = TypeHoleRep[A](name)(ScalaTyping.NoTypeTag[A])
  def typeHole[A](name: String): TypeRep = TypeHoleRep[A](name)
  //def typeHole[A](name: String): TypeRep = ??? // FIXME not used; adapt API and ScalaTypingMacros.typeRep
  
  
  //implicit def funType[A: TypeEv, B: TypeEv]: TypeEv[A => B] = {
  //  implicit val A = typeEv[A].rep.tagAs[A]
  //  implicit val B = typeEv[B].rep.tagAs[B]
  //  TypeEv(ScalaTypeRep(typeTag[A => B], typeEv[A], typeEv[B]))
  //}
  def funType(a: TypeRep, b: TypeRep): TypeRep = {
    DynamicTypeRep(symbolOf[Any => Any].fullName, a, b)
  }
  val unitType: TypeRep = {
    SimpleTypeRep(typeOf[Unit])
  }
  
  
  //implicit def typeAll[A: TypeTag]: TypeEv[A] = TypeEv(ScalaTypeRep(typeTag[A]))
  
  import scala.language.experimental.macros
  implicit def typeEvImplicit[A]: TypeEv[A] = macro ScalaTypingMacros.typeEvImplicitImpl[A]
  
  
  /*
  import scala.language.experimental.macros
  //implicit def scope[A: TypeTag]: Scope[A] = null // TODO
  implicit def scope[A]: Scope[A] = macro ScalaTyping.scopeImpl[A]
  */
  
}
//object ScalaTyping {
  
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
//}

import reflect.macros.blackbox
class ScalaTypingMacros(val c: blackbox.Context) {
  import ScalaTyping.debugEnabled
  
  type Ctx = c.type
  val Ctx: c.type = c
  import Ctx.universe._
  
  val holeType = typeOf[Base.HoleType]
  
  // TODO an API to aggregate all needed typereps, without repetition (for QQ code-gen)
  def typeRep(base: Tree, tp: Type): Tree = {
    
    tp.widen match {
      case t if t <:< holeType // I imagine that this is less expensive than the following check
      && (tp.baseType(symbolOf[Base.HoleType]) != NoType) => // we have `Nothing <: HoleType`, but Nothing does not 'really' extend HoleType
        //debug("HOLE "+tp)
        q"$base.typeHole[$tp](${tp.typeSymbol.name.toString})"
        //q"$base.TypeHoleRep[$tp](${tp.typeSymbol.name.toString})()"
      case TypeRef(_, sym, args) =>
        //if (!sym.asType.isClass) c.abort(c.enclosingPosition, s"Unknown type! $sym")
        //q"$base.ScalaTypeRep(${sym.fullName.toString}, ..${args map (t => q"TypeEv(${typeRep(base,t)})")})"
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

















