package squid
package ir.graph3

import utils._
object IR extends Graph
import IR.Predef._

object MyTests2 extends App {
  import IR._
  val c0,c1 = new CallId(bindVal("α",typeRepOf[Any],Nil))
  
  // FIXME the Pop is lost after we postpone the branch, so the branch is misinterpreted
  
  val shd0 = Code[Int,Any](Box.rep(Pop(Id), Branch(Id, c0, code"readInt".rep, code"???".rep).mkRep))
  val shd1 = code"identity($shd0)"
  //val g = Branch(Id, c0, code"$shd1 + $shd1".rep, code"???".rep).mkRep
  val g = Box.rep(Push(c0,Id,Id), code"$shd1 + $shd1".rep)
  println(g.showGraph)
  ScheduleDebug debugFor
  println(g.show)
  
}
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
