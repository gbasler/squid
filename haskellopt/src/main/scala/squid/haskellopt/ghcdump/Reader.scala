package squid
package haskellopt
package ghcdump

import squid.utils._
import ammonite.ops._
import ammonite.ops.ImplicitWd._
import java.nio.file.{Files, Paths}
import io.bullet.borer.Cbor
import io.bullet.borer.Dom._

object Reader {
  
  def apply(dump: FilePath, in: Interpreter): in.Module = {
    val byteArray = Files.readAllBytes(dump.toNIO)
    val decoded = Cbor.decode(byteArray).to[Element].value
    in(decoded)
  }
  
}
