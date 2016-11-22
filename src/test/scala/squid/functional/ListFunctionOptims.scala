package squid
package functional

import utils._

object OptimTestDSL extends OptimTestDSL

class ListFunctionOptims extends MyFunSuite(OptimTestDSL) {
  import OptimTestDSL.Predef._
  
  test ("Basics") {
    
    eqt(ir"42".rep, base.const(42))
    eqt(ir" 'unoptimized ", ir" 'optimized ")
    
    // Note: the naive inliner in OptimTestDSL will mess up things like:
    //println(ir"var x = 0; x")
    
  }
  
  test ("Collapsing map & andThen chains") {
    
    val ls = ir"$$ls: List[Int]"
    val f = ir"$$f: (Int => Int)"
    
    eqt( ir"$ls map $f map $f map (_ + 1)" , ir"$ls map { x => $f( $f( x ) ) +1    }" )
    eqt( ir"$ls map $f map (_ + 1) map $f" , ir"$ls map { x => $f( $f( x )   +1 )  }" )
    eqt( ir"$ls map (_ + 1) map $f map $f" , ir"$ls map { x => $f( $f( x     +1 ) )}" )
    
  }
  
  test ("Removing map & andThen chains") {
    
    val one = ir"1"  // So Scala doesn't partially evaluate it in the second snippet
    eqt( ir"List(1,2,3) map (_ + 1) map (_ toDouble)" , ir"List(1+$one toDouble, 2+$one toDouble, 3+$one toDouble)" )
    
  }
  
  
  object Stopt extends StaticOptimizer[OptimTestDSL]
  import Stopt.optimize
  
  test ("Static optimization") {
    
    assert(optimize { 'unoptimized } == 'optimized)
    
    assert(optimize { List(1,2,3) map (_ + 1) map (_ toDouble) } == List(2.0, 3.0, 4.0) )
    
    def foo(ls: List[Int], f: Int => Int) = optimize { ls map f map (_ + 1) map f }
    assert(foo(1 :: 2 :: 3 :: Nil, (- _)) == List(0, 1, 2))
    
  }
  
  test ("Static optimization with a macro annotation") {
    
    @optimize(Stopt)
    def foo(n: Int) = (for(_ <- 0 until n) yield 'unoptimized.toString) mkString raw" \o/ "
    
    assert(foo(3) == raw"'optimized \o/ 'optimized \o/ 'optimized")
    
  }
  
}


