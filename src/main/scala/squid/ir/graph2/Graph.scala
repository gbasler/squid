// Copyright 2018 EPFL DATA Lab (data.epfl.ch)
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

package squid.ir
package graph2

import squid.ir.graph.CallId
import squid.utils._
import squid.utils.CollectionUtils.MutSetHelper
import squid.utils.meta.{RuntimeUniverseHelpers => ruh}

import scala.collection.immutable.ListSet
import scala.collection.mutable

/*

Important invariant: lambdas don't get rebound to different symbols; indeed we currently rely on MirrorVal#abs to durably
point to the associated lambda.

*/
class Graph extends AST with GraphScheduling with GraphRewriting with CurryEncoding { graph =>
  
  val edges = new java.util.WeakHashMap[Val,Node]
  val lambdaBound = new java.util.WeakHashMap[Val,Val]
  
  def showEdges =
    collection.JavaConverters.mapAsScalaMap(edges).map(_ ||> ("\t" + _ + " -> " + _)).mkString("\n")
  
  def bind(v: Val, d: Node): Unit = {
    //require(!edges.isDefinedAt(v))
    require(!edges.containsKey(v))
    rebind(v, d)
  }
  def rebind(v: Val, d: Node): Unit = {
    //println(s"$v !> $d")
    //require(!v.isInstanceOf[MirrorVal])
    require(v =/= BranchVal)
    //require(!d.isInstanceOf[BoundVal] || d.isInstanceOf[SyntheticVal], s"$d")  // TODO enforce?
    //edges += v -> d
    edges.put(v, d)
  }
  //def rebind(r: Rep, d: Def): r.type = rebind(r.bound, d) thenReturn r
  def isBound(v: Val) = edges.containsKey(v)
  def boundTo_?(v: Val) = edges.get(v)
  def boundTo_!(v: Val) = boundTo_?(v) also (r => require(r =/= null, v))
  def boundTo(v: Val) = Option(boundTo_?(v))
  
  //val NothingType = Predef.implicitType[Nothing].rep
  val Bottom = MethodApp(staticModule("squid.lib.package"),
    loadMtdSymbol(loadTypSymbol("squid.lib.package$"), "$u22A5", None), Nil, Nil, NothingType)
  
  object BranchVal extends BoundVal("<branch>")(NothingType, Nil)
  //lazy val BranchRep = BranchVal.toRep
  
  //class MirrorVal(v: Val) extends BoundVal("@"+v.name)(v.typ,Nil) {
  //  require(!v.isInstanceOf[MirrorVal])
  //}
  //class MirrorVal(v: Val) extends CrossStageValue(v,v.typ) {
  class MirrorVal(val v: Val) extends HoleClass(v.name,v.typ)(None,None) {
    private[graph2] var abs: Rep = null
    def getAbs = abs.boundTo.asInstanceOf[ConcreteNode].dfn.asInstanceOf[Abs] // assumes doesn't change... (big assumption)
    def setAbs(a: Rep) = { require(abs === null); require(a.boundTo.asInstanceOf[ConcreteNode].dfn.isInstanceOf[Abs], a.boundTo); abs = a }
    def rebindAbs(newAbs:Abs) = rebind(abs.bound,ConcreteNode(newAbs))
    override def equals(obj: Any) = obj match {
      case MirrorVal(w) => v === w
      case _ => false
    }
  }
  object MirrorVal {
    def unapply(arg: MirrorVal): Some[Val] = Some(arg.v)
  }
  
  type CtorSymbol = Class[_]
  
  sealed trait RepOrNode
  
  sealed abstract class Node extends RepOrNode {
    def typ: TypeRep
    def isSimple: Bool
    def mkRep = this match {
      case ConcreteNode(v:Val) =>
        assert(isBound(v))
        new Rep(v) //alsoDo {bind(v,this)}
      case _ =>
        val v = freshBoundVal(typ)
        bind(v, this)
        new Rep(v)
    }
    def mkString(showInlineNames: Bool = true, showInlineCF:Bool = true) =
      new DefPrettyPrinter(showInlineNames, showInlineCF) apply this
  }
  object Node {
    
  }
  
  // TODO rm?
  def boundToNotVal(v: Val): Node = boundTo_!(v) match {
    case ConcreteNode(v: Val) => boundToNotVal(v)
    case n => n
  }
  
  class Rep(val bound: Val) extends RepOrNode {
    def boundTo = graph.boundTo_!(bound)
    def boundToNotVal = graph.boundToNotVal(bound)
    def typ = bound.typ
    
    def iterator = graph.iterator(this)
    def showGraph = graph.showGraph(this)
    def showFullGraph = graph.showGraph(this,true)
    def showRep = graph.showRep(this)
    def isSimple: Bool = boundTo.isSimple // FIXME?
    
    def asNode: Node = ConcreteNode(bound) // could also return the bound variable it may point to
    
    def simpleString = {
      val d = boundTo
      if (d.isSimple) d.toString
      else bound.toString
    }
    def fullString = s"$bound = $boundTo"
    
    override def hashCode() = bound.hashCode
    override def equals(obj: Any) = obj match {
      case n: Rep => n.bound === bound
      case _ => false
    }
    override def toString = s"$bound=$boundTo"
  }
  object Rep {
    def unapply(n: Rep) = Some(n.boundTo)
  }
  
  case class ValNode(v: Val) extends ConcreteNode(v) {
    override lazy val mkRep = super.mkRep
  }
  abstract class ConcreteNode(val dfn: Def) extends Node { // TODO rename boundTo
    require(dfn.isInstanceOf[Val] ==> this.isInstanceOf[ValNode])
    def typ = dfn.typ
    def isSimple = dfn.isSimple
    override def toString = dfn.toString // calls prettyPrint(boundTo)
  }
  object ConcreteNode {
    def apply(dfn: Def) = dfn match {
      case v: Val => ValNode(v)
      case _ => new ConcreteNode(dfn){}
    }
    //def unapply(n: Node) = Option(n.boundTo)
    def unapply(n: ConcreteNode) = Some(n.dfn)
  }
  sealed abstract class ControlFlow extends Node {
    override def toString = simpleString
    def simpleString = (new DefPrettyPrinter)(this)
    def isSimple = true // FIXME?
  }
  
  override protected def freshNameImpl(n: Int) = "$"+n
  
  /** placeholder Val that should never be in the graph. */
  //class SyntheticVal(name: String, typ: TypeRep, annots: List[Annot]=Nil) extends BoundVal("@"+name)(typ, annots)
  
  sealed abstract class Box extends ControlFlow {
    def typ: TypeRep = result.typ
    val cid: CallId
    val result: Rep
    def kind: BoxKind
  }
  object Box {
    def apply(cid: CallId, result: Rep, kind: BoxKind): ControlFlow = kind match {
      case Call => new Call(cid, result){}
      case Arg => new Arg(cid, result){}
      case Pass => new Pass(cid, result){}
    }
    //def apply(cid: CallId, result: Rep, kind: BoxKind): Node = result.boundTo match {
    //  case Branch(Condition(ops,c), thn, els) =>
    //    Branch(Condition((kind,cid)::ops,c), apply(cid,thn,kind).mkRep, apply(cid,els,kind).mkRep)
    //  case _ =>
    def unapply(box: Box) = Some(box.cid, box.result, box.kind)
  }
  sealed abstract class BoxKind {
    override def toString = this match {
      case Call => "↑"
      case Arg => "↓"
      case Pass => "|"
    }
  }
  
  //case class Call(cid: CallId, result: Rep) extends ControlFlow(new SyntheticVal("C"+cid, result.typ)) {
  abstract case class Call(cid: CallId, result: Rep) extends Box {
    def kind: Call.type = Call
  }
  object Call extends BoxKind {
    def apply(cid: CallId, result: Rep) = Box(cid, result, Call)
  }
  abstract case class Arg(cid: CallId, result: Rep) extends Box {
    def kind: Arg.type = Arg
  }
  object Arg extends BoxKind {
    def apply(cid: CallId, result: Rep) = Box(cid, result, Arg)
  }
  abstract case class Pass(cid: CallId, result: Rep) extends Box {
    def kind: Pass.type = Pass
  }
  object Pass extends BoxKind {
    def apply(cid: CallId, result: Rep) = Box(cid, result, Pass)
  }
  
  //abstract case class Branch(cid: CallId, thn: Rep, els: Rep)(typ: TypeRep) extends ControlFlow(ruh.uni.lub(thn.typ.tpe::els.typ.tpe::Nil)) {
  //case class Branch(cid: CallId, thn: Rep, els: Rep) extends ControlFlow(ruh.uni.lub(thn.typ.tpe::els.typ.tpe::Nil)) {
  //abstract 
  case class Branch(cond: Condition, thn: Rep, els: Rep) extends ControlFlow {
    lazy val typ = ruh.uni.lub(thn.typ.tpe::els.typ.tpe::Nil)
  }
  object Branch {
    /*
    def apply(cond: Condition, thn: Rep, els: Rep) =
      if (cond.isAlwaysTrue) thn.boundTo else if (cond.isAlwaysFalse) els.boundTo else new Branch(cond,thn,els){} // FIXME duplication?!
    */
  }
  abstract case class Condition(ops: List[Op], cid: CallId) {
    def isAlwaysTrue: Bool = ops === ((Call,cid)::Nil)
    def isAlwaysFalse: Bool = ops === ((Pass,cid)::Nil)
  
    override def toString = s"${ops.map{case(k,c)=>s"$k$c;"}.mkString}$cid?"
    //override def toString = Branch(this,BranchRep,BranchRep).toString
  }
  object Condition {
    def simple(cid: CallId) = Condition(Nil,cid)
    def apply(ops: List[Op], cid: CallId) = {
      // Try to simplify the ops if the condition is always true or false:
      ops.foldLeft(CCtx.empty) {
        case (cctx, op) => cctx.withOp_?(op).getOrElse(CCtx.empty)
      }.map.get(cid) match {
        case Some(Some(_)) => new Condition(Call->cid :: Nil, cid){}
        case Some(None) => new Condition(Pass->cid :: Nil, cid){}
        case _ => new Condition(ops, cid){}
      }
    }
  }
  type Op = (BoxKind,CallId)
  
  def isNormalVal(d: Def): Bool = d.isInstanceOf[BoundVal] && !d.isInstanceOf[MirrorVal]
  
  def rep(dfn: Def) = dfn match {
    //case v: Val => reificationContext.getOrElse(v, new Node(v)) // mirror val?
    case v: Val if isNormalVal(v) && reificationContext.contains(v) => reificationContext(v)
    case v: Val if isNormalVal(v) && edges.containsKey(v) => new Rep(v)
    case v: Val =>
      val occ = Option(lambdaBound.get(v)).getOrElse(???) // TODO B/E
      val mir = boundTo_!(occ)//.asInstanceOf[ConcreteNode].dfn.asInstanceOf[MirrorVal]
      assert(mir.asInstanceOf[ConcreteNode].dfn.asInstanceOf[MirrorVal].v === v)
      new Rep(occ)
    case _ =>
      val v = freshBoundVal(dfn.typ)
      bind(v, ConcreteNode(dfn))
      new Rep(v)
  }
  
  def dfn(r: Rep): Def = dfn(r.boundTo)
  def dfn(n: Node): Def = n match {
    case ConcreteNode(d) => d
    //case _: ControlFlow => ControlFlowVal
    case Box(_,r,_) => dfn(r)
    case Branch(_,_,_) => BranchVal
  }
  
  def repType(r: Rep) = r.typ
  
  
  val reificationContext = mutable.Map.empty[Val,Rep]
  
  def letinImpl(bound: BoundVal, value: Rep, body: => Rep) = { 
    require(!reificationContext.contains(bound))
    try {
      reificationContext += bound -> value
      body
    } finally reificationContext -= bound 
  }
  
  override def letin(bound: BoundVal, value: Rep, body: => Rep, bodyType: TypeRep) =
    letinImpl(bound,value,body)
  
  override def abs(param: BoundVal, body: => Rep): Rep = {
    //letinImpl(param, rep(new MirrorVal(param)), super.abs(param, body))
    
    val mirr = new MirrorVal(param)
    val occ = rep(mirr)
    lambdaBound.put(param, occ.bound)
    letinImpl(param, occ, super.abs(param, body) also mirr.setAbs)
  }
  
  
  // TODO support non-evaluation of mkArg in case no occurrences reachable?
  override def substituteVal(r: Rep, v: BoundVal, mkArg: => Rep): Rep = {
    val cid = new CallId("α")
    
    val occ = Option(lambdaBound.get(v)).getOrElse(???) // TODO B/E
    //println(s"OCC $occ")
    val mir = boundTo_!(occ).asInstanceOf[ConcreteNode].dfn.asInstanceOf[MirrorVal]
    assert(mir.v === v)
    
    val newOcc = mir.toRep
    val arg = mkArg
    val bran = Branch(Condition.simple(cid), arg, newOcc)
    
    rebind(occ, bran)
    lambdaBound.put(v,newOcc.bound)
    
    // Q: does this create code dup?
    /*
    val body = r.boundTo.mkRep
    rebind(r.bound, Pass(cid, body))
    */
    /*
    val origBody = mir.getAbs.body
    val body = origBody.boundTo.mkRep
    rebind(origBody.bound, Pass(cid, body))
    */
    
    val abs = mir.getAbs
    //println(s"ABS ${mir.abs}")
    assert(mir.getAbs =/= null)
    val body = abs.body
    //println(s"ABSd ${abs}")
    mir.rebindAbs(Abs(abs.param, Pass(cid, body).mkRep)(abs.typ))
    //println(s"NABS ${mir.abs}")
    
    Call(cid, r).mkRep
  }
  
  
  
  
  //def showGraph(rep: Rep): String = rep.simpleString + {
  def showGraph(rep: Rep, full: Bool = false): String = s"${rep.bound} = ${rep.boundTo.mkString(!full,!full)}" + {
    //val defsStr = iterator(rep).collect { case r @ Rep(ConcreteNode(d)) if !d.isSimple => s"\n\t${r.bound} = ${d};" }.mkString
    val defsStr = iterator(rep).toList.distinct.filterNot(_ === rep).collect {
      case r if full =>
        s"\n\t${r.bound} = ${r.boundTo.mkString(false,false)};"
      case r @ Rep(ConcreteNode(d)) if !d.isSimple => s"\n\t${r.bound} = ${d};"
    }.mkString
    if (defsStr.isEmpty) "" else " where:" + defsStr
  }
  def iterator(r: Rep): Iterator[Rep] = mkIterator(r)(false,mutable.HashSet.empty)
  def mkIterator(r: Rep)(implicit rev: Bool, done: mutable.HashSet[Val]): Iterator[Rep] = done.setAndIfUnset(r.bound, {
    val ite = r.boundTo match {
    //val ite = r.boundToNotVal match { // misses to the iterate the Rep's on the way!
      case cn@ConcreteNode(v:Val) => mkIterator(cn.mkRep)
      case r: ConcreteNode => mkDefIterator(r.dfn)
      case Box(_,res,_) => mkIterator(res) 
      case Branch(cid,thn,els) => mkIterator(thn) ++ mkIterator(els)
    }
    val site = Iterator.single(r)
    if (rev) ite ++ site else site ++ ite
  }, Iterator.empty)
  def mkDefIterator(dfn: Def)(implicit rev: Bool, done: mutable.HashSet[Val]): Iterator[Rep] = dfn.children.flatMap(mkIterator)
  
  implicit class GraphDefOps(private val self: Def) {
    def isSimple = self match {
      //case _: SyntheticVal => false  // actually considered trivial?
      case _: LeafDef => true
      case Bottom => true
      case _ => false
    }
  }
  
  private val colors = List(/*Console.BLACK,*/Console.RED,Console.GREEN,Console.YELLOW,Console.BLUE,Console.MAGENTA,Console.CYAN,
    /*Console.WHITE,Console.BLACK_B,Console.RED_B,Console.GREEN_B,Console.YELLOW_B,Console.BLUE_B,Console.MAGENTA_B,Console.CYAN_B*/)
  private def colorOf(cid: CallId) = colors(cid.uid%colors.size)
  
  override def prettyPrint(d: Def) = (new DefPrettyPrinter)(d)
  class DefPrettyPrinter(showInlineNames: Bool = true, showInlineCF:Bool = true) extends super.DefPrettyPrinter {
    val printed = mutable.Set.empty[Rep]
    override val showValTypes = false
    override val desugarLetBindings = false
    var curCol = Console.BLACK
    override def apply(r: Rep): String = printed.setAndIfUnset(r, (r.boundTo match {
      case _ if !showInlineCF => super.apply(r.bound)
      case ConcreteNode(d) if !d.isSimple => super.apply(r.bound)
      case n => (if (showInlineNames) Debug.GREY +r.bound+":" + curCol else "")+apply(n)
    }) alsoDo {printed -= r}, s"[RECURSIVE ${super.apply(r.bound)}]")
    override def apply(d: Def): String = d match {
      case Bottom => "⊥"
      case MirrorVal(v) => s"<$v>"
      case _ => super.apply(d)
    }
    def apply(n: Node): String = n match {
      case Pass(cid, res) =>
        val col = colorOf(cid)
        s"$col⟦$cid⟧$curCol ${res |> apply}"
      case Call(cid, res) =>
        val col = colorOf(cid)
        s"$col⟦$cid$curCol ${res |> apply}$col⟧$curCol"
      case Arg(cid, res) =>
        val col = colorOf(cid)
        //s"$col$cid⟨⟩$curCol${res|>apply}"
        s"⟦$col$cid⟧$curCol${res|>apply}"
      case Branch(Condition(ops,cid), cbr, els) =>
        val oldCol = curCol
        curCol = colorOf(cid)
        //s"${cid}⟨${cbr |> apply}⟩$oldCol${curCol = oldCol; apply(els)}"
        s"(${ops.map{case(k,c)=>s"$k$c;"}.mkString}$curCol$cid ? ${cbr |> apply} ¿ $oldCol${curCol = oldCol; apply(els)})"
      case cn@ConcreteNode(v:Val) => apply(cn.mkRep)
      case ConcreteNode(d) => apply(d)
    }
  }
  
  
}
