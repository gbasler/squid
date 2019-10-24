package graph4

import squid.utils._

import scala.collection.mutable
import squid.utils.CollectionUtils.MutSetHelper
import squid.utils.CollectionUtils.IteratorHelper

class GraphIR extends GraphDefs {
  
  val showInlineNames: Bool = false
  //val showInlineNames: Bool = true
  
  val printRefs: Bool = false
  //val printRefs: Bool = true
  
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
          case App(lr @ NodeRef(l: Lam), arg) =>
            if (lr.references.size === 1) {
              assert(lr.references.head === ref)
              
              // Note: can't actually perform an in-place replacement:\
              // the lambda may have been reduced before, and also its scope would become mangled (due to the captures/pop nodes)
              
              val v = l.param
              val cid = new CallId(v, nextUnique)
              l.paramRef.node = Control(Drop(Id)(cid), arg)
              ref.node = Control(Push(cid,Id,Id), l.body)
              
              again()
            }
            else {
              val v = l.param
              val cid = new CallId(v, nextUnique)
              //l.paramRef = Branch(Id, Map(cid -> Control.mkRef(Drop(Id)(cid), arg))).mkRefNamed("Φ")
              val newOcc = v.mkRefNamed("ε")
              l.paramRef.node = Branch(Map(Id -> cid), Control.mkRef(Drop(Id)(cid), arg), newOcc)
              l.paramRef = newOcc
              ref.node = Control(Push(cid,Id,Id), l.body)
              again()
            }
          case _ =>
            //println(s"Users of $ref: ${ref.references}")
            ref.children.foreach(rec)
        })
        
      }
      modDefs.foreach(_._2 |> rec)
      
      changed
    }
    
    // FIXME don't repeat shared defs!
    def show: Str = s"module $modName where" + modDefs.map{ case(n,r) => s"\n $n = ${r.showGraph}" }.mkString("")
    
  }
  
  
}
