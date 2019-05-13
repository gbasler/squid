package squid
package haskellopt

import squid.utils._
import squid.ir.graph3._
import ammonite.ops.FilePath

import scala.collection.mutable

class GraphOpt {
  object Graph extends Graph with RecGraphScheduling {
    val DummyTyp = Any
    override def staticModuleType(name: String): TypeRep = DummyTyp
  }
  import Graph.PgrmModule
  
  val imports = mutable.Set.empty[String]
  
  def loadFromDump(dump: FilePath): PgrmModule = {
    import Graph.{DummyTyp => dt, UnboxedMarker}
    
    var modName = Option.empty[String]
    val moduleBindings = mutable.Map.empty[String, Graph.Rep]
    
    object GraphDumpInterpreter extends ghcdump.Interpreter {
      
      type Expr = Graph.Rep
      type Lit = Graph.ConstantLike
      
      val bindings = mutable.Map.empty[BinderId, (Graph.Val, Graph.Rep)]
      val ignoredBindings = mutable.Set.empty[Graph.Val]
      
      val typeBinding = Graph.bindVal("ty", dt, Nil)
      ignoredBindings += typeBinding
      
      def EVar(b: BinderId): Expr = bindings(b)._2
      def EVarGlobal(ExternalName: ExternalName): Expr =
        if (ExternalName.externalModuleName === modName.get) moduleBindings(ExternalName.externalName) else
        //Graph.module(Graph.staticModule(ExternalName.externalModuleName), ExternalName.externalName, dummyTyp)
        Graph.staticModule {
          val mod = ExternalName.externalModuleName
          val nme = ExternalName.externalName
          (if (nme.head.isLetter) s"$mod.$nme" else s"($mod.$nme)") match {
            // weird special cases in GHC:
            case "(GHC.Types.:)" => "(:)"
            case "(GHC.Types.[])" => "[]"
            case n => imports += mod; n
          }
        }
      def ELit(Lit: Lit): Expr = Graph.rep(Lit)
      def EApp(e0: Expr, e1: Expr): Expr = e1.node match {
        case Graph.ConcreteNode(v: Graph.Val) if ignoredBindings(v) => e0
        case Graph.ConcreteNode(Graph.StaticModule(nme)) if nme.contains("$f") => // TODO more robust?
          // ^ such as '$fNumInteger' in '$27.apply(GHC.Num.$fNumInteger)'
          e0
        case _ =>
           Graph.app(e0, e1)(dt)
      }
      def ETyLam(bndr: Binder, e0: Expr): Expr = e0 // Don't represent type lambdas...
      def ELam(bndr: Binder, e0: => Expr): Expr = {
        val v = Graph.bindVal(bndr.binderName+"_"+bndr.binderId.name+"_"+bindings.size, dt, Nil)
        bindings += bndr.binderId -> (v -> Graph.Rep.withVal(v,v))
        if (bndr.binderName.startsWith("$")) { ignoredBindings += v; e0 } // ignore type and type class lambdas
        else Graph.abs(v, e0)
      }
      def ELet(lets: Array[(Binder, () => Expr)], e0: => Expr): Expr = {
        lets.foldRight(() => e0){
          case ((bndr, rhs), body) =>
            val v = Graph.bindVal(bndr.binderName+"_"+bndr.binderId.name+"_"+bindings.size, dt, Nil)
            val rv = Graph.Rep.withVal(v,v)
            bindings += bndr.binderId -> (v -> rv)
            () => {
              val bod = rhs()
              rv.hardRewireTo_!(bod)
              body()
            }
        }()
      }
      def ECase(e0: Expr, bndr: Binder, alts: Array[Alt]): Expr = ???
      def EType(ty: Type): Expr = typeBinding |> Graph.readVal
      
      def LitInteger(n: Int): Lit = Graph.Constant(n)
      def MachInt(n: Int): Lit = Graph.CrossStageValue(n, UnboxedMarker)
      def LitString(s: String): Lit = Graph.Constant(s)
      
    }
    val mod = ghcdump.Reader(dump, GraphDumpInterpreter)
    modName = Some(mod.moduleName)
    
    mod.moduleTopBindings.foreach { tb =>
      val v = Graph.bindVal(tb.bndr.binderName, dt, Nil)
      val rv = Graph.Rep.withVal(v,v)
      moduleBindings += tb.bndr.binderName -> rv
      GraphDumpInterpreter.bindings += tb.bndr.binderId -> (v -> rv)
    }
    
    PgrmModule(mod.moduleName,
      mod.moduleTopBindings.iterator
        .filter(_.bndr.binderName =/= "$trModule")
        .map(tb => tb.bndr.binderName -> {
          val (_,rv) = GraphDumpInterpreter.bindings(tb.bndr.binderId)
          rv.hardRewireTo_!(tb.expr)
          rv
        }).toMap
    )
    
  }
  
  
}
