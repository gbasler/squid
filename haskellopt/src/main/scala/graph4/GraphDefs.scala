package graph4

import squid.utils._

import scala.collection.mutable
import squid.utils.CollectionUtils.MutSetHelper
import squid.utils.CollectionUtils.IteratorHelper

import scala.annotation.tailrec

abstract class GraphDefs extends GraphInterpreter { self: GraphIR =>
  
  type Condition = Map[Instr, CallId]
  object Condition {
    def merge(lhs: Condition, rhs: Condition): Opt[Condition] = Some {
      val commonKeys = lhs.keySet & rhs.keySet
      lhs ++ (rhs -- commonKeys) ++ commonKeys.iterator.map { k =>
        val l = lhs(k)
        val r = rhs(k)
        assert(l.v === r.v)
        // ^ perhaps this is too restrictive; may need to change to:
        //if (l.v =/= r.v) return None
        if (l =/= r) return None
        k -> r
      }
    }
    /** Note that a condition might be incompatible with a given ambient instruction
      * (for example, instruction `Push[a0]` and condition `a1?`);
      * so in these cases this will return None. */
    def throughControl(in: Instr, c: Condition): Opt[Condition] = {
      val res = mutable.Map.empty[Instr, CallId]
      val ite = c.iterator
      while (ite.hasNext) {
        val (i, c) = ite.next
        val newInstr = try in `;` i // this can fail if something ends up being propagated too far...
          catch { case _: BadComparison => return None }
        newInstr.lastCallId match {
          case Some(c2) =>
            if (c2 =/= c) return None
          case None =>
            res.get(newInstr) match {
              case Some(c2) => if (c2 =/= c) return None
              case None => res(newInstr) = c
            }
        }
      }
      Some(res.toMap) // TODO avoid this conversion cost
    }
    
    protected def test_impl(cnd: Condition, ictx: Instr, canFail: Bool): Bool = {
      //println(s"test [$ictx] $cnd")
      var atLeastOne = false // at least one subcondition could be tested
      var result = true
      cnd.foreach { case (i,c) =>
        try ((ictx `;` i).lastCallId match {
          case Some(c2) =>
            atLeastOne = true
            result &&= c2 === c
          case None => if (canFail) scala.util.control.Breaks.break() else result = false
        })
        catch { case _: BadComparison => result = false }
        // ^ we currently use this because our Condition-s are unordered and so we may test things that should have never been tested
        // TODO perhaps it's better to just use a List or ListMap for Condition
      }
      assert(atLeastOne)
      result
    }
    
    /** Must have full context; may crash if `ictx` is only partial. */
    def test_!(cnd: Condition, ictx: Instr): Bool =
      test_impl(cnd, ictx, false)
    
    /** Returns None if some context is missing to determine the truth of the condition. */
    def test(cnd: Condition, ictx: Instr): Opt[Bool] = 
      scala.util.control.Breaks tryBreakable[Opt[Bool]] Some((test_impl(cnd, ictx, true))) catchBreak None
    // Note: the unprotected version was:
    /*
    def test(cnd: Condition, ictx: Instr): Opt[Bool] = Some {
      cnd.forall { case (i,c) =>
        (ictx `;` i).lastCallId.getOrElse(return None) === c
      }
    }
    */
    
    def show(c: Condition): Str =
      c.map{ case (Id, c) => s"$c"; case (i, c) => s"[$i]$c" }.mkString(Console.BOLD + " & ")
  }
  
  type Path = (Instr, Condition)
  object Path {
    def empty: Path = (Id, Map.empty)
    def throughControl(in: Instr, p: Path): Opt[Path] = {
      val newCond = Condition.throughControl(in, p._2).getOrElse(return None)
      Some((in `;` p._1, newCond))
    }
    def throughBranchLHS(cnd: Condition, p: Path): Opt[Path] =
      Condition.merge(cnd, p._2).map((p._1, _))
  }
  
  type CtorPath = (ModuleRef, List[Instr -> Ref], Condition)
  object CtorPath {
    def empty(mod: ModuleRef): CtorPath = (mod, Nil, Map.empty)
    def throughControl(in: Instr, p: CtorPath): Opt[CtorPath] = {
      val newCond = Condition.throughControl(in, p._3).getOrElse(return None)
      Some((p._1, p._2.map(arg => (in `;` arg._1, arg._2)), newCond))
    }
    def throughBranchLHS(cnd: Condition, p: CtorPath): Opt[CtorPath] =
      Condition.merge(cnd, p._3).map((p._1, p._2, _))
    def throughRef(ref: Ref, p: CtorPath): CtorPath = (p._1, (Id -> ref) :: p._2, p._3)
  }
  
  sealed abstract class Node {
    
    def children: Iterator[NodeRef] = this match {
      case Control(i, b) => Iterator(b)
      case Branch(c, t, e) => Iterator(t, e)
      case Lam(p, b) => Iterator(b)
      case App(l, r) => Iterator(l, r)
      case Case(s, as) => Iterator(s) ++ as.iterator.map(_._3)
      case CtorField(s, c, a, i) => Iterator(s)
      case _: ConstantNode | _: Var => Iterator.empty
    }
    
    def mkRef = new NodeRef(this)
    def mkRefNamed(name: Str) = new NodeRef(this, Some(name))
    def mkRefFrom(r: Ref) = new NodeRef(this, r.name)
    
    def canBeShownInline: Bool = this match {
      //case _: Control | _: App => true
      case _: App => true
      case _ => isSimple
    }
    def isSimple: Bool = this match {
      case IntBoxing(_) => true
      case _: ConstantNode | _: Var | _: CtorField => true
      case _: Control => true // so we don't need parens in things like `[a][b](...)` or `f @ [a](...)`
      case _ => false
    }
    
    import Console.{BOLD, RESET}
    override def toString: Str = this match {
      case Control(Id, b) => s"[]${b.subString}"
      case Control(i, b) => s"[$i]${b.subString}"
      case Branch(c, t, e) => s"${Condition.show(c)} $BOLD?$RESET $t $BOLD¿$RESET $e"
      case l @ Lam(p, b) => s"\\${l.param} -> $b"
      case IntBoxing(n) => s"$n"
      case App(Ref(App(Ref(ModuleRef(m, op)), lhs)), rhs) if knownModule(m) && !op.head.isLetter =>
        s"${lhs.subString} $op ${rhs.subString}"
      case App(Ref(ModuleRef(m, op)), lhs) if knownModule(m) && !op.head.isLetter =>
        s"(${lhs.subString} $op)"
      case App(l, r) => s"$l @ ${r.subString}"
      case Case(s, as) =>
        s"${BOLD}case$RESET ${s} ${BOLD}of {$RESET ${
          as.map{ case (ctor,ari,ref) => s"$ctor $BOLD->$RESET $ref" }.mkString(s"$BOLD; $RESET")} $BOLD}$RESET"
      case CtorField(s, c, a, i) => s"${s.subString}.$c#$i"
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
  
  case class Branch(cnd: Condition, thn: Ref, els: Ref) extends VirtualNode {
    cnd.foreach{case(i,c) => assert(i.lastCallId.isEmpty, s"condition was not simplified: ${i->c}")}
  }
  
  sealed abstract class ConcreteNode extends Node
  
  case class App(lhs: Ref, rhs: Ref) extends ConcreteNode
  
  class Var(val name: Str, val unique: Int) extends ConcreteNode {
    override def hashCode: Int = unique
  }
  
  case class Lam(var paramRef: NodeRef, body: Ref) extends ConcreteNode {
    def param: Var = paramRef.node |>! {
      case v: Var => v
    }
  }
  
  case class Case(scrut: NodeRef, arms: List[(Str, Int, NodeRef)]) extends ConcreteNode {
    // Maybe point to ctorField accesses for one-step reductions when possible?
  }
  
  case class CtorField(scrut: NodeRef, ctor: Str, arity: Int, idx: Int) extends ConcreteNode
  
  sealed abstract class ConstantNode extends ConcreteNode
  
  case class ModuleRef(modName: Str, defName: Str) extends ConstantNode {
    val isCtor: Bool = {
      val hd = defName.head
      hd === ':' || hd === '[' || hd.isUpper ||
        modName === "GHC.Tuple" && hd === '(' && defName.last === ')' && defName.init.tail.forall(_ === ',')
    }
  }
  
  sealed abstract class Lit extends ConstantNode {
    val boxed: Bool
    val value: Any
  }
  case class IntLit(boxed: Bool, value: Int) extends Lit
  case class StrLit(boxed: Bool, value: Str) extends Lit
  
  object IntBoxing {
    def unapply(nde: App): Opt[Int] = nde |>? {
      case App(Ref(ModuleRef("GHC.Types","I#")), Ref(IntLit(_,n))) => n
    }
  }
  
  class NodeRef(private var _node: Node, var name: Opt[Str] = None) {
    
    var references: List[NodeRef] = Nil
    
    /** Only control and branch nodes maintain their known paths to lambdas. */
    val pathsToLambdas: mutable.Map[Path, NodeRef] = mutable.Map.empty
    val usedPathsToLambdas: mutable.Set[Path] = mutable.Set.empty
    
    /** Handles unsaturated ctors by keeping one distinct instr for each ctor app argument */
    val pathsToCtorApps: mutable.Set[CtorPath] = mutable.Set.empty
    
    node match {
      case _: Lam => pathsToLambdas += Path.empty -> this
      case mod: ModuleRef if mod.isCtor => pathsToCtorApps += CtorPath.empty(mod)
      case _ =>
    }
    children.foreach { c => c.addref(this); c.propagatePaths(this)(0) }
    
    def children: Iterator[NodeRef] = _node.children
    
    // Note: I'm not completely sure it's okay to stop early based on the `done` set (and so potentially not remove every ref?);
    //       But I got some stack overflows here, so for now that's what we'll do.
    def remrefs(oldChildren: Iterator[Ref])(implicit mod: Module, done: mutable.HashSet[Ref] = mutable.HashSet.empty): Unit = done.setAndIfUnset(this, {
      oldChildren.foreach(_.remref(this))
    })
    def addref(ref: NodeRef): Unit = {
      references ::= ref
      //propagatePaths(ref)(mutable.HashSet.empty) // now done in each call site, as it sometimes need to be delayed...
    }
    def remref(ref: NodeRef)(implicit mod: Module, done: mutable.HashSet[Ref]): Unit = {
      def go(refs: List[NodeRef]): List[NodeRef] = refs match {
        case `ref` :: rs => rs
        case r :: rs => r :: go(rs)
        case Nil => Nil
      }
      references = go(references)
      if (references.isEmpty && !mod.roots(this)) remrefs(children)
    }
    //def propagatePaths(ref: NodeRef)(implicit done: mutable.HashSet[Ref]): Unit = done.setAndIfUnset(this, {
    // ^ If we stop propagating recursively using done.setAndIfUnset, it makes us miss some reductions
    //     For example in HigherOrder.hs ([↓]f_ε_115 @ λ_40) and in Church.hs
    //def propagatePaths(ref: NodeRef)(implicit done: mutable.HashSet[Ref]): Unit = ({
    def propagatePaths(ref: NodeRef)(implicit depth: Int): Unit = (depth + 1) |> { implicit depth =>
      //println(s"> $this tells $ref")
      
      if (depth > MaxPropagationDepth) {
        System.err.println(s"WARNING: Maximum propagation deapth reached ($MaxPropagationDepth)")
        return
      }
      
      //println(s"> $this tells $ref ${
      //  pathsToLambdas.map("\n\t"+_).mkString
      //}"); Thread.sleep(50)
      
      // If the new reference is a control or branch, we need to inform them of our known paths to lambdas:
      ref.node match {
        case Control(i, b) =>
          assert(b eq this)
          val oldSize = ref.pathsToLambdas.size
          if (oldSize < MaxPropagationWidth)
          pathsToLambdas.foreach { case (p, l) =>
            //println(s"> $this tells $ref about $p")
            Path.throughControl(i, p).foreach { newPath =>
              assert(ref.pathsToLambdas.get(newPath).forall(_ eq l))
              ref.pathsToLambdas += newPath -> l
            }
          }
          val oldSize2 = ref.pathsToCtorApps.size
          if (oldSize2 < MaxPropagationWidth)
          pathsToCtorApps.foreach { p =>
            //println(s"> $this tells $ref about $p")
            CtorPath.throughControl(i, p).foreach { newPath =>
              ref.pathsToCtorApps += newPath
            }
          }
          if (ref.pathsToLambdas.size > oldSize || ref.pathsToCtorApps.size > oldSize2)
            ref.references.foreach(ref.propagatePaths(_))
        case Branch(c, t, e) =>
          assert((t eq this) || (e eq this))
          val oldSize = ref.pathsToLambdas.size
          if (oldSize < MaxPropagationWidth)
          pathsToLambdas.foreach { case (p, l) =>
            val newPath = if (t eq this) {
              Path.throughBranchLHS(c, p)
            } else Some(p)
            newPath.foreach { newPath =>
              assert(ref.pathsToLambdas.get(newPath).forall(_ eq l))
              ref.pathsToLambdas += newPath -> l
            }
          }
          val oldSize2 = ref.pathsToCtorApps.size
          if (oldSize2 < MaxPropagationWidth)
          pathsToCtorApps.foreach { p =>
            val newPath = if (t eq this) {
              CtorPath.throughBranchLHS(c, p)
            } else Some(p)
            newPath.foreach { newPath =>
              ref.pathsToCtorApps += newPath
            }
          }
          if (ref.pathsToLambdas.size > oldSize || ref.pathsToCtorApps.size > oldSize2)
            ref.references.foreach(ref.propagatePaths(_))
        case App(lhs, rhs) =>
          if (lhs eq this) {
            val oldSize2 = ref.pathsToCtorApps.size
            if (oldSize2 < MaxPropagationWidth)
            pathsToCtorApps.foreach { p =>
              val newPath = CtorPath.throughRef(rhs, p)
              ref.pathsToCtorApps += newPath
            }
            if (ref.pathsToCtorApps.size > oldSize2)
              ref.references.foreach(ref.propagatePaths(_))
          } else {
            assert(rhs eq this)
          }
        case _ =>
      }
      
    }
    def node: Node = _node
    def node_=(that: Node)(implicit mod: Module): Unit = {
      val oldChildren = children
      _node = that
      children.foreach(_.addref(this)) // Note: we need to do that before `remrefs` or some counts will drop to 0 prematurely
      remrefs(oldChildren) // We should obviously not remove the refs of the _current_ children (_node assignment is done above)
      children.foreach(_.propagatePaths(this)(0))
    }
    def rewireTo(that: Ref)(implicit mod: Module): Unit = node = Id(that)
    
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
    
    def toBeScheduledInline = node match {
      //case IntBoxing(_) => true // maybe use this?
      case _: ConstantNode | _: Var => true
      case _ => false
    }
    
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
    
    def showGraph: Str = s"$this" + {
      val defsStr = showGraphOf(Iterator(this), Map.empty, true, showRefs)
      if (defsStr.isEmpty) "" else " where:" + defsStr
    }
    
    /** This is just a debugging help,
      * and to stabilize the hashCode — since Ref-s are now used as part of keys in pathsToCtorApps. */
    protected val id = curNodeRefId alsoDo (curNodeRefId += 1)
    
    override def hashCode = id
    
    override def toString =
      if (toBeShownInline)
        (if (showInlineNames) Debug.GREY +showName+":" + Console.RESET else "") + node.toString
      else showName
    
    def subString = if (toBeShownInline && !node.isSimple) s"($toString)" else toString
    
    def showName = s"${name.getOrElse("")}_${id.toHexString}"
    def showDef = s"$showName = $node"
    def show = if (toBeShownInline && !showInlineNames) node else showDef
  }
  object NodeRef {
    def unapply(arg: NodeRef): Some[Node] = Some(arg.node)
  }
  
  import CallId._, Console.{BOLD,RESET}
  
  class CallId(val v: Var, val uid: Int) {
    def color = if (uid === -1) Debug.GREY else
      //colors((uid + colors.size - 1) % colors.size)
      colors(uid % colors.size)
    override def toString = s"$color$v:${uid.toHexString}$RESET"
    override def hashCode = uid.hashCode
    override def equals(that: Any) = that match { case that: CallId => that.uid === uid; case _ => false }
  }
  object CallId {
    private val colors = List(
      //Console.BLACK,
      Console.RED,Console.GREEN,Console.YELLOW,Console.BLUE,Console.MAGENTA,Console.CYAN,
      //Console.WHITE,Console.BLACK_B,Console.RED_B,Console.GREEN_B,Console.YELLOW_B,Console.BLUE_B,Console.MAGENTA_B,Console.CYAN_B
    ) ++ (for { i <- 90 to 96 } yield s"\u001b[$i;1m") // Some alternative color versions (https://misc.flogisoft.com/bash/tip_colors_and_formatting#colors)
  }
  
  class BadComparison(msg: Str) extends Exception(msg)
  object BadComparison extends BadComparison("switch to commented badComparison for a useful message and stack trace")
  BadComparison
  
  def badComparison(msg: => Str) = throw BadComparison // faster, but no info on failure
  //def badComparison(msg: Str) = throw new BadComparison(msg) // better diagnostic, but slower
  
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
        if (strictCallIdChecking && cid =/= DummyCallId && cid.v =/= p.originalVar)
          badComparison(s"${cid.v} === ${p.originalVar} in Push($cid, $payload, $rest)")
        payload `;` rest2
      case d @ Drop(rest2) =>
        val baseAssertion = strictCallIdChecking && cid =/= DummyCallId
        // Note that the following assertion is quite drastic, and prevents us from blindly pushing boxes into branches.
        // It would be sufficient to only check cid.v, but I prefer to make assertions as strict as possible.
        if (baseAssertion && cid =/= d.originalCid)
          badComparison(s"$cid === ${d.originalCid} in Push($cid, $payload, $rest)")
        // A less drastic version:
        //if (baseAssertion && cid.v =/= d.originalCid.v)
        //  badComparison(s"${cid.v} === ${d.originalCid.v} in Push($cid, $payload, $rest)")
        rest2
      case rest: TransitiveControl => apply(cid, payload, rest)
    }
  }
  case class Pop(rest: Instr)(val originalVar: Var) extends Instr {
  }
  object Pop { def it(originalVar: Var) = Pop(Id)(originalVar) }
  
  case class Drop(rest: Instr)(val originalCid: CallId) extends Instr {
  }
  case object Drop { def it(originalCid: CallId) = Drop(Id)(originalCid) }
  
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
  
  
  def showGraphOf(refs: Iterator[Ref], modDefNames: Map[Ref, Str], printRefCounts: Bool, showRefs: Bool): Str = {
    val iterator = refs.flatMap(_.iterator)
    iterator.toList.distinct.flatMap { r =>
      (modDefNames.get(r) match {
        case Some(defName) =>
          //val defStr = s"\n$BOLD → \t$defName$RESET = $r"
          val defStr = s"\n$BOLD  $defName$RESET = $r"
          defStr :: Nil
        case None => Nil
      }) ::: (
        if (showFull || !r.toBeShownInline) {
          val rhs = r.node
          val headStr = s"\n    $r = $rhs;"
          val str =
            if (showRefs) s"$headStr\t\t\t\t{${r.references.mkString(", ")}}"
            else if (printRefCounts && r.references.size > 1) s"$headStr\t\t\t\t\t(x${r.references.size})"
            else headStr
          assert(!showPaths || !showCtorPaths)
          if (showPaths) (str
            + r.pathsToLambdas.map(pl => s"\n\t  -- ${Condition.show(pl._1._2)} --> [${pl._1._1}]${pl._2}").mkString
            + r.usedPathsToLambdas.map(p => s"\n\t  -X- ${Condition.show(p._2)} --> [${p._1}]").mkString
          ) else if (showCtorPaths) (str
            + r.pathsToCtorApps.map(pc => s"\n\t  -- ${Condition.show(pc._3)} --> ${pc._1}${
                pc._2.reverseIterator.map(arg => s" [${arg._1}]${arg._2}").mkString}").mkString
          ) else str
        } :: Nil
        else Nil
      )
    }.mkString
  }
  
}
