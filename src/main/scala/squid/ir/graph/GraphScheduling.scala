package squid.ir
package graph

import squid.utils._
import squid.utils.CollectionUtils.MutSetHelper

import scala.collection.mutable

trait GraphScheduling extends AST { graph: Graph =>
  
  import squid.quasi.MetaBases
  import squid.utils.meta.{RuntimeUniverseHelpers => ruh}
  import ruh.sru
  
  override def runRep(rep: Rep): Any = eval(rep)
  
  type CCtx = Map[CallId, Int]
  def hasCall(cid: CallId)(implicit cctx: CCtx) = cctx(cid) > 0
  def withCall(cid: CallId)(implicit cctx: CCtx) = cctx + (cid -> (cctx(cid)+1))
  def withoutCall(cid: CallId)(implicit cctx: CCtx) = {
    val old = cctx(cid)
    require(old > 0)
    cctx + (cid -> (old-1))
  }
  def emptyCCtx: CCtx = Map.empty.withDefaultValue(0)
  
  object EvalDebug extends PublicTraceDebug
  
  /** Note: currently creates garbage Reps – could save edges and restore them later... */
  def eval(rep: Rep) = {
    import EvalDebug.{debug=>edebug}
    def rec(rep: Rep)(implicit cctx: CCtx, vctx: Map[Val,ConstantLike]): ConstantLike =
    edebug(s"Eval $rep [${cctx.mkString(",")}] {${vctx.mkString(",")}}") thenReturn EvalDebug.nestDbg(rep.dfn match {
      case Call(cid,res) => rec(res)(withCall(cid), vctx)
      case Arg(cid,cbr,els) if hasCall(cid) => rec(cbr)(withoutCall(cid), vctx)
      case Arg(cid,cbr,els) => assert(cctx(cid) == 0); rec(els)
      case Abs(p,b) =>
        Constant((a: Any) => rec(b)(cctx,vctx+(p->Constant(a))).value)
      case v: Val => vctx(v)
      case Ascribe(r, tp) => rec(r)
      case c: ConstantLike => c
      case bd: BasicDef =>
        val (reps,bound) = bd.reps.map(r => r -> r.bound.copy()()).unzip
        val curried = bound.foldRight(bd.rebuild(bound.map(readVal)).toRep)(abs(_,_))
        val fun = scheduleAndRun(curried)
        reps.foldLeft(fun){case(f,a) => f.asInstanceOf[Any=>Any](rec(a).value)} into Constant
    }) also (r => edebug(s"~> "+r))
    rec(rep)(emptyCCtx,Map.empty).value
  }
  
  def scheduleAndRun(rep: Rep): Any = SimpleASTBackend runRep rep |> treeInSimpleASTBackend
  def scheduleAndCompile(rep: Rep): Any = SimpleASTBackend compileRep rep |> treeInSimpleASTBackend
  
  protected def treeInSimpleASTBackend(rep: Rep) = reinterpret(rep, SimpleASTBackend) { bv =>
    System.err.println(s"Found a free variable! ${bv} ${edges get bv}")
    SimpleASTBackend.bindVal(bv.name,bv.typ.asInstanceOf[SimpleASTBackend.TypeRep],Nil).toRep
  }
  protected def treeInANFBackend(rep: Rep) = reinterpret(rep, ANFBackend) { bv =>
    System.err.println(s"Found a free variable! ${bv} ${edges get bv}")
    ANFBackend.bindVal(bv.name,bv.typ.asInstanceOf[ANFBackend.TypeRep],Nil).toRep
  }
  
  override def scalaTreeIn(MBM: MetaBases)(SRB: MBM.ScalaReflectionBase, rep: Rep, ExtrudedHandle: BoundVal => MBM.u.Tree): MBM.u.Tree =
    SimpleASTBackend.scalaTreeIn(MBM)(SRB, treeInSimpleASTBackend(rep), bv => {
        System.err.println(s"Found a free variable! ${bv}")
        import MBM.u._
        //q"""scala.sys.error(${bv.name}+" not bound")"""
        q"""squid.lib.unbound(${bv.name})"""
      })
  
  // FIXME: use proper CCtx instead of a simple Set[CallId]
  trait Reinterpreter extends super.Reinterpreter {
    // TODO use an intermediate representation that understands a higher-level concept of functions and:
    //   - can merge several control-flow boolean parameters into one integer flag parameter
    //   - can prune useless branches (to refine)
    // TODO the target IR should defunctionalize the continuations we generate here...
    //   or at least we should use bool or int flag params when possible
    // TODO detect effects or costly computations and do not lift them out of nested expr
    //   if we're not sure the path must be taken
    //   or if the path isn't 'pure', in the case of effects (if the path's effects interact with the arg's effects)
    // TODO allow passing in args only closed in some of the calls... the other calls passing null...
    //   again, should only be done if the path is sure to be taken... assuming we're coming from that call!
    // TODO handle mutually-recursive functions
    
    override val recoverLetIns = false
    
    val liveVals = mutable.Set.empty[Val]
    val pointers = mutable.Map.empty[Rep,mutable.Set[Rep]]
    val alwaysBoundAt = mutable.Map.empty[Rep,Set[Val]]
    
    def applyTopLevel(r: Rep) = {
      
      assert(liveVals.isEmpty)
      liveVals ++= iterator(r).collect{case Rep(Abs(v,_)) => v}
      
      val analysed = mutable.HashSet.empty[Rep]
      def analyse(r: Rep): Unit = /*println(s"> Analayse $r") thenReturn*/ analysed.setAndIfUnset(r, {
        pointers.getOrElseUpdate(r,mutable.Set.empty)
        def addptr(m:Rep): Unit = {
          //println(s"Add ${r.bound} -> ${m} ${pointers.get(m)}")
          if (!m.dfn.isSimple)
          pointers.getOrElseUpdate(m,mutable.Set.empty) += r
          analyse(m)
        }
        r.dfn match {
          case MethodApp(self, mtd, targs, argss, tp) =>
            addptr(self)
            argss.iterator.flatMap(_.reps.iterator).foreach(addptr)
          case Abs(_, b) => addptr(b)
          case Ascribe(r, _) => addptr(r)
          case Module(r, _, _) => addptr(r)
          case Call(_, res) => addptr(res)
          case Arg(_, cbr, els) => addptr(cbr); addptr(els)
          case _: LeafDef =>
        }
      })
      assert(pointers.isEmpty)
      analyse(r)
      
      val analysed2 = mutable.HashSet.empty[(Rep,Set[Val])]
      def analyse2(r: Rep)(implicit vctx: Set[Val]): Unit = analysed2.setAndIfUnset(r -> vctx, r.dfn match {
        case MethodApp(self, mtd, targs, argss, tp) =>
          analyse2(self)
          argss.iterator.flatMap(_.reps.iterator).foreach(analyse2)
        case Abs(v, b) => analyse2(b)(vctx + v)
        case Ascribe(r, _) => analyse2(r)
        case Module(r, _, _) => analyse2(r)
        case Call(_, res) => analyse2(res)
        case Arg(_, cbr, els) => analyse2(cbr); analyse2(els)
        case _: LeafDef =>
      })
      analyse2(r)(Set.empty)
      assert(alwaysBoundAt.isEmpty)
      alwaysBoundAt ++= analysed2.groupBy(_._1).mapValues(_.unzip._2.reduce(_ & _))
      //println(s"alwaysBoundAt: ${alwaysBoundAt.map({case(k,v)=>s"\n [${pointers(k).size}] \t${v} \t${k.bound}"}).mkString}")
      
      val res = apply(r)
      val rtyp = rect(r.typ)
      functions.foldLeft(res) {
        case (acc, f -> ((nf,_))) => newBase.letin(f.bound |> bound, nf, acc, rtyp)
      }
    }
    def scheduleFunction(r: Rep): Seq[(Set[CallId],Rep)] -> newBase.Rep = {
      
      //println(s"> Schfun $r")
      
      cctx ::= mutable.Set.empty
      val oldvctx = vctx
      vctx = Set.empty
      params ::= mutable.Buffer.empty
      curCall ::= r
      val res = params.head -> apply(r.dfn)
      vctx = oldvctx
      cctx = cctx.tail
      params = params.tail
      curCall = curCall.tail
      res
    }
    
    var cctx: List[mutable.Set[CallId]] = mutable.Set.empty[CallId]::Nil
    var vctx: Set[Val] = Set.empty
    var params: List[mutable.Buffer[(Set[CallId],Rep)]] = Nil
    var curCall: List[Rep] = Nil
    /** Use a ListMap to retain order, otherwise we might refer to functions not yet defined. */
    var functions = mutable.ListMap.empty[Rep,newBase.Rep->(()=>newBase.Rep)]
    
    def apply(r: Rep): newBase.Rep = {
      //println(s"Schedule ${r.bound} [${cctx.head.mkString(",")}] {${vctx.mkString(",")}} $r")
    if (pointers.isEmpty) applyTopLevel(r) else r.dfn match {
    //case d @ Abs(bv, _) =>
    //  assert(!vctx(bv)) // TODO if so, refresh
    //  vctx += bv
    //  super.apply(d) alsoDo {vctx -= bv}
    case Call(cid, res) =>
      assert(!cctx.head(cid)) // FIXME use a real CCtx with multiplicities
      cctx.head += cid
      apply(res) alsoDo {cctx.head -= cid}
    // The reason the following is wrong is that a PassArg may ACTUALLY influence control-flow by neutering Arg nodes
    // that are nested in 'res' – it's always been its primary purpose! – and from here we don't see the full calling
    // context (to do so, we'd need an analysis of which calls may arrive at the current curCall)
    /*
    case PassArg(cid, res) =>
      val putBack = cctx.head contains cid
      cctx.head -= cid
      apply(res) alsoDo {if (putBack) cctx.head += cid}
    */
    case Arg(cid, cbr, els) => // FIXME handle cycles in els and cbr
      //println(s"Arg $r")
      //if (cctx.head.nonEmpty) { // FIXedME
        //assert(ctx.head.last === cid)
        if (cctx.head(cid)) {
          cctx.head -= cid
          apply(cbr) alsoDo {cctx.head += cid}
        //} else apply(els)
      }
      //else apply(els.get.bound/*TODO B/E*/) alsoDo {params.head += els.get}
      else if (params.isEmpty) apply(els) // if we're not scheduling a function, there is no one to delegate arguments to...
      //else bindOrGet(r.bound) alsoDo {params.head += r}
      else {
        //if (!(r.dfn.unboundVals subsetOf params.head._1)) println(s"Oops: $r ${r.dfn.unboundVals} ill-scoped in ${params.head._1}")
        //println(s"Arg $r ${r.dfn.unboundVals} scoped in ${params.head._1}")
        //if (r.dfn.unboundVals subsetOf params.head._1) recv(r.bound) |> newBase.readVal alsoDo {params.head._2 += r}
        val rfv = r.freeVals filter liveVals
        val boundInCall = curCall.headOption map alwaysBoundAt getOrElse Set.empty
        //if (vctx.isEmpty || (rfv subsetOf vctx)) recv(r.bound) |> newBase.readVal alsoDo {
        if (vctx.isEmpty || (rfv subsetOf boundInCall)) recv(r.bound) |> newBase.readVal alsoDo {
          //println(s"> New param: $r  ${rfv} ${vctx}")
          
          //params.head._2 += r}
          //params.head._2 += cctx.head.foldRight(r)(Call(_,_).toRep)}
          params.head += (cctx.head.toSet->r)}
        else { // cannot extract impl node as a parameter, because it refers to variables not bound at all calls
          // FIXME should use flow analysis to know 'variables not bound at all calls' --- and also other things?
          // TODO also do this if the expression is effectful or costly and we're in a path that may not always be taken! -- unless ofc we're targetting a pure lazy language like Haskell
          //println(s"Oops: $r ${rfv} ill-scoped in ${params.head._1} ${rfv -- params.head._1 toList}")
          
          //println(s"> Oops: ${rfv} not in ${boundInCall} for:  ${r.showGraphRev}")
          
          // Generate boolean params to know which subexpression to compute;
          // Problem: expressions under an Arg may contain variables only bound in some of the _callers_, so using them
          // here won't work with traditional PL semantics (it could be made to work with dynamic scopes or global stack variables)
          /*
          val p = bindVal("Φ", Predef.implicitType[Bool].rep, Nil)
          val rtyp = r.typ |> rect
          //params.head._2 += Arg(cid, const(true), Some(const(false))).toRep
          params.head._2 += Rep.bound(p, Arg(cid, const(true), Some(const(false))))
          newBase.methodApp(newBase.staticModule("squid.lib.package"),
            newBase.loadMtdSymbol(newBase.loadTypSymbol("squid.lib.package$"), "IfThenElse"),
            rtyp::Nil, newBase.Args(recv(p) |> newBase.readVal,
              newBase.byName(apply(cbr)),
              //newBase.byName(apply(Arg(cid,cbr,None))), // SOF
              newBase.byName(els.fold(???)(apply))
            )::Nil,rtyp)
          */
          // Solution for now: resort to using lambdas, and assume a later defunc phase will remove them... (should be easy-ish in these cases)
          ///*
          //val extrudedVals = r.dfn.unboundVals -- params.head._1 toList;
          //assert(extrudedVals.nonEmpty)
          //val extrudedVals = r.dfn.unboundVals -- vctx toList;
          val extrudedVals = vctx.toList
          val k = bindVal("κ", lambdaType(extrudedVals.map(_.typ), r.typ), Nil)
          //val rtyp = r.typ |> rect
          val appSym = ruh.FunctionType.symbol(extrudedVals.size).toType.member(sru.TermName("apply")).asMethod
          //newBase.mapp(k, appSym, rtyp)()(extrudedVals:_*)
          vctx += k
          recv(k)
          apply(mapp(k|>readVal, appSym, r.typ)()(Args(extrudedVals.map(readVal):_*))) alsoDo {
            vctx -= k
            params.head += cctx.head.toSet -> Rep.bound(k, Arg(cid, lambda(extrudedVals, cbr), lambda(extrudedVals, els)))
          }
          //apply(mapp(k|>readVal, appSym, r.typ)()(Args(extrudedVals.map(readVal):_*)) also(r=>println(r.showGraphRev))) alsoDo {vctx -= k}
          //*/
          //val extrudedVals1 = cbr.dfn.unboundVals -- params.head._1 toList;
          //val extrudedVals2 = r.dfn.unboundVals -- params.head._1 toList;
          //assert(extrudedVals.nonEmpty)
          //val k = bindVal("κ", lambdaType(extrudedVals.map(_.typ), r.typ), Nil)
          ////val rtyp = r.typ |> rect
          //params.head._2 += Rep.bound(k, Arg(cid, lambda(extrudedVals, cbr), Some(lambda(extrudedVals, ))))
          //val appSym = ruh.FunctionType.symbol(extrudedVals.size).toType.member(sru.TermName("apply")).asMethod
          ////newBase.mapp(k, appSym, rtyp)()(extrudedVals:_*)
          //apply(mapp(k.toRep, appSym, r.typ)()(Args(extrudedVals.map(rep):_*)))
          
        }
      }
    case _ =>
      //val addedBinding = r.dfn |>? { case Abs(v, _) => vctx += v; v }
      //println(s"> Apply $r (${pointers(r).map(_.bound)})")
      //(if (pointers(r).size > 1) {
      if (pointers.get(r).exists(_.size > 1)) { // pointers may not be defined on newly created nodes
        functions.getOrElseUpdate(r, {
        //functions.getOrElse(r, {
        //  println(s"> Making function for ${r.bound}...")
          val rtyp = rect(r.typ)
          val params -> res = scheduleFunction(r)
          
          //println(s"> Function: $params -> $res")
          
          val fdef =
          params.foldRight(res) {
            case ((cids,p), acc) => newBase.lambda(recv(p.bound)::Nil, acc)//(rtyp)
          }
          //bound += r.bound -> newBase.bindVal(r.bound.name, rtyp, Nil)
          //params.foldRight(res) {
          val rv = r.bound |> recv
          val call = () =>
          params.foldLeft(rv |> newBase.readVal) {
            case (acc,(cids,param)) =>
              //newBase.app(acc, recv(p) |> newBase.readVal)(rtyp)
              assert(cids & cctx.head isEmpty, s"$cids & ${cctx.head}") // Q: really? won't it i general be a superset?
              cctx.head ++= cids
              newBase.app(acc, param |> apply)(rtyp) alsoDo {cctx.head --= cids}
              //newBase.app(acc, param |> recv |> newBase.readVal)(rtyp)
          }
          fdef -> call
          //res
        })._2()
      }
      else apply(r.dfn)
      //) alsoDo {addedBinding.foreach(vctx -= _)}
    }} 
    //.also { res => println(s"Scheduled ${r.bound} = $res") }
    
    override def apply(d: Def) = d match {
      case d @ Abs(bv, _) =>
        //assert(!vctx(bv), s"$bv in $vctx") // TODO if so, refresh  // FIXME hygiene?!
        vctx += bv
        super.apply(d) alsoDo {vctx -= bv}
      case _ => super.apply(d)
    }
    //protected def bindOrGet(v: Val) = bound.getOrElseUpdate(v, recv(v)) |> newBase.readVal
    override protected def recv(v: Val): newBase.BoundVal =
      bound.getOrElse(v, super.recv(v)) // FIXME calling super.recv in the wrong order messes with some interpreters, such as BaseInterpreter
  }
  override def reinterpret(r: Rep, NewBase: squid.lang.Base)(ExtrudedHandle: (BoundVal => NewBase.Rep) = DefaultExtrudedHandler): NewBase.Rep =
    new Reinterpreter {
      val newBase: NewBase.type = NewBase
      override val extrudedHandle = ExtrudedHandle
    } applyTopLevel r
  
}
