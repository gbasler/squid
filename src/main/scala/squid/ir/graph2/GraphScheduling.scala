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
import squid.ir.graph.SimpleASTBackend
import squid.utils._
import squid.utils.CollectionUtils._

import scala.collection.{mutable, immutable}
import scala.collection.immutable.ListMap

trait GraphScheduling extends AST { graph: Graph =>
  
  import squid.quasi.MetaBases
  import squid.utils.meta.{RuntimeUniverseHelpers => ruh}
  import ruh.sru
  
  override def runRep(rep: Rep): Any = eval(rep)
  
  case class CCtx(map: Map[CallId, Opt[CCtx]]) {
    override lazy val hashCode = super.hashCode
    
    def isDefinedAt(cid: CallId) = map(cid).isDefined
    def + (cid: CallId) = CCtx(map + (cid -> Some(this)))
    def - (cid: CallId) =
      //map.getOrElse(cid, this)
      //map(cid) // TODO B/E?
      //CCtx(map - cid)
      CCtx(map + (cid -> None))
    private implicit def self: this.type = this
    def withOp_?(op: Op) = op._1 match {
      case Call => Some(withCall(op._2))
      case Pass => Some(withPass(op._2))
      case Arg => withArg_?(op._2)
    }
  
    override def toString = s"E{${map.map(_ ||> (_ + "->" + _.getOrElse("∅"))).mkString(",")}}"
  }
  object CCtx { val empty: CCtx = CCtx(Map.empty) }
  def hasCond(cond: Condition)(implicit cctx: CCtx) = hasCond_?(cond).getOrElse(
    lastWords(s"Cannot resolve $cond in $cctx"))
  def hasCond_?(cond: Condition)(implicit cctx: CCtx): Opt[Bool] = cond.ops.foldLeft(cctx){
    case (ccur,(Call,cid)) => withCall(cid)(ccur)
    case (ccur,(Pass,cid)) => withPass(cid)(ccur)
    case (ccur,(Arg,cid)) => withArg_?(cid)(ccur).getOrElse(return None)
  }.map.get(cond.cid).map(_.isDefined)
  def withCall(cid: CallId)(implicit cctx: CCtx) = cctx + cid
  def withPass(cid: CallId)(implicit cctx: CCtx) = cctx - cid
  
  // FIXME Q: remove cid from map below?
  def withArg(cid: CallId)(implicit cctx: CCtx) = CCtx(cctx.map ++ cctx.map(cid).get.map) // TODO B/E?
  def withArg_?(cid: CallId)(implicit cctx: CCtx): Opt[CCtx] = for {
    m <- cctx.map get cid
    c <- m
  } yield CCtx(cctx.map ++ c.map)
  
  object EvalDebug extends PublicTraceDebug
  import EvalDebug.{debug=>Edebug}
  
  /** Note: currently creates garbage Reps – could save edges and restore them later... */
  def eval(rep: Rep) = {
    def rec(rep: Rep)(implicit cctx: CCtx, vctx: Map[Val,ConstantLike]): ConstantLike = rep.boundTo match {
      case Call(cid,res) => rec(res)(withCall(cid), vctx)
      case Arg(cid,res) => rec(res)(withArg(cid), vctx)
      case Pass(cid,res) => rec(res)(withPass(cid), vctx)
      case Branch(cond,thn,els) if hasCond(cond) => rec(thn)
      case Branch(cid,thn,els) => /*assert(!cctx.isDefinedAt(cid));*/ rec(els)
      case ConcreteNode(d) => d match {
        case Abs(p,b) =>
          Constant((a: Any) => rec(b)(cctx,vctx+(p->Constant(a))).value)
        case v: Val => vctx(v) // hapens?
        case MirrorVal(v) => vctx(v)
        case Ascribe(r, tp) => rec(r)
        case c: ConstantLike => c
        case bd: BasicDef =>
          val (reps,bound) = bd.reps.map(r => r -> freshBoundVal(r.typ)).unzip
          val curried = bound.foldRight(bd.rebuild(bound.map(readVal)).toRep)(abs(_,_))
          val fun = ScheduleDebug.muteFor { scheduleAndRun(curried) }
          reps.foldLeft(fun){case(f,a) => f.asInstanceOf[Any=>Any](rec(a).value)} into Constant
      }
    }
    rec(rep)(CCtx.empty,Map.empty).value
  }
  
  object ScheduleDebug extends PublicTraceDebug
  import ScheduleDebug.{debug=>Sdebug}
  
  def scheduleAndRun(rep: Rep): Any = SimpleASTBackend runRep rep |> treeInSimpleASTBackend
  def scheduleAndCompile(rep: Rep): Any = SimpleASTBackend compileRep rep |> treeInSimpleASTBackend
  
  /*protected*/ def treeInSimpleASTBackend(rep: Rep) = reinterpret(rep, SimpleASTBackend) { bv =>
    System.err.println(s"Found a free variable! ${bv} ${edges get bv}")
    SimpleASTBackend.bindVal(bv.name,bv.typ.asInstanceOf[SimpleASTBackend.TypeRep],Nil).toRep
  }
  
  override def scalaTreeIn(MBM: MetaBases)(SRB: MBM.ScalaReflectionBase, rep: Rep, ExtrudedHandle: BoundVal => MBM.u.Tree): MBM.u.Tree =
    SimpleASTBackend.scalaTreeIn(MBM)(SRB, treeInSimpleASTBackend(rep), bv => {
        System.err.println(s"Found a free variable! ${bv}")
        import MBM.u._
        //q"""scala.sys.error(${bv.name}+" not bound")"""
        q"""squid.lib.unbound(${bv.name})"""
      })
  
  trait Reinterpreter extends super.Reinterpreter { rein =>
    import rein.{newBase => nb}
    
    override def apply(r: Rep) = schedule(r)
    
    // TODO extract this into Base?
    def appN(f: nb.Rep, args: List[nb.Rep], typ: nb.TypeRep): nb.Rep = {
      val appSym = ruh.FunctionType.symbol(args.size).toType.member(sru.TermName("apply")).asMethod
      nb.mapp(f, recm(appSym), typ)()(nb.Args(args:_*))
    }
    def dead(rep: nb.Rep, typ: nb.TypeRep) = {
      nb.methodApp(nb.staticModule("squid.lib.package"),
        nb.loadMtdSymbol(nb.loadTypSymbol("squid.lib.package$"), "dead", None), typ::Nil, nb.Args(rep)::Nil, typ)
    }
    
    // FIXME we currently uselessly duplicate arguments to scheduled defs
    def schedule(rep: Rep): nb.Rep = {
      Sdebug(s"Scheduling: ${rep.showFullGraph}")
      
      //val allCtx = mutable.Map.empty[Rep,Set[CallId]]
      //val someCtx = mutable.Map.empty[Rep,Set[CallId]]
      val reachingCtxs = mutable.Map.empty[Rep,Set[CCtx]] // TODO rm...
      val nPaths = mutable.Map.empty[Rep,Int]//.withDefaultValue(0)
      
      //val bottom = Bottom.toRep
      //nPaths(bottom) = Int.MaxValue
      
      /** We need to distinguish each 'slot' in a predecessor node, and on top of that each side of a branch (currently
        * we assign 0 for left, 1 for right) */
      val pointers = mutable.Map.empty[Rep,mutable.Set[List[Int]->Rep]]
      
      //val analysed = mutable.Set.empty[CCtx->Rep]
      
      // TODO use `analysed` to avoid useless traversals?
      def analyse(pred: List[Int]->Rep, rep: Rep)(implicit cctx: CCtx): Unit = {
      //if (analysed.contains(cctx->rep)) println(s"??? graph seems cyclic! $rep $cctx") else { analysed += cctx -> rep
      //  Sdebug(s"Analyse $rep $cctx")
        //Thread.sleep(50)
        //nPaths(rep) += 1
        nPaths(rep) = nPaths.getOrElse(rep,0) + 1
        //someCtx(rep) = someCtx.getOrElse(rep,Set.empty) intersect cctx.map.keySet
        //allCtx(rep) = allCtx.getOrElse(rep,Set.empty) intersect cctx.map.keySet
        reachingCtxs(rep) = reachingCtxs.getOrElse(rep,Set.empty) + cctx
        rep.boundTo match {
          case Pass(cid, res) => analyse(pred,res)(withPass(cid)) // FIXME probably
          case Call(cid, res) => analyse(pred,res)(withCall(cid))
          case Arg(cid, res) => analyse(pred,res)(withArg(cid))
          case Branch(cond, thn, els) =>
            if (hasCond(cond)) analyse(pred ||> (0 :: _), thn) 
            else               analyse(pred ||> (1 :: _), els)
          //case Node(MirrorVal(v)) => 
          case ConcreteNode(d) =>
            val ptrs = pointers.getOrElseUpdate(rep,mutable.Set.empty)
            ptrs += pred
            d.children.zipWithIndex.foreach{case (c, idx) => analyse((idx::Nil) -> rep, c)}
        }
      }
      analyse((Nil,rep),rep)(CCtx.empty)
      
      //println(s"Paths:"+nPaths.map{case r->n => s"\n\t[$n]\t${r.fullString}"}.mkString)
      //Sdebug(s"Paths:"+nPaths.map{case r->n => s"\n\t[$n]\t${r.fullString}\n\t\t${reachingCtxs(r).mkString(" ")}"}.mkString)
      Sdebug(s"Pointers:"+pointers.map{case r->s => s"\n\t[${s.size}]\t${r.fullString}\n\t\t${reachingCtxs(r).mkString(" ")}"}.mkString)
      
      //val scheduled = mutable.ListMap.empty[Node,(nb.BoundVal,nb.Rep,List[Branch],nb.TypeRep)] // scala bug: wrong order!!
      //var scheduled = immutable.ListMap.empty[ConcreteNode,(nb.BoundVal,nb.Rep,List[Rep],nb.TypeRep)]
      var scheduled = ListMap.empty[Rep,(nb.BoundVal,nb.Rep,List[Rep],nb.TypeRep)]
      // ^ FIXME keys type?
      
      // TODO prevent infinite recursion?
      // TODO remove `pred` and `n` and associated analyses
      type recRet = ListMap[Rep,nb.BoundVal]->nb.Rep
      def rec(pred: Rep, rep: Rep, n: Int, topLevel:Bool=false)(implicit vctx: Map[Val,nb.BoundVal], cctx: CCtx): recRet = {
        //println(pred,nPaths.get(pred),rep,nPaths.get(rep))
        //Sdebug(s"> Sch ${pred.bound} -> $rep  (${vctx.mkString(",")})  ${reachingCtxs(pred).mkString(" ")}")
        Sdebug(s"> Sch $rep  (${vctx.mkString(",")})  ${cctx}  <- ${pred.bound}")
        assert(reachingCtxs(pred).nonEmpty)
        
        //val m = nPaths.getOrElse(rep, if (n > 0) return rec(pred, bottom, 0) else 0)
        val m = nPaths.getOrElse(rep, println(s"No path count for ${rep}") thenReturn 0)
        if ((m === 0) && n > 0) return {
          val as->r = rec(pred, rep, 0)
          as->dead(r,rect(rep.typ))
        }
        
        val res = ScheduleDebug.nestDbg { rep.boundTo |>[recRet] { // Note: never scheduling control-flow nodes, on purpose
          //case Box(cid,res,_) => rec(pred,res,n)
          case Box(cid,res,k) => rec(pred,res,n)(vctx,cctx.withOp_?(k->cid).getOrElse(???))
          //case Branch(cond,thn,els) if allCtx(pred)(cond) => rec(pred,thn,n)
          //case Branch(cond,thn,els) if !someCtx(pred)(cond) => rec(pred,els,n)
          case b @ Branch(cond,thn,els) =>
            //val reaching = reachingCtxs(pred)
            //if (reaching.forall(hasCond(cond)(_))) rec(pred,thn,n)
            //else if (!reaching.exists(hasCond(cond)(_))) rec(pred,els,n)
            val hc = hasCond_?(cond)
            if (hc===Some(true)) rec(pred,thn,n)
            else if (hc===Some(false)) rec(pred,els,n)
            else {
              //val v = freshBoundVal(rep.typ)
              //val w = recv(v)
              val v = rep.bound
              val w = recv(v)
              Sdebug(s"$v ~> $w")
              ListMap(rep->w) -> nb.readVal(w)
            }
          // Note that if we don't have `!d.isSimple`, we'll end up trying to schedule variable occurrences, which will
          // obviously fail as they will be extruded from their scope...
          // But if that condition enough to prevent scope extrusion in general?!
          case nde @ ConcreteNode(d) if !d.isSimple && pointers(rep).size > 1 && !topLevel => // FIXME condition '!d.isSimple'
            Sdebug(s"! 1 < |${pointers(rep).iterator.mapLHS(_.mkString(":")).mapRHS(_.bound).mkSetString}|")
            val (fsym,_,args,_) = scheduled.getOrElse(rep, {
              //val as->nr = rec(rep,rep,nPaths(nde))(Map.empty)
              val as->nr = rec(rep,rep,nPaths(rep),topLevel=true)(Map.empty,CCtx.empty) ||> (_.toList)
              //val v = freshBoundVal(lambdaType(as.unzip._2.map(_.typ),nde.typ))
              val v = bindVal("sch"+rep.bound.name, if (as.isEmpty) nde.typ else lambdaType(as.unzip._1.map(_.typ),nde.typ), Nil)
              val w = recv(v)
              (w,if (as.isEmpty) nr else nb.lambda(as.unzip._2,nr),as.unzip._1,rect(v.typ)) : (nb.BoundVal,nb.Rep,List[Rep],nb.TypeRep)
            } also (scheduled += rep -> _))
            val s = args.map(b => rec(rep,b,m))
            val a: ListMap[Rep,nb.BoundVal] = s.flatMap(_._1)(scala.collection.breakOut)
            val f = fsym|>nb.readVal
            val e = if (s.isEmpty) f else appN(f,s.map(_._2),rect(nde.typ))
            a -> e
          case ConcreteNode(d) => d match {
            case Abs(p,b) =>
              //println(s"Abs($p,$b)")
              val v = recv(p)
              val as->r = rec(rep,b,m)(vctx + (p->v), cctx)
              //println(s"/Abs")
              as->newBase.lambda(v::Nil,r)
            case MirrorVal(v) => ListMap.empty -> (vctx get v map nb.readVal getOrElse extrudedHandle(v))
            case MethodApp(self, mtd, targs, argss, tp) =>
              val sas->sr = rec(rep,self,m)
              var ass = sas
              val newArgss = argss.map(_.map(nb){r =>
                val as->sr = rec(rep,r,m)
                ass ++= as
                sr
              })
              ass->nb.methodApp(sr, recm(mtd), targs.map(rect), newArgss, rect(tp))
            case Ascribe(r, typ) =>
              rec(rep,r,m) ||> (_2 = nb.ascribe(_,rect(typ)))
            case Module(r, name, typ) =>
              rec(rep,r,m) ||> (_2 = nb.module(_,name,rect(typ)))
            case ld: LeafDef => ListMap.empty->apply(ld)
          }
        }}
        //Sdebug(s"<"+res._1.map("\t"+_).mkString("\n"))
        Sdebug(s"< "+res._1.mkString("\n  "))
        res
      }
      val lsm->r = rec(rep,rep,1)(Map.empty,CCtx.empty)
      assert(lsm.isEmpty) // TODO B/E
      scheduled.valuesIterator.foldRight(r){case ((funsym,fun,args,typ),r) => nb.letin(funsym,fun,r,typ)}
    }
    
    override protected def recv(v: Val): newBase.BoundVal =
      bound.getOrElse(v, super.recv(v)) // FIXME calling super.recv in the wrong order messes with some interpreters, such as BaseInterpreter
    
  }
  override def reinterpret(r: Rep, NewBase: squid.lang.Base)(ExtrudedHandle: BoundVal => NewBase.Rep = DefaultExtrudedHandler): NewBase.Rep =
    new Reinterpreter {
      val newBase: NewBase.type = NewBase
      override val extrudedHandle = ExtrudedHandle
    } apply r
  
  
  
  
}
