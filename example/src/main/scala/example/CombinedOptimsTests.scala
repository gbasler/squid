package example

import squid._
import utils._
import ir._
import squid.lang.Optimizer
import utils.Debug.show


/**
  * Created by lptk on 15/09/16.
  */
object CombinedOptimsTests extends App {
  object DSL extends SimpleAST
  import DSL.Predef._
  import DSL.Quasicodes._
  
  object Optim extends DSL.SelfTransformer with ListOptims with TupleVarOptim with TopDownTransformer
  object BindNorm extends DSL.SelfTransformer with BindingNormalizer with TopDownTransformer
  
  var pgrm = code{
    List(1,2,3).foldLeft((0,0))((acc,x) => (acc._2, acc._1+x))
  }
  
  show(pgrm)
  
  pgrm = pgrm transformWith Optim
  pgrm = pgrm transformWith BindNorm
  
  show(pgrm)
  
  //pgrm = pgrm transformWith Optim
  //show(pgrm)
  
}


// TODO make ANF pretty-printing remove useless ReadVar bindings, to get uncluttered output
object CombinedOptimsTestsSimpleANF extends App {
  object DSL extends SimpleANF with OnlineOptimizer with CurryEncoding.ApplicationNormalizer
  import DSL.Predef._
  import DSL.Quasicodes._
  
  object Optim extends DSL.SelfTransformer
    with ListOptims
    with TupleVarOptim
    //with BindingNormalizer
    with TopDownTransformer
    with FixPointTransformer // Q: why not FixPointRuleBasedTransformer?
  
  var pgrm = code{
    List(1,2,3).foldLeft((0,0))((acc,x) => (acc._2, acc._1+x))
    //println(1,2)
  }
  
  show(pgrm)
  //show(pgrm rep)
  
  pgrm = pgrm transformWith Optim
  //pgrm = base.ANFDebug debugFor (pgrm transformWith Optim)
  
  //show(pgrm rep)
  show(pgrm)
  
  //pgrm = pgrm transformWith Optim
  //
  //show(pgrm rep)
  //show(pgrm)
  
  
}


import CombinedOptimsTestsSimpleANF._
class FinalOptim extends /*DSL.SelfTransformer with*/ Optimizer {
  val base: DSL.type = DSL
  //def pipeline = Optim.pipeline andThen BindNorm.pipeline andThen Optim.pipeline
  def pipeline = ???
}

