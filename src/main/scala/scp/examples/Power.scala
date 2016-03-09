package scp
package examples


import lang._
import quasi._
import ir._

import utils.Debug._


/**
  * Created by lptk on 04/03/16.
  * 
  * Stages the power of any open Int expression; for example dsl"$$x: Int" or dsl"42" 
  * 
  */
//object Power extends Quasi[MyDSL] with App {
object Power extends App {
  
  import DeepDSL._
  
  
  //dsl""
  
  def power[S](n: Int, q: Q[Int,S]): Q[Int,S] = {
    assert(n >= 0)
    if (n == 0) dsl"1"
    else dsl"$q * ${power(n-1, q)}"
  }
  
  val n3 = power(3, dsl"($$n: Int)")
  show(n3)
  val p3 = dsl"(n: Int) => $n3"
  show(p3)
  
  
  def optim[A,S](code: Q[A,S]) = ??? // TODO
  
  
  math.pow(2,3)
  3.5.isWhole
  
  
  
}

//object Power_Closed extends Quasi[MyDSL] with App {
object Power_Closed extends App {
  
  import DeepDSL._
  
  def lambda[A,B](qf: Q[A,{}] => Q[B,{}]): Q[A => B,{}] = Quoted(abs((x: Rep) => qf(Quoted(x)).rep))
  
  def power[S](n: Int)(q: Q[Int,S]): Q[Int,S] = {
    assert(n >= 0)
    if (n == 0) dsl"1"
    else dsl"$q * ${power(n-1)(q)}"
  }
  
  //val n3 = power(3) _
  val n3 = lambda(power(3))
  show(n3)
  //val p3 = dsl"(n: Int) => ${lambda(n3)}(n)"
  val p3 = dsl"(n: Int) => $n3(n)"
  show(p3)
  val p32 = dsl"(n: Int) => ${power(3)(dsl"($$n:Int)")}"
  show(p32)
  
  
  def optim[A,S](code: Q[A,S]) = ??? // TODO
  
  
  math.pow(2,3)
  3.5.isWhole
  
  
  
}








