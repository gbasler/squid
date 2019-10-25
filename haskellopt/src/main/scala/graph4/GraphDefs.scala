package graph4

import squid.utils._

import scala.collection.mutable
import squid.utils.CollectionUtils.MutSetHelper
import squid.utils.CollectionUtils.IteratorHelper

import scala.annotation.tailrec

abstract class GraphDefs { self: GraphIR =>
  
  type Condition = Map[Instr, CallId]
  object Condition {
    def merge(lhs: Condition, rhs: Condition): Opt[Condition] = Some {
      val commonKeys = lhs.keySet & rhs.keySet
      lhs ++ (rhs -- commonKeys) ++ commonKeys.map { k =>
        val l = lhs(k)
        val r = rhs(k)
        assert(l.v === r.v)
        if (l =/= r) return None
        k -> r
      }
    }
  }
  
  type Path = (Instr, Condition)
  object Path {
    def empty: Path = (Id, Map.empty)
    def throughControl(in: Instr, p: Path): Path = {
      val newCond = p._2.map{ case (i, c) => (in `;` i, c) }
      if (newCond.size =/= p._2.size) die // TODO merge making sure compatible — fallible!
      (in `;` p._1, newCond)
    }
    def throughBranchLHS(cnd: Condition, p: Path): Option[Path] =
      Condition.merge(cnd, p._2).map((p._1, _))
  }
  
  sealed abstract class Node {
    
    def children: Iterator[NodeRef] = this match {
      case Control(i, b) => Iterator(b)
      case Branch(c, t, e) => Iterator(t, e)
      case Lam(p, b) => Iterator(b)
      case App(l, r) => Iterator(l, r)
      case Case(s, as) => Iterator(s) ++ as.iterator.map(_._2)
      case CtorField(s, c, i) => Iterator(s)
      case _: ConstantNode | _: Var => Iterator.empty
    }
    
    def mkRef = new NodeRef(this)
    def mkRefNamed(name: Str) = new NodeRef(this, Some(name))
    
    def canBeShownInline: Bool = this match {
      //case _: Control | _: App => true
      case _: App => true
      case _ => isSimple
    }
    def isSimple: Bool = this match {
      case App(Ref(ModuleRef("GHC.Types","I#")), Ref(IntLit(_,n))) => true
      case _: ConstantNode | _: Var | _: CtorField => true
      case _: Control => true // so we don't need parens in things like `[a][b](...)` or `f @ [a](...)`
      case _ => false
    }
    
    override def toString: Str = this match {
      case Control(Id, b) => s"[]${b.subString}"
      case Control(i, b) => s"[$i]${b.subString}"
      case Branch(c, t, e) => s"${c.map{ case (Id, c) => s"$c"; case (i, c) => s"[$i]$c" }
        .mkString(Console.BOLD + " & ")} ${Console.BOLD}?${Console.RESET} $t ${Console.BOLD}¿${Console.RESET} $e"
      case l @ Lam(p, b) => s"\\${l.param} -> $b"
      case App(Ref(ModuleRef("GHC.Types","I#")), Ref(IntLit(_,n))) => s"$n"
      case App(Ref(App(Ref(ModuleRef(m, op)), lhs)), rhs) if knownModule(m) && !op.head.isLetter =>
        s"${lhs.subString} $op ${rhs.subString}"
      case App(Ref(ModuleRef(m, op)), lhs) if knownModule(m) && !op.head.isLetter =>
        s"(${lhs.subString} $op)"
      case App(l, r) => s"$l @ ${r.subString}"
      case Case(s, as) => ??? // TODO
      case CtorField(s, c, i) => ??? // TODO
      case ModuleRef(mod,ref) => if (knownModule(mod)) ref else s"$mod.$ref"
      case IntLit(b,n) => (if (b) "" else "#") + n
      case StrLit(b,s) => (if (b) "" else "#") + '"' + s + '"'
      case v: Var =>
        //v.name+"$"+v.unique
        //v.name+"_"+v.unique
        v.name+"'"+v.unique
    }
  }
  
  sealed abstract class VirtualNode extends Node
  
  case class Control(i: Instr, body: NodeRef) extends VirtualNode
  object Control {
    @tailrec def mkRef(i: Instr, body: NodeRef): NodeRef = if (i === Id) body else body.node match {
      case Control(i2, body2) => Control.mkRef(i `;` i2, body2)
      case _ => Control(i, body).mkRef
    }
  }
  
  case class Branch(cnd: Condition, thn: Ref, els: Ref) extends VirtualNode
  
  sealed abstract class ConcreteNode extends Node
  
  case class App(lhs: Ref, rhs: Ref) extends ConcreteNode
  
  class Var(val name: Str, val unique: Int) extends ConcreteNode
  
  case class Lam(var paramRef: NodeRef, body: Ref) extends ConcreteNode {
    def param: Var = paramRef.node |>! {
      case v: Var => v
    }
  }
  
  case class Case(scrut: NodeRef, arms: List[(Str, NodeRef)]) extends ConcreteNode {
    // Maybe point to ctorField accesses for one-step reductions when possible?
  }
  
  case class CtorField(scrut: NodeRef, ctor: Str, idx: Int) extends ConcreteNode
  
  sealed abstract class ConstantNode extends ConcreteNode
  
  case class ModuleRef(modName: Str, defName: Str) extends ConstantNode
  
  sealed abstract class Lit extends ConstantNode {
    val boxed: Bool
    val value: Any
  }
  case class IntLit(boxed: Bool, value: Int) extends Lit
  case class StrLit(boxed: Bool, value: Str) extends Lit
  
  
  class NodeRef(private var _node: Node, var name: Opt[Str] = None) {
    
    var references: List[NodeRef] = Nil
    
    /** Only control and branch nodes maintain their known paths to lambdas. */
    val pathsToLambdas: mutable.Map[Path, NodeRef] = mutable.Map.empty
    val usedPathsToLambdas: mutable.Set[Path] = mutable.Set.empty
    
    node match {
      case _: Lam => pathsToLambdas += Path.empty -> this
      case _ =>
    }
    addrefs()
    
    def children: Iterator[NodeRef] = _node.children
    
    def addrefs(): Unit = {
      children.foreach(_.addref(this))
    }
    def remrefs(): Unit = {
      children.foreach(_.remref(this))
    }
    def addref(ref: NodeRef): Unit = {
      references ::= ref
      propagatePaths(ref)
    }
    def remref(ref: NodeRef): Unit = {
      def go(refs: List[NodeRef]): List[NodeRef] = refs match {
        case `ref` :: rs => rs
        case r :: rs => r :: go(rs)
        case Nil => Nil
      }
      references = go(references)
      if (references.isEmpty) remrefs()
    }
    def propagatePaths(ref: NodeRef): Unit = {
      
      // If the new reference is a control or branch, we need to inform them of our known paths to lambdas:
      ref.node match {
        case Control(i, b) =>
          assert(b eq this)
          pathsToLambdas.foreach { case (p, l) =>
            val newPath = Path.throughControl(i, p)
            assert(ref.pathsToLambdas.get(newPath).forall(_ eq l))
            ref.pathsToLambdas += newPath -> l
          }
          ref.references.foreach(ref.propagatePaths(_))
        case Branch(c, t, e) =>
          assert((t eq this) || (e eq this))
          pathsToLambdas.foreach { case (p, l) =>
            val newPath = if (t eq this) {
              Path.throughBranchLHS(c, p)
            } else Some(p)
            newPath.foreach { newPath =>
              assert(ref.pathsToLambdas.get(newPath).forall(_ eq l))
              ref.pathsToLambdas += newPath -> l
            }
          }
          ref.references.foreach(ref.propagatePaths(_))
        case _ =>
      }
      
    }
    def node: Node = _node
    def node_=(that: Node): Unit = {
      remrefs()
      _node = that
      addrefs()
    }
    def rewireTo(that: Ref): Unit = node = Id(that)
    
    /*
    def nonSimpleReferences: List[NodeRef] = {
      var refs = references
      var res: List[NodeRef] = Nil
      while(refs.nonEmpty) {
        val ref = refs.head
        refs = refs.tail
        if (ref.node.canBeShownInline) {
          refs ++= ref.references
        } else res ::= ref
      }
      res
    }
    */
    
    def iterator: Iterator[Ref] = mkIterator(false, mutable.HashSet.empty)
    def mkIterator(implicit rev: Bool, done: mutable.HashSet[Ref]): Iterator[Ref] = done.setAndIfUnset(this, {
      Iterator.single(this) ++ node.children.flatMap(_.mkIterator)
    }, Iterator.empty)
    
    def toBeShownInline: Bool = (
      !showFull
      && node.canBeShownInline
      && (
        node.isSimple && !node.isInstanceOf[Control]
        // ^ always show simple nodes like constants and variables inline, except for controls
        || references.size <= 1
      )
    )
    
    def showGraph: Str = showGraph()
    def showGraph(printRefCounts: Bool = true, showRefs: Bool = showRefs): Str = s"$this" + {
      val defsStr = iterator.toList.distinct
        .filter(showFull || !_.toBeShownInline)
        .map { r =>
          val rhs = r.node
          val str =
            if (showRefs) s"\n\t${r} = $rhs;\t\t\t\t{${r.references.mkString(",")}}"
            else if (printRefCounts && r.references.size > 1) s"\n\t${r} = $rhs;\t\t\t\t\t(${r.references.size}x)"
            else s"\n\t${r} = $rhs;"
          if (showPaths) (str
            + r.pathsToLambdas.map(pl => s"\n\t  -- ${pl._1._2} --> [${pl._1._1}]${pl._2}").mkString
            + r.usedPathsToLambdas.map(p => s"\n\t  -X- ${p._2} --> [${p._1}]").mkString
          ) else str
        }
        .mkString
      if (defsStr.isEmpty) "" else " where:" + defsStr
    }
    
    /** This is just a debugging help. */
    protected val id = curNodeRefId alsoDo (curNodeRefId += 1)
    def showName = s"${name.getOrElse("")}_${id.toHexString}"
    
    override def toString =
      if (toBeShownInline)
        (if (showInlineNames) Debug.GREY +showName+":" + Console.RESET else "") + node.toString
      else showName
    def subString = if (toBeShownInline && !node.isSimple) s"($toString)" else toString 
  }
  object NodeRef {
    def unapply(arg: NodeRef): Some[Node] = Some(arg.node)
  }
  
  import CallId._, Console.{BOLD,RESET}
  
  class CallId(val v: Var, val uid: Int) {
    def color =
      //colors((uid + colors.size - 1) % colors.size)
      colors(uid % colors.size)
    override def toString = s"$color$v:${uid.toHexString}$RESET"
  }
  object CallId {
    private val colors = List(
      //Console.BLACK,
      Console.RED,Console.GREEN,Console.YELLOW,Console.BLUE,Console.MAGENTA,Console.CYAN,
      //Console.WHITE,Console.BLACK_B,Console.RED_B,Console.GREEN_B,Console.YELLOW_B,Console.BLUE_B,Console.MAGENTA_B,Console.CYAN_B
    ) ++ (for { i <- 90 to 96 } yield s"\u001b[$i;1m") // Some alternative color versions (https://misc.flogisoft.com/bash/tip_colors_and_formatting#colors)
  }
  
  sealed abstract class Instr {
    
    def `;` (that: Instr): Instr =
    //println(s"($this) ; ($that)") thenReturn (
    (this, that) match {
      case (Id, _) => that
      case (Push(cid, pl, rest), t: TransitiveControl) => Push(cid, pl, rest `;` t)
      case (Push(cid, pl, rest0), rest1) => Push(cid, pl, rest0 `;` rest1)
      case (p @ Pop(rest0), rest1) => Pop(rest0 `;` rest1)(p.originalVar)
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
    
    protected def thenString = this match {
      case Id => ""
      case _ => s";$toString"
    }
    override def toString: Str = this match {
      case Id => s"Id"
      case Pop(i) => s"$BOLD↓$RESET${i.thenString}"
      case d @ Drop(i) => s"$BOLD${d.originalCid.color}∅$RESET${i.thenString}" // also see: Ø
      case Push(c,Id,i) => s"$BOLD$c↑$RESET${i.thenString}"
      case Push(c,p,i) => s"$BOLD$c↑$RESET$BOLD[$RESET${p}$BOLD]$RESET${i.thenString}"
    }
  }
  
  sealed abstract case class Push(cid: CallId, payload: Instr, rest: TransitiveControl) extends TransitiveControl {
  }
  object Push {
    def apply(cid: CallId, payload: Instr, rest: TransitiveControl): TransitiveControl = new Push(cid, payload, rest){}
    def apply(cid: CallId, payload: Instr, rest: Instr): Instr = rest match {
      case p @ Pop(rest2) =>
        assert(!strictCallIdChecking || cid === DummyCallId || cid.v === p.originalVar, s"${cid.v} === ${p.originalVar} in Push($cid, $payload, $rest)")
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
  case class Pop(rest: Instr)(val originalVar: Var) extends Instr {
  }
  object Pop { def it(originalVar: Var) = Pop(Id)(originalVar) }
  
  case class Drop(rest: Instr)(val originalCid: CallId) extends Instr {
  }
  object Drop { def it(originalCid: CallId) = Drop(Id)(originalCid) }
  
  sealed abstract class TransitiveControl extends Instr {
    def `;` (that: TransitiveControl): TransitiveControl = (this,that) match {
      case (Id,_) => that
      case (_,Id) => this
      case (Push(cid0, pl0, rest0), t) => Push(cid0, pl0, rest0 `;` t)
    }
  }
  case object Id extends TransitiveControl {
    /** It's sometimes useful to rewire a node to another node, without having to duplicate its Def! */
    def apply(r: NodeRef) = Control(Id,r)
  }
  
}
