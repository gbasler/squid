package scp
package ir2

object BaseInterpreterMacros {
  
  import scala.language.experimental.macros
  
  def genLambdaBody: Any = macro genLambdaBodyImpl
  
  import scala.reflect.macros.blackbox
  
  def genLambdaBodyImpl(c: blackbox.Context) = {
    import c.universe._
    val result =
    q"params match { case ..${
      for (i <- 0 to 22) yield {
        val (vars, params, assigns) = (for (j <- 0 until i) yield {
          val v = TermName(s"x$j")
          val p = TermName(s"p$j")
          (pq"$v", q"val $p: Any", q"$v.value = $p")
        }).unzip3
        cq"List(..${vars}) => (..$params) => {..$assigns; body}"
      }
    }}"
    //println(result)
    result: c.Tree
  }
  
}
