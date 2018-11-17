// Copyright 2018 EPFL DATA Lab (data.epfl.ch)
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
package graph2

import utils._
import squid.lib.matching._
import squid.lib
import squid.ir.graph.{SimpleASTBackend => AST}

import haskell.Builder
import haskell.Prelude._

object ListFusionTests extends Graph

class ListFusionTests extends MyFunSuite(ListFusionTests) with GraphRewritingTester[ListFusionTests.type] {
  import DSL.Predef._
  import DSL.Quasicodes._
  
  object Tr extends SimpleRuleBasedTransformer with DSL.SelfTransformer {
    
    rewrite {
      case c"666" => c"42"
        
      case code"(($x: $xt) => $body:$bt)($arg:$argt where (argt<:<xt))" =>
        println(s"!>> SUBSTITUTE ${x.rep} with ${arg.rep} in ${body.rep.showGraph}")
        val res = body.subs(x) ~> arg
        println(s"!<< SUBSTITUTE'd ${res.rep.showGraph}")
        //println(s"Nota: ${showEdges}")
        res
        
      case c"sum" => c"foldr(+)(0)"
      //case c"down($n)" => c"build(downBuild($n))"
      case c"down($n)" =>
        //c"magicBuild[Int,Any]((cons:(Int => Any => Any)) => (nil:Any) => downAux($n)(cons)(nil))"
        val cons = Variable[Int => Any => Any]
        val nil = Variable[Any]
        c"magicBuild[Int,Any]($cons => $nil => downAux($n)(${cons.toCode.asInstanceOf[ClosedCode[Nothing]]})(${
          nil.toCode.asInstanceOf[ClosedCode[Nothing]]}))"
      //case c"down($n)" => c"magicBuild[](downAux($n))"
        
      //case c"map[$ta,$tb]($f)($xs)" => c"build(mapBuild($f,$xs))"
      //case c"map[$ta,$tb]($f)($xs)" => c"magicBuild[$tb]((cons:($tb => Nothing => Any)) => (nil:Nothing) => foldr(cons compose $f)(nil)($xs))"
      case c"map[$ta,$tb]($f)($xs)" =>
        //c"magicBuild[$tb,Any]((cons:($tb => Any => Any)) => (nil:Any) => foldr(cons compose $f)(nil)($xs))"
        c"magicBuild[$tb,Any]((cons:($tb => Any => Any)) => (nil:Any) => foldr(compose(cons)($f))(nil)($xs))"
        
      // TODO add condition that the build node not be shared...
      case c"foldr[$ta,$tb]($k)($z)(build[ta]($g))" =>
        println(s">>> FUSION of $k $z $g")
        c"$g($k)($z)"
        
      //case c"foldr[$ta,$tb]($k)($z)(magicBuild[ta,ta]($g))" =>
      //case c"foldr[$ta,$tb]($k)($z)(magicBuild[ta]($g))" =>
      case c"foldr[$ta,$tb]($k)($z)(magicBuild[ta,$tc]($g))" =>
        //println(s">>> FUSION of $k $z $g")
        // ^ FIXME show shouldn't crash on "Cannot resolve _?" when the graph is incomplete
        println(s">>> FUSION of ${k.rep} ${z.rep} ${g.rep}")
        val g0 = g.asInstanceOf[Code[(ta.Typ => tb.Typ => tb.Typ) => tb.Typ => tb.Typ,g.Ctx]]
        c"$g0($k)($z)"
      
      case c"compose[$ta,$tb,$tc]($f)($g)" => c"(x:$ta) => $f($g(x))"
        
      // works but sometimes makes the output less classy
      //case c"+" => c"(lhs:Int)=>(rhs:Int)=>lhs+rhs"
        
    }
    
  }
  
  override def doTest[A](cde: ClosedCode[A], expectedSize: Int = Int.MaxValue)
      (expectedResult: Any = null, preprocess: A => Any = id[A], doEval: Bool = true) = {
    
    val tree0 = DSL.treeInSimpleASTBackend(cde.rep)
    println("Scala:\n"+tree0.show)
    println("Haskell:\n"+ToHaskell(AST)(tree0))
    
    val rep = super.doTest(cde,expectedSize)(expectedResult, preprocess, doEval)
    
    val tree = DSL.treeInSimpleASTBackend(rep)
    println("Haskell:\n"+ToHaskell(AST)(tree))
    printSep()
    
    rep
  }
  
  
  test("A") {
    
    doTest(c"666+${Const(1)}")()
    doTest(c"sum(Nil)")()
    doTest(c"down(666)")()
    
  }
  
  test("B") {
    
    doTest(c"sum(down(5))")(5 until 0 by -1 sum)
    
    //DSL.ScheduleDebug debugFor 
    //doTest(c"sum(map((_:Int) + 1)(down(5)))")(0 to 5 sum)
    doTest(c"sum(map((+)(1))(down(5)))")(5 until 0 by -1 map (_ + 1) sum)
    
  }
  
  test("C0") {
    
    //DSL.ScheduleDebug debugFor
    doTest(code{
      val bat = (sf: List[Int] => Int) => (arg: Int) => sf(map((c:Char) => ord(c)+arg)(loremipsum))
      val foo = (sf: List[Int] => Int) => (arg: Int) => (bat(sf)(arg))
      foo(compose(sum)(map(_*2)))(42)
    })( 7184 )
    // ^ fuses beautifully:
    //     foldr[Char, Any](((x_0: Char) => +(ord(x_0).+(42).*(2))))(0)(loremipsum)
    //     foldr $ (\x_0 -> ((((ord $ x_0)+42)*2)+)) $ 0 $ loremipsum
    
  }
  test("C1") {
    
    //DSL.ScheduleDebug debugFor
    doTest(code{
      val bat = (sf: List[Int] => Int) => (arg: Int) => sf(map((c:Char) => ord(c)+arg)(loremipsum))
      val foo = (sf: List[Int] => Int) => (arg: Int) => (bat(sf)(arg),bat(sf)(arg+1))
      foo(sum)(42)
    })( (3592,3618) )
    // ^ fuses:
    //      let sch$8518_0 = \x8512_1 x4_2 x8513_3 -> foldr $ (\x_4 -> x8512_1 $ ((ord $ x_4)+x4_2)) $ x8513_3 $ loremipsum
    //      in let sch$7068_5 = (+)
    //      in ((sch$8518_0 $ sch$7068_5 $ 42 $ 0),(sch$8518_0 $ sch$7068_5 $ (42+1) $ 0))
  }
  test("C2") {
    
    //DSL.ScheduleDebug debugFor
    doTest(code{
      val bat = (sf: List[Int] => Int) => (arg: Int) => sf(map((c:Char) => ord(c)+arg)(loremipsum))
      val foo = (sf: List[Int] => Int) => (arg: Int) => (bat(sf)(arg),bat(sf)(arg+1))
      foo(compose(sum)(map(_*2)))(42)
    })( (7184,7236) )
    // ^ doesn't fully fuse
    
  }
  
  
}
