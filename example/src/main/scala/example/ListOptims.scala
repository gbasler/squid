// Copyright 2017 EPFL DATA Lab (data.epfl.ch)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package example

import squid._
import utils._
import ir._
import squid.lang.Optimizer
import utils.Debug.show

trait ListOptims extends SimpleRuleBasedTransformer { self =>
  import base.Predef._
    
  
  rewrite {
    
    case code"($ls: List[$ta]) .foldLeft[$tb] ( $init )( $f )" =>
      
      code""" var cur = $init
            $ls foreach { x => cur = $f(cur, x) }
            cur """
      
      ///*
    case code"($ls: List[$t]) foreach ($f: t => Any)" =>
      
      //val res =
      code""" var iter = $ls
            while (iter.nonEmpty)
            { $f(iter.head); iter = iter.tail } """
      
      //show(res rep)
      //res
      //*/
  }
  
  
}


object ListOptimTests extends App {
  object DSL extends SimpleAST
  import DSL.Predef._
  import DSL.Quasicodes._
  
  object Optim extends DSL.SelfTransformer with ListOptims with TopDownTransformer
  object BindNorm extends DSL.SelfTransformer with BindingNormalizer with TopDownTransformer
  
  var pgrm = code{
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
  
  val pgrm = code{
    val f = (x: List[Int]) => x.foldLeft(0)(_ + _)
    f(List(1,2,3))
  }
  
  show(pgrm)
  
  show(pgrm.run)
  
}


object ListOptimTestsANF extends App {
  object DSL extends SimpleANF
  import DSL.Predef._
  import DSL.Quasicodes._
  
  object Optim extends DSL.SelfTransformer with ListOptims with TopDownTransformer
  //object Optim extends DSL.SelfTransformer with ListOptims with BottomUpTransformer
  
  var pgrm = code{
    val f = (x: List[Int]) => x.foldLeft(0)(_ + _)
    f(List(1,2,3))
  }
  //var pgrm = ir{
  //  val f = (x: List[Int]) => x foreach println
  //  f(List(1,2,3))
  //}
  
  //show(pgrm rep)
  show(pgrm)
  
  pgrm = pgrm transformWith Optim
  
  //show(pgrm rep)
  show(pgrm)
  
  //pgrm = pgrm transformWith Optim
  //show(pgrm)
  
  //show(pgrm.run)
  
}




import ListOptimTests._
class ListOptimizer extends Optimizer {
  val base: DSL.type = DSL
  def pipeline = Optim.pipeline andThen BindNorm.pipeline
}

