package graph4

import squid.utils._
import scala.collection.mutable

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
  
  /** Try putting fewer recursive markers after rewriting;
    * in particular, in places where this has not demonstrated usefulness so far (but has increase graph complexity). */
  val useAggressivePE = false
  //val useAggressivePE = true
  
  
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
    
    def simplify(): Bool = simplifyGraphModule(this)
    
    
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
