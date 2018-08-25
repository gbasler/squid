package squid

import squid.ir.SimpleANF
import squid.ir.StandardEffects
import squid.lang.ScalaCore

/** This is just an example IR instance for demonstration purposes. */
object IR extends SimpleANF with StandardEffects with ScalaCore {
  override val showCompiledTrees = true
}
