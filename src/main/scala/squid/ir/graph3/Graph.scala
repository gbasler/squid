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

package squid.ir
package graph3

import squid.utils._
import squid.utils.CollectionUtils.MutSetHelper
import squid.utils.meta.{RuntimeUniverseHelpers => ruh}

import scala.collection.immutable.ListSet
import scala.collection.mutable

/*

Improvements:
  - There is no way to duplicate the linked list of branches when we're just interested in ONE reduction! We should
    reorder branches before making reductions (in simplifier and rewriter), instead of accounting for all the branches
    on the way!

Possible improvements:
  - an unrolling factor which needs parametrizing tokens and branches, allowing to push a stop past a branch of the same Val

*/
class Graph extends AST with GraphScheduling with GraphRewriting with CurryEncoding { graph =>
  
  val showPopDropOrigins = false
  
  override protected def freshNameImpl(n: Int) = "$"+n.toHexString
  
  override def showVal(v: BoundVal): String = v.toString
  
  /** Whether to check call identifiers for Pop and Drop. */
  val strictCallIdChecking = false
  
  /** Whether to always push boxes into branches, though it may lead to more complicated graphs. */
  val aggressiveBoxPushing = false
  
  /** Whether to support direct recursion in the graph, which corresponds to the lifted form of recursive functions. */
  val supportDirectRecursion = false
  
  object CallId {
    private var curId = 0; def reset(): Unit = curId = 0
  }
  class CallId(val v: Val, dummy: Bool = false) {
    val uid: Int = CallId.curId alsoDo (CallId.curId += 1)
    //def uidstr: String = s"${v.name}$uid"
    def uidstr: String = if (dummy) s"~$v:$uid~" else s"$v:$uid"
    override def toString: String = uidstr
  }
  
  protected var freshnessCount = 0
  override def wrapConstruct(r: => Rep) = { freshnessCount += 1; super.wrapConstruct(r) }
  
  class Rep(var node: Node) {
    
    protected var freshness = freshnessCount
    def isFresh: Bool = freshness === freshnessCount
    
    // TODO check all usages; there might be obsolete usages of this, which is now...
    /** Only used for debuggability, to give a nice name to the Rep. */
    val bound: Val = freshBoundVal(typ)
    
    def showGraph = graph.showGraph(this)
    def showFullGraph = showGraph // TODO
    def eval = graph.eval(this)
    def typ: TypeRep = node.typ
    
    def fullString = toString // TODO
    def simpleString = bound.name
    
    def rewireTo(that: Rep): Unit = node = Id(that)
    
    /** To avoid; only use if `that` is fresh and won't be used again; otherwise we will duplicate its node. */
    @deprecated("Just don't", "?")
    def hardRewireTo_!(that: Rep): Unit = {
      require(that.isFresh)
      that.freshness = freshnessCount - 1
      node = that.node
    }
    
    def allChildren: mutable.Set[Rep] = {
      val traversed = mutable.Set.empty[Rep]
      var workList = this :: Nil
      while (workList.nonEmpty) {
        val cur = workList.head
        workList = workList.tail
        if (!traversed(cur)) {
          traversed += cur
          workList = workList ::: cur.node.children.toList
        }
      }
      traversed
    }
    def size: Int = allChildren.filter(!_.node.isInstanceOf[Box]).toSet.size
    
    // TODO rm?
    def directlyReachable: collection.Set[Rep] = {
      val traversed = mutable.Set.empty[Rep]
      def rec(rep: Rep): Unit = if (!traversed(rep)) {
        traversed += rep
        rep.node match {
          case Box(_, body) => rec(body)
          case Branch(_, _, thn, els) =>
            rec(thn)
            rec(els)
          case _: ConcreteNode =>
        }
      }
      rec(this)
      traversed
    }
    
    override def toString = s"$bound = $node"
  }
  object Rep {
    def unapply(r: Rep): Some[Node] = Some(r.node)
    def withVal(v: Val, d: Def) = new Rep(ConcreteNode(d)) {
      override val bound = v
    }
  }
  
  sealed abstract class Node {
    def children: Iterator[Rep]
    def typ: TypeRep
    def mkRep = new Rep(this)
    override def toString = printNode(this)
    
    /** Used to make sure we're not duplicating a Val node, which would be bad as it would mess up with `lambdaVariableOccurrences` */
    def assertNotVal: this.type =
      assert(!this.isInstanceOf[ConcreteNode] || !this.asInstanceOf[ConcreteNode].dfn.isInstanceOf[Val],
        s"$this <: ConcreteNode(_:Val)") thenReturn this
  }
  def printNode(n: Node) = (new DefPrettyPrinter)(n)
  
  object ConcreteRep {
    def unapply(arg: Rep): Opt[Def] = arg |>? {
      case Rep(ConcreteNode(d)) => d
    }
  }
  case class ConcreteNode(dfn: Def) extends Node {
    def typ: TypeRep = dfn.typ
    def children = dfn.children
    //override def toString = s"$dfn"
  }
  case class Box(ctrl: Control, body: Rep) extends Node {
    def typ: TypeRep = body.typ
    def children: Iterator[Rep] = Iterator(body)
    //override def toString = s"$bound = [$cid? ${lhs.bound} ¿ ${rhs.bound}]"
  }
  object Box {
    def rep(ctrl: Control, body: Rep): Rep = if (ctrl === Id) body else body.node match {
      case Box(ctrl2, body2) => Box.rep(ctrl `;` ctrl2, body2)
      case _ => Box(ctrl, body).mkRep
    }
  }
  case class Branch(ctrl: Control, cid: CallId, lhs: Rep, rhs: Rep) extends Node {
    lazy val typ: TypeRep = branchType(lhs.typ, rhs.typ)
    // ^ when this was a `def`, it used to become the bottleneck in branch-deep graphs!
    
    def children: Iterator[Rep] = Iterator(lhs,rhs)
    //override def toString = s"[$cid? ${lhs.bound} ¿ ${rhs.bound}]"
  }
  def branchType(lhs: => TypeRep, rhs: => TypeRep): TypeRep = ruh.uni.lub(lhs.tpe :: rhs.tpe :: Nil)
  
  sealed abstract class Control {
    
    def `;` (that: Control): Control =
    //println(s"($this) ; ($that)") thenReturn (
    (this,that) match {
      case (Id, _) => that
      case (Push(cid, pl, rest), t: TransitiveControl) => Push(cid, pl, rest `;` t)
      case (Push(cid, pl, rest0), rest1) => Push(cid, pl, rest0 `;` rest1)
      case (p @ Pop(rest0), rest1) => Pop(rest0 `;` rest1)(p.originalVal)
      case (d @ Drop(rest0), rest1) => Drop(rest0 `;` rest1)(d.originalCid)
    }
    //) also ("= (" + _ + ")" also println)
    
    def lastCallId = lastPush.map(_.cid)
    def lastPush: Opt[Push] = this match {
      case p: Push => p.rest.lastPush orElse Some(p)
      case Pop(rest) => rest.lastPush
      case Drop(rest) => rest.lastPush
      case Id => None
    }
    
    def push(cid: CallId) = this `;` Push(cid,Id,Id)
    
    ////def throughLambda: Control = this `;` Push(DummyCallId,Pop(Id),Id)
    //def throughLambda: Control = Push(DummyCallId,this `;` Pop(Id),Id)
    //// ^ TODO use in matcher? – it did not seem sufficient, though.... we probably have to create a new variable and propoer substitution.
    
    override def toString = (new DefPrettyPrinter)(this).mkString(";")
  }
  sealed abstract case class Push(cid: CallId, payload: Control, rest: TransitiveControl) extends TransitiveControl {
  }
  object Push {
    def apply(cid: CallId, payload: Control, rest: TransitiveControl): TransitiveControl = new Push(cid, payload, rest){}
    def apply(cid: CallId, payload: Control, rest: Control): Control = rest match {
      case p @ Pop(rest2) =>
        assert(!strictCallIdChecking || cid === DummyCallId || cid.v === p.originalVal, s"${cid.v} === ${p.originalVal} in Push($cid, $payload, $rest)")
        payload `;` rest2
      case d @ Drop(rest2) =>
        assert(!strictCallIdChecking || cid === DummyCallId ||
          // Note that the following assertion is quite drastic, and prevents us from blindly pushing boxes into branches.
          // It would be sufficient to only check cid.v, but I prefer to make assertions as strict as possible. 
          cid === d.originalCid, s"$cid === ${d.originalCid} in Push($cid, $payload, $rest)"
          // A less drastic version:
          //cid.v === d.originalCid.v, s"${cid.v} === ${d.originalCid.v} in Push($cid, $payload, $rest)"
        )
        rest2
      case rest: TransitiveControl => apply(cid, payload, rest)
    }
  }
  case class Pop(rest: Control)(val originalVal: Val) extends Control {
  }
  object Pop { def it(originalVal: Val) = Pop(Id)(originalVal) }
  
  case class Drop(rest: Control)(val originalCid: CallId) extends Control {
  }
  object Drop { def it(originalCid: CallId) = Drop(Id)(originalCid) }
  
  sealed abstract class TransitiveControl extends Control {
    def `;` (that: TransitiveControl): TransitiveControl = (this,that) match {
      case (Id,_) => that
      case (_,Id) => this
      case (Push(cid0, pl0, rest0), t) => Push(cid0, pl0, rest0 `;` t)
    }
  }
  case object Id extends TransitiveControl {
    /** It's sometimes useful to rewire a node to another node, without having to duplicate its Def! */
    def apply(r: Rep) = Box(Id,r)
  }
  
  
  //def dfn(r: Rep): Def = r.dfn
  def dfn(r: Rep): Def = r.bound
  
  //def rep(dfn: Def) = new Rep(dfn, dfn.children.toList)
  def rep(dfn: Def) = dfn match {
    case v: Val if reificationContext.contains(v) => reificationContext(v)
    case v: Val => lastWords(s"$v not in ${reificationContext}")
    case _ => new Rep(ConcreteNode(dfn))
  }
  //def simpleRep(dfn: Def): Rep = rep(dfn)
  def repType(r: Rep) = r.typ
  
  def mkValRep(v: Val) = {
    assert(!reificationContext.contains(v))
    new Rep(ConcreteNode(v))
  }
  
  
  var reificationContext = Map.empty[Val,Rep]
  
  def letinImpl(bound: BoundVal, value: Rep, mkBody: => Rep) = { 
    require(!reificationContext.contains(bound))
    val old = reificationContext
    try {
      reificationContext += bound -> value
      mkBody
    } finally reificationContext = old 
  }
  
  override def letin(_bound: BoundVal, value: Rep, mkBody: => Rep, bodyType: TypeRep) =
    letinImpl(_bound, if (value.isFresh) new Rep(value.node) {
      override val bound = _bound
      node match {
        case ConcreteNode(a: Abs) => // Make sure to update the variable->lambda mapping
          assert(lambdaVariableBindings.containsKey(a.param))
          lambdaVariableBindings.put(a.param,this)
        case _ =>
      }
    } else value, mkBody)
  
  private var ignoreValBindings = false
  def ignoreValBindings_![R](mkRes: => R) = {
    val prev = ignoreValBindings
    try {
      ignoreValBindings = true
      mkRes
    } finally ignoreValBindings = prev
  }
  
  // TODO make more robust? check for illegal occurrences in non-fresh inserted capturing lambdas...?
  override def abs(param: BoundVal, mkBody: => Rep): Rep = {
    val occ = mkValRep(param)
    if (!ignoreValBindings) {
      assert(!lambdaVariableOccurrences.containsKey(param))
      lambdaVariableOccurrences.put(param, occ)
    }
    val old = reificationContext
    try {
      reificationContext = reificationContext.map{case(k,v) => (k,Box.rep(Pop.it(param),v))}
      letinImpl(param, occ, super.abs(param, mkBody) also {lambdaVariableBindings.put(param,_)})
    } finally reificationContext = old
  }
  
  //class MirrorVal(v: Val) extends BoundVal("@"+v.name)(v.typ,Nil)
  protected val lambdaVariableOccurrences = new java.util.WeakHashMap[Val,Rep]
  protected val lambdaVariableBindings = new java.util.WeakHashMap[Val,Rep] // TODO rm
  // ^ TODO: use a more precise RepOf[NodeSubtype] type
  
  // TODO only evaluate mkArg if the variable actually occurs (cf. mandated semantics of Squid)
  override def substituteVal(r: Rep, v: BoundVal, mkArg: => Rep): Rep = substituteVal(r, v, mkArg, Id)
  def substituteVal(r: Rep, v: BoundVal, mkArg: => Rep, payload: Control): Rep = {
    val cid = new CallId(v)
    
    val occ = Option(lambdaVariableOccurrences.get(v)).getOrElse(???) // TODO B/E
    val newOcc = mkValRep(v)
    lambdaVariableOccurrences.put(v,newOcc)
    
    //val abs = Option(lambdaVariableBindings.get(v)).getOrElse(???).node.asInstanceOf[ConcreteNode].asInstanceOf[Abs] // TODO B/E
    val lam = Option(lambdaVariableBindings.get(v)).getOrElse(???) // TODO B/E
    //assert(abs.node.asInstanceOf[ConcreteNode].dfn.isInstanceOf[Abs])
    val abs = lam.node.asInstanceOf[ConcreteNode].dfn.asInstanceOf[Abs]
    
    //println(s". $v . $occ . $newOcc . $lam . $abs . ${abs.body.showGraph}")
    
    /*
    // We replace the old Abs node... which should now be garbage-collected.
    // We do this because AST#Abs' body is not mutable
    
    //abs.body.node = Box(Block(cid), abs.body.node.mkRep)
    lam.node = ConcreteNode(Abs(v, Box(Block(cid), abs.body).mkRep)(abs.typ))
    */
    
    val arg = mkArg
    //val bran = Branch(None, cid, Box(End(cid),arg).mkRep, newOcc)
    val bran = Branch(Id, cid, Box(Drop.it(cid),arg).mkRep, newOcc)
    occ.node = bran
    
    //println(s". $v . $occ . $newOcc . $lam . $abs . ${abs.body.showGraph}")
    
    Box(Push(cid,payload,Id), r).mkRep
  }
  
  def sanityCheck(rep: Rep, fuel: Int)(implicit cctx: CCtx): Opt[Int] = if (fuel < 0) None else try {
    rep.node match {
      case Box(ctrl,res) => sanityCheck(res, fuel-1)(withCtrl_!(ctrl)) // FIXME probably
      case Branch(ctrl, cid, thn, els) =>
        if (hasCid_!(ctrl, cid))
             sanityCheck(thn, fuel/2)
        else sanityCheck(els, fuel/2)
      case ConcreteNode(d) =>
        val (newCtx, newFuel) = d match {
          case abs: Abs => // When traversing a lambda, we need to assume a token, even if just the dummy token!
            //(cctx push DummyCallId, fuel/2)
            (cctx push new CallId(abs.param), fuel/2)
          case _ => (cctx, fuel)
        }
        (Iterator(Some(fuel)) ++ d.children.map{ c => sanityCheck(c, newFuel)(newCtx)}).min // minimum on options: None is minimal
    }
  } catch {
    case e: AssertionError =>
      println(s"${Console.RED}Sanity check failed${Console.RESET} at [${cctx}] ${rep}")
      throw e
  }
  
  
  def iterator(r: Rep): Iterator[Rep] = mkIterator(r)(false,mutable.HashSet.empty)
  def mkIterator(r: Rep)(implicit rev: Bool, done: mutable.HashSet[Rep]): Iterator[Rep] = done.setAndIfUnset(r, {
    Iterator.single(r) ++ r.node.children.flatMap(mkIterator)
  }, Iterator.empty)
  
  def showGraph(rep: Rep, full: Bool = false): String = s"$rep" + {
    val defsStr = iterator(rep).toList.distinct.filterNot(_ === rep).collect {
      case r if full =>
        //s"\n\t${r.bound} = ${r.boundTo.mkString(false,false)};"
        s"\n\t${r.bound} = ${r.node};"
      case r @ Rep(ConcreteNode(d)) if !d.isSimple => s"\n\t${r.bound} = $d;"
      case r @ Rep(br: Branch) => s"\n\t${r.bound} = ${(new DefPrettyPrinter)(br)};"
    }.mkString
    if (defsStr.isEmpty) "" else " where:" + defsStr
  }
  
  implicit class GraphDefOps(private val self: Def) {
    def isSimple = self match {
      //case _: SyntheticVal => false  // actually considered trivial?
      case _: LeafDef => true
      //case Bottom => true
      case _ => false
    }
  }
  
  private val colors = List(
    //Console.BLACK,
    Console.RED,Console.GREEN,Console.YELLOW,Console.BLUE,Console.MAGENTA,Console.CYAN,
    //Console.WHITE,Console.BLACK_B,Console.RED_B,Console.GREEN_B,Console.YELLOW_B,Console.BLUE_B,Console.MAGENTA_B,Console.CYAN_B
  ) ++ (for { i <- 90 to 96 } yield s"\u001b[$i;1m") // Some alternative color versions (https://misc.flogisoft.com/bash/tip_colors_and_formatting#colors)
  private def colorOf(cid: CallId) = colors((cid.uid + colors.size - 1) % colors.size)
  
  override def prettyPrint(d: Def) = (new DefPrettyPrinter)(d)
  class DefPrettyPrinter(showInlineNames: Bool = false, showInlineCF:Bool = true) extends super.DefPrettyPrinter {
    val printed = mutable.Set.empty[Rep]
    override val showValTypes = false
    override val desugarLetBindings = false
    var curCol = Console.BLACK
    override def apply(r: Rep): String = printed.setAndIfUnset(r, (r.node match {
      //case ConcreteNode(d:Val) => Debug.GREY +r.bound+":" + curCol+apply(d)  // Useful for displaying occurrences' identities
      case _ if !showInlineCF => super.apply(r.bound)
      case ConcreteNode(d) if !d.isSimple => super.apply(r.bound)
      case _: Branch => super.apply(r.bound)
      case n => (if (showInlineNames) Debug.GREY +r.bound+":" + curCol else "")+apply(n)
    }) alsoDo {printed -= r}, s"[RECURSIVE ${super.apply(r.bound)}]")
    //override def apply(d: Def): String = d match {
    //  case Bottom => "⊥"
    //  case MirrorVal(v) => s"<$v>"
    //  case _ => super.apply(d)
    //}
    def apply(ctrl: Control): List[String] = ctrl match {
      case Id => Nil
      case Push(cid, Id, rest) =>
        val col = colorOf(cid)
        s"$col$cid↑$curCol" :: apply(rest)
      case Push(cid, payload, rest) =>
        val col = colorOf(cid)
        s"$col$cid↑[${apply(payload).mkString(";")}$col]$curCol" :: apply(rest)
      case p @ Pop(rest) if showPopDropOrigins => s"↓${Debug.GREY}(${p.originalVal})$curCol" :: apply(rest)
      case d @ Drop(rest) if showPopDropOrigins => s"🚫${Debug.GREY}(${d.originalCid})$curCol" :: apply(rest)
      case Pop(rest) => s"↓" :: apply(rest)
      case Drop(rest) => s"🚫" :: apply(rest)
    }
    def apply(n: Node): String = n match {
      case Box(ctrl, res) =>
        apply(ctrl).mkString(";") + apply(res)
      case Branch(ctrl, cid, thn, els) =>
        val oldCol = curCol
        curCol = colorOf(cid)
        s"(${if (ctrl === Id) "" else s"[$ctrl]"}$curCol$cid ? ${thn |> apply} ¿ $oldCol${curCol = oldCol; els |> apply})"
      case ConcreteNode(d) => apply(d)
    }
  }
  
  
  
}

