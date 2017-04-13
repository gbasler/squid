package squid

import lang._
import quasi._
import ir._

object MyBase extends SimpleAST with ClassEmbedder
object TestDSL extends SimpleAST with ClassEmbedder with SimpleReps
object NormDSL extends SimpleAST with ClassEmbedder with OnlineOptimizer with BindingNormalizer //with BlockNormalizer

object Test {
  object InnerTestDSL extends SimpleAST
}
