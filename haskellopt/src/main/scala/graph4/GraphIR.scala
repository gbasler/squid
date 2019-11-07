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
  
  val showPaths: Bool = false
  //val showPaths: Bool = true
  
  val showCtorPaths: Bool = debugRewriting
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
  //val ignoreSingletonCases = true
  
  val inlineScheduledLets = !debugScheduling
  //val inlineScheduledLets = false
  
  val UnrollingFactor = 1
  
  val MaxPropagationDepth = 64
  val MaxPropagationWidth = 128
  
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
      
      def rec(ref: NodeRef): Unit = { // TODO make stack-safe
        
        def again(): Unit = {
          changed = true
          if (!multiStepReductions)
            scala.util.control.Breaks.break()
          // Note: commenting the following (or worse, enabling the above) will make some tests slow as they will print many more steps
          traversed -= ref; rec(ref)
          //ref.children.foreach(rec)
        }
        
        traversed.setAndIfUnset(ref, ref.node match { // TODO simplify obvious things like boxes that resolve branches...
            
          case c0 @ Control(i0, r @ NodeRef(c1 @ Control(i1, body))) =>
            ref.node = Control(i0 `;` i1, body)(c0.recIntros + c1.recIntros, c0.recElims + c1.recElims)
            again()
            
          // TODO one-shot reductions for pattern matching too
            
          case Case(scrut, arms) =>
            val ps = scrut.pathsToCtorApps.headOption
            ps match {
              case Some(CtorPath(ctor, argsRev, cnd, ri, re)) =>
                println(s"CASE $ref -- ${Condition.show(cnd)} -->> $ctor${argsRev.reverseIterator.map(arg => s" [${arg._1
                  }${if (ri > 0) "+"+ri else ""}${if (re > 0) "-"+re else ""}]${arg._2}").mkString}")
                
                // ASSUMPTION: no need to use ri/re here; they will be used in CtorField reductions, which should be enough...
                
                caseReductions += 1
                
                val rightArm = arms.filter(_._1 === ctor.defName) |>! {
                  case arm :: Nil => arm._3
                  case Nil =>
                    arms.filter(_._1 === "_") |>! { // FIXME handle this failing
                      case arm :: Nil => arm._3
                    }
                }
                if (cnd.isEmpty) {
                  ref.rewireTo(rightArm)
                } else {
                  val rebuiltScrut = new RebuildCtorPaths(cnd)(scrut, Id)
                  val rebuiltCase = Case(rebuiltScrut, arms).mkRefNamed("_cε")
                  ref.node = Branch(cnd, rightArm, rebuiltCase)
                }
                again()
              case None =>
                ref.children.foreach(rec)
            }
            
          case CtorField(scrut, ctorName, ari, idx) =>
            val ps = scrut.pathsToCtorApps.headOption
            ps match {
              case Some(CtorPath(ctor, argsRev, cnd, ri, re)) =>
                println(s"FIELD $ref -- ${Condition.show(cnd)} -->> $ctor${argsRev.reverseIterator.map(arg => s" [${arg._1
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
                  //ref.rewireTo(ctrl)
                  ref.node = ctrl getOrElse ModuleRef("Prelude","undefined") // TODO improve
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
              case None =>
                ref.children.foreach(rec)
            }
            
          case App(fun, arg) =>
            val ps = if (smartRewiring) fun.pathsToLambdas.headOption
              // If we are not using smart rewiring, we need to keep track of which reductions have already been done: 
              else fun.pathsToLambdas.iterator.filterNot(_._1 |> ref.usedPathsToLambdas).headOption
            
            ps match {
              case Some((p @ Path(i, cnd, ri, re), lr)) =>
                println(s"LAMBDA $ref -- ${Condition.show(cnd)} -->> [$i${if (ri > 0) "+"+ri else ""}${if (re > 0) "-"+re else ""}]$lr")
                
                betaReductions += 1
                
                val l = lr.node.asInstanceOf[Lam]
                assert(l.paramRef.pathsToLambdas.isEmpty)
                
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
              case None =>
                ref.children.foreach(rec)
            }
            
          case _ =>
            //println(s"Users of $ref: ${ref.references}")
            ref.children.foreach(rec)
            
        })
        
      }
      modDefs.foreach(_._2 |> rec)
      
      changed
      
    } catchBreak true
    
    protected class RebuildPaths(cnd: Condition) {
      def apply(ref: Ref, ictx: Instr): Ref = rec(ref, ictx, cnd)
      protected def rec(ref: Ref, ictx: Instr, curCnd: Condition): Ref = ref.node match {
        case c @ Control(i, b) => Control(i, rec(b, ictx `;` i, curCnd))(c.recIntros, c.recElims).mkRefFrom(ref)
        case Branch(c, t, e) =>
          val brCndOpt = Condition.throughControl(ictx, c) // I've seen this fail (in test Church.hs)... not sure that's legit
          val accepted = brCndOpt.isDefined && brCndOpt.get.forall{case(i,cid) => cnd.get(i).contains(cid)}
          val newCnd = if (accepted) {
            val brCnd = brCndOpt.get
            assert(brCnd.forall{case(i,cid) => curCnd.get(i).contains(cid)}, (brCnd,curCnd,cnd))
            curCnd -- brCnd.keysIterator
          } else curCnd
          if (newCnd.isEmpty) {
            if (accepted) e else t
          } else
            (if (accepted) Branch(c, rec(t, ictx, newCnd), e) else Branch(c, t, rec(e, ictx, newCnd))).mkRefFrom(ref)
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
