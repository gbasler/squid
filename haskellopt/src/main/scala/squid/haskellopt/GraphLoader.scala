package squid
package haskellopt

import squid.utils._
import squid.ir.graph3._
import ammonite.ops.FilePath
import squid.haskellopt.ghcdump.Interpreter

import scala.collection.mutable

class GraphLoader {
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
  Yet, the -O flag is useful, as it makes list literals be represented as build.
  So it's probably best to selectively disable optimizations, such as the application of rewrite rules (a shame, really).
  
  */
  
  object Graph extends HaskellGraph {
    val DummyTyp = Any
    override def staticModuleType(name: String): TypeRep = DummyTyp
    def mkName(base: String, idDomain: String) =
      //base + "_" + idDomain + freshName  // gives names that are pretty cluttered...
      base + freshName
    def setMeaningfulName(v: Val, n: String): Unit = {
      //println(s"Set $v -> $n")
      if (v.name.contains('$')) v.name_! = n
    }
  }
  import Graph.PgrmModule
  
  val imports = mutable.Set.empty[String]
  
  def loadFromDump(dump: FilePath): PgrmModule = {
    import Graph.{DummyTyp => dt, UnboxedMarker}
    
    var modName = Option.empty[String]
    val moduleBindings = mutable.Map.empty[GraphDumpInterpreter.BinderId, String -> Graph.Rep]
    
    object GraphDumpInterpreter extends ghcdump.Interpreter {
      
      type Expr = Graph.Rep
      type Lit = Graph.ConstantLike
      type Alt = (AltCon, () => Expr, Graph.Rep => Map[BinderId, Graph.Rep])
      
      val bindings = mutable.Map.empty[BinderId, Either[Graph.Val, Graph.Rep]] // Left for lambda-bound; Right for let-bound
      
      val TypeBinding = Graph.bindVal("ty", dt, Nil)
      val DummyRead = TypeBinding |> Graph.mkValRep
      
      def EVar(b: BinderId): Expr = bindings(b).fold(_ |> Graph.readVal, id)
      def EVarGlobal(ExternalName: ExternalName): Expr =
        if (ExternalName.externalModuleName === modName.get) moduleBindings(ExternalName.externalUnique)._2 else
        //Graph.module(Graph.staticModule(ExternalName.externalModuleName), ExternalName.externalName, dummyTyp)
        Graph.staticModule {
          val mod = ExternalName.externalModuleName
          val nme = ExternalName.externalName
          (if (nme.head.isLetter) s"$mod.$nme" else s"($mod.$nme)") match {
            // weird special cases in GHC:
            case "(GHC.Types.:)" => "(:)"
            case "(GHC.Types.[])" => "[]"
            case "(GHC.Tuple.())" => "()"
            case "(GHC.Tuple.(,))" => "(,)" // TODO other tuple arities...
            case n => mod match {
              case "GHC.Integer.Type" =>
              case _ => imports += mod
            }
            n
          }
        }
      def ELit(Lit: Lit): Expr = Graph.rep(Lit)
      def EApp(e0: Expr, e1: Expr): Expr = e1.node match {
        //case Graph.ConcreteNode(v: Graph.Val) if ignoredBindings(v) => e0
        case Graph.ConcreteNode(TypeBinding) => e0
        case Graph.Box(_,Graph.Rep(Graph.ConcreteNode(TypeBinding))) => e0 // the reification context may wrap up occurrences in a box!
        case Graph.ConcreteNode(Graph.StaticModule(nme)) if nme.contains("$f") => // TODO more robust?
          // ^ such as '$fNumInteger' in '$27.apply(GHC.Num.$fNumInteger)'
          e0
        case _ =>
           Graph.app(e0, e1)(dt)
      }
      def ETyLam(bndr: Binder, e0: Expr): Expr = e0 // Don't represent type lambdas...
      def ELam(bndr: Binder, e0: => Expr): Expr = {
        val v = Graph.bindVal(Graph.mkName(bndr.binderName, bndr.binderId.name), dt, Nil)
        bindings += bndr.binderId -> Left(v)
        if (bndr.binderName.startsWith("$")) Graph.letinImpl(v, DummyRead, e0) // ignore type and type class lambdas
        else Graph.abs(v, e0)
      }
      def ELet(lets: Seq[(Binder, () => Expr)], e0: => Expr): Expr = {
        //println(s"LETS ${lets}")
        
        import Graph.reificationContext
        val old = reificationContext
        try
        lets.foldRight(() => e0){
          case ((bndr, rhs), body) =>
            val name = Graph.mkName(bndr.binderName, bndr.binderId.name)
            //println(s"LET ${name}")
            val v = Graph.bindVal(name+"$", dt, Nil)
            val rv = Graph.Rep.withVal(v,v)
            require(!reificationContext.contains(v))
            reificationContext += v -> rv
            bindings += bndr.binderId -> Left(v)
            () => {
              //println(s"LET' ${name} ${Graph.reificationContext}")
              val bod = rhs()
              Graph.setMeaningfulName(bod.bound, name)
              rv.rewireTo(bod)
              body()
            }
        }()
        finally reificationContext = old
      }
      def ECase(e0: Expr, bndr: Binder, alts: Seq[Alt]): Expr =
        // TODO handle special case where the only alt is AltDefault?
      {
        bindings += bndr.binderId -> Right(e0)
        val altValues = alts.map { case (c,r,f) =>
          val rs = f(e0)
          bindings ++= rs.map(r => r._1 -> Right(r._2))
          (c, rs.size, r)
        }
        Graph.mkCase(e0, altValues.map{case(con,arity,rhs) =>
          (con.asInstanceOf[AltDataCon].name, // FIXME
          arity,
          rhs)
        })
      }
      def EType(ty: Type): Expr = DummyRead
      
      def Alt(altCon: AltCon, altBinders: Seq[Binder], altRHS: => Expr): Alt = {
        (altCon, () => altRHS, r => altBinders.zipWithIndex.map(b => b._1.binderId -> Graph.methodApp(r, Graph.GetMtd, Nil,
          Graph.Args(
            altCon.asInstanceOf[AltDataCon] // FIXME
              .name |> Graph.staticModule,
            b._2 |> Graph.const,
          ) :: Nil, dt)).toMap)
      }
      
      def LitInteger(n: Int): Lit = Graph.Constant(n)
      def MachInt(n: Int): Lit = Graph.CrossStageValue(n, UnboxedMarker)
      def LitString(s: String): Lit = Graph.Constant(s)
      
    }
    val mod = ghcdump.Reader(dump, GraphDumpInterpreter)
    modName = Some(mod.moduleName)
    
    mod.moduleTopBindings.foreach { tb =>
      val v = Graph.bindVal(tb.bndr.binderName, dt, Nil)
      val rv = Graph.Rep.withVal(v,v)
      moduleBindings += tb.bndr.binderId -> (tb.bndr.binderName -> rv)
      GraphDumpInterpreter.bindings += tb.bndr.binderId -> Right(rv)
    }
    
    PgrmModule(mod.moduleName, mod.modulePhase,
      mod.moduleTopBindings.iterator
        .filter(_.bndr.binderName =/= "$trModule")
        .map(tb => tb.bndr.binderName -> {
          val (nme,rv) = moduleBindings(tb.bndr.binderId)
          rv.rewireTo(tb.expr)
          Graph.setMeaningfulName(tb.expr.bound, nme)
          rv
        }).toMap
    )
    
  }
  
  
}
