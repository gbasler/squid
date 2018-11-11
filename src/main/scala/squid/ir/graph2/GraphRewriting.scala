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
import squid.utils.meta.{RuntimeUniverseHelpers => ruh}

import scala.collection.mutable

/*

FIXME:
  Under an Arg, we really can't make any assumptions, because arbitrary context may have been restored!
      ...that is, unless we actually saw the corresponding Call on the way...
  The best we can do is push the arg down... but we can't push it past a branch!
  Maybe we could make more elaborate assumptions on what's in nested contexts... but then we don't really have a way to
  reconstruct the branching for it: branches only inspect the top-level
    We could probably extend branching to be able to look at _paths_, but that seems much more complex

TODO:
  Use analysis before doing the rewrites, to prune possible paths to explore

*/
trait GraphRewriting extends AST { graph: Graph =>
  
  type XCtx = GXCtx
  def newXCtx: XCtx = GXCtx.empty
  
  protected case class GXCtx(assumed: Set[Condition], assumedNot: Set[Condition], curOps: List[Op], valMap: Map[Val,Val], traverseBranches: Bool = true) {
    assert(!(assumed intersects assumedNot), s"${assumed} >< ${assumedNot}")
  }
  protected object GXCtx { def empty = GXCtx(Set.empty, Set.empty, Nil, Map.empty) }
  
  //override def mapDef(f: Def => Def)(r: Rep): r.type = ???
  //override protected def mapRep(rec: Rep => Rep)(d: Def) = ???
  
  override def spliceExtract(xtor: Rep, args: Args)(implicit ctx: XCtx) = ??? // TODO
  override def extract(xtor: Rep, xtee: Rep)(implicit ctx: XCtx) =
    extractGraph(xtor,xtee)(GXCtx.empty.copy(traverseBranches = false)).headOption map (_.extr)
  
  override def rewriteRep(xtor: Rep, xtee: Rep, code: Extract => Option[Rep]): Option[Rep] = {
    
    val matches = extractGraph(xtor, xtee)(GXCtx.empty) flatMap
      (_ merge (GraphExtract fromExtract repExtract(SCRUTINEE_KEY -> xtee)))
    
    matches.flatMap { ge =>
      //println(s"...considering $xtor << ${ge.traversedReps.map(_.simpleString)} --> ${ge.extr}")
      //println(s"...  ${ge.argsToRebuild} ${ge.callsToAvoid}")
      code(ge.extr) |>? {
        case Some(x0) =>
          println(s"...transforming ${xtor.simpleString}")
          println(s"...  ${ge.assumptions}")
          if (ge.assumptions.isEmpty) x0
          else {
            ge.assumptions.foldRight(x0) {
              case cond -> x => Branch(cond,x,xtee).mkRep
            }
          }
      }
    }.headOption
    
  }
  
  // Q: do we need something like the old 'callsToAvoid'?
  protected case class GraphExtract(extr: Extract, assumptions: Set[Condition]) { // FIXME branchesToRebuild should include negatives
    def merge(that: GraphExtract): Option[GraphExtract] =
      //if ((argsToRebuild intersects that.callsToAvoid) || (that.argsToRebuild intersects callsToAvoid)) Stream.Empty
      //else 
      graph.merge(extr,that.extr).map(e =>
        GraphExtract(e, assumptions ++ that.assumptions))
    override def toString = s"{${assumptions.mkString(",")}}\\\t${extr._1.getOrElse(SCRUTINEE_KEY,"")} ${
      (extr._1-SCRUTINEE_KEY).map(r => "\n\t "+r._1+" -> "+r._2).mkString}"
  }
  protected object GraphExtract {
    val empty: GraphExtract = GraphExtract(EmptyExtract, Set.empty)
    def fromExtract(e: Extract): GraphExtract = empty copy (extr = e)
  }
  
  protected def extractGraph(xtor: Rep, xtee: Rep)(implicit ctx: XCtx): List[GraphExtract] = debug(s"Extract ${xtor} << $xtee") thenReturn nestDbg {
    import GraphExtract.fromExtract
    
    xtor -> xtee match { // FIXME not matching Rep(Call...) ?!
        
      //case _ -> Rep(Call(cid, res)) =>
      //case _ -> Rep(Arg(cid, res)) =>
      case _ -> Rep(Box(cid, res, k)) =>
        extractGraph(xtor, res)(ctx.copy(curOps = (k,cid) :: ctx.curOps))
        
      case _ -> Rep(Branch(cond, thn, els)) =>
        val newCond = Condition(ctx.curOps ++ cond.ops, cond.cid)
        if (newCond.isAlwaysTrue) extractGraph(xtor, thn)
        else if (newCond.isAlwaysFalse) extractGraph(xtor, els)
        else extractGraph(xtor, thn)(ctx.copy(assumed = ctx.assumed + newCond)) ++
             extractGraph(xtor, els)(ctx.copy(assumedNot = ctx.assumedNot + newCond))
        
      case Rep(ConcreteNode(dxtor)) -> Rep(ConcreteNode(dxtee)) => dxtor -> dxtee match {
          
        case (_, Ascribe(v,tp)) => extractGraph(xtor,v)
          
        case (Ascribe(v,tp), _) =>
          for { a <- tp.extract(xtee.typ, Covariant).toList
                b <- extractGraph(v, xtee)
                m <- fromExtract(a) merge b } yield m
          
        case (h:HOPHole, _) => ??? // TODO
          
        case (_, MirrorVal(_)) => die // TODO?
          
        case (_, Hole(_)) => die
          
        case VirtConst(vxtor) -> Constant(_) =>  // Note: if this does not match, we may still match an explicit usage of the const function...
          extractGraph(vxtor, xtee)
            //.map(ge => ge.copy(traversedReps = ge.traversedReps.tail)) // just to avoid duplicating the traversed rep in traversedReps
          
        case (Constant(v1), Constant(v2)) =>
          mergeOpt(extractType(xtor.typ, xtee.typ, Covariant), if (v1 == v2) Some(EmptyExtract) else None).map(fromExtract).toList
          
        case (a:ConstantLike) -> (b:ConstantLike) if a.value === b.value => GraphExtract.empty :: Nil
          
        case (Hole(name), _) => for {
          typE <- xtor.typ.extract(xtee.typ, Covariant).toList
          r1 = xtee
          
          //() = println(s">>>  $r2  =/=  ${try removeArgs(ctx.assumedNotCalled)(r1) catch { case e => e}}")
          
          r2 = ctx.curOps.foldLeft(r1) {
            case r -> (k -> cid) => Box(cid,r,k).mkRep
          }
          
          e <- merge(typE, repExtract(name -> r2))
        } yield GraphExtract fromExtract e
          
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
            (hExtr,h) = a2.param.toHole(a1.param)
            //m <- mergeGraph(pt, hExtr |> fromExtract)
            m <- merge(pt, hExtr).toList
            b <- extractGraph(a1.body, a2.body)(ctx.copy(valMap = ctx.valMap + (a1.param -> a2.param)))
            m <- m |> fromExtract merge b
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
    }
    
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
  
  def rewriteSteps(tr: SimpleRuleBasedTransformer{val base: graph.type})(rep: Rep): Iterator[Rep] = {
    //println(edges)
    
    rep.iterator.flatMap(r => {
      tr.rules.flatMap(rule => rewriteRep(rule._1,r,rule._2) also_? {
        case Some(res) =>
          //println(s" ${r}  =>  $res")
          rebind(r.bound, res.boundTo)
          //println(edges)
      })
    })
    
  }
  
  
  // TODO:
  //override protected def unapplyConst(rep: Rep, typ: TypeRep): Option[Any] = ???
  
}
