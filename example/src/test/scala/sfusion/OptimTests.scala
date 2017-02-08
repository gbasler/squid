package sfusion

import org.scalatest.FunSuite
import squid.TestDSL
import squid.ir._

/**
  * Created by lptk on 07/02/17.
  */
class OptimTests extends FunSuite {
  
  object Code extends squid.ir.SimpleANF with ClassEmbedder with OnlineOptimizer {
    object Desug extends Desugaring //with TopDownTransformer
    object Norm extends SelfTransformer with CurryEncoding.ApplicationNormalizer
    def pipeline = Desug.pipeline andThen Norm.pipeline
    
    embed(Sequence)
    embed(impl.`package`)
    
  }
  import Code.Predef._
  import Code.Quasicodes._
  
  val Impl = new Code.Lowering('Impl) with TopDownTransformer
  val Imperative = new Code.Lowering('Imperative) with TopDownTransformer
  
  val CtorInline = new Code.SelfTransformer with SimpleRuleBasedTransformer with TopDownTransformer {
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
  
  test("Basics") {
    
    //val c0 = ir"Sequence(1,2,3,4)" // FIXME handle varargs in @embed...
    
    val c0 = ir"Sequence.fromIndexed(1 to 10).toString"
    //val c0 = ir"(s:Sequence[Any])=>s.show(10)" // FIXME typing of `take`
    
    println(c0)
    println("\n === Impl ===\n")
    val c1 = c0 transformWith Impl
    println(c1)
    println("\n === CtorInline ===\n")
    val c2 = c1 transformWith CtorInline
    println(c2)
    println("\n === Imperative ===\n") // FIXME "Term of type () => Int was rewritten to a term of type Boolean, not a subtype."
    val c3 = c2 transformWith Imperative
    println(c3)
    println("\n === End ===\n")
    
  }
  
  test("WAT") {
    
    // FIXME typing of `take`
    /*
    val c0 = impl.`package`.embedIn(Code).Object.Defs.take[String]
    println(c0)
    println(c0.typ)
    */
    
    //val c0 = Sequence.embedIn(Code).Class.Defs.show[Int]
    //import TestDSL.Predef._; val c0 = Sequence.embedIn(TestDSL).Class.Defs.show[Int]
    
  }
  
  
}
