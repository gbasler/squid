package squid.ir.graph

import squid.ir.{SchedulingANFBase, SimpleANF}
import squid.utils._

object SimpleASTBackend extends squid.ir.SimpleAST {
//object SimpleASTBackend extends SimpleANF with SchedulingANFBase {
  
  override def showScala(r: Rep): String = 
    super.showScala(r) |> trimPrefixes
  
  def trimPrefixes(str: String) = str
    .replaceAll("haskell.Prelude.","")
    .replaceAll("squid.lib.`package`.","")
    .replaceAll("squid.lib.","")
    .replaceAll("scala.collection.immutable.","")
    .replaceAll("scala.","")
    .replaceAll("Tuple2.apply","Tuple2")
  
}
object A extends App {
  import SimpleASTBackend.Predef._
  val a = code"""{
    val f_2 = ((x_0: scala.Int) => ((y_1: scala.Int) => x_0.+(y_1)));
    f_2(11)(f_2(33)(44))
  }"""
  a.run also println // FIXME!
  a.compile also println
}


object ANFBackend extends SimpleANF with SchedulingANFBase {
  
  override def showScala(r: Rep): String = 
    super.showScala(r) |> trimPrefixes
  
  def trimPrefixes(str: String) = str
    .replaceAll("haskell.Prelude.","")
    .replaceAll("squid.lib.`package`.","")
    .replaceAll("squid.lib.","")
    .replaceAll("scala.collection.immutable.","")
    .replaceAll("scala.","")
    .replaceAll("Tuple2.apply","Tuple2")
  
}
