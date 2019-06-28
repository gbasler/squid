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
import squid.utils.CollectionUtils._

import scala.collection.mutable

private final class UnboxedMarker // for Haskell gen purposes

/** Dummy class for encoding Haskell pat-mat in the graph. */
private class HaskellADT {
  def `case`(cases: (String -> Any)*): Any = ???
  def get(ctorName: String, fieldIdx: Int): Any = ???
}

abstract class HaskellGraph extends Graph {
  
  //override val showPopDropOrigins = true
  
  override val strictCallIdChecking = true
  override val supportDirectRecursion = true
  
  // Uncomment for nicer names in the graph, but mapped directly to the Haskell version (which becomes less stable):
  //override protected def freshNameImpl(n: Int) = "_"+n.toHexString
  
  // Otherwise creates a stack overflow while LUB-ing to infinity
  override def branchType(lhs: => TypeRep, rhs: => TypeRep): TypeRep = Any
  
  val Any = Predef.implicitType[Any].rep
  val UnboxedMarker = Predef.implicitType[UnboxedMarker].rep
  
  val HaskellADT = loadTypSymbol("squid.ir.graph3.HaskellADT")
  val CaseMtd = loadMtdSymbol(HaskellADT, "case")
  val GetMtd = loadMtdSymbol(HaskellADT, "get")
  
  val WildcardVal = bindVal("", Any, Nil)
  
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
    
    object Stats {
      val (tot, lams, apps, boxes, brans) = {
        var tot, lams, apps, boxes, brans = 0
        toplvlRep.allChildren.iterator.map(_.node alsoDo {tot += 1}).foreach {
          case ConcreteNode(_: Abs) =>
            lams += 1
          case ConcreteNode(Apply(_, _)) =>
            apps += 1
          case Box(_, _) =>
            boxes += 1
          case Branch(_, _, _, _) =>
            brans += 1
          case _ =>
        }
        (tot, lams, apps, boxes, brans)
      }
      val unreducedRedexes = {
        val traversed = mutable.Set.empty[Rep]
        def rec(rep: Rep): Int = traversed.setAndIfUnset(rep, rep.node match {
          case ConcreteNode(Apply(fun, arg)) =>
            def findLambdas(rep: Rep): Int = traversed.setAndIfUnset(rep, (rep.node match {
              case Box(_, body) => findLambdas(body)
              case Branch(_, _, lhs, rhs) => findLambdas(lhs) + findLambdas(rhs)
              case ConcreteNode(_: Abs) => 1
              case ConcreteNode(_) => 0
            }) alsoDo (traversed -= rep), 0)
            findLambdas(fun)
          case nde => nde.children.map(rec).sum
        }, 0)
        rec(toplvlRep)
      }
    }
  }
  
  
  
  object Applies {
    def unapply(rep: Rep): Some[Rep -> List[Rep]] = {
      def go(rep: Rep): Rep -> List[Rep] = rep match {
        case ConcreteRep(Apply(fun, arg)) => go(fun) ||> (_2 = arg :: _)
        case r => r -> Nil
      }
      val (f, as) = go(rep)
      Some(f -> as.reverse)
    }
  }
  
  type RedKey = (List[(CallId,Bool,Rep)],Rep)
  val reduced = mutable.Map.empty[Rep,mutable.Set[RedKey]].withDefault(_ => mutable.Set.empty)
  
  // TODO use this simpler function to do beta reduction too, instead of the old simplifyGraph?
  def simplifyHaskell(rep: Rep): Iterator[Rep] = {
    import CollectionUtils.TraversableOnceHelper
    
    // TODO add capability to optimize through case branches...
    type Path[+A] = (List[(Control,CallId,Bool,Rep)],Control,A)
    
    def findPath[A](rep: Rep)(select: PartialFunction[Rep,A]): List[Path[A]] = {
      val trav = mutable.Set.empty[Rep]
      
      def go(rep: Rep, curCtrl: Control): List[Path[A]] = trav.setAndIfUnset(rep, rep.node match {
        case Branch(ctrl,cid,thn,els) => mayHaveCid(ctrl,cid)(curCtrl) match {
          case Some(true) => go(thn,curCtrl)
          case Some(false) => go(els,curCtrl)
          case None =>
            def mp(rep: Rep, isLHS: Bool): List[Path[A]] =
              go(rep, curCtrl).map { case (conds, c, r) =>
                ((ctrl, cid, isLHS, if (isLHS) els else thn) :: conds, c, r) }
            mp(thn, true) ++ mp(els, false)
        }
        case Box(c,bod) =>
          go(bod, curCtrl `;` c).map { case (conds,ctrl,abs) =>
            (conds.map { case (c2,cid,side,b) => (c `;` c2, cid, side, b) }, c `;` ctrl, abs) }
        case _ =>
          select.andThen(res => (Nil,Id,res) :: Nil).applyOrElse(rep, (_: Rep) => Nil)
      }, Nil) alsoDo (trav -= rep)
      
      go(rep,Id)
    }
    
    def tryRewrite(rep: Rep)(paths: List[Path[Rep]]): Option[Rep] = paths.collectFirstSome { case (conds, ctrl, arg) =>
      val key: RedKey = (conds.map { case (c2, cid, side, b) => (cid, side, b) }, arg)
      if (reduced(rep)(key)) None else Some {
        val newBody = Box.rep(ctrl, arg)
        rep.rewireTo(conds match {
          case Nil => newBody
          case _ =>
            //println(s"$conds, $ctrl, $arg")
            val other = rep.node.mkRep // NOTE: this is only okay because we know the original rep will be overwritten with a new node!
            val reds = reduced(rep)
            reduced -= rep
            reds += key
            reduced += other -> reds
            conds.foldRight(newBody) {
              case ((ctrl1,cid1,isLHS1,other1), nde) =>
                val r = nde//.mkRep
                val (thn,els) = if (isLHS1) (r, other) else (other, r)
                Branch(ctrl1,cid1,thn,els).mkRep
            }
        })
        rep
      }
    }
    
    rep.allChildren.iterator.collectSome {
      
      case rep @ ConcreteRep(MethodApp(scrut, CaseMtd, Nil, ArgsVarargs(Args(), Args(alts @ _*))::Nil, _)) =>
        def mkAlt(scrut: Rep, r: Rep): String -> Rep = r.node |>! {
          case ConcreteNode(MethodApp(_,Tuple2.ApplySymbol,Nil,Args(lhs,rhs)::Nil,_)) =>
            lhs.node |>! {
              case ConcreteNode(StaticModule(con)) =>
                val boundVals = List.tabulate(ctorArities(con))(idx => bindVal(s"arg$idx", Any, Nil))
                con -> rhs
            }
        }
        val altsList = alts.map(mkAlt(rep, _))
        //println(scrut)
        //println(altsList)
        findPath(scrut) {
          // TODO handle non-fully-applied ctors... (i.e., applied across control/branches)
          case Applies(ConcreteRep(StaticModule(mod)),args)
            if altsList.exists { case (con, _) => (mod endsWith con) || (con === "_") }
          =>
            altsList.find(mod endsWith _._1).getOrElse(
              altsList.find(_._1 === "_").get
            )._2
        }.map { case (conds, ctrl, arg) =>
          (conds, Id, arg)
          // We should not place the control `ctrl` found while looking for the scrutinee's ctor, as the scrutinee is
          // then discarded (we just directly redirect to the right branch, and don't use any of the `args`), and the
          // `ctrl` will actually be rediscovered independently by the ctor argument accessor nodes.  
        } |> tryRewrite(rep)
        
      case rep @ ConcreteRep(MethodApp(scrut,GetMtd,Nil,Args(con,idx)::Nil,_)) =>
        val ConcreteRep(StaticModule(conStr)) = con
        val ConcreteRep(Constant(idxInt: Int)) = idx
        findPath(scrut) {
          case Applies(ConcreteRep(StaticModule(mod)),args)
            if mod endsWith conStr // e.g., "GHC.Types.True" endsWith "True"
          =>
            val arg = args(idxInt)
            arg
        } |> tryRewrite(rep)
    }
    
  }
  
  
  
  override def prettyPrint(d: Def) = (new HaskellDefPrettyPrinter)(d)
  class HaskellDefPrettyPrinter(showInlineNames: Bool = false, showInlineCF:Bool = true) extends DefPrettyPrinter(showInlineNames, showInlineCF) {
    override def apply(d: Def): String = d match {
      case Apply(lhs,rhs) => s"${apply(lhs)} @ ${apply(rhs)}" // FIXME?
      case _ => super.apply(d)
    }
  }
  override def printNode(n: Node) = (new HaskellDefPrettyPrinter)(n)
  
  
}
