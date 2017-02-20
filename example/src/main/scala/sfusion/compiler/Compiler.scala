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
  * TODO high-level optims such as map.map->map and map.flatten->flatMap; applying on the level of Sequence defs
  *   to work well this requires an effect system that accounts for latent effects,
  *     such that `s.map(a => a+readInt)` is pure but `s.map(a => a+readInt).fold(...)` is impure
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
  
  val ImplOptim = new Code.SelfTransformer with FixPointRuleBasedTransformer with TopDownTransformer {
    import impl._
    
    // Q: works with hygienic context polym?!
    // TODO generalize for any associative binop;
    // Note: this would be best achieved by simply re-associating to the right all associative binops
    /** Extracts the right-hand side of some string addition starting with free variable `acc?:String`.
      * This is useful because currently pattern `acc + $x` cannot match something like `(acc + a) + b`. */
    object StringAccAdd {
      val Acc = ir"acc?:String"
      // ^ Note: inline `${ir"acc?:String"}` in the pattern is mistaken for an xtion hole (see github.com/LPTK/Squid/issues/11)
      def unapply[C](x:IR[Any,C]): Option[IR[Any,C]] = x match {
        case ir"($Acc:String) + $rest" => Some(rest)
        case ir"($lhs:String) + $rhs" => unapply(lhs) map (x => ir"$x.toString + $rhs") orElse (unapply(rhs) map (x => ir"$lhs + $x"))
        case _ => None
      }
    }
    
    rewrite {
      case ir"fold[String,String]($s)($z)((acc,s) => ${StringAccAdd(body)})" => // TODO generalize... (statements?!)
        val strAcc = ir"strAcc? : StringBuilder"
        val body2 = body subs 'acc -> ir"$strAcc.result"
        val zadd = if (z =~= ir{""}) ir"()" else ir"$strAcc ++= $z" // FIXME does not compile when inserted in-line... why?
        ir"val strAcc = new StringBuilder; $zadd; foreach($s){ s => strAcc ++= $body2.toString }; strAcc.result"
    }
  }
  
  
  def dumpPhase(name: String, code: => String, time: Long) = {
    println(s"\n === $name ===\n")
    println(code)
  }
  
  import Code.Rep
  
  val phases: List[String->(Rep=>Rep)] = List(
    "Impl" -> Impl.pipeline,
    "CtorInline" -> CtorInline.pipeline,
    //"DCE 0" -> DCE.pipeline,  // FIXME only remove effectless/just-read things
    "ImplOptim" -> ImplOptim.pipeline,
    "Imperative" -> Imperative.pipeline,
    "Low-Level Norm" -> LowLevelNorm.pipeline,
    "ReNorm (should be the same)" -> ((r:Rep) => base.reinterpret(r, base)())
  )
  
  protected val SAME = "[Same]"
  
  def pipeline = (r: Rep) => {
    
    dumpPhase("Init", base.showRep(r), 0)
    
    phases.foldLeft(r) { case (r0, name -> f) =>
      val t0 = System.nanoTime()
      val r1 = f(r0)
      val t1 = System.nanoTime()
      dumpPhase(name, if (r1 =~= r0) SAME else base.showRep(r1), t1-t0)
      r1
    }
    
    
  }
  
  var curId = Option.empty[String]
  override def wrapOptim[A](id: String)(code: => A) = {
    curId = Some(id)
    try super.wrapOptim(id)(code) finally curId = None
  }
  
}


