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
    val modDefs: List[AST.Defn]
    
    private val nameCounts = mutable.Map.empty[String, Int]
    private val nameAliases = mutable.Map.empty[Var, String]
    protected def printVar(v: Var): String = if (v === WildcardVal) "_" else nameAliases.getOrElseUpdate(v, {
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
    
    protected def constantToExpr(c: ConstantNode): AST.Expr = c match {
      case IntLit(true, n) => AST.Inline(n.toString)
      case IntLit(false, n) => AST.Inline(s"$n#")
      case StrLit(true, s) => AST.Inline(s.toString)
      case StrLit(false, s) => AST.Inline(s"$s#")
      case ModuleRef(m, n) =>
        val str = if (knownModule(m)) n else s"$m.$n"
        AST.Inline(if (n.head.isLetter) str else s"($str)")
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
    }
    
    val modDefs: List[AST.Defn] = mod.modDefs.map { case (name, ref) =>
      Sdebug(s"Scheduling $name")
      def rec(ref: Ref)(implicit ictx: Instr): AST.Expr = ScheduleDebug.nestDbg {
        Sdebug(s"[$ictx] ${ref.show}")
        ref.node match {
          case Control(i,b) =>
            rec(b)(ictx `;` i)
          case Branch(c,t,e) =>
            if (Condition.test_!(c,ictx)) rec(t) else rec(e)
          case IntBoxing(n) => AST.Inline(n.toString)
          case App(l,r) =>
            AST.App(rec(l), rec(r))
          case l @ Lam(_,b) =>
            AST.Lam(AST.Vari(l.param), Nil, rec(b)(ictx push DummyCallId))
          case v: Var => AST.Vari(v)
          case c: ConstantNode => constantToExpr(c)
          case Case(s,as) => ???
          case CtorField(s,c,i) => ???
        }
      }
      val e = rec(ref)(Id)
      new AST.Defn(bindVal(name), Nil, Lazy(e))
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
      override val mergeLets = true
    }
    
    val modDefs: List[AST.Defn] = {
      val withIdents = mod.modDefs.map { case (nme,df) =>
        val ident = AST.mkIdent(nme)
        printVar(ident) // allocate the right 'bare' name for each top-level definition
        (nme,df,ident)
      }
      withIdents.map { case (nme,df,ide) =>
        Sdebug(s"Scheduling $nme")
        val rootScp = ScheduleDebug.nestDbg {
          val scp = new Scope(DummyCallId, Id, None) {
            override val ident = ide
          }
          scp.enter(Id, df)
          scp
        }
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
            if (ps.nonEmpty) {
              assert(scp.returns.isEmpty)
              scp.potentiallyRecursiveParents = ps.filter { p =>
                //changed = true
                true // TODO <================================================================== !!actually check tests!!
              } 
              if (scp.potentiallyRecursiveParents.isEmpty) Sdebug(s"Performing delayed entries of ${scp}") thenReturn ScheduleDebug.nestDbg {
                changed = true
                val es = scp.delayedEntries
                scp.delayedEntries = Nil
                es.foreach{ case (in,ref) => scp.enter(in,ref) }
              }
            }
          }
        }
        rootScp.iterator.foreach { scp =>
          val ps = scp.potentiallyRecursiveParents
          if (ps.nonEmpty) {
            Sdebug(s"Recursive scope: ${scp}")
            val rec = ps.last
            scp.recursiveParent = Some(rec)
            rec.isRecursive = true
            rec.params.foreach { case ((i,r),sch) =>
              Sdebug(s"Now doing param $r in $scp")
              scp.rec(Drop(i)(scp.cid), r)
            }
          }
        }
        Sdebug(s">>>>>> FINAL RESULT")
        Sdebug(rootScp.show())
        Sdebug(s">>>>>> /")
        rootScp.toDefn
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
        case IntBoxing(n) => AST.Inline(n.toString)
        case App(l, r) => AST.App(children(0).toExpr, children(1).toExpr)
        case l @ Lam(_,lbody) => die // we handle lambdas specially
        case v: Var => AST.Vari(v)
        case c: ConstantNode => constantToExpr(c)
        case Case(s,as) => ???
        case CtorField(s,c,i) => ???
      }
      override def toString = if (children.isEmpty || nde.isSimple) nde.toString else children.mkString(s"($nde)[",",","]")
    }
    case class ScopeAccess(scp: Scope, v: Var, r: Ref) extends Scheduled {
      def toExpr: AST.Expr = scp.recursiveParent match {
        case Some(rec) =>
          assert(rec.returns.size === 1) // TODO actually index into the callIdent if several returns
          AST.Vari(scp.callIdent)
        case None => if (scp.isRecursive) {
          assert(scp.returns.size === 1) // TODO actually index into the callIdent if several returns
          AST.Vari(scp.callIdent)
        } else scp.toVari(r)
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
    
    type Binding = Either[Ref -> Scheduled, Call]
    
    class Scope(val cid: CallId, val payload: Instr, val parent: Opt[Scope]) {
      
      val ictx: Instr = parent.map(_.ictx).getOrElse(Id) `;` Push(cid, payload, Id)
      
      val ident = AST.mkIdent("rec")
      val callIdent = AST.mkIdent("rec_call")
      
      lazy val push = Push(cid,payload,Id)
      
      val idents: mutable.Map[Ref, AST.Ident] = mutable.Map.empty
      def toIdent(ref: Ref): AST.Ident = idents.getOrElseUpdate(ref, AST.mkIdent(ref.name.getOrElse("")))
      def toVari(ref: Ref): AST.Vari = AST.Vari(toIdent(ref))
      def printRef(ref: Ref) = printVar(toIdent(ref))
      
      // We separate parameters to avoid clashes with arguments to the same recursive scopes:
      val paramIdents: mutable.Map[Ref, AST.Ident] = mutable.Map.empty
      def toParamIdent(ref: Ref): AST.Ident = paramIdents.getOrElseUpdate(ref, AST.mkIdent(ref.name.getOrElse("")))
      
      def toArg(ref: ((Instr,Ref), Scheduled)): AST.Expr = {
        Sdebug(s"?! toArg $ref ${ref._1._2 |> toIdent} ${ref._1._2 |> toIdent |> printVar} ${ref._2.toExpr} ${ref._2.toExpr.stringify}")
        AST.Vari(toIdent(ref._1._2))
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
        assert(testsPerformed.get(c).forall(_ === truth))
        testsPerformed += c -> truth
        parent.foreach(p => Condition.throughControl(push,c).foreach(c => p.addTest(c, truth)))
      }
      
      var params: List[Instr -> Ref -> Scheduled] = Nil
      var returns: List[Ref -> Scheduled] = Nil
      var bindings: List[Binding] = Nil
      val bound: mutable.Set[Ref] = mutable.Set.empty
      
      var delayedEntries: List[Instr -> Ref] = Nil
      
      def bind(ref: Ref, sch: Scheduled): Unit = if (!bound(ref)) { // we test again here as sometimes things are bound during calls to `rec`
        assert(!bound(ref))
        bound += ref
        bind_!(ref, sch)
      }
      def bind_!(ref: Ref, sch: Scheduled): Unit = {
        Sdebug(s"BIND<$ident> ${ref|>printRef} = ${sch} (${ref|>toIdent} / ${ref})")
        bindings ::= Left(ref -> sch)
      }
      
      /* Important remark: each NodeRef can be associated with a unique ConcreteNode per scope (possibly a variable) */
      def rec(in: Instr, body: Ref): Scheduled = ScheduleDebug.nestDbg {
        Sdebug(s"<${ident}> [$in] ${body.showDef}")
        //if (ScheduleDebug.isDebugEnabled) Thread.sleep(50)
        
        def asVar = Concrete(toIdent(body), Nil)
        
        in match {
            
          case Id =>
            body.node match {
              case c: ConcreteNode =>
                if (bound(body)) asVar else {
                  if (!body.toBeScheduledInline)
                    bound += body
                  val res = c match {
                    case l @ Lam(_,b) =>
                      val scp = new Scope(DummyCallId, Id, Some(this))
                      Sdebug(s"New lambda at ${body} ${l.param} ${l.paramRef} ${scp.toIdent(l.paramRef)} ${scp.toIdent(l.paramRef)|>printVar}")
                      scp.enter(Id, b)
                      //Sdebug(s"?L? ${b} ${b|>scp.printRef} ${b|>printRef}")
                      Lambda(l.paramRef |> scp.toIdent, scp)
                    case _: Var =>
                      Concrete(body |> toIdent, Nil)
                      //Concrete(v, Nil)
                    case _ =>
                      Concrete(c, c.children.map(rec(Id, _)).toList)
                  }
                  if (body.toBeScheduledInline) res
                  else {
                    bind_!(body, res)
                    asVar
                  }
                }
              case Control(i,b) =>
                rec(i,b)
              case Branch(c,t,e) =>
                if (bound(body)) {
                  Sdebug(s"BRANCH—already bound! $c $body ${toIdent(body)|>printVar} ${asVar.toExpr.stringify}")
                  asVar
                } else {
                  val truth = Condition.test_!(c, ictx)
                  addTest(c, truth)
                  val res = if (truth) rec(Id,t) else rec(Id,e)
                  Sdebug(s"BRANCH $c ${res} $body ${toIdent(body)|>printVar}")
                  bind(body, res)
                  asVar
                }
            }
            
          case Pop(i) =>
            Sdebug(s"Pop to ${parent.get} with $payload")
            if (!bound(body)) {
              val res = parent.get.rec(payload `;` i, body)
              bind(body, res)
            }
            asVar
            
          case Drop(i) =>
            Sdebug(s"Drop to ${parent.get}")
            if (params.exists(_._1._2 === body)) {
              Sdebug(s"PARAM<$ident> ${body} already exists")
            } else {
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
            ScopeAccess(scp, scp.ident, body)
            
        }
      }
      def enter(in: Instr, body: Ref): Unit = if (potentiallyRecursiveParents.nonEmpty) {
        Sdebug(s"Cannot enter $this yet, as it's possibly recursive (${potentiallyRecursiveParents.mkString(", ")})")
        delayedEntries ::= (in,body)
      } else {
        Sdebug(s"Enter $this")
        val ret = rec(in, body)
        //Sdebug(s"Return $ret")
        Sdebug(s"Return $ret ${ret.toExpr} ${ret.toExpr.stringify} ${body|>toIdent} ${body|>toIdent|>printVar}")
        returns ::= body -> ret
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
                  if (sch.isVari) rest // if it's already a variable, no need to bind it again
                  else Left((scp.toVari(ref), sch.toExpr)) :: rest
              }
              val rest2 = scp.foldBindings(ret)
              scp.params.foldRight(rest2) {
                case (((i,p),sch), rest2) =>
                  //Sdebug(s"!!! LET ${p}=$sch ${toIdent(p)} ${toIdent(p)|>printVar}")
                  //if (sch.isVari) rest else // FIXME?
                  Left((scp.toParamIdent(p)|>AST.Vari, sch.toExpr)) :: rest2
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
            case _ => returns.foldLeft[AST.Expr](AST.Inline("(" + "," * returns.size + ")")) {
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
        val aliases = (idents.iterator ++ paramIdents.iterator).map { case (ref, ide) => s"${ide|>printVar} = ${ref.showName}" }
          .mkString(s"   [ "," ; "," ]")
        val outerTests = if (testsPerformed.isEmpty) "" else s"${pre}Outer-tests: ${testsPerformed.map{
            case (c, b) => (if (b) "" else "!")+Condition.show(c)
          }.mkString(" && ")
        }"
        def defs = bindings.reverse.map{
          case Left((r,Lambda(v, scp))) => s"$pre${printRef(r)} = \\${v|>printVar} -> ${scp.show(indent + 1)}"
          case Left((r,s)) => s"$pre${printRef(r)} = "+s.toExpr.stringify
          case Right(c) => pre + c.scp.show(indent + 1)
        }.mkString
        s"$toString: (${params.map(_._1._2 |> toParamIdent |> printVar).mkString(", ")}) -> ($rets) where$aliases$outerTests${defs}"
      }
      
      override def toString = s"<${ident.toString}>(${Push(cid,payload,Id)})"
    }
    
  }
  
}
