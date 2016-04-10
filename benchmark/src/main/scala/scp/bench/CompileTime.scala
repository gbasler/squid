package scp
package bench

import scala.reflect.runtime.universe._
import org.scalameter.api._
import scp.gen.SimpleReification

/**
  * TODO a QQ-Separate approach with one different dsl"" per class
  */
//object CompileTime extends Bench.OfflineReport {
//object CompileTime extends Bench.Local[Double] {
object CompileTime extends Bench.LocalTime {
  override def aggregator: Aggregator[Double] = Aggregator.median
  
  //def reporter: Reporter[Double] = Reporter.Composite(
  //    new RegressionReporter(tester, historian),
  //    HtmlReporter(!online)
  //  )
  
  val cl = getClass.getClassLoader.asInstanceOf[java.net.URLClassLoader]
  val cp = cl.getURLs().map(_.getFile()).mkString(System.getProperty("path.separator"))
  
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
  
  //val nbClasses = Gen.range("Number of classes")(1, 12, 3)
  //val nbClasses = Gen.range("Number of classes")(1, 2, 1)
  val nbClasses = Gen.range("Number of classes")(1, 1, 1)
  
  def testSetups(qq: Boolean) = for { n <- nbClasses } yield {
    import scala.tools.reflect.ReflectGlobal
    
    //val pgrm = showCode(SimpleReification(qq, n, 5))
    val pgrm = showCode(SimpleReification(qq, n, 2)) ////////////////////////////////////////////
    
    val compiler = new ReflectGlobal(nscSettings, nscReporter, getClass.getClassLoader)
    //val run = new compiler.Run
    compiler -> pgrm
    //run -> pgrm
  }
  
  performance of "Compilation" in {
    
    measure method "QQ" in {
      using(testSetups(true)) in { case (compiler, pgrm) =>
        val run = new compiler.Run
        
        //println("Compiling program: "+pgrm)
        
        run.compileSources(compiler.newSourceFile(pgrm) :: Nil)
        if (nscReporter.infos.nonEmpty) {
          throw new Exception(nscReporter.infos.toString)
        }
        nscReporter.reset()
      }
    }
    measure method "Exp" in {
      using(testSetups(false)) in { case (compiler, pgrm) =>
        val run = new compiler.Run
        
        //println("Compiling program: "+pgrm)
        
        run.compileSources(compiler.newSourceFile(pgrm) :: Nil)
        if (nscReporter.infos.nonEmpty) {
          throw new Exception(nscReporter.infos.toString)
        }
        nscReporter.reset()
      }
    }
    
  }
  
  
  
  
  
}



