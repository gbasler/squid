package squid.ir.graph

import squid.utils._

object SimpleASTBackend extends squid.ir.SimpleAST {
  
  override def showScala(r: Rep): String = 
    super.showScala(r) |> trimPrefixes
  
  def trimPrefixes(str: String) = str
    .replaceAll("haskell.Prelude.","")
    .replaceAll("squid.lib.`package`.","")
    .replaceAll("squid.lib.","")
    .replaceAll("scala.","")
  
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