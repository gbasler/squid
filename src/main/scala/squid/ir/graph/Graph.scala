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
  
  def rebind(v: Val, d: Def): Unit = edges += v -> d
  def rebind(r: Rep, d: Def): Unit = rebind(r.bound, d)
  
  ////class Rep(v: Val)
  //type Rep = Val
  //class SyntheticVal(name: String, typ: TypeRep, annots: List[Annot]=Nil) extends BoundVal("@"+name: String)(typ, annots) {
  //  override def toString = {
  //    val d = dfnOrGet(this)
  //    if (d.isSimple) super.toString else s"${super.toString} = ${d}"
  //  }
  //}
  ////class Node(name: String)(typ: TypeRep, annots: List[Annot]=Nil) extends BoundVal("@"+name: String)(typ, annots) {
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
  class SyntheticVal(name: String, typ: TypeRep, annots: List[Annot]=Nil) extends BoundVal("@"+name: String)(typ, annots) {
  //class SyntheticVal(name: String, typ: TypeRep, annots: List[Annot]=Nil) extends BoundVal("@"+name: String)(typ, annots) with Rep {
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
    def dfn: Def = edges.getOrElse(bound, bound)
    
    override def equals(that: Any) = that match {
      case r: Rep => r.bound === bound
      case _ => false
    }
    override def hashCode = bound.hashCode
    
    override def toString = {
      val d = dfn
      if (d.isSimple) d.toString else s"$bound = $d"
    }
    def simpleString = {
      val d = dfn
      if (d.isSimple) d.toString else bound.toString
    }
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
      new Rep(v) alsoDo rebind(v, d)
    }
    def unapply(e: Rep) = Some(e.dfn)
  }
  
  case class Call(cid: CallId, result: Rep) extends SyntheticVal("C"+cid, result.typ) {
  //case class Call(call: Id, result: Rep) extends Rep {
    
  }
  ////case class Arg(nodes: mutable.Map[Option[Id], Rep]) extends SyntheticVal("C") {
  case class Arg(cid: CallId, cbr: Rep, els: Option[Rep]) extends SyntheticVal("A"+cid, cbr.typ) {
  //case class Arg(cid: Id, cbr: Rep, els: Option[Rep]) extends Rep {
    
  }
  // TODO Q: should this one be encoded with a normal MethodApp? -> probably not..
  case class Split(scrut: Rep, branches: Map[CtorSymbol, Rep]) extends SyntheticVal("S", branches.head._2.typ) {
  //case class Split(scrut: Rep, branches: Map[CtorSymbol, Rep]) extends Rep {
    
  }
  
  override protected def unboundVals(d: Def): Set[Val] = d match {
    case Call(cid, res) => res.dfn.unboundVals
    case Arg(cid, cbr, els) => cbr.dfn.unboundVals ++ els.dlof(_.dfn.unboundVals)(Set.empty)
    case _:SyntheticVal => ??? // TODO
    case _ => super.unboundVals(d)
  }
  
  //override def prettyPrint(d: Def) = d match {
  //  case Node(d) if d.isSimple =>
  //    ???
  //    prettyPrint(d)
  //  case _ => super.prettyPrint(d)
  //}
  override def prettyPrint(d: Def) = (new DefPrettyPrinter)(d)
  class DefPrettyPrinter extends super.DefPrettyPrinter {
    override val showValTypes = false
    override val desugarLetBindings = false
    override def apply(r: Rep): String = r match {
      case Rep(d) if d.isSimple => apply(d)
      //case _ => super.apply(r)
      case _ => super.apply(r.bound)
    }
    //override def apply(d: Def): String = if (get.isSimple) {
    override def apply(d: Def): String = d match {
      case Call(cid, res) => s"C[$cid](${res |> apply})"
      case Arg(cid, cbr, els) => s"$cid->${cbr |> apply}" + els.fold("")("|"+apply(_))
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
      case bv: BoundVal =>
        new Rep(bv)
      case _ =>
        val v = freshBoundVal(dfn.typ)
        rebind(v, dfn)
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
    //if (done(r)) Iterator.empty else {
    //  done(r) = true
    //  Iterator.single(r) ++ defn.mkIterator
    //}
    //done.setAndIfUnset(r, Iterator.single(r) ++ mkDefIterator(dfnOrGet(r)), Iterator.empty)
    done.setAndIfUnset(r.bound,
      if (rev) mkDefIterator(r.dfn) ++ Iterator.single(r) else Iterator.single(r) ++ mkDefIterator(r.dfn),
      Iterator.empty)
  def mkDefIterator(dfn: Def)(implicit rev: Bool, done: mutable.HashSet[Val]): Iterator[Rep] = dfn match {
    case MethodApp(self, mtd, targs, argss, tp) =>
      mkIterator(self) ++ argss.flatMap(_.reps.flatMap(mkIterator))
    case Abs(_, b) => mkIterator(b)
    case Ascribe(r, _) => mkIterator(r)
    case Module(r, _, _) => mkIterator(r)
    //case Rep(d) => mkDefIterator(d) // TODO rm?
    case Call(cid, res) =>
      mkIterator(res)
    case Arg(cid, cbr, els) =>
      mkIterator(cbr) ++ els.iterator.flatMap(mkIterator)
    case _:SyntheticVal => ??? // TODO
    case _: LeafDef => Iterator.empty
    //case Constant(_)|BoundVal(_)|CrossStageValue(_, _)|HoleClass(_, _)|StaticModule(_) => Iterator.empty
    //case Abs(_, _) | Ascribe(_, _) | MethodApp(_, _, _, _, _) | Module(_, _, _) | NewObject(_) | SplicedHoleClass(_, _) => ???
  }
  
  
  override def letin(bound: BoundVal, value: Rep, body: => Rep, bodyType: TypeRep) =
    //??? // oops, need a Node here
    //{edges += bound -> value} thenReturn body
    //new Rep(bound, value.dfn) thenReturn body
    rebind(bound, value.dfn) thenReturn body
    //body alsoDo rebind(bound, value.dfn)
  
  
  
  
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
    def applyTopLevel(r: Rep) = {
      
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
          case Arg(_, cbr, els) => addptr(cbr); els.foreach(addptr)
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
        case Arg(_, cbr, els) => analyse2(cbr); els.foreach(analyse2)
        case _: LeafDef =>
      })
      analyse2(r)(Set.empty)
      assert(alwaysBoundAt.isEmpty)
      alwaysBoundAt ++= analysed2.groupBy(_._1).mapValues(_.unzip._2.reduce(_ & _))
      //println(s"alwaysBoundAt: ${alwaysBoundAt.map({case(k,v)=>s"\n\t${k.bound} -> ${v.mkString(",")}"})}")
      //println(s"alwaysBoundAt: ${alwaysBoundAt.map({case(k,v)=>s"\n [${pointers(k).size}] \t${k.bound} -> ${v}"}).mkString}")
      println(s"alwaysBoundAt: ${alwaysBoundAt.map({case(k,v)=>s"\n [${pointers(k).size}] \t${v} \t${k.bound}"}).mkString}")
      
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
      cctx ::= mutable.Buffer.empty
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
    var cctx: List[mutable.Buffer[CallId]] = mutable.Buffer.empty[CallId]::Nil
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
      apply(res) alsoDo cctx.head.remove(cctx.head.indices.last)
    case Arg(cid, cbr, els) =>
      //println(s"Arg $r")
      if (cctx.head.nonEmpty) {
        //assert(ctx.head.last === cid)
        if (cctx.head.last === cid) {
          cctx.head.remove(cctx.head.indices.last)
          apply(cbr) alsoDo {cctx.head += cid}
        } else els.fold(???)(apply) // TODO B/E
      }
      //else apply(els.get.bound/*TODO B/E*/) alsoDo {params.head += els.get}
      else if (params.isEmpty) els.fold(???)(apply) // TODO B/E
      //else bindOrGet(r.bound) alsoDo {params.head += r}
      else {
        //if (!(r.dfn.unboundVals subsetOf params.head._1)) println(s"Oops: $r ${r.dfn.unboundVals} ill-scoped in ${params.head._1}")
        //println(s"Arg $r ${r.dfn.unboundVals} scoped in ${params.head._1}")
        //if (r.dfn.unboundVals subsetOf params.head._1) recv(r.bound) |> newBase.readVal alsoDo {params.head._2 += r}
        if (vctx.isEmpty || (r.dfn.unboundVals subsetOf vctx)) recv(r.bound) |> newBase.readVal alsoDo {params.head._2 += r}
        else { // cannot extract impl node as a parameter, because it refers to variables not bound at all calls
          // FIXME should use flow analysis to know 'variables not bound at all calls' --- and also other things?
          // TODO also do this if the expression is effectful or costly and we're in a path that may not always be taken! -- unless ofc we're targetting a pure lazy language like Haskell
          //println(s"Oops: $r ${r.dfn.unboundVals} ill-scoped in ${params.head._1} ${r.dfn.unboundVals -- params.head._1 toList}")
          println(s"Oops: ${r.dfn.unboundVals} not in ${vctx} for:  ${r.showGraphRev}")
          
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
            params.head._2 += Rep.bound(k, Arg(cid, lambda(extrudedVals, cbr), Some(lambda(extrudedVals, els.getOrElse(???)))))
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
    println(s"> Reduce $r")
    
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
      case `p` => rebind(r, Arg(cid, arg, Some(els)))
      //case `p` => Rep(Arg(cid, arg, Some(r.dfn.toRep)))
      case Call(_, res) => rec(res)
      case Arg(_, cbr, els) => rec(cbr); els.foreach(rec)
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
