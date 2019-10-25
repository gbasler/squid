package graph4

import squid.utils._

import scala.collection.mutable
import squid.utils.CollectionUtils.MutSetHelper
import squid.utils.CollectionUtils.IteratorHelper

class GraphIR extends GraphDefs {
  
  val showInlineNames: Bool = false
  //val showInlineNames: Bool = true
  
  val showPaths: Bool = false
  //val showPaths: Bool = true
  
  val showFull: Bool = false
  //val showFull: Bool = true
  
  val showRefCounts: Bool = true
  //val showRefCounts: Bool = false
  
  val showRefs: Bool = false
  //val showRefs: Bool = true
  
  val strictCallIdChecking = true
  
  
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
  
  val knownModule = Set("GHC.Num", "GHC.Types", "GHC.Base")
  
  val DummyVar = new Var("<dummy>", -1)
  val DummyCallId = new CallId(DummyVar, -1) // TODO rm? or use
  
  
  /* Better to keep the `modDefs` definitions ordered, for easier reading of output code.
   * Unfortunately, it seems we get them in mangled order from GHC. */
  case class GraphModule(modName: Str, modPhase: Str, modDefs: List[Str -> NodeRef]) {
    private var uniqueCount = 0
    def nextUnique = uniqueCount alsoDo {uniqueCount += 1} // move somewhere else?
    
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
      // TODO
    }
    
    def simplify(): Bool = {
      
      var changed = false
      val traversed = mutable.Set.empty[NodeRef]
      
      def rec(ref: NodeRef): Unit = {
        
        def again(): Unit = {
          changed = true
          //scala.util.control.Breaks.break()
          // Note: commenting the following (or worse, uncommenting the above) will make some tests slow as they will print many more steps
          traversed -= ref; rec(ref)
        }
        
        traversed.setAndIfUnset(ref, ref.node match {
            
          case Control(i0, r @ NodeRef(Control(i1, body))) =>
            ref.node = Control(i0 `;` i1, body)
            again()
            
          case App(fun, arg) =>
            val ps = fun.pathsToLambdas.iterator.filterNot(_._1 |> ref.usedPathsToLambdas).headOption
            ps match {
              case Some((p @ (i, cnd), lr)) =>
                println(s"$ref -- $cnd -->> [$i]$lr")
                
                val l = lr.node.asInstanceOf[Lam]
                assert(l.paramRef.pathsToLambdas.isEmpty)
                
                val v = l.param
                val cid = new CallId(v, nextUnique)
                val ctrl = Control(Push(cid,i,Id), l.body)
                
                if ((lr eq fun) && lr.references.size === 1) {
                  // If `fun` is a directly applied one-shot lambda, we need less ceremony.
                  // Note: Can't actually perform an in-place replacement:
                  //       the lambda may have been reduced before,
                  //       and also its scope would become mangled (due to the captures/pop nodes)
                  assert(cnd.isEmpty)
                  assert(lr.references.head === ref)
                  
                  l.paramRef.node = Control(Drop(Id)(cid), arg)
                  ref.node = ctrl
                  // no need to update l.paramRef since the lambda is now supposed to be unused
                  assert(lr.references.isEmpty, lr.references)
                  
                } else {
                  
                  val newOcc = v.mkRefNamed(v.name+"_ε")
                  l.paramRef.node = Branch(Map(Id -> cid), Control.mkRef(Drop(Id)(cid), arg), newOcc)
                  l.paramRef = newOcc
                  if (cnd.isEmpty) {
                    ref.node = ctrl
                  } else {
                    val rebuiltApp = App(fun, arg).mkRefNamed("_ε")
                    rebuiltApp.usedPathsToLambdas ++= ref.usedPathsToLambdas
                    ref.usedPathsToLambdas.clear()
                    rebuiltApp.usedPathsToLambdas += p
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
    }
    
    def showGraph: Str = showGraph()
    def showGraph(showRefCounts: Bool = showRefCounts, showRefs: Bool = showRefs): Str =
      s"${Console.BOLD}module $modName where${Console.RESET}" +
        showGraphOf(modDefs.iterator.flatMap(_._2.iterator), modDefs.iterator.map(_.swap).toMap, showRefCounts, showRefs)
    
  }
  
  
}
