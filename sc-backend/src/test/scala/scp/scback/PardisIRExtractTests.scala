package scp
package scback

import utils._

import scp.ir2.{FixPointRuleBasedTransformer, FixPointTransformer, SimpleRuleBasedTransformer, TopDownTransformer}

import collection.mutable.ArrayBuffer

class PardisIRExtractTests extends PardisTestSuite {
  
  import Sqd.Predef._
  import Sqd.Quasicodes._
  
  
  /*_*/
  
  
  
  test("Constants") {
    
    val q = ir{42}
    
    q match {
      case ir"${Const(x)}" =>
        x [Int]
    }
    
    q matches {
      case ir"42" =>
    } and {
      case ir"${Const(x)}:Double" => 
        x [Double]
        fail
      case ir"${Const(x)}:Int" =>
        x [Int]
        assert(x == 42)
    } and {
      case ir"$x:Double" => fail
      case ir"$x:Nothing" => fail
      case ir"$x:Int" =>
        assert(x == Const(42))
    }
    
  }
  
  
  
  test("Methods") {
    // TODO impl proper extraction
    
    //block(ir{ArrayBuffer(1,2,3)}) match {
    //  case ir"ArrayBuffer(1,2,3)" =>
    //}
    
  }
  
  
  
  test("Rewriting Consecutive Statements") {
    
    assert(SC.OptionApplyObject(null)(SC.typeInt).isPure) // we have the right SC version
    
    
    // Specializes Seq constructions to ArrayBuffer
    object Tr extends SimpleRuleBasedTransformer with TopDownTransformer with Sqd.SelfTransformer {
      rewrite {
        case ir"($arr: ArrayBuffer[Int]) append 42" => ir{ println("nope!") }
        case ir"($arr: ArrayBuffer[$t]) append $x; (arr:ArrayBuffer[t]).clear" => ir{ $(arr).clear }
      }}
    
    sameDefs(ir{ val arr = ArrayBuffer(1,2,3); arr append 42;    arr.size } transformWith Tr,
             ir{ val arr = ArrayBuffer(1,2,3); println("nope!"); arr.size })
    
    sameDefsAfter(
             ir{ val arr = ArrayBuffer(1,2,3); arr append 43;    arr.size }, _ transformWith Tr)
    
    // Tries to rm pure stmts referring to rm'd syms (trans clos):
    sameDefs(ir{ val arr = new ArrayBuffer[Int](); Option(arr append 1); arr.clear; arr.size } transformWith Tr,
             ir{ val arr = new ArrayBuffer[Int]();                       arr.clear; arr.size })
    
    sameDefsAfter( // should not apply:
             ir{val arr = new ArrayBuffer[Int](); val lol = arr append 1; arr.clear; println(lol); arr.size}, _ transformWith Tr)
    
    // More complicated, with nested blocks:
    sameDefs(ir{
      if (42.toDouble < 43) {
        println("hey")
        val arr = new ArrayBuffer[Int]()
        Option(arr append 1)
        arr.clear
      } else println("yo")
    } transformWith Tr, ir{
      if (42.toDouble < 43) {
        println("hey")
        val arr = new ArrayBuffer[Int]()
        arr.clear
      } else println("yo")
    })
    
  }
  
  
  
  test("Rewriting Bindings") {
    
    // Specializes Seq constructions to ArrayBuffer
    object Tr extends SimpleRuleBasedTransformer with TopDownTransformer with Sqd.SelfTransformer {
      rewrite { case ir"val x = Seq[$t]($xs*); $body: $bt" =>  // println(s"Running rwr code!! body = $body")
          ir"val x = ArrayBuffer($xs*); $body"
      }}
    
    val a0 = ir{ val s = Seq(1,2,3);         println(s(0)); s.size }
    val ar = ir{ val s = ArrayBuffer(1,2,3); println(s(0)); s.size }
    val a1 = a0 transformWith Tr
    
    sameDefs(a1, ar)
    
    // Verify that the bindings are correctly re-wired, and that the types are right:
    stmts_ret(a1) match {
      case (liftedSeqShit :: SC.Stm(s0,abap:SC.ArrayBufferApplyObject[_]) :: _ :: _ :: SC.Stm(s2,SC.ArrayBufferSize(s1)) :: Nil) -> (s3:Sqd.Sym) =>
        assert(s0 == s1)
        assert(s2 == s3)
        assert(s0.tp == irTypeOf[ArrayBuffer[Int]].rep)
        assert(abap.typeA == irTypeOf[Int].rep)
    }
    
    // Another way to do the same thing (but note that it uses a FixedPointTransformer!):
    sameDefs(a0 rewrite {
      case ir"val x = Seq[$t]($xs*); $body: $bt" => ir"val x = ArrayBuffer($xs*); $body"
    }, ar)
    
    // FIXME reinsert pure statements
    //sameDefs(ir{ Seq(1,2,3)        .map(_+1) } transformWith Tr, 
    //         ir{ ArrayBuffer(1,2,3).map(_+1)(Seq.canBuildFrom[Int]) },true)
    
    // FIXME args to map?!
    //println(ir{ val f: Int => Int = _+1; Seq(1,2,3).map(f) } transformWith Tr)
    
  }
  
  
  
  test("FixPoint Compounded Rewriting (sequences and bindings)") {
    
    object Tr extends FixPointRuleBasedTransformer with TopDownTransformer with Sqd.SelfTransformer {
      rewrite {
        case ir"($arr: ArrayBuffer[$t]) append $x; (arr:ArrayBuffer[t]).clear" =>  // println(s"Running rwr code!! body = $body")
          ir{ $(arr).clear }
        case ir"val arr = new ArrayBuffer[$t](); arr.clear; $body: $bt" =>
          ir{ val arr = new ArrayBuffer[t.Typ](); $(body) }
      }
    }
    
    // should not apply
    sameDefsAfter( ir{
      val arr = ArrayBuffer(1,2,3)
      arr append 1
      arr.size
    }, _ transformWith Tr )
    
    // should remove the option
    sameDefs( ir{
      val arr = new ArrayBuffer[Int]()
      Option(arr append 1)
      arr.clear
      arr.size
    } transformWith Tr, ir{
      val arr = new ArrayBuffer[Int]()
      arr.size
    })
    
    // should keep the option (used later by effectful expr)
    sameDefs( ir{
      val arr = new ArrayBuffer[Int]()
      val a1 = arr(1)
      arr append 1
      val opt = Option(a1)
      arr.clear
      println(opt)
    } transformWith Tr, ir{
      val arr = new ArrayBuffer[Int]()
      val a1 = arr(1)
      arr.clear
      val opt = Option(a1)
      println(opt)
    })
    
    // should keep the option (returned)
    sameDefs( ir{
      val arr = new ArrayBuffer[Int]()
      val a1 = arr(1)
      arr append 1
      val opt = Option(a1)
      arr.clear
      opt
    } transformWith Tr, ir{
      val arr = new ArrayBuffer[Int]()
      val a1 = arr(1)
      arr.clear
      val opt = Option(a1)
      opt
    })
    
    // should not apply (Option returned)
    sameDefsAfter( ir{
      val arr = new ArrayBuffer[Int]()
      val opt = Option(arr append 1)
      arr.clear
      opt
    }, _ transformWith Tr )
    
    // should not apply
    sameDefsAfter( ir{
      val arr = new ArrayBuffer[Int]()
      val lol = arr append 1
      arr.clear
      println(lol)
      arr.size
    }, _ transformWith Tr )
    
    // should not remove the `clear` cf returned FIXME
    println( ir{
      val arr = new ArrayBuffer[Int]()
      arr append 1
      arr append 2
      arr append 3
      arr.clear
    } transformWith Tr )
    
    // should apply both (but keep one append)
    sameDefs( ir{
      val arr = new ArrayBuffer[Int]()
      arr append 1
      arr append 2
      arr.clear
      arr append 3
    } transformWith Tr, ir{
      val arr = new ArrayBuffer[Int]()
      arr append 3
    })
    
    // should apply several times
    sameDefs( ir{
      val arr = new ArrayBuffer[Int]()
      arr append 1
      arr.clear
      arr append 2
      arr.clear
      arr append 3
      arr.clear
      arr.size
    } transformWith Tr, ir{
      new ArrayBuffer[Int]().size
    })
    
    // TODO test rwr inside nested block... eg ITE
    
  }
  
  
  
  test("Speculative Rewritings") {
    
    // TODO test
    
    
  }
  
  
  
  
  
  
  
}

