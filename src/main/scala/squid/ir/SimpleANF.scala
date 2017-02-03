package squid
package ir

import utils._
import collection.mutable

/**
  * Simplest encoding of ANF, using plain AST nodes and online renormalization via the `rep` function.
  * Also features an appropriate `rewriteRep` impl that understands ANF blocks;
  *   an efficient repr of the ANF block associated with an AST node is stored lazily in the Rep wrapper
  * 
  * TODO Needs to commute let bindings
  *   > take care that we're not binding the same BV several times! need to renew
  * 
  * TODO adapt extract/rewrite to ignore pure let-bound defs
  *   match in the middle of a "block": {{{ ir"readInt.toDouble.toInt" rewrite { case ir"readInt.toDouble" => ir"readDouble" } }}}
  *     need to make sure removed symbols are not used!
  *   blur distinction effects/bound-vals -- or make unused bound vals effects automatically
  * 
  * TODO CSE with a WeakHashMap[Set[Val],Rep] for pure exprs
  * 
  * TODO use a specialized Def for Imperative, to avoid overhead associated with the complex structure of the virtualized one
  * 
  * Q: should the return of a block be a trivial expr?!
  * 
  * Implem. Notes
  *   if we make holes not _.isTrivial, we get a SOF in the pretty-printing (which replaces bound vals by holes)
  * 
  */
class SimpleANF extends AST with CurryEncoding { anf =>
  
  object ANFDebug extends PublicTraceDebug
  
  
  //type Block = List[Either[Val -> Rep, Rep]] -> Def
  type Block = List[Either[Val -> Rep, Rep]] -> Rep
  
  case class Rep(dfn: Def) { // FIXME status of lambdas? (and their curry encoding)
    //def isTrivial = dfn.isTrivial
    lazy val isTrivial: Bool = dfn match {
      case m: MethodApp => //println(m.sym.fullName)
        (m.sym.fullName startsWith "squid.lib.uncurried") || (m.sym.fullName == "scala.Any.asInstanceOf")
        // Note: should NOT make "squid.lib.Var.apply" trivial since it has to be let-bound for code-gen to work
      case Module(pre, _, _) => pre.isTrivial
      case Ascribe(x, _) => x.isTrivial
      case _ => true
    }
    
    def isPure = isTrivial && !isHole // TODO refine
    //def isHole = dfn.isInstanceOf[Hole] || dfn.isInstanceOf[SplicedHole]
    lazy val isHole: Bool = dfn match {
      case Hole(_) | SplicedHole(_) => true
      case Ascribe(x, _) => x.isHole
      case _ => false
    }
    final def isImpure = !isPure
    
    lazy val asBlock: Block = dfn match {
      case LetIn(p,v,b) => b.asBlock >> { case es -> r => (Left(p -> v) :: es) -> r }
      case Imperative(effs,ret) => ret.asBlock >> { case es -> r => ((effs map (e => Right(e)) toList) ++ es) -> r }
      //case MethodApp(s,m,ts,ass,t) => 
      case _ => Nil -> this
    }
    
    override def toString = s"$dfn"
  }
  
  
  /** In ANF, we keep only one effect per Imperative call to make their inductive traversal more natural.
    * Important for matching sequences of effects. */
  def mkImperative(effects: Seq[Rep], res: Rep): Rep = {
    effects.foldRight(res)((eff,acc) => Imperative(eff::Nil,acc) |> Rep) // Q: why not `rep`?
  }
  /*
  
  TODO(?) remove pure effects from Imperative
  
  */
  /** Normalizes a Def and wraps it in a Rep */
  def rep(dfn: Def) = {
    
    val isImperativeCall = dfn |>? { case Imperative(_, _) => true } Else false
    
    val statements = mutable.Buffer[Either[Val -> Rep, mutable.Buffer[Rep]]]()
    
    def makeStmtIfNecessary(letBound: Bool)(r: Rep): Rep = /*println(s"mkStmt $r") before*/ r match {
      case _ if r.isTrivial && (!r.isHole || !isImperativeCall) => r
      case LetIn(p, v, b) =>
        // TODO check not already bound in body
        statements += Left(p -> v)
        b |> makeStmtIfNecessary(false)
      case Imperative(effs, ret) =>
        val buf = statements.lastOption match {
          case Some(Right(b)) => b
          case _ => mutable.Buffer[Rep]() and (b => statements += Right(b))
        }
        buf ++= effs
        ret |> makeStmtIfNecessary(false)
      case _ if letBound => r
      //case _ if letBound || isImperativeCall => r
      case _ if isImperativeCall =>
        //println(r,statements)
        val buf = statements.lastOption match {
          case Some(Right(b)) => b
          case _ => mutable.Buffer[Rep]() and (b => statements += Right(b))
        }
        if (r isImpure) buf += r
        r
      case _ =>
        //println(s"Binding $r")
        readVal(freshBoundVal(r.typ) and { v => statements += Left(v -> r) })
    }
    
    def construct(init: Rep) = statements.foldRight(init) {
      case (Left(v -> r), body) => 
        MethodApp(lambda(v::Nil, body), Function1ApplySymbol, Nil, Args(r)::Nil, dfn.typ) |> rep
      case (Right(b), body) => if (b isEmpty) body 
        //else Imperative(b, body) |> Rep  // don't construct multi-effect Imperative's
        else mkImperative(b, body)
    }
    
    val normal = dfn match {
      //case Apply(f,a) => Apply(f,a,dfn.typ) |> Rep
      case LetIn(p, v, b) if v.isPure && !hasHoles(b) =>
        //println(s"Inl $p as $v in $b")
        inline(p, b, v)
        
      case Imperative(effs, ret) =>
        //effs foreach { e => e |> makeStmtIfNecessary(false) and (r => statements += Right(r)) }
        effs foreach makeStmtIfNecessary(false)
        //construct(ret |> makeStmtIfNecessary(false))
        val newRet = ret >>? {
          case Imperative(effs2, ret2) => 
            effs2 foreach makeStmtIfNecessary(false)
            ret2
        }
        construct(newRet)
        
      case MethodApp(s,m,ts,ass,t) =>
        //dfn |>? { case LetIn(p, v, b) => println(p,v,b) }
        val isLetBinding = dfn |>? { case LetIn(_, _, _) => true } Else false
        val newSelf = makeStmtIfNecessary(false)(s)
        val newArgss = ass.map(_.map(anf)(makeStmtIfNecessary(isLetBinding)))
          // ^ If we're handling a let binding, there is no need to let-bind the argument (bound value)
          // We still need to call `makeStmtIfNecessary` just in case what's bound is an imperative block or another let binding!
        val init = {
          //MethodApp(newSelf,m,ts,newArgss,t) |> (if (statements nonEmpty) rep else Rep) |> makeStmtIfNecessary(true) // SOF
          (MethodApp(newSelf,m,ts,newArgss,t) |> (if (statements nonEmpty) rep else Rep)) >>? {
            case Imperative(es,r) =>
              val buf = statements.lastOption match { // TODO factor
                case Some(Right(b)) => b
                case _ => mutable.Buffer[Rep]() and (b => statements += Right(b))
              }
              buf ++= es
              r
          }
        } 
        construct(init)
      case _ =>
        //assert(dfn isTrivial)
        Rep(dfn)
    }
    
    // Note: no need to renormalize _after_ postProcess, since what postProcess returns should be normal.
    postProcess(normal)
  }
  override def simpleRep(dfn: Def): Rep = Rep(dfn)
  
  def dfn(r: Rep): Def = r.dfn
  
  def repType(r: Rep): TypeRep = r.dfn.typ
  
  override protected def freshNameImpl(n: Int) = "$"+n
  //override protected def freshNameImpl(n: Int) = "$"
  
  
  
  
  protected def boundVals(r: Rep): Set[Val] = ???
  
  /** From the ANF Block repr to corresponding AST node 
    * Note: could optimize: build a Rep with the ANF built in to avoid having it recomputed later */
  protected def constructBlock(b: Block) = {
    val (es, r) = b
    es.foldRight(r) {
      case Left(p -> v) -> b => letin(p, v, b, r.typ)
      case Right(r) -> Imperative(es, ret) => Imperative(r :: es.toList, ret) |> rep
      case Right(r) -> b => Imperative(r::Nil, b) |> rep
    }
  }
  
  /* TODO flexible spliced holes
   * TODO consecutive mutable statement matching like in PardisIR */
  /** Rewrites a Rep by applying a rewriting on the corresponding ANF block;
    * aborting the rewriting if it removes Vals still used later on. */
  override def rewriteRep(xtor: Rep, xtee: Rep, code: Extract => Option[Rep]): Option[Rep] = /*ANFDebug muteFor*/ {
    import Console.{BOLD, RESET}
    debug(s"${BOLD}Rewriting$RESET $xtee ${BOLD}with$RESET $xtor")
    //nestDbg(extract(xtor, xtee)) and (res => debug(s"${BOLD}Result:$RESET $res"))
    
    // When extracting the body of a lambda or let binding, the bound Val is replaced with a Hole that remembers this original Val (in field `originalSymbol`)
    // Here we aggregate all Val references originally present instead of the extraction holes
    def originalVals(r: Rep) = {
      val res = mutable.Set[Val]()
      r |> traversePartial {
        case RepDef(h: Hole) => h.originalSymbol foreach (res += _); false
        //case h: SplicedHole => h.originalSymbol
      }
      res
    }
    
    /** Backtracking rewriting block matcher
      * @param ex: current extraction artifact
      * @param matchedVals: which bound Vals have been traversed and replaced by holes so far
      * @param xy: the current (extractor -> extracted) pair */
    def rec(ex: Extract, matchedVals: List[Val])(xy: Block -> Block): Option[Rep] = {
      
      def mkCode(ex: Extract): Option[Rep] = {
        debug(s"${Console.BOLD}Constructing code with $ex${Console.RESET}")
        val r = try code(ex) catch {
          case e: Throwable =>
            debug(s"${Console.RED}Construction did not complete.${Console.RESET} ($e)")
            throw e
        }
        debug(s"${Console.GREEN}Constructed: ${r map showRep}${Console.RESET}")
        r
      }
      
      xy match {
    
        // Matching simple expressions (block returns)
        case (Nil -> r0, Nil -> r1) =>
          for {
            e <- extract(r0, r1)
            m <- merge(e, ex)
            c <- mkCode(m)
          } yield c
    
        // Matching two effects
        case ((Right(e0) :: es0) -> r0, (Right(e1) :: es1) -> r1) =>
          extract(e0, e1) flatMap (rec(_, matchedVals)(es0 -> r0, es1 -> r1))
    
        // Matching an effect with a let binding (eg: {readInt; 42} with {val r = readInt; $body: Int})
        case ((Left(b0 -> v0) :: es0) -> r0, (Left(b1 -> v1) :: es1) -> r1) =>
          for {
            e <- extract(v0, v1)
            (hExtr,h) = b1.toHole(b0)
            e <- merge(e, ex)
            e <- merge(e, hExtr)
            hr = rep(h)
            //() = debug(s"Extracting binder $b1: $hr")
            es1i = es1 map {
              case Right(r) => Right(inline(b1, r, hr))
              case Left(b -> v) => Left(b -> inline(b1, v, hr))
            }
            r1i = inline(b1, r1, hr)
            r <- rec(e, b1 :: matchedVals)(es0 -> r0, es1i -> r1i)
          } yield r
    
        // Matching a whole block with a hole
        case (Nil -> r, bl) if r.isHole =>
          // TODO specially handle SplicedHole?
          // TODO also try eating less code? (backtracking)
          extract(r, bl |> constructBlock) flatMap (merge(_, ex)) flatMap mkCode
          
        // Matching an arbitrary statement of a block with the last expression of an xtor
        // -- rewriting can happen in the middle of a Block, and then we have to ensure later terms do not reference removed bindings
        case (Nil -> r0, (stmt :: es) -> r1) =>
          val (rep, rebuild) = stmt match {
            case Left(b -> v) => v -> ((x:Rep) => Left(b -> x))
            case Right(r) => r -> ((x:Rep) => Right(x))
          }
          for {
            e <- extract(r0, rep)
            e <- merge(e, ex)
            c <- try mkCode(e) catch {
              case EarlyReturnExc(rs,f) =>
                throw EarlyReturnExc(rs, f andThen (r => constructBlock(rebuild(r)::es, r1) ))
            }
            r = constructBlock((rebuild(c) :: es) -> r1)
            if !(originalVals(r) exists matchedVals.toSet) // abort if one of the Vals matched so far is still used in the result of the rewriting
          } yield r
          
        case _ => None
    
      }
    }
    
    if (xtor.asBlock._1.isEmpty && xtee.asBlock._1.nonEmpty) return None
    // ^ TODO handle `{val r = expr; r}` as well as `expr`
    
    rec(repExtract(SCRUTINEE_KEY -> xtee),Nil)(xtor.asBlock -> xtee.asBlock) and_?
      { case Some(r) => debug(s"${BOLD}=> Rewrote$RESET $r") }
    
  }
  
  
  
  
  
  
  
  
  
  
  
  // Currently, the purpose of the following is only to build nice flat Scala trees from nested imperatives...
  // may be simpler to just add a switch to the super.ReinterpreterToScala impl 
  
  import squid.quasi.MetaBases
  import utils.meta.{RuntimeUniverseHelpers => ruh}
  import ruh.sru
  
  override def scalaTreeIn(MBM: MetaBases)(SRB: MBM.ScalaReflectionBase, rep: Rep, ExtrudedHandle: (BoundVal => MBM.u.Tree)): MBM.u.Tree = ANFDebug.muteFor { muteFor {
    new ReinterpreterToScala {
      val MetaBases: MBM.type = MBM
      val newBase: SRB.type = SRB
      override val extrudedHandle = ExtrudedHandle
    } apply rep
  }}
  
  abstract class ReinterpreterToScala extends super.ReinterpreterToScala {
    val MetaBases: MetaBases
    import MetaBases.u._
    val newBase: MetaBases.ScalaReflectionBase
    
    //val repCache = mutable.Map[Int, newBase.Rep]()
    
    override def apply(d: Def) = d match {
      case Imperative(es,r) => q"..${es map apply}; ..${r |> apply}"
      case _ => super.apply(d)
    }
  }
  
}




















