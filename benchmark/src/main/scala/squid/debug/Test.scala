package squid.debug

import scala.tools.reflect.{ReflectGlobal, ToolBoxFactory}

/**
  * Created by lptk on 07/04/16.
  */
class Test(useQQ: Boolean, withClasses: Boolean = false, separateQQ: Boolean = false, useToolbox: Boolean = false) {
  import squid._
  import gen._
  
  import scala.reflect.runtime.universe._
  import scala.tools.reflect.ToolBox
  
  //showCode(SimpleReification(false, 1, 1, 1))
  
  //showCode(SimpleReification(false, 2, 2, 2))
  
  //showCode(SimpleReification(true, 2, 2, 2))
  val reif = SimpleReification(useQQ, 2, 2, 2, withClasses, separateQQ = separateQQ)
  //val reif = SimpleReification(useQQ, 10, 10, 2, withClasses, separateQQ = separateQQ)
  println(showCode(reif))
  
  val pgrm = showCode(reif)
  
  val cl = getClass.getClassLoader.asInstanceOf[java.net.URLClassLoader]
  val cp = cl.getURLs().map(_.getFile()).mkString(System.getProperty("path.separator"))
  
  if (useToolbox) {
    val tb = runtimeMirror(getClass.getClassLoader).mkToolBox(options = s"-cp $cp")
    
    val parsed = tb.parse(pgrm)
    //println(showCode(parsed))
    
    val res = tb.compile(parsed)
  } else {
    
    val dir = {
      import java.io.File
      val dir = new File("genbin")
      dir.mkdir()
      dir
    }
    
    import scala.tools.nsc._
    val nscReporter = new reporters.StoreReporter
    val nscSettings = new Settings()
    nscSettings.processArgumentString(s"-d $dir")
    
    val compiler = new ReflectGlobal(nscSettings, nscReporter, getClass.getClassLoader)
    val run = new compiler.Run
    
    run.compileSources(compiler.newSourceFile(pgrm) :: Nil)
    println(nscReporter.infos)
    nscReporter.reset
    
  }
  
  //println(res())
  
  //"Done."
  
}

/** Note: may not work in normal sbt run because of weird classpath issues; but will work in a forked run or in an IDE like IntelliJ */
object Test extends App {
  new Test(false)
  //new Test(true)
  //new Test(true, separateQQ = true)
  //new Test(true, withClasses = true)
  //new Test(false, withClasses = true)
  
  
  
  
}










