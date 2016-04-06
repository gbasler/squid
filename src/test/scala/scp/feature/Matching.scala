package scp
package feature

import org.scalatest.FunSuite
import org.scalatest.ShouldMatchers

import utils.Debug._

class Matching extends FunSuite with ShouldMatchers {
  
  import TestDSL._
  
  test("Type Ascription") {
    
    dsl"42" match {
      case dsl"$x: Double" => ???
      case dsl"$x: Int" =>
        assert(x === dsl"42")
    }
    
    val n = dsl"42"
    val m = dsl"42:Int"
    assert(n =~= m)
    assert(dsl"$n * $m" =~= dsl"$m * $n")
    
    assert(dsl"(x: Int) => x + $n" =~= dsl"(x: Int) => x + $m")
    
  }
  
  test("DSL Methods") {
    
    dsl"42.toDouble" match {
      case dsl"($x: Int).toDouble" =>
        show(x)
    }
    
  }
  
  test("Free Variables") {
    
    //assert(dsl"$$x" =~= dsl"$$x")
    assert(dsl"$$x:Int" =~= dsl"$$x:Int")
    assert(dsl"($$x:Int)+1" =~= dsl"($$x:Int)+1")
    
  }
  
  class Expr[A]; //object Expr { def apply[A] = new Expr[A] }
  class App[A,B] extends Expr[B]; object App { def apply[A,B] = new App[A,B] }
  
  test("GADT") {
    
    (dsl"App[Int,Double]": Q[Expr[Double], {}]) match {
      //case dsl"$a: App[$s,$t]" => println(a,s,t) // FIXME type tag for t,s...
      case _ =>
    }
    
  }
  
  
}















