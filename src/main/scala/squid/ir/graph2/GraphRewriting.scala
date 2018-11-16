// Copyright 2018 EPFL DATA Lab (data.epfl.ch)
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
package graph2

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
  
  type XCtx = GXCtx
  def newXCtx: XCtx = GXCtx.empty
  
  // TODO make assumed/assumedNot as single assumptions ListSet?
  protected case class GXCtx(assumed: Set[Condition], assumedNot: Set[Condition], curOps: List[Op], valMap: Map[Val,Val], traverseBranches: Bool = true) {
    assert(!(assumed intersects assumedNot), s"${assumed} >< ${assumedNot}")
  }
  protected object GXCtx { def empty = GXCtx(Set.empty, Set.empty, Nil, Map.empty) }
  
  //override def mapDef(f: Def => Def)(r: Rep): r.type = ???
  //override protected def mapRep(rec: Rep => Rep)(d: Def) = ???
  
  protected val transformed = mutable.Set.empty[(Rep,ListSet[Condition->Bool],List[Rep])]
  def alreadyTransformedBy(xtor: Rep, ge: GraphExtract): Bool = transformed contains ((xtor, ge.assumptions, ge.traversedReps))
  def rememberTransformedBy(xtor: Rep, ge: GraphExtract): Unit = transformed += ((xtor, ge.assumptions, ge.traversedReps))
  
  override def spliceExtract(xtor: Rep, args: Args)(implicit ctx: XCtx) = ??? // TODO
  override def extract(xtor: Rep, xtee: Rep)(implicit ctx: XCtx) =
    extractGraph(xtor,xtee)(GXCtx.empty.copy(traverseBranches = false)).headOption map (_.extr)
  
  override def rewriteRep(xtor: Rep, xtee: Rep, code: Extract => Option[Rep]): Option[Rep] = {
    //println(s"rewriteRep $xtee  >>  $xtor")
    //println(s"already: ${transformed.map(r => s"\n\t${r._1.bound}, ${r._2}, ${r._3.map(_.bound)}")}")
    
    val matches = extractGraph(xtor, xtee)(GXCtx.empty) flatMap
      (_ merge (GraphExtract fromExtract repExtract(SCRUTINEE_KEY -> xtee)))
    
    //matches.iterator.flatMap { ge =>
    matches.filterNot(alreadyTransformedBy(xtor,_)).iterator.flatMap { ge =>
      //println(s"...considering $xtor << ${ge.traversedReps.map(_.simpleString)} --> ${ge.extr}")
      //println(s"...  ${ge.argsToRebuild} ${ge.callsToAvoid}")
      code(ge.extr) |>? {
        case Some(x0) =>
          //println(s"...transforming ${xtor.simpleString} -> $x0")
          //println(s"...transforming ${xtor.simpleString} -> ${x0.showGraph}")
          println(s"...transforming ${xtee.simpleString} -> ${x0.showGraph}")
          println(s"...  ${ge.assumptions}")
          println(s"...  ${xtor.simpleString} :: ${ge.traversedReps.map(_.bound)}")
          rememberTransformedBy(xtor,ge)
          //println(f: ${showEdges}")
          if (ge.assumptions.isEmpty) x0 else {
            val oldXtee = xtee.boundTo.mkRep // alternatively, could do the rebind here?
            ge.assumptions.toList.reverse.foldRight(x0) {
              case cond->true -> x => Branch(cond,x,oldXtee).mkRep
              case cond->false -> x => Branch(cond,oldXtee,x).mkRep
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
    def matching (r: Rep) = r.boundTo match {
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
  
  protected def extractGraph(xtor: Rep, xtee: Rep)(implicit ctx: XCtx): List[GraphExtract] = debug(s"Extract ${xtor} << $xtee") thenReturn nestDbg {
    import GraphExtract.fromExtract
    
    xtor -> xtee |> { // FIXME not matching Rep(Call...) ?!
        
      case Rep(ConcreteNode(Hole(name))) -> _ => for {
        typE <- xtor.typ.extract(xtee.typ, Covariant).toList
        r1 = xtee
        
        //() = println(s">>>  $r2  =/=  ${try removeArgs(ctx.assumedNotCalled)(r1) catch { case e => e}}")
        
        r2 = ctx.curOps.foldLeft(r1) {
          case r -> (k -> cid) => Box(cid,r,k).mkRep
        }
        
        e <- merge(typE, repExtract(name -> r2))
      } yield GraphExtract fromExtract e
        
      //case _ -> Rep(Call(cid, res)) =>
      //case _ -> Rep(Arg(cid, res)) =>
      case _ -> Rep(Box(cid, res, k)) =>
        extractGraph(xtor, res)(ctx.copy(curOps = (k,cid) :: ctx.curOps))
        
      case _ -> Rep(Branch(cond, thn, els)) =>
        val newCond = Condition(ctx.curOps ++ cond.ops, cond.cid)
        if (newCond.isAlwaysTrue) extractGraph(xtor, thn)
        else if (newCond.isAlwaysFalse) extractGraph(xtor, els)
        else (if (newCond in ctx.assumedNot) Nil else extractGraph(xtor, thn)(ctx.copy(assumed = ctx.assumed + newCond)).map(_ assuming newCond)) ++
          (if (newCond in ctx.assumed) Nil else extractGraph(xtor, els)(ctx.copy(assumedNot = ctx.assumedNot + newCond)).map(_ assumingNot newCond))
        
      case Rep(ConcreteNode(dxtor)) -> Rep(ConcreteNode(dxtee)) => dxtor -> dxtee match {
          
        case (_, Ascribe(v,tp)) => extractGraph(xtor,v)
          
        case (Ascribe(v,tp), _) =>
          for { a <- tp.extract(xtee.typ, Covariant).toList
                b <- extractGraph(v, xtee)
                m <- fromExtract(a) merge b } yield m
          
        case (h:HOPHole, _) => ??? // TODO
          
        //case (_, MirrorVal(_)) => die // TODO?
        case (_, MirrorVal(v)) => GraphExtract.empty optionIf (dxtor === v) toList
          
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
          
        case (a1: Abs, a2: Abs) =>
          require(a1.param.isExtractedBinder, s"alternative not implemented yet")
          for {
            pt <- a1.ptyp.extract(a2.ptyp, Contravariant).toList //map fromExtract
            ///*
            (hExtr,h) = a2.param.toHole(a1.param)
            //m <- mergeGraph(pt, hExtr |> fromExtract)
            m <- merge(pt, hExtr).toList
            b <- extractGraph(a1.body, a2.body)(ctx.copy(valMap = ctx.valMap + (a1.param -> a2.param)))
            m <- m |> fromExtract merge b
            //*/
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
      case Rep(Call(_,_)|Arg(_,_)|Branch(_,_,_)) -> _ => die
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
  protected def extractGraphArgList(self: ArgList, other: ArgList)(implicit ctx: GXCtx): List[GraphExtract] = {
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
  
  def simplifyGraph(rep: Rep): Unit = {
    val traversed = mutable.Set.empty[Rep]
    def rec(rep: Rep)(implicit cctx: CCtx): Unit = {
      def again = { traversed -= rep; rec(rep) }
      traversed.setAndIfUnset(rep, rep.boundTo match {
        case Branch(cond,thn,els) =>
          // FIXME code dup?
          if (cond.isAlwaysTrue) rebind(rep.bound, thn.boundTo) thenReturn again
          else if (cond.isAlwaysFalse) rebind(rep.bound, els.boundTo) thenReturn again
          else { rec(thn); rec(els) }
        case Box(cid,b @ Rep(Branch(Condition(ops,c),thn,els)),kind) =>
          //rebind(rep.bound, Box(cid,b,kind))
          rebind(rep.bound, Branch(Condition((kind,cid)::ops,c), Box(cid,thn,kind).mkRep, Box(cid,els,kind).mkRep))
          again
        case Box(cid,res,kind) => rec(res)
        case ConcreteNode(d) => d.children.foreach(rec)
      })
    }
    rec(rep)(CCtx.empty)
  }
  
  def rewriteSteps(tr: SimpleRuleBasedTransformer{val base: graph.type})(rep: Rep): Iterator[Rep] = {
    //println(edges)
    
    //println(s"Before simpl: ${rep.showFullGraph}")
    simplifyGraph(rep)
    //println(s"After simpl: ${rep.showFullGraph}")
    
    //rep.iterator.flatMap(r => {
    rep.iterator.filterNot(_.boundTo.isInstanceOf[Branch]).flatMap(r => { // don't rewrite branches at the top-level; it just introduces unnecessary hypotheses
      val oldBound = r.bound
      tr.rules.iterator.flatMap(rule => rewriteRep(rule._1,r,rule._2) also_? {
        case Some(res) =>
          if (r.bound =/= oldBound) println(s"!!! ${r.bound} =/= ${oldBound}")
          //println(s" ${r}  =>  $res")
          
          rebind(r.bound, res.boundTo)
          
          //println(edges)
      })
    })
    
  }
  
  
  // TODO:
  //override protected def unapplyConst(rep: Rep, typ: TypeRep): Option[Any] = ???
  
}
