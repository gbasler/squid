package squid
package ir.graph3

import utils._
object IR extends Graph
import IR.Predef._

object MyTests extends App {
  //import scala.util.Random.nextInt
  def nextInt = 42
  
  def f(c:CC[Any]) = {
    println(c.rep.showGraph)
    //IR.ScheduleDebug debugFor
    println(c)
    println(c.rep.eval)
    println
  } 
  
  code"2.toDouble" also f
  code"val x = nextInt; x + x.toDouble" also f
  
  val v = Variable[Int]
  val oc = code"$v + 1"
  val c0 = code"val $v = nextInt; $v + 1" also f
  val c1 = code"val $v = 0.5.toInt; $v + 1" also f
  f(c0)
  val c2 = code"val $v = nextInt; $oc" also f // FIXME: capture of inserted variable does not happen
  
}
