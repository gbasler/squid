package squid
package haskellopt

import squid.utils._
import squid.ir.graph3._
import ammonite.ops.FilePath

import scala.collection.mutable

class GraphOpt {
  object Graph extends Graph with RecGraphScheduling {
    val dummyTyp = Predef.implicitType[Any].rep
    override def staticModuleType(name: String): TypeRep = dummyTyp
  }
  
  case class Module(modName: String, lets: Map[String, Graph.Rep]) {
    def show = {
      "module " + modName + ":" + lets.map {
        case (name, body) => s"\n$name = ${body.showGraph}"
      }.mkString
    }
  }
  
  def loadFromDump(dump: FilePath): Module = {
    object GraphDumpInterpreter extends ghcdump.Interpreter {
      
      type Expr = Graph.Rep
      type Lit = Graph.Constant
      
      import Graph.{dummyTyp => dt}
      
      val bindings = mutable.Map.empty[BinderId, Graph.Val]
      
      def EVar(b: BinderId): Expr = bindings(b) |> Graph.readVal
      def EVarGlobal(ExternalName: ExternalName): Expr =
        //Graph.module(Graph.staticModule(ExternalName.externalModuleName), ExternalName.externalName, dummyTyp)
        Graph.staticModule(ExternalName.externalModuleName+"."+ExternalName.externalName)
      def ELit(Lit: Lit): Expr = Graph.rep(Lit)
      def EApp(e0: Expr, e1: Expr): Expr = Graph.app(e0, e1)(dt)
      def ETyLam(bndr: Binder, e0: Expr): Expr = e0 // Don't represent type lambdas...
      def ELam(bndr: Binder, e0: => Expr): Expr = {
        val v = Graph.bindVal(bndr.binderName+"_"+bndr.binderId.name, dt, Nil)
        bindings += bndr.binderId -> v
        Graph.abs(v, e0)
      }
      def ELet(lets: Array[(Binder, () => Expr)], e0: => Expr): Expr = {
        // FIXME first bind all names, as they can be mutually-recursive...
        lets.foldRight(() => e0){
          case ((bndr, rhs), body) =>
            val v = Graph.bindVal(bndr.binderName+"_"+bndr.binderId.name, dt, Nil)
            bindings += bndr.binderId -> v
            () => Graph.letin(bindings(bndr.binderId), rhs(), body(), dt)
        }()
      }
      def ECase(e0: Expr, bndr: Binder, alts: Array[Alt]): Expr = ???
      def EType(ty: Type): Expr = Graph.Constant(()) |> Graph.rep // FIXME
      
      def LitInteger(n: Int): Lit = Graph.Constant(n)
      def LitString(s: String): Lit = Graph.Constant(s)
      
    }
    val mod = ghcdump.Reader(dump, GraphDumpInterpreter)
    
    Module(mod.moduleName,
      mod.moduleTopBindings.iterator
        .filter(_.bndr.binderName =/= "$trModule")
        .map(tb => tb.bndr.binderName -> tb.expr).toMap
    )
    
  }
  
  
}
