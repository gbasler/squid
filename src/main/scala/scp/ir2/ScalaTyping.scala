package scp
package ir2

import lang2._
import utils._
import CollectionUtils.TraversableOnceHelper
import utils.meta.{RuntimeUniverseHelpers => ruh}
import ruh.{sru, srum}
import sru.{internal => srui}
import sru.{Type => ScalaType}

import scala.reflect.runtime.universe.TypeTag
import scala.collection.mutable

object ScalaTyping {
  sealed abstract trait TypeHole[A <: String] // TODO make type hole traits extend this
}
import ScalaTyping._

trait ScalaTyping extends Base with TraceDebug {
self: lang2.IntermediateBase => // for 'repType' TODO rm
  
  type TypSymbol = sru.TypeSymbol
  //type TypeRep = ScalaType
  implicit class TypeRep(val tpe: ScalaType) {
    override def toString = sru.show(tpe)
    override def equals(that: Any) = that.isInstanceOf[TypeRep] && that.asInstanceOf[TypeRep].tpe =:= tpe
    override def hashCode: Int = tpe.hashCode
  }
  implicit def toScala(tr: TypeRep): ScalaType = tr.tpe // TODO rm
  case class ExtractedType(val vari: Variance, override val tpe: ScalaType) extends TypeRep(tpe) {
    override def toString = s"${vari symbol}$tpe"
  }
  override def mergeTypes(a: TypeRep, b: TypeRep): Option[ExtractedType] = (a -> b match {
    case ExtractedType(Invariant, a) -> ExtractedType(Invariant, b) => if (a =:= b) Some(Invariant, a) else None
    case ExtractedType(Invariant, a) -> ExtractedType(Covariant, b) => if (b <:< a) Some(Invariant, a) else None
    case ExtractedType(Invariant, a) -> ExtractedType(Contravariant, b) => if (a <:< b) Some(Invariant, a) else None
    case ExtractedType(Covariant, a) -> ExtractedType(Covariant, b) => Some(Covariant, sru.lub(a::b::Nil))
    case ExtractedType(Contravariant, a) -> ExtractedType(Contravariant, b) => Some(Contravariant, sru.glb(a::b::Nil))
    case ExtractedType(Covariant, a) -> ExtractedType(Contravariant, b) => Some(Invariant, a) // arbitrary!
    case (a: ExtractedType) -> (b: ExtractedType) => mergeTypes(b, a) map { case ExtractedType(v,t) => v -> t }
    case _ => wtf
  }) map ExtractedType.tupled and { case None => debug(s"Could not merge types $a and $b") case _ =>}
  
  def uninterpretedType[A: TypeTag]: TypeRep = sru.typeTag[A].tpe
  
  def typeApp(self: Rep, typ: TypSymbol, targs: List[TypeRep]): TypeRep = sru.internal.typeRef(repType(self), typ, targs map (_ tpe))
  
  def recordType(fields: List[(String, TypeRep)]): TypeRep = {
    // TODO cache stuff
    import sru._
    import sru.{internal => inl}
    import inl.{reificationSupport => reif}
    val reftSymdef = reif.newNestedSymbol(srum.staticModule("scp.ir2.ScalaTyping"), TypeName.apply("<refinement>"), NoPosition, NoFlags, true)
    val syms = fields map {
      case (name,typ) =>
        val symdef = reif.newNestedSymbol(reftSymdef, TermName(name), NoPosition, /*internal.reificationSupport.FlagsRepr.apply(138412112L)*/NoFlags, false);
        reif.setInfo[Symbol](symdef, reif.NullaryMethodType(typ));
        symdef
    }
    val reftp = reif.RefinedType(Nil, reif.newScopeWith(syms: _*), reftSymdef)
    reif.setInfo[Symbol](reftSymdef, reftp)
    reftp
  }
  object RecordType {
    def unapply(tpe: ScalaType) = tpe match {
      case sru.RefinedType(Nil, scp) =>
        Some(scp map (s => s.name.toString -> s.typeSignature))
      case _ => None
    } 
  }
  
  def staticModuleType(fullName: String) = {
    val modSym = srum.staticModule(fullName)
    RuntimeSymbols.ensureDefined(s"module $fullName", modSym)
    //assert(modSym.moduleClass.asType.toType =:= modSym.typeSignature)  // seems to always return true
    
    /* In the future we may want to return a type attesting that this is a singleton type,
    so we can equate method calls like DSL.Quasicodes.qcbase and DSL.Predef.base that return equivalent singleton types */
    //new TypeRep(sru.internal.thisType(modSym.moduleClass))
    
    new TypeRep(modSym.typeSignature)
  }
  
  def constType(value: Any, underlying: TypeRep): TypeRep = constType(value)
  def constType(value: Any): TypeRep = sru.internal.constantType(sru.Constant(value))
  
  
  def typeHole(name: String): TypeRep = TypeHoleRep(name)
  
  
  
  def typLeq(a: TypeRep, b: TypeRep): Boolean = a <:< b
  def weakTypLeq(a: TypeRep, b: TypeRep, va: Variance = Covariant): Boolean = extractType(a, b, va).isDefined
  
  /* // breaks scalac...
  lazy val (funOwnerType, funSym) = {
    val sym = sru.symbolOf[Any => Any]
    (sym.owner.asType.toType, sym)
  }
  */
  lazy val funSym = sru.symbolOf[Any => Any]
  lazy val funOwnerType = funSym.owner.asType.toType
  def funType(paramt: TypeRep, bodyt: TypeRep): TypeRep = srui.typeRef(funOwnerType, funSym, paramt.tpe :: bodyt.tpe :: Nil)
  
  
  object TypeHoleRep {
    import sru._
    def apply(name: String) =
      srui.typeRef(typeOf[ScalaTyping], symbolOf[TypeHole[_]], srui.constantType(Constant(name)) :: Nil)
    //def unapply(tp: TypeRep) = tp.tpe match {
    def unapply(tp: Type) = tp match {
      case ht @ TypeRef(_, sym, arg :: Nil) if sym == symbolOf[TypeHole[_]] =>
        //debug(arg,arg.getClass)
        arg match {
          case ConstantType(sru.Constant(name: String)) =>
            Some(name)
          case _ => 
            System.err.println(s"Warning: hole type `$ht` has been widened.")
            None
        }
      case _ => None
    }
  }
  def hasHoles(tp: TypeRep) = tp exists { case TypeHoleRep(_) => true  case _ => false }
  
  def extractType(self: TypeRep, other: TypeRep, va: Variance): Option[Extract] = {
    import sru._
    import ruh._
    
    debug(s"$va Match $other with $self")
    nestDbg { self.tpe -> other.tpe match {
      // TODO thisType, constantType
        
      case TypeHoleRep(name) -> _ => Some(Map(), Map(name -> ExtractedType(va, other)), Map())
        
      case RefinedType(ps0, scp0) -> PossibleRefinement(ps1, scp1) =>
        if (ps0.size != ps1.size) return None
        val ps = (ps0.iterator zipAnd ps1){extractType(_,_,va)}
        val syms1 = mutable.HashMap(scp1 map (s => s.name -> s) toSeq: _*)
        val scp = scp0.iterator flatMap { s0 =>
          val s1 = syms1 getOrElse (s0.name, return None)
          if (s0.alternatives.size != s1.alternatives.size) return None
          (s0.alternatives zipAnd s1.alternatives) {
            case (s0,s1) => extractType(s0.typeSignature, s1.typeSignature, va)
          }
        }
        mergeAll(ps ++ scp)
        
      //case TypeRef(tp0, sum0, targs0) -> xtyp =>
      case typ -> xtyp => // FIXME: is it okay to do this here? we should probably ensure typ is a TypeRef...
        val targs = typ.typeArgs
        
        if (va == Contravariant && xtyp.typeSymbol != typ.typeSymbol) {
        
          val baseTargs = typ.baseType(xtyp.typeSymbol) match {
            case NoType =>
              debug(s"$va $typ is not an instance of ${xtyp.typeSymbol}")
              if (typ <:< xtyp) {
                debug(s"... but $typ is still somehow a subtype of ${xtyp}")
                //assert(Any <:< xtyp,
                //  s"$typ <:< $xtyp but !($xtyp >:> Any) and ${xtyp.typeSymbol} is not a base type of $typ")
              assert(typ <:< ruh.Nothing,
                s"$typ <:< $xtyp but !($typ <:< Nothing) and ${xtyp.typeSymbol} is not a base type of $typ")
                Stream continually ruh.Nothing
              }
              else return None
            case base =>
              debug(s"$va $typ is an instance of ${xtyp.typeSymbol} as '$base'")
              base.typeArgs.toStream
          }
          
          if (isDebugEnabled) {
            val ets = baseTargs zip xtyp.typeArgs zip (xtyp.typeSymbol.asType.typeParams map (Variance of _.asType) map (_ * va))
            if (ets nonEmpty) debug(s"$va Extr Targs(contra case): "+(ets mkString " "))
          }
          
          assert(!baseTargs.hasDefiniteSize || xtyp.typeArgs.size == baseTargs.size)
          
          val extr = (baseTargs zip xtyp.typeArgs zip xtyp.typeSymbol.asType.typeParams) map {
            case ((a,b), p) => extractType(a, b, (Variance of p.asType) * va) getOrElse (return None) }
          
          val extr2 = targs zip typ.typeSymbol.asType.typeParams map {case(ta,tp) => /*Variance.of(tp.asType) match {
            //case Covariant | Invariant => extractType(ta, Nothing, Contravariant)
            case Covariant | Invariant => extractType(ta, Nothing, Covariant)
            case Contravariant => extractType(ta, Any, Contravariant)
          }*/
            //extractType(ta, Nothing, Covariant)
            extractType(ta, Any, Contravariant) // FIXME should really be the interval 'LB..HB'
          } map (_  getOrElse (return None)) //filter (_ => false)
          
          Some((extr ++ extr2).foldLeft[Extract](Map(), Map(), Map()){case(acc,a) => merge(acc,a) getOrElse (return None)})
          
        } else if (va == Invariant && xtyp.typeSymbol != typ.typeSymbol) {
          
          debug(s"${xtyp.typeSymbol} and ${typ.typeSymbol} cannot be matched invariantly")
          None
          
        } else {
          
          val base = xtyp.baseType(typ.typeSymbol)
          
          val baseTargs = if (base == NoType) {
            debug(s"$xtyp not an instance of ${typ.typeSymbol}")
            
            if (xtyp <:< typ) {
              debug(s"... but $xtyp is still somehow a subtype of ${typ}")
              assert(xtyp <:< ruh.Nothing,
                s"$xtyp <:< $typ but !($xtyp <:< Nothing) and ${typ.typeSymbol} is not a base type of $xtyp")
              
              Stream continually ruh.Nothing
            }
            else return None
          }
          else base.typeArgs.toStream
          
          if (isDebugEnabled) {
            val ets = targs zip baseTargs zip (typ.typeSymbol.asType.typeParams map (Variance of _.asType) map (_ * va))
            if (ets nonEmpty) debug(s"$va Extr Targs: " + (ets mkString " "))
          }
          
          assert(!baseTargs.hasDefiniteSize || targs.size == baseTargs.size)
          
          val extr = (targs zip baseTargs zip typ.typeSymbol.asType.typeParams) map {
            case ((a,b), p) => extractType(a, b, (Variance of p.asType) * va) getOrElse (return None) }
          
          //dbg("EXTR",extr)
          Some(extr.foldLeft[Extract](Map(), Map(), Map()){case(acc,a) => merge(acc,a) getOrElse (return None)})
        }
        
        
    }}
    
  }
  
  import sru._
  def reinterpretType(tr: TypeRep, newBase: Base): newBase.TypeRep = tr.tpe match {
      
    case TypeHoleRep(name) => 
      newBase match {
        case newBase: newBase.type with quasi2.QuasiBase =>
          newBase.typeHole(name)
        case _ => throw new IRException(
          s"Base $newBase does not inherit from QuasiBase and cannot handle hole type '$name'")
      }
      
    case TypeRef(pre, sym, targs) => newBase.typeApp( // FIXME
      { //reinterpretType(pre, newBase), 
        
        //val mo = pre.asInstanceOf[AST#ModuleObject]
        //newBase.moduleObject(mo.fullName, mo.isPackage)
        
        try newBase.moduleObject(pre.typeSymbol.fullName, pre.typeSymbol.isPackage) // FIXME class loading
        catch { case (_: java.lang.ClassNotFoundException) | (_:java.lang.reflect.InvocationTargetException) => null.asInstanceOf[newBase.Rep] }
      },
      {
        try newBase.loadTypSymbol(sym.fullName) // FIXME class loading
        catch {
          case (_: java.lang.ClassNotFoundException) | (_:java.lang.reflect.InvocationTargetException) => null.asInstanceOf[newBase.TypSymbol]
        }
      },
      targs map (t => reinterpretType(t, newBase)))
      
    //case sru.RefinedType(ps0, scp0) => ??? // TODO -- eg record type
    case sru.RefinedType(Nil, scp0) => newBase.recordType(scp0 map (s => s.name.toString -> reinterpretType(s.typeSignature, newBase)) toList)
    case sru.RefinedType(ps0, scp0) => ??? // TODO B/E
    case _ =>
      tr foreach {
        case TypeHoleRep(name) => throw new IRException(
          s"Type $tr could not be reinterpreted and cannot be left uninterpreted as it contains hole type '$name'")
        case _ =>
      }
      newBase.uninterpretedType(ruh mkTag tr)
  }
  
  
  
}







