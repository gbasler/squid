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
import squid.utils.CollectionUtils.MutSetHelper

import scala.collection.{mutable,immutable}

trait GraphScheduling extends AST { graph: Graph =>
  
  import squid.quasi.MetaBases
  import squid.utils.meta.{RuntimeUniverseHelpers => ruh}
  import ruh.sru
  
  override def runRep(rep: Rep): Any = eval(rep)
  
  case class CCtx(map: Map[CallId, Opt[CCtx]]) {
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
  }
  object CCtx { val empty: CCtx = CCtx(Map.empty) }
  def hasCond(cond: Condition)(implicit cctx: CCtx) = hasCond_?(cond).get
  def hasCond_?(cond: Condition)(implicit cctx: CCtx): Opt[Bool] = cond.ops.foldLeft(cctx){
    case (ccur,(Call,cid)) => withCall(cid)(ccur)
    case (ccur,(Pass,cid)) => withPass(cid)(ccur)
    case (ccur,(Arg,cid)) => withArg_?(cid)(ccur).getOrElse(return None)
  }.map.get(cond.cid).map(_.isDefined)
  def withCall(cid: CallId)(implicit cctx: CCtx) = cctx + cid
  def withPass(cid: CallId)(implicit cctx: CCtx) = cctx - cid
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
          val fun = scheduleAndRun(curried)
          reps.foldLeft(fun){case(f,a) => f.asInstanceOf[Any=>Any](rec(a).value)} into Constant
      }
    }
    rec(rep)(CCtx.empty,Map.empty).value
  }
  
  def scheduleAndRun(rep: Rep): Any = SimpleASTBackend runRep rep |> treeInSimpleASTBackend
  def scheduleAndCompile(rep: Rep): Any = SimpleASTBackend compileRep rep |> treeInSimpleASTBackend
  
  protected def treeInSimpleASTBackend(rep: Rep) = reinterpret(rep, SimpleASTBackend) { bv =>
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
    
    def schedule(rep: Rep): nb.Rep = {
      
      //val allCtx = mutable.Map.empty[Rep,Set[CallId]]
      //val someCtx = mutable.Map.empty[Rep,Set[CallId]]
      val reachingCtxs = mutable.Map.empty[Rep,Set[CCtx]]
      val nPaths = mutable.Map.empty[Rep,Int]//.withDefaultValue(0)
      
      //val bottom = Bottom.toRep
      //nPaths(bottom) = Int.MaxValue
      
      def analyse(rep: Rep)(implicit cctx: CCtx): Unit = {
        //println(s"Analyse $rep")
        //nPaths(rep) += 1
        nPaths(rep) = nPaths.getOrElse(rep,0) + 1
        //someCtx(rep) = someCtx.getOrElse(rep,Set.empty) intersect cctx.map.keySet
        //allCtx(rep) = allCtx.getOrElse(rep,Set.empty) intersect cctx.map.keySet
        reachingCtxs(rep) = reachingCtxs.getOrElse(rep,Set.empty) + cctx
        rep.boundTo match {
          case Pass(cid, res) => analyse(res)(withPass(cid)) // FIXME probably
          case Call(cid, res) => analyse(res)(withCall(cid))
          case Arg(cid, res) => analyse(res)(withArg(cid))
          case Branch(cond, thn, els) => if (hasCond(cond)) analyse(thn) else analyse(els)
          //case Node(MirrorVal(v)) => 
          case ConcreteNode(d) => d.children.foreach(analyse)
        }
      }
      analyse(rep)(CCtx.empty)
      
      //println(s"Paths:"+nPaths.map{case r->n => s"\n\t[$n]\t${r.fullString}"}.mkString)
      
      //val scheduled = mutable.ListMap.empty[Node,(nb.BoundVal,nb.Rep,List[Branch],nb.TypeRep)] // scala bug: wrong order!!
      //var scheduled = immutable.ListMap.empty[ConcreteNode,(nb.BoundVal,nb.Rep,List[Rep],nb.TypeRep)]
      var scheduled = immutable.ListMap.empty[Rep,(nb.BoundVal,nb.Rep,List[Rep],nb.TypeRep)]
      // ^ FIXME keys type?
      
      // TODO prevent infinite recursion?
      def rec(pred: Rep, rep: Rep, n: Int)(implicit vctx: Map[Val,nb.BoundVal]): List[nb.BoundVal->Rep]->nb.Rep = {
        //println(pred,nPaths.get(pred),rep,nPaths.get(rep))
        //println(rep,vctx)
        
        //val m = nPaths.getOrElse(rep, if (n > 0) return rec(pred, bottom, 0) else 0)
        val m = nPaths.getOrElse(rep, println(s"No path count for ${rep}") thenReturn 0)
        if ((m === 0) && n > 0) return {
          val as->r = rec(pred, rep, 0)
          as->dead(r,rect(rep.typ))
        }
        
        rep.boundTo match { // Note: never scheduling control-flow nodes, on purpose
          case Call(cid,res) => rec(pred,res,n)
          case Arg(cid,res) => rec(pred,res,n)
          //case Branch(cond,thn,els) if allCtx(pred)(cond) => rec(pred,thn,n)
          //case Branch(cond,thn,els) if !someCtx(pred)(cond) => rec(pred,els,n)
          case b @ Branch(cond,thn,els) =>
            val reaching = reachingCtxs(pred)
            if (reaching.forall(hasCond(cond)(_))) rec(pred,thn,n)
            else if (!reaching.exists(hasCond(cond)(_))) rec(pred,els,n)
            else {
              val v = freshBoundVal(rep.typ)
              val w = recv(v)
              //((w,b)::Nil) -> nb.readVal(w)
              ((w,rep)::Nil) -> nb.readVal(w)
            }
          // Note that if we don't have `!d.isSimple`, we'll end up trying to schedule variable occurrences, which will
          // obviously fail as they will be extruded from their scope...
          // But if that condition enough to prevent scope extrusion in general?!
          case nde @ ConcreteNode(d) if !d.isSimple && m > n =>
            //println(s"Sch $m > $n")
            val (fsym,_,args,_) = scheduled.getOrElse(rep, {
              //val as->nr = rec(rep,rep,nPaths(nde))(Map.empty)
              val as->nr = rec(rep,rep,nPaths(rep))(Map.empty)
              val v = freshBoundVal(lambdaType(as.unzip._2.map(_.typ),nde.typ))
              val w = recv(v)
              (w,if (as.isEmpty) nr else nb.lambda(as.unzip._1,nr),as.unzip._2,rect(v.typ))
            } also (scheduled += rep -> _))
            val s = args.map(b => rec(rep,b,m))
            val a = s.flatMap(_._1)
            val f = fsym|>nb.readVal
            val e = if (s.isEmpty) f else appN(f,s.map(_._2),rect(nde.typ))
            a -> e
          case ConcreteNode(d) => d match {
            case Abs(p,b) =>
              //println(s"Abs($p,$b)")
              val v = recv(p)
              val as->r = rec(rep,b,m)(vctx + (p->v))
              //println(s"/Abs")
              as->newBase.lambda(v::Nil,r)
            case MirrorVal(v) => Nil -> (vctx(v) |> nb.readVal)
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
            case ld: LeafDef => Nil->apply(ld)
          }
        }
      }
      val Nil->r = rec(rep,rep,1)(Map.empty) // TODO B/E
      scheduled.valuesIterator.foldRight(r){case ((funsym,fun,args,typ),r) => nb.letin(funsym,fun,r,typ)}
    }
    
  }
  override def reinterpret(r: Rep, NewBase: squid.lang.Base)(ExtrudedHandle: BoundVal => NewBase.Rep = DefaultExtrudedHandler): NewBase.Rep =
    new Reinterpreter {
      val newBase: NewBase.type = NewBase
      override val extrudedHandle = ExtrudedHandle
    } apply r
  
  
  
  
}
