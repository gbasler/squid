package scp

import lang2._
import quasi2._
import ir2._

object MyBase extends SimpleAST with ClassEmbedder
object TestDSL2 extends SimpleAST with ClassEmbedder
object NormDSL extends SimpleAST with ClassEmbedder with OnlineOptimizer with BindingNormalizer //with BlockNormalizer

object Test {
  object InnerTestDSL extends SimpleAST
}
