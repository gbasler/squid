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

/*
  * 
  * Note: cannot use `var acc = ir"1.0" : IR[Double, x.Ctx]` on the rhs of the rewriting,
  * because we would get {{{ _ <: Ctx }}}, which is not sufficient
  * (see the definition of `Ctx`, since it mirrors a contravariant type parameter).
  * 
  */
object PowOptim extends App {
  
  object Code extends squid.ir.SimpleAST
  import Code.Predef._
  import Code.Quasicodes._
  
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
  
  //val norm = normCode.compile  // TODO port from SCP
  val norm = normCode.run
  println(norm)
  
  println(norm(1,2))
  
}
