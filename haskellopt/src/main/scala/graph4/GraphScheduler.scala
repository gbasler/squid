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
    
    private val nameCounts = mutable.Map.empty[String, Int]
    private val nameAliases = mutable.Map.empty[Var, String]
    protected def resetVarPrintingState(): Unit = { nameCounts.clear(); nameAliases.clear() }
    
    protected def printVar(v: Var): String = if (v === WildcardVal) "_" else nameAliases.getOrElseUpdate(v, {
      val nme = v.name
        //.takeWhile(_ =/= '$') // don't think I currently use dollar signs
        .stripPrefix("β") // most intermediate expressions end up having a β name; it's better to just remove it
        .stripSuffix("_ε") // I use these suffixes to make debugging graph dumps easier, but they're very ugly
      val cnt = nameCounts.getOrElse(nme, 0)
      nameCounts(nme) = cnt+1
      if (nme.isEmpty) "_"+cnt else // annoyance with leading underscores is that GHC thinks they are type holes when their definition is missing... 
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
        val str = if (knownModule(m)) n else s"$m.$n"
        AST.Inline(if (n.head.isLetter || n === "[]") str else s"($str)")
    }
    
    protected val pp: ParameterPassingStrategy =
      //UnboxedTupleParameters
      CurriedParameters
    
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
      |-- Case reductions:  ${mod.caseReductions}
      |-- Field reductions:  ${mod.fieldReductions}
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
    
    object AST extends HaskellAST(pp) {
      type Ident = Var
      def printIdent(id: Ident): String = printVar(id)
      def mkIdent(nameHint: String): Ident = bindVal(mkName(nameHint, ""))
      val dropUnusedLets = true
      val inlineOneShotLets = inlineScheduledLets
      val inlineTrivialLets = inlineScheduledLets
      val commonSubexprElim = true
      val useOnlyIntLits = self.useOnlyIntLits
    }
    
    val modDefStrings: List[Str] = mod.modDefs.map { case (name, ref) =>
      Sdebug(s"Scheduling $name")
      def rec(ref: Ref)(implicit ictx: Instr): AST.Expr = ScheduleDebug.nestDbg {
        Sdebug(s"[$ictx] ${ref.show}")
        ref.node match {
          case Control(i,b) =>
            rec(b)(ictx `;` i)
          case Branch(c,t,e) =>
            if (Condition.test_!(c,ictx)) rec(t) else rec(e)
          case IntBoxing(n) => constantToExpr(IntLit(true, n))
          case App(l,r) =>
            AST.App(rec(l), rec(r))
          case l @ Lam(_,b) =>
            AST.Lam(AST.Vari(l.param), Nil, rec(b)(ictx push DummyCallId))
          case v: Var => AST.Vari(v)
          case c: ConstantNode => constantToExpr(c)
          case Case(s,as) =>
            AST.Case(rec(s), as.map{ case (c,a,r) => (c, List.fill(a)(bindVal("ρ") |> AST.Vari), rec(r)) })
          case CtorField(s,c,a,i) => AST.CtorField(rec(s),c,a,i)
        }
      }
      val e = rec(ref)(Id)
      new AST.Defn(bindVal(name), Nil, Lazy(e)).stringify
    }
    
  }
  
  class SmartScheduler(val mod: GraphModule) extends Scheduler { scheduler =>
    
    object AST extends HaskellAST(pp) {
      type Ident = Var
      def printIdent(id: Ident): String = printVar(id)
      def mkIdent(nameHint: String): Ident = bindVal(mkName(nameHint, ""))
      val dropUnusedLets = false // TODO make unused lets an error?
      val inlineOneShotLets = inlineScheduledLets
      val inlineTrivialLets = inlineScheduledLets
      val commonSubexprElim = false
      //override val mergeLets = true
      val useOnlyIntLits = self.useOnlyIntLits
      override val mergeLets = !debugScheduling
    }
    
    /** Did anything significant change? — as in: a parameter, return, or performed test was added to a scope... */
    var anythingChanged = true
    
    val modDefStrings: List[Str] = {
      mod.modDefs.map { case (nme,df) =>
        Sdebug(s"Scheduling $nme")
        
        if (!debugScheduling) resetVarPrintingState()
        val ide = AST.mkIdent(nme)
        printVar(ide) // allocate the right 'bare' name for each top-level definition
        
        val rootScp = ScheduleDebug.nestDbg {
          val scp = new Scope(DummyCallId, Id, None) {
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
            rootScp.iterator.foreach { scp =>
              val ps = scp.potentiallyRecursiveParents
              //if (ps.nonEmpty) Sdebug(s"$scp has potentially recursive parents") thenReturn ScheduleDebug.nestDbg {
              if (ps.size > UnrollingFactor) Sdebug(s"$scp has potentially recursive parents") thenReturn ScheduleDebug.nestDbg {
                assert(scp.returns.isEmpty)
                scp.potentiallyRecursiveParents = ps.filter { p =>
                  Sdebug(s"Parent $p has tests: ${p.testsPerformed.map(cb => Condition.show(cb._1) + " == " + cb._2).mkString("; ")}")
                  val fails = ScheduleDebug.nestDbg { p.testsPerformed.exists { case (c, res) =>
                    val truth = Condition.test_!(c, scp.ictx)
                    Sdebug(s"Here: ${Condition.show(c)} == ${truth}")
                    scp.addTest(c, truth)
                    truth =/= res also { b => if (b) Sdebug(s"This was not a real recursive parent") }
                  }}
                  !fails
                }
                //if (scp.potentiallyRecursiveParents.isEmpty) Sdebug(s"Performing delayed entries of ${scp}") thenReturn ScheduleDebug.nestDbg {
                //if (scp.potentiallyRecursiveParents.nonEmpty) {
                if (scp.potentiallyRecursiveParents.size > UnrollingFactor) {
                  //scp.params = Nil // erasing what was potentially done in the loop below ("Now doing param..."), as it may no longer be valid
                } else Sdebug(s"Performing delayed entries of ${scp}") thenReturn ScheduleDebug.nestDbg {
                  changed = true
                  scp.params = Nil // erasing what was potentially done in the loop below ("Now doing param..."), as it may no longer be valid
                  val es = scp.delayedEntries
                  scp.delayedEntries = Nil
                  es.foreach{ case (in,ref) => scp.enter(in,ref) }
                }
              }
            }
          }
          rootScp.iterator.foreach { scp =>
            val ps = scp.potentiallyRecursiveParents
            //if (ps.nonEmpty) {
            if (ps.size > UnrollingFactor) {
              val recScp = ps.last
              Sdebug(s"Apparently recursive scope: ${scp} -> ${ps.last}")
              ScheduleDebug.nestDbg {
                scp.recursiveParent = Some(recScp)
                recScp.isRecursive = true
                recScp.params.foreach { case ((i,r),sch) =>
                  Sdebug(s"Now doing param $r a.k.a. ${recScp.printRef(i,r)} or ${recScp.toParamIdent(r)|>printVar} ($sch (${sch.toExpr.stringify}))")
                  // Should be the same as scp.parent.get.rec(i, r):
                  // (But note that scp.parent.get may very well NOT be recScp.)
                  scp.rec(Drop(i)(scp.cid), r)
                  //val res = scp.rec(Drop(i)(scp.cid), r)
                  //Sdebug(s"Got: $res a.k.a. ${res.toExpr} a.k.a. ${res.toExpr.stringify}")
                }
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
      def toExpr: AST.Expr = scp.recursiveParent match {
        case Some(rec) =>
          assert(rec.returns.size === 1) // TODO actually index into the callIdent if several returns
          AST.Vari(scp.callIdent)
        case None => if (scp.isRecursive) {
          assert(scp.returns.size === 1) // TODO actually index into the callIdent if several returns
          AST.Vari(scp.callIdent)
        } else AST.Vari(scp.toIdent(in,r))
      }
    }
    case class Lambda(v: Var, scp: Scope) extends Scheduled {
      def toExpr: AST.Expr = {
        val defn = scp.toDefn
        assert(defn.params.isEmpty)
        AST.Lam(AST.Vari(v), Nil, defn.body)
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
      def toVari(ref: IRef): AST.Vari = AST.Vari(toIdent(ref))
      def printRef(iref: IRef) = printVar(toIdent(iref))
      
      // We separate parameters to avoid clashes with arguments to the same recursive scopes:
      val paramIdents: mutable.Map[Ref, AST.Ident] = mutable.Map.empty // TODO should it also be indexed with the Instr?
      def toParamIdent(ref: Ref): AST.Ident = paramIdents.getOrElseUpdate(ref,
        AST.mkIdent(ref.name.filterNot(_.startsWith("β")).getOrElse("p")))
      
      def toArg(ref: (IRef, Scheduled)): AST.Expr = {
        Sdebug(s"?! toArg $ref ${ref._1 |> toIdent} ${ref._1 |> toIdent |> printVar} ${ref._2.toExpr} ${ref._2.toExpr.stringify}")
        AST.Vari(toIdent(ref._1))
        //ref._2.toExpr // refers to the wrong one!
      }
      
      var subScopes: List[Scope] = Nil
      parent.foreach(_.subScopes ::= this)
      
      def iterator: Iterator[Scope] = Iterator(this) ++ subScopes.iterator.flatMap(_.iterator)
      
      def parents: List[Scope] = parent.toList.flatMap(p => p :: p.parents)
      
      var potentiallyRecursiveParents = if (cid === DummyCallId) Nil else parents.filter(_.cid === cid)
      //var potentiallyRecursiveParents = if (cid === DummyCallId) Nil else parents.filter(_.cid.v === cid.v) // TODO try this?
      
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
      var returns: List[IRef -> Scheduled] = Nil
      var delayedEntries: List[IRef] = Nil
      
      var bindings: List[Either[IRef -> Scheduled, Call]] = Nil
      val bound: mutable.Set[IRef] = mutable.Set.empty
      
      def bind(iref: IRef, sch: Scheduled): Unit = if (!bound(iref)) { // we test again here as sometimes things are bound during calls to `rec`
        assert(!bound(iref))
        bound += iref
        bind_!(iref, sch)
      }
      /** Sometimes we want to manually set `bound` and later call this `bind_!` which does not check it. */
      def bind_!(iref: IRef, sch: Scheduled): Unit = {
        Sdebug(s"iBIND<$ident> ${iref|>printRef} = ${sch} (${iref|>toIdent} / ${iref})")
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
                  if (!body.toBeScheduledInline)
                    bound += Id->body
                  val res = c match {
                    case l @ Lam(_,b) =>
                      val scp = new Scope(DummyCallId, Id, Some(this))
                      Sdebug(s"New lambda at ${body} ${l.param} ${l.paramRef} ${scp.toIdent(Id->l.paramRef)} ${scp.toIdent(Id->l.paramRef)|>printVar}")
                      scp.enter(Id, b)
                      //Sdebug(s"?L? ${b} ${b|>scp.printRef} ${b|>printRef}")
                      Lambda(Id->l.paramRef |> scp.toIdent, scp) // TODO should the Instr really be Id here?
                    case _: Var =>
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
                rec(i,b)
              case Branch(c,t,e) =>
                if (bound(Id->body)) {
                  Sdebug(s"BRANCH—already bound! $c $body ${toIdent(iref)|>printVar} ${asVar.toExpr.stringify}  as  ${asVar} / ${asVar.toExpr.stringify}")
                  asVar
                } else {
                  val truth = Condition.test_!(c, ictx)
                  addTest(c, truth)
                  val res = if (truth) rec(Id,t) else rec(Id,e)
                  Sdebug(s"BRANCH $c ${res} $body ${toIdent(iref)|>printVar}")
                  bind(iref, res)
                  asVar
                }
            }
            
          case Pop(i) =>
            Sdebug(s"Pop to ${parent.get} with $payload")
            if (!bound(iref)) {
              val res = parent.get.rec(payload `;` i, body)
              bind(iref, res)
            }
            asVar
            
          case Drop(i) =>
            Sdebug(s"Drop to ${parent.get}")
            if (params.exists(_._1._2 === body)) {
              Sdebug(s"PARAM<$ident> ${body} already exists")
            } else {
              anythingChanged = true
              val res = parent.get.rec(i, body)
              params ::= i -> body -> res
              Sdebug(s"PARAM<$ident> ${body} ==> ${res} ==> ${toParamIdent(body)} (${toParamIdent(body)|>printVar})")
              //Sdebug(s"?Drop? ${body} ${bound(body)} ${toParamIdent(body)}/${toParamIdent(body)|>printVar} ${parent.get.toIdent(body)}/${parent.get.toIdent(body)|>printVar}")
            }
            Concrete(toParamIdent(body), Nil)
            
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
      //def enter(in: Instr, body: Ref): Unit = if (potentiallyRecursiveParents.nonEmpty) {
      def enter(in: Instr, body: Ref): Unit = if (potentiallyRecursiveParents.size > UnrollingFactor) {
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
            if (!scp.isRecursive && scp.recursiveParent.isEmpty) {
              val ret = scp.returns.foldRight(rest) {
                case ((ref, sch), rest) =>
                  Sdebug(s"BIND RETURN ${scp.toVari(ref)}:${scp.printRef(ref)} = ${sch} (${sch.toExpr.stringify})")
                  if (sch.isVari) rest // if it's already a variable, no need to bind it again
                  else Left((scp.toVari(ref), sch.toExpr)) :: rest
              }
              val rest2 = scp.foldBindings(ret)
              scp.params.foldRight(rest2) {
                case (((i,p),sch), rest2) =>
                  //Sdebug(s"!!! LET ${p}=$sch ${toIdent(p)} ${toIdent(p)|>printVar}")
                  //if (sch.isVari) rest else // FIXME?
                  Left((scp.toParamIdent(p)|>AST.Vari, sch.toExpr)) :: rest2
                  //Left((scp.toParamIdent(p)|>AST.Vari, sch.toExpr)) :: Left((scp.toIdent((i,p))|>AST.Vari, scp.toParamIdent(p)|>AST.Vari)) :: rest2
              }
            }
            else if (scp.isRecursive) {
              assert(scp.recursiveParent.isEmpty)
              val d = scp.defn
              Sdebug(s"CALL REC $scp")
              val call = AST.Call(d, scp.params.map(toArg))
              Right(d.value) :: Left((AST.Vari(scp.callIdent), call)) :: rest
            } else {
              assert(scp.recursiveParent.isDefined)
              val p = scp.recursiveParent.get
              Sdebug(s"REC CALL $scp -> $p")
              val call = AST.Call(p.defn, p.params.map(toArg))
              Left((AST.Vari(scp.callIdent), call)) :: rest
            }
        }
      }
      
      def toDefn: AST.Defn = Sdebug(s"Defn of $this") thenReturn ScheduleDebug.nestDbg {
        new AST.Defn(ident, params.map(_._1._2 |> toParamIdent |> AST.Vari), Lazy {
          val ret = returns match {
            case Nil => die
            case (_, r) :: Nil => r.toExpr
            case _ => returns.foldLeft[AST.Expr](AST.Inline("(" + "," * (returns.size - 1) + ")")) {
              case (app, (_, r)) => AST.App(app, r.toExpr) }
          }
          Sdebug(s"Ret: ${ret.stringify}")
          Sdebug(s"Bindings: ${bindings.reverse}")
          val defs = foldBindings(Nil)
          
          val body = AST.Let.reorder(defs, ret) // note: will access the Defn of recursive definitions that are being defined here...
          //val body = AST.Let.multiple(defs, ret)
          
          body
        })
      } //also (res => Sdebug(s"=> ${res.stringify}")) // can cause stack overflow as it forces the body
      
      def show(indent: Int = 0): Str = {
        val pre = "\n" + "  " * (indent+1)
        val rets = returns.map {
          case (r,s) => (if (!s.isVari) s"${printRef(r)}=" else "") + s.toExpr.stringify
        }.mkString(", ")
        val aliases = (idents.iterator.map{ case (ref, ide) => s"${ide|>printVar} = [${ref._1}]${ref._2.showName}" }
          ++ paramIdents.iterator.map { case (ref, ide) => s"${ide|>printVar} = ${ref.showName}" }
        ).mkString(s"   [ "," ; "," ]")
        val outerTests = if (testsPerformed.isEmpty) "" else s"${pre}Outer-tests: ${testsPerformed.map{
            case (c, b) => (if (b) "" else "!")+Condition.show(c)
          }.mkString(" && ")
        }"
        def defs = bindings.reverse.map{
          case Left((r,Lambda(v, scp))) => s"$pre${printRef(r)} = \\${v|>printVar} -> ${scp.show(indent + 1)}"
          case Left((r,s)) => s"$pre${printRef(r)} = "+s.toExpr.stringify //+ s"   \t\t(${s})"
          case Right(c) => pre + c.scp.show(indent + 1)
        }.mkString
        s"$toString: (${params.map(_._1._2 |> toParamIdent |> printVar).mkString(", ")}) -> ($rets) where$aliases$outerTests${defs}"
      }
      
      override def toString = s"<${ident.toString}>(${Push(cid,payload,Id)})"
    }
    
  }
  
}
