package squid.ir
package graph

import squid.utils._
import squid.utils.CollectionUtils.MutSetHelper
import squid.utils.meta.{RuntimeUniverseHelpers => ruh}

import scala.collection.immutable.ListSet
import scala.collection.mutable

/* The Grah IR
 * TODO: cache calls to `dfn` based on a token that is invalidated when the graoh is actually changed? or a hashset that is emptied?
 *    or something more fine-grained?
 *    there are currently many 'online' traversals (notably, on every .dfn access, and every time .simplify_! is called)
 *    and these mini-traversals need not be repeated while doing things like graph matching which do not change the
 *    graph but perform a lot of traversals!
 * In the future, we may want to remove `with CurryEncoding` and implement proper multi-param lambdas... */
class Graph extends AST with GraphScheduling with GraphRewriting with CurryEncoding { graph =>
  
  val onlineOptimizeCalls = true
  
  object GraphDebug extends PublicTraceDebug
  
  //val edges = mutable.Map.empty[Val,Def]
  val edges = new java.util.WeakHashMap[Val,Def]
  
  def bind(v: Val, d: Def): Unit = {
    //require(!edges.isDefinedAt(v))
    require(!edges.containsKey(v))
    rebind(v, d)
  }
  def rebind(v: Val, d: Def): Unit = {
    require(!v.isInstanceOf[SyntheticVal])
    //require(!d.isInstanceOf[BoundVal] || d.isInstanceOf[SyntheticVal], s"$d")  // TODO enforce?
    //edges += v -> d
    edges.put(v, d)
  }
  def rebind(r: Rep, d: Def): r.type = rebind(r.bound, d) thenReturn r
  
  val Bottom = bindVal("⊥", Predef.implicitType[Nothing].rep, Nil)
  bind(Bottom,Bottom) // prevents rebinding of Bottom, just in case
  
  type CtorSymbol = Class[_]
  
  class Rep(val bound: Val) {
    require(!bound.isInstanceOf[SyntheticVal])
    //def dfn: Def = edges.getOrElse(bound, bound)
    //def dfn: Def = Option(edges.get(bound)) getOrElse bound
    def dfn: Def = safe_dfn(Set.empty)
    def safe_dfn(implicit traversed: Set[Rep]): Def = {
      val d_? = edges.get(bound)
      //println(d_?)
      d_? match {
        case null => bound
          
        case Arg(cid0, thn0, SafeRep(Arg(cid1, thn1, els))) if cid0 === cid1 =>
          //val d = Arg(cid0, thn0, els)
          val d = Arg(cid0, thn0, els, d_?.typ) // specify the type here to avoid SOF
          rebind(bound, d)
          d
          
        // does not seem useful (it seems to make terms bigger, if anything):
        //case Arg(cid, SafeRep(Arg(cid0, thn0, els0)), SafeRep(Arg(cid1, thn1, els1))) if (cid0 === cid1) && (thn0 === thn1) =>
        //case Arg(cid, SafeRep(Arg(cid0, thn0, els0)), SafeRep(Arg(cid1, thn1, els1))) if (cid0 === cid1) && (thn0.safe_dfn === thn1.safe_dfn) =>
        //  val d = Arg(cid0, PassArg(cid,thn0).toRep, Arg(cid, els0, els1).toRep)
        //  rebind(bound, d)
        //  d
          
        case Call(cid0, SafeRep(Arg(cid1,thn,els))) =>
          val d = if (cid0 === cid1) thn.safe_dfn
            else Arg(cid1,call(cid0,thn),call(cid0,els), d_?.typ)
          rebind(bound, d)
          d
          
        case Call(cid,SafeRep(d:LeafDef)) if !d.isInstanceOf[Val] =>
          rebind(bound, d)
          d
          
        case d => d
      }
    }
    
    def isBottom = dfn === Bottom
    
    def iterator = graph.iterator(this)
    
    /** contrary to unboundVals and its synonym freeVariables, this does not cache (incompatible with mutability of the
      * graph), and goes through Arg/Call/Split nodes. It's still fine to use unboundVals, but it will only give the
      * variables bound by the Rep's immediately used by the Def (which are immutable and can be cached) */
    def freeVals: Set[Val] = iterator.collect{case Rep(v:Val) if !v.isInstanceOf[SyntheticVal] => v}.toSet
    
    def size = iterator.size
    
    def simplified = simplifyCallArgs(this)(GXCtx.empty,mutable.Map.empty)
    def simplify_! : this.type = {
      val res = simplified
      if (res.bound =/= bound) // && !(res.dfn eq dfn) or !(res.dfn === dfn) ?
        println(s"-- $this\n++ $res") thenReturn
        rebind(bound, res.dfn)
      this
    }
    def simplifiedTopLevel =
      simplifyCallArgs(this)(GXCtx.empty.copy(assumedNotCalled = freeArgs),mutable.Map.empty)
    
    def freeArgs = graph.freeArgs(dfn)(emptyCCtx)
    
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
  object Rep {
    protected[graph] def bound(v: Val, d: Def) = {
      assert(!d.isInstanceOf[Val] || d.isInstanceOf[SyntheticVal])
      new Rep(v) alsoDo bind(v, d)
    }
    def unapply(e: Rep) = Some(e.dfn)
  }
  object SafeRep {
    def unapply(e: Rep)(implicit traversed: Set[Rep]): Option[Def] =
      if (traversed(e)) None else Some(e.safe_dfn(traversed + e))
  }
  
  override protected def freshNameImpl(n: Int) = "$"+n
  //override protected def freshNameImpl(n: Int) = "@"+n
  
  /** Synthetic vals are never supposed to be let-bound...? */
  class SyntheticVal(name: String, typ: TypeRep, annots: List[Annot]=Nil) extends BoundVal("@"+name)(typ, annots)
  
  def isNormalVal(d: Def): Bool = d.isInstanceOf[BoundVal] && !d.isInstanceOf[SyntheticVal]
  def isNormalVal(r: Rep): Bool = r.dfn |> isNormalVal
  
  // TODO override 'reps' and 'rebuild' in these? (also, factor reps and children!)
  case class Call(cid: CallId, result: Rep) extends SyntheticVal("C"+cid, result.typ) {
    // Note that 'unboundVals' won't see this (good, because since it's caching it would be wrong),
    // because it matches on BoundVal and only calls children in the default case...:
    override def children: Iterator[Rep] = Iterator.single(result)
  }
  object Call {
    def apply(cid: CallId, result: Rep) = result.dfn match {
      case Arg(`cid`, cbr, _) if onlineOptimizeCalls => cbr.dfn
      case _ => new Call(cid,result)
    }
  }
  def call(cid: CallId, result: Rep): Rep = result.dfn match {
    //case Arg(`cid`, thn, _) => thn
    //case Arg(cid1, thn, els) => Arg(cid1, call(cid, thn), call(cid, els)).toRep
    // ^ caused SOF
    case _ => Call(cid,result).toRep
  }
  
  abstract case class Arg(cid: CallId, cbr: Rep, els: Rep)(typ: TypeRep) extends SyntheticVal("A"+cid, typ) {
    override def children: Iterator[Rep] = Iterator(cbr,els)
  }
  object Arg {
    def apply(cid: CallId, cbr: Rep, els: Rep) = new Arg(cid: CallId, cbr: Rep, els: Rep)(ruh.uni.lub(cbr.typ.tpe::els.typ.tpe::Nil)){}
    def apply(cid: CallId, cbr: Rep, els: Rep, typ: TypeRep) = new Arg(cid: CallId, cbr: Rep, els: Rep)(typ){}
  }
  object ArgSet {
    def unbuild(cid: CallId, cbr: Rep, els: Rep): List[CallId] -> Rep = cbr.dfn match {
      case Arg(cid0,cbr0,e@Rep(Call(`cid`,`els`))) => unbuild(cid0,cbr0,e) ||> (_.::(cid) -> _)
      case _ => (cid :: Nil) -> cbr
    }
    def unapply(a: Arg): Some[(List[CallId],Rep,Rep)] = {
      val cs -> e = unbuild(a.cid, a.cbr, a.els)
      Some(cs.distinct, e, a.els)
    }
  }
  object PassArg {
    def apply(cid: CallId, res: Rep): Arg = Arg(cid, res, res)
    def unapply(a: Arg): Option[CallId -> Rep] = a.cid -> a.cbr optionIf
      //(a.cbr === a.els)
      (a.cbr.dfn === a.els.dfn)
  }
  // TODO Q: should this one be encoded with a normal MethodApp? -> probably not..
  case class Split(scrut: Rep, branches: Map[CtorSymbol, Rep]) extends SyntheticVal("S", branches.head._2.typ) {
    override def children: Iterator[Rep] = Iterator.single(scrut) ++ branches.valuesIterator
  }
  
  //override protected def unboundVals(d: Def): Set[Val] = d match {
  //  case Call(cid, res) => res.dfn.unboundVals
  //  case Arg(cid, cbr, els) => cbr.dfn.unboundVals ++ els.dfn.unboundVals
  //  case _:SyntheticVal => ??? // TODO
  //  case _ => super.unboundVals(d)
  //}
  override protected def unboundVals(d: Def): Set[Val] = ??? // to make sure not used, until we make 'dfn' opaque again
  
  private val colors = List(/*Console.BLACK,*/Console.RED,Console.GREEN,Console.YELLOW,Console.BLUE,Console.MAGENTA,Console.CYAN,
    /*Console.WHITE,Console.BLACK_B,Console.RED_B,Console.GREEN_B,Console.YELLOW_B,Console.BLUE_B,Console.MAGENTA_B,Console.CYAN_B*/)
  private def colorOf(cid: CallId) = colors(cid.uid%colors.size)
  
  override def prettyPrint(d: Def) = (new DefPrettyPrinter)(d)
  class DefPrettyPrinter extends super.DefPrettyPrinter {
    val printed = mutable.Set.empty[Rep]
    override val showValTypes = false
    override val desugarLetBindings = false
    var curCol = Console.BLACK
    override def apply(r: Rep): String = printed.setAndIfUnset(r, (r match {
      case Rep(d) if d.isSimple => apply(d)
      //case _ => super.apply(r)
      case _ => super.apply(r.bound)
    }) alsoDo {printed -= r}, "[RECURSIVE]"+super.apply(r.bound))
    override def apply(d: Def): String = d match {
      //case Call(cid, res) => s"C[$cid](${res |> apply})"
      //case Call(cid, res) => s"$cid⌊${res |> apply}⌋"
      //case Call(cid, res) => s"〚$cid ${res |> apply}〛"
      case Call(cid, res) =>
        //s"⟦$cid ${res |> apply}⟧"
        val col = colorOf(cid)
        s"$col⟦$cid$curCol ${res |> apply}$col⟧$curCol"
        //s"$col⟦$curCol$cid ${res |> apply}$col⟧$curCol"
      //case Arg(cid, cbr, els) => s"$cid->${cbr |> apply}" + 
      //  //(if (els.isBottom) "" else "|"+apply(els))
      //  "|"+apply(els)
      //case Arg(cid, cbr, els) => s"$cid?${cbr |> apply}!${apply(els)}"
      //case Arg(cid, cbr, els) => s"$cid${Console.BOLD}?${Console.RESET}${cbr |> apply}${Console.BOLD}|${Console.RESET}${apply(els)}"
      case PassArg(cid, res) =>
        val col = colorOf(cid)
        s"$col$cid⟨⟩$curCol${res|>apply}"
      case ArgSet(cids, cbr, els) =>
        val oldCol = curCol
        curCol = colorOf(cids.last)
        s"${cids.map(c=>colorOf(c)+c).mkString("&")}⟨${cbr |> apply}⟩$oldCol${curCol = oldCol; apply(els)}"
        //s"${cids.map(c=>colorOf(c)+c).mkString("&")}→${cbr |> apply}|$oldCol${curCol = oldCol; apply(els)}"
        /*
      case Arg(cid, cbr, els) =>
        val oldCol = curCol
        //val col = colors(cid.uid%colors.size)
        curCol = colorOf(cid)
        //s"$cid${Console.BOLD}⟨${Console.RESET}${cbr |> apply}${Console.BOLD}⟩${Console.RESET}${apply(els)}"
        //s"$cid$curCol⟨${cbr |> apply}⟩$oldCol${curCol = oldCol; apply(els)}"
        s"$curCol$cid⟨${cbr |> apply}⟩$oldCol${curCol = oldCol; apply(els)}"
        */
      //case Arg(cid, cbr, els) => s"$cid→${cbr |> apply}⟨${apply(els)}⟩"
      //case Arg(cid, cbr, els) => s"$cid➔${cbr |> apply}⟨${apply(els)}⟩"
      //case Arg(cid, cbr, els) => s"$cid➤${cbr |> apply}⟨${apply(els)}⟩"
        
      case _:SyntheticVal => ??? // TODO
      case _ => super.apply(d)
    }
  }
  
  
  // Implementations of AST methods:
  
  //def rep(dfn: Def) = Rep(dfn)
  def rep(dfn: Def) =
    //postProcess(new Rep(dfn)) // TODO do this here?
    //new Rep(freshBoundVal(dfn.typ), dfn)
    dfn match {
      //case bv: Val =>
      case bv: Val if isNormalVal(bv) =>
        new Rep(bv)
        
      //case RedundantlyWrapped(r) => r  // FIXME seems to make things diverge
        
      case _ =>
        val v = freshBoundVal(dfn.typ)
        bind(v, dfn)
        new Rep(v)
    }
  /*
  object RedundantlyWrapped {
    //def rec(d: Def)(implicit cctx: Set[CallId]) = d |>? {
    //  case Call(cid, res) if !(cctx contains cid) => rec(res)(cctx + cid)
    //  case Arg(cid, cbr, els) if cctx contains cid => rec(cbr)(cctx - cid)
    //}
    //def unapply(d: Def) = rec(d)
    def rec(rep: Rep)(implicit cctx: ListSet[CallId]): Set[CallId] -> Rep = rep.dfn match {
      case Call(cid, res) if !(cctx contains cid) => rec(res)(cctx + cid)
      case Arg(cid, cbr, els) if cctx contains cid => rec(cbr)(cctx - cid)
      case _ => cctx -> rep
    }
    def unapply(d: Def) = d |>?? {
      case Call(cid, res) =>
        //val cs->r = rec(res)(Set single cid)
        //val cs->r = rec(res)(Nil)
        val cs->r = rec(res)(ListSet.empty + cid)
        //if (c === cid && )
        if (r === res) None
        else println(s"$cs: $d -> $r") thenReturn Some(cs.foldRight(r)(Call(_,_).toRep))
    }
  }
  */
  object RedundantlyWrapped {
    def rec(cid: CallId, rep: Rep): Option[() => Rep] = rep.dfn match {
      case Call(`cid`, res) => None
      case Call(cid0, res) => rec(cid,res).map(f => () => call(cid0,f()))
      case Arg(`cid`, cbr, els) => Some(() => cbr)
      case Arg(cid0, cbr, els) => for {
        cbrR <- rec(cid,cbr)
        cbrE <- rec(cid,els)
      //} yield Some(() => Arg(cid0, cbrR(), cbrE()))
      } yield () => Arg(cid0, cbrR(), cbrE()).toRep
      case _: LeafDef => Some(() => rep) // TODO also defs with no relevant Arg in reps
      case _ => None
    }
    def unapply(d: Def) = d |>?? {
      case Call(cid, res) => rec(cid,res).map(_())
    }
  }
  
  def dfn(r: Rep): Def = r.dfn  // TODO make it opaque so it's not seen by other infra?
  //def dfn(r: Rep): Def = r.bound
  
  def repType(r: Rep) = r.dfn.typ
  
  def showGraph(r: Rep) = {
    reverseIterator(r).collect { case nde @ Rep(d) if !d.isSimple =>
      s"${nde.bound} = ${d};\n"
    }.mkString + r.simpleString
  }
  def showGraphRev(r: Rep) = r.simpleString + {
    val defsStr = iterator(r).collect { case nde @ Rep(d) if !d.isSimple => s"\n\t${nde.bound} = ${d};" }.mkString
    if (defsStr.isEmpty) "" else " where:" + defsStr
  }
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
  }
  
  
  override def letin(bound: BoundVal, value: Rep, body: => Rep, bodyType: TypeRep) =
    bind(bound, value.dfn) thenReturn body
  
  
  // TODO use the special interpretation of lambda params trick:
  //   A variable bound by a lambda can be bound in the graph; its interpretation depends on whether we're looking at it
  //   via the lambda or not.
  //   This reduces the number of necessary call/args, and avoids the false sharing we have with the ucrrent scheme
  //     `if (isBound(v)) rebind else bind ...`
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
    // ^ We don't want to use `super.substituteVal` as it's very complex
    
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
    if (occurs) {
      val cid = new CallId("α")
      val arg = Arg(cid, mkArg, v |> readVal)
      val argr = arg |> rep
      
      //val subsd = substituteValFastUnhygienic(r, v, argr)
      
      val traversing = mutable.Set.empty[Rep] // FIME probably not right
      val traversed = mutable.Map.empty[Rep,Rep] // FIME probably not right; what about different call contexts?
      
      def rec(r: Rep)(implicit cctx: CCtx): Rep = traversed.getOrElseUpdate(r, traversing.setAndIfUnset(r, r.dfn match {
        case Call(cid,res) => rebind(r, Call(cid,rec(res)(withCall(cid))))
        case Arg(cid,cbr,els) =>
          // TODO:
          //if (hasCall(cid)) rec(cbr)(withoutCall(cid))
          //else 
            rebind(r, Arg(cid,rec(cbr),rec(els)))
        case Abs(p,b) => rebind(r, Abs(p,rec(b))(r.typ))
        case `v` => argr // FIXME wrap in calls?
        case v: Val if v.isSimple => PassArg(cid,r).toRep
        case bd: BasicDef =>
          rebind(r, mapRep(rec)(bd))
      }, r))
      val subsd = rec(r)(emptyCCtx)
      
      call(cid, subsd)
    } else r
    
  }
  
  // TODO it should actually return a CCtx
  def freeArgs(d: Def)(implicit ctx: CCtx): Set[CallId] = d match {
    case Call(cid,res) => freeArgs(res.dfn)(withCall(cid))
    case Arg(cid,thn,els) if hasCall(cid) => freeArgs(thn.dfn)(withoutCall(cid))
    case Arg(cid,thn,els) => freeArgs(thn.dfn) ++ freeArgs(els.dfn) + cid
    case Abs(p,b) => freeArgs(b.dfn)
    case bd: BasicDef => bd.reps.iterator.flatMap(_.dfn |> freeArgs).toSet
  }
  
  
  implicit class GraphRepOps(private val self: Rep) {
    //def reduceStep = self optionIf graph.reduceStep _
    def showGraph = graph.showGraph(self)
    def showGraphRev = graph.showGraphRev(self)
    def showRep = graph.showRep(self)
    def iterator = graph.iterator(self)
  }
  
  implicit class GraphDefOps(private val self: Def) {
    def isSimple = self match {
      //case _: SyntheticVal => false  // actually considered trivial?
      case _: LeafDef => true
      case _ => false
    }
  }
  
  // TODO remove this old impl; we now use rewritings and `substituteVal`
  /*
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
  */
  
}
