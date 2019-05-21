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

private final class UnboxedMarker // for Haskell gen purposes

/** Dummy class for encoding Haskell pat-mat in the graph. */
private class HaskellADT {
  def `case`(cases: (String -> Any)*): Any = ???
  def get(ctorName: String, fieldIdx: Int): Any = ???
}

/** New scheduling algorithm that supports recursive definitions and Haskell output. */
trait HaskellGraphScheduling extends AST { graph: Graph =>
  /* Notes.
  
  Problems with exporting Haskell from GHC-Core:
   - After rewrite rules, we get code that refers to functions not exported from modules,
     such as `GHC.Base.mapFB`, `GHC.List.takeFB`
   - After some point (phase Specialise), we start getting fictive symbols,
     such as `GHC.Show.$w$cshowsPrec4`
   - The _simplifier_ itself creates direct references to type class instance methods, such as
     `GHC.Show.$fShowInteger_$cshowsPrec`, and I didn't find a way to disable that behavior... 
  So the Haskell export really only works kind-of-consistently right after the desugar phase OR after some
  simplification with optimization turned off, but no further...
  Yet, the -O flag is useful, as it makes list literals be represented as build.
  So it's probably best to selectively disable optimizations, such as the application of rewrite rules (a shame, really).
  
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
  
  // Uncomment for nicer names in the graph, but mapped directly to the Haskell version (which becomes less stable):
  //override protected def freshNameImpl(n: Int) = "_"+n.toHexString
  
  // Otherwise creates a stack overflow while LUB-ing to infinity
  override def branchType(lhs: => TypeRep, rhs: => TypeRep): TypeRep = Any
  
  //import mutable.{Map => M}
  import mutable.{ListMap => M}
  
  val Any = Predef.implicitType[Any].rep
  val UnboxedMarker = Predef.implicitType[UnboxedMarker].rep
  
  val HaskellADT = loadTypSymbol("squid.ir.graph3.HaskellADT")
  val CaseMtd = loadMtdSymbol(HaskellADT, "case")
  val GetMtd = loadMtdSymbol(HaskellADT, "get")
  
  var ctorArities = mutable.Map.empty[String, Int]
  def mkCase(scrut: Rep, alts: Seq[(String, Int, () => Rep)]): Rep = {
    methodApp(scrut, CaseMtd, Nil, ArgsVarargs(Args(),Args(alts.map{case(con,arity,rhs) =>
      ctorArities.get(con) match {
        case Some(a2) => assert(a2 === arity)
        case None => ctorArities += con -> arity
      }
      Tuple2(con |> staticModule, rhs())
    }:_*))::Nil, Any)
  }
  
  object Tuple2 {
    //val TypSymbol = loadTypSymbol("scala.Tuple2")
    val ModTypSymbol = loadTypSymbol("scala.Tuple2$")
    val Mod = staticModule("scala.Tuple2")
    val ApplySymbol = loadMtdSymbol(ModTypSymbol, "apply")
    def apply(x0: Rep, x1: Rep): Rep = methodApp(Mod, ApplySymbol, Nil, Args(x0,x1)::Nil, Any)
  }
  
  
  case class PgrmModule(modName: String, modPhase: String, lets: Map[String, Rep]) {
    val letReps = lets.valuesIterator.toList
    lazy val toplvlRep = {
      val mv = bindVal(modName, Any, Nil)
      Rep.withVal(mv, Imperative(letReps.init, letReps.last, allowEmptyEffects = true))
    }
    def showGraph = toplvlRep.showGraph
    def show = "module " + showGraph
  }
  
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
    
    /** Stringification context: current scope, association between sub-scopes to what to bind in them */
    type StrfyCtx = (Set[Val], List[Set[Val] -> SchDef])
    
    // This could be made generic and given an Applicative instance
    case class SchDef(freeVals: Set[Val], private val body: StrfyCtx => String) {
      def mapStr(strf: String => String): SchDef = SchDef(freeVals, strctx => strf(body(strctx)))
      def mkStr(ctx: StrfyCtx): String = body(ctx)
    }
    object SchDef {
      def mk(s: String): SchDef = SchDef(Set.empty, _ => s)
      def sequence(schDefs: List[SchDef])(mkStr: List[String] => String): SchDef =
        SchDef(schDefs.flatMap(_.freeVals)(collection.breakOut), m => mkStr(schDefs.map(_.body(m))))
    }
    
    class SchedulableRep private(val rep: Rep) {
      var backEdges: mutable.Buffer[Control -> SchedulableRep] = mutable.Buffer.empty
      scheduledReps += rep -> this
      val children = rep.node.children.map(c => scheduledReps.getOrElseUpdate(c, new SchedulableRep(c))).toList
      children.foreach(_.backEdges += Id -> this)
      
      /** `branches` maps each branch instance to what it's transformed to,
        * along with the original branch val (for argument-naming purposes), and the extruded variables. */
      val branches: M[(Control,Branch),(TrBranch,Val,List[Val])] = rep.node match {
        case br: Branch => M((Id,br) -> (Left(Id,br),rep.bound,Nil))
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
      
      def printDef(dbg: Bool): String = printDefWith(dbg)(branches.toMap)
      def printDefWith(dbg: Bool)(implicit brc: BranchCtx): String = rep.node match {
      case _: Branch => rep.bound.toString // really?
      case _ =>
        if (brc.nonEmpty) Sdebug(s"? ${rep}"+
          s"\n\t${branches.map(kv => s"${kv._1}>>${kv._2._1}[${kv._2._2}]").mkString}"+
          s"\n\t${brc.map(kv => s"${kv._1}>>${kv._2._1}[${kv._2._2}]").mkString}"
        ) else Sdebug(s"? ${rep}")
        val res = HaskellScheduleDebug.nestDbg(new HaskellDefPrettyPrinter(showInlineCF = false) {
          override def apply(n: Node): String = if (dbg) super.apply(n) else n match {
            case Box(_, body) => apply(body)
            case ConcreteNode(d) => apply(d)
            case _: Branch => die
          }
          override def apply(r: Rep): String = {
            Sdebug(s"? ${r}")
            val sr = scheduledReps(r)
            def printArg(cb: (Control, Branch), pre: String): String = brc.get(cb._1->cb._2).map {
                case (Left(_),v,xvs) => v.toString + (if (xvs.isEmpty) "" else xvs.mkString("(",",",")"))
                case (Right(r),v,xvs) => pre + (if (xvs.isEmpty) "" else s"\\(${xvs.mkString(",")}) -> ") + apply(r.rep)
              }.getOrElse {
                if (!dbg) /*System.err.*/println(s"/!!!\\ ERROR: at ${r.bound}: could not find cb $cb in:\n\t${brc}")
                s"${Console.RED}???${Console.RESET}"
              }
            val res = HaskellScheduleDebug.nestDbg (sr.rep.node match {
              case ConcreteNode(d) if d.isSimple => super.apply(d)
              case ConcreteNode(ByName(body)) /*if !dbg*/ => apply(body)
              case ConcreteNode(MethodApp(_,Tuple2.ApplySymbol,Nil,Args(lhs,rhs)::Nil,_)) /*if !dbg*/ =>
                s"{${apply(lhs)} -> ${apply(rhs)}}"
              case ConcreteNode(MethodApp(scrut,GetMtd,Nil,Args(Rep(ConcreteNode(StaticModule(con))),Rep(ConcreteNode(Constant(idx))))::Nil,_)) if !dbg =>
                s"${apply(scrut)}!$con#$idx"
              case b: Branch =>
                assert(sr.branches.size === 1)
                printArg((Id,b),"") // FIXME??
              case _ if !dbg && (sr.usages <= 1) =>
                assert(sr.usages === 1)
                sr.printDefWith(dbg)(xtendBranchCtx(sr))
              case _ =>
                s"${sr.rep.bound}(${sr.branches.valuesIterator.collect{
                  case (Left(cb),v,_) => printArg(cb,s"$v=")
                }.mkString(",")})"
            })
            Sdebug(s"= ${res}")
            res
          }
        } apply rep.node)
        Sdebug(s"= ${res}")
        res
      }
      def mkHaskellDef: SchDef = mkHaskellDefWith(branches.toMap, Map.empty)
      type CaseCtx = Map[(Rep,String), List[Val]]
      def mkHaskellDefWith(implicit brc: BranchCtx, enclosingCases: CaseCtx): SchDef = {
        def mkDef(d: Def)(implicit enclosingCases: CaseCtx): SchDef = d match {
            case v: Val => SchDef(Set(v), _ => printVal(v))
            case Constant(n: Int) => s"$n" |> SchDef.mk
            case Constant(s: String) => '"'+s+'"' |> SchDef.mk
            case CrossStageValue(n: Int, UnboxedMarker) => s"$n#" |> SchDef.mk
            case StaticModule(name) => name |> SchDef.mk
            case Apply(a,b) =>
              val (SchDef(fv0,fs0), SchDef(fv1,fs1)) = (mkSubRep(a), mkSubRep(b))
              SchDef(fv0 ++ fv1, m => s"(${fs0(m)} ${fs1(m)})")
            case ByName(body) => mkSubRep(body) // Q: should we really keep this one?
            case MethodApp(scrut, CaseMtd, Nil, ArgsVarargs(Args(), Args(alts @ _*))::Nil, _) =>
              val (SchDef(fv0,fs0), SchDef(fv1,fs1)) =
                (mkSubRep(scrut), SchDef.sequence(alts.map(mkAlt(scrut,_)).toList)(_.mkString("; ")))
              SchDef(fv0 ++ fv1, m => s"(case ${fs0(m)} of {${fs1(m)}})")
            case MethodApp(scrut,GetMtd,Nil,Args(con,idx)::Nil,_) =>
              (con,idx) |>! {
                case (Rep(ConcreteNode(StaticModule(con))),Rep(ConcreteNode(Constant(idx: Int)))) =>
                  // TODO if no corresponding enclosing case, do a patmat right here
                  enclosingCases(scrut->con)(idx) |> printVal |> SchDef.mk
              }
            case Abs(p,b) =>
              val SchDef(fv,fs) = mkSubRep(b)
              SchDef(fv - p, m => s"(\\${p |> printVal} -> ${
                var whereDefs = List.empty[String]
                val m2: StrfyCtx = m._1 + p -> m._2.flatMap {
                  case fvs -> sd =>
                    val fvs2 = fvs - p
                    if (fvs2.isEmpty) {
                      whereDefs ::= sd.mkStr(m); Nil } else fvs2 -> sd :: Nil
                }
                if (whereDefs.isEmpty) fs(m2) else
                s"let { ${whereDefs.mkString("; ")} } in ${fs(m2)}"
              })")
          }
        def mkAlt(scrut: Rep, r: Rep): SchDef = r.node |>! {
          case ConcreteNode(MethodApp(_,Tuple2.ApplySymbol,Nil,Args(lhs,rhs)::Nil,_)) =>
            lhs.node |>! {
              case ConcreteNode(StaticModule(con)) =>
                val boundVals = List.tabulate(ctorArities(con))(idx => bindVal(s"arg$idx", Any, Nil))
                mkSubRep(rhs)(enclosingCases + ((scrut,con) -> boundVals)).mapStr(str =>
                  (if (con.head.isLetter || con === "[]" || con.head === '(') con else s"($con)") +
                    boundVals.map{" "+_}.mkString + s" -> $str")
            }
        }
        def mkSubRep(r: Rep)(implicit enclosingCases: CaseCtx): SchDef = {
          val sr = scheduledReps(r)
          // TODO use proper unboxed tuple arguments for the continuations
          def mkArg(cb: (Control,Branch), pre: String): SchDef = brc(cb) match {
            case (Left((_,_)),v,xvs) =>
              val str = if (xvs.isEmpty) printVal(v) else "("+printVal(v)+xvs.map(printVal).mkString("(",",",")")+")"
              str |> SchDef.mk
            case (Right(r),v,xvs) =>
              val SchDef(fv,f) = mkSubRep(r.rep)
              SchDef(fv, { case (scp,m) =>
                val toBind = xvs.filterNot(scp)
                pre + (if (toBind.isEmpty) "" else s"\\(${toBind.map(printVal).mkString(",")}) -> ") + f(scp->m)
              })
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
              sr.mkHaskellDefWith(xtendBranchCtx(sr), enclosingCases)
            case _ =>
              val name = sr.rep.bound |> printVal
              val args = sr.mkParamsRest.map(_._1 |> (mkArg(_,"")))
              SchDef.sequence(args) { argStrs =>
                val valArgs = sr.valParams.get.map(printVal)
                val allArgStrs = valArgs ++ argStrs
                if (allArgStrs.isEmpty) name else
                s"($name(# ${allArgStrs.mkString(", ")} #))"
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
        val name = rep.bound |> printVal
        mkParamsRest.foreach(_._2 |> printVal) // This is done just to get the `printVal`-generated names in the 'right' order.
        mkParamsRest.groupBy(_._2).valuesIterator.foreach {
          case Nil | _ :: Nil =>
          case (_,v) :: _ => lastWords(s"Duplicated param: $v (i.e., ${v |> printVal})")
        }
        mkHaskellDef.mapStr { str =>
          val paramList = if (params.isEmpty) "" else s"(# ${params.map(printVal).mkString(", ")} #)"
          s"$name$paramList = $str"
        }
      }
      
      def params = valParams.get ++ mkParamsRest.map(_._2)
      def paramRestVals = branches.valuesIterator.collect{case (Left(cb),v,xvs) => v}
      lazy val mkParamsRest: List[(Control,Branch)->Val] =
        branches.valuesIterator.collect{case (Left(cb),v,xvs) => cb->v}.toList
      
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
    def toHaskell(imports: List[String]): String
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
        dbg(s"  Back Edge: $backCtrl ${sr2.rep.bound}")
        sr.branches.valuesIterator.foreach {
        case (Left(cb0 @ (c0,b)), v, xvs) =>
          val c = backCtrl `;` c0
          if (!sr2.branches.contains(cb0)) {
            dbg(s"    New: ${cb0}")
            val (lambdaBound, nde) = sr2.rep.node match {
              case ConcreteNode(abs: Abs) if ByName.unapply(abs).isEmpty =>
                (Some(abs.param), Box(Push(DummyCallId, Id, Id),abs.body))
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
                dbg(s"    Consider: $newCtrl")
                mayHaveCid(newCtrl `;` b.ctrl, b.cid)(Id) match {
                  case Some(cnd) =>
                    val r3 = sch.scheduledReps(if (cnd) b.lhs else b.rhs)
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
        dbg(s"\t${sr.branches.map(kv => s"${kv._1}>>${kv._2._1 match {
          case Left(cb) => "L:"+cb
          case Right(sr) => "R:("+sr.rep+")"
        }}[${kv._2._2}] ").mkString(" ")}")
      }
      s">> Done! <<"
    }
    
    val sreps = sch.scheduledReps.valuesIterator.toList.sortBy(_.rep.bound.name)
    sreps.foreach { sr =>
      if (sr.shouldBeScheduled) {
        sr.subReps.foreach(_.usages += 1)
      }
    }
    // Note: the root will probably always have 0 usage
    /*
    reps.foreach { sr =>
      if (sr.shouldBeScheduled) {
        println(s"[${sr.usages}] $sr")
        //println(sr.args.map(_.rep.bound).toList)
      }
    }
    */
    
    val isExported = mod.letReps.toSet
    
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
    
    lazy val haskellDefs = scheduledReps.map(r => r -> r.haskellDef)
    lazy val (topLevelReps, nestedReps) = {
      haskellDefs.foreach {case (r,d) =>
        //println(s"Schscp ${r.maximalSharedScope}")
        r.valParams = Some((d.freeVals -- r.maximalSharedScope.get).toList.sortBy(sch.printVal))
      }
      haskellDefs.partition{ case (r,d) => isExported(r.rep) || (d.freeVals & r.maximalSharedScope.get isEmpty) }
    }
    lazy val m: sch.StrfyCtx = {
      //println(s"Top: ${topLevelReps}")
      println(s"Nested: ${nestedReps.map(n => n._1.rep.bound -> n._2.freeVals)}")
      (Set.empty, nestedReps.map { case (r,d) => (d.freeVals & r.maximalSharedScope.get) -> d })
    }
    
    new ScheduledModule {
      // FIXME what if some nestedReps were never scheduled? (could happen if they have impossible scopes!)
      override def toHaskell(imports: List[String]) = s"""
        |-- Generated Haskell code from Graph optimizer
        |-- Optimized after GHC phase:
        |${mod.modPhase.split("\n").map("--   "+_).mkString("\n")}
        |
        |{-# LANGUAGE UnboxedTuples #-}
        |{-# LANGUAGE MagicHash #-}
        |
        |module ${mod.modName} (${mod.letReps.map(_.bound).mkString(",")}) where
        |
        |${imports.map("import "+_).mkString("\n")}
        |
        |${topLevelReps.map(_._2.mkStr(m)).mkString("\n\n")}
        |""".tail.stripMargin
      
      override def toString: String = {
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
      
    }
    
  }
  
  
  
  override def prettyPrint(d: Def) = (new HaskellDefPrettyPrinter)(d)
  class HaskellDefPrettyPrinter(showInlineNames: Bool = false, showInlineCF:Bool = true) extends graph.DefPrettyPrinter(showInlineNames, showInlineCF) {
    override def apply(d: Def): String = d match {
      case Apply(lhs,rhs) => s"${apply(lhs)} ${apply(rhs)}" // FIXME?
      case _ => super.apply(d)
    }
  }
  override def printNode(n: Node) = (new HaskellDefPrettyPrinter)(n)
  
}

