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
package feature

import squid.ir.BottomUpTransformer
import squid.ir.SimpleRuleBasedTransformer
import squid.utils._

class CrossQuotationVariableFunctions extends MyFunSuite {
  import DSL.Predef._
  import DSL.Quasicodes._
  
  def foo[C](cde: Code[Int,C]) = code"$cde + 1"
  
  test("Basics") {
    
    val model = code"val x = 0; (x+1) * 2"
    
    val res = code"val v = 0; ${(v:Variable[Int]) => foo(v.toCode)} * 2"
    res eqt model
    assert(res.run == 2) // res is closed!
    
    code"val v = 0; ${(v:Variable[Int]) => code"$v + 1"} * 2" eqt model
    
    // result of function is not existential because does not use the variable:
    code"val v = 0; ${(v:Variable[Int]) => foo(code"42")} * 2" eqt
      code"val x = 0; (${code"42"}+1) * 2"
    
    
    // result of function is the WRONG existential:
    import scala.language.existentials
    val w -> cde = { val v = Variable[Int]; v -> code"$v+1" } 
    // ^ cde has type: Code[Int,v.Ctx] where val v: Variable[Int]
    var cdev = cde : (Code[Int,v.Ctx] forSome {val v: Variable[Int]})
    // cdev contains the context requirement of the wrong variable, as existential
    cdev = code"val v = 0; ${(v:Variable[Int]) => cdev} * 2"
    // ^ makes sure the context is the same; the existential context requirement was not wrongly removed
    val closed = code"val $w = 0; $cdev"
    assertDoesNotCompile("closed.run") // Error:(53, 12) Cannot prove that AnyRef <:< v.Ctx.
    assert(closed.close.get.run == 2)
    
  }
  
  test("Erroneous uses") {
    
    val x = 123
    
    assertDoesNotCompile("""
      code"val v = 0; ${(x:Variable[Int]) => x.toCode} * 2"
    """) // Quasiquote Error: Inserted variable function refers to a variable 'x' that is not bound in the scope of that quote.
    
    assertDoesNotCompile("""
      code"val v = 0; ${(y:Variable[Int]) => y.toCode} * 2"
    """) // Embedding Error: Quoted expression does not type check: not found: value y
    
    val fun = (v:Variable[Int]) => foo(v.toCode)
    assertDoesNotCompile("""
      code"val v = 0; ${fun} * 2"
    """) // Error:(71, 25) Quasiquote Error: Inserted variable functions must be lambda expressions.
    
    val v = Variable[Int]
    val cde = code"$v+1" // contains context of wrong variable!
    val res = code"val v = 0; ${(v:Variable[Int]) => cde} * 2"
    assertDoesNotCompile("res.run") // Error:(77, 9) Cannot prove that AnyRef <:< v.Ctx.
    code"val $v = 1; $res" eqt code"val a = 1; val b = 0; (a + 1) * 2"
    
  }
  
  test("Arities 2 and 3") {
    
    // TODO
    
    // TODO test error on arity 4
    
  }
  
}