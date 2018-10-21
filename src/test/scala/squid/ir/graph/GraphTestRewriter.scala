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

package squid
package ir
package graph

import utils._
import squid.lib.matching._
import squid.lib.const

trait GraphTestRewriter extends MyFunSuiteTrait {
  val DSL: Graph
  import DSL.Predef._
  import DSL.GraphRepOps
  import DSL.IntermediateCodeOps
  
  /*
  
    Note:
      Rewriting as implemented is currently a little dangerous: it will apply rewrite patterns that match things also
      used elsewhere, possibly duplicating some work -- because the other use is still there so some what was matched is
      still present and used in the rewritten program
      In contrast, it seems GHC only rewrite variables used once... (is that true?)
     
  */
  
  def rw[A](c: ClosedCode[A], expectedSize: Int = Int.MaxValue)
           (expectedResult: Any = null, preprocess: A => Any = (a:A) => a, doEval: Bool = true, maxCount: Int = 32) = {
    var mod = true
    var count = 0
    var cur = c
    println("\n-> "+cur.rep.showGraphRev)
    val curs = cur.show
    println(curs)
    def printCheckEval(): Unit = if (doEval) {
      val value = //DSL.EvalDebug.debugFor
        cur.run
      if (expectedResult =/= null) assert(preprocess(value) == expectedResult, s"for ${curs}")
      println(s"== ${value}\n== ${Console.RED}${try DSL.scheduleAndRun(cur.rep) catch{case e:Throwable=>e}}${Console.RESET}")
    }
    printCheckEval()
    trait Mixin extends DSL.SelfTransformer {
      abstract override def transform(rep: DSL.Rep) = {
        //val res = super.transform(rep)
        if (!mod) { val res = super.transform(rep)
          //if (mod) println(s"${Console.BOLD}~> Transformed:${Console.RESET} ${cur.rep.showGraphRev}"+"\n~> "+cur.rep.show)
          if (mod) println(s"${rep.bound}${Console.BOLD} ~> ${Console.RESET}${res.showGraphRev}")
          res
        } else rep
      }
    }
    object Tr extends SimpleRuleBasedTransformer with DSL.SelfTransformer with Mixin // FIXME ugly crash if we forget 'with DSL.SelfTransformer' 
      //with BottomUpTransformer 
      with TopDownTransformer
    {
      rewrite {
        //case code"(${Const(n)}:Int)+(${Const(m)}:Int)" => println(s"!Constant folding $n + $m"); mod = true; Const(n+m)
        case r @ code"const[Int](${n0@Const(n)})+const[Int](${m0@Const(m)})" =>
          //println(s"!Constant folding ${r.rep}"); mod = true; Const(n+m)
          println(s"!Constant folding ${r.rep.bound}; i.e., ${n0.rep.simpleString}=$n + ${m0.rep.simpleString}=$m"); mod = true; Const(n+m)
        //case code"readInt.toDouble" => mod = true; code"readDouble" // stupid, just for testing...
        case r @ code"($n:Int).toDouble.toInt" => //mod = true; n
          mod = true
          //println(s"Rwr ${r.rep.bound} with ${n.rep.showGraphRev}")
          //println(s"Rwr  ${r.rep.showGraphRev}\nWith ${n.rep.showGraphRev}")
          println(s"Rwr with ${n.rep.bound} of ${r.rep.showGraphRev}")
          n
        case code"(($x: $xt) => $body:$bt)($arg)" =>
          //mod = true; body.subs(x) ~> arg
          mod = true
          println(s"!>> SUBSTITUTE ${x.rep} with ${arg.rep} in ${body.rep.showGraphRev}")
          val res = body.subs(x) ~> arg
          println(s"!<< SUBSTITUTE'd ${res.rep.showGraphRev}")
          res
        //case code"Match[Option[$t0],$tr]($opt)($cases*)" =>
        //  println(s"!!! MATCH ${opt.rep.showGraphRev}")
        //  ???
        //case code"Match[Option[$t0],$tr](Some($v:t0))($cases*)" =>
        //  ???
      }
      
      //override def transform(rep: DSL.Rep) = {
      //  val res = super.transform(rep)
      //  if (mod) println(s"${Console.BOLD}Current Rep:${Console.RESET} ${rep.showGraphRev}"+"\n"+rep.show)
      //  res
      //}
    }
    while (mod && count < maxCount) {
      count += 1
      mod = false
      cur = cur transformWith Tr
      if (mod) {
        //cur.rep.simplify_!
        //println(r.rep.iterator.toList.map(r=>s"\n\t\t ${r.bound} :: "+(r.dfn)).mkString)
        println(s"${Console.BOLD}~> Transformed:${Console.RESET} "+cur.rep.showGraphRev+"\n~> "+cur.show)
        printCheckEval()
      }
    }
    if (count >= maxCount) println(s"<<< !!! >>> Reached maximum count!  count == $maxCount")
    //assert(cur.rep.size <= expectedSize, s"for ${cur.rep.showGraphRev}")
    assert(cur.rep.simplifiedTopLevel.size <= expectedSize, s"for ${cur}")
    println(" --- END ---\n")
  }
    
}
