package sfusion

import org.scalatest.FunSuite
//import squid.ir.ClassEmbedder
//import squid.ir.TopDownTransformer
import squid.ir._

/**
  * Created by lptk on 07/02/17.
  */
class OptimTests extends FunSuite {
  
  object Code extends squid.ir.SimpleANF with ClassEmbedder with OnlineDesugaring {
    embed(Sequence)
    embed(impl.`package`)
  }
  import Code.Predef._
  
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
    println(c0)
    println(" === Impl ===")
    val c1 = c0 transformWith Impl
    println(c1)
    println(" === CtorInline ===")
    val c2 = c1 transformWith CtorInline
    println(c2)
    println(" === Imperative ===") // FIXME "Term of type () => Int was rewritten to a term of type Boolean, not a subtype."
    val c3 = c2 transformWith Imperative
    println(c3)
    println(" === End ===")
    
  }
  
  
}
