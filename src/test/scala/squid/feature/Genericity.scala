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
package feature

import utils.Debug._

class Genericity extends MyFunSuite {
  import TestDSL.Predef._
  
  
  test("Generic Contexts and AnyRef") {
    
    assertCompiles(""" def constructTuple[C](x: Code[Int, C]): Code[Int, C] = code"$x + ${Const(123)}" """)
    assertCompiles(""" def constructTuple[C]: Code[Int, C] = code"${Const(123):Code[Int,AnyRef]}" """)
    
  }
  
  
  test("List") {
    
    val ls = code"List(1.0, 2.0)"
    //println(ls.rep.typ)
    
    
    //def foo[A: TypeEv](x: Q[A,{}]) = x match {
    def foo[A: TypeEv](x: Q[List[A],{}]) = x match {
      case code"List[A]($x,$y)" =>
        //println(typeEv[A])
        code"List($y,$x)"
    }
    
    //println(foo(ls))
    
    (ls: Q[_,{}]) match {
      case code"$ls: List[Int]" => ???
      case code"$ls: List[Double]" =>
    }
    
    
    (ls: Q[_,{}]) match {
      case code"$ls: List[$t]" => eqt(t, codeTypeOf[Double])
    }
    
    /*
    // Note: Scala weirdness:
    { trait t
      import scala.reflect.runtime.universe._
      implicit val _t_0: TypeTag[t] = ???
      typeTag[t]
      typeTag[List[t]]
    }
    */
    
    
    
  }
  
}



