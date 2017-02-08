package sfusion
package compiler

import java.io.File

import squid.utils._
import squid.ir._
import squid.lang._

/**
  * Created by lptk on 08/02/17.
  */
class TestCompiler extends Compiler {
  
  override def dumpPhase(name: String, code: => String) = {
    
    val p = curPrinter.get
    p.println(s"\n// === $name ===\n")
    val str = code
    if (str == SAME) p.println("// Same as above.")
    else p.println(code)
    
  }
  
  var curPrinter = Option.empty[java.io.PrintWriter]
  override def wrapOptim[A](id: String)(code: => A) = {
    //println(id)
    import File.{separator => SEP}
    val file = new File(s"example_gen${SEP}test${SEP}sfusion${SEP}$id.scala")
    val p = new java.io.PrintWriter(file)
    curPrinter = Some(p)
    p.println("// Automatically-generated code")
    try super.wrapOptim(id)(code) finally {
      p.close()
      curPrinter = None
    }
  }
  
  
}

