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
import squid.utils.meta.{RuntimeUniverseHelpers => ruh}

import scala.collection.immutable.ListSet
import scala.collection.mutable



import squid.ir.graph.SimpleASTBackend

trait GraphScheduling extends AST { graph: Graph =>
  
  import squid.quasi.MetaBases
  import squid.utils.meta.{RuntimeUniverseHelpers => ruh}
  import ruh.sru
  
  override def runRep(rep: Rep): Any = eval(rep)
  
  //case class CCtx(map: Map[CallId, Opt[CCtx]]) {
  //}
  type CCtx = List[CallId]
  object CCtx { val empty: CCtx = Nil }
  
  def eval(rep: Rep) = {
    def rec(rep: Rep)(implicit cctx: CCtx, vctx: Map[Val,ConstantLike]): ConstantLike = rep.node match {
      case Stop(res) => rec(res)(cctx.tail, vctx)
      case Call(cid,res) => rec(res)(cid::cctx, vctx)
      case Branch(stops,cid,thn,els) =>
        if (cctx.drop(stops).head === cid) rec(thn) else rec(els)
      case cn@ConcreteNode(d) => d match {
        case Abs(p,b) =>
          Constant((a: Any) => rec(b)(cctx,vctx+(p->Constant(a))).value)
        case v: Val => rec(cn.mkRep) // used to be 'rec(new Rep(v))', but now we interpret these as graph edges!
        //case MirrorVal(v) => vctx(v)
        case Ascribe(r, tp) => rec(r)
        case c: ConstantLike => c
        case bd: BasicDef =>
          val (reps,bound) = bd.reps.map(r => r -> freshBoundVal(r.typ)).unzip
          val curried = bound.foldRight(bd.rebuild(
            bound.map(readVal)
            //bound.map(new MirrorVal(_).toRep)
          ).toRep)(abs(_,_))
          println(curried.showGraph)
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
    System.err.println(s"Found a free variable! ${bv} ${/*edges get bv*/}")
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
      ???
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

