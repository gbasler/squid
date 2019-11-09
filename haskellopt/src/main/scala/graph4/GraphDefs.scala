package graph4

import squid.utils._

import scala.collection.mutable
import squid.utils.CollectionUtils.MutSetHelper
import squid.utils.CollectionUtils.IteratorHelper

import scala.annotation.tailrec

abstract class GraphDefs extends GraphInterpreter { self: GraphIR =>
  
  type Condition = Map[Instr, CallMaybe]
  object Condition {
    def merge(lhs: Condition, rhs: Condition): Opt[Condition] = {
      val res = mergeImpl(lhs, rhs)
      // A sanity check; can be useful to enable:
      //val res2 = mergeImpl(rhs, lhs)
      //assert(res === res2, (lhs,rhs,res,res2))
      res
    }
    private def mergeImpl(lhs: Condition, rhs: Condition): Opt[Condition] = Some {
      val commonKeys = lhs.keySet & rhs.keySet
      lhs ++ (rhs -- commonKeys) ++ commonKeys.iterator.map { k =>
        val l = lhs(k)
        val r = rhs(k)
        /*
        assert(l.v === r.v)
        // ^ perhaps this is too restrictive; may need to change to:
        //if (l.v =/= r.v) return None
        if (l =/= r) return None
        k -> r
        */
        k -> (l merge r getOrElse (return None))
      }
    }
    /** Note that a condition might be incompatible with a given ambient instruction
      * (for example, instruction `Push[a0]` and condition `a1?`);
      * so in these cases this will return None. */
    def throughControl(in: Instr, c: Condition): Opt[Condition] = {
      val res = mutable.Map.empty[Instr, CallMaybe]
      val ite = c.iterator
      while (ite.hasNext) {
        val (i, c) = ite.next
        val newInstr = try in `;` i // this can fail if something ends up being propagated too far...
          catch { case _: BadComparison => return None }
        newInstr.lastCallId match {
          case Some(c2) =>
            //if (c2 =/= c) return None
            if (c2 isIncompatibleWith c) return None
            // ^ Unless the uncovered CallId is incompatilbe, we can discharge the condition c
          case None =>
            res.get(newInstr) match {
              //case Some(c2) => if (c2 =/= c) return None
              case Some(c2) => res(newInstr) = c2 merge c getOrElse {return None}
              case None => res(newInstr) = c
            }
        }
      }
      Some(res.toMap) // TODO avoid this conversion cost
    }
    
    // TODO make this function complete? We'd need to add disjunctions to branch conditions...
    def tryNegate(cnd: Condition): Opt[Condition] = cnd.map {
      case (i,CallNot(cids)) => (i, if (cids.size > 1) return None else cids.head)
      case (i,cid:CallId) => (i, CallNot(cid::Nil))
    } optionIf (cnd.size <= 1)
    
    protected def test_impl(cnd: Condition, ictx: Instr, canFail: Bool): Bool = {
      //println(s"test [$ictx] $cnd")
      var atLeastOne = false // at least one subcondition could be tested
      var result = true
      cnd.foreach { case (i,c) =>
        try ((ictx `;` i).lastCallId match {
          case Some(c2) if (c match { // check that we can actually perform the comparison!
              case cid: CallId => cid.v === c2.v || c2 === DummyCallId
              case _ => true
          }) =>
            atLeastOne = true
            result &&= (c match {
              case CallNot(cids) => !(cids contains c2)
              case c: CallId => c2 === c
            })
          case _ => if (canFail) scala.util.control.Breaks.break() else result = false
        })
        catch { case _: BadComparison => result = false }
        // ^ we currently use this because our Condition-s are unordered and so we may test things that should have never been tested
        // TODO perhaps it's better to just use a List or ListMap for Condition
      }
      assert(atLeastOne, (ictx, show(cnd)))
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
  
  sealed abstract class AnyPath
  
  case class Path(in: Instr, cnd: Condition, throughRecIntros: Int, throughRecElims: Int) extends AnyPath {
    def concat(that: Path): Opt[Path] = {
      //println(s">>> $this + $that")
      val tre = throughRecElims + that.throughRecElims
      val tri = throughRecIntros + that.throughRecIntros
      if (tre > UnrollingFactor || tri > UnrollingFactor) return None
      val p = Path(in `;` that.in,
        Condition.merge(cnd, Condition.throughControl(in, that.cnd).getOrElse(return None)).getOrElse(return None),
        tri, tre) // also (res => s">>> == $res")
      Some(p)
    }
  }
  object Path {
    def empty: Path = Path(Id, Map.empty, 0, 0)
    def throughControl(cnt: Control, p: Path): Opt[Path] = {
      val tre = p.throughRecElims + cnt.recElims
      val tri = p.throughRecIntros + cnt.recIntros
      if (tre > UnrollingFactor || tri > UnrollingFactor) return None
      val newCond = Condition.throughControl(cnt.i, p.cnd).getOrElse(return None)
      Some(Path(cnt.i `;` p.in, newCond, tri, tre))
    }
    def throughBranchLHS(cnd: Condition, p: Path): Opt[Path] =
      Condition.merge(cnd, p.cnd).map(Path(p.in, _, p.throughRecIntros, p.throughRecElims))
  }
  
  case class CtorPath(modRef: ModuleRef, refsRev: List[Instr -> Ref], cnd: Condition, throughRecIntros: Int, throughRecElims: Int) extends AnyPath
  object CtorPath {
    def empty(mod: ModuleRef): CtorPath = CtorPath(mod, Nil, Map.empty, 0, 0)
    def throughControl(cnt: Control, p: CtorPath): Opt[CtorPath] = {
      val tre = p.throughRecElims + cnt.recElims
      val tri = p.throughRecIntros + cnt.recIntros
      if (tre > UnrollingFactor || tri > UnrollingFactor) return None
      val newCond = Condition.throughControl(cnt.i, p.cnd).getOrElse(return None)
      Some(CtorPath(p.modRef, p.refsRev.map(arg => (cnt.i `;` arg._1, arg._2)), newCond, tri, tre))
    }
    def throughBranchLHS(cnd: Condition, p: CtorPath): Opt[CtorPath] =
      Condition.merge(cnd, p.cnd).map(CtorPath(p.modRef, p.refsRev, _, p.throughRecIntros, p.throughRecElims))
    def throughRef(ref: Ref, p: CtorPath): CtorPath =
      CtorPath(p.modRef, (Id -> ref) :: p.refsRev, p.cnd, p.throughRecIntros, p.throughRecElims)
  }
  
  val Bottom = ModuleRef("Prelude", "undefined")
  
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
      case c @ Control(i, b) =>
        var iStr = if (i === Id) "" else i.toString
        if (c.recIntros > 0) iStr += "+" + c.recIntros
        if (c.recElims > 0) iStr += "-" + c.recElims
        s"[$iStr]${b.subString}" // Note that this is not ambiguous: [i]x#f is [i](x#f) and not ([i]x)#f
        //s"[$iStr]${b.node match {
        //  case _: CtorField => "("+b.subString+")"
        //  case _ => b.subString
        //}}"
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
  
  case class Control(i: Instr, body: NodeRef)(val recIntros: Int, val recElims: Int) extends VirtualNode
  object Control {
    @tailrec def mkRef(i: Instr, body: NodeRef, recIntros: Int, recElims: Int): NodeRef = if (i === Id) body else body.node match {
      case c @ Control(i2, body2) => Control.mkRef(i `;` i2, body2, recIntros + c.recIntros, recElims + c.recElims)
      case _ => Control(i, body)(recIntros, recElims).mkRef
    }
  }
  
  case class Branch(cnd: Condition, thn: Ref, els: Ref) extends VirtualNode {
    assert(cnd.nonEmpty)
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
  
  type Arm = (Str, Int, NodeRef)
  case class Case(scrut: NodeRef, arms: List[Arm]) extends ConcreteNode {
    // TODO only allow single-arm if the arm has vars
    assert(!ignoreSingleArmCases && arms.nonEmpty || arms.size > 1)
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
    val pathsToLambdasAndCases: mutable.Map[Path, NodeRef] = mutable.Map.empty
    val usedPathsToLambdas: mutable.Set[Path] = mutable.Set.empty
    
    def realPathsToCases: Iterator[Path -> Ref] = pathsToLambdasAndCases.iterator
    /* // I found a better way of dealing with outdated Case paths: removing them when a node changes
    def realPathsToCases: Iterator[Path -> Ref] = realPathsToCasesImpl(0,0,0)
    def realPathsToCasesImpl(tI: Int, tE: Int, depth: Int): Iterator[Path -> Ref] =
      if (depth > 8) Iterator.empty
      else pathsToLambdasAndCases.iterator.flatMap {
        case (p, r @ Ref(_: Case)) => Some((p, r))
        case (p, r @ Ref(c: Control)) =>
          //println(s"...${depth}.", this, p, r)
          val ri = tI + c.recIntros
          val re = tE + c.recElims
          if (
            (r eq this) || // hacky check to avoid useless computation
            ri > UnrollingFactor || re > UnrollingFactor) Iterator.empty
          else r.realPathsToCasesImpl(ri,re,depth+1).flatMap { case (p2, r2) => p concat p2 map (_ -> r2) }
        case (p, r @ Ref(_: Branch)) => r.realPathsToCasesImpl(tI,tE,depth+1).flatMap { case (p2, r2) => p concat p2 map (_ -> r2) }
        case (p, r @ Ref(_: Lam)) => None
        //case _ => die
        case _ => Iterator.empty
      }
    */
    
    /** Handles unsaturated ctors by keeping one distinct instr for each ctor app argument */
    val pathsToCtorApps: mutable.Set[CtorPath] = mutable.Set.empty  // TODO should it really be a Set?
    
    def createPaths(): Unit = {
      node match {
        case _: Lam | _: Case => pathsToLambdasAndCases += Path.empty -> this
        case mod: ModuleRef if mod.isCtor => pathsToCtorApps += CtorPath.empty(mod)
        case _ =>
      }
      assert(!node.isInstanceOf[Lam] && !node.isInstanceOf[Case] || pathsToLambdasAndCases.size === 1, (showDef, pathsToLambdasAndCases))
    }
    createPaths()
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
        System.err.println(s"WARNING: Maximum propagation depth reached ($MaxPropagationDepth)")
        return
      }
      def warnWidth = System.err.println(s"WARNING: Maximum propagation width reached ($MaxPropagationDepth)")
      
      //println(s"> $this tells $ref ${
      //  pathsToLambdas.map("\n\t"+_).mkString
      //}"); Thread.sleep(50)
      
      // If the new reference is a control or branch, we need to inform them of our known paths to lambdas:
      ref.node match {
        case cnt @ Control(i, b) =>
          assert(b eq this)
          var oldSize = ref.pathsToLambdasAndCases.size
          if (oldSize >= MaxPropagationWidth) warnWidth else
          pathsToLambdasAndCases.foreach { case (p, l) =>
            //println(s"> $this tells $ref about $p")
            Path.throughControl(cnt, p).foreach { newPath =>
              //assert(ref.pathsToLambdasAndCases.get(newPath).forall(_ eq l))
              if (!ref.pathsToLambdasAndCases.get(newPath).forall(_ eq l)) oldSize = 0
              // ^ It's possible that paths to Case nodes change, and override previous paths...
              ref.pathsToLambdasAndCases += newPath -> l
            }
          }
          val oldSize2 = ref.pathsToCtorApps.size
          if (oldSize2 >= MaxPropagationWidth) warnWidth else
          pathsToCtorApps.foreach { p =>
            //println(s"> $this tells $ref about $p")
            CtorPath.throughControl(cnt, p).foreach { newPath =>
              ref.pathsToCtorApps += newPath
            }
          }
          if (ref.pathsToLambdasAndCases.size > oldSize || ref.pathsToCtorApps.size > oldSize2)
            ref.references.foreach(ref.propagatePaths(_))
        case Branch(c, t, e) =>
          assert((t eq this) || (e eq this))
          var oldSize = ref.pathsToLambdasAndCases.size
          if (oldSize >= MaxPropagationWidth) warnWidth else
          pathsToLambdasAndCases.foreach { case (p, l) =>
            val newPath = if (t eq this) {
              Path.throughBranchLHS(c, p)
            } else // if we come from the else arm, we need to make sure that the propagated path is incompatible with c 
            if (Condition.merge(c, p.cnd).isEmpty) {
              Some(p) // If the path's condition is already incompatible with the branch, we don't need to add anything to it
            } else {
              Condition.tryNegate(c).flatMap(nc =>
                Condition.merge(p.cnd, nc).map(newCond => p.copy(cnd = newCond)))
            }
            newPath.foreach { newPath =>
              //assert(ref.pathsToLambdasAndCases.get(newPath).forall(_ eq l))
              if (!ref.pathsToLambdasAndCases.get(newPath).forall(_ eq l)) oldSize = 0
              ref.pathsToLambdasAndCases += newPath -> l
            }
          }
          val oldSize2 = ref.pathsToCtorApps.size
          if (oldSize2 >= MaxPropagationWidth) warnWidth else
          pathsToCtorApps.foreach { p =>
            val newPath = if (t eq this) {
              CtorPath.throughBranchLHS(c, p)
            } else // if we come from the else arm, we need to make sure that the propagated path is incompatible with c 
            if (Condition.merge(c, p.cnd).isEmpty) {
              Some(p) // If the path's condition is already incompatible with the branch, we don't need to add anything to it
            } else {
              Condition.tryNegate(c).flatMap(nc =>
                Condition.merge(p.cnd, nc).map(newCond => p.copy(cnd = newCond)))
            }
            newPath.foreach { newPath =>
              ref.pathsToCtorApps += newPath
            }
          }
          if (ref.pathsToLambdasAndCases.size > oldSize || ref.pathsToCtorApps.size > oldSize2)
            ref.references.foreach(ref.propagatePaths(_))
        case App(lhs, rhs) =>
          if (lhs eq this) {
            val oldSize2 = ref.pathsToCtorApps.size
            if (oldSize2 >= MaxPropagationWidth) warnWidth else
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
      
      //println(s"($showDef) := $that   ${pathsToLambdasAndCases}")
      
      val oldChildren = children
      _node = that
      
      // Remove old paths from this node:
      val oldPaths = pathsToLambdasAndCases.keysIterator.toBuffer
      if (oldPaths.nonEmpty && !that.isInstanceOf[Lam] && !that.isInstanceOf[Case]) {
        pathsToLambdasAndCases.clear()
        references.foreach(_.removePaths(this, oldPaths))
      }
      // Note: we're not removing paths to ctor apps, because these should (in principle) be stable
      
      children.foreach(_.addref(this)) // Note: we need to do that before `remrefs` or some counts will drop to 0 prematurely
      remrefs(oldChildren) // We should obviously not remove the refs of the _current_ children (_node assignment is done above)
      children.foreach(_.propagatePaths(this)(0))
      
      // This does not seem necessary, weirdly:
      //createPaths()
      //references.foreach(propagatePaths(_)(0))
      
    }
    def rewireTo(that: Ref, recIntros: Int = 0)(implicit mod: Module): Unit = node = Id(that, recIntros)
    
    def removePaths(child: Ref, paths: mutable.Buffer[Path]): Unit = {
      val oldSize = pathsToLambdasAndCases.size
      node match {
        case cnt @ Control(i, b) =>
          val toRemove = paths.flatMap(Path.throughControl(cnt, _))
          pathsToLambdasAndCases --= toRemove
          if (pathsToLambdasAndCases.size < oldSize) references.foreach(_.removePaths(this, toRemove))
        case Branch(c, t, e) =>
          val toRemove = if (t eq child) {
            paths.flatMap(Path.throughBranchLHS(c, _))
          } else {
            paths.flatMap { p =>
              p :: (Condition.tryNegate(c).flatMap(nc =>
                Condition.merge(p.cnd, nc).map(newCond => p.copy(cnd = newCond)))).toList
            }
          }
          pathsToLambdasAndCases --= toRemove
          if (pathsToLambdasAndCases.size < oldSize) references.foreach(_.removePaths(this, toRemove))
        case _: Lam | _: Case =>
          assert(pathsToLambdasAndCases.size === 1, (showDef, pathsToLambdasAndCases))
        case _ =>
          assert(pathsToLambdasAndCases.isEmpty, (showDef, pathsToLambdasAndCases))
          // ^ No concrete nodes should have paths, except for lambdas and cases (which have only paths to themselves)
      }
    }
    
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
      //case _: ConstantNode | _: Var => true
      case _: Var => true
      case _ => false
    }
    
    def iterator: Iterator[Ref] = mkIterator(false, mutable.HashSet.empty)
    def mkIterator(implicit rev: Bool, done: mutable.HashSet[Ref]): Iterator[Ref] = done.setAndIfUnset(this, {
      Iterator.single(this) ++ node.children.flatMap(_.mkIterator)
    }, Iterator.empty)
    
    def toBeShownInline: Bool = (
      //!showFull
      (!showFull || node.isInstanceOf[ConstantNode])
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
  
  sealed abstract class CallMaybe {
    def isCompatibleWith(cim: CallMaybe): Bool = merge(cim).isDefined
    def isIncompatibleWith(cim: CallMaybe): Bool = !isCompatibleWith(cim)
    def merge(cim: CallMaybe): Opt[CallMaybe] = (this, cim) match {
      case (CallNot(cids0), CallNot(cids1)) => Some(CallNot((cids0 ++ cids1).distinct))
      case (CallNot(c0), c1: CallId) => c1 optionUnless (c0 contains c1)
      case (c1: CallId, CallNot(c0)) => c1 optionUnless (c0 contains c1)
      case (c0: CallId, c1: CallId) => c0 optionIf c0 === c1
    }
  }
  abstract case class CallNot(cids: List[CallId]) extends CallMaybe {
    assert(cids.nonEmpty)
    override def toString = s"!{${cids.mkString(",")}}"
  }
  object CallNot {
    // We sort the cids so that we can later compare CallNot-s by simple structural equality
    def apply(cids: List[CallId]): CallNot = new CallNot(cids.sortBy(_.uid)){}
  }
  class CallId(val v: Var, val uid: Int) extends CallMaybe {
    def color = if (uid === -1) Debug.GREY else
      //colors((uid + colors.size - 1) % colors.size)
      colors(uid % colors.size)
    override def toString = s"$color$v:${uid.toHexString}$RESET"
    override def hashCode = uid.hashCode
    override def equals(that: Any) =
      //that match { case that: CallId => that.uid === uid; case _ => false }
      this eq that.asInstanceOf[AnyRef] // probably more efficient than the condition above
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
      //case p @ Pop(i) => s"$BOLD(${p.originalVar})↓$RESET${i.thenString}"
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
    def apply(r: NodeRef, recIntros: Int = 0) = Control(Id,r)(recIntros, 0)
  }
  
  
  def showGraphOf(refs: Iterator[Ref], modDefNames: Map[Ref, Str], printRefCounts: Bool, showRefs: Bool): Str = {
    val done = mutable.HashSet.empty[Ref]
    val iterator = refs.flatMap(_.mkIterator(false, done))
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
          val headStr = s"\n    ${r.showName} = $rhs;"
          val str =
            if (showRefs) s"$headStr\t\t\t\t{${r.references.mkString(", ")}}"
            else if (printRefCounts && r.references.size > 1) s"$headStr\t\t\t\t\t(x${r.references.size})"
            else headStr
          assert(!showPaths || !showCtorPaths)
          if (showPaths) (str
            + r.pathsToLambdasAndCases.map(pl =>
            s"\n\t  -- ${Condition.show(pl._1.cnd)} --> [${pl._1.in}]${pl._2}  +${pl._1.throughRecIntros} -${pl._1.throughRecElims}").mkString
            + r.usedPathsToLambdas.map(p => s"\n\t  -X- ${Condition.show(p.cnd)} --> [${p.in}]").mkString
          ) else if (showCtorPaths) (str
            + r.pathsToCtorApps.map(pc => s"\n\t  -- ${Condition.show(pc.cnd)} --> ${pc.modRef}${
                pc.refsRev.reverseIterator.map(arg => s" [${arg._1}]${arg._2}").mkString}  +${pc.throughRecIntros} -${pc.throughRecElims}").mkString
          ) else str
        } :: Nil
        else Nil
      )
    }.mkString
  }
  
}
