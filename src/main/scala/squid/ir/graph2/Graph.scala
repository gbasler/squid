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

class Graph extends AST with GraphScheduling with GraphRewriting with CurryEncoding { graph =>
  
  val edges = new java.util.WeakHashMap[Val,Def]
  
  def bind(v: Val, d: Def): Unit = {
    //require(!edges.isDefinedAt(v))
    require(!edges.containsKey(v))
    rebind(v, d)
  }
  def rebind(v: Val, d: Def): Unit = {
    //require(!v.isInstanceOf[MirrorVal])
    require(v =/= BranchVal)
    //require(!d.isInstanceOf[BoundVal] || d.isInstanceOf[SyntheticVal], s"$d")  // TODO enforce?
    //edges += v -> d
    edges.put(v, d)
  }
  //def rebind(r: Rep, d: Def): r.type = rebind(r.bound, d) thenReturn r
  def isBound(v: Val) = edges.containsKey(v)
  def boundTo_?(v: Val) = edges.get(v)
  def boundTo_!(v: Val) = boundTo_?(v) also (r => require(r =/= null))
  def boundTo(v: Val) = Option(boundTo_?(v))
  
  //val NothingType = Predef.implicitType[Nothing].rep
  val Bottom = MethodApp(staticModule("squid.lib.package"),
    loadMtdSymbol(loadTypSymbol("squid.lib.package$"), "$u22A5", None), Nil, Nil, NothingType)
  
  object BranchVal extends BoundVal("<branch>")(NothingType, Nil)
  
  //class MirrorVal(v: Val) extends BoundVal("@"+v.name)(v.typ,Nil) {
  //  require(!v.isInstanceOf[MirrorVal])
  //}
  //class MirrorVal(v: Val) extends CrossStageValue(v,v.typ) {
  class MirrorVal(val v: Val) extends HoleClass(v.name,v.typ)(None,None) {
    override def equals(obj: Any) = obj match {
      case MirrorVal(w) => v === w
      case _ => false
    }
  }
  object MirrorVal {
    def unapply(arg: MirrorVal): Some[Val] = Some(arg.v)
  }
  
  type CtorSymbol = Class[_]
  
  abstract class Rep {
    def typ: TypeRep
    def iterator = graph.iterator(this)
    def showGraph = graph.showGraph(this)
    def showRep = graph.showRep(this)
    def simpleString: String
  }
  object Rep {
    
  }
  
  class Node(val bound: Val) extends Rep {
    def boundTo = graph.boundTo_!(bound)
    def typ = bound.typ
    
    def simpleString = {
      val d = boundTo
      if (d.isSimple) d.toString
      else bound.toString
    }
    
    override def hashCode() = bound.hashCode
    override def equals(obj: Any) = obj match {
      case n: Node => n.bound === bound
      case _ => false
    }
    override def toString = s"$bound=$boundTo"
  }
  object Node {
    //def unapply(n: Node) = Option(n.boundTo)
    def unapply(n: Node) = Some(n.boundTo)
  }
  abstract class ControlFlow(val typ: TypeRep) extends Rep {
    override def toString = simpleString
    def simpleString = (new DefPrettyPrinter)(this)
  }
  
  override protected def freshNameImpl(n: Int) = "$"+n
  
  /** placeholder Val that should never be in the graph. */
  //class SyntheticVal(name: String, typ: TypeRep, annots: List[Annot]=Nil) extends BoundVal("@"+name)(typ, annots)
  
  //case class Call(cid: CallId, result: Rep) extends ControlFlow(new SyntheticVal("C"+cid, result.typ)) {
  case class Call(cid: CallId, result: Rep) extends ControlFlow(result.typ) {
  }
  object Call {
  }
  case class Arg(cid: CallId, result: Rep)(typ: TypeRep) extends ControlFlow(result.typ) {
  }
  
  //abstract case class Branch(cid: CallId, thn: Rep, els: Rep)(typ: TypeRep) extends ControlFlow(ruh.uni.lub(thn.typ.tpe::els.typ.tpe::Nil)) {
  case class Branch(cid: CallId, thn: Rep, els: Rep) extends ControlFlow(ruh.uni.lub(thn.typ.tpe::els.typ.tpe::Nil)) {
  }
  
  def isNormalVal(d: Def): Bool = d.isInstanceOf[BoundVal] && !d.isInstanceOf[MirrorVal]
  
  def rep(dfn: Def) = dfn match {
    //case v: Val => reificationContext.getOrElse(v, new Node(v)) // mirror val?
    case v: Val if isNormalVal(v) && reificationContext.contains(v) => reificationContext(v)
    case v: Val if isNormalVal(v) && edges.containsKey(v) => new Node(v)
    case _ =>
      val v = freshBoundVal(dfn.typ)
      bind(v, dfn)
      new Node(v)
  }
  
  def dfn(r: Rep): Def = r match {
    case Node(d) => d
    //case _: ControlFlow => ControlFlowVal
    case Call(_,r) => dfn(r)
    case Arg(_,r) => dfn(r)
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
  
  override def abs(param: BoundVal, body: => Rep): Rep =
    letinImpl(param, rep(new MirrorVal(param)), super.abs(param, body))
  
  
  
  
  
  def showGraph(rep: Rep) = rep.simpleString + {
    val defsStr = iterator(rep).collect { case nde @ Node(d) if !d.isSimple => s"\n\t${nde.bound} = ${d};" }.mkString
    if (defsStr.isEmpty) "" else " where:" + defsStr
  }
  def iterator(r: Rep): Iterator[Rep] = mkIterator(r)(false,mutable.HashSet.empty)
  def mkIterator(r: Rep)(implicit rev: Bool, done: mutable.HashSet[Val]): Iterator[Rep] = r match {
    case r: Node =>
      done.setAndIfUnset(r.bound,
        if (rev) mkDefIterator(r.boundTo) ++ Iterator.single(r)
        else Iterator.single(r) ++ mkDefIterator(r.boundTo), Iterator.empty)
    case Call(cid,res) => Iterator.single(r) ++ mkIterator(res) 
    case Arg(cid,res) => Iterator.single(r) ++ mkIterator(res) 
    case Branch(cid,thn,els) => Iterator.single(r) ++ mkIterator(thn) ++ mkIterator(els) 
  }
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
  class DefPrettyPrinter extends super.DefPrettyPrinter {
    val printed = mutable.Set.empty[Rep]
    override val showValTypes = false
    override val desugarLetBindings = false
    var curCol = Console.BLACK
    override def apply(r: Rep): String = r match {
      case Call(cid, res) =>
        val col = colorOf(cid)
        s"$col⟦$cid$curCol ${res |> apply}$col⟧$curCol"
      case Arg(cid, res) =>
        val col = colorOf(cid)
        //s"$col$cid⟨⟩$curCol${res|>apply}"
        s"⟦$col$cid⟧$curCol${res|>apply}"
      case Branch(cid, cbr, els) =>
        val oldCol = curCol
        curCol = colorOf(cid)
        //s"${cid}⟨${cbr |> apply}⟩$oldCol${curCol = oldCol; apply(els)}"
        s"${cid} ? ${cbr |> apply} ¿ $oldCol${curCol = oldCol; apply(els)}"
      case r: Node =>
        printed.setAndIfUnset(r, (r match {
          case Node(d) if d.isSimple => apply(d)
          //case _ => super.apply(r)
          case _ => super.apply(r.bound)
        }) alsoDo {printed -= r}, s"[RECURSIVE ${super.apply(r.bound)}]")
    } 
    override def apply(d: Def): String = d match {
      case Bottom => "⊥"
      case MirrorVal(v) => s"<$v>"
      case _ => super.apply(d)
    }
  }
  
  
}
