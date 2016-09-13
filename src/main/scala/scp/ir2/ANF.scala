package scp
package ir2

import lang2._
import scp.quasi2.MetaBases
import utils._
import utils.CollectionUtils._
import utils.meta.{RuntimeUniverseHelpers => ruh}
import ruh.sru

import scala.collection.mutable

/** Effectful-let-insertion-based implementation of ANF
  * 
  * 
  * 
  * */
class ANF extends AST with CurryEncoding with ANFHelpers { anf =>
  
  object ANFDebug extends PublicTraceDebug
  //import ANFDebug._  // Does not seem to be picked up because we already extend TraceDebug... :/
  
  
  def rep(definition: Def) = definition match {
    case Apply(RepDef(Abs(p,b)), a) if isConstructing || !hasHoles(b) => inline(p, b, a)
    case _ => new SimpleRep { val dfn = definition }
  }
  def dfn(r: Rep): Def = r.dfn
  
  def repType(r: Rep): TypeRep = r.dfn.typ
  
  sealed trait Rep { rep =>
    val dfn: Def
    val uniqueId = repCount oh_and (repCount += 1)
    
    /** Note: holes are considered impure;
      * Q: shouldn't Blocks look at their effects to decide purity? */
    lazy val isPure = dfn.isTrivial || (dfn match {
      // Note: relative order of v! and v:= matters, so neitheris pure.
      // Note: we do not treat Var.apply as pure so it is properly let-bound and specially handled
      case ma: MethodApp if ma.sym.fullName startsWith "scp.lib.uncurried" => true // TODO use annotations instead
      case _ => false
    })
    final def isImpure = !isPure
    
    def scrapEffects: SimpleRep
    def toBlock: Block
    def isBlock = false
    
    def inline: SimpleRep
    
    override def equals(that: Any) = that match {
      case that: Rep => uniqueId === that.uniqueId || isPure && that.isPure && this =~= that  case _ => false }
    override def hashCode = uniqueId
    
    override def toString = prettyPrint(this)
  }
  private var repCount = 0
  
  
  sealed trait SimpleRep extends Rep { rep =>
    
    def scrapEffects: this.type = {
      this |> traverse {
        case RepDef(_: Abs) => false
        case `rep` => true
        case r => r.scrapEffects; true
      }
      if (!isPure) effect_!(this)
      this
    }
    def toBlock: Block = Block(scrapEffects)
    
    def inline = this
    
    def renew = {
      require(isImpure)
      anf.rep(dfn)
    }
    
  }
  
  object Block {
    def apply(repFun: => Rep): Block = new Block ({ // Tries not to create a different Block we get a Block with no effects as the result
      repFun >>? { case b: Block =>
        if (currentBlock.effects isEmpty) return b
        else b
      }
    })
  }
  class Block private(repFun: => Rep) extends Rep {
    val effects = mutable.Buffer[SimpleRep]()
    
    val hasEffect: mutable.Set[Int] = mutable.Set[Int](
      scopes.headOption.iterator flatMap (_.hasEffect) toSeq: _*) // No need to register effects that are already registered in an outer scope
    
    val effectHoles = mutable.Set[Int]()
    
    scopes push this
    val result: SimpleRep = try { // Note that the result of a Block may be composed of references to its effects wrapped in some pure computation
      ANFDebug.debug("{{ New block }}"); ANFDebug.nestDbg {
        val r = repFun match {
          case b: Block =>
            b.effects foreach effect_!
            //effect_!(b.result) // Should not be necessary, as all effects of the result should already be registered
            b.result
          case s: SimpleRep => s
        }
        effects.filter_! { // Currently, we let-bind holes so effect holes retain the right eval order, and here remove the other ones
          case r @ RepDef(Hole(_) | SplicedHole(_)) => effectHoles(r.uniqueId)
          case _ => true
        }
        ANFDebug.debug("= "+r)
        r
      }
    } finally scopes pop;
    val dfn = result.dfn
    
    override lazy val isPure = effects.isEmpty && result.isPure  // Note: second clause should be redundant
    
    def reps = effects :+ result
    
    def inline = {
      effects foreach effect_!
      result.inline
    }
    override def scrapEffects = inline
    override def toBlock = this
    override def isBlock = true
    
  }
  protected val scopes = mutable.Stack[Block]()
  
  def currentBlock = scopes.top
  def ensureRegistered(r: Rep) = (r isPure) || (currentBlock hasEffect r.uniqueId)
  def effect_!(r: Rep): Unit = (ANFDebug.debug("! "+r)) before
    r match {
      case b: Block => b.effects foreach effect_!
      case r: SimpleRep =>
        val sc = currentBlock
        if (r.isImpure && !sc.hasEffect(r.uniqueId)) {
          sc.effects += r
          sc.hasEffect += r.uniqueId
        }
    }
  
  
  
  // Make sure to wrap terms in a top-level Block:
  override def wrapConstruct(r: => Rep): Rep = Block(super.wrapConstruct(r))
  override def wrapExtract(r: => Rep): Rep = Block(super.wrapExtract(r))
  
  // Wrap lambda bodies in Block's
  override def abs(param: BoundVal, body: => Rep): Rep = super.abs(param, Block(body))
  
  
  override def mergeableReps(a: Rep, b: Rep): Boolean = {
    //println(s"Merge? $a ~ $b  =>  "+(a.uniqueId == b.uniqueId))
    if (a.isPure || b.isPure) super.mergeableReps(a, b) // Q: is it safe to use || here? (instead of &&)
    else (a, b) match {
      case (b0: Block, b1: Block) => // typically, things extracted by holes will be wrapped in Block's (which may be distinct!) 
        if (b0.effects.size =/= b1.effects.size) return false
        ((b0.effects zipAnd b1.effects)(mergeableReps) forall identity) && mergeableReps(b0.result, b1.result)
      case _ => a.uniqueId == b.uniqueId
    }
  }
  
  /** ANF inlines everything, so we need to keep track of which effectful xtor Rep's matched which xtee so it's not later
    * matched with a distinct one (and vice versa).
    * To do this, we register associations inside the Extract map by using names of the form #{uniqueId}, and we remove
    * these extra names in the top-level `extractRep`.
    * Two impure extracted Reps can only be merged by `mergeableReps` if they are the same.
    * 
    * Note: this is not very efficient, as it will do the work several times: extract all effects, the result, and their
    * subexpressions independently and then only check if they extracted the same thing.
    * Solving this would involve advanced trickery or major redesign. */
  override protected def extract(xtor: Rep, xtee: Rep): Option[Extract] = {
    //dbgs("   "+xtor);dbgs("<< "+xtee)
    val (xrep, rep, res) = xtor -> xtee match {
        
      case RepDef(Typed(Hole(name), tp)) -> (s: SimpleRep) => // Wraps extracted Reps in Blocks
        ( xtor,  xtee,  extractType(tp, s.typ, Covariant) flatMap (e => merge((Map(name -> s.toBlock),Map(),Map()),e)) )
        // ^ FIXME does it break rep'd holes?
      case RepDef(Hole(name)) -> (s: SimpleRep) => ??? // TODO
        
      case (_: SimpleRep) -> (_: SimpleRep) => (xtor, xtee, super.extract(xtor, xtee))
      case (_: Block) -> (_: SimpleRep) => (xtor, xtee, None)
        
      //case _ -> (_: SimpleRep) => (xtor, xtee, super.extract(xtor, xtee)) // TODO make sure only match pure stuff when Block extracts Simple?
      ////case (_: Block) -> (_: SimpleRep) => (xtor, xtee, super.extract(xtor, xtee))
        
      case (b0: Block) -> (b1: Block) =>
        (b0, b1, 
          // TODO handle holes in effects (they can eat more than one effect!)
          if (b0.effects.size != b1.effects.size) None
          else mergeAll(b0.reps.iterator zip b1.reps.iterator map (extract _).tupled)
        )
      case _ => wth("A SimpleRep extracting a Block") // naked rep extracts a Block?!
      //case _ => wth(s"A SimpleRep used as an extractor: ${xtor} << $xtee") // naked rep extracts?!
    }
    
    val newRes = xtee match { case b: Block => res map filterANFExtract  case _ => res }
    
    if (rep.isPure && xrep.isPure   // Q: can it really happen that one is pure and the other not?
      || xtor.dfn.isInstanceOf[Hole]||xtor.dfn.isInstanceOf[SplicedHole]
      || xtee.isBlock // if we extracted a Block, no need to rememeber its id (block normally only happen at top-level or RHS of lambdas)
    ) newRes
    else newRes flatMap (merge(_, (Map(
      "#"+xrep.uniqueId -> rep,
      "#"+rep.uniqueId -> xrep   // TODO check not hole!!
    ),Map(),Map()) ))
  }
  
  override def extractRep(xtor: Rep, xtee: Rep): Option[Extract] = super.extractRep(xtor, xtee) map filterANFExtract
  
  /** TODO also handle vararg reps! */
  protected def filterANFExtract(maps: Extract) = (maps._1 filterKeys (k => !(k startsWith "#")), maps._2, maps._3)
  
  
  /** Note: could return a Rep and the caller could see if it was applied by watching eval of code... or not really (we may decide not to rewrite even if we call code) */
  override def rewriteRep(xtor: Rep, xtee: Rep, code: Extract => Option[Rep]): Option[Rep] = /*ANFDebug muteFor*/ {
    if (!xtee.isInstanceOf[Block]) return None
    
    val xbl = xtor.asInstanceOf[Block]
    val bl = xtee.asInstanceOf[Block]
    val reps = bl.effects :+ bl.result
    
    //println("Rewriting Block: "+bl)
    
    for (r <- reps; ex <- extract(xbl.result, r); res <- code(ex)) {
      ANFDebug debug(s"Rewriting $r -> $res")
      
      val cache = mutable.Map(ex._1 collect { case name -> rep if name startsWith "#" => name.tail.toInt -> rep } toSeq: _*)
      val removed = cache.keySet.toSet
      cache += r.uniqueId -> res
      
      // Updates references to rewritten Reps:
      val Updater = new RepTransformer({ r => cache get r.uniqueId map (
        _.inline and { r2 => if (r2 isImpure) ANFDebug debug s"UPDATE $r -> $r2" }) getOrElse r }, identity)
      
      return Some(Block {
        val newReps = for (e <- reps if !cache.isDefinedAt(e.uniqueId) || e.uniqueId == r.uniqueId) yield {
          val e2 = e |> Updater.apply
          e2 |> traversePartial {
            case r if removed(r.uniqueId) =>
              // A rewrite rule may want to remove an effect still used somewhere, which it should not be able to do
              ANFDebug debug(s"Rewrite aborted: $r is still used")
              return None 
          }
          cache += e.uniqueId -> e2
          e2 |> effect_!
          e2
        }
        newReps.last
      })
    }
    
    None
  }
  
  
  override def hole(name: String, typ: TypeRep): Rep = super.hole(name, typ) and effect_!
  override def splicedHole(name: String, typ: TypeRep): Rep = super.splicedHole(name, typ) and effect_!
  
  
  
  override def transformRep(r: Rep)(pre: Rep => Rep, post: Rep => Rep = identity): Rep =
    ANFDebug.debug(Console.BOLD+"--- NEW TRANSFO ---") before
    (new ANFRepTransformer(pre,post))(r)
  
  class ANFRepTransformer(pre: Rep => Rep, post: Rep => Rep) extends super.RepTransformer(pre, post) {
    val cache = mutable.Map[Int, Rep]()
    
    val Updater = new RepTransformer({ r => cache get r.uniqueId map (
      _ and (r2 => if (r2 isImpure) ANFDebug debug s"Updating $r -> $r2")) getOrElse r }, identity)
    
    override def apply(r: Rep): Rep = {
      import Console._
      def deb(str: => String) = if (r isImpure) ANFDebug.debug(str)
      deb {
        cache get r.uniqueId map { c =>
          s"Already transformed $r to "+c
        } getOrElse "Transforming "+r
      }
      ANFDebug nestDbg cache.getOrElseUpdate(r.uniqueId, {
        val r2 = pre(r)
        post(cache.getOrElseUpdate(r2.uniqueId, r2 match {
          case b: Block => Block {
            
            b.effects foreach { e =>
              deb("INIT "+e)
              val e2 = ANFDebug nestDbg Updater(e)
              deb("NEW "+e2)
              val e3 = apply(e2)
              deb("LAST "+e3)
              cache += e.uniqueId -> e3 // we might already have a binding e2->e3, but we also need e->e3!
              effect_!(e3)
            }
            
            var newRes = b.result // TODO factor logic w/ above..?
            deb("RET-INIT "+newRes)
            newRes = ANFDebug nestDbg Updater(newRes).inline // `inline` does nothing if it's already a SimpleRep
            deb("RET-NEW "+newRes)
            newRes = ANFDebug nestDbg apply(newRes).inline
            deb("RET-LAST "+newRes)
            cache += b.result.uniqueId -> newRes
            newRes
            //effect_!(newRes)
            newRes
          }
          case bv: BoundValRep => bv |> pre |> post  // BoundVal is not viewed as a Rep in AST, so AST never does that
          case _ =>
            val d = dfn(r2)
            val newD = apply(d) 
            (if (newD eq d) r else rep(newD))
            
        }))  and (r3 => cache += r3.uniqueId -> r3)
      }  ) and (res => if (!(r eq res)) deb(s"${BOLD}RW${RESET} $r  -->  $res"))
    }
  }
  
  override def readVal(v: BoundVal): Rep = v.asInstanceOf[BoundValRep]
  class BoundValRep(name: String, typ: TypeRep, annots: List[Annot]) extends super.BoundVal(name)(typ, annots) with SimpleRep {
    val dfn = this
    override def equals(that: Any) = that match { case bv: BoundValRep => bv.uniqueId === uniqueId  case _ => false }
  }
  override def bindVal(name: String, typ: TypeRep, annots: List[Annot]): BoundVal = new BoundValRep(name, typ, annots)
  
  def inline(param: BoundVal, body: Rep, arg: Rep) = {
    ANFDebug.debug(s"Inlining $arg as $param in $body")
    
    arg |> effect_!
    //assert(arg.isPure || (currentBlock hasEffect arg.uniqueId)) // TODO enable this and properly fix where it crashes
    
    val res = body match {
      case b: Block =>
        val effs = b.effects map (_ uniqueId) toSet;
        val Upd = new ANFRepTransformer(identity, { // Note: doing this in bottomUp; topDown does not work with ANF.. why?
          case RepDef(`param`) => arg
          case r: SimpleRep if effs(r.uniqueId) => r.renew
          // ^ Makes brand new effects for the inlined body (so they're not conflated with effects from unrelated inlinings)
          case r => r
        })
        Upd(b)
      case _ => wtf
    }
    
    res.inline
  }
  
  override def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep = {
    
    // TODO investigate -- weird: when the mtd is Imperative, we receive blocks here
    //assert(!self.isBlock && !(argss exists (_.reps exists (_ isBlock))), "WTF") // TODO BE
    
    val pure = mtd == Var.Apply.Symbol || mtd == Var.Bang.Symbol
    if (mtd == Imperative.Symbol) { // TODO rm: useless since effects should have already been propagated -- and if not, the order isn't going to be right...
      argss match {
        case ArgsVarargs(Args(), effs)::Args(res)::Nil =>
          effs.reps foreach effect_!
          effs.reps foreach {
            case r @ RepDef(Hole(_) | SplicedHole(_)) => currentBlock.effectHoles += r.uniqueId
            case _ =>
          }
          res
        case ArgsVarargSpliced(Args(), xs)::Args(res)::Nil =>
          //System.err.println("Unsupported @ "+(new Exception).getStackTrace()(0))
          // TODO check we're in xtion?
          super.methodApp(self, mtd, targs, argss, tp)
      }
    }
    else {
      //assert(!self.isBlock && !(argss exists (_.reps exists (_ isBlock))), "WTF") // TODO BE
      //super.methodApp(self, mtd, targs, argss, tp) and (ma => if (!pure) effect_!(ma))
      val newSelf = self.inline
      val newArgss = argss map (_.map(this)(_ inline))
      assert(!newSelf.isBlock && !(newArgss exists (_.reps exists (_ isBlock))), "WTF") // TODO BE
      val ma = super.methodApp(newSelf, mtd, targs, newArgss, tp)
      if (!pure) effect_!(ma)
      ma
    }
  }
  
  
  
  
  
  protected def ensureEnclosingScope(r: => SimpleRep) = if (scopes isEmpty) Block(r) else r
  
  protected def processSubs(originalR: Rep, newR: => Rep) = {
    // TODO? if was a block, make a block, otherwise inline/scrap
    
    //ensureEnclosingScope {
    //  val r = rFun
    //  //if (originalR.isBlock) 
    //  r.inline
    //}
    
    ensureEnclosingScope(newR.scrapEffects)
    
  }
  override def substitute(r: Rep, defs: Map[String, Rep]): Rep = processSubs(r, super.substitute(r, defs))
  override def substituteLazy(r: Rep, defs: Map[String, () => Rep]): Rep = processSubs(r, super.substituteLazy(r, defs))
  
  
}
