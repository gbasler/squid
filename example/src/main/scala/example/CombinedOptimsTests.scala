package example

import scp._
import utils._
import ir2._
import scp.lang2.Optimizer
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
  
  var pgrm = ir{
    List(1,2,3).foldLeft((0,0))((acc,x) => (acc._2, acc._1+x))
  }
  
  show(pgrm)
  
  pgrm = pgrm transformWith Optim
  pgrm = pgrm transformWith BindNorm
  
  show(pgrm)
  
  //pgrm = pgrm transformWith Optim
  //show(pgrm)
  
}


object CombinedOptimsTestsSimpleANF extends App {
  object DSL extends SimpleANF with OnlineOptimizer with CurryEncoding.ApplicationNormalizer
  import DSL.Predef._
  import DSL.Quasicodes._
  
  object Optim extends DSL.SelfTransformer
    with ListOptims
    with TupleVarOptim
    //with BindingNormalizer
    with TopDownTransformer
    with FixPointTransformer
  
  var pgrm = ir{
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


object CombinedOptimsTestsANF extends App {
  object DSL extends ANF
  import DSL.Predef._
  import DSL.Quasicodes._
  
  //object Optim extends DSL.SelfTransformer with ListOptims /*with TupleVarOptim*/ with TopDownTransformer
  //object Optim extends DSL.SelfTransformer with FixPointRuleBasedTransformer with ListOptims with TupleVarOptim with TopDownTransformer
  //object BindNorm extends DSL.SelfTransformer with BindingNormalizer with TopDownTransformer // FIXedME dups while
  
  //object Optim extends DSL.SelfTransformer with FixPointRuleBasedTransformer with ListOptims with TupleVarOptim with TopDownTransformer with BindingNormalizer // FIXedME dups while
  
  //object Optim extends DSL.SelfTransformer
  //  with FixPointRuleBasedTransformer
  //  with ListOptims
  //  with TupleVarOptim
  //  with BindingNormalizer
  //  with TopDownTransformer
  //  //with BottomUpTransformer
  
  object Optim extends DSL.SelfTransformer
    with ListOptims
    with TupleVarOptim
    with BindingNormalizer
    with TopDownTransformer
    with FixPointTransformer
  
  
  // doesn't properly optimize:
  //object Optim extends DSL.SelfTransformer with SimpleRuleBasedTransformer with TopDownTransformer with FixPointRuleBasedTransformer with ListOptims with TupleVarOptim with BindingNormalizer // FIXedME dups while
  
  
  var pgrm = ir{
    
    List(1,2,3).foldLeft((0,0))((acc,x) => (acc._2, acc._1+x))
    
    //List(1,2,3).foldLeft(0)(_ + _)
    
    //var acc = 0
    //List(1,2,3).foreach { x => acc += x }
    //acc
    
    //val r = readInt
    //List(1,2,3).foreach { x => println(r) }
    
  }
  //var pgrm = ir{
  //  var cur_0: scala.Tuple2[scala.Int, scala.Int] = scala.Tuple2.apply[scala.Int, scala.Int](0, 0);
  //  {
  //    var iter_1: scala.collection.immutable.List[scala.Int] = scala.collection.immutable.List.apply[scala.Int](1, 2, 3);
  //    while (iter_1.nonEmpty) 
  //      {
  //        {
  //          val x_2 = iter_1.head;
  //          cur_0 = {
  //            val a_3 = cur_0;
  //            val x_4 = x_2;
  //            scala.Tuple2.apply[scala.Int, scala.Int](a_3._2, a_3._1.+(x_4))
  //          }
  //        };
  //        iter_1 = iter_1.tail
  //      }
  //    
  //  };
  //  cur_0
  //}
  
  show(pgrm)
  
  pgrm = pgrm transformWith Optim
  //pgrm = base.ANFDebug debugFor (pgrm transformWith Optim)
  
  //show(pgrm rep)
  show(pgrm)
  
  //pgrm = pgrm transformWith BindNorm transformWith Optim
  //pgrm = pgrm transformWith BindNorm
  //show(pgrm)
  
  //pgrm = pgrm transformWith Optim
  //show(pgrm)
  
}


import CombinedOptimsTestsANF._
class FinalOptim extends /*DSL.SelfTransformer with*/ Optimizer {
  val base: DSL.type = DSL
  //def pipeline = Optim.pipeline andThen BindNorm.pipeline andThen Optim.pipeline
  def pipeline = ???
}

