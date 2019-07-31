// Copyright 2019 EPFL DATA Lab (data.epfl.ch)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package squid
package ir

import utils._
import CollectionUtils.TraversableOnceHelper
import utils.meta.{RuntimeUniverseHelpers => ruh}
import ruh.{srum, sru}
import squid.lang.Base
import squid.lang.IntermediateBase
import squid.quasi.ModularEmbedding
import sru.{internal => srui}
import sru.{Type => ScalaType}

import scala.reflect.runtime.universe.TypeTag
import scala.collection.mutable

object ScalaTyping {
  sealed abstract trait TypeHole[A <: String] // TODO make type hole traits extend this
}
import ScalaTyping._

trait ScalaTyping extends Base with TraceDebug {
self: IntermediateBase => // for 'repType' TODO rm
  
  /** It can be useful being able to retrieve a ClassTag from a CodeType, especially when dealing with Arrays. */
  implicit class ScalaTypingCodeTypeOps[T](self: CodeType[T]) {
    import scala.reflect.{classTag, ClassTag}
    def classTag: ClassTag[T] = ClassTag[T](runtimeClass)
    def runtimeClass: Class[T] = sru.rootMirror.runtimeClass(self.rep.tpe).asInstanceOf[Class[T]]
    def classTagCode: ClosedCode[ClassTag[T]] = {
      implicit val T = self
      import Predef.{Const => _, _}
      code"ClassTag[T](${Const(runtimeClass)})"
    }
  }
  
  type TypSymbol = sru.TypeSymbol
  type TypParam = sru.FreeTypeSymbol
  //type TypeRep = ScalaType
  implicit class TypeRep(val tpe: ScalaType) {
    override def toString = sru.show(tpe)
    override def equals(that: Any) = that.isInstanceOf[TypeRep] && that.asInstanceOf[TypeRep].tpe =:= tpe
    override def hashCode: Int = tpe.hashCode
  }
  object TypeRep { def unapply(t: TypeRep) = Some(t.tpe) }
  implicit def toScala(tr: TypeRep): ScalaType = tr.tpe // TODO rm
  
  class ExtractedType(val variance: Variance, val lb: ScalaType, val ub: ScalaType)
    extends TypeRep(if (variance == Contravariant) ub else lb)
    // ^ Note: sole purpose of `variance` field is to make the choice a little smarter and usually more appropriate...
    // though chosing `ub` or `lb` here is kind of arbitrary; it means that when extracting a type that is soundly
    // allowed to range within T0..T1, we pick T0 or T1 as the representative when a single type ends up being used.
    // The intuition is that this is the 'most precise' type satisfying the constraint – similar to what Scala's type
    // inference does (which sometimes has Nothing or Any inferred, depending on variance of where the type occurs).
    // As an example, in: `Some(0) match { case code"Some($v:$t)" => ... }`, `t` is inferred as `+Int..Any`; here the
    // intuitive thing to do when asked to materialize it (as in: pick a single type) is to make it `Int`, not `Any`.
  {
    assert(lb <:< ub, s"! $lb <:< $ub")
    override lazy val toString = variance.symbol * variance.asInt.abs +
      (if (lb =:= ub) s"$ub"
      else if (lb <:< ruh.Nothing) s"..$ub"
      else if (ruh.Any <:< ub) s"$lb.."
      else s"$lb..$ub")
  }
  object ExtractedType {
    def apply(variance: Variance, lb: ScalaType, ub: ScalaType) = new ExtractedType(variance,lb,ub)
    def apply(vari: Variance, tpe: ScalaType): ExtractedType = vari match {
      case Invariant => ExtractedType(vari,tpe,tpe)
      case Covariant => ExtractedType(vari,tpe,ruh.Any)
      case Contravariant => ExtractedType(vari,ruh.Nothing,tpe)
    }
    def ifNonEmpty(variance: Variance, lb: ScalaType, ub: ScalaType) = ExtractedType(variance, lb, ub) optionIf (lb <:< ub)
    def unapply(tp: TypeRep): Some[(Variance, ScalaType, ScalaType)] = Some(tp match {
      case et: ExtractedType => (et.variance, et.lb, et.ub)
      case TypeRep(tp) => (Invariant, tp, tp)
    })
  }
  
  override def mergeTypes(a: TypeRep, b: TypeRep): Option[ExtractedType] = (a,b) match {
    case (ExtractedType(av,a0,a1), ExtractedType(bv,b0,b1)) =>
      val v = if (av == bv) av else Invariant
      if ((a0 <:< b0) && (b0 <:< a1)) Some(ExtractedType(v,b0,a1))
      else if ((b0 <:< a0) && (a0 <:< b1)) Some(ExtractedType(v,a0,b1))
      // ^ Note: two cases above kind of redundant with the one below, but they're probably be a bit faster/simpler to compute
      else ExtractedType.ifNonEmpty(v, sru.lub(a0::b0::Nil), sru.glb(a1::b1::Nil))
  }
  
  
  def uninterpretedType[A: TypeTag]: TypeRep = sru.typeTag[A].tpe
  
  override def loadMtdTypParamSymbol(mtd: MtdSymbol, name: String): TypSymbol = mtd.asInstanceOf[AST#MtdSymbol].asMethodSymbol.typeParams.find(_.name.toString === name).get.asType
  
  
  //def typeApp(self: Rep, typ: TypSymbol, targs: List[TypeRep]): TypeRep = sru.internal.typeRef(repType(self), typ, targs map (_ tpe))
  def typeApp(self: TypeRep, typ: TypSymbol, targs: List[TypeRep]): TypeRep =
    sru.internal.typeRef(self, typ, targs map (_ tpe))
  
  def staticTypeApp(typ: TypSymbol, targs: List[TypeRep]): TypeRep = {
    //assert(typ.isStatic)
    assert(typ.isStatic || typ.isParameter)
    if (typ.isStatic)
      sru.internal.typeRef(typ.owner.asType.toType, typ, targs map (_ tpe))
    else
      //sru.internal.typeRef(typ.owner.asMethod.owner.asType.toType, typ, targs map (_ tpe))
      typ.toType
  }
  
  def valType(self: TypeRep, valName: String): TypeRep =
    sru.internal.singleType(self, self.tpe.member(sru.TermName(valName)))
  
  
  def constType(value: Any, underlying: TypeRep): TypeRep = constType(value)
  def constType(value: Any): TypeRep = sru.internal.constantType(sru.Constant(value))
  
  
  def typeHole(name: String): TypeRep = TypeHoleRep(name)
  
  override def typeParam(name: String): TypParam =
    sru.internal.newFreeType(name, sru.Flag.PARAM)//.toType
  
  
  def typLeq(a: TypeRep, b: TypeRep): Boolean = a <:< b
  def weakTypLeq(a: TypeRep, b: TypeRep, va: Variance = Covariant): Boolean = extractType(a, b, va).isDefined
  
  def lambdaType(paramTyps: List[TypeRep], ret: TypeRep): TypeRep =
    ruh.FunctionType(paramTyps map (_ tpe): _*)(ret)
  
  
  object TypeHoleRep {
    import sru._
    def apply(name: String) =
      srui.typeRef(typeOf[ScalaTyping.type], symbolOf[TypeHole[_]], srui.constantType(Constant(name)) :: Nil)
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
        
        if (xtyp.typeSymbol.isParameter) return None alsoDo debug(s"Cannot match parameter type `$xtyp`.")
        // ^ Sometimes we get this kind of types because of type-tag-based uninterpretedType
        
        if (xtyp =:= ruh.Null) return None alsoDo debug(s"Cannot match type `Null`.") // TODO  actually allow this match (but soundly)
        
        def erron = None alsoDo debug(s"Method `baseType` returned an erroneous type.")
        
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
                Stream continually None
              }
              else return None
            case base if base |> isErroneous => return erron
            case base =>
              debug(s"$va $typ is an instance of ${xtyp.typeSymbol} as '$base'")
              base.typeArgs.toStream.map(Some.apply)
          }
          
          if (isDebugEnabled) {
            val ets = baseTargs zip xtyp.typeArgs zip (xtyp.typeSymbol.asType.typeParams map (Variance of _.asType) map (_ * va))
            if (ets nonEmpty) debug(s"$va Extr Targs(contra case): "+(ets mkString " "))
          }
          
          assert(!baseTargs.hasDefiniteSize || xtyp.typeArgs.size == baseTargs.size)
          
          val extr = (baseTargs zip xtyp.typeArgs zip xtyp.typeSymbol.asType.typeParams) map {
            case ((a,b), p) =>
              val newVa = (Variance of p.asType) * va
              extractType(a.fold[TypeRep](ExtractedType(newVa,ruh.Nothing,ruh.Any))(TypeRep), b, newVa) getOrElse (return None) }
          
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
          
        } else { // Here, either we're in covariant case, or the typeSymbols match (or both)
          
          val base = xtyp.baseType(typ.typeSymbol).orElse(NoType)
          // ^ this `.orElse(NoType)` seems to semantically do nothing; in fact, it will convert a NoType in the wrong 
          // universe to a NoType in our current universe so that the comparison below `base == NoType` works correctly.
          // The reason we sometimes get types from different universes (a.k.a. alien types) is too gruesome to be explained here.
          
          val baseTargs = if (base == NoType) {
            debug(s"$xtyp not an instance of ${typ.typeSymbol}")
            
            if (xtyp <:< typ) {
              debug(s"... but $xtyp is still somehow a subtype of ${typ}")
              assert((xtyp <:< ruh.Nothing) || (xtyp <:< ruh.Null),
                s"$xtyp <:< $typ but !($xtyp <:< Nothing) and ${typ.typeSymbol} is not a base type of $xtyp")
              
              Stream continually None
            }
            else return None
          }
          else if (base |> isErroneous) return erron
          else base.typeArgs.toStream.map(Some.apply)
          
          if (isDebugEnabled) {
            val ets = targs zip baseTargs zip (typ.typeSymbol.asType.typeParams map (Variance of _.asType) map (_ * va))
            if (ets nonEmpty) debug(s"$va Extr Targs: " + (ets mkString " "))
          }
          
          assert(!baseTargs.hasDefiniteSize || targs.size == baseTargs.size, s"$baseTargs $targs")
          
          val extr = (targs zip baseTargs zip typ.typeSymbol.asType.typeParams) map {
            case ((a,b), p) =>
              val newVa = (Variance of p.asType) * va
              extractType(a, b.fold[TypeRep](ExtractedType(newVa,ruh.Nothing,ruh.Any))(TypeRep), newVa) getOrElse (return None) }
          
          //dbg("EXTR",extr)
          Some(extr.foldLeft[Extract](Map(), Map(), Map()){case(acc,a) => merge(acc,a) getOrElse (return None)})
        }
        
        
    }}
    
  }
  
  /** Note: will _not_ try to special-case type holes (encoded as normal Scala types...)
    * This is because reinterpreted types are not usually from extractor terms -- however, this assumption might turn wrong at some point */
  def reinterpretType(tr: TypeRep, newBase: Base): newBase.TypeRep = {
    val modEmb = new ModularEmbedding[sru.type,newBase.type](sru, newBase, debug = x => debug(x))
    modEmb.liftType(tr.tpe)
  }
  
  
  def nullValue[T: CodeType]: ClosedCode[T] = {
    val tp = implicitly[CodeType[T]].rep.tpe |>=? {
      case TypeHoleRep(name) => throw new IllegalArgumentException("Type hole has no known nullValue.")
    }
    Code(const(
      if (tp <:< sru.typeOf[Unit]) NULL_UNIT
      else if (tp <:< sru.typeOf[Bool]) NULL_BOOL
      else if (tp <:< sru.typeOf[Char]) NULL_CHAR
      else if (tp <:< sru.typeOf[Byte]) NULL_BYTE
      else if (tp <:< sru.typeOf[Short]) NULL_SHORT
      else if (tp <:< sru.typeOf[Int]) NULL_INT
      else if (tp <:< sru.typeOf[Long]) NULL_LONG
      else if (tp <:< sru.typeOf[Float]) NULL_FLOAT
      else if (tp <:< sru.typeOf[Double]) NULL_DOUBLE
      else {
        val N = sru.typeOf[Null]
        assert(N <:< tp || tp <:< N, // second case is for the stupid literal type Null(null)
          s"$tp is not nullable nor .")
        NULL_NULL
      }
    ))
  }
  
  private var NULL_UNIT: Unit = _
  private var NULL_BOOL: Bool = _
  private var NULL_CHAR: Char = _
  private var NULL_BYTE: Byte = _
  private var NULL_SHORT: Short = _
  private var NULL_INT: Int = _
  private var NULL_LONG: Long = _
  private var NULL_FLOAT: Float = _
  private var NULL_DOUBLE: Double = _
  private var NULL_NULL: Null = _
  
  
}







