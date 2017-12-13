package stagerwr

import squid.utils._
import squid.ir._

object Optim extends Embedding.SelfTransformer with FixPointRuleBasedTransformer with TopDownTransformer

/* Created by lptk on 18/06/17. */
class Optimizer extends squid.lang.Optimizer {
  val base: Embedding.type = Embedding
  def pipeline = Optim.pipeline //andThen BindNorm.pipeline
}

