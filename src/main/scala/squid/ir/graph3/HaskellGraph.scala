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
  
  val DummyTyp = Any
  override def staticModuleType(name: String): TypeRep = DummyTyp
  def mkName(base: String, idDomain: String) =
    //base + "_" + idDomain + freshName  // gives names that are pretty cluttered...
    base + freshName
  def setMeaningfulName(v: Val, n: String): Unit = {
    //println(s"Set $v -> $n")
    if (v.name.contains('$')) v.name_! = n
  }
  
  val HaskellADT = loadTypSymbol("squid.ir.graph3.HaskellADT")
  val CaseMtd = loadMtdSymbol(HaskellADT, "case")
  val GetMtd = loadMtdSymbol(HaskellADT, "get")
  
  val WildcardVal = bindVal("", Any, Nil)
  
  var ctorArities = mutable.Map.empty[String, Int] // FIXME ctor names can be shared among data types!
  
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
        case Appl(fun, arg) => go(fun) ||> (_2 = arg :: _)
        case r => r -> Nil
      }
      val (f, as) = go(rep)
      Some(f -> as.reverse)
    }
  }
  object ModuleDef {
    def apply(name: String): Rep = ConcreteRep(StaticModule(name))
    def unapply(arg: Rep): Option[String] = arg |>? { case ConcreteRep(StaticModule(name)) => name }
  }
  /** This extractor is mostly just to gloss over the fact that lists ctors seem annoyingly special.
    * Note that is seems we can define other ctors, as in `data A a = (:::) a`, so we should in principle handle all of them. */
  object AsCtor {
    def unapply(arg: Rep): Option[String] = arg |>? {
      case ModuleDef("(:)") => ":"
      case ModuleDef("[]") => "[]"
      // ^ List is special, for some weird reason; is allowed to have non-alpha ctor name
      case ModuleDef(name) if !name.head.isLetter || name.head.isUpper => name
    }
  }
  object Appl {
    def apply(lhs: Rep, rhs: Rep): Rep = app(lhs, rhs)(DummyTyp)
    def unapply(arg: Rep): Option[Rep -> Rep] = arg |>? { case ConcreteRep(Apply(lhs,rhs)) => lhs -> rhs }
  }
  
  // Type RedKey and val reduced used to be defined here, but I moved them to GraphRewriting so they can be updated when
  // beta reduction rewires application nodes.
  
  //val MaxPathSize = 4
  val MaxPathSize = 16
  //val MaxPathSize = 32
  
  // TODO use this simpler function to do beta reduction too, instead of the old simplifyGraph?
  def simplifyHaskell(rep: Rep): Iterator[Rep] = {
    import RewriteDebug.{debug=>Rdebug}
    import CollectionUtils.TraversableOnceHelper
    
    // TODO add capability to optimize through case branches...
    type Path[+A] = (List[(Control,CallId,Bool,Rep)],Control,A)
    
    def findPath[A](rep: Rep)(select: PartialFunction[Rep,A]): List[Path[A]] = {
      val trav = mutable.Set.empty[Rep]
      val select2 = select.andThen(res => (Nil,Id,res) :: Nil)
      
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
          select2.applyOrElse(rep, (_: Rep) => Nil)
      }, Nil) alsoDo (trav -= rep)
      
      go(rep,Id).filter(_._1.size <= MaxPathSize || { println(s"MAX PATH SIZE REACHED!"); false })
    }
    
    def tryRewrite(rep: Rep, otherKey: Option[Rep] = None)(paths: List[Path[Rep]]): Option[Rep] = paths.collectFirstSome { case (conds, ctrl, arg) =>
      val key: RedKey = (conds.map { case (c2, cid, side, b) => (cid, side, b) }, arg)
      Rdebug(s"Considering ${key} (${reduced(rep)(key)})")
      if (reduced(rep)(key) || otherKey.exists(r=>reduced(rep).contains(Nil->r))) None else Some {
        Rdebug(s"Not added: ${rep.bound} -> ${otherKey.map(_.bound)}")
        val newBody = Box.rep(ctrl, arg)
        reduced(newBody) += Nil -> newBody
        Rdebug(s"Rewiring ${rep.bound} over $ctrl")
        rep.rewireTo(conds match {
          case Nil => newBody
          case _ =>
            //println(s"$conds, $ctrl, $arg")
            val other = rep.node.mkRep // NOTE: this is only okay because we know the original rep will be overwritten with a new node!
            val reds = reduced(rep)
            reduced -= rep
            reds += key
            reduced += other -> reds
            reduced(other) ++= otherKey.map(Nil -> _)
            Rdebug(s"Added: ${other.bound} -> ${otherKey.map(_.bound)}")
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
      
      case rep @ ConcreteRep(StaticModule("GHC.Base.map")) =>
        val dt = DummyTyp
        val fv = bindVal("f", dt, Nil)
        val xsv = bindVal("xs", dt, Nil)
        val cv = bindVal("c", dt, Nil)
        val nv = bindVal("n", dt, Nil)
        //val hv = bindVal("h", dt, Nil)
        //val tv = bindVal("t", dt, Nil)
        
        rep.rewireTo(abs(fv, { abs(xsv, {
          app(
            ModuleDef("GHC.Base.build"),
            abs(cv, {
              abs(nv, {
                app(app(app(
                  ModuleDef("GHC.Base.foldr"),
                  /*
                  // This version currently creates more scop extrusions than the one using (.) below
                  abs(hv, {
                    abs(tv, {
                        app(app(
                          cv |> readVal,
                          app(fv |> readVal, hv |> readVal)(dt)
                          )(dt),
                          tv |> readVal
                        )(dt)
                    })
                  })
                  */
                  app(app(
                    ModuleDef("(GHC.Base..)"),
                    cv |> readVal,
                    )(dt),
                    fv |> readVal
                    )(dt)
                  )(dt),
                  nv |> readVal
                  )(dt),
                  xsv |> readVal
                )(dt)
              })
            })
          )(dt)
        })}))
        Some(rep)
      
      //case rep @ App(App(App(ModuleDef("GHC.Base.foldr"), k), z), App(ModuleDef("GHC.Base.build"), g)) => ???
        
      case rep @ Applies(ModuleDef("GHC.Base.foldr"), List(k, z, b)) =>
        //println(b.showGraph)
        findPath(b) {
          case Appl(ModuleDef("GHC.Base.build"), g)
            if !reduced.isDefinedAt(g) || !reduced.isDefinedAt(rep)
          =>
            Rdebug(s"OMG IT'S HAPPENING [$rep] ${g} $k $z")
            //Thread.sleep(30)
            reduced(g) = mutable.Set.empty
            //reduced(g) += Nil -> g
            //println(reduced.isDefinedAt(g))
            g
        }.map{ case (conds, ctrl, g) =>
          //reduced(g) += ((Nil,g))
          val bg = Box.rep(ctrl, g)
          val arg = app(app(bg,k)(DummyTyp),z)(DummyTyp)
          //reduced(rep) += ((Nil,arg))
          reduced(rep) += ((Nil,rep))
          (conds, Id, arg)
        } |> tryRewrite(rep)
        
        
      case rep @ ConcreteRep(MethodApp(scrut, CaseMtd, Nil, ArgsVarargs(Args(), Args(alts @ _*))::Nil, _)) =>
        val altsList: Seq[String -> Rep] = alts.map {
          case ConcreteRep(MethodApp(_,Tuple2.ApplySymbol,Nil,Args(lhs,rhs)::Nil,_)) =>
            lhs.node |>! { case ConcreteNode(StaticModule(con)) => con -> rhs }
        }
        findPath(scrut) {
          // TODO handle non-fully-applied ctors... (i.e., applied across control/branches)
          case Applies(AsCtor(ctorName),args)
            if altsList.exists { case (con, _) => (ctorName endsWith con) || con === "_" } // e.g., "GHC.Types.True" endsWith "True"
            // ^ I assume this cannot match random functions that happen to be named the same as the ctor (hopefully illegal)
            //   However it doesn't support pattern synonyms.
            //   A more conservative approach would be as follows:
            //if altsList.exists { case (con, _) => ctorName endsWith con } || altsList.forall { case (con, _) => con === "_" }
          =>
            altsList.find { case (con, _) => ctorName endsWith con }.getOrElse {
              altsList.find(_._1 === "_").get
            }._2
        }.map { case (conds, ctrl, arg) =>
          (conds, Id, arg)
          // We should not place the control `ctrl` found while looking for the scrutinee's ctor, as the scrutinee is
          // then discarded (we just directly redirect to the right branch, and don't use any of the `args`), and the
          // `ctrl` will actually be rediscovered independently by the ctor argument accessor nodes.  
        } |> tryRewrite(rep)
        
      case rep @ ConcreteRep(MethodApp(scrut,GetMtd,Nil,Args(con,idx)::Nil,_)) =>
        val ModuleDef(conStr) = con
        val ConcreteRep(Constant(idxInt: Int)) = idx
        findPath(scrut) {
          case Applies(AsCtor(ctorName),args)
            if ctorName endsWith conStr // e.g., "GHC.Types.True" endsWith "True"
          =>
            val arg = args(idxInt)
            arg
        } |> tryRewrite(rep)
        
        
        
      /*
      // Even this specific rewrite version that applies to lambdas ends up creating scope extrusions
      case rep @ ConcreteRep(Apply(ConcreteRep(Apply(ConcreteRep(StaticModule("(GHC.Base..)")), f1)), f2)) =>
        findPath(f2) {
          //case ConcreteRep(abs: Abs)
          case abs @ ConcreteRep(_: Abs)
          =>
            abs
        }.map { case (conds, ctrl, arg) =>
          val com = bindVal("com", DummyTyp, Nil)
          val pop = Pop.it(com)
          (conds, Id, abs(com, app(Box.rep(pop,f1), app(Box.rep(pop,f2), com |> readVal)(DummyTyp))(DummyTyp)))
          //(conds, Id, abs(com, app(Box.rep(pop,f1), app(Box.rep(pop`;`ctrl,arg), com |> readVal)(DummyTyp))(DummyTyp)))
        } |> tryRewrite(rep, Some(f2))
      */
      
      
      // The following `sum` desugarings allow full reduction in the graph, but they turned out to be harmful
      // performance-wise, as it seems GHC knows how to compile `sum` better.
      /*
      case rep @ ConcreteRep(StaticModule("Data.Foldable.sum")) =>
        val xsv = bindVal("sxs", DummyTyp, Nil)
        //???
        rep.rewireTo(
          
        /* currently creates scope extrusion: */ 
        //abs(xsv,
        //app(
          
          //app(
            app(
              app(
                ConcreteRep(StaticModule("GHC.Base.foldr")),
                ConcreteRep(StaticModule("(+)")),
              )(DummyTyp),
              ConcreteRep(Constant(0))
            )(DummyTyp)
          //)(DummyTyp)
          
        //,xsv |> readVal
        //)(DummyTyp)
        //)
          
        )
        Some(rep)
      */
      /*
      case rep @ ConcreteRep(Apply(lhs, rhs)) =>
        findPath(lhs) {
          case s @ ConcreteRep(StaticModule("Data.Foldable.sum"))
            //if !reduced.isDefinedAt(s)
            //if !reduced(s).contains(Nil->rep)
          =>
            //reduced(s) = mutable.Set.empty
            //reduced(s) += Nil -> rep
            ()
        }.map { case (conds, ctrl, ()) =>
          Rdebug(s"SUM REWRITE [$rep]")
          val arg =
          app(
            app(
              app(
                ConcreteRep(StaticModule("GHC.Base.foldr")),
                ConcreteRep(StaticModule("(+)")),
              )(DummyTyp),
              ConcreteRep(Constant(0))
            )(DummyTyp),
            rhs
          )(DummyTyp)
          (conds, Id, arg)
        } |> tryRewrite(rep, Some(lhs))
      */
        
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
