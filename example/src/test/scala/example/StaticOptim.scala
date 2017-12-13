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
import squid.ir.{BindingNormalizer, TopDownTransformer}
import utils._

/**
  * Created by lptk on 15/09/16.
  */
object StaticOptim extends App {
  
  object Stopt extends StaticOptimizer[ListOptimizer]
  import Stopt._
  
  
  def vanilla = {
    val f = (x: List[Long]) => x.foldLeft(0L)(_ + _)
    val ls = List.range(0, 66666L)
    for (i <- 0 until 1000) f(ls)
    f(ls)
  }
  
  def opt = optimize {
    val f = (x: List[Long]) => x.foldLeft(0L)(_ + _)
    val ls = List.range(0, 66666L)
    for (i <- 0 until 1000) f(ls)
    f(ls)
  }
  
  def time[A](name: String)(f: => A) = {
    val t0 = System.nanoTime()
    val result = f
    val t1 = System.nanoTime()
    println(s"$name took: " + (t1 - t0) + "ns")
    result
  }
  
  def test = {
    assert( time("Vanilla  ")(vanilla) == 
            time("Optimized")(opt) )
  }
  
  for (i <- 0 until 5) test
  println
  
  time("Optimized")(opt) |> println
  time("Vanilla  ")(vanilla) |> println
  //time("Optimized")(opt) |> println
  
}
