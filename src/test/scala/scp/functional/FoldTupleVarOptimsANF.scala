package scp
package functional

import scp.ir2.{ANF, OnlineOptimizer, BindingNormalizer, TopDownTransformer}
import utils._

object FoldTupleVarOptimsANF extends ANF with OnlineOptimizer with BindingNormalizer
//class FoldTupleVarOptimsANF extends MyFunSuite2(ANFDSL) {
class FoldTupleVarOptimsANF extends MyFunSuite2(FoldTupleVarOptimsANF) {
  import DSL.Predef._
  
  //object FoldTupleVarOptim extends FoldTupleVarOptimsANF
  //val FoldTupleVarOptim: FoldTupleVarOptimsANF.type = FoldTupleVarOptimsANF // TODO rename
  object FoldTupleVarOptim extends FoldTupleVarOptimsANF.SelfTransformer with FoldTupleVarOptim // TODO rename
  object BN extends FoldTupleVarOptimsANF.SelfTransformer with BindingNormalizer // TODO rename
  
  test("Foldleft to foreach to while") {
    
    val ls = ir"$$ls: List[Int]"
    val f = ir"$$f: ((Int, Int) => Int)"
    
    //println(ir"$ls.foldLeft(0)($f)")
    //FoldTupleVarOptim.TranformerDebug debugFor 
    // FIXedME var repl
    // TODO inv. why 'v_0' read not bound?
    //println(ir"$ls.foldLeft(0)($f)" transformWith FoldTupleVarOptim) // FIXedME does not make a while // TODO test
    //println(ir"$ls.foldLeft(0)($f)" transformWith FoldTupleVarOptim rep)
    //println(ir"$ls.foldLeft(0)($f)" transformWith FoldTupleVarOptim transformWith FoldTupleVarOptim) // FIXedME
    
    //val q = ir"$ls.foldLeft(0)($f)"
    ////base.ANFDebug debugFor
    ////println(q transformWith FoldTupleVarOptim rep) // FIXME
    //val q2 = q.erase match {
    //case ir"($ls: List[$ta]).foldLeft[$tb]($init)($f)" =>
    //  //Debug show (ls, init, f)
    //  ir""" var cur = $init
    //        $ls foreach { x => cur = $f(cur, x) }
    //        cur """
    //  
    //}
    //println(q2)
    
    
    
    /*
    val q = 
    base.ANFDebug debugFor 
    //println(ir"while(Nil nonEmpty){}" rep)
    //println(ir"while(Nil nonEmpty){}")
    //println(dbg_ir"while($ls nonEmpty){}")
    ir"var acc = 0; while(acc != 0){}"
    println(q rep)
    println(q)
    */
    
    //println(ir"{ val tmp = 0;$f(1,tmp) }; 42") // FIXME not inlined? <-- it has a hole!!!
    //println(ir"{ val tmp = 0;$f(1,tmp) }; 42" rep)
    
    //println(ir"while($ls nonEmpty){{ val tmp = 0;$f(1,2) }; 42}")
    //println(ir"var ite = $ls; while($ls nonEmpty){{ val tmp = ite.head;$f(1,2) }; ite = ite.tail}")
    //println(ir"var acc = 0; var ite = $ls; while(ite nonEmpty){{ val tmp = ite.head; acc = $f(acc,tmp) }; ite = ite.tail}")
    
    
    
    // TODO test...
    
    // FIXME dup of 'f' -- or fixed by proper global val num?
    //println(ir"val f: ((Int, Int) => Int) = (a,b) => a+b; var acc = 0; var ite = $ls; while(ite nonEmpty){{ val tmp = ite.head; acc = f(acc,tmp) }; ite = ite.tail}")
    
    //println(ir"(a:Int,b:Int) => a+b")
    //base.TranformerDebug debugFor 
    //println(ir"((a:Int,b:Int) => a+b)(1,2)")
    
    //println(ir"var acc = 0; var ite = $ls; while(ite nonEmpty){{ val tmp = ite.head; acc = (acc+tmp) }; ite = ite.tail}")
    //println(ir"var acc = 0; while(acc < 32){{ val tmp = acc+1; acc = (acc+tmp) }; println}")
    //println(ir"var acc = 0; while(acc < 32){acc += 1; println}")
    //println(ir"var acc = 0; while(acc.isWhole){acc = 0}")
    
    // Fixed
    //val q = ir"var a = 0; a += 1; a*2"
    //val q = base.ANFDebug debugFor ir"var a = 0; a"
    //println(q rep)
    //println(q)
    
    //base debugFor (
    //q.erase match {
    //  case ir"var $v: $tv = $init; $body: $tb" =>
    //    Debug show (v,init,body)
    //})
    //println(ir"var v: Int = 0; $$body: Int" rep)
    
    
    // TODO make it work: matching sthg of type unit with sthg of hole type (which gets inserted a () by QQ!!!)
    
    
    //println(ir"var a = 0; a += 1; a += 1")
    //println(ir"println;println")
    
    
    
    //ir"$ls.foldLeft(0)($f)" transformWith FoldTupleVarOptim eqt
    //ir"""
    //  var acc = 0;
    //  {
    //    var ite = $ls
    //    while (ite nonEmpty) {
    //      { val tmp = ite.head; acc = $f(acc,tmp) }
    //      ite = ite.tail
    //    }
    //  }
    //  acc
    //"""
    
  }
  
  test("Tuple Variable Inlining") {
    
    // FIXME
    
    /*
    val x = ir"println; var lol = 1; lol + 1"
    println(x)
    println(x rep)
    println
    
    //FoldTupleVarOptim.TranformerDebug debugFor
    println(x transformWith FoldTupleVarOptim)
    */
    
    /*
    val simple = ir"var two = (1,0); two._1 + two._2"
    
    //base debugFor
    FoldTupleVarOptim.TranformerDebug debugFor
    println(simple transformWith FoldTupleVarOptim)
    */
    
    
    
    //println(ir"var two = (1,0); while (two._1 + two._2 < 42) two = (two._1 + 1, two._2 + 2); two" rep)
    
    // FIXME loses update to first
    println(ir"var two = (1,0); while (two._1 + two._2 < 42) two = (two._1 + 1, two._2 + 2); two" transformWith FoldTupleVarOptim)
    
    
    
    /*
    eqtBy(ir"var two = (1,0); while (two._1 + two._2 < 42) two = (two._1 + 1, two._2 + 2); two" transformWith FoldTupleVarOptim,
    ir""" // FIXME this one doesn't get things let-bound
    
      var a = 1;
      var b = 0;
      while (a + b < 42) {
        a += 1
        b += 2
      }
      (a, b)
    """)(_ =~= _)
    */
    
  }
  
  test("FoldLeft on tuple to while on scalar vars Simple") {
    
  }
  
  test("FoldLeft on tuple to while on scalar vars") {
    
    // TODO make the combined optims work!
    // Problem is: `cur` is not assigned a tuple, but an applied Function2 which is equivalent to a tuple, and we don't inline it...
    // Even without inlining, we could solve the problem by just normalizing. Eg put it in ANF.
    
    //println(ir"List(1,2,3).foldLeft((0,0))((acc,x) => (acc._2, acc._1+x))" transformWith FoldTupleVarOptim)
    //println(ir"val r = List(1,2,3).foldLeft((0,0))((acc,x) => (acc._2, acc._1+x)); r._1 + r._2" transformWith FoldTupleVarOptim)
    
    var q = ir"List(1,2,3).foldLeft((0,0))((acc,x) => (acc._2, acc._1+x))"
    /*
    //println(q)
    println(q) // FIXedME
    q = q transformWith FoldTupleVarOptim
    println(q) // FIXedME
    
    //q = /*base.debugFor*/ (q transformWith BN) // FIXedME crashes  FIXME doesn't apply
    
    //println(q) // FIXedME
    
    //q = q transformWith FoldTupleVarOptim
    //
    //println(q) // FIXedME
    
    
    */
    
    
    ///*
    q = ir"""{
  val x_0: scala.collection.immutable.List[scala.Int] = scala.collection.immutable.List.apply[scala.Int](1, 2, 3);
  val x_1: scala.Tuple2[scala.Int, scala.Int] = scala.Tuple2.apply[scala.Int, scala.Int](0, 0);
  var v_2: scala.Tuple2[scala.Int, scala.Int] = x_1;
  val x_3: scala.collection.immutable.List[scala.Int] = scala.collection.immutable.List.apply[scala.Int](1, 2, 3);
  var v_4: scala.collection.immutable.List[scala.Int] = x_3;
  scp.lib.`package`.While(v_4.nonEmpty, {
    val x_5: scala.Int = v_4.head;
    val x_6: scala.Tuple2[scala.Int, scala.Int] = scp.lib.`package`.uncurried2[scala.Tuple2[scala.Int, scala.Int], scala.Int, scala.Tuple2[scala.Int, scala.Int]](((acc_7: scala.Tuple2[scala.Int, scala.Int]) => ((x_8: scala.Int) => {
  val x_9: scala.Int = acc_7._2;
  val x_10: scala.Int = acc_7._1;
  val x_11: scala.Int = x_10.+(x_8);
  scala.Tuple2.apply[scala.Int, scala.Int](x_9, x_11)
}))).apply(v_2, x_5);
    v_2=(x_6);
    val x_12: scala.collection.immutable.List[scala.Int] = v_4.tail;
    v_4=(x_12)
  });
  v_2
}""" // FIXME crashes
    import base.Quasicodes._
//    q = ir{
//  scp.lib.While(true, {
//    val x_6: scala.Tuple2[scala.Int, scala.Int] = scp.lib.uncurried2[scala.Tuple2[scala.Int, scala.Int], scala.Int, scala.Tuple2[scala.Int, scala.Int]](((acc_7: scala.Tuple2[scala.Int, scala.Int]) => ((x_8: scala.Int) => {
//  val x_9: scala.Int = acc_7._2;
//  val x_10: scala.Int = acc_7._1;
//  val x_11: scala.Int = x_10.+(x_8);
//  scala.Tuple2.apply[scala.Int, scala.Int](x_9, x_11)
//}))).apply((1,2),3);
//    //v_2=(x_6);
//    //val x_12: scala.collection.immutable.List[scala.Int] = v_4.tail;
//    //v_4=(x_12)
//  });
//  //v_2
//      (1,2)
//} // FIXME crashes
    
//    q = ir{
//  //scp.lib.While(true, {
//    val x_6: scala.Tuple2[scala.Int, scala.Int] = scp.lib.uncurried2[scala.Tuple2[scala.Int, scala.Int], scala.Int, scala.Tuple2[scala.Int, scala.Int]](((acc_7: scala.Tuple2[scala.Int, scala.Int]) => ((x_8: scala.Int) => {
//  scala.Tuple2.apply[scala.Int, scala.Int](4, 5)
//}))).apply((1,2),3);
//  //});
//      (1,2)
//} // FIXME crashes
    
    //q = ir{
    //  scp.lib.uncurried2[(Int, Int), Int, (Int, Int)]((acc_7: (Int, Int)) => (x_8: Int) => {
    //    (4, 5)
    //  }).apply((1,2),3);
    //}// FIXME crashes
    
    //q = ir{
    //  scp.lib.uncurried2((acc_7: Unit) => (x_8: Int) => {
    //    (4, 5)
    //  }).apply(Unit,3);
    //}
    
    q = q transformWith BN // FIXedME crashes
    
    println(q) // FIXME loses the variables!!
    
    q = q transformWith FoldTupleVarOptim
    
    println(q) // FIXedME
    
    //*/
    
    //println(q rep) // FIXME
    //println(q run) // FIXME returns (0,0) instead of (2,4) -- cf handling of variables...
    //println(q rep)
    //q = q transformWith (new NormDSL.SelfTransformer with BindingNormalizer with TopDownTransformer)
    //println(q)
    
  }
  
  
  object Stopt extends StaticOptimizer[FoldTupleVarOptim]
  import Stopt._
  
  test("Static optimization") {
    
    /*
    assert(optimize {
      List(1,2,3).foldLeft(0)(_ + _)
    } == 6)
    
    
    assert(optimize {
      //List(1,2,3).foldLeft(0)(acc_n => acc_n._1 + acc_n._2)
      //List(1,2,3).foldLeft((0,0))((acc,x) => (acc._2,acc._1+x))
      val r = List(1,2,3).foldLeft((0,0))((acc,x) => (acc._2,acc._1+x)); r._1 + r._2
    } == (2+4))
    */
    
  }
  
}
