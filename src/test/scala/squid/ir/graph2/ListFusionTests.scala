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
import squid.ir.graph.{ANFBackend, SimpleASTBackend => AST}
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
        //println(s"!>> SUBSTITUTE ${x.rep} with ${arg.rep} in ${body.rep.showGraph}")
        val res = body.subs(x) ~> arg
        //println(s"!<< SUBSTITUTE'd ${res.rep.showGraph}")
        
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
  
  //def genHaskell(name:String,cde:ClosedCode[Any]) = {
  def genHaskell(name:String,tree:AST.Rep) = {
    val hs = ToHaskell(AST)(tree)
    
    // IR generates absconse bloat
    //val tree2 = AST.reinterpret(tree,ANFBackend)()
    //println("Tree2:\n"+tree2.show)
    //val hs = ToHaskell(ANFBackend)(tree2)
    
    println("Haskell:\n"+hs)
    import java.nio.file.{Paths, Files}
    import java.nio.charset.StandardCharsets
    val lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent pretium leo ac placerat vestibulum. Suspendisse iaculis bibendum imperdiet. Sed felis tellus, placerat eu pretium in, tempus sed est. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Etiam erat urna, fringilla eget nulla eu, facilisis venenatis libero. In hac habitasse platea dictumst. Suspendisse potenti. Donec et nisi et neque luctus dignissim eget sit amet velit. Nulla volutpat eu ex a consectetur. In diam arcu, porta ut dignissim sit amet, fermentum porta mi. Nam dictum dictum eros, mattis ornare turpis. Fusce arcu dolor, cursus eu risus sed, lacinia accumsan augue. Sed purus nulla, volutpat eget aliquam sit amet, gravida at quam. Phasellus malesuada ultrices velit at aliquet. Praesent pulvinar nunc magna, et venenatis turpis efficitur et. Nulla pharetra urna vehicula erat euismod, id aliquet mi eleifend. Donec rhoncus gravida nunc sit amet sagittis. Vestibulum velit orci, viverra sed pharetra ac, tincidunt vitae risus. Nulla quis feugiat arcu, non facilisis tellus. Nulla facilisi. Praesent gravida sem sed sapien scelerisque consequat. Duis tristique enim ac tempor vulputate. Interdum et malesuada fames ac ante ipsum primis in faucibus. In turpis dolor, fermentum in lectus in, sodales ullamcorper elit. Donec vel accumsan sem, eu ullamcorper erat. Maecenas placerat fringilla lorem, nec efficitur quam sodales et. Fusce eu augue eu neque lacinia viverra. Mauris eget fermentum purus. Vestibulum rhoncus id felis ac elementum. Nam maximus ornare faucibus. Nullam quis accumsan lacus. Nam congue tortor urna. Nullam risus ligula, ultricies in arcu ut, luctus rhoncus leo. Sed ullamcorper mauris vel feugiat semper. Nullam faucibus, augue eu luctus ullamcorper, dui felis condimentum diam, id lacinia justo justo vel turpis. Nunc non dolor magna. Fusce ut mauris at dolor dapibus convallis. Duis fringilla ac turpis at finibus. Phasellus mi lacus, gravida et odio vel, vestibulum vehicula purus. Cras sodales auctor lobortis. Ut sagittis nisl aliquam velit mollis hendrerit. Donec posuere, quam efficitur consectetur dignissim, est urna fringilla magna, vel tincidunt sem turpis vel nulla. Mauris nec nisi sit amet leo ultrices sodales ut cursus est. Sed auctor tortor non odio finibus, sed blandit erat eleifend. Interdum et malesuada fames ac ante ipsum primis in faucibus. Praesent in velit fermentum nisi eleifend euismod. In ac pellentesque dolor, at varius diam. Fusce vitae elementum lorem. Vivamus pharetra neque at varius dapibus. Cras ut sapien urna. Vivamus blandit pulvinar rhoncus. Nulla lacus velit, dapibus a pretium vitae, porta non urna. Cras lorem nulla, finibus a efficitur nec, elementum nec orci."
    val hsCnt =
      s"""
        |import Criterion.Main
        |import Data.Char 
        |
        |{-# NOINLINE loremipsum #-}
        |loremipsum = "${lorem * 10}"
        |
        |{-# NOINLINE myid #-}
        |myid a = a
        |
        |main = defaultMain [bench "max" $$ nf (${hs.splitSane('\n').map("\n  "+_).mkString}) 42]
        |""".stripMargin
    Files.write(Paths.get(s"example_gen/graph/$name.hs"), hsCnt.getBytes(StandardCharsets.UTF_8))
  }
  
  override def doTest[A](cde: ClosedCode[A], expectedSize: Int = Int.MaxValue)
      (expectedResult: Any = null, preprocess: A => Any = id[A], doEval: Bool = true) = {
    
    val tree0 = DSL.treeInSimpleASTBackend(cde.rep)
    println("Scala:\n"+tree0.show)
    //println("Haskell:\n"+ToHaskell(AST)(tree0))
    genHaskell("H0",tree0)
    
    val rep = super.doTest(cde,expectedSize)(expectedResult, preprocess, doEval)
    
    val tree = DSL.treeInSimpleASTBackend(rep)
    //println("Haskell:\n"+ToHaskell(AST)(tree))
    genHaskell("H1",tree)
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
  test("C2") { // FIXME prevent fusion of shared lists...
    // gets better with -fno-strictness
    
    //DSL.ScheduleDebug debugFor
    doTest(code{
      val bat = (sf: List[Int] => Int) => (arg: Int) => myid(sf(map((c:Char) => ord(c)+arg)(loremipsum)))
      val foo = (sf: List[Int] => Int) => (arg: Int) => (bat(sf)(arg),bat(sf)(arg+1))
      //foo(compose(sum)(map(_*2)))(42)
      foo(compose(sum)(map(_*2)))
    })( (7184,7236), _(42) )
    // ^ now fuses! though we have two foldr... -> it's the usual list fusion caveat with sharing!! it duplicates runtime work...
    //      let sch$23508_0 = \x15476_1 x23506_2 -> x15476_1 $ (x23506_2*2)
    //      in let sch$13507_3 = (+)
    //      in let sch$9_4 = \x6_5 x4_6 -> (ord $ x6_5)+x4_6
    //      in let sch$12_7 = loremipsum
    //      in ((foldr $ (\x_8 -> sch$23508_0 $ sch$13507_3 $ (sch$9_4 $ x_8 $ 42)) $ 0 $ sch$12_7),(foldr $ (\x_9 -> sch$23508_0 $ sch$13507_3 $ (sch$9_4 $ x_9 $ (42+1))) $ 0 $ sch$12_7))
    
  }
  test("C3") { // FIXedME needs a continuation! not yet implemented in scheduling
    
    //DSL.ScheduleDebug debugFor
    doTest(code{
      //val bat = (sf: List[Int] => Int) => (arg: Int) => sf(map((c:Char) => ord(c)+arg)(loremipsum))
      val bat = (sf: List[Int] => Int) => (arg: Int) => {
        val res = sf(map((c:Char) => ord(c)+arg)(loremipsum))
        (res * res + 1)
        //(((res * res + 1) * res + 1) * res + 1)
        //(((((res * res + 1) * res + 1) * res + 1) * res + 1) * res + 1) * res + 1
      }
      //val foo = (sf: List[Int] => Int) => (arg: Int) => (bat(sf)(arg),bat(sf)(arg-1),bat( compose(sf)(map(_*2)) )(arg+1))
      val foo = (sf: List[Int] => Int) => (arg: Int) => (bat(sf)(arg),bat( compose(sf)(map(_*2)) )(arg+1))
      //foo(sum)(42)
      foo(sum)
    //})( (3592,7236), _(42) )
    })( (12902465,52359697), _(42) )
    //})( (657784393,203944259,63000405), _(42) )  // crashes the IR: NON-EMPTY-LIST!! ListMap($27677=<arg> -> [_arg:Int])
    //})( (-836763575,440721301), _(42) )
    // ^ fully fuses!
    //      let sch'10099_0 = (\κ_1 x10094_2 -> ((foldr (\x_3 -> κ_1 x_3)) x10094_2) loremipsum) in
    //      let sch'8390_4 = (+) in
    //      let sch'9_5 = (\x6_6 x4_7 -> (ord x6_6)+x4_7) in
    //      ((sch'10099_0 (\x_3 -> sch'8390_4 (sch'9_5 x_3 42)) 0),(sch'10099_0 (\x_3 -> sch'8390_4 ((sch'9_5 x_3 (42+1))*2)) 0))
   
  }
  // TODO somehow make the IR not share basic applications? (on variables?)
  
  test("My Tests") { // currently generated by C3; but with the 'Any's replace by 'Int's
    
    //println{
    //  val sch$10099_3 = ((κ_0: Function1[Char, Function1[Int, Int]], x10094_1: Int) => foldr[Char, Int](((x_2: Char) => κ_0(x_2)))(x10094_1)(loremipsum));
    //  val sch$8390_4 = +;
    //  val sch$9_7 = ((x6_5: Char, x4_6: Int) => ord(x6_5).+(x4_6));
    //  Tuple2[Int, Int](sch$10099_3.apply(((x_8: Char) => sch$8390_4(sch$9_7.apply(x_8, 42))), 0), sch$10099_3.apply(((x_9: Char) => sch$8390_4(sch$9_7.apply(x_9, (42).+(1)).*(2))), 0))
    //}
    
    //doTest(c"val a = 666+${Const(1)}")()
    
    /*
Haskell:
(let sch'10099_0 = (\κ_1 x10094_2 -> ((((foldr) $ (\x_3 -> ((κ_1) $ (x_3)))) $ (x10094_2)) $ (loremipsum))) in
(let sch'8390_4 = (+) in
(let sch'9_5 = (\x6_6 x4_7 -> (((ord) $ (x6_6))+(x4_7))) in
(((sch'10099_0) (\x_3 -> ((sch'8390_4) $ ((sch'9_5) (x_3) (42)))) (0)),((sch'10099_0) (\x_3 -> ((sch'8390_4) $ (((sch'9_5) (x_3) ((42)+(1)))*(2)))) (0))))))

sch'10099_0
  :: Foldable t1 =>
     (t2 -> a -> b -> b) -> ([Char] -> t2) -> b -> t1 a -> b

     */
    //(((sch'10099_0) (\x_3 -> ((sch'8390_4) $ ((sch'9_5) (x_3) (42)))) (0)),((sch'10099_0) (\x_3 -> ((sch'8390_4) $ (((sch'9_5) (x_3) ((42)+(1)))*(2)))) (0)))
    
    //let sch'10099_0 = (\κ_1 x10094_2 -> foldr $ (\x_3 -> κ_1 $ x_3) $ x10094_2 $ loremipsum) in
    //let sch'10099_0 = (\κ_1 x10094_2 -> ((((foldr) $ (\x_3 -> ((κ_1) $ (x_3)))) $ (x10094_2)) $ (loremipsum))) in
    //let sch'10099_0 = (\κ_1 x10094_2 -> ((((foldr) $ (\x_3 -> ((κ_1) $ (x_3)))) $ (x10094_2)) $ (loremipsum))) in
    
    //(( foldr $ (\x_3 -> (κ_1 $ x_3))  $ x10094_2)
    //(((foldr $ (\x_3 -> (κ_1 $ x_3))) $ x10094_2)
    
    
  }
  
}
