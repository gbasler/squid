package squid
package ir.graph3

import utils._
object IR extends Graph
import IR.Predef._

object MyTests extends App {
  
  code"2.toDouble".rep.showGraph also println
  
}
