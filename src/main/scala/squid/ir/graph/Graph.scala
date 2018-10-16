package squid.ir
package graph

import squid.utils._
import squid.utils.CollectionUtils.MutSetHelper

import scala.collection.mutable

/* In the future, we may want to remove `with CurryEncoding` and implement proper multi-param lambdas... */
class Graph extends AST with CurryEncoding { graph =>
  
  object GraphDebug extends PublicTraceDebug
  
  //val edges = mutable.Map.empty[Rep,Def]
  val edges = mutable.Map.empty[Val,Def]
  //val edges = mutable.Map.empty[Val,Rep]
  
  def bind(v: Val, d: Def): Unit = {
    require(!edges.isDefinedAt(v))
    rebind(v, d)
  }
  def rebind(v: Val, d: Def): Unit = {
    require(!v.isInstanceOf[SyntheticVal])
    //require(!d.isInstanceOf[BoundVal] || d.isInstanceOf[SyntheticVal], s"$d")  // TODO enforce?
    edges += v -> d
  }
  def rebind(r: Rep, d: Def): r.type = rebind(r.bound, d) thenReturn r
  
  val Bottom = bindVal("⊥", Predef.implicitType[Nothing].rep, Nil)
  bind(Bottom,Bottom) // prevents rebinding of Bottom, just in case
  
  ////class Rep(v: Val)
  //type Rep = Val
  //class SyntheticVal(name: String, typ: TypeRep, annots: List[Annot]=Nil) extends BoundVal("@"+name)(typ, annots) {
  //  override def toString = {
  //    val d = dfnOrGet(this)
  //    if (d.isSimple) super.toString else s"${super.toString} = ${d}"
  //  }
  //}
  ////class Node(name: String)(typ: TypeRep, annots: List[Annot]=Nil) extends BoundVal("@"+name)(typ, annots) {
  ////  def get = edges(this)
  ////  //override def toString = if (get.isSimple) {
  ////  //  println(name,get,get.getClass)
  ////  //  get.toString
  ////  //} else super.toString
  ////}
  ////type Node = Rep
  ////type Node = Val
  //object Rep {
  //  def unapply(r: Rep): Def|>Option = edges get r
  //  //def unapply(r: Rep): Some[Def] = edges(r) into Some.apply
  //}
  
  // Synthetic vals are never supposed to be let-bound...?
  class SyntheticVal(name: String, typ: TypeRep, annots: List[Annot]=Nil) extends BoundVal("@"+name)(typ, annots) {
  //class SyntheticVal(name: String, typ: TypeRep, annots: List[Annot]=Nil) extends BoundVal("@"+name)(typ, annots) with Rep {
    //override def toString = {
    //  val d = dfnOrGet(this)
    //  if (d.isSimple) super.toString else s"${super.toString} = ${d}"
    //}
    def dfn = this
  }
  
  //sealed abstract class Rep {
  //sealed abstract trait Rep {
  //  def dfn: Def
  //}
  
  //override protected def freshNameImpl(n: Int) = "$"+n
  override protected def freshNameImpl(n: Int) = "@"+n
  
  //class Id
  type CtorSymbol = Class[_]
  
  //case class Expr(dfn: Def) extends Rep {
  //}
  //sealed abstract class Expr extends Rep
  //case class Ref(v: Val) extends Expr
  //class DelayedRef(v: Val) extends Expr
  //class Expr(getDfn: => Def) extends Rep {
  //  //lazy val bound: Val = 
  //  private var _bound: Val = _
  //  private def bind(v: Val) = {
  //    _bound = v
  //    edges += v -> getDfn
  //  }
  //  def asBoundBy(v: Val) = {
  //    assert(_bound === null)
  //    bind(v)
  //    this
  //  }
  //  def bound = {
  //    if (_bound === null) bind(freshBoundVal(dfn.typ))
  //    _bound
  //  }
  //}
  //class Expr(initialDef: Def) extends Rep {
  class Rep(val bound: Val) {
    require(!bound.isInstanceOf[SyntheticVal])
    def dfn: Def = edges.getOrElse(bound, bound)
    
    def isBottom = dfn === Bottom
    
    def iterator = graph.iterator(this)
    
    //def freeVals: Set[Val] = graph.freeVals(dfn)
    def freeVals: Set[Val] = iterator.collect{case Rep(v:Val) if !v.isInstanceOf[SyntheticVal] => v}.toSet
    
    override def equals(that: Any) = that match {
      case r: Rep => r.bound === bound
      case _ => false
    }
    override def hashCode = bound.hashCode
    
    override def toString = {
      val d = dfn
      if (d.isSimple) d.toString else s"$bound = $d"
    }
    def simpleString = 
      if (dfn.isSimple) dfn.toString
      //if (dfn.isSimple) s"‘$dfn $bound’"
      else bound.toString
  }
  //object Expr {
  object Rep {
    //def apply(d: Def) = new Rep(d)
    //def bound(v: Val, d: Def) = new Rep(d) {
    //  //_bound = v
    //  asBoundBy(v)
    //}
    //def bound(v: Val, d: Def) = new Rep(v,d)
    def bound(v: Val, d: Def) = {
      assert(!d.isInstanceOf[Val] || d.isInstanceOf[SyntheticVal])
      new Rep(v) alsoDo bind(v, d)
    }
    def unapply(e: Rep) = Some(e.dfn)
  }
  
  // TODO override reps and rebuild in these? (also, factor reps and children!)
  case class Call(cid: CallId, result: Rep) extends SyntheticVal("C"+cid, result.typ) {
  //case class Call(call: Id, result: Rep) extends Rep {
    
    // Note that 'unboundVals' won't see this (good, because since it's caching it would be wrong),
    // because it matches on BoundVal and only calls children in the default case...
    override def children: Iterator[Rep] = Iterator.single(result)
  }
  ////case class Arg(nodes: mutable.Map[Option[Id], Rep]) extends SyntheticVal("C") {
  case class Arg(cid: CallId, cbr: Rep, els: Rep) extends SyntheticVal("A"+cid, cbr.typ) {
  //case class Arg(cid: Id, cbr: Rep, els: Option[Rep]) extends Rep {
    
    override def children: Iterator[Rep] = Iterator(cbr,els)
  }
  // TODO Q: should this one be encoded with a normal MethodApp? -> probably not..
  case class Split(scrut: Rep, branches: Map[CtorSymbol, Rep]) extends SyntheticVal("S", branches.head._2.typ) {
  //case class Split(scrut: Rep, branches: Map[CtorSymbol, Rep]) extends Rep {
    
    override def children: Iterator[Rep] = Iterator.single(scrut) ++ branches.valuesIterator
  }
  
  //override protected def unboundVals(d: Def): Set[Val] = d match {
  //  case Call(cid, res) => res.dfn.unboundVals
  //  case Arg(cid, cbr, els) => cbr.dfn.unboundVals ++ els.dfn.unboundVals
  //  case _:SyntheticVal => ??? // TODO
  //  case _ => super.unboundVals(d)
  //}
  override protected def unboundVals(d: Def): Set[Val] = ??? // to make sure not used, until we make 'dfn' opaque again
  
  /** contrary to unboundVals and its synonym freeVariables, this does not cache (incompatible with mutability of the
    * graph), and goes through Arg/Call/Split nodes. It's still fine to use unboundVals, but it will only give the
    * variables bound by the Rep's immediately used by the Def (which are immutable and can be cached) */
  //def freeVals(d: Def): Set[Val] = d match {
  //  case v: Val if !v.isInstanceOf[SyntheticVal] => Set.single(v)
  //  case Abs(v,body) => body.freeVals - v
  //  case bd: BasicDef => bd.children.flatMap(_.freeVals).toSet
  //}
  //def freeVals(d: Def): Set[Val] = iterator(d).collect{case Rep(v:Val) => v}.toSet
  
  //override def prettyPrint(d: Def) = d match {
  //  case Node(d) if d.isSimple =>
  //    ???
  //    prettyPrint(d)
  //  case _ => super.prettyPrint(d)
  //}
  override def prettyPrint(d: Def) = (new DefPrettyPrinter)(d)
  class DefPrettyPrinter extends super.DefPrettyPrinter {
    val printed = mutable.Set.empty[Rep]
    override val showValTypes = false
    override val desugarLetBindings = false
    override def apply(r: Rep): String = printed.setAndIfUnset(r, r match {
      case Rep(d) if d.isSimple => apply(d)
      //case _ => super.apply(r)
      case _ => super.apply(r.bound)
    }, super.apply(r.bound))
    //override def apply(d: Def): String = if (get.isSimple) {
    override def apply(d: Def): String = d match {
      case Call(cid, res) => s"C[$cid](${res |> apply})"
      case Arg(cid, cbr, els) => s"$cid->${cbr |> apply}" + 
        //(if (els.isBottom) "" else "|"+apply(els))
        "|"+apply(els)
      case _:SyntheticVal => ??? // TODO
      case _ => super.apply(d)
    }
  }
  
  // Implementations of AST methods:
  
  //def rep(dfn: Def) = Rep(dfn)
  def rep(dfn: Def) =
    //postProcess(new Rep(dfn)) // would force the Rep too early (before it's let bound), resulting in binder duplication...
    //new Rep(freshBoundVal(dfn.typ), dfn)
    dfn match {
      //case bv: BoundVal =>
      case bv: BoundVal if !bv.isInstanceOf[SyntheticVal] =>
        new Rep(bv)
      case _ =>
        val v = freshBoundVal(dfn.typ)
        bind(v, dfn)
        new Rep(v)
    }
  //def rep(dfn: Def) = dfn match {
  //  case v: Val => v
  //  //case _ => freshBoundVal(dfn.typ) alsoApply {edges += _ -> dfn}
  //  case _ => new SyntheticVal(freshName.tail, dfn.typ) also {edges += _ -> dfn}
  //}
  
  //def dfn(r: Rep) = edges(r)
  //def dfn(r: Rep) = r match { case Node(d) => d  case bv => bv }
  //def dfn(r: Rep): Def = r
  
  def dfn(r: Rep): Def = r.dfn  // TODO make it opaque so it's not seen by other infra?
  //def dfn(r: Rep): Def = r.bound
  
  //def dfnOrGet(r: Rep) = r match { case Rep(d) => d  case bv => bv }
  
  def repType(r: Rep) = r|>dfn typ
  
  //override def showRep(r: Rep) = r match {
  //  //case Node(_:NonTrivialDef) => super.showRep(r)
  //  //case Node(d) => 
  //  case Node(d) if r.isSimple => 
  //    println(d,d.getClass)
  //    d.toString
  //  case _ => super.showRep(r)
  //}
  //override def showRep(r: Rep) = if (r.isSimple) sh
  
  //override def showRep(r: Rep) = {
  def showGraph(r: Rep) = {
    //val printed = mutable.Set.empty[Rep] // TODO rm
    
    //println(iterator(r).toList)
    //showGraph(r)
    //iterator(r).collect{ case nde @ Node(_: NonTrivialDef|_: Rep) if !printed(nde) =>
    //iterator(r).collect{ case nde @ Node(_: NonTrivialDef|_: Node) if !printed(nde) =>
    //iterator(r).collect{ case nde @ Rep(d: NonTrivialDef) if {assert(!printed(nde));!printed(nde)} =>
    //iterator(r).collect{ case nde @ Rep(d) if !d.isSimple && {assert(!printed(nde));!printed(nde)} =>
    //  printed(nde) = true
    //iterator(r).toList.reverse.collect { case nde @ Rep(d) if !d.isSimple =>
    reverseIterator(r).collect { case nde @ Rep(d) if !d.isSimple =>
      //nde.toString
      //s"$nde = ${nde|>dfn}"
      //s"$nde = ${nde.get}"
      s"${nde.bound} = ${d};\n"
    //}.toList.reverse.mkString(";\n")
    }.mkString + r.simpleString
  }
  def showGraphRev(r: Rep) = r.simpleString + {
    val defsStr = iterator(r).collect { case nde @ Rep(d) if !d.isSimple => s"\n\t${nde.bound} = ${d};" }.mkString
    if (defsStr.isEmpty) "" else " where:" + defsStr
  }
  //def showGraph(r: Rep) = {
  //}
  def iterator(r: Rep): Iterator[Rep] = mkIterator(r)(false,mutable.HashSet.empty)
  def reverseIterator(r: Rep): Iterator[Rep] = mkIterator(r)(true,mutable.HashSet.empty)
  def mkIterator(r: Rep)(implicit rev: Bool, done: mutable.HashSet[Val]): Iterator[Rep] =
    //done.setAndIfUnset(r, Iterator.single(r) ++ mkDefIterator(dfnOrGet(r)), Iterator.empty)
    done.setAndIfUnset(r.bound, /*println(s"ite ${r.bound}  ${done}") thenReturn*/ {
      if (rev) mkDefIterator(r.dfn) ++ Iterator.single(r) else Iterator.single(r) ++ mkDefIterator(r.dfn)
    } /*alsoDo println(s"DONE ite ${r.bound}  ${done}")*/, Iterator.empty)
  def mkDefIterator(dfn: Def)(implicit rev: Bool, done: mutable.HashSet[Val]): Iterator[Rep] = dfn match {
  //def mkDefIterator(dfn: Def)(implicit rev: Bool, done: mutable.HashSet[Val]): Iterator[Rep] = if (done.contains(dfn)) Iterator.empty else dfn match {
  //  case v:Val if done.contains(v) => Iterator.empty // probably not useful
    case MethodApp(self, mtd, targs, argss, tp) =>
      mkIterator(self) ++ argss.flatMap(_.reps.flatMap(mkIterator))
    case Abs(_, b) => mkIterator(b)
    case Ascribe(r, _) => mkIterator(r)
    case Module(r, _, _) => mkIterator(r)
    //case Rep(d) => mkDefIterator(d) // TODO rm?
    case Call(cid, res) =>
      mkIterator(res)
    case Arg(cid, cbr, els) =>
      mkIterator(cbr) ++ mkIterator(els)
    case _:SyntheticVal => ??? // TODO
    case _: LeafDef => Iterator.empty
    //case Constant(_)|BoundVal(_)|CrossStageValue(_, _)|HoleClass(_, _)|StaticModule(_) => Iterator.empty
    //case Abs(_, _) | Ascribe(_, _) | MethodApp(_, _, _, _, _) | Module(_, _, _) | NewObject(_) | SplicedHoleClass(_, _) => ???
  }
  
  
  override def letin(bound: BoundVal, value: Rep, body: => Rep, bodyType: TypeRep) =
    //??? // oops, need a Node here
    //{edges += bound -> value} thenReturn body
    //new Rep(bound, value.dfn) thenReturn body
    bind(bound, value.dfn) thenReturn body
    //body alsoDo rebind(bound, value.dfn)
  
  
  // TODO use the special interpretation of lambda params trick:
  //   A variable bound by a lambda can be bound in the graph; its interpretation depends on whether we're looking at it
  //   via the lambda or not.
  //   This reduces the number of necessary call/args, and avoids the false sharing we have with the ucrrent scheme
  //     if (isBound(v)) rebind else bind ...
  //   It should also allow us to do substitution without rebuilding the whole body! (currently needed because we can't
  //     really replace a variable Rep with a Rep arg'ing that variable, or it would become cyclic)
  override def substituteVal(r: Rep, v: BoundVal, mkArg: => Rep): Rep = {
    /*
    //println(s"Subs $v in ${r.bound}")
    val cid = new CallId("α")
    var occurs = false
    val subsd = super.substituteVal(r, v, {occurs = true; Arg(cid, mkArg, v |> readVal) |> rep})
    val res = if (occurs) Call(cid, subsd) |> rep else r
    //println(s"Subs yield: ${res.showGraphRev}")
    res
    */
    
    
    // Kinda wrong: doesn't check for occurrences in 'mkArg'
    // but maybe in the graph IR we can assume it won't be a problem since we have more stringent requirements for the
    // use of bindings (due to the global 'egdes' map)
    // Also, seems to cause a SOF in the tests
    
    /*
    // less safe but much more efficient:
    //val occurs = r.dfn.unboundVals contains v
    val occurs = freeVals(r.dfn) contains v
    if (occurs) {
      val cid = new CallId("α")
      rebind(v, Arg(cid, mkArg, v |> readVal))
      Call(cid, r) |> rep
    } else r
    */
    // ^ this version is kinda dumb: it creates a cycle that makes the Reinterpreter SOF
    
    val occurs = r.freeVals contains v
    //println(occurs)
    if (occurs) {
      val cid = new CallId("α")
      //val arg = mkArg
      val arg = Arg(cid, mkArg, v |> readVal)
      
      val argr = arg |> rep
      //val subsd = substituteValFastUnhygienic(r, v, Arg(cid, arg, v |> readVal) |> rep)
      val subsd = substituteValFastUnhygienic(r, v, argr)
      Call(cid, subsd) |> rep
      
      ////iterator(r).foreach {
      //iterator(r).toArray.foreach {
      //  //case r @ Rep() =>
      //  //case r => if (r.bound === v) rebind(r, arg)
      //  case r => if (r.dfn === v && r.bound =/= v) rebind(r, arg)
      //}
      //Call(cid, r) |> rep
    } else r
    
    
    // obsolete stub:
    /*
    val arg = try mkArg
    catch { case e: Throwable =>
      System.err.println(s"Arg construction did not complete for substitution of variable $v." +
        s" Note that the Graph IR does not support speculative rewriting as of yet.")
    }
    val occurs = r.dfn.unboundVals contains v
    if (occurs) {
      val cid = new CallId("α")
      //rebind(v, Arg(cid, mkArg, Some(v |> readVal)))
      //Call(cid, r) |> rep
      def rec(r: Rep): Unit = r.dfn match {
        case Arg(c,v) => 
      }
      rec(r)
      Call(cid, r) |> rep
    } else r
    */
  }
  override def mapDef(f: Def => Def)(r: Rep) = {
    val d = r.dfn
    val newD = f(d)
    //println(s"..mapD $d>$newD")
    rebind(r, newD)
  }
  override protected def mapRep(rec: Rep => Rep)(d: Def) = d match {
    case Arg(cid,cbr,els) =>
      val cbr2 = rec(cbr)
      val els2 = rec(els)
      //println(s"..mapR $cbr>$cbr2 $els>$els2")
      if ((cbr2 eq cbr) && (els2 eq els)) d
      else Arg(cid,cbr2,els2)
    case Call(cid,res) =>
      val res2 = rec(res)
      if (res2 eq res) d
      else Call(cid,res2)
    case _ => super.mapRep(rec)(d)
  }
  //protected def mapRep_!(rec: Rep => Rep) = {
  //  val f = mapRep{ r => val r2 = rec(r); if (r neq r2)  }
  //  (d: Def) =>
  //}
  //override def extractVal(r: Rep) = Some(r.bound)
  override def extractVal(r: Rep) = super.extractVal(r)//.orElse(Some(r.bound))
  
  def isNormalVal(d: Def): Bool = d.isInstanceOf[BoundVal] && !d.isInstanceOf[SyntheticVal]
  def isNormalVal(r: Rep): Bool = r.dfn |> isNormalVal
  
  //type XCtx = Set[CallId]
  //def newXCtx: XCtx = Set.empty
  //type XCtx = (Set[CallId],mutable.Set[CallId])
  //def newXCtx: XCtx = (Set.empty,mutable.Set.empty)
  import squid.lib.MutVar
  //type XCtx = (Set[CallId],MutVar[Set[CallId]])
  //def newXCtx: XCtx = (Set.empty,MutVar(Set.empty))
  //type XCtx = (Set[CallId], MutVar[(Set[CallId], () => Unit)])
  //type XCtx = (Set[CallId],Set[CallId],MutVar[(Rep,Rep) => Rep])
  type XCtx = (Set[CallId],Set[CallId],MutVar[(Rep,Rep) => Rep],MutVar[List[Rep]])
  //def newXCtx: XCtx = (Set.empty,MutVar(identity))
  def newXCtx: XCtx = (Set.empty,Set.empty,MutVar((r,_)=>r),MutVar(Nil))
  
  /*
  override def rewriteRep(xtor: Rep, xtee: Rep, code: Extract => Option[Rep]): Option[Rep] = {
    
    def rec(xtor: Rep, xtee: Rep) = xtor.dfn -> xtee.dfn match {
      case Constant(v1) -> Constant(v2) if v1 == v2 => 
    }
    rec(xtor, xtee)
    
    ???
    
  }
  */
  
  //val transformed = mutable.Set.empty[(Rep,Extract => Option[Rep])]
  protected val transformed = mutable.Set.empty[(Rep,List[Rep])] // FIXedME
  // ^ TODO make it a weak hashmap with the Rep xtor as the key... or even just a weak hash set?
  // ^ FIXME actually too restrivtive: if it finds ONE POSSIBLE match and does the rewrite, next time it will always
  //     find the SAME match and not do any rewriting, but another match may have worked!
  //     We'd actually need to build a _stream_ of possible matches, and filter it with 'transformed'
  //     Could we make that part of the XCtx?
  
  override def spliceExtract(xtor: Rep, args: Args)(implicit ctx: XCtx) = ??? // TODO
  override def extract(xtor: Rep, xtee: Rep)(implicit ctx: XCtx) = ???
  override def rewriteRep(xtor: Rep, xtee: Rep, code: Extract => Option[Rep]): Option[Rep] = {
    
    val matches = extractGraph(xtor, xtee)((Set.empty,Set.empty,Map.empty)) flatMap
      (_ merge (GraphExtract fromExtract repExtract(SCRUTINEE_KEY -> xtee)))
    //flatMap {
    //  case extr =>
    //    val reps = ctx._4.!
    //    if (transformed contains xtor->reps) None else {
    //      println(s"...considering $xtor << ${reps.map(_.bound)} --> ${extr}")
    //      //val x = ctx._3.!(x0,xtee)
    //      ////transformed += ((xtor,xtee))
    //      //Some(x)
    //      //code(x0) map ctx._3.! into_? {
    //      //  case Some(x) => transformed += ((xtor,reps)); x
    //      //}
    //      code(extr) |>? {
    //        case Some(x0) =>
    //          println(s"...transforming ${xtor.bound} << ${ctx._4.!.map(_.bound)}")
    //          val x = ctx._3.!(x0,xtee)
    //          transformed += ((xtor,reps))
    //          x
    //      }
    //    }
    //}
    matches.filterNot(transformed contains xtor -> _.traversedReps).flatMap { ge =>
      println(s"...considering $xtor << ${ge.traversedReps.map(_.bound)} --> ${ge.extr}")
      code(ge.extr) |>? {
        case Some(x0) =>
          println(s"...transforming ${xtor.bound} << ${ge.traversedReps.map(_.bound)}")
          val x = ge.postProcess(x0,xtee)
          transformed += (xtor -> ge.traversedReps)
          x
      }
    }.headOption
    //matches.map { ge =>
    //  if (transformed contains xtor -> ge.traversedReps)
    //}
    
  }
  
  protected case class GraphExtract(extr: Extract, traversedReps: List[Rep], postProcess: (Rep,Rep) => Rep) {
    def merge(that: GraphExtract): Stream[GraphExtract] =
      graph.merge(extr,that.extr).map(e =>
        //GraphExtract(e, traversedReps ++ that.traversedReps, postProcess andThen that.postProcess)).toStream
        GraphExtract(e, traversedReps ++ that.traversedReps, (r,o) => that.postProcess(postProcess(r,o),o))).toStream
    //def map(f: Extract => Extract): GraphExtract = ???
    //def flatMap(f: Extract => GraphExtract): GraphExtract = ???
    def matching (r: Rep) = r.dfn match {
      case sv: SyntheticVal => this
      case _ => copy(traversedReps = r :: traversedReps)
    }
  }
  object GraphExtract {
    //val Empty: GraphExtract = GraphExtract(EmptyExtract, Nil, identity)
    val Empty: GraphExtract = GraphExtract(EmptyExtract, Nil, (r,_)=>r)
    def fromExtract(e: Extract): GraphExtract = Empty copy (extr = e)
  } 
  //protected implicit class GraphExtractStreamOps(self: Stream[GraphExtract]) {
  //  def flatMap(f: Extract => Stream[GraphExtract]): Stream[GraphExtract] =
  //    //self.flatMap(_.flatMap(f))
  //    //self.flatMap(ge => ge.flatMap(f))
  //    for { a <- self; b <- f(a) } yield b
  //}
  protected def streamSingle[A](a: A): Stream[A] = a #:: Stream.Empty
  
  //type GXCtx = (Set[CallId], Set[CallId])
  type GXCtx = (Set[CallId], Set[CallId], Map[Val,Val])
  
  def extractGraph(xtor: Rep, xtee: Rep, extractHole: Bool = true)(implicit ctx: GXCtx): Stream[GraphExtract] = {
    import GraphExtract.fromExtract
    
    xtor.dfn -> xtee.dfn match {
       
      case (_, Ascribe(v,tp)) => // Note: even if 'this' is a Hole, it is needed for term equivalence to ignore ascriptions
        //mergeOpt(typ extract (tp, Covariant), extractImpl(v))
        /** ^ It should not matter what the matchee is ascribed to. We'd like `42` and `42:Any` to be equi-matchable */
        extractGraph(xtor,v)
        
      //case (Ascribe(v,tp), _) =>
      //  mergeOpt(tp extract (d.typ, Covariant), v.extract(r))
        
      //case (h:HOPHole, _) =>
      //  typ extract (d.typ, Covariant) flatMap { e =>
      //    val List(ps) = h.yes // FIXME: generalize
      //    // This part is a little tricky: we have no easy way to retrieve the types of HOPV parameters...
      //    // if some of these parameters do not even appear in the term, we give them type Any, which is sound thanks 
      //    // to function parameter contravariance.
      //    val psMap = mutable.Map[Val,Val]().withDefault(hs => hs.copy()(typ = Predef.implicitType[Any].rep,hs.annots))
      //    val rebound = bottomUpPartial(r) {
      //      case RepDef(h0:Hole) if h0.matchedSymbol exists (ps contains _) => 
      //        psMap += h0.matchedSymbol.get -> h0.originalSymbol.get
      //        readVal(h0.originalSymbol.get)
      //      case RepDef(h0:Hole) if h0.matchedSymbol exists (h.no contains _) =>
      //        return None
      //    }
      //    val f = lambda(ps map psMap, rebound)
      //    merge(e, repExtract(h.name -> f))
      //  }
      //  
      //case (Hole(name), _) => // Note: will also extract holes... which is important to asses open term equivalence
      //  // Note: it is not necessary to replace 'extruded' symbols here, since we use Hole's to represent free variables (see case for Abs)
      //  typ extract (d.typ, Covariant) flatMap { merge(_, (Map(name -> r), Map(), Map())) }
      case (Hole(name), _) if extractHole =>
        val directly = for {
          typE <- xtor.typ.extract(xtee.typ, Covariant).toStream
          
          //e <- merge(typE, repExtract(name -> xtee))
          
          //(rs,ts,rss) <- merge(typE, repExtract(name -> xtee))
          //r1 = rs(name)
          //r2 = ctx._1.foldRight(r1)(Call(_,_).toRep)
          //r3 = ctx._2.foldRight(r2)(Call(_,_).toRep)
          //e = (rs + (name -> r3),ts,rss)
          
          r1 = xtee
          r2 = ctx._1.foldRight(r1)(Call(_,_).toRep)
          r3 = ctx._2.foldRight(r2)(Call(_,_).toRep)
          e <- merge(typE, repExtract(name -> r3))
          
        } yield GraphExtract fromExtract e
        val inspecting = extractGraph(xtor,xtee,extractHole=false)
        directly ++ inspecting
        
      case _ -> Call(cid, res) if !(ctx._1 contains cid) =>
        extractGraph(xtor, res)((ctx._1, ctx._2 + cid, ctx._3))
      case _ -> Arg(cid, cbr, _) if ctx._2 contains cid =>
        extractGraph(xtor, cbr)((ctx._1, ctx._2 - cid, ctx._3))
      case _ -> Arg(cid, cbr, els) =>
        //(for { cbrE <- extractGraph(xtor, cbr)((ctx._1 + cid, ctx._2)); elsE <- extractGraph(xtor, els) } yield
        //for { cbrE <- cbrE; elsE <- elsE } yield merge(cbrE,elsE)).flatten
        for {
          cbrE <- extractGraph(xtor, cbr)((ctx._1 + cid, ctx._2, ctx._3)).map { ge =>
            ge.copy(postProcess = (res,orig) => Arg(cid,ge.postProcess(res,orig),orig).toRep)
          }
          elsE <- extractGraph(xtor, els)
          e <- cbrE merge elsE
        } yield e
        
      //case (bv @ BoundVal(n1), h @ Hole(n2)) if n1 == n2 && h.matchedSymbol == Some(bv) => // This is needed when we do function matching (see case for Abs); n2 is to be seen as a FV
      //  //Some(EmptyExtract) // I thought the types could not be wrong here (case for Abs checks parameter types)
      //  // Before we had `matchedSymbol`, matching extracted binders could cause problems if we did not check the type; it's probably not necessary anymore
      //  typ.extract(d.typ, Covariant)
      //  
      //// Q: really still needed?
      //case (bv: BoundVal, h: Hole) if h.originalSymbol exists (_ === bv) => // This is needed when we match an extracted binder with a hole that comes from that binder
      //  Some(EmptyExtract)
      //  
      //case (_, Hole(_)) => None
      //  
      //case (v1: BoundVal, v2: BoundVal) =>
      //  // Bound variables are never supposed to be matched;
      //  // if we match a binder, we'll replace the bound variable with a free variable first
      //  //throw new AssertionError("Bound variables are not supposed to be matched.")
      //  
      //  // actually now with extracted bindings they may be...
      //  if (v1 == v2) Some(EmptyExtract) else None // check same type?
      //  
      //  //if (v1.name == v2.name) Some(EmptyExtract) else None // check same type?
      case (v1: BoundVal, v2: BoundVal) =>
        // Q: check same type?
        if (
           v1 == v2 // Q: really legit?
        || ctx._3.get(v1).contains(v2)
        ) EmptyExtract |> fromExtract |> streamSingle else Stream.Empty
      //  
      ///** It may be okay to consider constants `1` (Int) and `1.0` (Double) equivalent;
      //  * however, it's probably a good idea to ensure transitivity of the match-relation, so that (a <~ b and b <~ c) => a <~ c
      //  * but we'd have 1 <~ 1.0 and 1.0 <~ ($x:Double) but not 1 <~ ($x:Double) */
      ////case (Constant(v1), Constant(v2)) => if (v1 == v2) Some(EmptyExtract) else None
      //case (Constant(v1), Constant(v2)) => mergeOpt(extractType(typ, d.typ, Covariant), if (v1 == v2) Some(EmptyExtract) else None)
      //  
      //case (a1: Abs, a2: Abs) =>
      //  // The body of the matched function is recreated with a *free variable* in place of the parameter, and then matched with the
      //  // body of the matcher, so what the matcher extracts contains potentially (safely) extruded variables.
      //  for {
      //    pt <- a1.ptyp extract (a2.ptyp, Contravariant)
      //    //b <- a1.body.extract(a2.inline(rep(a2.param.toHole(a1.param.name))))
      //    (hExtr,h) = a2.param.toHole(a1.param)
      //    b <-
      //      if (a1.param.isExtractedBinder && newExtractedBindersSemantics) 
      //           a1.inline(a2.param|>readVal).extract(a2.body) flatMap (merge(hExtr, _))
      //           // ^ (generates warning cf type change)  TODO: thread extraction context with var mapping instead
      //      else a1.body.extract(a2.inline(rep(h))) flatMap (merge(hExtr, _))
      //           // ^ 'a2.param.toHole' is a free variable that 'retains' the memory that it was bound to 'a2.param'
      //    m <- merge(pt, b)
      //  } yield m
      case (a1: Abs, a2: Abs) =>
        require(a1.param.isExtractedBinder, s"alternative not implemented yet")
        for {
          pt <- a1.ptyp.extract(a2.ptyp, Contravariant).toStream //map fromExtract
          (hExtr,h) = a2.param.toHole(a1.param)
          //m <- mergeGraph(pt, hExtr |> fromExtract)
          m <- merge(pt, hExtr).toStream
          b <- extractGraph(a1.body, a2.body)((ctx._1, ctx._2, ctx._3 + (a1.param -> a2.param)))
          m <- mergeGraph(m |> fromExtract, b)
        } yield m
      //  
      case (StaticModule(fullName1), StaticModule(fullName2)) if fullName1 == fullName2 =>
        fromExtract(EmptyExtract) into streamSingle
      //
      //case Module(pref0, name0, tp0) -> Module(pref1, name1, tp1) =>
      //  // Note: if prefixes are matchable, module types should be fine
      //  // If prefixes are different _but_ type is the same, then it should be the same module!
      //  // TODO also cross-test with ModuleObject, and ideally later MethodApp... 
      //  pref0 extract pref1 flatMap If(name0 == name1) orElse extractType(tp0,tp1,Invariant)
      //  
      //case (NewObject(tp1), NewObject(tp2)) => GraphExtract fromExtract (tp1 extract (tp2, Covariant)) into streamSingle
      case (NewObject(tp1), NewObject(tp2)) => tp1 extract (tp2, Covariant) map fromExtract toStream
      //  
      //case (MethodApp(self1,mtd1,targs1,args1,tp1), MethodApp(self2,mtd2,targs2,args2,tp2))
      //  //if mtd1 == mtd2
      //  //if {val r = mtd1 == mtd2; debug(s"Symbol: ${mtd1.fullName} ${if (r) "===" else "=/="} ${mtd2.fullName}"); r}
      //  if mtd1 === mtd2 || { debug(s"Symbol: ${mtd1.fullName} =/= ${mtd2.fullName}"); false }
      //=>
      //  assert(args1.size == args2.size, s"Inconsistent number of argument lists for method $mtd1: $args1 and $args2")
      //  assert(targs1.size == targs2.size, s"Inconsistent number of type arguments for method $mtd1: $targs1 and $targs2")
      //  
      //  for {
      //    s <- self1 extract self2
      //    t <- {
      //      /** The following used to end with '... extract (b, Variance of p.asType)'.
      //        * However, method type parameters seem to always be tagged as invariant.
      //        * This was kind of restrictive. For example, you could not match apply functions like "Seq()" with "Seq[Any]()"
      //        * We now match method type parameters covariantly, although I'm not sure it is sound. At least the fact we
      //        * now also match the *returned types* prevents obvious unsoundness sources.
      //        */
      //      //mergeAll( (targs1 zip targs2 zip mtd1.typeParams) map { case ((a,b),p) => a extract (b, Covariant) } )
      //      mergeAll( (targs1 zip targs2) map { case (a,b) => a extract (b, Covariant) } )
      //    }
      //    a <- mergeAll( (args1 zip args2) map { case (as,bs) => extractArgList(as, bs) } )  //oh_and print("[Args:] ") and println
      //  
      //    /** It should not be necessary to match return types, knowing that we already match all term and type arguments.
      //      * On the other hand, it might break legitimate things like (()=>42).apply():Any =~= (()=>42:Any).apply(),
      //      * in which case the return type of .apply is Int in the former and Any in the latter, but everything else is equivalent */
      //    //rt <- tp1 extract (tp2, Covariant)  //oh_and print("[RetType:] ") and println
      //    rt = EmptyExtract
      //  
      //    m0 <- merge(s, t)
      //    m1 <- merge(m0, a)
      //    m2 <- merge(m1, rt)
      //  } yield m2
      case (MethodApp(self1,mtd1,targs1,args1,tp1), MethodApp(self2,mtd2,targs2,args2,tp2))
        if mtd1 === mtd2 || { debug(s"Symbol: ${mtd1.fullName} =/= ${mtd2.fullName}"); false }
      =>
        assert(args1.size == args2.size, s"Inconsistent number of argument lists for method $mtd1: $args1 and $args2")
        assert(targs1.size == targs2.size, s"Inconsistent number of type arguments for method $mtd1: $targs1 and $targs2")
        for {
          s <- extractGraph(self1,self2)
          // TODO:
          t = GraphExtract fromExtract EmptyExtract
          //t <-
          //  //mergeAll( (targs1 zip targs2 zip mtd1.typeParams) map { case ((a,b),p) => a extract (b, Covariant) } )
          //  mergeAll( (targs1 zip targs2) map { case (a,b) => a extract (b, Covariant) } )
          a <- mergeAllGraph( (args1 zip args2) map { case (as,bs) => extractGraphArgList(as, bs) } )  //oh_and print("[Args:] ") and println
          rt = GraphExtract fromExtract EmptyExtract
          m0 <- mergeGraph(s, t)
          m1 <- mergeGraph(m0, a)
          m2 <- mergeGraph(m1, rt)
        } yield m2
        
      case (a:ConstantLike) -> (b:ConstantLike) if a.value === b.value => GraphExtract.Empty #:: Stream.Empty
        
      //  // TODO?
      //case (or: OtherRep, r) => or extractRep r
      //case (r, or: OtherRep) => or getExtractedBy r
        
      case _ =>
        //println(s"Nope: ${xtor} << $xtee")
        Stream.Empty
    }
    
  }.map(_ matching xtee)
  
  protected def mergeGraph(lhs: GraphExtract, rhs: GraphExtract)(implicit ctx: GXCtx): Stream[GraphExtract] = lhs merge rhs
  //protected def extractGraphArgList(xtor: ArgList, xtee: ArgList)(implicit ctx: GXCtx): Stream[GraphExtract] = {
  protected def extractGraphArgList(self: ArgList, other: ArgList)(implicit ctx: GXCtx): Stream[GraphExtract] = {
    def extractRelaxed(slf: Args, oth: Args): Stream[GraphExtract] = {
      import slf._
      if (reps.size != oth.reps.size) return Stream.Empty
      val args = (reps zip oth.reps) map { case (a,b) => extractGraph(a, b) }
      //(streamSingle(EmptyExtract |> GraphExtract.fromExtract) /: args) {
      (streamSingle(GraphExtract.Empty) /: args) {
        case (acc, a) => for (acc <- acc; a <- a; m <- acc merge a) yield m }
    }
    import self._
    (self, other) match {
      case (a0: Args, a1: Args) =>
        require(reps.size == other.reps.size)
        extractRelaxed(a0,a1)
      case (ArgsVarargs(a0, va0), ArgsVarargs(a1, va1)) => for {
        a <- extractGraphArgList(a0, a1)
        va <- extractRelaxed(va0,va1)
        m <- mergeGraph(a, va)
      } yield m
      case (ArgsVarargSpliced(a0, va0), ArgsVarargSpliced(a1, va1)) => for {
        a <- extractGraphArgList(a0, a1)
        va <- extractGraph(va0, va1)
        m <- mergeGraph(a, va)
      } yield m
      case (ArgsVarargSpliced(a0, va0), ArgsVarargs(a1, vas1)) => for { // case dsl"List($xs*)" can extract dsl"List(1,2,3)"
        a <- extractGraphArgList(a0, a1)
        va <- spliceExtractGraph(va0, vas1)
        m <- mergeGraph(a, va)
      } yield m
      case _ => Stream.Empty
    }
  }
  def spliceExtractGraph(xtor: Rep, args: Args)(implicit ctx: GXCtx): Stream[GraphExtract] = ??? // TODO
  
  protected def mergeAllGraph(as: TraversableOnce[Stream[GraphExtract]]): Stream[GraphExtract] = {
    if (as isEmpty) return streamSingle(GraphExtract.Empty)

    //as.reduce[Option[Extract]] { case (acc, a) => for (acc <- acc; a <- a; m <- merge(acc, a)) yield m }
    /* ^ not good as it evaluates all elements of `as` (even if it's an Iterator or Stream) */

    val ite = as.toIterator
    var res = ite.next()
    while(ite.hasNext && res.nonEmpty)
      //res = res flatMap (_ merge ite.next())
      res = for { a <- res; b <- ite.next(); m <- a merge b } yield m
    res
    //???
  }
  
  /*
  override def extract(xtor: Rep, xtee: Rep)(implicit ctx: XCtx) = xtor.dfn match {
  case h @ Hole(name) => // TODO other forms of holes!! HOPHole, SplicedHole...
    val oldCtx_4 = ctx._4.!
    ctx._4 := xtee :: ctx._4.!
    super.extract(xtor, xtee) match {
      case Some((rs,ts,rss)) =>
        val r1 = rs(name)
        val r2 = ctx._1.foldRight(r1)(Call(_,_).toRep)
        val r3 = ctx._2.foldRight(r2)(Call(_,_).toRep)
        Some(rs + (name -> r3),ts,rss)
      case None => ctx._4 := oldCtx_4; None
    }
  case _ =>
    //println(s"${xtor.bound} << $xtee")
    val oldCtx_3 = ctx._3.!
    val oldCtx_4 = ctx._4.!
    val res = xtee.dfn match {
      case Call(cid, res) if !ctx._1.contains(cid) =>
        //super.extract(xtor, res)((ctx._1, ctx._2 + cid, ctx._3))
        extract(xtor, res)((ctx._1, ctx._2 + cid, ctx._3, ctx._4))
      //case Arg(cid, cbr, els) => super.extract(xtor, cbr)(ctx + cid) orElse super.extract(xtor, els)
        /*
      case Arg(cid, cbr, els) =>
        val cbrE = super.extract(xtor, cbr)((ctx._1 + cid, ctx._2 - cid, ctx._3))
        //if (cbrE.isDefined) ctx._3 := ctx._3.! + cid
        if (cbrE.isDefined && !ctx._2.contains(cid)) ctx._3 := { (res: Rep, orig: Rep) =>
          rebind(xtee, els.dfn)
          Arg(cid,oldCtx_2(res,orig),orig).toRep
        }
        cbrE orElse super.extract(xtor, els)
        */
      case Call(_, _) => None
      case Arg(cid, cbr, _) if ctx._2 contains cid =>
        //super.extract(xtor, cbr)((ctx._1, ctx._2 - cid, ctx._3))
        extract(xtor, cbr)((ctx._1, ctx._2 - cid, ctx._3, ctx._4))
      case Arg(cid, cbr, els) =>
        //val cbrE = super.extract(xtor, cbr)((ctx._1 + cid, ctx._2 - cid, ctx._3))
        //val cbrE = super.extract(xtor, cbr)((ctx._1 + cid, ctx._2, ctx._3))
        val cbrE = extract(xtor, cbr)((ctx._1 + cid, ctx._2, ctx._3, ctx._4))
        //if (cbrE.isDefined) ctx._3 := ctx._3.! + cid
        //if (cbrE.isDefined && !ctx._2.contains(cid)) ctx._3 := { (res: Rep, orig: Rep) =>
        if (cbrE.isDefined) ctx._3 := { (res: Rep, orig: Rep) =>
          //rebind(xtee, els.dfn)
          // ^ FIXME this is not right: it will remove possible paths;
          //     e.g., in (C0->a|a')+(C1->b|b') we'll get (C0->C1->a+b|@0|@0) where @0 = a'+b'
          //     I only put it there temporarily; we need a general solution to prevent the same patterns from firing again!
          Arg(cid,oldCtx_3(res,orig),orig).toRep
        }
        cbrE orElse extract(xtor, els)
      case _ =>
        //println(s"+= $xtee")
        ctx._4 := xtee :: ctx._4.!
        //val (rs,ts,rss) = super.extract(xtor, xtee)
        //(rs,ts,rss)
        /*
        super.extract(xtor, xtee) map {
          //case (rs,ts,rss) => (rs.mapValues(Call(cid,_).toRep),ts,rss)
          case (rs,ts,rss) => (rs.mapValues{r =>
          //case (rs,ts,rss) => (rs.filterNot(_._2 |> isNormalVal).mapValues{r =>
            if (r|>isNormalVal) { // shoudl not wrap Val's; indeed they may be the result of a symbol extarction!
              r
            } else {
              val r1 = ctx._1.foldRight(r)(Call(_,_).toRep)
              //ctx._2.foldRight(r1)(Arg(_,_,Bottom.toRep).toRep)
              ctx._2.foldRight(r1)(Call(_,_).toRep)
            }
          },ts,rss) also { res =>
            println(s"GOOD $ctx\n\t$rs\nto\n\t$res")
          }
        }
        */
        super.extract(xtor, xtee)
    }
    if (res.isEmpty) ctx._3 := oldCtx_3
    if (res.isEmpty) ctx._4 := oldCtx_4
    res
  }
  //override def rewriteRep(xtor: Rep, xtee: Rep, code: Extract => Option[Rep]): Option[Rep] = {
  //override def rewriteRep(xtor: Rep, xtee: Rep, code: Extract => Option[Rep]): Option[Rep] = transformed.setAndIfUnset((xtor,xtee), {
  //override def rewriteRep(xtor: Rep, xtee: Rep, code: Extract => Option[Rep]): Option[Rep] = if (transformed contains (xtor,xtee)) None else {
  override def rewriteRep(xtor: Rep, xtee: Rep, code: Extract => Option[Rep]): Option[Rep] = {
    //super.rewriteRep(xtor: Rep, xtee
    //implicit val ctx: XCtx = (Set.empty, )
    val ctx: XCtx = newXCtx
    //val res = extract(xtor, xtee)(ctx) flatMap (merge(_, repExtract(SCRUTINEE_KEY -> xtee))) flatMap code
    //ctx._3.!.foldRight(res)(Arg(_,_,xtee).toRep)
    //extract(xtor, xtee)(ctx) flatMap (merge(_, repExtract(SCRUTINEE_KEY -> xtee))) flatMap code map {
    //  res => ctx._3.!.foldRight(res)(Arg(_,_,xtee).toRep)
    //}
    //extract(xtor, xtee)(ctx) flatMap (merge(_, repExtract(SCRUTINEE_KEY -> xtee))) flatMap code map (ctx._3.!(_,xtee)) also (
    //  (transformed += ((xtor,xtee))) If _.isDefined
    //)
    //extract(xtor, xtee)(ctx) flatMap (merge(_, repExtract(SCRUTINEE_KEY -> xtee))) flatMap code map (ctx._3.!(_,xtee)) into_? {
    //  case Some(x) => transformed += ((xtor,xtee)); x
    //}
    //extract(xtor, xtee)(ctx) flatMap (merge(_, repExtract(SCRUTINEE_KEY -> xtee))) flatMap code flatMap {
    //  case x0 =>
    //    //if (transformed contains ctx._4.!) None
    //    transformed.setAndIfUnset(xtor -> ctx._4.!, {
    //      println(s"Transforming $xtor << ${ctx._4.!.map(_.bound)}")
    //      val x = ctx._3.!(x0,xtee)
    //      //transformed += ((xtor,xtee))
    //      Some(x)
    //  }, None)
    //}
    extract(xtor, xtee)(ctx) flatMap (merge(_, repExtract(SCRUTINEE_KEY -> xtee))) flatMap {
      case extr =>
        val reps = ctx._4.!
        if (transformed contains xtor->reps) None else {
          println(s"...considering $xtor << ${reps.map(_.bound)} --> ${extr}")
          //val x = ctx._3.!(x0,xtee)
          ////transformed += ((xtor,xtee))
          //Some(x)
          //code(x0) map ctx._3.! into_? {
          //  case Some(x) => transformed += ((xtor,reps)); x
          //}
          code(extr) |>? {
            case Some(x0) =>
              println(s"...transforming ${xtor.bound} << ${ctx._4.!.map(_.bound)}")
              val x = ctx._3.!(x0,xtee)
              transformed += ((xtor,reps))
              x
          }
        }
    }
  }
  //}, None) alsoDo(println(s"() tr ${transformed.map(a=>a._1.bound+"<<"+a._2.bound)}"))
  */
  
  override protected def unapplyConst(rep: Rep, typ: TypeRep): Option[Any] = rep.dfn match {
    case Call(_,r) => unapplyConst(r,typ)
    // there was no case for Arg on purpose...
    // this is not satisfying, because it's partial and biased: will only look at 'cbr' first... in fact it's actually unsound!
    //case Arg(_,cbr,els) => unapplyConst(cbr,typ) orElse unapplyConst(els,typ)
    case _ => super.unapplyConst(rep,typ)
  }
  
  
  import squid.quasi.MetaBases
  import squid.utils.meta.{RuntimeUniverseHelpers => ruh}
  import ruh.sru
  
  override def scalaTreeIn(MBM: MetaBases)(SRB: MBM.ScalaReflectionBase, rep: Rep, ExtrudedHandle: (BoundVal => MBM.u.Tree)): MBM.u.Tree =
    SimpleASTBackend.scalaTreeIn(MBM)(SRB, reinterpret(rep, SimpleASTBackend)(bv =>
      SimpleASTBackend.bindVal(bv.name,bv.typ.asInstanceOf[SimpleASTBackend.TypeRep],Nil).toRep), bv => {
        import MBM.u._
        //q"""scala.sys.error(${bv.name}+" not bound")"""
        q"""squid.lib.unbound(${bv.name})"""
      })
  /*
  import squid.quasi.MetaBases
  import squid.utils.meta.{RuntimeUniverseHelpers => ruh}
  import ruh.sru
  
  override def scalaTreeIn(MBM: MetaBases)(SRB: MBM.ScalaReflectionBase, rep: Rep, ExtrudedHandle: (BoundVal => MBM.u.Tree)): MBM.u.Tree = GraphDebug.muteFor { muteFor {
    new ReinterpreterToScala {
      val MetaBases: MBM.type = MBM
      val newBase: SRB.type = SRB
      override val extrudedHandle = ExtrudedHandle
    } applyRep rep
  }}
  
  abstract class ReinterpreterToScala extends super.ReinterpreterToScala {
    val MetaBases: MetaBases
    import MetaBases.u._
    val newBase: MetaBases.ScalaReflectionBase
    
    def applyRep(r: Rep) = super.apply(r)
    
    //val repCache = mutable.Map[Int, newBase.Rep]()
    
    override def apply(d: Def) = d match {
      case Imperative(es,r) => q"..${es map applyRep}; ..${r |> applyRep}"
      case _ => super.apply(d)
    }
  }
  */
  
  //abstract class Reinterpreter extends super.Reinterpreter {
  trait Reinterpreter extends super.Reinterpreter {
    // TODO use an intermediate representation that understands a higher-level concept of functions and:
    //   - can merge several control-flow boolean parameters into one integer flag parameter
    //   - can prune useless branches (to refine)
    // TODO the target IR should defunctionalize the continuations we generate here...
    //   or at least we should use bool or int flag params when possible
    // TODO detect effects or costly computations and do not lift them out of nested expr
    //   if we're not sure the path must be taken
    //   or if the path isn't 'pure', in the case of effects (if the path's effects interact with the arg's effects)
    // TODO allow passing in args only closed in some of the calls... the other calls passing null...
    //   again, should only be done if the path is sure to be taken... assuming we're coming from that call!
    
    override val recoverLetIns = false
    
    /*
    //def apply(r: Rep) = apply(dfn(r):Def)
    //def apply(r: Rep) = apply(dfnOrGet(r):Def)
    //def apply(r: Rep) = apply(r match {
    //  case Rep(d: NonTrivialDef) => r
    //  case Rep(d) => d
    //  case _ => r
    //})
    def apply(r: Rep): newBase.Rep = ???
    //def apply(r: Rep) = {
    //  //val d = dfnOrGet(r)
    //  val d = dfn(r)
    //  //if (d.isSimple) apply(d)
    //  //else apply(r:Def)
    //  apply(if (d.isSimple) d else r)
    //}
    def applyTopLevel(r: Rep) = {
      val rtyp = rect(r.typ)
      //iterator(r).collect{ case nde @ Rep(d: NonTrivialDef) => nde -> d }.foldRight(apply(r)){
      //  case (nde -> d, term) => newBase.letin(nde |> recv, apply(d), term, rtyp)
      //}
      //iterator(r).collect{ case nde @ Rep(d: NonTrivialDef) => nde -> d }.foldRight(() => apply(r)){
      //  case (nde -> d, termFun) =>
      //    val b = nde |> recv
      //    val v = apply(d)
      //    bound += nde -> b
      //    () => newBase.letin(b, v, termFun(), rtyp)
      //}
      //iterator(r).collect{ case nde @ Rep(d: NonTrivialDef) => nde -> d }.foldLeft(() => apply(r)){
      iterator(r).collect{ case nde @ Rep(d) if !d.isSimple => nde -> d }.foldLeft(() => apply(r)){
        case (termFun, nde -> d) =>
          val b = nde.bound |> recv
          bound += nde.bound -> b
          () => {
            val v = apply(d)
            newBase.letin(b, v, termFun(), rtyp)
          }
      }()
    }
    */
    
    val pointers = mutable.Map.empty[Rep,mutable.Set[Rep]]
    val alwaysBoundAt = mutable.Map.empty[Rep,Set[Val]]
    val liveVals = mutable.Set.empty[Val]
    
    def applyTopLevel(r: Rep) = {
      
      assert(liveVals.isEmpty)
      liveVals ++= iterator(r).collect{case Rep(Abs(v,_)) => v}
      
      val analysed = mutable.HashSet.empty[Rep]
      def analyse(r: Rep): Unit = /*println(s"> Analayse $r") thenReturn*/ analysed.setAndIfUnset(r, {
        pointers.getOrElseUpdate(r,mutable.Set.empty)
        def addptr(m:Rep): Unit = {
        //def addptr(m:Rep): Unit = if (m.dfn.isSimple) pointers(m)=mutable.Set.empty else {
          //println(s"Add ${r.bound} -> ${m} ${pointers.get(m)}")
          
          //if (m.dfn.isSimple) pointers(m)=mutable.Set.empty else
          if (!m.dfn.isSimple)
          pointers.getOrElseUpdate(m,mutable.Set.empty) += r
          analyse(m)
        }
        r.dfn match {
          //case _: Constant | _: Module | _: Sym =>
          //case Function(params, result) => addptr(result)
          //case Arg(nodes) => nodes.valuesIterator.foreach(addptr)
          //case Call(cid, res) => addptr(res)
          //case Appli(sym, args) => args.foreach(addptr)
          //case Split(scrut, branches) => addptr(scrut); branches.foreach(_._2 |> addptr)
          case MethodApp(self, mtd, targs, argss, tp) =>
            addptr(self)
            argss.iterator.flatMap(_.reps.iterator).foreach(addptr)
          case Abs(_, b) => addptr(b)
          case Ascribe(r, _) => addptr(r)
          case Module(r, _, _) => addptr(r)
          case Call(_, res) => addptr(res)
          case Arg(_, cbr, els) => addptr(cbr); addptr(els)
          //case Constant(_)|BoundVal(_)|CrossStageValue(_, _)|HoleClass(_, _)|StaticModule(_) => false
          case _: LeafDef =>
        }
      })
      assert(pointers.isEmpty)
      analyse(r)
      
      val analysed2 = mutable.HashSet.empty[(Rep,Set[Val])]
      def analyse2(r: Rep)(implicit vctx: Set[Val]): Unit = analysed2.setAndIfUnset(r -> vctx, r.dfn match {
        case MethodApp(self, mtd, targs, argss, tp) =>
          analyse2(self)
          argss.iterator.flatMap(_.reps.iterator).foreach(analyse2)
        case Abs(v, b) => analyse2(b)(vctx + v)
        case Ascribe(r, _) => analyse2(r)
        case Module(r, _, _) => analyse2(r)
        case Call(_, res) => analyse2(res)
        case Arg(_, cbr, els) => analyse2(cbr); analyse2(els)
        case _: LeafDef =>
      })
      analyse2(r)(Set.empty)
      assert(alwaysBoundAt.isEmpty)
      alwaysBoundAt ++= analysed2.groupBy(_._1).mapValues(_.unzip._2.reduce(_ & _))
      //println(s"alwaysBoundAt: ${alwaysBoundAt.map({case(k,v)=>s"\n\t${k.bound} -> ${v.mkString(",")}"})}")
      //println(s"alwaysBoundAt: ${alwaysBoundAt.map({case(k,v)=>s"\n [${pointers(k).size}] \t${k.bound} -> ${v}"}).mkString}")
      //println(s"alwaysBoundAt: ${alwaysBoundAt.map({case(k,v)=>s"\n [${pointers(k).size}] \t${v} \t${k.bound}"}).mkString}")
      
      //val Seq() -> res = scheduleFunction(r) // TODO B/E
      val res = apply(r)
      val rtyp = rect(r.typ)
      functions.foldLeft(res) {
        case (acc, f -> ((nf,_))) => newBase.letin(f.bound |> bound, nf, acc, rtyp)
      }
      //res
    }
    def scheduleFunction(r: Rep): Seq[Rep] -> newBase.Rep = {
      println(s"> Schfun $r ${alwaysBoundAt(r)}")
      cctx ::= mutable.Set.empty
      val oldvctx = vctx
      vctx = Set.empty
      //params ::= (vctx,mutable.Buffer.empty)
      params ::= (alwaysBoundAt(r),mutable.Buffer.empty)
      val res = params.head._2 -> apply(r.dfn)
      vctx = oldvctx
      cctx = cctx.tail
      params = params.tail
      res
      
      //vctx = oldvctx
      //cctx = cctx.tail
      //val ps = params.head._2
      //params = params.tail
      //val res = ps -> apply(r.dfn)
      //res
    }
    //var ctx: List[List[CallId]] = Nil
    //var ctx: List[mutable.Buffer[CallId]] = Nil
    var cctx: List[mutable.Set[CallId]] = mutable.Set.empty[CallId]::Nil
    var vctx: Set[Val] = Set.empty
    //var params: List[mutable.Buffer[Rep]] = Nil
    var params: List[(Set[Val],mutable.Buffer[Rep])] = Nil
    //var functions = mutable.Map.empty[Rep,newBase.Rep]
    //var functions = mutable.Map.empty[Rep,newBase.Rep->newBase.Rep]
    var functions = mutable.Map.empty[Rep,newBase.Rep->(()=>newBase.Rep)]
    //def apply(r: Rep): newBase.Rep = {
    //  //ctx ::= Nil
    //  r.dfn match {
    //    case Arg(cid, cbr, els) =>
    //      ???
    //    case d =>
    //      apply()
    //  }
    //}
    def apply(r: Rep): newBase.Rep = if (pointers.isEmpty) applyTopLevel(r) else r.dfn match {
    //case d @ Abs(bv, _) =>
    //  assert(!vctx(bv)) // TODO if so, refresh
    //  vctx += bv
    //  super.apply(d) alsoDo {vctx -= bv}
    case Call(cid, res) =>
      cctx.head += cid
      apply(res) alsoDo {cctx.head -= cid}
    case Arg(cid, cbr, els) =>
      //println(s"Arg $r")
      //if (cctx.head.nonEmpty) { // FIXedME
        //assert(ctx.head.last === cid)
        if (cctx.head(cid)) {
          cctx.head -= cid
          apply(cbr) alsoDo {cctx.head += cid}
        //} else apply(els)
      }
      //else apply(els.get.bound/*TODO B/E*/) alsoDo {params.head += els.get}
      else if (params.isEmpty) apply(els)
      //else bindOrGet(r.bound) alsoDo {params.head += r}
      else {
        //if (!(r.dfn.unboundVals subsetOf params.head._1)) println(s"Oops: $r ${r.dfn.unboundVals} ill-scoped in ${params.head._1}")
        //println(s"Arg $r ${r.dfn.unboundVals} scoped in ${params.head._1}")
        //if (r.dfn.unboundVals subsetOf params.head._1) recv(r.bound) |> newBase.readVal alsoDo {params.head._2 += r}
        val rfv = r.freeVals filter liveVals
        if (vctx.isEmpty || (rfv subsetOf vctx)) recv(r.bound) |> newBase.readVal alsoDo {params.head._2 += r}
        else { // cannot extract impl node as a parameter, because it refers to variables not bound at all calls
          // FIXME should use flow analysis to know 'variables not bound at all calls' --- and also other things?
          // TODO also do this if the expression is effectful or costly and we're in a path that may not always be taken! -- unless ofc we're targetting a pure lazy language like Haskell
          //println(s"Oops: $r ${rfv} ill-scoped in ${params.head._1} ${rfv -- params.head._1 toList}")
          println(s"Oops: ${rfv} not in ${vctx} for:  ${r.showGraphRev}")
          
          // Generate boolean params to know which subexpression to compute;
          // Problem: expressions under an Arg may contain variables only bound in some of the _callers_, so using them
          // here won't work with traditional PL semantics (it could be made to work with dynamic scopes or global stack variables)
          /*
          val p = bindVal("Φ", Predef.implicitType[Bool].rep, Nil)
          val rtyp = r.typ |> rect
          //params.head._2 += Arg(cid, const(true), Some(const(false))).toRep
          params.head._2 += Rep.bound(p, Arg(cid, const(true), Some(const(false))))
          newBase.methodApp(newBase.staticModule("squid.lib.package"),
            newBase.loadMtdSymbol(newBase.loadTypSymbol("squid.lib.package$"), "IfThenElse"),
            rtyp::Nil, newBase.Args(recv(p) |> newBase.readVal,
              newBase.byName(apply(cbr)),
              //newBase.byName(apply(Arg(cid,cbr,None))), // SOF
              newBase.byName(els.fold(???)(apply))
            )::Nil,rtyp)
          */
          // Solution for now: resort to using lambdas, and assume a later defunc phase will remove them... (should be easy-ish in these cases)
          ///*
          //val extrudedVals = r.dfn.unboundVals -- params.head._1 toList;
          //assert(extrudedVals.nonEmpty)
          //val extrudedVals = r.dfn.unboundVals -- vctx toList;
          val extrudedVals = vctx.toList
          val k = bindVal("κ", lambdaType(extrudedVals.map(_.typ), r.typ), Nil)
          //val rtyp = r.typ |> rect
          val appSym = ruh.FunctionType.symbol(extrudedVals.size).toType.member(sru.TermName("apply")).asMethod
          //newBase.mapp(k, appSym, rtyp)()(extrudedVals:_*)
          vctx += k
          recv(k)
          apply(mapp(k|>readVal, appSym, r.typ)()(Args(extrudedVals.map(readVal):_*))) alsoDo {
            vctx -= k
            params.head._2 += Rep.bound(k, Arg(cid, lambda(extrudedVals, cbr), lambda(extrudedVals, els)))
          }
          //apply(mapp(k|>readVal, appSym, r.typ)()(Args(extrudedVals.map(readVal):_*)) also(r=>println(r.showGraphRev))) alsoDo {vctx -= k}
          //*/
          //val extrudedVals1 = cbr.dfn.unboundVals -- params.head._1 toList;
          //val extrudedVals2 = r.dfn.unboundVals -- params.head._1 toList;
          //assert(extrudedVals.nonEmpty)
          //val k = bindVal("κ", lambdaType(extrudedVals.map(_.typ), r.typ), Nil)
          ////val rtyp = r.typ |> rect
          //params.head._2 += Rep.bound(k, Arg(cid, lambda(extrudedVals, cbr), Some(lambda(extrudedVals, ))))
          //val appSym = ruh.FunctionType.symbol(extrudedVals.size).toType.member(sru.TermName("apply")).asMethod
          ////newBase.mapp(k, appSym, rtyp)()(extrudedVals:_*)
          //apply(mapp(k.toRep, appSym, r.typ)()(Args(extrudedVals.map(rep):_*)))
          
        }
      }
    case _ =>
      //val addedBinding = r.dfn |>? { case Abs(v, _) => vctx += v; v }
      //println(s"> Apply $r (${pointers(r).map(_.bound)})")
      //(if (pointers(r).size > 1) {
      if (pointers.get(r).exists(_.size > 1)) { // pointers may not be defined on newly created nodes
        functions.getOrElseUpdate(r, {
        //functions.getOrElse(r, {
        //  println(s"> Making function for ${r.bound}...")
          val rtyp = rect(r.typ)
          val params -> res = scheduleFunction(r)
          println(s"> Function: $params -> $res")
          //functions += r ->
          val fdef =
          params.foldRight(res) {
            case (p, acc) => newBase.lambda(recv(p.bound)::Nil, acc)//(rtyp)
          }
          //bound += r.bound -> newBase.bindVal(r.bound.name, rtyp, Nil)
          //params.foldRight(res) {
          val rv = r.bound |> recv
          val call = () =>
          params.foldRight(rv |> newBase.readVal) {
            case (param, acc) =>
              //newBase.app(acc, recv(p) |> newBase.readVal)(rtyp)
              newBase.app(acc, param |> apply)(rtyp)
              //newBase.app(acc, param |> recv |> newBase.readVal)(rtyp)
          }
          fdef -> call
          //res
        })._2()
      }
      else apply(r.dfn)
      //) alsoDo {addedBinding.foreach(vctx -= _)}
    }
    override def apply(d: Def) = d match {
      case d @ Abs(bv, _) =>
        assert(!vctx(bv), s"$bv in $vctx") // TODO if so, refresh
        vctx += bv
        super.apply(d) alsoDo {vctx -= bv}
      case _ => super.apply(d)
    }
    //protected def bindOrGet(v: Val) = bound.getOrElseUpdate(v, recv(v)) |> newBase.readVal
    override protected def recv(v: Val): newBase.BoundVal =
      bound.getOrElse(v, super.recv(v)) // FIXME calling super.recv in the wrong order messes with some interpreters, such as BaseInterpreter
    /*
    override def apply(d: Def) = d match {
      case Call(cid, res) =>
        ctx.head += cid
        apply(res) alsoDo ctx.head.remove(ctx.head.indices.last)
      case Arg(cid, cbr, els) =>
        //params.head += 
        //???
        
        //if (ctx.head.lastOption.contains(cid)) apply(cbr) alsoDo ctx.head.remove(ctx.head.indices.last)
        //else apply(els.get.bound/*TODO B/E*/) alsoDo {params.head += els.get.bound}
        
        if (ctx.head.nonEmpty) {
          //assert(ctx.head.last === cid)
          if (ctx.head.last === cid) {
            ctx.head.remove(ctx.head.indices.last)
            apply(cbr) alsoDo {ctx.head += cid}
          } else els.fold(???)(apply) // TODO B/E
        }
        //else apply(els.get.bound/*TODO B/E*/) alsoDo {params.head += els.get}
        else apply(r.bound) alsoDo {params.head += r}
        
      case _ => super.apply(d)
    }
    */
    /*
    def apply(r: Rep): newBase.Rep = {
      schedule(r)(Nil)
    }
    def schedule(r: Rep)(implicit ctx: List[CallId]): newBase.Rep = {
      r.dfn match {
        case Arg(cid, cbr, els) =>
          ???
        case d =>
          apply()
      }
    }
    */
  }
  override def reinterpret(r: Rep, NewBase: squid.lang.Base)(ExtrudedHandle: (BoundVal => NewBase.Rep) = DefaultExtrudedHandler): NewBase.Rep =
    new Reinterpreter {
      val newBase: NewBase.type = NewBase
      override val extrudedHandle = ExtrudedHandle
    } applyTopLevel r
    //} apply r //applyTopLevel r
  
  
  implicit class GraphRepOps(private val self: Rep) {
    //def reduceStep: Rep = graph.reduceStep(self) thenReturn self
    def reduceStep = self optionIf graph.reduceStep _
    def showGraph = graph.showGraph(self)
    def showGraphRev = graph.showGraphRev(self)
    def showRep = graph.showRep(self)
    def iterator = graph.iterator(self)
  }
  
  implicit class GraphDefOps(private val self: Def) {
    def isSimple = self match {
      //case _: SyntheticVal => false  // actually considered trivial?
      //case Constant(_)|BoundVal(_)|CrossStageValue(_, _)|HoleClass(_, _)|StaticModule(_) => true
      case _: LeafDef => true
      case _ => false
    }
  }
  
  def reduceStep(r: Rep): Bool = {
    println(s"> Reducing... $r")
    
    r.dfn match {
      //case Apply(f,arg) =>
      //  println(f)
      //  println(edges get f.bound)
      //  println(edges)
      //case Apply(ar @ Rep(Abs(_,_)),arg) =>
      case Apply(ar @ Rep(Abs(p,b)),v) =>
      //case BetaRedex(p, v, b) => // matches redexes across Ascribe nodes
        val cid = new CallId("β")
        //Call(id, b) |> rep
        rebind(r.bound, Call(cid, b))
        // TODO also rebind usages... p
        val newp = bindVal(p.name+"'",p.typ,p.annots)
        mkArgs(p, cid, v, newp.toRep)(b)
        rebind(ar.bound, Abs(newp, b)(ar.typ))
        true
      case MethodApp(self, mtd, targs, argss, tp) =>
        reduceStep(self) || argss.iterator.flatMap(_.reps.iterator).exists(reduceStep)
      case Abs(_, b) => reduceStep(b)
      case Ascribe(r, _) => reduceStep(r)
      case Module(r, _, _) => reduceStep(r)
      //case Constant(_)|BoundVal(_)|CrossStageValue(_, _)|HoleClass(_, _)|StaticModule(_) => false
      case _: LeafDef => false
    }
    
  }
  
  def mkArgs(p: Val, cid: CallId, arg: Rep, els: Rep)(r: Rep): Unit = {
    val traversed = mutable.HashSet.empty[Rep]
    def rec(r: Rep): Unit = traversed.setAndIfUnset(r, r.dfn match {
      case `p` => rebind(r, Arg(cid, arg, els))
      //case `p` => Rep(Arg(cid, arg, Some(r.dfn.toRep)))
      case Call(_, res) => rec(res)
      case Arg(_, cbr, els) => rec(cbr); rec(els)
      case Abs(_, b) => rec(b)
      case Split(_,_) => ??? // TODO
      case Ascribe(r,_) => rec(r)
      case Module(r,_,_) => rec(r)
      case MethodApp(self, mtd, targs, argss, tp) =>
        rec(self)
        argss.iterator.flatMap(_.reps.iterator).foreach(rec)
      //case Constant(_)|BoundVal(_)|CrossStageValue(_, _)|HoleClass(_, _)|StaticModule(_) =>
      case _: LeafDef =>
    })
    rec(r)
  }
  
  
  
  
}

object CallId { private var curId = 0; def reset(): Unit = curId = 0 }
class CallId(val name: String = "") {
  val uid: Int = CallId.curId alsoDo (CallId.curId += 1)
  def uidstr: String = s"$name$uid"
  override def toString: String = uidstr
}
