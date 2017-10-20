package squid

import lang._
import quasi._
import ir._

object MyBase extends SimpleAST with ClassEmbedder
object TestDSL extends SimpleAST with ClassEmbedder with SimpleReps
object TestDSLWithConstantTypes extends SimpleAST with ClassEmbedder with SimpleReps {
  override val widenConstantTypes: Boolean = false 
  // ^ this is mostly because of TypeMatchingWithConstantTypes.Trivial, which currently relies on type `Option[Int(42)]` being inferred
}
object NormDSL extends SimpleAST with ClassEmbedder with OnlineOptimizer with BindingNormalizer //with BlockNormalizer

object Test {
  object InnerTestDSL extends SimpleAST
}
