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

package squid
package feature

import squid.lib.persist
import utils._
import utils.typing.singleton.scope

/*

TODO: the scope macro
  scope.x refers to the variable bound by x, possibly cross-quote;
    can also be a type so we can have Code[T,scope.x], slightly shorter than Code[T,scope.x.Ctx] (maybe not worth it)
  scope - x removes x from the scope

Q: can we make this work?
  def f(v: Variable[Int])(c: scope.Code[Int]) = ... // scope should refer to v...
also, alternatively:
  def f(v: Variable[Int])(c: Code[Int,scope.v]) = ...
and even, also:
  def f(scp: Scope)(c: scp.Code[Int]) = ...
called as:
  f(scope)(...)
though that's not better than just a C type parameter

*/
class CrossQuotationTests extends MyFunSuite(CrossStageDSL) {
  import DSL.Predef._
  import DSL.Quasicodes._
  
  def foo[C](cde: Code[String,C]) = code{ ${cde} + "!" }
  
  test("Lambda-Bound") {
    
    val c0 = code{ x: Int => ${
      
      //val c: Code[Int, x.type] = code{x+1}
      // ^ Scalac as of 2.12 does not allow taking the .type of a non-AnyRef type
      val c0: Code[Int, scope.x] = code{x+1}
      val c = code{x+1}
      c eqt c0
      
      eqt(DSL.crossQuotation(x), code{x}.rep)
      //println(DSL.crossQuotation(x+1)) // Quasiquote Error: Cannot refer to local quoted variable 'x' from outside of quotation.
      
      identity(code{${c} * x})
      
    }}
    c0 eqt code"(x:Int) => (x+1)*x"
    val f0 = c0.compile
    assert(f0(42) == 43 * 42)
    
    assertCompiles      ("code{ x: Int => ${ println( ); code{   x  } } }")
    assertDoesNotCompile("code{ x: Int => ${ println(x); code{   x  } } }")
    assertDoesNotCompile("code{ x: Int => ${ println( ); code{ %(x) } } }")
    // ^ Error: Quasiquote Error: Cannot refer to local quoted 'x' from outside of quotation.
    
      //val c0: Code[Int, scope.x] = code"x+1"
    val c0q = code"""(x: Int) => $${
      val c0: Code[Int, scope.x] = code{x+1}
      val c = code{x+1} // Mixed QQ/QC
      identity(code"$$c * x")
    }"""
    c0q eqt c0
    
    val c0m = code{ x: Int => ${
      val c = code"x+1" // Mixed QC/QQ
      identity(code"$c * x")
    }}
    c0m eqt c0
    
    val f1 = code{ (x:Int, y:String) => ${ foo(code{y * x}) } }
    assert(f1.run.apply(3,"hey!") == "hey!hey!hey!!")
    
    val f1q = code"""(x:Int, y:String) => $${ foo(code"y * x") }"""
    f1q eqt f1
    
    val c1 = code{ x: String => ${
      val c: Code[Int,x.type] = code{x.length}
      code{${c} + 1}
    }}
    c1 eqt code"(x: String) => x.length + 1"
    
    val c1q = code"""(x: String) => $${
      val c: Code[Int,x.type] = code"x.length"
      code"$${c} + 1"
    }"""
    c1q eqt c1
    
  }
  
  test("Val-Bound") {
    
    val c0: ClosedCode[String] = code{ val x = 42; ${
      
      foo(code{'ok.name * x})
      
    }}
    c0 eqt code{ val a = 42; 'ok.name * a + "!" }
    
    val c0q = code"""val x = 42; $${
      
      foo(code"'ok.name * x")
      
    }"""
    c0q eqt c0
    
    val c1: ClosedCode[String] = code{
      val x = 42
      val y = 'ok.name
      ${ foo(code{ y * x }) }
    }
    c1 eqt code{ val a = 42; val b = 'ok.name; b * a + "!" }
    
    val c1q = code"""
      val x = 42
      val y = 'ok.name
      $${ foo(code" y * x ") }
    """
    c1q eqt c1
    
    // Erroneous use (reported after type checking by @compileTimeOnly):
    /*
    val oops2 = 'ko
    code{oops2.name.length} alsoApply println
    */
    // ^ Error: Cross-quotation reference was never captured. Perhaps you intended to use a cross-stage-persistent reference, which needs the @squid.lib.persist annotation.
    
  }
  
  test("Crossing Several Quotation Boundaries") {
    
    val c0: ClosedCode[String => String] = code{ val x = 42; ${
      identity(code{ y: String => ${
        foo(code{ y * x }) // 'x' here refers to the one defined two levels out
      }})
    }}
    c0 eqt code{ val a = 42; (b: String) => b * a + "!" }
    
    val c0q: ClosedCode[String => String] = code""" val x = 42; $${
      identity(code{(y: String) => $${
        foo(code" y * x ") // 'x' here refers to the one defined two levels out
      }})
    }"""
    c0q eqt c0
    
  }
  
}
