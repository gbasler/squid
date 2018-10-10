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
  class Rep(initialDef: Def) {
    //lazy val bound: Val = 
    private var _bound: Val = _
    private def bind(v: Val) = {
      _bound = v
      edges += v -> initialDef
    }
    def asBoundBy(v: Val) = {
      //println(s"$initialDef bound by $v")
      assert(_bound === null)
      bind(v)
      this
    }
    def maybeBound = Option(_bound)
    def bound = {
      //if (_bound === null) bind(freshBoundVal(initialDef.typ))
      //if (_bound === null) bind(initialDef match {
      //  case v: Val => v
      //  case _ => freshBoundVal(initialDef.typ)
      //})
      if (_bound === null) initialDef match {
        case v: Val => _bound = v
        case _ => bind(freshBoundVal(initialDef.typ))
      }
      _bound
    }
    def dfn: Def = edges.getOrElse(bound, bound)
    //def dfn: Def = edges.getOrElse(bound, ???)
    def maybeDfn: Def = maybeBound flatMap edges.get getOrElse initialDef
    
    override def equals(that: Any) = that match {
      case r: Rep => r.bound === bound
      case _ => false
    }
    override def hashCode = bound.hashCode
    
    override def toString = {
      val d = maybeDfn
      if (d.isSimple) d.toString else s"${maybeBound getOrElse "<...>"} = $maybeDfn"
    }
    def simpleString = {
      val d = dfn
      if (d.isSimple) d.toString else bound.toString
    }
  }
  //object Expr {
  object Rep {
    def apply(d: Def) = new Rep(d)
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
  
  def rep(dfn: Def) = Rep(dfn)
  //def rep(dfn: Def) = dfn match {
  //  case v: Val => v
  //  //case _ => freshBoundVal(dfn.typ) alsoApply {edges += _ -> dfn}
  //  case _ => new SyntheticVal(freshName.tail, dfn.typ) also {edges += _ -> dfn}
  //}
  
  //def dfn(r: Rep) = edges(r)
  //def dfn(r: Rep) = r match { case Node(d) => d  case bv => bv }
  //def dfn(r: Rep): Def = r
  def dfn(r: Rep): Def = r.dfn
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
      if (rev) mkDefIterator(dfn(r)) ++ Iterator.single(r) else Iterator.single(r) ++ mkDefIterator(dfn(r)),
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
    case Constant(_)|BoundVal(_)|CrossStageValue(_, _)|HoleClass(_, _)|StaticModule(_) => Iterator.empty
    //case Abs(_, _) | Ascribe(_, _) | MethodApp(_, _, _, _, _) | Module(_, _, _) | NewObject(_) | SplicedHoleClass(_, _) => ???
  }
  
  
  override def letin(bound: BoundVal, value: Rep, body: => Rep, bodyType: TypeRep) =
    //??? // oops, need a Node here
    //{edges += bound -> value} thenReturn body
    value.asBoundBy(bound) thenReturn body
  
  
  
  
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
  
  abstract class Reinterpreter extends super.Reinterpreter {
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
      }
    }
  }
  override def reinterpret(r: Rep, NewBase: squid.lang.Base)(ExtrudedHandle: (BoundVal => NewBase.Rep) = DefaultExtrudedHandler): NewBase.Rep =
    new Reinterpreter {
      val newBase: NewBase.type = NewBase
      override val extrudedHandle = ExtrudedHandle
    } applyTopLevel r apply ()
  
  
  implicit class GraphRepOps(private val self: Rep) {
    //def reduceStep: Rep = graph.reduceStep(self) thenReturn self
    def reduceStep = self optionIf graph.reduceStep _
    def showGraph = graph.showGraph(self)
    def iterator = graph.iterator(self)
  }
  
  implicit class GraphDefOps(private val self: Def) {
    def isSimple = self match {
      case _: SyntheticVal => false
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
class CallId(val name: String) {
  val uid: Int = CallId.curId alsoDo (CallId.curId += 1)
  def uidstr: String = s"$name$uid"
  override def toString: String = uidstr
}
