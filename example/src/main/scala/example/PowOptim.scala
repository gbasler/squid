// Copyright 2019 EPFL DATA Lab (data.epfl.ch)
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
package powoptim

object Code extends squid.ir.SimpleAST
import Code.Predef._
import Code.Quasicodes._

object PowOptim1 extends App {
  
  def optPow[T,C](pgrm: Code[T,C]): Code[T,C] = pgrm fix_topDown_rewrite {
    case code"Math.pow($x, 0.0)" => code"1.0"
    case code"Math.pow($x, ${Const(d)})"
      if d.isWhole && 0 < d && d < 16
    =>
      code"$x * Math.pow($x, ${Const(d-1)})"
  }
  
  def mkPow[C](b: Code[Double, C], e: Code[Double, C]) = code"Math.pow($b, $e)"
  
  val p4code = code"${(x: Variable[Double]) => mkPow(code"$x", code"3.0")}"
  println(p4code)
  println(optPow(p4code))
  
  val p4slow: Double => Double = p4code.run
  
  def mkFastPow(n: Int): Double => Double = {
    
    // Alternatives that all work:
    val powCode = code"${ (x: Variable[Double]) => mkPow(code"$x", Const(n)) }"
    //val powCode = code"""(x: Double) => $${ mkPow(code"x", Const(n)) }"""
    //val powCode = code{ (x: Double) => ${ mkPow(code"x", Const(n)) } }
    
    val powCodeOpt = optPow(powCode)
    powCodeOpt.compile
  }
  val p4fast = mkFastPow(4)
  
  println(p4fast(42))
  
}

/*
  * 
  * Note: cannot use `var acc = code"1.0" : Code[Double, x.Ctx]` on the rhs of the rewriting,
  * because we would get {{{ _ <: Ctx }}}, which is not sufficient
  * (see the definition of `Ctx`, since it mirrors a contravariant type parameter).
  * 
  */
object PowOptim2 extends App {
  
  def opt[T,C](pgrm: Code[T,C]): Code[T,C] = pgrm rewrite {
    case code"Math.pow($x, ${Const(d)})"
    if d.isValidInt && (0 to 16 contains d.toInt)
    =>
      val xv = Variable[Double]
      var acc = code"1.0" withContextOf xv
      for (n <- 1 to d.toInt) acc = code"$acc * $xv"
      code"val $xv = $x; $acc"
  }
  
  import Math._
  val normCode = opt(code{ (x:Double,y:Double) => sqrt(pow(x,2) + pow(y,2)) })
  println(normCode)
  
  //val norm = normCode.compile // also possible; invokes the compiler at run time
  val norm = normCode.run
  
  println(norm(1,2))
  
}
