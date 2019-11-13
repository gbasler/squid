package graph4

import squid.utils._

import scala.collection.mutable
import squid.utils.CollectionUtils.MutSetHelper
import squid.utils.CollectionUtils.IteratorHelper

class GraphIR extends GraphDefs {
  
  val debugScheduling: Bool = false
  //val debugScheduling: Bool = true
  
  val debugRewriting = false
  //val debugRewriting = true
  
  
  val showInlineNames: Bool = false
  //val showInlineNames: Bool = true
  
  val showPaths: Bool = debugRewriting
  //val showPaths: Bool = true
  
  val showCtorPaths: Bool = false
  //val showCtorPaths: Bool = true
  
  val showFull: Bool = debugRewriting
  //val showFull: Bool = true
  
  val showRefCounts: Bool = true
  //val showRefCounts: Bool = false
  
  val showRefs: Bool = false
  //val showRefs: Bool = true
  
  val smartRewiring = true
  //val smartRewiring = false
  
  val strictCallIdChecking = true
  
  /** Determines whether multiple reductions are performed with each simplification pass.
    * If logging of the rewritten graphs is enabled, doing only one step at a time will be much slower,
    * but it will make things easier to understand and debug. */
  val multiStepReductions = !debugRewriting
  //val multiStepReductions = false
  
  /** Setting this to `true` sounds like a good idea,
    * and it does simplify the graph since it removes control-flow case nodes with a single arm...
    * However, this ends up making scheduled programs more clumsy, as things like:
    *   `case ds of { (,) ρ ρ' -> ρ + ρ' }`
    * end up looking like:
    *   `(let (,) arg _ = ds in arg) + (let (,) _ arg = ds in arg)` */
  val ignoreSingleArmCases = false
  //val ignoreSingleArmCases = true
  
  val inlineScheduledLets = !debugScheduling
  //val inlineScheduledLets = false
  
  //val UnrollingFactor = 0  // does not even reduce recursive calls
  val UnrollingFactor = 1
  //val UnrollingFactor = 2
  
  val MaxPropagationDepth = 64
  val MaxPropagationWidth = 128
  
  /** Whether to push control nodes into other nodes which are only referenced once (so it's okay to copy them).
    * This sometimes overall increases the number of nodes, and in IterCont it turned a:
    *   `rec f = f (rec f)`
    * into a:
    *   `rec f = f (f (rec f))` */
  val rewriteSingleReferenced = true
  //val rewriteSingleReferenced = false
  
  val useOnlyIntLits = true
  
  
  type Ref = NodeRef
  val Ref: NodeRef.type = NodeRef
  
  
  object RewriteDebug extends PublicTraceDebug
  //import RewriteDebug.{debug=>Rdebug}
  
  
  protected def freshName =
    "$"
    //"_"
  
  def mkName(base: Str, idDomain: Str) =
    //base + "_" + idDomain  // gives names that are pretty cluttered...
    base
  
  def setMeaningfulName(r: Ref, n: Str): Unit = {
    if (r.name.isEmpty) r.name = Some(n)
  }
  
  private var varCount = 0
  
  def bindVal(name: Str): Var = {
    new Var(name, varCount) alsoDo (varCount += 1)
  }
  
  protected var curNodeRefId = 0
  
  val knownModule = Set("GHC.Num", "GHC.Types", "GHC.Base", "GHC.Real", "GHC.Maybe", "GHC.Classes")
  
  val DummyVar = new Var("<dummy>", -1)
  val DummyCallId = new CallId(DummyVar, -1) // TODO rm? or use
  {
    override def toString = s"$color<d>${Console.RESET}"
  }
  
  val WildcardVal = new Var("_", -2)
  
  abstract class Module {
    val roots: Ref => Bool
  }
  object DummyModule extends Module {
    val roots = _ => true
  }
  
  /* Better to keep the `modDefs` definitions ordered, for easier reading of output code.
   * Unfortunately, it seems we get them in mangled order from GHC. */
  case class GraphModule(modName: Str, modPhase: Str, modDefs: List[Str -> NodeRef]) extends Module {
    require(modDefs.map(_._1).distinct.size === modDefs.size)
    private implicit def self: this.type = this
    private var uniqueCount = 0
    def nextUnique = uniqueCount alsoDo {uniqueCount += 1} // move somewhere else?
    
    var betaReductions: Int = 0
    var caseReductions: Int = 0
    var caseCommutings: Int = 0
    var fieldReductions: Int = 0
    var oneShotBetaReductions: Int = 0
    
    val roots = modDefs.iterator.map(_._2).toSet
    
    def nodes = modDefs.iterator.flatMap(_._2.iterator)
    
    /** There might be cyclic subgraphs (due to recursion or sel-feed) which are no longer reachable
      * but still point to reachable nodes.
      * I'd like to keep everything deterministic so I prefer to avoid using weak references,
      * and to instead call this special gc() function from time to time.
      * TODO actually call it */
    def gc(): Unit = {
      // TODO
      checkRefs()
    }
    def checkRefs(): Unit = {
      import squid.utils.CollectionUtils.MutSetHelper
      def go(ref: Ref)(implicit done: mutable.HashSet[Ref] = mutable.HashSet.empty): Unit = done.setAndIfUnset(ref, {
        ref.children.foreach { c =>
          assert(c.references.contains(ref), s"$c does not back-reference $ref, but only ${c.references}")
          go(c)
        }
      })
      modDefs.foreach(_._2 |> go)
    }
    
    def simplify(): Bool = scala.util.control.Breaks tryBreakable {
      
      var changed = false
      val traversed = mutable.Set.empty[NodeRef]
      
      var next = modDefs.map(_._2) // TODO use an ArrayBuffer to generate less garbage
      
      while (next.nonEmpty) {
        val ref = next.head
        next = next.tail
        
        traversed += ref
        
        def again(): Unit = {
          changed = true
          if (!multiStepReductions)
            scala.util.control.Breaks.break()
          // Note: commenting the following (or worse, enabling the above) will make some tests slow as they will print many more steps
          traversed -= ref; next = ref :: next
          //continue()
        }
        def continue(): Unit = {
          next :::= ref.children.filterNot(traversed).toList
        }
        
        ref.node match { // TODO simplify obvious things like boxes that resolve branches...
            
          case c0 @ Control(i0, r @ NodeRef(c1 @ Control(i1, body))) =>
            // if we do control/branch reduction as below, the following tests become necessary to prevent divergence:
            val ri = c0.recIntros + c1.recIntros
            val re = c0.recElims + c1.recElims
            //if (ri > UnrollingFactor || re > UnrollingFactor) {
            if (ri > UnrollingFactor) { // Less stringent than the above, but seems to be sufficient
            //if (ri > UnrollingFactor * 3) { // TODO experiment with this
              // ^ note: using UnrollingFactor * K where K > 1 seems to help, but I'm not sure scheduling can handle it
              continue()
            } else {
              //val old = ref.showDef
              ref.node = Control(i0 `;` i1, body)(ri, re)
              //println(s"BOX ${old} (${r.showDef}) ~~~> ${ref.show}")
              again()
            }
            
          case cn @ Control(i, br @ NodeRef(Branch(c, t, e))) =>
            Condition.test(c, i) match {
              case Some(b) =>
                println(s"BRANCH ${ref.showName} = [$i] ${br.showDef} ~~~> [...] ${if (b) t else e}")
                ref.node = Control(i, if (b) t else e)(cn.recIntros, cn.recElims)
                again()
              case None =>
                if (rewriteSingleReferenced && i.isInstanceOf[Push] && br.references.size === 1) {
                // Note: Using this less restrictive condition is not a good idea (will lose work sharing),
                //       but it should work; currently however it crashes some tests.
                //if (rewriteSingleReferenced && br.references.size === 1) {
                  Condition.throughControl(i,c) match {
                    case Some(newCnd) =>
                      println(s"CONTROL/BRANCHx1 ${ref.showDef}")
                      ref.node = Branch(newCnd,
                        Control(i, t)(cn.recIntros, cn.recElims).mkRefFrom(ref),
                        Control(i, e)(cn.recIntros, cn.recElims).mkRefFrom(ref))
                      again()
                    case None => continue()
                  }
                }
                else continue()
            }
            
          case cn @ Control(i: Push, ap @ NodeRef(App(lhs,rhs))) if rewriteSingleReferenced && ap.references.size === 1 =>
            println(s"CONTROL/APPx1 ${ref.showDef}")
            ref.node = App(Control(i, lhs)(cn.recIntros, cn.recElims).mkRefFrom(ref), Control(i, rhs)(cn.recIntros, cn.recElims).mkRefFrom(ref))
            again()
          case cn @ Control(i: Push, cs @ NodeRef(Case(scrut,arms))) if rewriteSingleReferenced && cs.references.size === 1 =>
            println(s"CONTROL/CASEx1 ${ref.showDef}")
            ref.node = Case(Control(i, scrut)(cn.recIntros, cn.recElims).mkRefFrom(ref), arms.map {
              case (ctorName, ctorArity, armBody) =>
                (ctorName, ctorArity, Control(i, armBody)(cn.recIntros, cn.recElims).mkRefFrom(ref))
            })
            again()
            
          case c0 @ Control(i0, r @ NodeRef(k: ConstantNode)) =>
            ref.node = k
            
          // TODO should we remove controls over constants? How do scheduled programs look like then?
            
          // TODO one-shot reductions for pattern matching too
            
          // TODO only insert boxes when necessary (e.g., not over mere constants), and push them into branches
            
          case Case(scrut, arms) =>
            val ps = scrut.pathsToCtorApps.headOption orElse scrut.realPathsToCases.headOption
            ps match {
              case Some(CtorPath(ctor, argsRev, cnd, ri, re)) =>
                println(s"CASE/CTOR $ref -- ${Condition.show(cnd)} -->> $ctor${argsRev.reverseIterator.map(arg => s" [${arg._1
                  }${if (ri > 0) "+"+ri else ""}${if (re > 0) "-"+re else ""}]${arg._2}").mkString}")
                
                // ASSUMPTION: no need to use ri/re here; they will be used in CtorField reductions, which should be enough...
                //   TODO test the assumption again later
                
                caseReductions += 1
                
                val rightArm = arms.filter(_._1 === ctor.defName) |>! {
                  case arm :: Nil => arm._3
                  case Nil =>
                    arms.filter(_._1 === "_") |>! {
                      case arm :: Nil => arm._3
                      case Nil => Bottom.mkRef
                    }
                }
                val rightArmAndControl = if (ri > 0 || re > 0) Left(Control(Id, rightArm)(ri,re)) else Right(rightArm)
                
                if (cnd.isEmpty) {
                  rightArmAndControl.fold(ref.node = _, ref rewireTo _)
                } else {
                  val rebuiltScrut = new RebuildCtorPaths(cnd)(scrut, Id)
                  val rebuiltCase = Case(rebuiltScrut, arms).mkRefNamed("_cε")
                  ref.node = Branch(cnd, rightArmAndControl.fold(_.mkRef, id), rebuiltCase)
                }
                again()
              case Some((p @ Path(i, cnd, ri, re), lr @ Ref(l: Lam))) =>
                ??? // make it bottom? this case is meaningless...
                again()
              case Some((p @ Path(i, cnd, ri, re), cse @ Ref(c: Case))) =>
                println(s"CASE/CASE $ref -- ${Condition.show(cnd)} -->> [$i${if (ri > 0) "+"+ri else ""}${if (re > 0) "-"+re else ""}]$cse")
                
                caseCommutings += 1
                
                val newArms = c.arms.map { case (armCtorName, armArity, armBody) =>
                  val cntrl = Control.mkRef(i, armBody, ri, re)
                  (armCtorName, armArity, Case(cntrl, arms).mkRefFrom(ref))
                }
                
                val cScrut = Control.mkRef(i, c.scrut, ri, re)
                val commutedCase = Case(cScrut, newArms)
                val commutedCaseWithControl = if (ri > 0  || re > 0) Control(Id, commutedCase.mkRefFrom(cse))(ri, re) else commutedCase
                
                if (cnd.isEmpty) {
                  ref.node = commutedCaseWithControl
                } else {
                  val rebuiltScrut = new RebuildCtorPaths(cnd)(scrut, Id)
                  val rebuiltCase = Case(rebuiltScrut, arms).mkRefNamed("_ccε")
                  ref.node = Branch(cnd, commutedCaseWithControl.mkRef, rebuiltCase)
                }
                again()
              case Some((p @ Path(i, cnd, ri, re), cse @ Ref(_))) =>
                println(s"WAT-CASE $ref $p ${cse.showDef}")
                ??? // FIXME? unexpected
                continue()
                //scrut.pathsToLambdasAndCases -= p
                //again()
              case None =>
                if (scrut.node === Bottom) ref.node = Bottom
                else {
                  val newArms = arms.filter(_._3.node =/= Bottom)
                  if (newArms.size < arms.size) {
                    newArms match {
                      case Nil => ref.node = Bottom
                      case arm :: Nil => ref.rewireTo(arm._3)
                      case _ => ref.node = Case(scrut, newArms)
                    }
                    again()
                  }
                  else {
                    val ctorArms = arms.flatMap { case (c,a,r) =>
                      val paths = r.pathsToCtorApps.filter(_.cnd.isEmpty) // TODO generalize to paths having branches
                      assert(paths.size <= 1, paths) // Note: I've seen this fail when paths used to get messed up
                      paths.take(1).map(p => (c,a,p))
                    }
                    if (ctorArms.size === arms.size) {
                      val sameCtorArmGroups = ctorArms.groupBy(_._3.modRef)
                      //println(sameCtorArmGroups)
                      if (sameCtorArmGroups.size === 1 && sameCtorArmGroups.head._2.forall(p =>
                        p._3.throughRecElims < UnrollingFactor && p._3.throughRecIntros < UnrollingFactor))
                      {
                        val (sameCtor, sameCtorArms) = sameCtorArmGroups.head
                        println(s"CASE-ARM/CTOR (${sameCtor}) ${sameCtorArms.map(x =>
                          "<<"+x._3.refsRev.reverse.map(cr => s"[${cr._1}]${cr._2}")
                            .mkString(" ")+s" +${x._3.throughRecIntros} -${x._3.throughRecElims}>>").mkString(" ")}")
                        assert(sameCtorArms.size === arms.size)
                        assert(sameCtorArms.nonEmpty)
                        val argsNum = sameCtorArms.head._3.refsRev.size
                        assert(sameCtorArms.tail.forall(_._3.refsRev.size === argsNum))
                        ref.node = sameCtorArms.head._3.refsRev.indices.foldLeft[ConcreteNode](sameCtor) {
                          case (apps, idx) =>
                            val newCase = Case(scrut, sameCtorArms.map { case (ctorName, arity, cPath) =>
                              val (i,r) = cPath.refsRev(argsNum - idx - 1) // the refs are reversed in CtorPath
                              val ctrl = Control.mkRef(i, r, cPath.throughRecIntros, cPath.throughRecElims)
                              (ctorName, arity, ctrl)
                            })
                            App(apps.mkRef, newCase.mkRef)
                        }
                        again()
                      }
                      else continue()
                    }
                    else continue()
                  }
                }
            }
            
          case CtorField(Ref(Bottom), ctorName, ari, idx) =>
            Bottom
            
          case CtorField(scrut, ctorName, ari, idx) =>
            val ps = scrut.pathsToCtorApps.headOption orElse scrut.realPathsToCases.headOption
            ps match {
              case Some(CtorPath(ctor, argsRev, cnd, ri, re)) =>
                println(s"FIELD/CTOR $ref -- ${Condition.show(cnd)} -->> $ctor${argsRev.reverseIterator.map(arg => s" [${arg._1
                  }${if (ri > 0) "+"+ri else ""}${if (re > 0) "-"+re else ""}]${arg._2}").mkString}")
                
                fieldReductions += 1
                
                val ctrl = if (ctorName === ctor.defName) Some {
                  assert(argsRev.size === ari)
                  val (instr, rightField) = argsRev.reverseIterator.drop(idx).next
                  Control(instr, rightField)(ri,
                    re min ri)
                    // ^ The idea is that if we did not match across a recursive PRODUCER,
                    //   there is no need to prevent further reductions on this path.
                    //   This allows reducing static structures, such as lists given extensionally in the source.
                } else None
                
                if (cnd.isEmpty) {
                  ref.node = ctrl getOrElse Bottom
                } else {
                  val rebuiltScrut = new RebuildCtorPaths(cnd)(scrut, Id)
                  val rebuiltCtorField = CtorField(rebuiltScrut, ctorName, ari, idx)
                  ref.node = ctrl match {
                    case Some(ctrl) =>
                      val rebuiltCtorFieldRef = rebuiltCtorField.mkRefNamed("_fε")
                      Branch(cnd, ctrl.mkRef, rebuiltCtorFieldRef) // TODO name mkRef
                    case None => rebuiltCtorField
                  }
                }
                again()
              case Some((p @ Path(i, cnd, ri, re), lr @ Ref(l: Lam))) =>
                ??? // make it bottom? this case is meaningless...
                again()
              case Some((p @ Path(i, cnd, ri, re), cse @ Ref(c: Case))) =>
                println(s"FIELD/CASE $ref -- ${Condition.show(cnd)} -->> [$i${if (ri > 0) "+"+ri else ""}${if (re > 0) "-"+re else ""}]$cse")
                
                caseCommutings += 1
                
                val newArms = c.arms.map { case (armCtorName, armArity, armBody) =>
                  val cntrl = Control.mkRef(i, armBody, ri, re)
                  (armCtorName, armArity, CtorField(cntrl, ctorName, ari, idx).mkRefFrom(ref))
                }
                
                val cScrut = Control.mkRef(i, c.scrut, ri, re)
                val commutedCase = Case(cScrut, newArms).mkRefFrom(ref)
                val commutedCaseWithControl =
                  if (re > 0) // Even though we put ri/re inside the scrut, we still need to prevent more field/case on the resulting top-level case!
                    Control(Id,commutedCase)(0,re).mkRef
                  else commutedCase
                
                if (cnd.isEmpty) {
                  ref.rewireTo(commutedCaseWithControl)
                } else {
                  val rebuiltScrut = new RebuildCtorPaths(cnd)(scrut, Id)
                  val rebuiltCtorField = CtorField(rebuiltScrut, ctorName, ari, idx).mkRefNamed("_cfε")
                  ref.node = Branch(cnd, commutedCaseWithControl, rebuiltCtorField)
                }
                again()
              case Some((p @ Path(i, cnd, ri, re), cse @ Ref(_))) =>
                println(s"WAT-FIELD $ref $p ${cse.showDef}")
                ??? // FIXME? unexpected
                continue()
                //scrut.pathsToLambdasAndCases -= p
                //again()
              case None =>
                continue()
            }
          case App(fun, arg) =>
            val ps = if (smartRewiring) fun.pathsToLambdasAndCases.headOption // TODO check path correct
              // If we are not using smart rewiring, we need to keep track of which reductions have already been done: 
              else fun.pathsToLambdasAndCases.iterator.filterNot(_._1 |> ref.usedPathsToLambdas).headOption
            
            ps match {
              case Some((p @ Path(i, cnd, ri, re), lr @ Ref(l: Lam))) =>
                println(s"LAMBDA $ref -- ${Condition.show(cnd)} -->> [$i${if (ri > 0) "+"+ri else ""}${if (re > 0) "-"+re else ""}]$lr")
                
                betaReductions += 1
                
                assert(l.paramRef.pathsToLambdasAndCases.isEmpty)
                
                val v = l.param
                val cid = new CallId(v, nextUnique)
                val ctrl = Control(Push(cid,i,Id), l.body)(ri, re)
                
                // TODO generalize to lr.nonBoxReferences.size === 1 to one-shot reduce across boxes
                //      or more generally one-shot reduce across boxes and ONE level of branches...
                if ((lr eq fun) && lr.references.size === 1) {
                  // If `fun` is a directly applied one-shot lambda, we need less ceremony.
                  // Note: Can't actually perform an in-place replacement:
                  //       the lambda may have been reduced before,
                  //       and also its scope would become mangled (due to the captures/pop nodes)
                  println(s"One shot!")
                  assert(cnd.isEmpty)
                  assert(lr.references.head === ref)
                  assert(ri === 0 && re === 0)
                  
                  oneShotBetaReductions += 1
                  
                  l.paramRef.node = Control(Drop(Id)(cid), arg)(ri, re)
                  ref.node = ctrl
                  // no need to update l.paramRef since the lambda is now supposed to be unused
                  assert(lr.references.isEmpty, lr.references)
                  
                } else {
                  
                  val newOcc = v.mkRefNamed(v.name+"_ε")
                  l.paramRef.node = Branch(Map(Id -> cid),
                    Control.mkRef(Drop(Id)(cid), arg,
                      re, ri), // NOTE inversion of ri/re here! Because this is the consumer part of the recursion...
                    newOcc)
                  l.paramRef = newOcc
                  if (cnd.isEmpty) {
                    ref.node = ctrl
                  } else {
                    val rebuiltFun = if (smartRewiring) new RebuildLambdaPaths(cnd)(fun, Id) else fun
                    val rebuiltApp = App(rebuiltFun, arg).mkRefNamed("_ε")
                    if (!smartRewiring) {
                      rebuiltApp.usedPathsToLambdas ++= ref.usedPathsToLambdas
                      ref.usedPathsToLambdas.clear()
                      rebuiltApp.usedPathsToLambdas += p
                    }
                    ref.node = Branch(cnd, ctrl.mkRef, rebuiltApp)
                  }
                  
                }
                again()
              case Some((p @ Path(i, cnd, ri, re), lr @ Ref(c: Case))) =>
                ??? // make it bottom? this case is meaningless...
                again()
              case None =>
                continue()
            }
            
          case _ =>
            //println(s"Users of $ref: ${ref.references}")
            continue()
            
        }
        
      }
      
      changed
      
    } catchBreak true
    
    
    /* TODO this is quite complex and essentially needs to be kept in sync with `propagatePaths`;
         it may be much easier to just store all the rebuilding info in the paths themselves,
         but that may also be more dangerous and difficult to debug (if the graph was changed in unexpected ways). */
    protected class RebuildPaths(fullCnd: Condition) {
      def apply(ref: Ref, ictx: Instr): Ref = rec(ref, ictx, fullCnd)
      protected def rec(ref: Ref, ictx: Instr, curCnd: Condition): Ref = ref.node match {
        case c @ Control(i, b) => Control(i, rec(b, ictx `;` i, curCnd))(c.recIntros, c.recElims).mkRefFrom(ref)
        case Branch(brCnd, t, e) =>
          val brCndUpdatedOpt = Condition.throughControl(ictx, brCnd) // I've seen this fail (in test Church.hs)... not sure that's legit
          /*
          //val accepted = brCndOpt.isDefined && brCndOpt.get.forall{case(i,cid) => cnd.get(i).contains(cid)}
          val accepted = brCndOpt.isDefined && brCndOpt.get.forall{case(i,cim) => cnd.get(i).exists(_.isCompatibleWith(cim))}
          val newCnd = if (accepted) {
            val brCnd = brCndOpt.get
            //assert(brCnd.forall{case(i,cid) => curCnd.get(i).contains(cid)}, (brCnd,curCnd,cnd))
            assert(brCnd.forall{case(i,cim) => curCnd.get(i).exists(_.isCompatibleWith(cim))}, (brCnd,curCnd,cnd))
            curCnd -- brCnd.keysIterator
          } else curCnd
          */
          val mrgCnd = brCndUpdatedOpt.flatMap(brCnd => Condition.merge(brCnd, fullCnd))
          val accepted = mrgCnd.isDefined // implies brCndUpdatedOpt.isDefined (if it's not, ofc the branch condition is not accepted)
          def rebuild(newCnd: Condition): Ref =
            (if (accepted) Branch(brCnd, rec(t, ictx, newCnd), e) else Branch(brCnd, t, rec(e, ictx, newCnd))).mkRefFrom(ref)
          val adaptedBrCnd = if (accepted) brCndUpdatedOpt.get else {
            if (brCndUpdatedOpt.nonEmpty && Condition.merge(brCndUpdatedOpt.get, fullCnd).isEmpty) {
              // This corresponds to the case, in `propagatePaths`,
              // where the path coming from the else arm is already incompatible with the branch's condition.
              return rebuild(curCnd)
            } else {
              val brCndElse = Condition.tryNegate(brCnd).get
              Condition.throughControl(ictx, brCndElse).get
            }
          }
          val newCnd = curCnd.flatMap {
            case b @ (i, cid: CallId) => adaptedBrCnd.get(i) match {
              case Some(cid2: CallId) => assert(cid2 === cid, (i, cid, cid2)); None
              case Some(CallNot(cids)) => assert(!cids.contains(cid)); Some(b)
              case None => Some(b)
            }
            case b @ (i, CallNot(cids)) => adaptedBrCnd.get(i) match {
              case Some(cid: CallId) => die
              case Some(CallNot(cids2)) =>
                val cids2s = cids2.toSet
                assert((cids2s -- cids).isEmpty)
                Some((i, CallNot(cids.filterNot(cids2s))))
              case None => Some(b)
            }
          }
          if (newCnd.isEmpty) {
            if (accepted) e else t
          } else rebuild(newCnd)
        case _ => lastWords(s"was not supposed to reach ${ref.showDef}")
      }
    }
    
    class RebuildLambdaPaths(cnd: Condition) extends RebuildPaths(cnd)
    
    class RebuildCtorPaths(cnd: Condition) extends RebuildPaths(cnd) {
      override def rec(ref: Ref, ictx: Instr, curCnd: Condition): Ref = ref.node match {
        case App(lhs, rhs) => App(rec(lhs, ictx, curCnd), rhs).mkRefFrom(ref)
        case _ => super.rec(ref, ictx, curCnd)
      }
    }
    
    trait RebuildDebug extends RebuildPaths {
      override def rec(ref: Ref, ictx: Instr, curCnd: Condition): Ref = {
        println(s"Rebuild [$ictx] ${ref.showDef}   with  ${Condition.show(curCnd)}")
        super.rec(ref: Ref, ictx, curCnd)
      }
    }
    
    
    def showGraph: Str = showGraph()
    def showGraph(showRefCounts: Bool = showRefCounts, showRefs: Bool = showRefs): Str =
      s"${Console.BOLD}module $modName where${Console.RESET}" +
        showGraphOf(nodes, modDefs.iterator.map(_.swap).toMap, showRefCounts, showRefs)
    
    object Stats {
      val (tot, lams, apps, boxes, brans) = {
        var tot, lams, apps, boxes, brans = 0
        nodes.map(_.node alsoDo {tot += 1}).foreach {
          case _: Lam =>
            lams += 1
          case _: App =>
            apps += 1
          case _: Control =>
            boxes += 1
          case _: Branch =>
            brans += 1
          case _ =>
        }
        (tot, lams, apps, boxes, brans)
      }
      //val unreducedRedexes =  // TODO port from v3?
    }
    
  }
  
  
}
