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
  
  case class CCtx(map: Map[CallId, CCtx]) {
    def isDefinedAt(cid: CallId) = map.isDefinedAt(cid)
    def + (cid: CallId) = CCtx(map + (cid -> this))
    def - (cid: CallId) =
      //map.getOrElse(cid, this)
      map(cid) // TODO B/E?
  }
  object CCtx { val empty: CCtx = CCtx(Map.empty) }
  def hasCall(cid: CallId)(implicit cctx: CCtx) = cctx.map.isDefinedAt(cid)
  def withCall(cid: CallId)(implicit cctx: CCtx) = cctx + cid
  def withoutCall(cid: CallId)(implicit cctx: CCtx) = cctx - cid
  
  object EvalDebug extends PublicTraceDebug
  import EvalDebug.{debug=>Edebug}
  
  /** Note: currently creates garbage Reps – could save edges and restore them later... */
  def eval(rep: Rep) = {
    def rec(rep: Rep)(implicit cctx: CCtx, vctx: Map[Val,ConstantLike]): ConstantLike = rep match {
      case Call(cid,res) => rec(res)(withCall(cid), vctx)
      case Arg(cid,res) => rec(res)(withoutCall(cid), vctx)
      case Branch(cid,thn,els) if hasCall(cid) => rec(thn)
      case Branch(cid,thn,els) => assert(!cctx.isDefinedAt(cid)); rec(els)
      case Node(d) => d match {
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
      
      val allCtx = mutable.Map.empty[Rep,Set[CallId]]
      val someCtx = mutable.Map.empty[Rep,Set[CallId]]
      val nPaths = mutable.Map.empty[Rep,Int]//.withDefaultValue(0)
      
      //val bottom = Bottom.toRep
      //nPaths(bottom) = Int.MaxValue
      
      def analyse(rep: Rep)(implicit cctx: CCtx): Unit = {
        //println(s"Analyse $rep")
        //nPaths(rep) += 1
        nPaths(rep) = nPaths.getOrElse(rep,0) + 1
        someCtx(rep) = someCtx.getOrElse(rep,Set.empty) intersect cctx.map.keySet
        allCtx(rep) = allCtx.getOrElse(rep,Set.empty) intersect cctx.map.keySet
        rep match {
          case Call(cid, res) => analyse(res)(withCall(cid))
          case Arg(cid, res) => analyse(res)(withoutCall(cid))
          case Branch(cid, thn, els) => if (hasCall(cid)) analyse(thn) else analyse(els)
          //case Node(MirrorVal(v)) => 
          case Node(d) => d.children.foreach(analyse)
        }
      }
      analyse(rep)(CCtx.empty)
      
      //println(s"Paths:"+nPaths.map{case r->n => s"\n\t[$n]\t${r.simpleString}"}.mkString)
      
      //val scheduled = mutable.ListMap.empty[Node,(nb.BoundVal,nb.Rep,List[Branch],nb.TypeRep)] // scala bug: wrong order!!
      var scheduled = immutable.ListMap.empty[Node,(nb.BoundVal,nb.Rep,List[Branch],nb.TypeRep)]
      
      // TODO prevent infinite recursion? 
      
      def rec(pred: Rep, rep: Rep, n: Int)(implicit vctx: Map[Val,nb.BoundVal]): List[nb.BoundVal->Branch]->nb.Rep = {
        //println(pred,nPaths.get(pred),rep,nPaths.get(rep))
        
        //val m = nPaths.getOrElse(rep, if (n > 0) return rec(pred, bottom, 0) else 0)
        val m = nPaths.getOrElse(rep, println(s"No path count for ${rep}") thenReturn 0)
        if ((m === 0) && n > 0) return {
          val as->r = rec(pred, rep, 0)
          as->dead(r,rect(rep.typ))
        }
        
        rep match {
          case Call(cid,res) => rec(pred,res,n)
          case Arg(cid,res) => rec(pred,res,n)
          case Branch(cid,thn,els) if allCtx(pred)(cid) => rec(pred,thn,n)
          case Branch(cid,thn,els) if !someCtx(pred)(cid) => rec(pred,els,n)
          case b @ Branch(cid,thn,els) =>
            val v = freshBoundVal(rep.typ)
            val w = recv(v)
            ((w,b)::Nil) -> nb.readVal(w)
          case nde @ Node(d) if m > n =>
            println(s"$m > $n")
            val (fsym,_,args,_) = scheduled.getOrElse(nde, {
              val as->nr = rec(nde,nde,nPaths(nde))(Map.empty)
              val v = freshBoundVal(lambdaType(as.unzip._2.map(_.typ),nde.typ))
              val w = recv(v)
              (w,nb.lambda(as.unzip._1,nr),as.unzip._2,rect(v.typ))
            } also (scheduled += nde -> _))
            val s = args.map(b => rec(rep,b,m))
            val a = s.flatMap(_._1)
            val e = appN(fsym|>nb.readVal,s.map(_._2),rect(nde.typ))
            a -> e
          case Node(d) => d match {
            case Abs(p,b) =>
              val v = recv(p)
              val as->r = rec(rep,b,m)(vctx + (p->v))
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
