package squid
package bench

import scala.reflect.runtime.universe._
import org.scalameter.api._
import squid.gen.SimpleReification

/**
  * TODO a QQ-Separate approach with one different dsl"" per class
  */
//object CompileTime extends Bench.OfflineReport {
//object CompileTime extends Bench.Local[Double] {
//object CompileTime extends Bench.LocalTime {
object CompileTime extends Bench.OfflineRegressionReport {
  override def aggregator: Aggregator[Double] = Aggregator.median
  override val tester = Tester.Accepter()
  override val historian = Historian.Complete()
  
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
  //val nbClasses = Gen.range("Number of classes")(1, 1, 1)
  val nbClasses = Gen.range("Number of classes")(1, 9, 4)
  
  def testSetups(qq: Boolean, separateQQ: Boolean = false) = for { n <- nbClasses } yield {
    import scala.tools.reflect.ReflectGlobal
    
    //val pgrm = showCode(SimpleReification(qq, n, 5))
    //val pgrm = showCode(SimpleReification(qq, n, 2, separateQQ = separateQQ)) ////////////////////////////////////////////
    val pgrm = showCode(SimpleReification(qq, n, 3, separateQQ = separateQQ))
    
    val compiler = new ReflectGlobal(nscSettings, nscReporter, getClass.getClassLoader)
    //val run = new compiler.Run
    val src = compiler.newSourceFile(pgrm)
    
    compiler -> src
    //new compiler.Run -> src // Exception in thread "main" java.lang.Exception: Set(pos: source-<console>,line-1,offset=7 Main is already defined as object Main ERROR)
    //run -> pgrm
  }
  
  performance of "Compilation" in {
    
    measure method "Exp" in {
      using(testSetups(false)) in { case (compiler, src) =>
        val run = new compiler.Run
        
        //println("Compiling program: "+pgrm)
        
        run.compileSources(src :: Nil)
        if (nscReporter.infos.nonEmpty) {
          throw new Exception(nscReporter.infos.toString)
        }
        nscReporter.reset()
      }
    }
    measure method "QQ Together" in {
      using(testSetups(true, false)) in { case (compiler, src) =>
        val run = new compiler.Run
        
        //println("Compiling program: "+pgrm)
        
        run.compileSources(src :: Nil)
        if (nscReporter.infos.nonEmpty) {
          throw new Exception(nscReporter.infos.toString)
        }
        nscReporter.reset()
      }
    }
    measure method "QQ Separate" in {
      using(testSetups(true, true)) in { case (compiler, src) =>
        val run = new compiler.Run
        
        //println("Compiling program: "+pgrm)
        
        run.compileSources(src :: Nil)
        if (nscReporter.infos.nonEmpty) {
          throw new Exception(nscReporter.infos.toString)
        }
        nscReporter.reset()
      }
    }
    
  }
  
  
  // Putting runtime tests here so we get a common report...
  
  import org.scalameter.picklers.Implicits._
  import squid.generated._
  
  val nbConstructs = Gen.enumeration("Number of constructs")(50, 100, 200)
  
  def testSetups2 = for { n <- nbConstructs } yield { n match {
    case 50 => Appli50
    case 100 => Appli100
    case 200 => Appli200
  }}
  
  def times50(x: => Any) { var i = 0; while(i<500) {x;i+=1} }
  
  performance of "Reification" in {
    measure method "Exp" in {
      using(testSetups2) in { appli =>
        this times50 appli.reifyExp
      }
    }
    measure method "QQ Together" in {
      using(testSetups2) in { appli =>
        this times50 appli.reifyQQ
      }
    }
    measure method "QQ Separate" in {
      using(testSetups2) in { appli =>
        this times50 appli.reifyQQSep
      }
    }
  
  }
  
  
  
  
}



