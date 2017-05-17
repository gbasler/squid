package squid
package quasi

import squid.ir.RewriteAbort
import squid.ir.Transformer
import utils._
import squid.lang.Base
import squid.lang.InspectableBase
import squid.lang.IntermediateBase
import utils.MacroUtils.MacroSetting

import scala.collection.mutable


/** Provides the macro infrastructure to allow for using quasiquotes ir"foo" and quasicode ir{foo}, 
  * which are more type-safe interfaces to manipulate code than the Base functions. */
/* TODO implement feature restrictions in quasiquotes, so pattern QQs cannot be defined for non-inspectable bases...
 * Also make Liftable instances nicer since we are now be able to assume QQ/QC can be used on all bases */
trait QuasiBase {
self: Base =>
  
  
  // TODO move these to IntermediateBase
  
  /** Should substitute holes for the given definitions */
  // TODO impl correctly
  //def substitute(r: Rep, defs: Map[String, Rep]): Rep = substituteLazy(r, defs map (kv => kv._1 -> (() => kv._2)))
  //def substituteLazy(r: Rep, defs: Map[String, () => Rep]): Rep
  def substitute(r: Rep, defs: Map[String, Rep]): Rep
  def substituteLazy(r: Rep, defs: Map[String, () => Rep]): Rep = substitute(r, defs map (kv => kv._1 -> kv._2()))
  
  def hole(name: String, typ: TypeRep): Rep
  def splicedHole(name: String, typ: TypeRep): Rep
  
  def typeHole(name: String): TypeRep
  
  
  
  
  
  
  /* --- --- --- Provided defs --- --- --- */
  
  
  final def substitute(r: Rep, defs: (String, Rep)*): Rep =
  /* if (defs isEmpty) r else */  // <- This "optimization" is not welcome, as some IRs (ANF) may relie on `substitute` being called for all insertions
    substitute(r, defs.toMap)
  
  protected def mkIR[T,C](r: Rep): IR[T,C] = new IR[T,C] {
    val rep = r
    override def equals(that: Any): Boolean = that match {
      case that: IR[_,_] => rep =~= that.rep
      case _ => false
    }
  }
  object IR {
    def apply[T,C](rep: Rep): IR[T,C] = mkIR(rep)
    def unapply[T,C](ir: IR[T,C]): Option[Rep] = Some(ir.rep)
  }
  
  // Unsealed to allow for abstract constructs to inherit from IR, for convenience.
  abstract class IR[+T, -C] protected () extends TypeErased with ContextErased {
    type Typ <: T
    type Ctx >: C
    
    val rep: Rep
    
    //// dangerous... -> when Rep extends IR, this makes SchedulingANF consider `x` and `(x:t)` equal, for example, 
    //// which has weird consequences (eg scheduling one as the other):
    //override def equals(that: Any): Boolean = that match {
    //  case that: IR[_,_] => rep =~= that.rep
    //  case _ => false
    //}
    
    def =~= (that: IR[_,_]): Boolean = rep =~= that.rep
    
    def cast[S >: T]: IR[S, C] = this
    def erase: IR[Any, C] = this
    
    def transformWith(trans: ir.Transformer{val base: self.type}) = IR[T,C](trans.pipeline(rep))
    
    /** Useful when we have non-denotable types (e.g., extracted types) */
    def withTypeOf[Typ >: T](x: IR[Typ, Nothing]) = this: IR[Typ,C]
    
    /** Useful when we have non-denotable contexts (e.g., rewrite rule contexts) */
    def withContextOf[Ctx <: C](x: IR[Any, Ctx]) = this: IR[T,Ctx]
    //def unsafeWithContextOf[Ctx](x: IR[Any, Ctx]) = this.asInstanceOf[IR[T,Ctx]]  // does not seem to be too useful...
    
    override def toString: String = {
      val repStr = showRep(rep)
      val quote = if (repStr contains '\n') "\"" * 3 else "\""
      s"ir$quote$repStr$quote"
    }
  }
  def `internal IR`[Typ,Ctx](rep: Rep) = IR[Typ,Ctx](rep) // mainly for macros
  type SomeIR = IR[_, _] // shortcut; avoids showing messy existentials
  
  
  sealed case class IRType[T] private[quasi] (rep: TypeRep) extends TypeErased {
    type Typ = T
    def <:< (that: IRType[_]) = rep <:< that.rep
    def =:= (that: IRType[_]) = rep =:= that.rep
  }
  def `internal IRType`[Typ](trep: TypeRep) = IRType[Typ](trep) // mainly for macros
  
  trait TypeErased { type Typ }
  trait ContextErased { type Ctx }
  
  /** Allows things like TypeOf[ir.type] and TypeOf[irTyp.type], useful when extracted values have non-denotable types  */
  type TypeOf[QT <: TypeErased] = QT#Typ
  
  /** Allows things like ContextOf[ir.type], useful when extracted values have non-denotable contexts  */
  type ContextOf[QT <: ContextErased] = QT#Ctx
  
  
  def irTypeOf[T: IRType] = implicitly[IRType[T]]
  def typeRepOf[T: IRType] = implicitly[IRType[T]].rep
  
  
  val Predef  = new Predef[DefaultQuasiConfig]
  class Predef[QC <: QuasiConfig] {
    val base: self.type = self // macro expansions will make reference to it
    
    type IR[+T,-C] = self.IR[T,C]
    type IRType[T] = self.IRType[T]
    type __* = self.__*
    
    val Const: self.Const.type = self.Const
    def irTypeOf[T: IRType] = self.irTypeOf[T]
    def typeRepOf[T: IRType] = self.typeRepOf[T]
    
    implicit def anyContextIsEmptyContext[A](ir: IR[A,AnyRef]): IR[A,Any] = ir.asInstanceOf[IR[A,Any]]
    
    def nullValue[T: IRType](implicit ev: base.type <:< (base.type with IntermediateBase)): IR[T,{}] = {
      val b: base.type with IntermediateBase = ev(base)
      //b.nullValue[T](irTypeOf[T]) // should work, but Scala doesn't like it
      b.nullValue[T](irTypeOf[T].asInstanceOf[b.IRType[T]])
    }
    // Unsafe version:
    //def nullValue[T: IRType]: IR[T,{}] = {
    //  val b = base.asInstanceOf[base.type with IntermediateBase]
    //  b.nullValue[T](irTypeOf[T].asInstanceOf[b.IRType[T]]) }
    
    def Abort(msg: String = ""): Nothing = throw new IllegalAccessError("Abort was called outside a rewrite rule!")
    
    object Return {
      private def oops = System.err.println("Return was called outside a rewrite rule!")
      def apply[T,C](x: IR[T,C]): IR[T,C] = {
        oops
        x
      }
      def transforming[A,CA,T,C](a: IR[A,CA])(f: IR[A,CA] => IR[T,C]): IR[T,C] = {
        oops
        f(a)
      }
      def transforming[A,CA,B,CB,T,C](a: IR[A,CA], b: IR[B,CB])(f: (IR[A,CA], IR[B,CB]) => IR[T,C]): IR[T,C] = {
        oops
        f(a,b)
      }
      def transforming[A,CA,B,CB,D,CD,T,C](a: IR[A,CA], b: IR[B,CB], d: IR[D,CD])(f: (IR[A,CA], IR[B,CB], IR[D,CD]) => IR[T,C]): IR[T,C] = {
        oops
        f(a,b,d)
      }
      def transforming[A,CA,T,C](as: List[IR[A,CA]])(f: List[IR[A,CA]] => IR[T,C]): IR[T,C] = {
        oops
        f(as)
      }
      def recursing[T,C](cont: Transformer{val base: self.type} => IR[T,C]): IR[T,C] = {
        oops
        cont(new Transformer { // Dummy transformer that doesn't do anything
          override def transform(rep: base.Rep) = rep
          override val base: self.type with InspectableBase = self.asInstanceOf[self.type with InspectableBase]
        })
      }
    }
    
    import scala.language.experimental.macros
    
    implicit class QuasiContext(private val ctx: StringContext) extends self.Quasiquotes.QuasiContext(ctx)
    
    implicit def implicitType[T]: IRType[T] = macro QuasiBlackboxMacros.implicitTypeImpl[QC, T]
    
    object dbg {
      /** import this `implicitType` explicitly to shadow the non-debug one */ 
      @MacroSetting(debug = true) implicit def implicitType[T]: IRType[T] = macro QuasiBlackboxMacros.implicitTypeImpl[QC, T]
    }
  }
  
  def `internal abort`(msg: String): Nothing = throw RewriteAbort(msg)
  
  
  
  /** Artifact of a term extraction: map from hole name to terms, types and spliced term lists */
  type Extract = (Map[String, Rep], Map[String, TypeRep], Map[String, Seq[Rep]])
  type Extract_? = Extract |> Option
  protected final val EmptyExtract: Extract = (Map(), Map(), Map())
  protected final val SomeEmptyExtract: Option[Extract] = EmptyExtract |> some
  @inline protected final def mkExtract(rs: String -> Rep *)(ts: String -> TypeRep *)(srs: String -> Seq[Rep] *): Extract = (rs toMap, ts toMap, srs toMap)
  @inline protected final def repExtract(rs: String -> Rep *): Extract = mkExtract(rs: _*)()()
  protected def extractedKeys(e: Extract): Set[String] = e._1.keySet++e._2.keySet++e._3.keySet
  
  protected def mergeOpt(a: Option[Extract], b: => Option[Extract]): Option[Extract] = for { a <- a; b <- b; m <- merge(a,b) } yield m
  protected def merge(a: Extract, b: Extract): Option[Extract] = {
    b._1 foreach { case (name, vb) => (a._1 get name) foreach { va => if (!mergeableReps(va, vb)) return None } }
    b._3 foreach { case (name, vb) => (a._3 get name) foreach { va =>
      if (va.size != vb.size || !(va.iterator zip vb.iterator forall {case (vva,vvb) => mergeableReps(vva, vvb)})) return None } }
    val typs = a._2 ++ b._2.map {
      case (name, t) =>
        a._2 get name map (t2 => name -> mergeTypes(t, t2).getOrElse(return None)) getOrElse (name -> t)
    }
    val splicedVals = a._3 ++ b._3
    val vals = a._1 ++ b._1
    Some(vals, typs, splicedVals)
  }
  protected def mergeAll(as: TraversableOnce[Option[Extract]]): Option[Extract] = {
    if (as isEmpty) return Some(EmptyExtract)
    
    //as.reduce[Option[Extract]] { case (acc, a) => for (acc <- acc; a <- a; m <- merge(acc, a)) yield m }
    /* ^ not good as it evaluates all elements of `as` (even if it's an Iterator or Stream) */
    
    val ite = as.toIterator
    var res = ite.next()
    while(ite.hasNext && res.isDefined) res = mergeOpt(res, ite.next())
    res
  }
  
  def mergeableReps(a: Rep, b: Rep): Boolean = a =~= b
  
  def mergeTypes(a: TypeRep, b: TypeRep): Option[TypeRep] =
    // Note: We can probably do better than =:=, but that is up to the implementation of TypingBase to override it
    if (typEq(a,b)) Some(a) else None
  
  
  private var extracting = false
  protected def isExtracting = extracting
  final protected def isConstructing = !isExtracting
  def wrapConstruct(r: => Rep) = { val old = extracting; extracting = false; try r finally { extracting = old } }
  def wrapExtract  (r: => Rep) = { val old = extracting; extracting = true;  try r finally { extracting = old } }
  //def wrapExtract  (r: => Rep) = { val old = extracting; extracting = true; println("BEG EXTR"); try r finally { println("END EXTR"); extracting = old } }
  // ^ TODO find a solution cf by-name params, which creates owner corruption in macros

  
  type __* = Seq[IR[_,_]] // used in insertion holes, as in:  ${xs: __*}
  
  
  
  /** TODO make it more generic: use Liftable! */
  /* To support insertion syntax `$xs` (in ction) or `$$xs` (in xtion) */
  def $[T,C](q: IR[T,C]*): T = ??? // TODO B/E  -- also, rename to 'unquote'?
  //def $[T,C](q: IR[T,C]): T = ??? // Actually unnecessary
  
  /* To support hole syntax `$$xs` (in ction) or `$xs` (in xtion)  */
  def $$[T](name: Symbol): T = ???
  /* To support hole syntax `$$xs: _*` (in ction) or `$xs: _*` (in xtion)  */
  def $$_*[T](name: Symbol): Seq[T] = ???
  
  
  
  import scala.language.experimental.macros
  
  class Quasiquotes[QC <: QuasiConfig] {
    val base: QuasiBase.this.type = QuasiBase.this
    
    implicit class QuasiContext(private val ctx: StringContext) {
      
      object ir { // TODO try to refine the types..?
        //def apply(t: Any*): Any = macro QuasiMacros.applyImpl[QC]
        //def unapply(t: Any): Any = macro QuasiMacros.unapplyImpl[QC]
        def apply(inserted: Any*): SomeIR = macro QuasiMacros.applyImpl[QC]
        def unapply(scrutinee: SomeIR): Any = macro QuasiMacros.unapplyImpl[QC]
      }
      
      object dbg_ir { // TODO try to refine the types..?
        @MacroSetting(debug = true) def apply(inserted: Any*): Any = macro QuasiMacros.applyImpl[QC]
        @MacroSetting(debug = true) def unapply(scrutinee: Any): Any = macro QuasiMacros.unapplyImpl[QC]
      }
      
    }
    
  }
  class Quasicodes[QC <: QuasiConfig] {
    val qcbase: QuasiBase.this.type = QuasiBase.this // macro expansions will make reference to it
    
    def ir[T](tree: T): IR[T, _] = macro QuasiMacros.quasicodeImpl[QC]
    @MacroSetting(debug = true) def dbg_ir[T](tree: T): IR[T, _] = macro QuasiMacros.quasicodeImpl[QC]
    
    def $[T,C](q: IR[T,C]*): T = macro QuasiMacros.forward$ // to conserve the same method receiver as for QQ (the real `Base`)
    //def $[T,C](q: IR[T,C]): T = macro QuasiMacros.forward$ // Actually unnecessary
    //def $[T,C](q: IR[T,C]*): T = macro QuasiMacros.forwardVararg$ // Actually unnecessary
    def $$[T](name: Symbol): T = macro QuasiMacros.forward$$
    
    type $[QT <: TypeErased] = TypeOf[QT]
    
  }
  object Quasicodes extends Quasicodes[DefaultQuasiConfig]
  object Quasiquotes extends Quasiquotes[DefaultQuasiConfig]
  
  // Note: could optimize cases with no key (currently encoded usign Unit) -- pur in a `mutable.HashMap[String,Rep]()`
  private val caches = mutable.HashMap[String,mutable.HashMap[Any,Rep]]()
  def cacheRep[K](id: String, key: K, mk: K => Rep): Rep = {
    caches.getOrElseUpdate(id,mutable.HashMap[Any,Rep]()).getOrElseUpdate(key,mk(key))
    //mk(key)  // toggle comment to see the effect of NOT caching
  }
  
}

object QuasiBase {
  
  /** Used to tag types generated for type holes, and to have a self-documenting name when the type is widened because of scope extrusion. */
  trait `<extruded type>`
  
}







