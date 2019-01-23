package squid
package ir.graph3

import utils._
object IR extends Graph
import IR.Predef._

object MyTests extends App {
  //import scala.util.Random.nextInt
  def nextInt = 42
  
  def f(c:OC[Any]) = {
    println(c.rep.showGraph)
    IR.ScheduleDebug.debugFor { println(c) }
    println(c.rep.eval)
    println
  } 
  
  code"2.toDouble" also f
  code"val x = nextInt; x + x.toDouble" also f
  
}
