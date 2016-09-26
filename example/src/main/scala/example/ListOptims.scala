package example

import scp._
import utils._
import ir2._
import utils.Debug.show
import scp.lang2.Optimizer

/**
  * Created by lptk on 15/09/16.
  */
trait ListOptims extends SimpleRuleBasedTransformer { self =>
  import base.Predef._
    
  
  rewrite {
    
    case ir"($ls: List[$ta]) .foldLeft[$tb] ( $init )( $f )" =>
      
      ir""" var cur = $init
            $ls foreach { x => cur = $f(cur, x) }
            cur """
      
      /*
    case ir"($ls: List[$t]) foreach ($f: t => Any)" =>
      
      //val res =
      ir""" var iter = $ls
            while (iter.nonEmpty)
            { $f(iter.head); iter = iter.tail } """
      
      //show(res rep)
      //res
      */
  }
  
  
}


object ListOptimTests extends App {
  object DSL extends SimpleAST
  import DSL.Predef._
  import DSL.Quasicodes._
  
  object Optim extends DSL.SelfTransformer with ListOptims with TopDownTransformer
  object BindNorm extends DSL.SelfTransformer with BindingNormalizer with TopDownTransformer
  
  var pgrm = ir{
    val f = (x: List[Int]) => x.foldLeft(0)(_ + _)
    f(List(1,2,3))
  }
  
  show(pgrm)
  
  //pgrm = pgrm transformWith Optim
  //show(pgrm)
  
  //pgrm = pgrm transformWith BindNorm
  //show(pgrm)
  
  //show(pgrm.run)
  
  
}


object ListOptimTestsOnline extends App {
  object DSL extends SimpleAST with OnlineOptimizer {
    
    object Optim extends DSL.SelfTransformer with ListOptims
    object BindNorm extends DSL.SelfTransformer with BindingNormalizer
    
    def pipeline = Optim.pipeline andThen BindNorm.pipeline
  }
  import DSL.Predef._
  import DSL.Quasicodes._
  
  val pgrm = ir{
    val f = (x: List[Int]) => x.foldLeft(0)(_ + _)
    f(List(1,2,3))
  }
  
  show(pgrm)
  
  show(pgrm.run)
  
}


object ListOptimTestsANF extends App {
  object DSL extends ANF
  import DSL.Predef._
  import DSL.Quasicodes._
  
  object Optim extends DSL.SelfTransformer with ListOptims with TopDownTransformer
  //object Optim extends DSL.SelfTransformer with ListOptims with BottomUpTransformer
  
  var pgrm = ir{
    val f = (x: List[Int]) => x.foldLeft(0)(_ + _)
    f(List(1,2,3))
  }
  //var pgrm = ir{
  //  val f = (x: List[Int]) => x foreach println
  //  f(List(1,2,3))
  //}
  
  //show(pgrm)
  show(pgrm rep)
  
  pgrm = pgrm transformWith Optim
  show(pgrm)
  //show(pgrm rep)
  
  //pgrm = pgrm transformWith Optim
  //show(pgrm)
  
  //show(pgrm.run)
  
  
}




import ListOptimTests._
class ListOptimizer extends Optimizer {
  val base: DSL.type = DSL
  def pipeline = Optim.pipeline andThen BindNorm.pipeline
}

