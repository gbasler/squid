package graph4

import squid.utils._

import scala.collection.mutable

abstract class GraphScheduler extends SmartGraphScheduler { self: GraphIR =>
  
  object ScheduleDebug extends PublicTraceDebug
  import ScheduleDebug.{debug=>Sdebug}
  
  def schedule(mod: GraphModule, naive: Bool = false): Scheduler =
    if (naive) new NaiveScheduler(mod)
    else new SmartScheduler(mod)
  
  abstract class Scheduler {
    val mod: GraphModule
    val AST: HaskellAST { type Ident = Var }
    val modDefStrings: List[Str]
    
    def usesLenses: Bool
    def mayUseLenses: Bool
    def usesTupleSelectors: Bool
    
    private val nameCounts = mutable.Map.empty[String, Int]
    private val nameAliases = mutable.Map.empty[Var, String]
    protected def resetVarPrintingState(): Unit = { nameCounts.clear(); nameAliases.clear() }
    
    protected def printVar(v: Var): String = if (v === WildcardVal) "_" else nameAliases.getOrElseUpdate(v, {
      val nme = v.name
        //.takeWhile(_ =/= '$') // don't think I currently use dollar signs
        .stripPrefix("β") // most intermediate expressions end up having a β name; it's better to just remove it
        //.stripPrefix("π")
        //.replaceAll("π", "c")
        .stripSuffix("_ε") // I use these suffixes to make debugging graph dumps easier, but they're very ugly
      val cnt = nameCounts.getOrElse(nme, 0)
      nameCounts(nme) = cnt+1
      if (nme.isEmpty) {
        // Note: an annoyance with leading underscores is that GHC thinks they are type holes when their definition is missing...
        if (mayUseLenses) "_t"+cnt // lens selectors use _1, _2, etc. so we shouldn't use these names
        else "_"+cnt
      } else
      //if (nme.isEmpty) "t"+cnt else
      cnt match {
        case 0 => nme
        case 1 => nme+"'"
        case n => nme+"'"+n
        //case n => nme+"_"+n
      }
    })
    
    protected def constantToExpr(c: ConstantNode): AST.Expr = c match {
      case IntLit(true, n) => if (useOnlyIntLits) AST.Inline(s"($n::Int)") else AST.Inline(n.toString)
      case IntLit(false, n) => AST.Inline(s"$n#")
      case CharLit(false, n) => AST.Inline(s"'$n'#")
      case CharLit(true, n) => AST.Inline(s"'$n'")
      case StrLit(true, s) => AST.Inline('"' + s + '"')
      case StrLit(false, s) => AST.Inline('"' + s + '"' + '#')
      case ModuleRef(m, n) // Q: is this case still useful?
        if (n startsWith "(") && (n endsWith ")") && n.init.tail.forall(_ === ',')
        => AST.Inline(n)
      case ModuleRef(m, n) =>
        val str = if (knownModule(m) || m === "") n // we use an empty `m` in the TestHarness as a hack to generate code
          else s"$m.$n"
        AST.Inline(if (n.head.isLetter || n === "[]") str else s"($str)")
    }
    
    protected val pp: ParameterPassingStrategy =
      //UnboxedTupleParameters
      CurriedParameters
    
    def toHaskell(imports: List[String], ghcVersion: String): String = {
      val allImports: List[Str] = List(
        "Control.Lens" optionIf usesLenses,
        "Data.Tuple.Select" optionIf usesTupleSelectors,
      ).flatten ::: imports
      def commentLines(str: String) = str.split("\n").map("--   "+_).mkString("\n")
      //HaskellScheduleDebug debugFor
      s"""
      |-- Generated Haskell code from Graph optimizer
      |-- Core obtained from: $ghcVersion
      |-- Optimized after GHC phase:
      |${mod.modPhase |> commentLines}
      |-- Beta reductions:  ${mod.betaReductions}
      |-- Incl. one-shot:   ${mod.oneShotBetaReductions}
      |-- Case reductions:  ${mod.caseReductions}
      |-- Field reductions: ${mod.fieldReductions}
      |-- Case commutings:  ${mod.caseCommutings}
      |-- Total nodes: ${mod.Stats.tot}; Boxes: ${mod.Stats.boxes}; Branches: ${mod.Stats.brans}
      |-- Apps: ${mod.Stats.apps}; Lams: ${mod.Stats.lams}
      |
      |{-# LANGUAGE UnboxedTuples #-}
      |{-# LANGUAGE MagicHash #-}
      |{-# LANGUAGE NoMonomorphismRestriction  #-}${
        // Tuple selectors sel1, sel2, etc. sometimes cause a type inference error that goes away with FlexibleContexts
        if (usesTupleSelectors) "\n{-# LANGUAGE FlexibleContexts  #-}" else "" }
      |
      |module ${mod.modName} (${mod.modDefs.map(_._1).mkString(",")}) where
      |
      |${allImports.map("import "+_).mkString("\n")}
      |
      |${modDefStrings.mkString("\n\n")}
      |""".tail.stripMargin
      //|-- Apps: ${mod.Stats.apps}; Lams: ${mod.Stats.lams}; Unreduced Redexes: ${mod.Stats.unreducedRedexes}
    }
    
    override def toString =
      s"module ${mod.modName} ???" // TODO
      //s"module ${mod.modName}${topLevelRepsOrdered.map("\n\t"+_.stringify).mkString}"
    
  }
  
  /** Does not work with recursive functions! */
  class NaiveScheduler(val mod: GraphModule) extends Scheduler {
    
    val usesLenses = false
    val mayUseLenses = false
    val usesTupleSelectors = false
    
    object AST extends HaskellAST(pp) {
      type Ident = Var
      def printIdent(id: Ident): String = printVar(id)
      def mkIdent(nameHint: String): Ident = bindVal(mkName(nameHint, ""))
      val dropUnusedLets = true
      val inlineOneShotLets = inlineScheduledLets
      val inlineTrivialLets = inlineScheduledLets
      val inlineCalls = inlineScheduledLets
      val commonSubexprElim = true
      val useOnlyIntLits = self.useOnlyIntLits
    }
    
    def liftRef(ref: Ref)(implicit ictx: Instr): AST.Expr = ScheduleDebug.nestDbg {
      Sdebug(s"[$ictx] ${ref.show}")
      ref.node match {
        case Control(i,b) =>
          liftRef(b)(ictx `;` i)
        case Branch(c,t,e) =>
          if (Condition.test_!(c,ictx)) liftRef(t) else liftRef(e)
        case IntBoxing(n) => constantToExpr(IntLit(true, n))
        case App(l,r) =>
          AST.App(liftRef(l), liftRef(r))
        case l @ Lam(_,b) =>
          AST.Lam(AST.Vari(l.param), Nil, liftRef(b)(ictx push DummyCallId))
        case v: Var => AST.Vari(v)
        case c: ConstantNode => constantToExpr(c)
        case Case(s,as) =>
          AST.Case(liftRef(s), as.map{ case (c,a,r) => (c, List.fill(a)(bindVal("ρ") |> AST.Vari), liftRef(r)) })
        case CtorField(s,c,a,i) => AST.CtorField(liftRef(s),c,a,i)
      }
    }
    lazy val modDefStrings: List[Str] = mod.modDefs.map { case (name, ref) =>
      Sdebug(s"Scheduling $name")
      val e = liftRef(ref)(Id)
      new AST.Defn(bindVal(name), Nil, Lazy(e)).stringify
    }
    
  }
  
  
}
