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
  
  // TODO norm of a Block's result?
  //def rep(definition: Def) = postProcess(definition match {
  //def rep(definition: Def) = ensureEnclosingScope(postProcess(definition match {
  def rep(definition: Def) = Block mkNoProcess (postProcess(definition match {
  //def rep(definition: Def) = postProcess(new SimpleRep { val dfn: Def = definition match {
      
    case Apply(RepDef(Abs(p,b)), a) if isConstructing || (!hasHoles(b) && !p.isExtractedBinder) =>
    //case Apply(RepDef(Abs(p,b)), a) if isConstructing || (!p.isExtractedBinder) =>
    //case Apply(RepDef(Abs(p,b)), a) =>
      //currentBlock.effectHoles += a.uniqueId
      a |>? { case s: SimpleRep if s isHole => currentBlock.effectHoles += s.uniqueId } // TODO what if the arg is a bigger expr (potentially still trivial!) containing a hole?
      //println(s"INL $p "+b)
      
      //inline(p, b, a.inline)
      inline(p, b, a.normalize)  //and (res => println(s"Inlined: $res"))
      //inline(p, b, a.normalize withName p)
      
    case Ascribe(s,t) =>
      //Ascribe(s.inline, t) |> mkRep
      Ascribe(s.normalize, t) |> mkRep
      
    case MethodApp(self, Imperative.Symbol, _::Nil, ArgList(effs @ _*)::Args(res)::Nil, typ) =>
      //effs foreach (_ inline)
      effs foreach {
        case r: SimpleRep if r isHole =>
          //println("EFF "+r)
          currentBlock.effectHoles += r.uniqueId
          r |> effect_!
        //case r => r.inline |> effect_!
        case r => r.normalize
      }
      //res.inline
      res.normalize
      //???
      
      // TODO?:
    case MethodApp(self, sym, targs, argss, typ) =>
      //val newSelf = self.inline
      //val newArgss = argss map (_.map(this)(_ inline))
      val newSelf = self.normalize
      val newArgss = argss map (_.map(this)(_ normalize))
      
      //new SimpleRep {
      //  val dfn: Def = MethodApp(newSelf, sym, targs, newArgss, typ)
      //}
      MethodApp(newSelf, sym, targs, newArgss, typ) |> mkRep
      
    //case _ => new SimpleRep { val dfn = definition }
    case d => d |> mkRep
  })) //and effect_!  // some intermediate reps you don't want to let-bind (cf transformer)
  //}) and effect_!)
  private def mkRep(d: Def) = new SimpleRep { val dfn: Def = d }
  
  def dfn(r: Rep): Def = r.dfn
  
  def repType(r: Rep): TypeRep = r.dfn.typ
  
  sealed trait Rep { rep =>
    val dfn: Def
    val uniqueId = repCount oh_and (repCount += 1)
    //if (uniqueId == 37)
    if (uniqueId == 27)
      42
    
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
    
    def normalize = inline and effect_!
    
    override def equals(that: Any) = that match {
      case that: Rep => uniqueId === that.uniqueId || isPure && that.isPure && this =~= that  case _ => false }
    override def hashCode = uniqueId
    
    override def toString = prettyPrint(this)
  }
  private var repCount = 0
  
  /*
  // TODO can also wrap other Rep's?
  //class NamedSimpleRep(val origin: BoundVal, val under: SimpleRep) extends SimpleRep { // TODO make inner class
  case class NamedSimpleRep(origin: BoundVal, under: SimpleRep) extends SimpleRep { // TODO make inner class
    override val uniqueId = under.uniqueId
    val dfn = under.dfn
    
    
    override def inline = this
    
    override def renew = {
      NamedSimpleRep(origin, under.renew.normalize)
    }
    
    
    override def toString = s"$origin~$under"
  }
  */
  
  sealed trait SimpleRep extends Rep { rep =>
    
    //def withName(bv: BoundVal) = new NamedSimpleRep(bv, this)
    
    def scrapEffects: this.type = {
      this |> traverse {
        case RepDef(_: Abs) => false
        case `rep` => true
        case r => r.scrapEffects; true
      }
      if (!isPure) effect_!(this)
      this
    }
    //def toBlock: Block = Block(scrapEffects)
    def toBlock: Block = Block mkNoProcess (scrapEffects)
    
    def inline = this
    
    def renew = { // TODO rm?
      require(isImpure)
      anf.rep(dfn)
    }
    
    def isHole = dfn match { case Hole(_) | SplicedHole(_) => true  case _ => false }
    
  }
  
  object Block {
    //def apply(repFun: => Rep): Block = postProcess(mkNoProcess(repFun)) match {
    //  case b: Block => b
    //  case r => 
    //    println(r)
    //    ??? //wtf
    //  //case r => mkNoProcess(r)
    //}
    
    //def apply(repFun: => Rep): Block = {
    //  val b = mkNoProcess(repFun)
    //  println("PRE "+b)
    //  val b2 = postProcess(b)
    //  println("POST "+b2)
    //  b2 match {
    //    case b: Block => b
    //    case r =>
    //      println(r)
    //      ??? //wtf
    //    //case r => mkNoProcess(r)
    //  }
    //}
    def apply(repFun: => Rep): Block = {
      val b = new Block(repFun)
      //println("PRE "+b)
      if (b.uniqueId == 18)
        42
      val b2 = postProcess(b)
      //println("POST "+b2)
      b2 match {
        case b: Block => b
        case r =>
          println(r)
          ??? //wtf
        //case r => mkNoProcess(r)
      }
    }
    
    private[ANF] def mkNoProcess(repFun: => Rep): Block = new Block ({ // Tries not to create a different Block we get a Block with no effects as the result
      //repFun >>? { case b: Block =>
      val r = repFun
      r >>? { case b: Block =>
        if (currentBlock.effects isEmpty) return b
        else b
      }
    })
    
    //def apply(repFun: => Rep): Block = postProcess(new Block ({ // Tries not to create a different Block we get a Block with no effects as the result
    //  repFun >>? { case b: Block =>
    //    if (currentBlock.effects isEmpty) return b
    //    else b
    //    //else postProcess(b)
    //  }
    //})) match {
    //  case b: Block => b
    //  case r => 
    //    println("??? "+r)
    //    mkNoProcess(r)
    //}
    //private[ANF] def mkNoProcess(repFun: => Rep) = apply(repFun)
  }
  class Block private(repFun: => Rep) extends Rep {
    val effects = mutable.Buffer[SimpleRep]()
    
    val hasEffect: mutable.Set[Int] = mutable.Set[Int](
      scopes.headOption.iterator flatMap (_.hasEffect) toSeq: _*) // No need to register effects that are already registered in an outer scope
    
    val effectHoles = mutable.Set[Int]()
    val associatedBoundValues = mutable.Map[Int, BoundVal]()
    
    
    scopes push this
    val result: SimpleRep = try { // Note that the result of a Block may be composed of references to its effects wrapped in some pure computation
      ANFDebug.debug("{{ New block }}"); ANFDebug.nestDbg {
        /*
        val r = repFun match {
          case b: Block =>
            b.effects foreach effect_!
            //effect_!(b.result) // Should not be necessary, as all effects of the result should already be registered
            b.result
          case s: SimpleRep => s
        }
        */
        val r = repFun.normalize
        
        effects.filter_! { // Currently, we let-bind holes so effect holes retain the right eval order, and here remove the other ones
          case r if r isHole => effectHoles(r.uniqueId)
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
      currentBlock.effectHoles ++= effectHoles // if we're wrapping everything in blocks later normalized we'd better have that...
      currentBlock.associatedBoundValues ++= associatedBoundValues
      
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
  // TODO make it accept only SimpleRep:
  def effect_!(r: Rep): Unit = (ANFDebug.debug("! "+r)) before
    r match {
      //case b: Block => b.effects foreach effect_!
      case b: Block =>
        System.err.println("wtf "+b)
        ???
      //case RepDef(MethodApp(_, Var.Apply.Symbol | Var.Bang.Symbol, _, _, _)) => // TODO explain?
      //case RepDef(MethodApp(_, Var.Bang.Symbol, _, _, _)) => // TODO explain?
      case r: SimpleRep =>
        //if (r.uniqueId == 139)
        if (r.uniqueId == 2)
          42
        val sc = currentBlock
        if (r.isImpure && !sc.hasEffect(r.uniqueId)) {
          sc.effects += r
          sc.hasEffect += r.uniqueId
        }
    }
  
  
  /*
  // Make sure to wrap terms in a top-level Block:
  override def wrapConstruct(r: => Rep): Rep = Block(super.wrapConstruct(r)) // TODO also invert?
  //override def wrapExtract(r: => Rep): Rep = Block(super.wrapExtract(r)) // FIXME problem: the block may be rewritten...
  override def wrapExtract(r: => Rep): Rep = super.wrapExtract(Block(r)).toBlock
  */
  // Make sure to wrap terms in a top-level Block:
  override def wrapConstruct(r: => Rep): Rep = super.wrapConstruct(Block(r)).asInstanceOf[Block] // TODO .toBlock (not calling process) to ensure in block?
  override def wrapExtract(r: => Rep): Rep = super.wrapExtract(Block(r)).asInstanceOf[Block]
  
  
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
  
  // TODO handle extractor bindings to pure values!
  //def extractValBinding(param: BoundVal, bdoy: Block, arg: Rep, headRep: Rep, tailReps: Seq[Rep]): Option[Extract] = {
  def extractValBinding(param: BoundVal, body: Block, arg: Rep, headEffect: Rep, tailEffects: Seq[Rep], result: Rep): Option[Extract] = {
    val Upd = new ANFRepTransformer(identity, { // Note: doing this in bottomUp; topDown does not work with ANF.. why?
      case `headEffect` => hole(param.name, param.typ)
      case r => r
    })
    //var xtedbody: Rep = Block mkNoProcess {(headEffect +: tailReps.init) foreach effect_!; tailReps.last}
    var xtedbody: Rep = Block mkNoProcess {headEffect |> effect_!; tailEffects foreach effect_!; result}
    //println("XBody: "+xtedbody)
    xtedbody |>= Upd.apply
    //println("XBUpdated: "+xtedbody)
    var ex = mergeOpt(extract(arg, headEffect), extract(body, xtedbody))
    //if (param.isExtractedBinder) ex = ex flatMap (merge(_,  (Map(param.name -> headEffect),Map(),Map())  ))
    if (param.isExtractedBinder) ex = ex flatMap (merge(_,  (Map(param.name -> rep(Hole(param.name)(param.typ, Some(param)))),Map(),Map())  ))
    ex
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
    //println(s"EXTR $xtor  <<<  $xtee")
    val (xrep, rep, res) = xtor -> xtee match {
        
      //case NamedSimpleRep(or, un) -> _ =>
      //  val ex: Extract = if (or isExtractedBinder) (Map(or.name -> xtee.toBlock),Map(),Map()) else EmptyExtract
      //  (un, xtee, extract(un, xtee) flatMap (merge(ex, _)))
      //  
        
      case _ if xtor.uniqueId == xtee.uniqueId => (xtor, xtee, Some(EmptyExtract))
      case (xtor:Block) -> _ if xtor.result.uniqueId == xtee.uniqueId => (xtor, xtee, Some(EmptyExtract))
        
        /*
      //case RepDef(MethodApp(self, Apply.Symbol))
      //case Apply(f,a) -> _ =>
      case Apply(RepDef(f: Abs),a) -> (bl: Block) if bl.effects.nonEmpty /*&& !f.param.isExtractedBinder*/ => // TODO handle case empty effects  TODO handle extrBinder
        //println(f,a,xtee)
        //println(a,xtee)
        //???
        //(xtor, xtee, None)
        val (arg +: reps) = bl.reps
        
        //val xtedbody = Block mkNoProcess {reps.init foreach effect_!; reps.last}
        val Upd = new ANFRepTransformer(identity, { // Note: doing this in bottomUp; topDown does not work with ANF.. why?
          case `arg` => hole(f.param.name, f.param.typ)
          case r => r
        })
        var xtedbody: Rep = Block mkNoProcess {reps.init foreach effect_!; reps.last}
        //println("XBody: "+xtedbody)
        xtedbody |>= Upd.apply
        //println("XBUpdated: "+xtedbody)
        var ex = mergeOpt(extract(a, arg), extract(f.body, xtedbody))
        if (f.param.isExtractedBinder) ex = ex flatMap (merge(_,  (Map(f.param.name -> arg),Map(),Map())  ))
        (xtor, xtee, ex)
        */
      case Apply(RepDef(Abs(p,b)),a) -> (bl: Block) if bl.effects.nonEmpty => // TODO handle extrBinder
        //val reps = bl.reps
        //(xtor, xtee, extractValBinding(p,b,a, reps.head, reps.tail)) // TODO handle case empty effects
        (xtor, xtee, extractValBinding(p, b.asInstanceOf[Block], a, bl.effects.head, bl.effects.tail, bl.result)) // TODO handle case empty effects
        
        
        // TODO invest. it seems we're always xting a block now...
      //case RepDef(Typed(Hole(name), tp)) -> (s: SimpleRep) => // Wraps extracted Reps in Blocks
      case RepDef(Typed(Hole(name), tp)) -> s if !xtor.isBlock || xtor.asInstanceOf[Block].effects.isEmpty => // Wraps extracted Reps in Blocks
        //println("XXX "+tp+"  "+s.typ)
        ( xtor,  xtee,  extractType(tp, s.typ, Covariant) flatMap (e => merge((Map(name -> s.toBlock),Map(),Map()),e)) )
        // ^ FIXME does it break rep'd holes?
      case RepDef(SplicedHole(name)) -> (s: SimpleRep) => ??? // TODO
        
      case (_: SimpleRep) -> (_: SimpleRep) => (xtor, xtee, super.extract(xtor, xtee))
      case (_: Block) -> (_: SimpleRep) => (xtor, xtee, None)
        
      //case _ -> (_: SimpleRep) => (xtor, xtee, super.extract(xtor, xtee)) // TODO make sure only match pure stuff when Block extracts Simple?
      ////case (_: Block) -> (_: SimpleRep) => (xtor, xtee, super.extract(xtor, xtee))
        
      case (_b0: Block) -> (b1: Block) =>
        //(b0, b1, {
        (_b0, b1, {
          val b0 = Block mkNoProcess (_b0.normalize)
          
          debug("Matching blocks:")
          debug("\t"+b0)
          debug("\t"+b1)
          //debug("\t"+Block(b0.normalize))
          
          val reps0 = b0.reps
          val reps1 = b1.reps
          reps0 indexWhere (_ isHole) match {
            case -1 =>
              if (reps0.size != reps1.size) None oh_and debug("Blocks have different numbers of effects.")
              else mergeAll(reps0.iterator zip reps1.iterator map (extract _).tupled)
            case i =>
              reps0 lastIndexWhere (_ isHole) match {
                case `i` =>
                  if (reps1.size < reps0.size-1) None oh_and debug("Extracted block does not have enough effects.")
                  else {
                    val (pre, h +: post) = reps0 splitAt i
                    val preEx = pre.iterator zip reps1.iterator map (extract _).tupled
                    val postEx = post.reverseIterator zip reps1.reverseIterator map (extract _).tupled
                    val extractedSlice = reps1.slice(pre.size, reps1.size-post.size)
                    lazy val hExt: Extract = h match {
                      case RepDef(Hole(name)) =>
                        /*
                        val (effs :+ res) = extractedSlice
                        val r = if (effs isEmpty) res.toBlock else Block mkNoProcess {
                          effs foreach effect_!
                          res and effect_!
                        }
                        */
                        val r = extractedSlice match {
                          case effs :+ res =>
                            if (effs isEmpty) res.toBlock else Block mkNoProcess {
                              effs foreach effect_!
                              res and effect_!
                            }
                          case Seq() => const(())
                        }
                        (Map(name -> r),Map(),Map())
                      case RepDef(SplicedHole(name)) => (Map(),Map(),Map(name -> extractedSlice.map(_ toBlock)))
                      case _ => wtf
                    }
                    //mergeAll(preEx :+ hExt ++ postEx)
                    mergeAll(preEx ++ Iterator(Some(hExt)) ++ postEx)
                  }
                case _ =>
                  System.err.println("Extraction of blocks with several effect holes not yet supported.") // TODO proper reporting/logging
                  None
              }
          }
          
        }
        )
      case _ => wth(s"A SimpleRep extracting a Block: $xtor << $xtee") // naked rep extracts a Block?!
      //case _ => wth(s"A SimpleRep used as an extractor: ${xtor} << $xtee") // naked rep extracts?!
    }
    
    val newRes = xtee match { case b: Block => res map filterANFExtract  case _ => res }
    
    if (rep.isPure && xrep.isPure   // Q: can it really happen that one is pure and the other not?
      || xtor.dfn.isInstanceOf[Hole]||xtor.dfn.isInstanceOf[SplicedHole]
      || xtee.isBlock // if we extracted a Block, no need to rememeber its id (block normally only happen at top-level or RHS of lambdas)
    ) newRes
    else newRes flatMap (merge(_, (Map(
      "#$"+xrep.uniqueId -> rep,
      "##"+rep.uniqueId -> xrep   // TODO check not hole!!
    ),Map(),Map()) ))
  }
  
  override def extractRep(xtor: Rep, xtee: Rep): Option[Extract] = super.extractRep(xtor, xtee) map filterANFExtract
  
  /** TODO also handle vararg reps! */
  protected def filterANFExtract(maps: Extract) = (maps._1 filterKeys (k => !(k startsWith "#")), maps._2, maps._3)
  
  
  /** Note: could return a Rep and the caller could see if it was applied by watching eval of code... or not really (we may decide not to rewrite even if we call code) */
  override def rewriteRep(xtor: Rep, xtee: Rep, code: Extract => Option[Rep]): Option[Rep] = /*ANFDebug muteFor*/ {
    import scala.util.control.Breaks._
    if (!xtee.isInstanceOf[Block]) return None
    
    val xbl = xtor.asInstanceOf[Block]
    val bl = xtee.asInstanceOf[Block]
    val reps = bl.effects :+ bl.result
    
    //println("Rewriting Block: "+bl)
    
    // TODO also match the effects in xbl!!!
    
    xbl |>? {
      case Apply(RepDef(Abs(p,b)),a) =>
        //for (rs <- reps.tails; if rs.nonEmpty; ex <- extractValBinding(p, b, a, rs.head, rs.tail); res <- disableRewritingsFor( code(ex) )) {
        for (rs <- bl.effects.tails; if rs.nonEmpty; ex <- extractValBinding(p, b.asInstanceOf[Block], a, rs.head, rs.tail, bl.result); res <- disableRewritingsFor( code(ex) )) {
          //???
          val init = bl.effects.take(bl.effects.size-rs.size)
          //println("Init effects: "+init)
          return Some(Block {
            init foreach effect_!  // TODO update...
            res.normalize
            //bl.result
          })
        }
    }
    for (r <- reps; ex <- extract(xbl.result, r); res <- disableRewritingsFor( code(ex) )) breakable {
      /* ^ We disable online rewritings from happening while constructed the RHS of a rewriting, otherwise it may eat effects
       * that it sees as local but that will actually be inlined in a bigger scope where they may also be used.
       * For example, {#X = readInt.toDouble; pow(#X,2)} may be transformed to {#X = readInt.toDouble; {#X = readInt.toDouble; #X * #X} }
       * and {#X = readInt.toDouble; {#Y = readDouble; s * s} } and {#Z = readDouble; #Y = readDouble; #Y * #Y } -- notice the wrong duplication */
      
      ANFDebug debug(s"Rewriting $r -> $res")
      
      val cache = mutable.Map(ex._1 collect { case name -> rep if name startsWith "#$" => name.tail.tail.toInt -> rep } toSeq: _*)
      //val removed = cache.keySet.toSet
      cache += r.uniqueId -> res
      
      /*
      //println(removed map cache)
      println(cache.valuesIterator.toSet)
      //removed -- reps.iterator.map(_ uniqueId) foreach { r =>
      cache.valuesIterator.map(_ uniqueId).toSet -- reps.iterator.map(_ uniqueId) foreach { r =>
        //ANFDebug debug
        println(s"Rewrite aborted: $r is not a local effect")
        return None
      }
      */
      
      val removed = ex._1 collect { case name -> rep if name startsWith "##" => name.tail.tail.toInt } toSet;
      //println(removed, reps.iterator.map(_ uniqueId).toSet)
      //println("CACHE "+cache)
      removed -- reps.iterator.map(_ uniqueId) foreach { r =>
        //ANFDebug debug
        /*println*/(s"Rewrite aborted: $r is not a local effect")
        //return None
        break
      }
      
      // Updates references to rewritten Reps:
      //val Updater = new RepTransformer({ r => cache get r.uniqueId map ( // simple RepTransformer won't transform Blocks!!
      val Updater = new ANFRepTransformer({ r => cache get r.uniqueId map (
        _.inline and { r2 => if (r2 isImpure) ANFDebug debug s"UPDATE $r -> $r2" }) getOrElse r }, identity)
      
      //return Some(Block {
      return Some(Block mkNoProcess {
        //val newReps = for (e <- reps if !cache.isDefinedAt(e.uniqueId) || e.uniqueId == r.uniqueId) yield {
        val newReps = for (e <- reps if !removed(e.uniqueId) || e.uniqueId == r.uniqueId) yield {
          //println("Effect "+e)
          val e2 = e |> Updater.apply
          //val e2 = ANFDebug debugFor (e |> Updater.apply)
          //println("Updated "+e2)
          e2 |> traversePartial {
            case r if removed(r.uniqueId) =>
              // A rewrite rule may want to remove an effect still used somewhere, which it should not be able to do
              ANFDebug debug
                /*println*/(s"Rewrite aborted: $r is still used")
              //return None // FIXME don't return
              break
          }
          cache += e.uniqueId -> e2
          e2.normalize
          /*
          e2 |> effect_! // not necessary aymore (cf normalize ^)
          e2
          */
        }
        newReps.last
      }) and { b =>
        //println("RWB "+b)
      }
    }
    
    None
  }
  
  
  //override def hole(name: String, typ: TypeRep): Rep = super.hole(name, typ) and effect_!
  //override def splicedHole(name: String, typ: TypeRep): Rep = super.splicedHole(name, typ) and effect_!
  override def hole(name: String, typ: TypeRep): Rep = super.hole(name, typ).normalize
  override def splicedHole(name: String, typ: TypeRep): Rep = super.splicedHole(name, typ).normalize
  
  
  
  override def transformRep(r: Rep)(pre: Rep => Rep, post: Rep => Rep = identity): Rep =
    ANFDebug.debug(Console.BOLD+"--- NEW TRANSFO ---"+Console.RESET) before
    (new ANFRepTransformer(pre,post))(r)
  
  class ANFRepTransformer(pre: Rep => Rep, post: Rep => Rep) extends super.RepTransformer(pre, post) {
    val cache = mutable.Map[Int, Rep]()
    
    //val Updater = new RepTransformer({ r => cache get r.uniqueId map (
    lazy val Updater = new ANFRepTransformer({ r => cache get r.uniqueId map ( // SOF
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
          //case b: Block => val newB = Block {
            
          //case NamedSimpleRep(org, und) =>
          //  //val rec = apply(und).normalize
          //  //val rec = apply(und).asInstanceOf[SimpleRep]
          //  ???
          //  apply(und) match {
          //    case s: SimpleRep => 
          //      val ret = NamedSimpleRep(org, s)
          //      println(cache get r2.uniqueId)
          //      cache += (r2.uniqueId -> ret) // rebinds
          //      ret
          //    case r => r
          //  }
            
          case b: Block => val newB = Block mkNoProcess {
    //val Updater = new ANFRepTransformer({ r => cache get r.uniqueId map ( // SOF
    //  _ and (r2 => if (r2 isImpure) ANFDebug debug s"Updating $r -> $r2")) getOrElse r }, identity)
            // ^ when defined here the Updater doesn't do its job fully
            
            b.effects foreach { e =>
              deb("INIT "+e)
              if (e.uniqueId == 141)
                42
              val e2 = ANFDebug nestDbg Updater(e)
              deb("NEW "+e2)
              //val e3 = apply(e2)
              val e3 = apply(e2).normalize
              deb("LAST "+e3)
              cache += e.uniqueId -> e3 // we might already have a binding e2->e3, but we also need e->e3!
              //effect_!(e3) // not necessary aymore (cf normalize ^)
            }
            
            var newRes = b.result // TODO factor logic w/ above..?
            deb("RET-INIT "+newRes)
            newRes = ANFDebug nestDbg Updater(newRes).inline // `inline` does nothing if it's already a SimpleRep
            deb("RET-NEW "+newRes)
            //newRes = ANFDebug nestDbg apply(newRes).inline
            newRes = ANFDebug nestDbg apply(newRes).normalize
            deb("RET-LAST "+newRes)
            cache += b.result.uniqueId -> newRes
            //effect_!(newRes)
            
            // TODO? also transfer effect holes?!
            //currentBlock.associatedBoundValues ++= b.associatedBoundValues
            //currentBlock.associatedBoundValues ++= (b.associatedBoundValues map {
            //  //case id => cache get id getOrElse()
            //  _ >>? (cache andThen (_ uniqueId))
            //})
            currentBlock.associatedBoundValues ++= (b.associatedBoundValues map { case k -> v => (k >>? (cache andThen (_ uniqueId))) -> v })
            
            newRes
          }
            if (newB.effects.size == b.effects.size && (newB.effects zip b.effects forall {case(a,b)=>a eq b}) && (newB.result eq b.result)) b
            else newB
          case bv: BoundValRep => bv |> pre |> post  // BoundVal is not viewed as a Rep in AST, so AST never does that
          case _ =>
            val d = dfn(r2)
            val newD = apply(d) 
            //if (newD eq d) r else rep(newD)
            if (newD eq d) r2 else rep(newD)
            
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
    ANFDebug debug
      /*println*/(s"Inlining $arg as $param in $body")
    
    arg |> effect_!
    //assert(arg.isPure || (currentBlock hasEffect arg.uniqueId)) // TODO enable this and properly fix where it crashes
    
    val res = body match {
      case b: Block =>
        b.associatedBoundValues += arg.uniqueId -> param
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
  
  /*
  override def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep = {
    
    // TODO investigate -- weird: when the mtd is Imperative, we receive blocks here
    //assert(!self.isBlock && !(argss exists (_.reps exists (_ isBlock))), "WTF") // TODO BE
    
    val pure = mtd == Var.Apply.Symbol || mtd == Var.Bang.Symbol
    if (mtd == Imperative.Symbol) { // TODO rm: useless since effects should have already been propagated -- and if not, the order isn't going to be right...
      argss match {
        case ArgsVarargs(Args(), effs)::Args(res)::Nil =>
          effs.reps foreach effect_!
          effs.reps foreach {
            case r: SimpleRep if r isHole => currentBlock.effectHoles += r.uniqueId
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
  */
  
  
  
  
  //protected def ensureEnclosingScope(r: => SimpleRep) = if (scopes isEmpty) Block(r) else r
  protected def ensureEnclosingScope(r: => Rep) = if (scopes isEmpty) Block(r) else r
  
  protected def processSubs(originalR: Rep, newR: => Rep) = {
    // TODO? if was a block, make a block, otherwise inline/scrap
    
    //ensureEnclosingScope {
    //  val r = rFun
    //  //if (originalR.isBlock) 
    //  r.inline
    //}
    
    //ensureEnclosingScope(newR.scrapEffects)
    
    newR
    //newR.normalize
    //Block(newR.normalize)
    //Block(newR.scrapEffects)
  } //and (r => println("Subs: "+r))
  override def substitute(r: Rep, defs: Map[String, Rep]): Rep = processSubs(r, super.substitute(r, defs))
  override def substituteLazy(r: Rep, defs: Map[String, () => Rep]): Rep = processSubs(r, super.substituteLazy(r, defs))
  
  
}
