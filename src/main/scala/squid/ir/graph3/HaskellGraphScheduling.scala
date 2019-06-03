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

import scala.collection.mutable
import squid.ir.graph.SimpleASTBackend

/** New scheduling algorithm that supports recursive definitions and Haskell output. */
trait HaskellGraphScheduling { graph: HaskellGraph =>
  /* Notes.
  
  Problems with fixed-point scheduling of recursive graphs:
   – Branch duplicity: sometimes, we end up scheduling to mutually-recursive branches that should really be the same:
     they simply recurse into each other, and could be simplified to the same node, as in for example $8 and $5 in the
     graph of `rec7 f () = f (rec7 f ())`, which is:
        rec7 = {f_a$4 => $11};
        $11 = {ds_d$6 => $10};
        $10 = $7.case($f);
        $7 = (ds_d$62 ? 🚫() ¿ ds_d$6);
        $f = scala.Tuple2.apply((),$e);
        $e = $8 ds_d$62↑[f_a$41↑]$10;
        $8 = ([↓]f_a$41 ? ↓;🚫;↓$5 ¿ ↓f_a$4);
        $5 = (f_a$41 ? 🚫$8 ¿ f_a$4);
     This is due to the two occurrences of `f` happening under two different 'expression scopes'.
     We end up scheduling two alternating arguments _1 and _3 for the worker def, which is unnecessary:
        _0(# _1, _2, _3 #) = (case _2 of {() -> (_3 (_0(# _3, (), _1 #)))})
        rec7 = (\f_a -> (\ds_d -> (_0(# f_a, ds_d, f_a #))))
     Naive approaches at considering such two branches the same obviously (in retrospect) end up in infinite recursions.
     So it seems the only thing to do is to perform some analysis, either on the graoh itself (but that seems hard and
     probably neccessarily incomplete) or on the generated defs for post-hoc simplification.
  
  */
  
  object HaskellScheduleDebug extends PublicTraceDebug
  import HaskellScheduleDebug.{debug=>Sdebug}
  
  //import mutable.{Map => M}
  import mutable.{ListMap => M}
  
  class RecScheduler(nb: squid.lang.Base) {
    /** Left: propagated argument; Right: provided argument */
    type TrBranch = Either[(Control,Branch),SchedulableRep]
    
    val scheduledReps = M.empty[Rep,SchedulableRep]
    
    private val nameCounts = mutable.Map.empty[String, Int]
    private val nameAliases = mutable.Map.empty[Val, String]
    def printVal(v: Val): String = nameAliases.getOrElseUpdate(v, {
      val nme = v.name takeWhile (_ =/= '$')
      val cnt = nameCounts.getOrElse(nme, 0)
      nameCounts(nme) = cnt+1
      if (nme.isEmpty) "_"+cnt else
      cnt match {
        case 0 => nme
        case 1 => nme+"'"
        case n => nme+"'"+n
      }
    })
    
    abstract case class SchDef(sr: SchedulableRep, branchParams: List[Val], subBindings: List[Val->SchShareable], body: SchExp) {
      private implicit def scope: Set[Val] = Set.empty
      val name = sr.rep.bound |> printVal
      def freeVars: Set[Val] = body.freeVars -- subBindings.map(_._1)
      def allChildren = subBindings.flatMap(_._2.allChildren) ++ body.allChildren
      lazy val nestingScope: Set[Val] = freeVars & sr.maximalSharedScope.get
      lazy val valParams: List[Val] = (freeVars -- sr.maximalSharedScope.get).toList.sortBy(printVal)
      lazy val params = valParams ++ branchParams
      
      val (topSubBindings,nestedSubBindings) =
        subBindings.partition(_._2.localRefs.isEmpty)
      
      subBindings.foreach(sb => assert(sb._2.localRefs.forall(branchParams.contains)))
      def toHs: String = {
        val paramList = if (params.isEmpty) "" else s"(# ${params.map(printVal).mkString(", ")} #)"
        s"${topSubBindings.map(sb => s"${sb._1|>printVal} = ${sb._2.toHs}\n").mkString}$name$paramList = ${
          if (nestedSubBindings.isEmpty) "" else {
            s"let ${nestedSubBindings.map(sb => s"\n    ${sb._1|>printVal} = ${sb._2.toHs}").mkString("")}\n  in "
          }
        }${body.toHs}"
      }
    }
    object SchDef {
      def apply(sr: SchedulableRep, branchParams: List[Val], body: SchExp): SchDef = {
        val (bod,sharedExps) = share(body, sr.rep.bound.name+"_sub")
        new SchDef(sr, branchParams, sharedExps.sortBy(_._1.name), bod){}
      }
    }
    
    sealed abstract class SchShareable extends SchExp
    sealed abstract class SchExp {
      
      // TODO maintain scope for 'case' and let bindings!(?)
      def toHs(implicit scope: Set[Val]): String
      
      def subExps: List[SchShareable] = this match {
        case sl: SchLam => sl :: Nil
        case SchArg(arg,Nil) => arg.subExps
        case SchArg(arg,xvs) => arg.subExps.filterNot(xvs exists _.localRefs)
        case SchCall(sr,Nil,_) => Nil
        case s: SchShareable => s :: children.flatMap(_.subExps).toList  
        case  _: SchVar | _: SchConst | _: SchParam => Nil
      }
      // FIXME order of f applications..?
      def mapSubExprs(f: SchShareable => SchExp): SchExp = {this match {
        case _: SchVar | _: SchConst | _: SchParam => this
        case SchApp(lhs, rhs) => f(SchApp(lhs.mapSubExprs(f), rhs.mapSubExprs(f)))
        case SchCase(scrut, arms) => f(SchCase(scrut.mapSubExprs(f), arms.map{case(a,b,c)=>(a,b,c.mapSubExprs(f))}))
        case SchCall(sr, args, rec) => f(SchCall(sr, args.map(a => a._1.mapSubExprs(f) -> a._2), rec))
        case lam: SchLam => f(lam)
        case SchArg(b, xvs) => f(SchArg(b.mapSubExprs(f), xvs))
      }}
      
      lazy val localRefs: Set[Val] = this match {
        case SchLam(p, bs0, bs1, b) =>
          b.localRefs ++ bs1.flatMap(_._2.localRefs) -- bs0.map(_.sr.rep.bound) -- bs1.map(_._1)
        case SchParam(v, possiblyExtruded) =>
          possiblyExtruded.toSet + v
          // ^ Note: there might be false positives... only way to know is to compute the actual 'extruded' variables as
          //   in SchParam#toHs, but that can only be done after we have fixed the scopes of everything, and we're
          //   currently using localRefs before that, from subExps...
        case SchArg(b, xvs) =>
          b.localRefs -- xvs // only seems to make sense, since we consider the variable references in SchParam
        case _ => children.flatMap(_.localRefs).toSet
      }
      lazy val freeVars: Set[Val] = this match {
        case SchVar(v) => Set.empty + v
        case SchLam(p, _, bs, b) =>
          //b.freeVars - p
          //b.freeVars ++ bs.flatMap(_._2.freeVars) - p
          b.freeVars ++ bs.flatMap(_._2.freeVars) - p -- bs.iterator.map(_._1)
        case SchCase(e, es) => e.freeVars ++ es.flatMap(c => c._3.freeVars -- c._2)
          
        case SchArg(b, xvs) => b.freeVars -- xvs
        /* This seemed to make sense, but in fact it doesn't!
           Indeed, in both SchParam and SchArg cases, we filter xvs based on the current actual scope. */
        // case SchParam(v, xvs) => xvs.toSet
          
        case c @ SchCall(_, _, rec) => c.children.flatMap(_.freeVars).toSet -- rec
          
        case _ => children.flatMap(_.freeVars).toSet
      }
      def children: Iterator[SchExp] = this match {
        case _: SchVar | _: SchConst | _: SchParam => Iterator.empty
        case SchApp(lhs, rhs) => Iterator(lhs, rhs)
        case SchCase(scrut, arms) => Iterator(scrut) ++ arms.iterator.map(_._3)
        case SchCall(_, args, _) => args.iterator.map(_._1)
        case SchLam(p, bs0, bs1, b) => Iterator(b) ++ bs0.iterator.map(_.body) ++ bs1.iterator.map(_._2) // Q: really include the bindings here?
        case SchArg(b, _) => Iterator(b)
      }
      def allChildren: Iterator[SchExp] = Iterator(this) ++ children.flatMap(_.allChildren)
      
      //override def toString = s"[|$toHs|]"
    }
    /** `value` can be a Constant, a CrossStageValue, or a StaticModule */
    case class SchConst(value: Any) extends SchExp {
      def toHs(implicit scope: Set[Val]) = value match {
        case Constant(n: Int) => n.toString
        case Constant(s: String) => '"'+s+'"'
        case CrossStageValue(n: Int, UnboxedMarker) => s"$n#"
        case sm: StaticModule => sm.fullName
      }
    }
    case class SchApp(lhs: SchExp, rhs: SchExp) extends SchShareable {
      def toHs(implicit scope: Set[Val]) = s"(${lhs.toHs} ${rhs.toHs})"
    }
    abstract case class SchLam(param: Val, var bindings: List[SchDef], subBindings: List[Val->SchShareable], body: SchExp) extends SchShareable {
      def toHs(implicit scope: Set[Val]) = (scope + param) |> { implicit scope =>
        /* Note that some of the subBindings will not even reference the lambda's parameter, because we only learn about
           actual continuation parameters later on. It would be best to do lambda lifting separately, in a later
           phase... or just trust the backend (GHC) to do it by itself. */
        s"(\\${param |> printVal} -> ${
          if (bindings.isEmpty && subBindings.isEmpty) "" else
          s"let { ${(bindings.map(_.toHs) ++ subBindings.map(sb => s"${sb._1 |> printVal} = ${sb._2.toHs}")).mkString("; ")} } in "
        }${body.toHs})"
      }
    }
    object SchLam {
      def apply(param: Val, bindings: List[SchDef], body: SchExp): SchLam = {
        val (bod,sharedExps) = share(body, "x")
        new SchLam(param, bindings, sharedExps, bod){}
      }
    }
    def share(sche: SchExp, nameBase: String): (SchExp, List[Val->SchShareable]) = {
      val sharedExps = mutable.Map.empty[SchShareable, Opt[Val]]
      sche.subExps.foreach(se => sharedExps.get(se) match {
        case Some(Some(_)) =>
        case Some(None) => sharedExps(se) = Some(bindVal(nameBase+freshName,Any,Nil))
        case None =>  sharedExps(se) = None
      })
      //println(s"SharedExps[$nameBase]: ${sharedExps}")
      (sche.mapSubExprs(s => sharedExps.get(s).flatten.fold[SchExp](s)(SchVar)),
        sharedExps.iterator.collect { case (se, Some(v)) => v -> se }.toList)
    }
    case class SchVar(v: Val) extends SchExp {
      val vStr = v |> printVal
      def toHs(implicit scope: Set[Val]) = vStr
    }
    case class SchParam(v: Val, possiblyExtruded: List[Val]) extends SchExp {
      // TODO use proper unboxed tuple arguments for the continuations
      def toHs(implicit scope: Set[Val]) = {
        val extruded = possiblyExtruded.filter(scope)
        if (extruded.isEmpty) printVal(v) else
        "{-P-}("+printVal(v)+extruded.map(printVal).mkString("(",",",")")+")"
      }
    }
    case class SchArg(body: SchExp, possiblyExtruded: List[Val]) extends SchShareable {
      def toHs(implicit scope: Set[Val]) = {
        val extruded = possiblyExtruded.filterNot(scope)
        if (extruded.isEmpty) body.toHs else
        s"{-A-}\\(${extruded.map(printVal).mkString(",")}) -> " + body.toHs(scope ++ extruded)
      }
    }
    /** @args lists the arguments passed in the call,
      *       and _which variables were extruded on the way from these arguments' branch occurrences_, which is
      *       important in order to determine which variables in the current scope the argument is supposed to be
      *       referring to directly, as opposed to via a continuation (for those extruded). */
    case class SchCall(sr: SchedulableRep, args: List[SchExp -> List[Val]], rec: Opt[Val]) extends SchShareable {
      val name = sr.rep.bound |> printVal
      def toHs(implicit scope: Set[Val]) = {
        val argStrs = args.map(a => a._1.toHs(scope -- a._2))
        val valArgs = sr.valParams.fold("??"::Nil)(_.map(printVal))
        val allArgStrs = valArgs ++ argStrs
        val body =
          if (allArgStrs.isEmpty) name else
          s"($name(# ${allArgStrs.mkString(", ")} #))"
        rec.fold(body) { v => val vstr = printVal(v); s"(let{-rec-} $vstr = $body in $vstr)" }
      }
    }
    case class SchCase(scrut: SchExp, arms: List[(String,List[Val],SchExp)]) extends SchShareable {
      def toHs(implicit scope: Set[Val]) = s"(case ${scrut.toHs} of {${arms.map { case (con, vars, rhs) =>
          (if (con.head.isLetter || con === "[]" || con.head === '(') con else s"($con)") +
            vars.map(printVal).map{" "+_}.mkString + s" -> ${rhs.toHs}"
      }.mkString("; ")}})"
    }
    
    class SchedulableRep private(val rep: Rep) {
      var backEdges: mutable.Buffer[Control -> SchedulableRep] = mutable.Buffer.empty
      scheduledReps += rep -> this
      val children = rep.node.children.map(c => scheduledReps.getOrElseUpdate(c, new SchedulableRep(c))).toList
      children.foreach(_.backEdges += Id -> this)
      
      /** `branches` maps each branch instance to what it's transformed to,
        * along with the original branch val (for argument-naming purposes), and the extruded variables. */
      val branches: M[(Control,Branch),(TrBranch,Val,List[Val])] = rep.node match {
        case br: Branch => M((Id,br) -> (Left(Id,br),br.cid.v.renew,Nil))
        case _ => M.empty
      }
      
      var usages = 0
      
      /** The most specific scope under which all references to this def appear. Top-level defs have the emtpy set. */
      var maximalSharedScope = Option.empty[Set[Val]]
      
      /** Add a scope from which this definition is referenced; returns whether the maximalSharedScope changed. */
      def registerScope_!(scp: Set[Val]): Bool = maximalSharedScope match {
        case Some(scp0) =>
          val inter = scp0 & scp
          maximalSharedScope = Some(inter)
          inter =/= scp0
        case None =>
          maximalSharedScope = Some(scp)
          true
      }
      
      var valParams = Option.empty[List[Val]]
      
      def shouldBeScheduled = rep.node match {
        case _: Branch => false
        case ConcreteNode(d) => !d.isSimple
        case _ => true
      }
      
      type BranchCtx = Map[(Control,Branch),(TrBranch,Val,List[Val])]
      def xtendBranchCtx(traversed: SchedulableRep)(implicit brc: BranchCtx): BranchCtx =
        // We need to take over the old `brc` entries, as we may uncover branches nested under other branches
        brc ++ traversed.branches.map {
          case bc -> ((Left(cb2),v,xvs)) => bc -> brc(cb2)
          case r => r
        }
      type CallCtx = Map[SchedulableRep -> BranchCtx, Lazy[Val]]
      
      def printDef(dbg: Bool): String = printDefWith(dbg)(branches.toMap, Map.empty)
      def printDefWith(dbg: Bool)(implicit brc: BranchCtx, curCalls: CallCtx): String = rep.node match {
      case _: Branch => rep.bound.toString // really?
      case _ =>
        if (brc.nonEmpty) Sdebug(s"! ${rep}"+
          s"\n\t${branches.map(kv => s"${kv._1}>>${kv._2._1}[${kv._2._2}]").mkString}"+
          s"\n\t${brc.map(kv => s"[${kv._2._2}] ${kv._1}  >->  ${kv._2._1}").mkString(s"\n\t")}"
        ) else Sdebug(s"! ${rep}")
        val res = HaskellScheduleDebug.nestDbg(new HaskellDefPrettyPrinter(showInlineCF = false) {
          override def apply(n: Node): String = if (dbg) super.apply(n) else n match {
            case Box(_, body) => apply2(body)
            case ConcreteNode(d) => apply(d)
            case _: Branch => die
          }
          override def apply(r: Rep): String = apply2(r)
          def apply2(r: Rep)(implicit brc: BranchCtx, curCalls: Map[SchedulableRep->BranchCtx,Lazy[Val]]): String = {
            Sdebug(s"? ${r}")
            val sr = scheduledReps(r)
            def printArg(cb: (Control, Branch), pre: String)(implicit curCalls: Map[SchedulableRep->BranchCtx,Lazy[Val]]): String = brc.get(cb).map {
                case (Left(_),v,xvs) =>
                  Sdebug(s"Propagating parameter ${v}")
                  v.toString + (if (xvs.isEmpty) "" else xvs.mkString("(",",",")"))
                case (Right(r),v,xvs) =>
                  Sdebug(s"Making argument ${v} = ${r}")
                  pre + (if (xvs.isEmpty) "" else s"\\(${xvs.mkString(",")}) -> ") + apply2(r.rep)
              }.getOrElse {
                if (!dbg) /*System.err.*/println(s"/!!!\\ ERROR: at ${r.bound}: could not find cb $cb in:\n\t${brc}")
                s"${Console.RED}???${Console.RESET}"
              }
            val res = HaskellScheduleDebug.nestDbg (sr.rep.node match {
              case ConcreteNode(d) if d.isSimple => super.apply(d)
              case ConcreteNode(ByName(body)) /*if !dbg*/ => apply2(body)
              case ConcreteNode(MethodApp(_,Tuple2.ApplySymbol,Nil,Args(lhs,rhs)::Nil,_)) /*if !dbg*/ if sr.usages <= 1 =>
                s"{${apply2(lhs)} -> ${apply2(rhs)}}"
              case ConcreteNode(MethodApp(scrut,GetMtd,Nil,Args(Rep(ConcreteNode(StaticModule(con))),Rep(ConcreteNode(Constant(idx))))::Nil,_))
              if !dbg && sr.usages <= 1 =>
                s"${apply2(scrut)}!$con#$idx"
              case b: Branch =>
                assert(sr.branches.size === 1)
                assert(sr.branches.head._1._1 === Id)
                assert(sr.branches.head._1._2 === b)
                printArg((Id,b),"") // FIXME??
              case _ if !dbg && (sr.usages <= 1) =>
                assert(sr.usages === 1)
                Sdebug(s"Inlining ${sr}")
                sr.printDefWith(dbg)(xtendBranchCtx(sr),curCalls)
              case _ =>
                Sdebug(s"Calling ${sr}; curCalls \n\t${curCalls.mkString("\n\t")}")
                val k = sr->brc
                curCalls.get(k) match {
                  case Some(lv) =>
                    Sdebug(s"This is a recursive call to ${lv.value}; curCalls \n\t${curCalls.mkString("\n\t")}")
                    s"[RECURSIVE ${lv.value}]"
                  case None =>
                    s"${sr.rep.bound}(${sr.branches.valuesIterator.collect[String] {
                      case (Left(cb),v,_) =>
                        printArg(cb,s"$v=")(curCalls + (k->Lazy(sr.rep.bound)))
                    }.mkString(",")})"
                }
            })
            Sdebug(s"= ${res}")
            res
          }
        } apply rep.node)
        Sdebug(s"= ${res}")
        res
      }
      
      type CaseCtx = Map[(Rep,String), List[Val]]
      
      def mkHaskellDef: SchExp = mkHaskellDefWith(branches.toMap, Map.empty, Map.empty)
      def mkHaskellDefWith(implicit brc: BranchCtx, enclosingCases: CaseCtx, curCalls: CallCtx): SchExp = {
        def mkDef(d: Def)(implicit enclosingCases: CaseCtx, curCalls: CallCtx): SchExp = d match {
            case v: Val => SchVar(v)
            case c: Constant => SchConst(c)
            case cs: CrossStageValue => SchConst(cs)
            case sm: StaticModule => SchConst(sm)
            case Apply(a,b) => SchApp(mkSubRep(a), mkSubRep(b))
            case ByName(body) => mkSubRep(body) // Q: should we really keep this one?
            case MethodApp(scrut, CaseMtd, Nil, ArgsVarargs(Args(), Args(alts @ _*))::Nil, _) =>
              SchCase(mkSubRep(scrut), alts.map(mkAlt(scrut,_)).toList)
            case MethodApp(scrut,GetMtd,Nil,Args(con,idx)::Nil,_) =>
              (con,idx) |>! {
                case (Rep(ConcreteNode(StaticModule(con))),Rep(ConcreteNode(Constant(idx: Int)))) =>
                  // TODO if no corresponding enclosing case, do a patmat right here
                  SchVar(enclosingCases(scrut->con)(idx))
              }
            case Abs(p,b) => SchLam(p, Nil, mkSubRep(b))  
          }
        def mkAlt(scrut: Rep, r: Rep): (String,List[Val],SchExp) = r.node |>! {
          case ConcreteNode(MethodApp(_,Tuple2.ApplySymbol,Nil,Args(lhs,rhs)::Nil,_)) =>
            lhs.node |>! {
              case ConcreteNode(StaticModule(con)) =>
                val boundVals = List.tabulate(ctorArities(con))(idx => bindVal(s"arg$idx", Any, Nil))
                (con,boundVals,mkSubRep(rhs)(enclosingCases + ((scrut,con) -> boundVals), curCalls))
            }
        }
        def mkSubRep(r: Rep)(implicit enclosingCases: CaseCtx, curCalls: CallCtx): SchExp = {
          val sr = scheduledReps(r)
          def mkArg(cb: (Control,Branch), pre: String)(implicit curCalls: CallCtx): SchExp = brc(cb) match {
            case (Left((_,_)),v,xvs) => SchParam(v, xvs)
            case (Right(r),v,xvs) => SchArg(mkSubRep(r.rep), xvs)
          }
          r.node match {
            case ConcreteNode(d) if d.isSimple => mkDef(d)
            case ConcreteNode(ByName(body)) => mkSubRep(body)
            case b: Branch =>
              assert(sr.branches.size === 1)
              mkArg((Id,b),"") // FIXME??
            case _ if sr.usages <= 1 =>
              //assert(sr.usages === 1)
              //assert(!sr.shouldBeScheduled)
              sr.mkHaskellDefWith(xtendBranchCtx(sr), enclosingCases, curCalls)
            case _ =>
              sr.rep.bound |> printVal
              val k = sr->brc
              curCalls.get(k) match {
                case Some(lv) => SchVar(lv.value)
                case None =>
                  val lv = Lazy.mk(sr.rep.bound.renew, computeWhenShow = false)
                  val args = sr.mkParamsRest.map(p => (mkArg(p._1,"")(curCalls + (k->lv)), p._3))
                  SchCall(sr, args, lv.value optionIf lv.isComputed)
              }
          }
        }
        //if (brc.nonEmpty)
        //  println(s"? ${rep}\n\t${brc.map(kv => s"${kv._1}>>${kv._2._1}[${kv._2._2}]").mkString}")
        rep.node match {
          case ConcreteNode(d) => mkDef(d)
          case Box(_, body) => mkSubRep(body)
          case _: Branch => die
        }
      }
      def haskellDef: SchDef = {
        rep.bound |> printVal
        mkParamsRest.foreach(_._2 |> printVal) // This is done just to get the `printVal`-generated names in the 'right' order.
        mkParamsRest.groupBy(_._2).valuesIterator.foreach {
          case Nil | _ :: Nil =>
          case (_,v,_) :: _ => lastWords(s"Duplicated param: $v (i.e., ${v |> printVal})")
        }
        SchDef(this, mkParamsRest.map(_._2), mkHaskellDef)
      }
      
      def params = valParams.get ++ mkParamsRest.map(_._2)
      def paramRestVals = branches.valuesIterator.collect{case (Left(cb),v,xvs) => v}
      lazy val mkParamsRest: List[((Control,Branch),Val,List[Val])] =
        branches.valuesIterator.collect{case (Left(cb),v,xvs) => (cb,v,xvs)}.toList
      
      def subReps =
        children ++ branches.valuesIterator.collect{case (Right(r),v,xvs) => r}.filter(_.shouldBeScheduled)
      
      override def toString =
        s"${rep.bound}(...) = ${rep.node}"
    }
    object SchedulableRep {
      def apply(rep: Rep): SchedulableRep = new SchedulableRep(rep)
    }
  }
  
  abstract class ScheduledModule {
    def toHaskell(imports: List[String], ghcVersion: String): String
    override def toString: String
  }
  
  def scheduleRec(rep: Rep): ScheduledModule = scheduleRec(PgrmModule("<module>", "?", Map("main" -> rep)))
  def scheduleRec(mod: PgrmModule): ScheduledModule = {
    val sch = new RecScheduler(SimpleASTBackend)
    mod.letReps.foreach(r => sch.printVal(r.bound))
    // ^ reserves non-disambiguated names for all (non-ambiguous) top-level defs
    val root = sch.SchedulableRep(mod.toplvlRep)
    var workingSet = sch.scheduledReps.valuesIterator.filter(_.branches.nonEmpty).toList
    //println(workingSet)
    
    def dbg(msg: => Unit): Unit = ()
    //def dbg(msg: String): Unit = println("> "+msg)
    
    dbg(s">> Starting fixed point... <<")
    while (workingSet.nonEmpty) {
      val sr = workingSet.head
      workingSet = workingSet.tail
      dbg(s"Working set: [${sr.rep.bound}], ${workingSet.map(_.rep.bound).mkString(", ")}")
      //println(sr, sr.branches)
      sr.backEdges.foreach { case (backCtrl, sr2) =>
        dbg(s"  Back Edge: $backCtrl ${sr2.rep.bound} \t \t \t \t = ${sr2.rep.node}")
        sr.branches.valuesIterator.foreach {
        case (Left(cb0 @ (c0,b)), v, xvs) =>
          val c = backCtrl `;` c0
          if (!sr2.branches.contains(cb0)) {
            dbg(s"    New: ${cb0}")
            val (lambdaBound, nde) = sr2.rep.node match {
              case ConcreteNode(abs: Abs) if ByName.unapply(abs).isEmpty =>
                (Some(abs.param), Box(Push(DummyCallId, Id, Id),abs.body))
                // It may be better for sanity checking to use a non-dummy CID here...:
                //(Some(abs.param), Box(Push(new CallId(abs.param), Id, Id),abs.body))
              case n => (None, n)
            }
            def addBranch(cb2: sch.TrBranch) = {
              dbg(s"    Add Branch: $cb2")
              if (cb2.isLeft) workingSet ::= sr2
              if (!nde.isInstanceOf[Branch] && lambdaBound.nonEmpty && cb2.isLeft)
                dbg(s"      !!! Extruded variable! ${lambdaBound}")
              sr2.branches += cb0 -> (cb2, v.renew, if (cb2.isRight) xvs else lambdaBound.toList ::: xvs)
            }
            nde match {
              case _: Branch => // can't bubble up to a branch!
              case Box(ctrl, _) =>
                val newCtrl = ctrl `;` c
                dbg(s"    Consider: $ctrl ; $backCtrl ; $c0 == $newCtrl")
                mayHaveCid(newCtrl `;` b.ctrl, b.cid)(Id) match {
                  case Some(cnd) =>
                    val r3 = sch.scheduledReps(if (cnd) b.lhs else b.rhs)
                    dbg(s"    Add '${b.cid}==$cnd' Edge: ${r3.rep.bound} -[$c]-> ${sr2.rep.bound}")
                    r3.backEdges += c -> sr2 // Important: not newCtrl, as the `ctrl` part will be picked up later
                    workingSet ::= r3
                    addBranch(Right(r3))
                  case None =>
                    assert(lambdaBound.isEmpty || sr2.rep.node.asInstanceOf[ConcreteNode].dfn.isInstanceOf[Abs])
                    addBranch(Left(newCtrl->b))
                }
              case ConcreteNode(d) =>
                assert(lambdaBound.isEmpty || d.isInstanceOf[Abs])
                addBranch(Left(c->b))
            }
            
          }
        case (Right(_),_,_) =>
        }
      }
    }
    dbg {
      dbg(s">> Results <<")
      for (sr <- sch.scheduledReps.valuesIterator; if sr.branches.nonEmpty) {
        dbg(sr.rep.toString)
        dbg(sr.branches.map(kv => s"\t[${kv._2._2}] ${kv._1}  >->  ${kv._2._1 match {
          case Left(cb) => "L:"+cb
          case Right(sr) => "R:("+sr.rep+")"
        }}").mkString("\n"))
      }
      s">> Done! <<"
    }
    
    val isExported = mod.letReps.toSet
    
    val sreps = sch.scheduledReps.valuesIterator.toList.sortBy(sr => (!isExported(sr.rep), sr.rep.bound.name))
    
    /** Set the usage counts of each nodes using a flood-fill algorithm. */
    def count(sr: sch.SchedulableRep): Unit = if (!sr.rep.node.isInstanceOf[Branch]) {
      sr.usages += 1
      if (sr.usages === 1) sr.subReps.foreach { sr2 =>
        //println(s"${sr.rep} -> ${sr2.rep.bound}")
        count(sr2)
      }
    }
    count(root)
    
    /*
    sreps.foreach { sr =>
      if (sr.shouldBeScheduled) {
        println(s"[${sr.usages}] $sr")
        //println(sr.args.map(_.rep.bound).toList)
      }
    }
    */
    
    def propagateScp(sr: sch.SchedulableRep, scp: Set[Val]): Unit = {
      if (sr.registerScope_!(scp)) sr.subReps.foreach(propagateScp(_, sr.rep.node match {
        case ConcreteNode(abs: Abs) => scp + abs.param
        case _ => scp
      }))
    }
    sreps.iterator.filter(_.rep |> isExported).foreach(propagateScp(_, Set.empty))
    
    val scheduledReps = for (
      sr <- sreps
      if sr.shouldBeScheduled && !(sr eq root) && sr.usages > 1 || isExported(sr.rep)
    ) yield sr
    
    lazy val showStr = {
      val defs = for (
        sr <- sreps
        //if sr.shouldBeScheduled && (sr.usages > 1 || (sr eq root))
        if sr.shouldBeScheduled && /*!(sr eq root) &&*/ (sr.usages > 1 || isExported(sr.rep))
      ) yield s"def ${
          if (sr eq root) "main" else sr.rep.bound
        }(${(".." :: sr.paramRestVals.map{ p =>
            s"$p: ${p.typ}"
          }.toList).mkString(",")
        }): ${sr.rep.typ} = ${sr.printDef(false)}"
      defs.mkString("\n")
    }
    
    //HaskellScheduleDebug debugFor
    println(showStr)
    
    val haskellDefs = scheduledReps.map(r => r -> r.haskellDef)
    
    // TODO rm valParams from SR
    haskellDefs.foreach {case (r,d) =>
      //println(s"Schscp ${r.maximalSharedScope}")
      r.valParams = Some(d.valParams)
    }
    
    val (topLevelReps, nestedReps) =
      haskellDefs.partition{ case (r,d) => isExported(r.rep) || d.nestingScope.isEmpty }
    
    //println(s"Top: ${topLevelReps}")
    println(s"Nested: ${nestedReps.map(n => n._1.rep.bound -> n._2.freeVars)}")
    
    var nestedCount = 0
    
    import sch._
    /** defs: association between sub-scopes to what to bind in them */
    def nestDefs(sd: SchExp, defs: List[Set[Val] -> SchDef]): Unit = sd match {
      case lam @ SchLam(p, _, bs, b) =>
        import squid.utils.CollectionUtils.TraversableOnceHelper
        val (toScheduleNow,toPostpone) = defs.mapSplit {
          case fvs -> sd =>
            val fvs2 = fvs - p
            if (fvs2.isEmpty) Left(sd) else Right(fvs2 -> sd)
        }
        toScheduleNow.foreach { d => nestedCount += 1; nestDefs(d.body, toPostpone); lam.bindings ::= d }
        bs.foreach(b => nestDefs(b._2, toPostpone))
        nestDefs(b, toPostpone)
      case _ => sd.children.foreach(nestDefs(_, defs)) // FIXME variables bound by `case`?!
    }
    val toNest = nestedReps.map { case (r,d) => d.nestingScope -> d }
    topLevelReps.map(_._2.body).foreach(nestDefs(_, toNest))
    softAssert(nestedCount === toNest.size, s"${nestedCount} === ${toNest.size}")
    
    val topLevelRepsOrdered: List[SchDef] = {
      val topLevels = topLevelReps.iterator.map { case(sr,d) => sr.rep -> d }.toMap
      val done = mutable.Set.empty[Rep]
      topLevelReps.flatMap { case(sr,d) =>
        (if (done(sr.rep)) Iterator.empty else { done += sr.rep; Iterator(d) }) ++ d.allChildren.collect {
          case SchCall(sr, _, _) if !done(sr.rep) && topLevels.contains(sr.rep) =>
            done += sr.rep; topLevels(sr.rep) }
      }
    }
    softAssert(topLevelRepsOrdered.size === topLevelReps.size, s"${topLevelRepsOrdered.size} === ${topLevelReps.size}")
    
    new ScheduledModule {
      def commentLines(str: String) = str.split("\n").map("--   "+_).mkString("\n")
      override def toHaskell(imports: List[String], ghcVersion: String) = s"""
        |-- Generated Haskell code from Graph optimizer
        |-- Core obtained from: $ghcVersion
        |-- Optimized after GHC phase:
        |${mod.modPhase |> commentLines}
        |-- Total nodes: ${mod.Stats.tot}; Boxes: ${mod.Stats.boxes}; Branches: ${mod.Stats.brans}
        |-- Apps: ${mod.Stats.apps}; Lams: ${mod.Stats.lams}; Unreduced Redexes: ${mod.Stats.unreducedRedexes}
        |
        |{-# LANGUAGE UnboxedTuples #-}
        |{-# LANGUAGE MagicHash #-}
        |
        |module ${mod.modName} (${mod.letReps.map(_.bound).mkString(",")}) where
        |
        |${imports.map("import "+_).mkString("\n")}
        |
        |${topLevelRepsOrdered.map(_.toHs).mkString("\n\n")}
        |""".tail.stripMargin
        //|${topLevelReps.sortBy{case(sr,d) => (!isExported(sr.rep),d.name)}.map(_._2.toHs).mkString("\n\n")}
        //|-- Optimized graph view:
        //|${mod.show |> commentLines}
      
      override def toString: String = showStr
      
    }
    
  }
  
  
}

