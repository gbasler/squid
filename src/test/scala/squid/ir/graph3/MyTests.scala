package squid
package ir.graph3

import utils._
object IR extends Graph
import IR.Predef._

object MyTests extends App {
  
  def f(c:OC[Any]) = println(c.rep.showGraph) 
  
  code"2.toDouble" also f
  code"val x = readInt; x + x.toDouble" also f
  
}
