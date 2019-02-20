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

import squid.utils._
import squid.utils.CollectionUtils.MutSetHelper
import squid.utils.meta.{RuntimeUniverseHelpers => ruh}

//import scala.collection.immutable.ListSet
//import scala.collection.mutable

import squid.utils.CollectionUtils._

import scala.collection.{mutable, immutable}
import scala.collection.immutable.ListMap


import squid.ir.graph.SimpleASTBackend

trait GraphScheduling extends AST { graph: Graph =>
  
  import squid.quasi.MetaBases
  import squid.utils.meta.{RuntimeUniverseHelpers => ruh}
  import ruh.sru
  
  override def runRep(rep: Rep): Any = eval(rep)
  
  type CCtx = List[CallId]
  object CCtx {
    def unknown: CCtx = empty
    val empty: CCtx = List.empty
  }
  def withCtrl_!(ctrl: Control)(implicit cctx: CCtx): CCtx = withCtrl_?(ctrl).get
  def withCtrl_?(ctrl: Control)(implicit cctx: CCtx): Opt[CCtx] = ctrl match {
    case Id => Some(cctx)
    case Begin(cid) => Some(cid :: cctx)
    case Block(cid) => Some(cctx.filterNot(_ === cid)) // FIXME correct?
    //case End(cid, rest) => withCtrl_!(rest)(cctx.dropWhile(_ =/= cid).tail)
    case End(cid, rest) =>
      val c = cctx.dropWhile(_ =/= cid)
      if (c.nonEmpty) withCtrl_?(rest)(c.tail) else None
  }
  //def withCall(cid: CallId)(implicit cctx: CCtx): CCtx = (cid -> Set.empty[Val]) :: cctx
  def hasCid(ctrl: Control, cid: CallId)(implicit cctx: CCtx): Bool = withCtrl_!(ctrl).exists(_ === cid)
  def mayHaveCid(ctrl: Control, cid: CallId)(implicit cctx: CCtx): Option[Bool] =
    withCtrl_?(ctrl).map(_.exists(_ === cid)) // FIXME an Id ctrl and empty CCtx give Some(false) instead of None...
  
  def eval(rep: Rep) = {
    def rec(rep: Rep)(implicit cctx: CCtx, vctx: Map[Val,ConstantLike]): ConstantLike = rep.node match {
      case Box(ctrl,res) => rec(res)(withCtrl_!(ctrl),vctx)
      case Branch(ctrl,cid,thn,els) =>
        if (hasCid(ctrl,cid)) rec(thn) else rec(els)
      case cn@ConcreteNode(d) => d match {
        case Abs(p,b) =>
          Constant((a: Any) => rec(b)(cctx,vctx+(p->Constant(a))).value)
        case v: Val => vctx(v)
        //case MirrorVal(v) => vctx(v)
        case Ascribe(r, tp) => rec(r)
        case c: ConstantLike => c
        case bd: BasicDef =>
          val (reps,bound) = bd.reps.map(r => r -> freshBoundVal(r.typ)).unzip
          val curried = bound.foldRight(bd.rebuild(
            bound.map(readVal)
            //bound.map(new MirrorVal(_).toRep)
          ).toRep)(abs(_,_))
          //println("! "+curried.showGraph)
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
      /** We need to distinguish each 'slot' in a predecessor node, and on top of that each side of a branch (currently
        * we assign 0 for left, 1 for right) */
      val pointers = mutable.Map.empty[Rep,mutable.Set[List[Int]->Rep]]
      
      // TODO use `analysed` to avoid useless traversals?
      def analyse(pred: List[Int]->Rep, rep: Rep)(implicit cctx: CCtx): Unit = {
        //println(pred,rep)
        rep.node match {
          case Box(ctrl,res) => analyse(pred,res)(withCtrl_!(ctrl)) // FIXME probably
          case Branch(ctrl, cid, thn, els) =>
            if (hasCid(ctrl, cid))
                 analyse(pred ||> (0 :: _), thn) 
            else analyse(pred ||> (1 :: _), els)
          case vn@ConcreteNode(v:Val) => //analyse(pred,vn.mkRep)
          case ConcreteNode(d) =>
            val ptrs = pointers.getOrElseUpdate(rep,mutable.Set.empty)
            ptrs += pred
            d.children.zipWithIndex.foreach{case (c, idx) => analyse((idx::Nil) -> rep, c)}
        }
      }
      analyse((Nil,rep),rep)(CCtx.empty)
      
      Sdebug(s"Pointers:"+pointers.map{case r->s => s"\n\t[${s.size}]\t${r.fullString}"}.mkString)
      
      
      
      var scheduled = ListMap.empty[Rep,(nb.BoundVal,nb.Rep,List[Rep],nb.TypeRep)]
      // ^ FIXME keys type?
      
      // TODO prevent infinite recursion?
      // TODO remove `pred` and `n` and associated analyses
      type recRet = ListMap[Rep,nb.BoundVal]->nb.Rep
      def rec(rep: Rep, topLevel:Bool=false)(implicit vctx: Map[Val,nb.BoundVal], cctx: CCtx): recRet = {
        Sdebug(s"> Sch $rep  (${vctx.mkString(",")})  ${cctx}")
        
        def postpone() = {
          val fv = freeVals(rep)
          val vset = vctx.keySet
          println(s"FV ${fv} ? $vset")
          if (fv & vset isEmpty) { // if none of the variables we locally bind are free in the branch, we can safely make the branch a parameter
            //val v = freshBoundVal(rep.typ)
            //val w = recv(v)
            val v = rep.bound
            val w = recv(v)
            Sdebug(s"$v ~> $w")
            ListMap(rep->w) -> nb.readVal(w)
          } else {
            val extrudedVals = (fv & vset).toList
            val k = bindVal("κ", lambdaType(extrudedVals.map(_.typ), rep.typ), Nil)
            val kbody = lambda(extrudedVals, rep)
            pointers(kbody)=mutable.Set(List.empty[Int]->kbody)
            val rk = recv(k)
            println(k,kbody)
            ListMap(kbody->rk) -> appN(rk|>nb.readVal,extrudedVals.map(recv).map(nb.readVal),rect(rep.typ))
          }
        }
        
        val res = ScheduleDebug.nestDbg { rep.node |>[recRet] { // Note: never scheduling control-flow nodes, on purpose
          
          //case Box(ctrl,res) => rec(res)(vctx,withCtrl(ctrl)) // FIXedME?
          case Box(ctrl,res) => withCtrl_?(ctrl) match {
            case Some(c) => rec(res)(vctx,c)
            case None => postpone()
          }
            
          //case Call(cid,res) => rec(res)(vctx,withCall(cid))
          case b @ Branch(ctrl,cid,thn,els) =>
            //val hc = cctx.drop(stops).headOption.map(_ === cond)
            //if (hc === Some(true)) rec(pred,thn,n)
            //else if (hc===Some(false)) rec(pred,els,n)
            //else {
            mayHaveCid(ctrl, cid) match {
            case Some(true) => rec(thn)
            case Some(false) => rec(els)
            case _ => postpone()
            }
          // Note that if we don't have `!d.isSimple`, we'll end up trying to schedule variable occurrences, which will
          // obviously fail as they will be extruded from their scope...
          // But if that condition enough to prevent scope extrusion in general?!
          case nde @ ConcreteNode(d) if !d.isSimple && pointers(rep).size > 1 && !topLevel => // FIXME condition '!d.isSimple'
            Sdebug(s"! 1 < |${pointers(rep).iterator.mapLHS(_.mkString(":")).mapRHS(_.bound).mkSetString}|")
            val (fsym,_,args,_) = scheduled.getOrElse(rep, {
              //val as->nr = rec(rep,rep,nPaths(nde))(Map.empty)
              val as->nr = rec(rep,topLevel=true)(Map.empty,CCtx.unknown) ||> (_.toList)
              //val v = freshBoundVal(lambdaType(as.unzip._2.map(_.typ),nde.typ))
              val v = bindVal(
                //"sch"+rep.bound.name,
                rep.bound.name,
                if (as.isEmpty) nde.typ else lambdaType(as.unzip._1.map(_.typ),nde.typ), Nil)
              val w = recv(v)
              (w,if (as.isEmpty) nr else nb.lambda(as.unzip._2,nr),as.unzip._1,rect(v.typ)) : (nb.BoundVal,nb.Rep,List[Rep],nb.TypeRep)
            } also (scheduled += rep -> _))
            val s = args.map(b => rec(b))
            val a: ListMap[Rep,nb.BoundVal] = s.flatMap(_._1)(scala.collection.breakOut)
            val f = fsym|>nb.readVal
            val e = if (s.isEmpty) f else appN(f,s.map(_._2),rect(nde.typ))
            a -> e
          case cn@ConcreteNode(d) => d match {
            //case v:Val => ListMap.empty -> (vctx get v map nb.readVal getOrElse extrudedHandle(v))
            case v:Val => vctx get v map (ListMap.empty -> nb.readVal(_) : recRet) getOrElse {
              //(ListMap(readVal(v))) ->)
              val v1 = bindVal("_"+v.name, v.typ, Nil)
              val w = recv(v1)
              ListMap(rep->w) -> nb.readVal(w)
            }
            case Abs(p,b) =>
              //println(s"Abs($p,$b)")
              val v = recv(p)
              val as->r = rec(b)(vctx + (p->v), cctx)
              //println(s"/Abs")
              as->newBase.lambda(v::Nil,r)
            case MethodApp(self, mtd, targs, argss, tp) =>
              val sas->sr = rec(self)
              var ass = sas
              val newArgss = argss.map(_.map(nb){r =>
                val as->sr = rec(r)
                ass ++= as
                sr
              })
              ass->nb.methodApp(sr, recm(mtd), targs.map(rect), newArgss, rect(tp))
            case Ascribe(r, typ) =>
              rec(r) ||> (_2 = nb.ascribe(_,rect(typ)))
            case Module(r, name, typ) =>
              rec(r) ||> (_2 = nb.module(_,name,rect(typ)))
            case ld: LeafDef => ListMap.empty->apply(ld)
          }
        }}
        //Sdebug(s"<"+res._1.map("\t"+_).mkString("\n"))
        Sdebug(s"< "+res._1.mkString("\n  "))
        res
      }
      val lsm->r = rec(rep)(Map.empty,CCtx.empty)
      //assert(lsm.isEmpty,lsm) // TODO B/E
      if(lsm.nonEmpty) System.err.println("NON-EMPTY-LIST!! "+lsm) // TODO B/E
      scheduled.valuesIterator.foldRight(r){case ((funsym,fun,args,typ),r) => nb.letin(funsym,fun,r,typ)}
      //???
    }
    
    override protected def recv(v: Val): newBase.BoundVal =
      bound.getOrElse(v, super.recv(v)) // FIXME calling super.recv in the wrong order messes with some interpreters, such as BaseInterpreter
    
  }
  override def reinterpret(r: Rep, NewBase: squid.lang.Base)(ExtrudedHandle: BoundVal => NewBase.Rep = DefaultExtrudedHandler): NewBase.Rep =
    new Reinterpreter {
      val newBase: NewBase.type = NewBase
      override val extrudedHandle = ExtrudedHandle
    } apply r
  
  
  /** Path-sensitive free-variables computation.
    * May return false-positives! Indeed, when a branch cannot be resolved we return the FVs of both sides... */
  // TODO propagate assumptions to reduce false-positives?
  def freeVals(rep: Rep)(implicit cctx: CCtx): Set[Val] = rep.node match {
    case Box(ctrl,res) => withCtrl_?(ctrl) match {
      case Some(c) => freeVals(res)(c)
      case None => freeVals(res) // approximation!
    }
    case Branch(ctrl,cid,thn,els) =>
      mayHaveCid(ctrl,cid) match {
        case Some(b) => freeVals(if (b) thn else els)
        case None => freeVals(thn) ++ freeVals(els) // approximation!
      }
    //case cn@ConcreteNode(v: Val) => freeVals(cn.mkRep)
    //case ConcreteNode(MirrorVal(v)) => Set single v
    case ConcreteNode(d) => d.children.flatMap(freeVals).toSet
  }
  
}

