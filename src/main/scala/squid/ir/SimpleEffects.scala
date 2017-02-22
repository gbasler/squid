package squid
package ir

import utils._
import collection.mutable

/** Rudimentary (simplistic?) effect system for AST.
  * 
  * Methods and types are associated with binary "referential transparency" info.
  * The central approximation is that "referentially-transparent" methods are those which execution is referentially 
  * transparent as long as all arguments (including closures) passed to them have no effect (immediate or latent).
  * 
  * Latent effects are effects delayed by lambda abstraction. We only keep track one level of 'latency' (collapsing all further levels).
  * As a result, `(() => () => println)()` is currently considered impure, although the latent effect is not actually executed.
  * 
  * Referentially-transparent types are (immutable) types that own only referentially-transparent methods (except those
  * registered explicitly in `opaqueMtds`).
  * 
  * TODO add @read effects: can be dead-code removed, but not moved around
  */
trait SimpleEffects extends AST {
  
  // TODO synchronize accesses to these...?
  protected val transparentMtds = mutable.Set[MtdSymbol]()
  protected val opaqueMtds = mutable.Set[MtdSymbol]()
  protected val transparentTyps = mutable.Set[TypSymbol]()
  
  protected val purityPropagatingMtds = mutable.Set[MtdSymbol]()
  
  def isTransparentMethod(m: MtdSymbol): Bool = {
    transparentMtds(m) || !opaqueMtds(m) && {
      val r = ((transparentTyps(m.owner.asType) If (m.owner.isType) Else false) 
        || (m.overrides exists (s => s.isMethod && isTransparentMethod(s.asMethod)))
        || m.isAccessor && {val rst = m.typeSignature.resultType.typeSymbol; rst.isModule || rst.isModuleClass }
      )
      (if (r) transparentMtds else opaqueMtds) += m
      r
    }
  }
  
  def isTransparentType(m: MtdSymbol) = transparentMtds(m)
  
  /** Allows for caching effects in the `Rep`. Implement with just `effect(r)` for no caching to happen. */
  def effectCached(r: Rep): SimpleEffect
  def effect(r: Rep): SimpleEffect = dfn(r) match {
    case Abs(p,b) => (b|>effectCached).prev
    case MethodApp(s,m,ts,pss,rt) =>
      val propag = m |> purityPropagatingMtds
      if (propag || (m |> isTransparentMethod)) {
        val e = (s +: pss.flatMap(_.reps)).map(effectCached).fold(SimpleEffect.Pure)(_ | _)
        if (propag) e else e.next
      } else SimpleEffect.Impure
    case Ascribe(r,_) => r|>effectCached
    case Module(r,_,_) => r|>effectCached
    case Constant(_) | _: BoundVal | StaticModule(_) | NewObject(_) | RecordGet(_,_,_) | _:Hole | _:SplicedHole => SimpleEffect.Pure
  }
  
  import scala.reflect.runtime.{universe=>sru}
  // This one not in `StandardEffects` because it can be viewed as a fundamental implementation detail of the curry encoding: 
  purityPropagatingMtds ++= sru.typeOf[squid.lib.`package`.type].members.filter(_.name.toString startsWith "uncurried").map(_.asMethod)
  
}

// TODO don't actually recreate values all the time -- cache the 4 values!
case class SimpleEffect(immediate: Bool, latent: Bool) {
  def | (that: SimpleEffect) = SimpleEffect(immediate || that.immediate, latent || that.latent)
  def isBoth = immediate && latent
  def next = SimpleEffect(immediate || latent, latent)
  def prev = SimpleEffect(false, immediate)
}
object SimpleEffect {
  val Pure = SimpleEffect(false,false)
  val Impure = SimpleEffect(true,true)
}



/* TODO add types "scala.TupleX", method "scala.Predef.$conforms"
 * Note: should NOT make "squid.lib.Var.apply" trivial since it has to be let-bound for code-gen to work */
trait StandardEffects extends SimpleEffects {
  
  import scala.reflect.runtime.{universe=>sru}
  import reflect.runtime.universe.TypeTag
  
  def typeSymbol[T:TypeTag] = implicitly[TypeTag[T]].tpe.typeSymbol.asType
  def methodSymbol[T:TypeTag](name: String, index: Int = -1) = {
    val alts = implicitly[TypeTag[T]].tpe.member(sru.TermName(name)).alternatives.filter(_.isMethod)
    val r = if (alts.isEmpty) throw new IllegalArgumentException
      else if (alts.size == 1) alts.head
      else {
        require(index >= 0)
        alts(index)
      }
    r.asMethod
  }
  
  transparentTyps += sru.typeOf[squid.lib.`package`.type].typeSymbol.asType
  
  transparentTyps += typeSymbol[Int]
  transparentTyps += typeSymbol[Bool]
  transparentTyps += typeSymbol[String]
  
  // TODO should make these @read but not pure:
  //pureTyps += sru.typeOf[Any].typeSymbol.asType // for, eg, `asInstanceOf`, `==` etc.
  transparentMtds += methodSymbol[Any]("asInstanceOf")
  
  {
    def addFunTyp(tp: sru.Type) = {
      transparentTyps += tp.typeSymbol.asType
      opaqueMtds += tp.member(sru.TermName("apply")).asMethod
    }
    addFunTyp(sru.typeOf[Function0[Any]])
    addFunTyp(sru.typeOf[Function1[Any,Any]])
    addFunTyp(sru.typeOf[Function2[Any,Any,Any]])
    addFunTyp(sru.typeOf[Function3[Any,Any,Any,Any]])
  }
  
  transparentTyps += typeSymbol[scala.collection.immutable.Traversable[Any]]
  transparentTyps += typeSymbol[scala.collection.immutable.Seq[Any]]
  transparentTyps += typeSymbol[scala.collection.immutable.Seq.type]
  //pureTyps += typeSymbol[scala.collection.immutable.List[Any]].typeSymbol.asType // 
  transparentTyps += typeSymbol[scala.collection.immutable.List.type]
  
  transparentTyps += typeSymbol[scala.collection.generic.GenericCompanion[List]] // for the `apply`/`empty` methods
  
  // These are not correct, as mutable collections `size` is not referentially-transparent -- TODO use the @read effect
  /*
  {
    val typ = sru.typeOf[scala.collection.GenTraversableOnce[_]]
    pureMtds += typ.member(sru.TermName("size")).asMethod
  }
  
  {
    val typ = sru.typeOf[scala.collection.GenSeqLike[_,_]]
    pureMtds += typ.member(sru.TermName("length")).asMethod
  }
  */
  
  transparentTyps += typeSymbol[Option[Any]]
  transparentTyps += typeSymbol[Some.type]
  transparentTyps += typeSymbol[None.type]
  
  transparentTyps += typeSymbol[Either[Any,Any]]
  transparentTyps += typeSymbol[Left.type]
  transparentTyps += typeSymbol[Right.type]
  
}
