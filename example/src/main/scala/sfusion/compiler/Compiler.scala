package sfusion
package compiler

import java.io.File
import java.io.PrintStream

import example.LogicNormalizer
import squid.utils._
import squid.ir._
import squid.lang._
import squid.anf.analysis
import squid.anf.transfo

/**
  * Created by lptk on 08/02/17.
  * 
  * TODO loop optims... can we match the pattern where the first iteration of the loop can be moved out?
  *   (a var starting true, set to false, with a comparison)
  *   as arises when concatenating a unary seq w/ another seq
  *   or just catch these earlier (convert `concat(single(.),.)` to `prepend(.,.)`) <- cleaner approach!
  * 
  */
class Compiler extends Optimizer {
  
  // TODO more settings
  /*
  val onOptimFailure: Option[(String,String) => Unit] = None
  val disableAssertions: Bool = false
  val outputDump: Option[String => File] = None
  val outputPrint: Option[PrintStream] = None
  */
  
  val base: Code.type = Code
  object Code extends squid.ir.SimpleANF with ClassEmbedder with OnlineOptimizer with analysis.BlockHelpers {
    object Desug extends Desugaring //with TopDownTransformer
    object Norm extends SelfTransformer with CurryEncoding.ApplicationNormalizer with transfo.OptionNormalizer //with FixPointRuleBasedTransformer
    def pipeline = Desug.pipeline andThen Norm.pipeline
    
    embed(Sequence)
    embed(impl.`package`)
    
  }
  import Code.Predef._
  import Code.Quasicodes._
  
  val Impl = new Code.Lowering('Impl) with TopDownTransformer
  val Imperative = new Code.Lowering('Imperative) with TopDownTransformer
  val DCE = new Code.SelfTransformer with squid.anf.transfo.DeadCodeElimination
  val LowLevelNorm = new Code.SelfTransformer with LogicNormalizer with transfo.VarInliner with FixPointRuleBasedTransformer with BottomUpTransformer
  
  val CtorInline = new Code.SelfTransformer with FixPointRuleBasedTransformer with TopDownTransformer {
    rewrite {
      case ir"val $s = new Sequence[$ta]($under,$size); $body: $bt" =>
        val underFV = ir"under? : (() => impl.Producer[$ta])"
        val sizeFV = ir"size? : SizeInfo"
        val body2 = body rewrite {
          case ir"$$s.under" => underFV
          case ir"$$s.size" => sizeFV
          case ir"$$s.show$$default$$1" => ir"10" // Annoying to have to write that!
        }
        val body3 = body2 subs 's -> Abort()
        ir"val under = $under; val size = $size; $body3"
    }
  }
  
  def dumpPhase(name: String, code: => String) = {
    println(s"\n === $name ===\n")
    println(code)
  }
  
  import Code.Rep
  
  val phases: List[String->(Rep=>Rep)] = List(
    "Impl" -> Impl.pipeline,
    "CtorInline" -> CtorInline.pipeline,
    //"DCE 0" -> DCE.pipeline,  // FIXME only remove effectless things
    "Imperative" -> Imperative.pipeline,
    "Low-Level Norm" -> LowLevelNorm.pipeline,
    "ReNorm (should be the same)" -> ((r:Rep) => base.reinterpret(r, base)())
  )
  
  protected val SAME = "[Same]"
  
  def pipeline = (r: Rep) => {
    
    dumpPhase("Init", base.showRep(r))
    
    phases.foldLeft(r) { case (r0, name -> f) =>
      val r1 = f(r0)
      dumpPhase(name, if (r1 =~= r0) SAME else base.showRep(r1))
      r1
    }
    
    
  }
  
  var curId = Option.empty[String]
  override def wrapOptim[A](id: String)(code: => A) = {
    curId = Some(id)
    try super.wrapOptim(id)(code) finally curId = None
  }
  
}


