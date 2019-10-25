package graph4

import squid.utils._
import squid.ir.graph3.{HaskellAST, ParameterPassingStrategy, UnboxedTupleParameters}

import scala.collection.mutable

abstract class GraphScheduler { self: GraphIR =>
  
  def schedule(mod: GraphModule): Scheduler = new Scheduler(mod)
  
  class Scheduler(mod: GraphModule) {
    
    private val nameCounts = mutable.Map.empty[String, Int]
    private val nameAliases = mutable.Map.empty[Var, String]
    def printVar(v: Var): String = if (v === WildcardVal) "_" else nameAliases.getOrElseUpdate(v, {
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
    
    val pp: ParameterPassingStrategy =
      UnboxedTupleParameters
      //CurriedParameters
    
    object AST extends HaskellAST(pp) {
      type Ident = Var
      def printIdent(id: Ident): String = printVar(id)
      def mkIdent(nameHint: String): Ident = bindVal(mkName(nameHint, ""))
    }
    
    def toHaskell(imports: List[String], ghcVersion: String): String = {
      def commentLines(str: String) = str.split("\n").map("--   "+_).mkString("\n")
      //HaskellScheduleDebug debugFor
      s"""
      |-- Generated Haskell code from Graph optimizer
      |-- Core obtained from: $ghcVersion
      |-- Optimized after GHC phase:
      |${mod.modPhase |> commentLines}
      |-- Beta reductions:  ${mod.betaReductions}
      |-- Incl. one-shot:  ${mod.oneShotBetaReductions}
      |-- Total nodes: ${mod.Stats.tot}; Boxes: ${mod.Stats.boxes}; Branches: ${mod.Stats.brans}
      |-- Apps: ${mod.Stats.apps}; Lams: ${mod.Stats.lams}
      |
      |{-# LANGUAGE UnboxedTuples #-}
      |{-# LANGUAGE MagicHash #-}
      |{-# LANGUAGE NoMonomorphismRestriction  #-}
      |
      |module ${mod.modName} (${mod.modDefs.map(_._1).mkString(",")}) where
      |
      |${imports.map("import "+_).mkString("\n")}
      |
      |${modDefs.map(d => d.stringify).mkString("\n\n")}
      |""".tail.stripMargin
      //|-- Apps: ${mod.Stats.apps}; Lams: ${mod.Stats.lams}; Unreduced Redexes: ${mod.Stats.unreducedRedexes}
    }
    
    override def toString =
      s"module ${mod.modName} ???" // TODO
      //s"module ${mod.modName}${topLevelRepsOrdered.map("\n\t"+_.stringify).mkString}"
    
    
    val modDefs: List[AST.Defn] = mod.modDefs.map { case (name, ref) =>
      
      def rec(ref: Ref)(implicit ictx: Instr): AST.Expr = ref.node match {
        case Control(i,b) =>
          rec(b)(ictx `;` i)
        case Branch(c,t,e) =>
          if (Condition.test_!(c,ictx)) rec(t) else rec(e)
        case App(l,r) =>
          AST.App(rec(l), rec(r))
        case l @ Lam(_,b) =>
          AST.Lam(AST.Vari(l.param), Nil, rec(b)(ictx push DummyCallId))
        case v: Var => AST.Vari(v) //AST.mkIdent(v.name)
        case IntLit(true, n) => AST.Inline(n.toString)
        case IntLit(false, n) => AST.Inline(s"$n#")
        case StrLit(true, s) => AST.Inline(s.toString)
        case StrLit(false, s) => AST.Inline(s"$s#")
        case ModuleRef(m, n) => AST.Inline(s"$m.$n")
        case Case(s,as) => ???
        case CtorField(s,c,i) => ???
      }
      val e = rec(ref)(Id)
      
      new AST.Defn(bindVal(name), Nil, e)
      
    }
    
    
  }
  
}
