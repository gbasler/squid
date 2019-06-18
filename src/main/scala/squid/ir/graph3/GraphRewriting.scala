// Copyright 2019 EPFL DATA Lab (data.epfl.ch)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package squid.ir
package graph3

import squid.ir.graph.CallId
import squid.utils._
import squid.utils.CollectionUtils.MutSetHelper
import squid.utils.CollectionUtils.IteratorHelper
import squid.utils.meta.{RuntimeUniverseHelpers => ruh}

import scala.collection.mutable
import scala.collection.immutable.ListSet

/*

Note:
  Under an Arg, we really can't make any assumptions, because arbitrary context may have been restored!
      ...that is, unless we actually saw the corresponding Call on the way...
  The best we can do is push the arg down...

TODO:
  Use analysis before doing the rewrites, to prune possible paths to explore

Note:
  It used to be unsound to match something in the body of a lambda, if that entailed wrapping it in control-flow
  As of [this commit], the problem should be fixed, by making substituteVal smarter and storing 'abs' in MirrorVal

*/
trait GraphRewriting extends AST { graph: Graph =>
  
  object RewriteDebug extends PublicTraceDebug
  import RewriteDebug.{debug=>Rdebug}
  
  /*
  override def extractVal(r: Rep): Option[Val] = r.boundTo |>? {
    case ConcreteNode(MirrorVal(v)) => v
  }
  */
  override def extractVal(r: Rep): Option[Val] = r.node |>? {
    case ConcreteNode(v:Val) => v
  }
  
  type XCtx = GXCtx
  def newXCtx: XCtx = GXCtx.empty
  
  type Condition = (Control,CallId)
  
  // TODO make assumed/assumedNot as single assumptions ListSet?
  protected case class GXCtx(assumed: Set[Condition], assumedNot: Set[Condition], curCtrl: Control, valMap: Map[Val,Val], traverseBranches: Bool = true) {
    assert(!(assumed intersects assumedNot), s"${assumed} >< ${assumedNot}")
  }
  protected object GXCtx { def empty = GXCtx(Set.empty, Set.empty, Id, Map.empty) }
  
  //override def mapDef(f: Def => Def)(r: Rep): r.type = ???
  //override protected def mapRep(rec: Rep => Rep)(d: Def) = ???
  /*
  protected val transformed = mutable.Set.empty[(Rep,ListSet[Condition->Bool],List[Rep])]
  def alreadyTransformedBy(xtor: Rep, ge: GraphExtract): Bool = transformed contains ((xtor, ge.assumptions, ge.traversedReps))
  def rememberTransformedBy(xtor: Rep, ge: GraphExtract): Unit = transformed += ((xtor, ge.assumptions, ge.traversedReps))
  */
  protected val transformed = mutable.Set.empty[(Rep,/*ListSet[Condition->Bool],*/CCtx,List[Rep])]
  def alreadyTransformedBy(xtor: Rep, ge: GraphExtract)(implicit cctx: CCtx): Bool = transformed contains ((xtor, /*ge.assumptions,*/cctx, ge.traversedReps))
  def rememberTransformedBy(xtor: Rep, ge: GraphExtract)(implicit cctx: CCtx): Unit = transformed += ((xtor, /*ge.assumptions,*/cctx, ge.traversedReps))
  
  override def spliceExtract(xtor: Rep, args: Args)(implicit ctx: XCtx) = ??? // TODO
  override def extract(xtor: Rep, xtee: Rep)(implicit ctx: XCtx) =
    extractGraph(xtor,xtee)(GXCtx.empty.copy(traverseBranches = false), CCtx.empty).headOption map (_.extr)
  
  override def rewriteRep(xtor: Rep, xtee: Rep, code: Extract => Option[Rep]): Option[Rep] = rewriteRepCtx(xtor, xtee, code)(CCtx.empty)
  def rewriteRepCtx(xtor: Rep, xtee: Rep, code: Extract => Option[Rep])(implicit cctx: CCtx): Option[Rep] = {
    //println(s"rewriteRep $xtee  >>  $xtor")
    //println(s"already: ${transformed.map(r => s"\n\t${r._1.bound}, ${r._2}, ${r._3.map(_.bound)}")}")
    
    val matches = extractGraph(xtor, xtee)(GXCtx.empty,cctx) flatMap
      (_ merge (GraphExtract fromExtract repExtract(SCRUTINEE_KEY -> xtee)))
    
    //if (matches.nonEmpty) println(matches.size,matches.filterNot(alreadyTransformedBy(xtor,_)).size)
    
    if (matches.nonEmpty) println(s"Matches for ${xtor}:"+
      matches.map(ge => "\n"+(if (alreadyTransformedBy(xtor,ge)) "√ " else "✗ ")+ge+s"\n\t${ge.traversedReps.map(_.simpleString)} ${
        ge.assumptions}").mkString)
    
    //matches.iterator.flatMap { ge =>
    matches.filterNot(alreadyTransformedBy(xtor,_)).iterator.flatMap { ge =>
      //println(s"...considering $xtor << ${ge.traversedReps.map(_.simpleString)} --> ${ge.extr}")
      code(ge.extr) |>? {
        case Some(x0) =>
          ///*
          println(s"rewriteRep $xtee  >>  $xtor")
          //println(s"...transforming ${xtor.simpleString} -> $x0")
          //println(s"...transforming ${xtor.simpleString} -> ${x0.showGraph}")
          println(s"...transforming ${xtee.simpleString} -> ${x0.showGraph}")
          println(s"...  ${ge.assumptions}")
          println(s"...  ${xtor.simpleString} :: ${ge.traversedReps.map(_.bound)}")
          //*/
          rememberTransformedBy(xtor,ge)
          //println(f: ${showEdges}")
          if (ge.assumptions.isEmpty) x0 else {
            val oldXtee = xtee.node.assertNotVal.mkRep // alternatively, could do the rebind here?
            ge.assumptions.toList.reverse.foldRight(x0) {
              case (ctrl,cid)->true -> x => Branch(ctrl,cid,x,oldXtee).mkRep
              case (ctrl,cid)->false -> x => Branch(ctrl,cid,oldXtee,x).mkRep
            }
          }
      }
    }.headOption
    
  }
  
  // Q: do we need something like the old 'callsToAvoid'?
  protected case class GraphExtract(extr: Extract, traversedReps: List[Rep], assumptions: ListSet[Condition->Bool]) {
    def merge(that: GraphExtract): Option[GraphExtract] =
      //if ((argsToRebuild intersects that.callsToAvoid) || (that.argsToRebuild intersects callsToAvoid)) Stream.Empty
      //else 
      
      //graph.merge(extr,that.extr).map(e =>
      //  GraphExtract(e, traversedReps ++ that.traversedReps, assumptions ++ that.assumptions))
      graph.merge(extr,that.extr).flatMap(e =>
        GraphExtract(e, traversedReps ++ that.traversedReps, assumptions ++ that.assumptions) optionUnless
          assumptions.exists{case(c,b) => that.assumptions.contains((c,!b))}
      )
    def assuming(a: Condition) = copy(assumptions = assumptions + (a->true))
    def assumingNot(a: Condition) = copy(assumptions = assumptions + (a->false))
    def matching (r: Rep) = r.node match {
      case _: Box | _: Branch => this
      case _ => copy(traversedReps = r :: traversedReps)
    }
    override def toString = s"{${assumptions.mkString(",")}}\\\t${extr._1.getOrElse(SCRUTINEE_KEY,"")} ${
      (extr._1-SCRUTINEE_KEY).map(r => "\n\t "+r._1+" -> "+r._2).mkString}"
  }
  protected object GraphExtract {
    val empty: GraphExtract = GraphExtract(EmptyExtract, Nil, ListSet.empty)
    def fromExtract(e: Extract): GraphExtract = empty copy (extr = e)
  }
  
  // TODO generalize to handle rewriting of incomplete graphs... (it will currently crash on them)
  // FIXME: make this traverse var-to-var bindings??! does it not already?
  protected def extractGraph(xtor: Rep, xtee: Rep)(implicit ctx: XCtx, cctx: CCtx): List[GraphExtract] = debug(s"Extract ${xtor} << $xtee") thenReturn nestDbg {
    import GraphExtract.fromExtract
    
    xtor -> xtee |> {
        
      case Rep(ConcreteNode(Hole(name))) -> _ => for {
        typE <- xtor.typ.extract(xtee.typ, Covariant).toList
        r1 = xtee
        
        //() = println(s">>>  $r2  =/=  ${try removeArgs(ctx.assumedNotCalled)(r1) catch { case e => e}}")
        
        //r2 = ctx.curCtrl.foldLeft(r1) {
        //  case r -> (k -> cid) => Box(cid,r,k).mkRep
        //}
        r2 = Box.rep(ctx.curCtrl,r1)
        
        e <- merge(typE, repExtract(name -> r2))
      } yield GraphExtract fromExtract e
        ///*
      //case _ -> Rep(Call(cid, res)) =>
      //case _ -> Rep(Arg(cid, res)) =>
      case _ -> Rep(Box(ctrl, res)) =>
        //println("BOX ",cid, res, k)
        //extractGraph(xtor, res)(ctx.copy(curCtrl = ctrl `;` ctx.curCtrl),withCtrl_?(ctrl).getOrElse(???)) // TODO upd cctx
        extractGraph(xtor, res)(ctx.copy(curCtrl = ctx.curCtrl `;` ctrl),withCtrl_?(ctrl).getOrElse(???)) // TODO upd cctx
        
      case _ -> Rep(Branch(ctrl, cid, thn, els)) => // FIXME should't we add ctx.curCtrl to the branch's condition here?!
        
        //println("BRANCH ",cond, thn, els, cctx)
        //val newCond = Condition(ctx.curCtrl ++ cond.ops, cond.cid)
        //hasCond_?(cond) match {
        mayHaveCid(ctrl,cid) match {
          case Some(true) => extractGraph(xtor, thn)(ctx.copy(assumed = ctx.assumed + (ctrl->cid)),cctx).map(_ assuming (ctrl->cid))
            
          //case Some(false) =>
          case _ => // !!! FIXME here we assume the rewriting is with full context 
            extractGraph(xtor, els)(ctx.copy(assumedNot = ctx.assumedNot + (ctrl->cid)),cctx).map(_ assumingNot (ctrl->cid))
          //case None =>
          //  lastWords("I think this is never used; but could be useful if we want to rewrite incomplete graph fragments?")
            
            /*
            if (newCond.isAlwaysTrue) extractGraph(xtor, thn)
            else if (newCond.isAlwaysFalse) extractGraph(xtor, els)
              // ^ Q: sound?
            else (if (newCond in ctx.assumedNot) Nil else extractGraph(xtor, thn)(ctx.copy(assumed = ctx.assumed + newCond),cctx).map(_ assuming newCond)) ++
              (if (newCond in ctx.assumed) Nil else extractGraph(xtor, els)(ctx.copy(assumedNot = ctx.assumedNot + newCond),cctx).map(_ assumingNot newCond))
            */
        }
        //*/
      case Rep(ConcreteNode(dxtor)) -> Rep(ConcreteNode(dxtee)) => dxtor -> dxtee match {
          
        case (_, Ascribe(v,tp)) => extractGraph(xtor,v)
          
        case (Ascribe(v,tp), _) =>
          for { a <- tp.extract(xtee.typ, Covariant).toList
                b <- extractGraph(v, xtee)
                m <- fromExtract(a) merge b } yield m
          
        case (h:HOPHole, _) => ??? // TODO
          
        ////case (_, MirrorVal(_)) => die // TODO?
        //case (_, MirrorVal(v)) => GraphExtract.empty optionIf (dxtor === v) toList
          
        case (_, Hole(_)) => die
          
        case VirtConst(vxtor) -> Constant(_) =>  // Note: if this does not match, we may still match an explicit usage of the const function...
          extractGraph(vxtor, xtee)
            //.map(ge => ge.copy(traversedReps = ge.traversedReps.tail)) // just to avoid duplicating the traversed rep in traversedReps
          
        case (Constant(v1), Constant(v2)) =>
          mergeOpt(extractType(xtor.typ, xtee.typ, Covariant), if (v1 == v2) Some(EmptyExtract) else None).map(fromExtract).toList
          
        case (a:ConstantLike) -> (b:ConstantLike) if a.value === b.value => GraphExtract.empty :: Nil
          
        //case (Hole(name), _) => // moved
          
        case (v1: BoundVal, v2: BoundVal) =>  // TODO implement other schemes for matching variables... cf. extractImpl
          // Q: check same type?
          if (
             v1 == v2 // Q: really legit?
          || ctx.valMap.get(v1).contains(v2)
          ) EmptyExtract |> fromExtract |> Nil.:: else Nil
          
        // TODO handle by-name
          
        case (a1: Abs, a2: Abs) =>
          require(a1.param.isExtractedBinder, s"alternative not implemented yet")
          for {
            pt <- a1.ptyp.extract(a2.ptyp, Contravariant).toList //map fromExtract
            /// *
            
            (hExtr,h) = a2.param.toHole(a1.param)
            //(hExtr,h) = ((Map(a1.param.name -> new Rep(lambdaBound.get(a2.param))),Map(),Map()):Extract) -> Hole(a2.param.name)(a2.param.typ, None, None)
            // ^ Essentially the same, but explicitly makes a Rep to the occurrence; this should now be handled by rep which is called by readVal in toHole
            
            //m <- mergeGraph(pt, hExtr |> fromExtract)
            m <- merge(pt, hExtr).toList
            b <- extractGraph(a1.body, a2.body)(ctx.copy(valMap = ctx.valMap + (a1.param -> a2.param),
              // FIXME handle traversing lambdas
              // It seems that we'll have to create a brand new variable and do a substitution here, which is a shame...
              // We should at least delay the modification of the graph into a thunk until we are sure this match is the
              // one we choose to perform the rewriting, otherwise we're going to pollute the graph unnecessarily.
              // In the meantime, we'll do beta substitution in `simplifyGraph`
              
              //curCtrl = Push(DummyCallId,Id,ctx.curCtrl)
              //curCtrl = ctx.curCtrl `;` Push(DummyCallId,Id,Id)
              //curCtrl = ctx.curCtrl push DummyCallId
              //curCtrl = ctx.curCtrl.throughLambda
            ),
              lastWords(s"Not yet supported: matching the inside of a lambda.")
              // FIXME handle traversing lambdas
              //cctx
              //cctx.throughLambda
              //Push(DummyCallId,Id,cctx)
              //cctx push DummyCallId
            )
            m <- m |> fromExtract merge b
            // * /
            /* // Old way of making sure a pass node is inserted; we now do it with smarter substituteVal and storing 'abs' in MirrorVal
            a2p -> a2b = if (ctx.curOps.isEmpty) a2.param -> a2.body else {
              /* This is to temporarily sove the unsoundness with extracting lambda bodies while wrapping them in
                 control-flow nodes; it only solves the case where the lambda's body is matched as is (with no further
                 inner patterns) – otherwise we'd need to _always_ do this, even when `ctx.curOps.isEmpty`.
                 This is kind of wasteful and will quickly pollute the graph; ideally
                 we should only do it if necessary, and revert it otherwise (but how?) */
              val v = a2.param.copy(name = a2.param.name+"'"+freshVarCount)()
              //println(s"NEW ${a2.param} -> $v IN ${a2.body.showFullGraph}")
              val occ = rep(new MirrorVal(v)) // we have to introduce a new lambda-bound-like variable
              lambdaBound.put(v, occ.bound)
              val bod = substituteVal(a2.body,a2.param,occ)
              //println(s"BOD ${bod.showFullGraph}")
              v -> bod
            }
            (hExtr,h) = a2p.toHole(a1.param)
            m <- merge(pt, hExtr).toList
            b <- extractGraph(a1.body, a2b)(ctx.copy(valMap = ctx.valMap + (a1.param -> a2p)))
            m <- m |> fromExtract merge b
            */
          } yield m
          
        case (StaticModule(fullName1), StaticModule(fullName2)) if fullName1 == fullName2 =>
          fromExtract(EmptyExtract) :: Nil
          
        case Module(pref0, name0, tp0) -> Module(pref1, name1, tp1) =>
          extractGraph(pref0,pref1).flatMap(_ optionIf name0 == name1) ++ extractType(tp0,tp1,Invariant).map(fromExtract).toList
          
        // TODO properly thread assumptions
        case (MethodApp(self1,mtd1,targs1,args1,tp1), MethodApp(self2,mtd2,targs2,args2,tp2))
          if mtd1 === mtd2 || { debug(s"Symbol: ${mtd1.fullName} =/= ${mtd2.fullName}"); false }
        =>
          assert(args1.size == args2.size, s"Inconsistent number of argument lists for method $mtd1: $args1 and $args2")
          assert(targs1.size == targs2.size, s"Inconsistent number of type arguments for method $mtd1: $targs1 and $targs2")
          for {
            s <- extractGraph(self1,self2)
            t <- mergeAll( (targs1 zip targs2) map { case (a,b) => a extract (b, Covariant) } ).toStream
            a <- mergeAllGraph( (args1 zip args2) map { case (as,bs) => extractGraphArgList(as, bs) } )
            rt = GraphExtract fromExtract EmptyExtract
            m0 <- s merge fromExtract(t)
            m1 <- m0 merge a
            m2 <- m1 merge rt
          } yield m2
          
        case (NewObject(tp1), NewObject(tp2)) => tp1 extract (tp2, Covariant) map fromExtract toList
          
        case _ => Nil
      }
      case Rep(Box(_,_)|Branch(_,_,_,_)) -> _ => die
      case _ => Nil
    } map (_ matching xtee)
    
  }
  
  protected def mergeAllGraph(as: TraversableOnce[List[GraphExtract]]): List[GraphExtract] = {
    if (as isEmpty) return GraphExtract.empty :: Nil
    val ite = as.toIterator
    var res = ite.next()
    while(ite.hasNext && res.nonEmpty) res = for { a <- res; b <- ite.next(); m <- a merge b } yield m
    res
  }
  protected def extractGraphArgList(self: ArgList, other: ArgList)(implicit ctx: GXCtx, cctx: CCtx): List[GraphExtract] = {
    def extractRelaxed(slf: Args, oth: Args): List[GraphExtract] = {
      import slf._
      if (reps.size != oth.reps.size) return Nil
      val args = (reps zip oth.reps) map { case (a,b) => extractGraph(a, b) }
      ((GraphExtract.empty :: Nil) /: args) {
        case (acc, a) => for (acc <- acc; a <- a; m <- acc merge a) yield m }
    }
    import self._
    (self, other) match {
      case (a0: Args, a1: Args) =>
        require(reps.size == other.reps.size)
        extractRelaxed(a0,a1)
      case (ArgsVarargs(a0, va0), ArgsVarargs(a1, va1)) => for {
        a <- extractGraphArgList(a0, a1)
        va <- extractRelaxed(va0,va1)
        m <- a merge va
      } yield m
      case (ArgsVarargSpliced(a0, va0), ArgsVarargSpliced(a1, va1)) => for {
        a <- extractGraphArgList(a0, a1)
        va <- extractGraph(va0, va1)
        m <- a merge va
      } yield m
      case (ArgsVarargSpliced(a0, va0), ArgsVarargs(a1, vas1)) => for { // case dsl"List($xs*)" can extract dsl"List(1,2,3)"
        a <- extractGraphArgList(a0, a1)
        va <- spliceExtractGraph(va0, vas1)
        m <- a merge va
      } yield m
      case _ => Nil
    }
  }
  def spliceExtractGraph(xtor: Rep, args: Args)(implicit ctx: GXCtx): List[GraphExtract] = GraphExtract.fromExtract(xtor match {
    case RepDef(SplicedHole(name)) => mkExtract()()(name -> args.reps)
    case RepDef(h @ Hole(name)) => // If we extract ($xs*) using ($xs:_*), we have to build a Seq in the object language and return it
      mkExtract(name -> mkSeq(args.reps))()()
    case _ => throw IRException(s"Trying to splice-extract with invalid extractor $xtor")
  }) :: Nil
  
  // TODO merge boxes and remove useless ones
  // TODO make census and move boxes down non-shared nodes
  // TODO inline one-shot lambdas
  // TODO(maybe?) recurse in both branch legs (while avoiding infinite recs) and remember negative assumptions...
  //   indeed, I noticed a lot of patterns like: $96 = (x1 ? 22 ¿ $140); $140 = (x1 ? $253 ¿ $139); $253 = (x1 ? $290 ¿ $252); ...
  //   doing this correctly would probably need some sort of lens, as we can't just modify the leg of a branch (which
  //   may be shared); we have to reconstruct whole branch nodes, possibly several levels up.
  // FIXME make non-recursive, as we can quickly reach stack limit with deep branch traces (though we should also try to simplify them)
  //   There appears to be a lot of dumb branching, as noted above; even for positive paths
  /** `recurse` tells us whether to follow possibly-cyclic paths with different contexts; won't work with recursive functions */
  /* Notes.
  
  Problems with simplifying recursive graphs:
   – Can no longer push boxes inside branches like there is no tomorrow, since we can have cyclic structures that would
     grow unboundedly if we did that. Consequently, we should avoid pushing boxes into cycles, and we should generalize
     beta reduction to be able to apply on interspersed branches and boxes, as opposed to only one top-level box
     followed by branches...
   
  */
  val betaReduced = mutable.Set.empty[(List[(CallId,Bool,Rep)],Abs)]
  
  def simplifyGraph(rep: Rep, recurse: Bool = true): Bool =
  //scala.util.control.Breaks tryBreakable(
  {
    var changed = false
    val traversed = mutable.Set.empty[Rep]
    
    // Note that this is currently never used (as aggressive box pushing is not performed for recursive graphs)
    val reachableSets = // TODO improve; this is a bit wasteful, as we could share computations
      mutable.Map.empty[Rep, collection.Set[Rep]].withDefault(_.directlyReachable)
    def directlyLeadsBackTo(from: Rep, to: Rep) = reachableSets(from)(to)
    def directlyLeadsBackTo2(from: Rep, to0: Rep, to1: Rep) = {
      val rs = reachableSets(from)
      rs(to0) || rs(to1)
    }
    
    // We need to look through both legs of branches appearing under the lhs of applications, to figure out whether
    // they lead to lambda expressions...
    // To avoid combinatory explosion of recursive exploration, we memoize the results here:
    type AbsPath = (List[(Control,Control,CallId,Bool,Rep)],Control,Abs)
    val leadsToAbs = mutable.Map.empty[Rep,List[AbsPath]]
    
    def rec(rep: Rep): Unit = {
      //println(s"$cctx\n${rep}")
      
      /** This version of beta-red duplicates the application all over the place in case there is more than one branch... */
      def betaRed_duplicating(absPath: AbsPath, arg: Rep): Node = {
        val (conds,ctrl,fun) = absPath
        conds.foldRight ({
          substituteVal(fun.body, fun.param, arg, ctrl).node // fine (no duplication) because substituteVal creates a fresh Rep...
            .assertNotVal
        }) {
          case ((viaCtrl,ctrl,cid,isLHS,other0), nde) =>
            // Reconstruct the application nodes of all branches
            val r = nde.mkRep
            val other = ConcreteNode(Apply(Box.rep(viaCtrl, other0), arg, rep.typ)).mkRep
            val (thn,els) = if (isLHS) (r, other) else (other, r)
            Branch(ctrl,cid,thn,els)
        }
      }
      /** This version of beta-red does the same as betaRed_duplicating if there is at most one branch;
        * otherwise, it simply rewires failing branches to the _original_ reconstituted application, which does often
        * create kind of dumb structures that could often be simplified (which we do not currently try to do).
        * Invariant: the original 'rep' is overwritten with the result of this call! */
      def betaRed(absPath: AbsPath, arg: Rep): Node = {
        val (conds,ctrl,fun) = absPath
        val newBody =
          substituteVal(fun.body, fun.param, arg, ctrl).node // fine (no duplication) because substituteVal creates a fresh Rep...
            .assertNotVal
        conds match {
          case Nil => newBody
            
          // It seems that even this seemingly inoffensive version of the previous beta-red causes problems with the scheduler later on...
          /*
          case (viaCtrl0,ctrl0,cid0,isLHS0,other0) :: Nil =>
            // Reconstruct the application nodes of the branch, if single
            val other = ConcreteNode(Apply(Box.rep(viaCtrl0, other0), arg, rep.typ)).mkRep
            //Rdebug(s"Other $other")
            val r = newBody.mkRep
            val (thn,els) = if (isLHS0) (r, other) else (other, r)
            Branch(ctrl0,cid0,thn,els)
          */
            
          case condsRest =>
            // Reconstruct the application nodes of the top-level branch, if any
            val other = rep.node.mkRep // NOTE: this is only okay because we know the original rep will be overwritten with a new node!
            //Rdebug(s"Other $other")
            condsRest.foldRight(newBody) {
              case ((viaCtrl1,ctrl1,cid1,isLHS1,other1), nde) =>
                val r = nde.mkRep
                val (thn,els) = if (isLHS1) (r, other) else (other, r)
                Branch(ctrl1,cid1,thn,els)
            }
        }
      }
      def again(): Unit = {
        changed = true
        reachableSets.clear() // ugh! TODO rm? is it necessary?
        //scala.util.control.Breaks.break()
        // Note: commenting the following (or worse, uncommenting the above) will make some tests slow as they will print many more steps
        traversed -= rep; rec(rep)
      }
      traversed.setAndIfUnset(rep, rep.node match {
          
        case Branch(ctrl,cid,thn,els) =>
          mayHaveCid(Id,cid)(ctrl) // NOTE that we're testing the branch condition ALONE (not within cctx, which would obviously be unsound)
          match {
            case Some(true) => rep rewireTo thn; again()
            case Some(false) => rep rewireTo els; again()
            case None =>
              //if (hasCid_!(ctrl,cid,allowUnrelated=true)) rec(thn) //else rec(els)
              //rec(els)
              // ^ This whacky conditional recursion into 'thn' was all but a lucky heuristic that happens not to lead to
              //   infinite rewriting...
              // But it does miss opportunities that sohuld have been discovered by following the 'thn' branch more
              // often, leading to non-totally reduced graphs, and in particular, making unrelated definitions not
              // reduce completely when put together in the same graph!
              // In principle, we should do something more deterministic based on always traversing both branches, but
              // try to push boxes only into branches that don't have a path that cycle back to us (cf. the case for box/branch).
              // This used to lead to less reduced graphs, because we needed to generalize beta reduction too, which is now done.
              
              rec(thn); rec(els)
          }
          
        //case Box(Id,body) => // Nothing to do here... rewiring would reinsert the same Box(Id,_) wrapper!
          
        case Box(ctrl, r @ Rep(ConcreteNode(d:LeafDef))) // Need to make sure 'd' is not a variable! 
          if !d.isInstanceOf[Val] && (ctrl =/= Id)
        => rep rewireTo r; again()
          
        case Box(ctrl0, Rep(Box(ctrl1,body))) =>
          rep.node.assertNotVal
          rep.node = Box(ctrl0 `;` ctrl1, body)
          again()
          
          
        // This used to be necessary when we could not perform beta reduction across arbitrary sequences of branches and boxes;
        // but now that we can, it becomes mostly superfluous, and it seems to make graphs more complicated for no gain.
        // However, it seems needed to make the original GraphRewritingTests tests pass, so it does seem to have an enabling
        // effect for constant folding, though it's most likely just due to a current limitation in the pattern rewriter. 
        case Box(ctrl0, br @ Rep(Branch(ctrl1,cid,thn,els)))
          if aggressiveBoxPushing
          && (!supportDirectRecursion || !directlyLeadsBackTo2(thn,rep,br)) // This check is only necessary for graphs with direct recursion
          // TODO 'directlyLeadsBackTo2' condition seems too restrictive: sometimes we can simplify to thn without it actually looping back! cf. HOR5
        => // Push boxes into branches aggressively
          rep.node.assertNotVal
          // We now _have_ to check whether the box resolves the branch or not;
          // otherwise, we could trigger the sanity-check assertion of `;` that a Drop only drops a specific cid...
          mayHaveCid(ctrl1,cid)(ctrl0) match {
            case Some(cnd) =>
              rep.node = Box(ctrl0, if (cnd) thn else els)
            case None =>
              rep.node = Branch(ctrl0 `;` ctrl1, cid, Box.rep(ctrl0,thn), Box.rep(ctrl0,els))
          }
          again()
          
        case Box(ctrl,res) => rec(res)
          
        // Simple beta reduction
        case ConcreteNode(Apply(Rep(ConcreteNode(fun: Abs)), arg)) =>
          Rdebug(s"!>> SUBSTITUTE [${rep.bound}] ${fun.param} with ${arg} in ${fun.body.showGraph}")
          rep.node.assertNotVal
          rep.node = betaRed((Nil, Id, fun), arg)
          Rdebug(s"!<< SUBSTITUTE'd ${rep.showGraph}")
          
          // Strangely, it seems that some tests break when we use again() for beta-reduction cases; this might be totally incidental
          //again()
          changed=true
          
        // TODO try alternative form of beta reduction across push controls (i.e., move the controls up and put drops on top of arg)
          
        // Beta reduction across a box
        case ConcreteNode(Apply(Rep(Box(ctrl,Rep(ConcreteNode(fun: Abs)))), arg)) =>
          Rdebug(s"!>> SUBSTITUTE [${rep.bound}] ${fun.param} with ${arg} over $ctrl in ${fun.body.showGraph}")
          rep.node.assertNotVal
          rep.node = betaRed((Nil, ctrl, fun), arg)
          Rdebug(s"!<< SUBSTITUTE'd ${rep.showGraph}")
          
          //again()
          changed=true
        
        // Doesn't seem to change anything
        /*
        // Try to push boxes down branches on the way from applications 
        case ConcreteNode(Apply(rep @ Rep(Box(ctrl0, br@Rep(Branch(ctrl1,cid,thn,els)))), arg)) if !directlyLeadsBackTo(rep,br) =>
          rep.node.assertNotVal
          rep.node = Branch(ctrl0 `;` ctrl1, cid, Box.rep(ctrl0,thn), Box.rep(ctrl0,els))
          again()
        */
          
        // Beta reduction through branches and possibly a box!
        case ConcreteNode(Apply(br @ Rep(_: Branch | _: Box), arg)) =>
          // see if this branch leads to potentially other branches that lead to a beta redex
          
          val trav = mutable.Set.empty[Rep]
          // TODO prune impossible conditions!
          def go(rep: Rep): List[AbsPath] = trav.setAndIfUnset(rep, leadsToAbs.getOrElseUpdate(rep, rep.node match {
            case Branch(ctrl,cid,thn,els) =>
              def upd(isLHS: Bool)(ap: AbsPath): AbsPath = ((Id, ctrl, cid, isLHS, if (isLHS) els else thn) :: ap._1, ap._2, ap._3) 
              go(thn).map(upd(true)) ++ go(els).map(upd(false)) : List[AbsPath]
            //case Box(ctrl,Rep(ConcreteNode(fun: Abs))) => (Nil,ctrl,fun) :: Nil
            case Box(c,bod) =>
              go(bod).map{case(conds,ctrl,abs) => (conds.map{case(vc,c2,cid,side,b) => (c `;` vc, c `;` c2, cid, side, b)}, c `;` ctrl, abs)}
            case ConcreteNode(fun: Abs) => (Nil,Id,fun) :: Nil
            case _ => Nil
          }) alsoDo (trav -= rep), Nil)
          
          val gone = go(br)
          //Rdebug(s"G: ${gone}")
          //betaReduced.foreach(b => Rdebug("\t"+b))
          
          //gone.headOption 
          gone.find { case g @ (conds, ctrl, abs) =>
            conds.nonEmpty && { // if 'conds' is empty, it means we have several boxes; it's better to let the other rwr reduce them first
              val k = (conds.map { case (vc, c2, cid, side, b) => (cid, side, b) }, abs)
              !betaReduced(k) && { betaReduced(k) = true; true }}
          } // TODO(maybe) do the res too? would it work out? (maybe not since we destructively make modifications)
          match {
            case Some(path @ (conds,ctrl,fun)) =>
              assert(conds.nonEmpty) // we avoid cases where we traversed only boxes
              Rdebug(s"!>> SUBSTITUTE [${rep.bound}] ${fun.param} with ${arg} over $ctrl and ${
                conds.map(c => (if (c._2 === Id) "" else s"[${c._2}]")+(if(c._4)"" else "!")+c._3).mkString(",")} in ${fun.body.showGraph}")
              rep.node.assertNotVal
              rep.node = betaRed(path, arg)
              Rdebug(s"!<< SUBSTITUTE'd ${rep.showGraph}")
              leadsToAbs.clear() // TODO can we avoid clearing the entire 'leadsToAbs'?
              
              //Thread.sleep(200)
              
              //again()
              changed=true
              
            case None =>
              rec(br); rec(arg)
          }
          
        case ConcreteNode(d) => d.children.foreach(rec)
      })
    }
    rec(rep)
    changed
  }
  //) catchBreak true
  
  def rewriteSteps(tr: SimpleRuleBasedTransformer{val base: graph.type})(r: Rep): Option[Rep] = {
    
    //println(s"Before simpl: ${rep.showFullGraph}")
    
    //simplifyGraph(r)
    if (simplifyGraph(r)) return Some(r)
    
    //println(s"After simpl: ${rep.showFullGraph}")
    
    def tryThis(r: Rep)(implicit cctx: CCtx): Option[Rep] = {
      val oldBound = r.bound
      tr.rules.iterator.flatMap(rule => rewriteRepCtx(rule._1,r,rule._2) also_? {
        case Some(res) =>
          if (r.bound =/= oldBound) println(s"!!! ${r.bound} =/= ${oldBound}")
          debug(s" ${r}  =>  $res")
          //assert(!res.boundTo.isInstanceOf[ConcreteNode] || !res.boundTo.asInstanceOf[ConcreteNode].dfn.isInstanceOf[Abs])
          assert(!r.node.isInstanceOf[ConcreteNode] || !r.node.asInstanceOf[ConcreteNode].dfn.isInstanceOf[Abs]) // not sure this is useful/correct(?)
          
          //rebind(r.bound, res.node)
          // ^ Basically duplicates the def; it's often fine as it will be a fresh Rep wrapping a Def created by the rewrite rule;
          //   but in principle it could really be any existing Rep too!
          //   Also, it breaks the unique lambda invariant.
          
          /*
          assert(edges.containsKey(res.bound))
          //rebind(r.bound, ConcreteNode(res.bound))
          // ^ kind of works, but then we get things that rewrite forever because definitions change names...
          //   maybe we could adapt the `alreadyTransformedBy` mechanism and make that work?
          
          // In this final alternative, we set to steal the Def (which is nice to avoid renaming) but make 'res' point
          // back at us so there is no duplication; also we have to take care of the lambda backpointer
          res.node |>? {
            case ConcreteNode(abs:Abs) =>
              val occ = Option(lambdaBound.get(abs.param)).getOrElse(???) // TODO B/E
              val mir = boundTo_!(occ).asInstanceOf[ConcreteNode].dfn.asInstanceOf[MirrorVal]
              assert(mir.abs === res)
              mir.abs = r
          }
          rebind(r.bound, res.node)
          rebind(res.bound, ConcreteNode(r.bound))
          */
          
          //res.node |>? {
          //  case ConcreteNode(abs:Abs) =>
          //    val occ = Option(lambdaVariableBindings.get(abs.param)).getOrElse(???) // TODO B/E
          //    
          //    // FIXME port missing logic here!
          //    
          //    //val mir = boundTo_!(occ).asInstanceOf[ConcreteNode].dfn.asInstanceOf[MirrorVal]
          //    //assert(mir.abs === res)
          //    //mir.abs = r
          //}
          ////r.node = ConcreteNode(res.bound) // to avoid duplicating the node
          //r.node = res.node
          //res.node = ConcreteNode(r.bound) // FIXME won't work
          
          r rewireTo res
          
      //}).collectFirst{case Some(r)=>r}
      }).headOption
      
    }
    
    def rec(r: Rep,tried:Bool=false)(implicit cctx: CCtx): Option[Rep] = //debug(s"Rec $r $cctx") thenReturn
    //r.boundTo match {
    r.node match {
        
      //case Box(cid,res,k) => tryThis(r) orElse rec(res)(cctx.withOp_?(k->cid).getOrElse(???)) // FIXedME: probably useless (and wasteful)
      //case Box(ctrl,res) => (if (tried) None else tryThis(r)) orElse rec(res,true)(withCtrl_?(ctrl).getOrElse(???))
      case Box(ctrl,res) => rec(res,true)(withCtrl_?(ctrl).getOrElse(???))
      // ^ Ignore top-level boxes (Q: why was it previously done as above?):
        
      case Branch(ctrl,cid,thn,els) => if (hasCid_!(ctrl,cid,allowUnrelated=true)) rec(thn) else rec(els)
      case cn@ConcreteNode(d) => tryThis(r) orElse d.children.flatMap(rec(_)).headOption
    }
    rec(r)(CCtx.empty)
  }
  
  
  override protected def unapplyConst(rep: Rep, typ: TypeRep): Option[Any] = {
    //println(s"?! ${rep.node} ${rep.node.getClass} : $typ")
    rep.node match {
      case Box(_, body) => unapplyConst(body, typ)
      case ConcreteNode(cst @ Constant(v)) if typLeq(cst.typ, typ) => Some(v)
      case _ => None
      //case _ => super.unapplyConst(rep, typ)  // does not work because `dfn` returns the Rep#bound representative...
    }
  } //also("= " + _ also println)
  
}
