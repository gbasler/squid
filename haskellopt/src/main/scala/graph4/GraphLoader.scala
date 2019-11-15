package graph4

import squid.utils._
import squid.haskellopt.ghcdump
import squid.ir.graph3._
import ammonite.ops.FilePath

import scala.collection.mutable

class GraphLoader[G <: GraphIR](val Graph: G) {
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
  (The -O flag could have been useful, as it makes list literals be represented as build.)
  So it's probably best to selectively disable optimizations, such as the application of rewrite rules.
  
  */
  
  import Graph._
  
  val imports = mutable.Set.empty[String]
  
  def loadFromDump(dump: FilePath, prefixFilter: Str): GraphModule = {
    implicit val dummyModule: Module = DummyModule
    
    var modName = Option.empty[String]
    val moduleBindings = mutable.Map.empty[GraphDumpInterpreter.BinderId, String -> Ref]
    
    var reificationContext = Map.empty[Var, Lazy[NodeRef]]
    
    val DummyNode = bindVal("<dummy>")
    
    object GraphDumpInterpreter extends ghcdump.Interpreter {
      
      type Expr = Ref
      type Lit = ConstantNode
      type Alt = (AltCon, () => Expr, Ref => Map[BinderId, Ref])
      
      val bindings = mutable.Map.empty[BinderId, Either[Var, Ref]] // Left for lambda- or let-bound; Right for module-bound
      
      val TypeBinding = bindVal("ty")
      val DummyRead = TypeBinding.mkRef
      
      def EVar(b: BinderId): Expr = bindings(b).fold(reificationContext(_).value, id)
      
      def EVarGlobal(ExternalName: ExternalName): Expr = {
        val mod = ExternalName.externalModuleName
        val nme = ExternalName.externalName
        if (mod === modName.get) moduleBindings(ExternalName.externalUnique)._2
        else ModuleRef(ExternalName.externalModuleName, {
          mod match {
            case "GHC.Integer.Type" =>
            case _ => imports += mod
          }
          nme
        }).mkRef
      }
      def ELit(Lit: Lit): Expr = Lit.mkRefNamed("κ")
      def EApp(e0: Expr, e1: Expr): Expr = e1.node match {
        case TypeBinding => e0
        case Control(_,Ref(TypeBinding)) => e0
        case ModuleRef(mod,nme) if nme.contains("$f") => e0
        case Control(_,Ref(ModuleRef(mod,nme))) if nme.contains("$f") => e0 // TODO more robust?
        case _ => e0.node match {
          case ModuleRef("GHC.Num", "fromInteger") if useOnlyIntLits => e1
          case ModuleRef("Control.Exception.Base", "patError") => Bottom.mkRef
          case _ => App(e0, e1).mkRefNamed("β") // ("α")
        }
      }
      def ETyLam(bndr: Binder, e0: Expr): Expr = e0 // Don't represent type lambdas...
      def ELam(bndr: Binder, mkE: => Expr): Expr = {
        val name = mkName(bndr.binderName, bndr.binderId.name)
        val param = bindVal(name)
        bindings += bndr.binderId -> Left(param)
        require(!reificationContext.contains(param))
        val old = reificationContext
        try if (bndr.binderName.startsWith("$")) { // ignore type and type class lambdas
          reificationContext += param -> Lazy(DummyRead)
          mkE
        }
        else {
          val occ = param.mkRefNamed(name)
          reificationContext = reificationContext.map { case(v,r) => (v, Lazy(Control.mkRef(Pop.it(param),r.value, 0, 0))) }
          reificationContext += param -> Lazy(occ)
          Lam(occ, mkE).mkRefNamed("λ")
        } finally reificationContext = old
      }
      def ELet(lets: Seq[(Binder, () => Expr)], e0: => Expr): Expr = {
        //println(s"LETS ${lets}")
        val old = reificationContext
        try lets.foldRight(() => e0) {
          case ((bndr, rhs), body) =>
            val name = mkName(bndr.binderName, bndr.binderId.name)
            //println(s"LET ${name}")
            val v = bindVal(name)
            val rv = DummyNode.mkRefNamed(name)
            require(!reificationContext.contains(v))
            reificationContext += v -> Lazy(rv)
            bindings += bndr.binderId -> Left(v)
            () => {
              //println(s"LET' ${name} ${reificationContext}")
              val parNum = rv.references.size
              val bod = rhs()
              setMeaningfulName(bod, name)
              rv.rewireTo(bod, if (bod.iterator.exists(_ === rv)) 1 else 0) // If the binding refers to itself, it needs a recursion marker.
              // Change context to point directly to the let-bound def (the indirection was only needed for recursive occurrences):
              reificationContext += v -> Lazy(bod)
              body()
            }
        }()
        finally reificationContext = old
      }
      def ECase(e0: Expr, bndr: Binder, alts: Seq[Alt]): Expr =
        // TODO handle special case where the only alt is AltDefault?
      {
        val old = reificationContext
        try {
          val v = bindVal("scrut")
          
          reificationContext += v -> Lazy(e0)
          // ^ It looks like this is sometimes used; probably if the scrutinee variable is accessed directly (instead of the ctor-index-getters)
          bindings += bndr.binderId -> Left(v)
          
          val altValues = alts.map { case (c,r,f) =>
            val rs = f(e0)
            
            val rsv = rs.map(bindVal("dummy") -> _)
            reificationContext ++= rsv.map(r => r._1 -> Lazy(r._2._2))
            bindings ++= rsv.map(r => r._2._1 -> Left(r._1))
            
            (c, rs.size, r)
          }
          val arms = altValues.reverseIterator.map { case (con,arity,rhs) =>
            (con match {
              case AltDataCon(name) => name
              case AltDefault => "_"
              case AltLit(_) => ??? // TODO
            },
            arity,
            rhs())
          }.toList
          assert(arms.nonEmpty)
          if (ignoreSingleArmCases && arms.size === 1)
            arms.head._3
          else Case(e0, arms).mkRefNamed("ψ")
        } finally reificationContext = old
      }
      def EType(ty: Type): Expr = DummyRead
      
      def Alt(altCon: AltCon, altBinders: Seq[Binder], altRHS: => Expr): Alt = {
        (altCon, () => altRHS, r => altBinders.zipWithIndex.map(b => b._1.binderId ->
          CtorField(
            r,
            altCon.asInstanceOf[AltDataCon] // FIXME properly handle the alternatives
              .name,
            altBinders.size,
            b._2).mkRefNamed("π") // was: σ
        ).toMap)
      }
      
      def LitInteger(n: Int): Lit = IntLit(true, n)
      def MachInt(n: Int): Lit = IntLit(false, n)
      def MachStr(s: String): Lit = StrLit(false, s)
      def MachChar(c: Char): Lit = CharLit(false, c)
      
    }
    val mod = ghcdump.Reader(dump, GraphDumpInterpreter)
    modName = Some(mod.moduleName)
    
    mod.moduleTopBindings.foreach { tb =>
      val rv = DummyNode.mkRefNamed(tb.bndr.binderName)
      moduleBindings += tb.bndr.binderId -> (tb.bndr.binderName -> rv)
      GraphDumpInterpreter.bindings += tb.bndr.binderId -> Right(rv)
    }
    
    GraphModule(mod.moduleName, mod.modulePhase, {
      var foundMain = false
      mod.moduleTopBindings.iterator
        .filter(b =>
          b.bndr.binderName =/= "$trModule" && (b.bndr.binderName =/= "main" || {!foundMain alsoDo {foundMain = true}}))
        .map(tb => tb.bndr.binderName -> {
          val (nme,rv) = moduleBindings(tb.bndr.binderId)
          val parNum = rv.references.size
          val e = tb.expr // compute the lazy value
          rv.rewireTo(e,
            if (e.iterator.exists(_ === rv)) 1 else 0) // If the binding refers to itself, it needs a recursion marker.
          setMeaningfulName(tb.expr, nme)
          rv
        }).toList.reverse.filter(_._1 + " " startsWith prefixFilter)
      }
    )
    
  }
  
}
