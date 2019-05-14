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
trait RecGraphScheduling extends AST { graph: Graph =>
  
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
  
  
  case class PgrmModule(modName: String, lets: Map[String, Rep]) {
    val letReps = lets.valuesIterator.toList
    lazy val toplvlRep = if (letReps.size < 2) letReps.head else {
      val mv = bindVal(modName, Any, Nil)
      Rep.withVal(mv, Imperative(letReps.init, letReps.last))
    }
    def showGraph = toplvlRep.showGraph
    def show = "module " + showGraph
  }
  
  class RecScheduler(nb: squid.lang.Base) {
    type TrBranch = Either[(Control,Branch),ScheduledRep]
    
    val scheduledReps = M.empty[Rep,ScheduledRep]
    
    class ScheduledRep private(val rep: Rep) {
      var backEdges: mutable.Buffer[ScheduledRep] = mutable.Buffer.empty
      scheduledReps += rep -> this
      val children = rep.node.children.map(c => scheduledReps.getOrElseUpdate(c, new ScheduledRep(c))).toList
      children.foreach(_.backEdges += this)
      
      val branches: M[(Control,Branch),(TrBranch,Val)] = rep.node match {
        case br: Branch => M((Id,br) -> (Left(Id,br),rep.bound))
        case _ => M.empty
      }
      
      var usages = 0
      
      def shouldBeScheduled = rep.node match {
        case _: Branch => false
        case ConcreteNode(d) => !d.isSimple
        case _ => true
      }
      
      def printDef(dbg: Bool): String = rep.node match {
      case _: Branch => rep.bound.toString
      case _ =>
        new DefPrettyPrinter(showInlineCF = false) {
          override def apply(n: Node): String = if (dbg) super.apply(n) else n match {
            case Box(_, body) => apply(body)
            case ConcreteNode(d) => apply(d)
            case _: Branch => die
          }
          override def apply(r: Rep): String = {
            val sr = scheduledReps(r)
            def printArg(cb: (Control,Branch), pre: String): String = branches.get(cb).map{
                case (Left(_),v) => v.toString
                case (Right(r),v) => pre + apply(r.rep)
              }.getOrElse("?")
            sr.rep.node match {
              case ConcreteNode(d) if d.isSimple => super.apply(d)
              case ConcreteNode(ByName(body)) if !dbg => apply(body)
              case ConcreteNode(MethodApp(_,Tuple2.ApplySymbol,Nil,Args(lhs,rhs)::Nil,_)) if !dbg =>
                s"{${apply(lhs)} -> ${apply(rhs)}}"
              case ConcreteNode(MethodApp(scrut,GetMtd,Nil,Args(Rep(ConcreteNode(StaticModule(con))),Rep(ConcreteNode(Constant(idx))))::Nil,_)) if !dbg =>
                s"${apply(scrut)}!$con#$idx"
              case b: Branch =>
                assert(sr.branches.size === 1)
                printArg((Id,b),"")
              case _ if !dbg && (sr.usages <= 1) =>
                assert(sr.usages === 1)
                sr.printDef(dbg)
              case _ =>
                s"${sr.rep.bound}(${sr.branches.valuesIterator.collect{
                  case (Left(cb),v) => printArg(cb,s"$v=")
                }.mkString(",")})"
            }
          }
        } apply rep.node
      }
      def printHaskellDef: String = {
        var enclosingCases = Map.empty[(Rep,String), List[Val]]
        def printDef(d: Def) = d match {
            case v: Val => s"$v"
            case Constant(n: Int) => s"$n"
            case Constant(s: String) => s
            case CrossStageValue(n: Int, UnboxedMarker) => s"$n#"
            case StaticModule(name) => name
            case Apply(a,b) => s"(${printSubRep(a)} ${printSubRep(b)})"
            case Abs(p,b) => s"(\\$p -> ${printSubRep(b)})"
          }
        def printAlt(scrut: Rep, r: Rep): String = r.node |>! {
          case ConcreteNode(MethodApp(_,Tuple2.ApplySymbol,Nil,Args(lhs,rhs)::Nil,_)) =>
            lhs.node |>! {
              case ConcreteNode(StaticModule(con)) =>
                val boundVals = List.tabulate(ctorArities(con))(idx => bindVal(s"arg$idx", Any, Nil))
                val oldEC = enclosingCases
                enclosingCases = enclosingCases + ((scrut,con) -> boundVals)
                try s"$con${boundVals.map{" "+_}.mkString} -> ${printSubRep(rhs)}"
                finally enclosingCases = oldEC
            }
        }
        def printSubRep(r: Rep): String = {
          val sr = scheduledReps(r)
          def printArg(cb: (Control,Branch), pre: String): String = branches.get(cb).map{
              case (Left(_),v) => v.toString
              case (Right(r),v) => pre + printSubRep(r.rep)
            }.getOrElse("?")
          r.node match {
            case ConcreteNode(d) if d.isSimple => printDef(d)
            case ConcreteNode(ByName(body)) => printSubRep(body) // Q: should we really keep this one?
            case ConcreteNode(MethodApp(scrut, CaseMtd, Nil, ArgsVarargs(Args(), Args(alts @ _*))::Nil, _)) =>
              s"(case ${printSubRep(scrut)} of {${alts.map(printAlt(scrut,_)).mkString("; ")}})"
            case ConcreteNode(MethodApp(scrut,GetMtd,Nil,Args(con,idx)::Nil,_)) =>
              (con,idx) |>! {
                case (Rep(ConcreteNode(StaticModule(con))),Rep(ConcreteNode(Constant(idx: Int)))) =>
                  enclosingCases(scrut->con)(idx).toString
              }
            case b: Branch =>
              assert(sr.branches.size === 1)
              printArg((Id,b),"")
            case _ if sr.usages <= 1 =>
              assert(sr.usages === 1)
              sr.printHaskellDef
            case _ =>
              val args = branches.valuesIterator.collect{case (Right(r),v) => r}.filter(_.shouldBeScheduled)
              val argList = if (args.isEmpty) "" else s" (# ${args.mkString(", ")} #)"
              s"${sr.rep.bound}$argList"
          }
        }
        rep.node match {
          case ConcreteNode(d) => printDef(d)
          case _ => die
        }
      }
      def printHaskell = {
        val paramList = if (params.isEmpty) "" else s"(# ${params.map{ p => s"$p" }.mkString(", ")} #)"
        s"${rep.bound} $paramList = $printHaskellDef"
      }
      
      def params = branches.valuesIterator.collect{case (Left(cb),v) => v}
      def args =
        children ++ branches.valuesIterator.collect{case (Right(r),v) => r}.filter(_.shouldBeScheduled)
      
      override def toString =
        s"${rep.bound}(${params.mkString(",")}) = ${printDef(true)}"
    }
    object ScheduledRep {
      def apply(rep: Rep): ScheduledRep = new ScheduledRep(rep)
    }
  }
  
  abstract class ScheduledModule {
    def toHaskell(imports: List[String]): String
    override def toString: String
  }
  
  def scheduleRec(rep: Rep): ScheduledModule =
    scheduleRec(PgrmModule("<module>", Map("main" -> rep)))
  def scheduleRec(mod: PgrmModule): ScheduledModule = {
    val sch = new RecScheduler(SimpleASTBackend)
    val root = sch.ScheduledRep(mod.toplvlRep)
    var workingSet = sch.scheduledReps.valuesIterator.filter(_.branches.nonEmpty).toList
    //println(workingSet)
    while (workingSet.nonEmpty) {
      val sr = workingSet.head
      workingSet = workingSet.tail
      //println(sr, sr.branches)
      sr.backEdges.foreach { sr2 =>
        sr.branches.valuesIterator.foreach {
        case (Left(cb @ (c,b)), v) =>
          if (!sr2.branches.contains(cb)) {
            def addBranch(cb2: sch.TrBranch) = {
              if (cb2.isLeft) workingSet ::= sr2
              sr2.branches += cb -> (cb2, v)
            }
            val nde = sr2.rep.node match {
              case ConcreteNode(abs: Abs) if ByName.unapply(abs).isEmpty => Box(Push(DummyCallId, Id, Id),abs.body)
              case n => n
            }
            nde match {
              case _: Branch => // can't bubble up to a branch!
              case Box(ctrl, _) =>
                val newCtrl = ctrl `;` c
                mayHaveCid(newCtrl `;` b.ctrl, b.cid)(Id) match {
                  case Some(c) =>
                    val r2 = sch.scheduledReps(if (c) b.lhs else b.rhs)
                    r2.backEdges += sr2
                    workingSet ::= r2
                    addBranch(Right(r2))
                  case None => addBranch(Left(newCtrl,b))
                }
              case ConcreteNode(_) => addBranch(Left(cb))
            }
            
          }
        case (Right(_),_) =>
        }
      }
    }
    val reps = sch.scheduledReps.valuesIterator.toList.sortBy(_.rep.bound.name)
    reps.foreach { sr =>
      if (sr.shouldBeScheduled) {
        sr.args.foreach(_.usages += 1)
      }
    }
    // Note: the root will probably always have 0 usage
    reps.foreach { sr =>
      if (sr.shouldBeScheduled) {
        println(s"[${sr.usages}] $sr")
        //println(sr.args.map(_.rep.bound).toList)
      }
    }
    
    val isTopLevel = mod.letReps.toSet
    
    val toplvls = for (
      sr <- reps
      if sr.shouldBeScheduled && !(sr eq root) && (sr.usages > 1 || isTopLevel(sr.rep))
    ) yield sr
    
    new ScheduledModule {
      override def toHaskell(imports: List[String]) = s"""
        |{-# LANGUAGE UnboxedTuples #-}
        |{-# LANGUAGE MagicHash #-}
        |
        |module ${mod.modName} (${mod.letReps.map(_.bound).mkString(",")}) where
        |
        |${imports.map("import "+_).mkString("\n")}
        |
        |${toplvls.map(_.printHaskell).mkString("\n\n")}
        |""".tail.stripMargin
      
      override def toString: String = {
        val defs = for (
          sr <- reps
          if sr.shouldBeScheduled && (sr.usages > 1 || (sr eq root))
        ) yield s"def ${
            if (sr eq root) "main" else sr.rep.bound
          }(${sr.params.map{ p =>
              s"$p: ${p.typ}"
            }.mkString(",")
          }): ${sr.rep.typ} = ${sr.printDef(false)}"
        defs.mkString("\n")
      }
      
    }
    
  }
  
  
}

