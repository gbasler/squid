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
      UnboxedTupleParameters
      //CurriedParameters
    
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
      val inlineOneShotLets = true
      val inlineTrivialLets = true
      val commonSubexprElim = true
    }
    
    val modDefs: List[AST.Defn] = mod.modDefs.map { case (name, ref) =>
      def rec(ref: Ref)(implicit ictx: Instr): AST.Expr = ref.node match {
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
      val e = rec(ref)(Id)
      new AST.Defn(bindVal(name), Nil, e)
    }
    
  }
  
  class SmartScheduler(val mod: GraphModule) extends Scheduler { scheduler =>
    
    object AST extends HaskellAST(pp) {
      type Ident = Var
      def printIdent(id: Ident): String = printVar(id)
      def mkIdent(nameHint: String): Ident = bindVal(mkName(nameHint, ""))
      val dropUnusedLets = false // TODO make unused lets an error?
      val inlineOneShotLets = true
      val inlineTrivialLets = true
      val commonSubexprElim = false
      //override val mergeLets = true
    }
    
    val idents: mutable.Map[(Scope,Ref), AST.Ident] = mutable.Map.empty
    def toIdent(scp: Scope, ref: Ref): AST.Ident = idents.getOrElseUpdate((scp,ref), AST.mkIdent(ref.name.getOrElse("")))
    def toVari(scp: Scope, ref: Ref): AST.Vari = AST.Vari(toIdent(scp, ref))
    
    val modDefs: List[AST.Defn] = {
      val withIdents = mod.modDefs.map { case (nme,df) =>
        val ident = AST.mkIdent(nme)
        printVar(ident) // allocates
        (nme,df,ident)
      }
      withIdents.map { case (nme,df,ide) =>
        Sdebug(s"Scheduling $nme")
        val scp = ScheduleDebug.nestDbg {
          val scp = new Scope(DummyCallId, Id, None) {
            override val ident = ide
          }
          scp.enter(Id, df)
          scp
        }
        Sdebug(s">>>>>> RESULT")
        Sdebug(scp.show())
        Sdebug(s">>>>>> /")
        scp.toDefn
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
    // TODO handle ScopeAccess correctly:
    case class ScopeAccess(scp: Scope, v: Var, r: Ref) extends Scheduled {
      //def toExpr: AST.Expr = AST.Inline(s"$v#${r|>toVari}") // TODO
      def toExpr: AST.Expr = toVari(scp, r)
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
      
      def toIdent(ref: Ref): AST.Ident = scheduler.toIdent(this, ref)
      def printRef(ref: Ref) = printVar(toIdent(ref))
      
      val subScopes: mutable.Map[CallId, Scope] = mutable.Map.empty
      var params: List[Ref] = Nil
      var returns: List[Ref -> Scheduled] = Nil
      var captures: List[Ref] = Nil // TODO use or rm
      var bindings: List[Binding] = Nil
      val bound: mutable.Set[Ref] = mutable.Set.empty
      
      def bind(ref: Ref, sch: Scheduled): Unit = if (!bound(ref)) { // we test again here as sometimes things are bound during calls to `rec`
        Sdebug(s"BIND ${ref|>printRef} = ${sch} (${ref|>toIdent} / ${ref})")
        assert(!bound(ref))
        bound += ref
        bindings ::= Left(ref -> sch)
      }
      
      def rec(in: Instr, body: Ref): Scheduled = ScheduleDebug.nestDbg {
        Sdebug(s"<${ident}> [$in] ${body.defstr}")
        in match {
            
          case Id =>
            def asVar = Concrete(toIdent(body), Nil)
            body.node match {
              case c: ConcreteNode =>
                if (bound(body)) asVar else {
                  val res = c match {
                    case l @ Lam(_,b) =>
                      Sdebug(s"New lambda at ${body}")
                      val scp = new Scope(DummyCallId, Id, Some(this))
                      scp.enter(Id, b)
                      Lambda(l.param, scp)
                    case _ =>
                      Concrete(c, c.children.map(rec(Id, _)).toList)
                  }
                  if (body.toBeScheduledInline) res
                  else {
                    bind(body, res)
                    asVar
                  }
                }
              case Control(i,b) =>
                rec(i,b)
              case Branch(c,t,e) =>
                if (bound(body)) {
                  asVar
                } else {
                  val res = if (Condition.test_!(c, ictx)) rec(Id,t) else rec(Id,e)
                  bind(body, res)
                  asVar
                }
            }
            
          case Pop(i) =>
            Sdebug(s"Pop to ${parent.get} with $payload")
            if (!captures.contains(body))
              captures ::= body
            parent.get.rec(payload `;` i, body)
            
          case Drop(i) =>
            Sdebug(s"Drop to ${parent.get}")
            if (!params.contains(body))
              params ::= body
            parent.get.rec(i, body)
            
          case Push(cid,p,r) =>
            Sdebug(s"Push ${cid}")
            var isNew = false
            val scp = subScopes.getOrElseUpdate(cid, {
              val scp = new Scope(cid, p, Some(this))
              isNew = true
              scp
            })
            assert(scp.payload === p, (scp.payload,p))
            scp.enter(r, body)
            if (isNew) bindings ::= Right(Call(scp))
            ScopeAccess(scp, scp.ident, body)
            
        }
      }
      def enter(in: Instr, body: Ref): Unit = {
        Sdebug(s"Enter $this")
        val ret = rec(in, body)
        Sdebug(s"Return $ret")
        returns ::= body -> ret
      }
      val ident = AST.mkIdent("SCOPE")
      
      def foldBindings(ret: AST.Expr): AST.Expr = bindings.foldLeft(ret) {
        case (rest, Left(r -> b)) =>
          val e = b.toExpr
          AST.Let(toVari(this, r), e, rest) //also (res => Sdebug(s"LET ${r|>printRef} / ${e.stringify} / ${rest.stringify}  ==>  ${res.stringify}"))
        case (rest, Right(Call(scp))) => // TODO only for non-recursive calls
          val ret = scp.returns.foldRight(rest) {
            case ((ref, sch), rest) =>
              if (sch.isVari) rest // if it's already a variable, no need to bind it again
              else AST.Let(toVari(scp,ref), sch.toExpr, rest)
          }
          scp.foldBindings(ret)
      }
      def toDefn: AST.Defn = Sdebug(s"Defn of $this") thenReturn ScheduleDebug.nestDbg {
        val ret = returns match {
          case Nil => die
          case (_, r) :: Nil => r.toExpr
          case _ => returns.foldLeft[AST.Expr](AST.Inline("(" + "," * returns.size + ")")) {
            case (app, (_, r)) => AST.App(app, r.toExpr) }
        }
        Sdebug(s"Ret: ${ret.stringify}")
        Sdebug(s"Bindings: ${bindings.reverse}")
        val body = foldBindings(ret)
        Sdebug(s"Body: $body")
        new AST.Defn(ident, params.map(_ |> toIdent |> AST.Vari), body)
      } also (res => Sdebug(s"=> ${res.stringify}"))
      
      def show(indent: Int = 0): Str = {
        val pre = "\n" + "  " * (indent+1)
        s"$toString: (${params.map(printRef).mkString(", ")}) -> (${returns.map {
          case (r,s) => (if (!s.isVari) s"${printRef(r)}=" else "") + s.toExpr.stringify
        }.mkString(", ")}) where${bindings.reverse.map{
          case Left((r,Lambda(v, scp))) => s"$pre${printRef(r)} = \\${v|>printVar} -> ${scp.show(indent + 1)}"
          case Left((r,s)) => s"$pre${printRef(r)} = "+s.toExpr.stringify
          case Right(c) => pre + c.scp.show(indent + 1)
        }.mkString}"
      }
      
      override def toString = s"<${ident.toString}>(${Push(cid,payload,Id)})"
    }
    
  }
  
}
