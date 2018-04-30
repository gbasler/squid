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

import squid.ir.RewriteAbort
import utils._

class TrickyTypes extends MyFunSuite {
  import TestDSL.Predef._
  
  test("Local Type Synonym") {
    
    code"Option.empty[String]" eqt {
      type String = java.lang.String
      code"Option.empty[String]"
    }
    
  }
  
  test("Local Type With Evidence") {
    
    class Lol
    
    assertDoesNotCompile("""
      code"Option.empty[Lol]"
    """)
    
    code"Option.empty[Int]" eqt {
      implicit val LolImpl = codeTypeOf[Int].asInstanceOf[CodeType[Lol]]
      code"Option.empty[Lol]"
    }
    
  }
  
  
  test("Explicit Empty Contexts and Inferred Contexts") {
    
    List[Code[Int,{}]](code"1", code"2", code"3").foldLeft(code"0") { (acc, exp) =>
      code"if ($acc != 0) $acc else $exp"
    }
    
  }
  
  
  test("Lambda with Expected Type") {
    import base.Quasicodes._
    
    val c1: Code[Int => Int => Bool => Bool,_] = code{ (s:Int) =>
      val n = 42
      k => { b => b }
    }
    
    eqt(c1.Typ, codeTypeOf[Int => Int => Bool => Bool])
    
  }
  
  
  def foo[T](x:Code[T,{}]) = code"$x==0"
  
  test("Any method called on type parameter") {
    foo(code"42") eqt code"${code"42:Any"} == 0"
    foo(code"42") neqt code"${code"42"} == 0" // Note: Int.== and Any.== have two different method symbols!!
  }
  
  test("Type evidence on non-singleton type") {
    
    eqt(foo(code"123").Typ : CodeType[_ <: Bool], codeTypeOf[Bool])
    // ^ note that this does not yield a `CodeType[Bool]` because of declaration-site variance of Code[_,C] types
    
  }
  
  
  import TrickyTypes._
  
  test("Abstract Types") {
    
    // References to Abstract used to incur a type tag (uninterpretedType fallback), now they should not
    same(typeRepOf[Abstract],
      base.staticTypeApp(
        base.loadTypSymbol("squid.feature.TrickyTypes$#Abstract"), Nil))
        // ^ Note: using "squid.feature.TrickyTypes.Abstract" raises: class squid.feature.TrickyTypes.Abstract not found
    
    same(code"Option.empty[Abstract]".run, None)
    
  }
  
  test("Abstract Type with Type Parameter") {
    
    def foo[A:CodeType] = {
      same(typeRepOf[AbsT[A]],
        base.staticTypeApp(base.loadTypSymbol("squid.feature.TrickyTypes$#AbsT"), typeRepOf[A]::Nil))
      code"Option.empty[AbsT[A]]"
    }
    foo[Int] eqt code"Option.empty[AbsT[Int]]"
    
    eqt(codeTypeOf[Id[Int]], codeTypeOf[Int])
    
  }
  
  test("Higher-Kinded Abstract Types") {
    
    same(typeRepOf[HK[Id]],  // note: doesn't dealias Id to a type lambda – type lambdas are not supported
      base.staticTypeApp(base.loadTypSymbol("squid.feature.TrickyTypes$#HK"),
        base.staticTypeApp(base.loadTypSymbol("squid.feature.TrickyTypes$#Id"), Nil)::Nil))
    
  }
  
  test("Modular Metaprogramming") {
    
    val mm = new ModularMetaprogImpl[Int]
    println(mm.cde)
    eqt(mm.cde, code"Option.empty[Int]")
    println(mm.cde2)
    eqt(mm.cde2, code"Option.empty[ModularMetaprog#Typ2]")
    // TODO only embed abstract types when static path to them is static?
    //      indeed, it would be more useful for programmers to reject the definition of cde2 above than accept it
    
  }
  
}

object TrickyTypes {
  
  type Abstract
  type Id[T] = T
  type HK[F[_]]
  type AbsT[T] <: Int
  
  import TestDSL.Predef._
  abstract class ModularMetaprog {
    type Typ
    implicit val Typ: CodeType[Typ]
    lazy val cde = code"Option.empty[Typ]" // Note: here Squid uses the implicit value available
    type Typ2
    lazy val cde2 = code"Option.empty[Typ2]" // Note: here in the absence of implicit, Squid widens the type to ModularMetaprog#Typ2!!
  }
  class ModularMetaprogImpl[T: CodeType] extends ModularMetaprog {
    type Typ = T
    val Typ = codeTypeOf[T] // Note: in Scala 2.12 (but not 2.11), it is sufficient to write: `implicitly`
  }
  
}