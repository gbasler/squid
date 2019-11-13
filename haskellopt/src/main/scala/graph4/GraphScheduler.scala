package graph4

import squid.utils._

import scala.collection.mutable

abstract class GraphScheduler { self: GraphIR =>
  
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
  
  class SmartScheduler(val mod: GraphModule) extends Scheduler { scheduler =>
    
    /** How far to reify scope bodies after finding the first potentially-recursive parents.
      * It should be at least UnrollingFactor, since we may need to dig as deep to recover the regular recursive structure. 
      * Note: it's possible that using a bigger UF could yield better perfs (as there would be less). */
    val UF = UnrollingFactor
    //val UF = UnrollingFactor + 1
    
    val FlattenScopes = !debugScheduling
    //val FlattenScopes = false
    //val FlattenScopes = true
    
    val useDummyCids = false
    //val useDummyCids = true
    
    object ScopeAccessMethod extends Enumeration {
      val Case, Selector, Lens = Value
    }
    val scopeAccessMethod: ScopeAccessMethod.Value =
      ScopeAccessMethod.Selector // note that sometimes, I've seen this create type inference errors (where using Case works)
      //ScopeAccessMethod.Case
      //ScopeAccessMethod.Lens
    
    var usesLenses = false
    val mayUseLenses = scopeAccessMethod === ScopeAccessMethod.Lens
    var usesTupleSelectors = false
    
    object AST extends HaskellAST(pp) {
      type Ident = Var
      def printIdent(id: Ident): String = printVar(id)
      def mkIdent(nameHint: String): Ident = bindVal(mkName(nameHint, ""))
      val dropUnusedLets = false // TODO make unused lets an error?
      val inlineOneShotLets = inlineScheduledLets
      val inlineTrivialLets = inlineScheduledLets
      //val inlineCalls = inlineScheduledLets // Causes assertion to fail in HaskellAST.Let.reorder since it may remove usages of some defs
      val inlineCalls = false
      val commonSubexprElim = false
      //override val mergeLets = true
      val useOnlyIntLits = self.useOnlyIntLits
      //override val mergeLets = !debugScheduling
      override val mergeLets = true
    }
    
    /** Did anything significant change? — as in: a parameter, return, or performed test was added to a scope... */
    var anythingChanged = true
    
    val modDefStrings: List[Str] = {
      val modDefsWithIdents = mod.modDefs.map { case (nme,df) => (nme,AST.mkIdent(nme),df) }
      if (debugScheduling) modDefsWithIdents.foreach { d =>
        printVar(d._2)  // allocate the right 'bare' name for each top-level definition
      }
      modDefsWithIdents.map { case (nme,ide,df) =>
        Sdebug(s"Scheduling $nme")
        
        if (!debugScheduling) resetVarPrintingState()
        printVar(ide) // allocate the right 'bare' name for each top-level definition
        
        val rootScp = ScheduleDebug.nestDbg {
          val scp = new Scope(if (useDummyCids) DummyCallId else new CallId(ide,mod.nextUnique), Id, None) {
            override val ident = ide
          }
          scp.enter(Id, df)
          scp
        }
        while (anythingChanged) { // TODO something more fine-grained that tracks changes in each scopes and possibly dependencies...
          anythingChanged = false
          // While there are potential recursive scopes, check their tests and expand them if the test results differ;
          // then only the truly recursive scopes will still have non-empty `potentiallyRecursiveParents`.
          var changed = true
          while (changed) {
            changed = false
            Sdebug(s">>>>>> RESULT")
            Sdebug(rootScp.show())
            Sdebug(s">>>>>> /")
            
            var scpStack = rootScp :: Nil
            while (scpStack.nonEmpty) {
              val scp = scpStack.head
              scpStack = scpStack.tail
              
              if (scp.returns.isEmpty) {
                assert(scp.delayedEntries.nonEmpty)
                Sdebug(s"Performing delayed entries of ${scp}") thenReturn ScheduleDebug.nestDbg {
                  assert(!scp.isRecursive)
                  if (scp.recursiveParent.nonEmpty) {
                    Sdebug(s"Oh noes! We had already assumed that this was a recursive call... with ${scp.params}; ${scp.captures}")
                    scp.recursiveParent = None // is this okay? isn't there anything else to reverse?
                    // Erasing what was potentially done in the loop below ("Now doing param..."), as it may no longer be valid:
                    //scp.params = Nil; scp.captures = Nil
                    // ^ not sure this actually is the right thing to do... It made scheduling of Statistics never finish when I tried.
                  }
                  if (ScheduleDebug.isDebugEnabled) Thread.sleep(200)
                  val es = scp.delayedEntries
                  scp.delayedEntries = Nil
                  es.foreach{ case (in,ref) => scp.enter(in,ref) }
                }
              }
              
              val ps = scp.potentiallyRecursiveParents
              if (ps.nonEmpty) Sdebug(s"$scp has potentially recursive parents") thenReturn ScheduleDebug.nestDbg {
                scp.potentiallyRecursiveParents = ps.filter { p =>
                  Sdebug(s"Parent $p")
                  ScheduleDebug.nestDbg {
                    var fails = false
                    def notParent(): Unit =
                      Sdebug(s"${Console.BOLD}This was not a real recursive parent${Console.RESET}")
                    val prets = p.returns.map(_._1)
                    val scprets = scp.returns.map(_._1)
                    Sdebug(s"Has returns: ${prets.mkString("; ")}")
                    Sdebug(s"We have returns: ${scprets.mkString("; ")}")
                    if (!scprets.forall(prets.toSet)) { // FIXME not sure... can parent returns sometimes miss ones we do not?!
                      fails = true
                      notParent()
                    }
                    Sdebug(s"Has tests: ${p.testsPerformed.map(cb => Condition.show(cb._1) + " == " + cb._2).mkString("; ")}")
                    fails = fails || ScheduleDebug.nestDbg { p.testsPerformed.exists { case (c, res) =>
                      val truth = Condition.test_!(c, scp.ictx)
                      Sdebug(s"Here: ${Condition.show(c)} == ${truth}")
                      scp.addTest(c, truth)
                      truth =/= res also { b => if (b) { notParent(); changed = true } }
                    }}
                    Sdebug(s"We have tests: ${scp.testsPerformed.map(cb => Condition.show(cb._1) + " == " + cb._2).mkString("; ")}")
                    fails = fails || ScheduleDebug.nestDbg { scp.testsPerformed.exists { case (c, res) =>
                      val truth = Condition.test(c, p.ictx)
                      Sdebug(s"There: ${Condition.show(c)} == ${truth}")
                      truth match {
                        case Some(`res`) => false
                        case Some(_) | None =>
                          notParent()
                          changed = true
                          true // i.e., fails
                      }
                    }}
                    !fails
                  }
                }
                if (scp.potentiallyRecursiveParents.isEmpty) {
                  assert(scp.recursiveParent.isEmpty) // if not, it means we'd have processed spurious params and captures...
                  scpStack :::= scp.subScopes
                }
              } else {
                scpStack :::= scp.subScopes
              }
              
            }
            
          }
          rootScp.iterator.foreach { scp =>
            val ps = scp.potentiallyRecursiveParents
            if (ps.nonEmpty) {
              //val recScp = ps.last // Using this one caused InterpBasicBench to fail on bad comparison (but it should not!)
              val recScp = ps.head // Seems to lead to slightly smaller programs
              Sdebug(s"Apparently recursive scope: ${scp} -> ${recScp}")
              var reallyRec = true
              
              // This is required if we try to tie recursions across different CallId-s, although I am not sure it's really possible (did not managed to yet):
              /*
              def notRec(): Unit = { // TODO see if this always happen for non-same-cid parents (if so, no need to even consider them)
                reallyRec = false
                Sdebug(s"${Console.BOLD}It was not actually a recursive parent!${Console.RESET}")
                // TODO rm params/captures?
                scp.recursiveParent = None
                scp.potentiallyRecursiveParents = scp.potentiallyRecursiveParents.filterNot(_ === recScp)
              }
              */
              ScheduleDebug.nestDbg {
                scp.recursiveParent = Some(recScp)
                recScp.params.foreach { case ((i,r),sch) => if (reallyRec) {
                  Sdebug(s"Now doing param $r a.k.a. ${scp.parent.get.printRef(i,r)} or ${recScp.toParamIdent((i,r))|>printVar} ($sch (${sch.toExpr.stringify}))")
                  // (Note that scp.parent.get may very well NOT be recScp; sometimes recursion jump scopes.)
                  //val res = 
                  scp.rec(i,r)
                  //try scp.rec(i,r) catch { case _: BadComparison => notRec() }
                  //Sdebug(s"Got: $res a.k.a. ${res.toExpr} a.k.a. ${res.toExpr.stringify}")
                }}
                // Similar to params:
                if (reallyRec) recScp.captures.foreach { case ((i,r),sch) => if (reallyRec) {
                  Sdebug(s"Now doing capture $r a.k.a. ${recScp.printRef(i,r)} or ${recScp.toCaptureIdent((i,r))|>printVar} ($sch (${sch.toExpr.stringify}))")
                  scp.rec(i,r)
                  //try scp.rec(i,r) catch { case _: BadComparison => notRec() }
                }}
                if (reallyRec) recScp.isRecursive = true
              }
            }
          }
        }
        Sdebug(s">>>>>> FINAL RESULT")
        Sdebug(rootScp.show())
        Sdebug(s">>>>>> /")
        val defn = rootScp.toDefn
        defn.stringify
      }
    }
    
    sealed abstract class Scheduled {
      def toExpr: AST.Expr
      def toExprStrSafe: Str = this match {
        case _: Lambda => s"<lam>"
        case _ => toExpr.stringify
      }
      def isVari: Bool = this match { case Concrete(_: Var, _) => true; case _ => false }
    }
    case class Concrete(nde: ConcreteNode, children: List[Scheduled]) extends Scheduled {
      assert(nde.children.size === children.size)
      assert(!nde.isInstanceOf[Lam])
      def toExpr: AST.Expr = nde match {
        case IntBoxing(n) => constantToExpr(IntLit(true, n))
        case App(l, r) => AST.App(children(0).toExpr, children(1).toExpr)
        case l @ Lam(_,lbody) => die // we handle lambdas specially
        case v: Var => AST.Vari(v)
        case c: ConstantNode => constantToExpr(c)
        case Case(s,as) =>
          assert(as.size === children.size - 1)
          AST.Case(children.head.toExpr, as.zip(children.tail).map {
            case ((c,a,r),s) => (c, List.fill(a)(bindVal("ρ") |> AST.Vari), s.toExpr) })
        case CtorField(s,c,a,i) => AST.CtorField(children(0).toExpr, c, a, i)
      }
      override def toString = if (children.isEmpty || nde.isSimple) nde.toString else children.mkString(s"($nde)[",",","]")
    }
    case class ScopeAccess(scp: Scope, v: Var, in: Instr, r: Ref) extends Scheduled {
      private def mkSelection(scp: Scope, prefix: AST.Expr, idx: Int) = {
        if (scopeAccessMethod === ScopeAccessMethod.Lens) {
          usesLenses = true
          AST.App(AST.App(AST.Inline("(^.)"), prefix), AST.Inline(s"_${idx+1}"))
        }
        else if (scopeAccessMethod === ScopeAccessMethod.Selector) {
          usesTupleSelectors = true
          AST.App(AST.Inline(s"sel${idx+1}"), prefix)
        }
        else AST.CtorField(prefix, scp.returnsTupleCtor, scp.returns.size, idx)
      }
      def toExpr: AST.Expr = scp.recursiveParent match {
        case Some(rec) => // recursive function call
          val scp = rec
          val callVari = AST.Vari(this.scp.callIdent)
          if (scp.returns.size > 1) {
            
            // FIXME the following does not work, weirdly — to investigate!
            //val idx = scp.returns.indexWhere(_._1 === (in,r))
            val idx = scp.returns.indexWhere(_._1._2 === r) // BAD: could lead to confusion!
            
            assert(idx >= 0, (idx, scp, v, in, r, scp.returns))
            
            //assert(scp.returns.count(_._1._2 === r) === 1) // seems to fail with higher unrolling factors
            if (scp.returns.count(_._1._2 === r) =/= 1)
              System.err.println(s"WARNING: SELECTION MAY BE WRONG! — $idx of $in/$r in ${scp.returns}")
              // ^ FIXME fails for maxMaybe1
            
            mkSelection(scp, callVari, idx)
          }
          else callVari
        case None => if (scp.isRecursive || !FlattenScopes) { // call to recursive function
          val callVari = AST.Vari(scp.callIdent)
          if (scp.returns.size > 1) {
            val idx = scp.returns.indexWhere(_._1 === (in,r))
            assert(idx >= 0, (idx, scp, v, in, r, scp.returns))
            mkSelection(scp, callVari, idx)
          }
          else callVari
        } else AST.Vari(scp.toIdent(in,r))
      }
    }
    case class Lambda(v: Var, scp: Scope) extends Scheduled {
      def toExpr: AST.Expr = {
        val defn = scp.toDefn
        //assert(defn.params.isEmpty) // No longer true due to variable captures being promoted to parameters...
        assert(defn.params.size === scp.captures.size)
        val newBody = defn.params.zipWithIndex.foldLeft(defn.body) {
          case (acc, (v,i)) => AST.Let(v, scp.captures(i)._2.toExpr, acc, true) // FIXME correct? what if this is a recursive scope? -> capture should be the right one...
          //case (acc, (v,i)) => AST.Let(v, scp.captures(i)._1|>scp.toVari, acc, true)
        }
        AST.Lam(AST.Vari(v), Nil, newBody)
      }
    }
    case class Call(scp: Scope)
    
    /** Used as the keys to identify unique let-bound definitions. */
    type IRef = (Instr, Ref)
    
    class Scope(val cid: CallId, val payload: Instr, val parent: Opt[Scope]) {
      
      val ictx: Instr = parent.map(_.ictx).getOrElse(Id) `;` Push(cid, payload, Id)
      
      val ident = AST.mkIdent("rec")
      val callIdent = AST.mkIdent("rec_call")
      
      lazy val push = Push(cid,payload,Id)
      
      val idents: mutable.Map[IRef, AST.Ident] = mutable.Map.empty
      def toIdent(iref: IRef): AST.Ident = idents.getOrElseUpdate(iref, AST.mkIdent(iref._2.name.getOrElse("t")))
      def toVari(ref: IRef): AST.Vari = {
        Sdebug(s"! toVari ${ref} --> ${ref|>printRef}")
        AST.Vari(toIdent(ref))
      }
      def printRef(iref: IRef) = printVar(toIdent(iref))
      
      // We used to separate parameters and captures to avoid clashes, but keeping track of the full Instr in the IRef prevents us from that now
      def toParamIdent(ref: IRef): AST.Ident = toIdent(ref)
      def toCaptureIdent(ref: IRef): AST.Ident = toIdent(ref)
      
      def toArg(ref: (IRef, Scheduled)): IRef = {
        assert(idents.contains(ref._1))
        val iref = ref._1._1.asInstanceOf[Drop].rest -> ref._1._2
        Sdebug(s"?! toArg $ref :: ${ref._1 |> printRef} --> ${iref} (${ref._2.toExpr.stringify})")
        iref
        //ref._2.toExpr // refers to the wrong one!
      }
      def toCaptureArg(ref: (IRef, Scheduled)): IRef = {
        assert(idents.contains(ref._1))
        val pop = ref._1._1.asInstanceOf[Pop]
        val iref = (payload `;` pop.rest) -> ref._1._2
        Sdebug(s"?! toCaptureArg $ref :: ${ref._1 |> printRef} --> ${iref} (${ref._2.toExpr.stringify})")
        iref
        //ref._2.toExpr
      }
      
      var subScopes: List[Scope] = Nil
      parent.foreach(_.subScopes ::= this)
      
      def iterator: Iterator[Scope] = Iterator(this) ++ subScopes.iterator.flatMap(_.iterator)
      
      def parents: List[Scope] = parent.toList.flatMap(p => p :: p.parents)
      
      var potentiallyRecursiveParents = if (cid === DummyCallId) Nil else parents.filter(_.cid === cid)
      // TODO try the following? but then more care must be taken; for example params will fail as is, since they drop the wrong scope (but captures will be fine) 
      //var potentiallyRecursiveParents = if (cid === DummyCallId) Nil else parents.filter(_.cid.v === cid.v)
      
      var recursiveParent: Opt[Scope] = None
      var isRecursive: Bool = false
      
      val defn = Lazy(toDefn) // TODO make sure it's not evaluated too early (before scheduling finishes) with an assumption
      
      val callSubScopes: mutable.Map[CallId, Scope] = mutable.Map.empty
      
      val testsPerformed: mutable.Map[Condition, Bool] = mutable.Map.empty
      def addTest(c: Condition, truth: Bool): Unit = if (Condition.test(c,push).isEmpty) { // if the condition is not trivial
        testsPerformed.get(c) match {
          case Some(t) => assert(t === truth)
          case None =>
            anythingChanged = true
            testsPerformed += c -> truth
            parent.foreach(p => Condition.throughControl(push,c).foreach(c => p.addTest(c, truth)))
        }
      }
      
      var params: List[IRef -> Scheduled] = Nil
      var captures: List[IRef -> Scheduled] = Nil
      var returns: List[IRef -> Scheduled] = Nil
      var delayedEntries: List[IRef] = Nil
      
      def allParams = params.map(_ -> true) ::: captures.map(_ -> false)
      
      def returnsTupleCtor: Str = {
        assert(returns.size > 1)
        "(" + "," * (returns.size - 1) + ")"
      }
      
      var bindings: List[Either[IRef -> Scheduled, Call]] = Nil
      val bound: mutable.Set[IRef] = mutable.Set.empty
      
      def bind(iref: IRef, sch: Scheduled): Unit = if (!bound(iref)) { // we test again here as sometimes things are bound during calls to `rec`
        assert(!bound(iref))
        bound += iref
        bind_!(iref, sch)
      }
      /** Sometimes we want to manually set `bound` and later call this `bind_!` which does not check it. */
      def bind_!(iref: IRef, sch: Scheduled): Unit = {
        Sdebug(s"iBIND<$ident> ${iref|>printRef} = ${sch.toExprStrSafe}  (${iref} = ${sch})")
        bindings ::= Left(iref -> sch)
      }
      
      /* Important remark: each distinct IRef=(Instr,NodeRef) can be associated with a unique ConcreteNode per scope (possibly a variable) */
      def rec(in: Instr, body: Ref): Scheduled = ScheduleDebug.nestDbg {
        Sdebug(s"<${ident}> [$in] ${body.showDef}")
        //if (ScheduleDebug.isDebugEnabled) Thread.sleep(50)
        
        val iref = (in,body)
        def asVar = Concrete(toIdent(iref), Nil)
        
        in match {
            
          case Id =>
            body.node match {
              case c: ConcreteNode =>
                if (bound(iref)) asVar else {
                  if (!body.toBeScheduledInline) // TODO rm toBeScheduledInline
                    bound += Id->body
                  val res = c match {
                    case l @ Lam(_,b) =>
                      val scp = new Scope(if (useDummyCids) DummyCallId else new CallId(l.param,mod.nextUnique), Id, Some(this))
                      Sdebug(s"New lambda at ${body} ${l.param} ${l.paramRef} ${scp.toIdent(Id->l.paramRef)} ${scp.toIdent(Id->l.paramRef)|>printVar}")
                      scp.enter(Id, b)
                      //Sdebug(s"?L? ${b} ${b|>scp.printRef} ${b|>printRef}")
                      Lambda(Id->l.paramRef |> scp.toIdent, scp) // TODO should the Instr really be Id here?
                    case v: Var =>
                      Sdebug(s"VAR $v ${iref |> printRef}")
                      Concrete(iref |> toIdent, Nil)
                      //Concrete(v, Nil)
                    case _ =>
                      Concrete(c, c.children.map(rec(Id, _)).toList)
                  }
                  if (body.toBeScheduledInline) res
                  else {
                    bind_!(iref, res)
                    asVar
                  }
                }
              case Control(i,b) =>
                //rec(i,b) // TOOD try to put back?
                if (bound(iref)) asVar else {
                  bind(iref,rec(i,b))
                  asVar
                }
              case Branch(c,t,e) =>
                if (bound(Id->body)) {
                  Sdebug(s"BRANCH—already bound! $c $body ${toIdent(iref)|>printVar} ${asVar.toExpr.stringify}  as  ${asVar} / ${asVar.toExpr.stringify}")
                  asVar
                } else {
                  val truth = Condition.test_!(c, ictx)
                  Sdebug(s"BRANCH [${ictx}](${Condition.show(c)}) $truth")
                  addTest(c, truth)
                  val res = if (truth) rec(Id,t) else rec(Id,e)
                  Sdebug(s"BRANCH ${toIdent(iref)|>printVar} --> ${res.toExprStrSafe} ($res)")
                  bind(iref, res)
                  asVar
                }
            }
            
          case p @ Pop(i) =>
            if (p.originalVar =/= cid.v) badComparison(s"$this `;` ${body.showDef}")
            Sdebug(s"Pop to ${parent.get}")
            val pi = payload `;` i // contrary to the Drop case, we rec into the parent throught payload `;` i and not just i!
            if (captures.exists(_._1 === (iref))) {
              Sdebug(s"CAPTURE<$ident> ${i -> body} already exists")
            } else {
              anythingChanged = true
              val res = parent.get.rec(pi, body)
              captures ::= (iref, res)
              Sdebug(s"CAPTURE<$ident> ${body} ==> ${res} ==> ${toCaptureIdent((iref))} (${toCaptureIdent((iref))|>printVar})")
              //Sdebug(s"?Pop? ${body} ${bound(body)} ${toCaptureIdent(body)}/${toCaptureIdent(body)|>printVar} ${parent.get.toIdent(body)}/${parent.get.toIdent(body)|>printVar}")
            }
            asVar

          case d @ Drop(i) =>
            if (d.originalCid =/= cid) badComparison(s"$this `;` ${body.showDef}")
            Sdebug(s"Drop to ${parent.get}")
            if (params.exists(_._1 === iref)) {
              Sdebug(s"PARAM<$ident> ${i -> body} already exists")
            } else {
              anythingChanged = true
              val res = parent.get.rec(i, body)
              params ::= iref -> res
              Sdebug(s"PARAM<$ident> ${body} ==> ${res} ==> ${toParamIdent((i,body))} (${toParamIdent((i,body))|>printVar})")
              //Sdebug(s"?Drop? ${body} ${bound(body)} ${toParamIdent(body)}/${toParamIdent(body)|>printVar} ${parent.get.toIdent(body)}/${parent.get.toIdent(body)|>printVar}")
            }
            asVar
            
          case Push(cid,p,r) =>
            Sdebug(s"Push ${cid}")
            var isNew = false
            val scp = callSubScopes.getOrElseUpdate(cid, {
              val scp = new Scope(cid, p, Some(this))
              isNew = true
              scp
            })
            assert(scp.payload === p, (scp.payload,p))
            scp.enter(r, body)
            //Sdebug(s"?P? ${body} ${body|>scp.printRef} ${body|>printRef}")
            if (isNew) bindings ::= Right(Call(scp))
            if (!bound(iref)) {
              val res = ScopeAccess(scp, scp.ident, r, body)
              bind(iref, res)
            }
            asVar
            
        }
      }
      def enter(in: Instr, body: Ref): Unit = if (potentiallyRecursiveParents.size > UF) {
        Sdebug(s"Cannot enter $this yet, as it's possibly recursive (${potentiallyRecursiveParents.mkString(", ")})")
        delayedEntries ::= (in,body)
      } else {
        Sdebug(s"Enter $this")
        if (returns.exists(_._1 === (in, body))) {
          Sdebug(s"Return for [$in] $body already exists as ${in->body|>toIdent|>printVar}")
        } else {
          val ret = rec(in, body)
          //Sdebug(s"Return $ret")
          Sdebug(s"Return $ret ${ret.toExpr} ${ret.toExpr.stringify} ${in->body|>toIdent} ${in->body|>toIdent|>printVar}")
          anythingChanged = true // Q: needed here?
          returns ::= (in, body) -> ret
        }
      }
      def foldBindings(ret: List[AST.Binding]): List[AST.Binding] = {
        bindings.foldLeft[List[AST.Binding]](ret) {
          case (rest, Left(r -> b)) =>
            val e = b.toExpr
            Left((toVari(r), e)) :: rest //also (res => Sdebug(s"LET ${r|>printRef} / ${e.stringify} / ${rest.stringify}  ==>  ${res.stringify}"))
          case (rest, Right(Call(scp))) =>
            if (!scp.isRecursive && scp.recursiveParent.isEmpty && FlattenScopes) {
              val ret = scp.returns.foldRight(rest) {
                case ((ref, sch), rest) =>
                  Sdebug(s"BIND RETURN ${scp.toVari(ref)}:${scp.printRef(ref)} = ${sch} (${sch.toExpr.stringify})")
                  if (sch.isVari) rest // if it's already a variable, no need to bind it again
                  else Left((scp.toVari(ref), sch.toExpr)) :: rest
                  //Left((scp.toVari(ref), sch.toExpr)) :: rest
              }
              val rest2 = scp.foldBindings(ret)
              scp.allParams.foldRight(rest2) {
                case (((i,p),sch)->isParam, rest2) =>
                  //Sdebug(s"!!! LET ${p}=$sch ${toIdent(p)} ${toIdent(p)|>printVar}")
                  //if (sch.isVari) rest else // FIXME?
                  Left(((if (isParam) scp.toParamIdent _ else scp.toCaptureIdent _)(i,p)|>AST.Vari, sch.toExpr)) :: rest2
                  //Left((scp.toParamIdent(p)|>AST.Vari, sch.toExpr)) :: Left((scp.toIdent((i,p))|>AST.Vari, scp.toParamIdent(p)|>AST.Vari)) :: rest2
              }
            }
            else if (scp.recursiveParent.isDefined) {
              val p = scp.recursiveParent.get
              Sdebug(s"REC CALL $scp -> $p")
              val call = AST.Call(p.defn, p.params.map(_|>scp.toArg|>toVari) ::: p.captures.map(_|>scp.toCaptureArg|>toVari))
              Left((AST.Vari(scp.callIdent), call)) :: rest
            } else {
              val d = scp.defn
              Sdebug(s"CALL REC $scp")
              val call = AST.Call(d, scp.params.map(_|>scp.toArg|>toVari) ::: scp.captures.map(_|>scp.toCaptureArg|>toVari))
              Right(d.value) :: Left((AST.Vari(scp.callIdent), call)) :: rest
            }
        }
      }
      
      def toDefn: AST.Defn = Sdebug(s"Defn of $this") thenReturn ScheduleDebug.nestDbg {
        //Sdebug(s"PARAMS: ${allParams.mkString(", ")}")
        //Sdebug(s"PARAM VARIS: ${allParams.map(pc => pc._1._1 |> (if (pc._2) toParamIdent else toCaptureIdent) |> printVar).mkString(", ")}")
        new AST.Defn(ident, allParams.map(pc => pc._1._1 |> (if (pc._2) toParamIdent else toCaptureIdent) |> AST.Vari), Lazy {
          val ret = returns match {
            case Nil => die
            case (_, r) :: Nil => r.toExpr
            case _ => returns.foldLeft[AST.Expr](AST.Inline(returnsTupleCtor)) {
              case (app, (_, r)) => AST.App(app, r.toExpr) }
          }
          Sdebug(s"Bindings: ${bindings.reverse}")
          //Sdebug(s"Bindings:${bindings.reverse.map{
          //  case Left((iref,sch)) => s"\n\t${printRef(iref)} = ${sch.toExpr.stringify}      i.e., $sch"
          //  case Right(c) => s"\n\t${c.scp.callIdent|>printVar} = CALL ${c.scp}"
          //}.mkString}")
          Sdebug(s"Ret: ${ret.stringify}")
          val defs = foldBindings(Nil)
          val body = AST.Let.reorder(defs, ret, debugScheduling) // note: will access the Defn of recursive definitions that are being defined here...
          //val body = AST.Let.multiple(defs, ret)
          body
        })
      } //also (res => Sdebug(s"=> ${res.stringify}")) // can cause stack overflow as it forces the body
      
      def show(indent: Int = 0): Str = {
        val pre = "\n" + "  " * (indent+1)
        val rets = returns.map {
          case (r,s) => (if (!s.isVari) s"${printRef(r)}=" else "") + s.toExpr.stringify
        }.mkString(", ")
        val aliases = idents.iterator.map { case (ref, ide) =>
          s"${ide|>printVar} = [${ref._1}]${ref._2.showName}" }.mkString(s"   [ "," ; "," ]")
        val capts = if (captures.isEmpty) "" else s"${pre}Captures: ${captures.map(c =>
          s"${c._1 |> toCaptureIdent |> printVar}->[${c._1._1}]${c._1._2}=${toCaptureArg(c)}").mkString(", ")}"
        val outerTests = if (testsPerformed.isEmpty) "" else s"${pre}Outer-tests: ${testsPerformed.map{
            case (c, b) => (if (b) "" else "!")+Condition.show(c)
          }.mkString(" && ")
        }"
        val possibRecParents = if (potentiallyRecursiveParents.nonEmpty)
          pre + "POT-REC-PARENTS: " + potentiallyRecursiveParents.mkString(", ") else ""
        val isRec = if (isRecursive) pre + "RECURSIVE" else ""
        val recParents = if (recursiveParent.nonEmpty) pre + "REC-PARENT: " + recursiveParent.get else ""
        val recInfo = possibRecParents + isRec + recParents
        def defs = bindings.reverse.map{
          case Left((r,Lambda(v, scp))) => s"$pre${printRef(r)} = \\${v|>printVar} -> ${scp.show(indent + 1)}"
          case Left((r,s)) => s"$pre${printRef(r)} = "+s.toExpr.stringify //+ s"   \t\t(${s})"
          case Right(c) => pre + s"${c.scp.callIdent}(${
            c.scp.params.map(c.scp.toArg).map(printRef).mkString(", ")}): " + c.scp.show(indent + 1)
        }.mkString
        s"$toString: (${params.map(_._1 |> toParamIdent |> printVar).mkString(", ")}) -> ($rets) " +
          s"where$aliases$capts$outerTests$recInfo${defs}"
      }
      
      override def toString = s"${Console.BOLD}<${ident.toString}>${Console.RESET}(${Push(cid,payload,Id)})"
    }
    
  }
  
}
