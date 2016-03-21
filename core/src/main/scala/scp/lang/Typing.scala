package scp.lang

import reflect.api
import reflect.api.Universe
import annotation.unchecked.uncheckedVariance
import scala.language.higherKinds
import scala.reflect.runtime.universe.{Type => ScalaType, _}

//trait Typing { base: Base =>
//  
//  def typEq(a: TypeRep[_], b: TypeRep[_]): Boolean
//  
//  implicit def funType[A: TypeEv, B: TypeEv]: TypeEv[A => B]
//  
//}

/**
  * TODO put TypeRep in companion object
  */
trait ScalaTyping extends Base {
  import ScalaTyping._
  
  //type TypeExt = TypeRep[_]
  
  //type DSLSymbol = MethodSymbol // TODO
  ///** Note: could cache results */
  //def loadSymbol(fullName: String, info: String, module: Boolean): DSLSymbol = {
  //  val mtd = DSLDef(fullName, info, module)
  //  val cls = Class.forName(mtd.path(0))
  //  ???
  //}
  def loadSymbol(sym: MethodSymbol): DSLSymbol = sym
  
  
  sealed trait TypeRep {
    implicit val tag: TypeTag[_]
    def tagAs[A] = tag.asInstanceOf[TypeTag[A]]
    val typ = tag.tpe
    
    def =:= (that: TypeRep) = typ =:= that.typ
    def <:< (that: TypeRep) = typ <:< that.typ
    
    def extract(tp: TypeRep): Option[Extract] = {
      debug("Match "+tp+" with "+this)
      (this, tp) match {
        case (_, TypeHoleRep(_)) => throw new IllegalArgumentException // TODO BE
        case (TypeHoleRep(holeName), tp) => Some(Map() -> Map(holeName -> tp))
        case (ScalaTypeRep(tag0, targs0 @ _*), ScalaTypeRep(tag1, targs1 @ _*)) =>
          extractTp(tp.typ)
      }
    }
    
    protected[ScalaTyping] def extractTp(xtyp: ScalaType): Option[Extract]
    
    override def toString = s"$typ"
  }
  
  /**
    * `targs` will only be used when the TypeRep is used as an extractor; it may not be present for normal types.
    * EDIT: nope
    */
  case class ScalaTypeRep[A](tag: TypeTag[A], targs: TypeEv[_]*) extends TypeRep {
  //case class ScalaTypeRep[A](targs: TypeEv[_]*) extends TypeRep {
    
    //val tag: TypeTag[A] = {
    //  import
    //}
    
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
          
          val extr = (targs zip base.typeArgs) map { case (a,b) => a.rep.extractTp(b) getOrElse (return None) }
          
          Some(extr.foldLeft[Extract](Map() -> Map()){case(acc,a) => merge(acc,a) getOrElse (return None)})
      }
      
    }
    
    
    override def equals(that: Any) = that match {
      case ScalaTypeRep(tag1, _ @ _*) => typ =:= tag1.tpe
      case _ => false
    }
    override def hashCode = typ.##
    
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
    protected[ScalaTyping] def extractTp(xtyp: ScalaType): Option[Extract] = {
      val tag = ScalaTyping.mkTag[A](xtyp)
      val rep = ScalaTypeRep(tag) // It is safe not to fill-in the targs because the targs are only used in extractors
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
  
  
  implicit def funType[A: TypeEv, B: TypeEv]: TypeEv[A => B] = {
    implicit val A = typeEv[A].rep.tagAs[A]
    implicit val B = typeEv[B].rep.tagAs[B]
    TypeEv(ScalaTypeRep(typeTag[A => B], typeEv[A], typeEv[B]))
  }
  
  
  //implicit def typeAll[A: TypeTag]: TypeEv[A] = TypeEv(ScalaTypeRep(typeTag[A]))
  
  import scala.language.experimental.macros
  implicit def typeEvImplicit[A]: TypeEv[A] = macro ScalaTyping.typeEvImplicitImpl[A]
  
  
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
        q"TypeEv($base.typeHole[$A](${A.typeSymbol.name.toString}))"
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
          TypeEv($base.ScalaTypeRep[$A](typeTag[$A], ..${evs map (_.name)}))
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
            q"TypeEv($base.ScalaTypeRep($tag))"
        }
        //null // failure
    }
    
    
    //q"null" // TODO
    //null
  }
  
}

















