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
package anf

import utils._
import ir._

class NormalizationTests extends MyFunSuite(SimpleANFTests.DSLWithEffects) {
  import DSL.Predef._
  
  test("Normalization of Tail Bindings") {
    
    code"readInt" eqt code"val n = readInt; n"
    
    code"print(0); val r = {println; readInt}; r" eqt code"print(0); println; readInt"
    
  }
  
  test("Normalization of Unit Values") {
    
    code"val a = (); a" eqt code"()"
    
    code"val a = identity(()); a" eqt code"identity(())"
    
    code"val a = println; print(a)" eqt code"println; print(())"
    
    code"val a = ??? ; print(a)" |> (a => a eqt a)
    
    code"println; ()" eqt code"println"
    
    code"val a = ??? ; ??? ; a" eqt code"val a = ??? ; ???"  // Note: this happens because  ???.typ <:< Unit  and  ???.typ <:< a.typ  and  a.isPure
    
  }
  
  test("Normalization of Unit Values In Patterns") {
    
    code"val a = println; 123" match {
      case code"val $x = println; $body" =>
        fail // bindings of type Unit are currently removed; this test make sure that they are NOT removed in patterns 
        // if the binder is extracted (as above), which would raise a runtime assertion error (missing extracted term).
      case code"println; $body" =>
        body eqt code"123"
    }
    
  }
  
  object ONorm extends DSL.SelfTransformer with transfo.OptionNormalizer with TopDownTransformer
  
  test("Option Normalization") {
    
    code"Option.empty[Int] map (_+1)" transformWith ONorm eqt 
      code"if ((None:Option[Int]).isDefined) Some((None:Option[Int]).get+1) else None"
    
    // FIXME: assertion failed; at squid.ir.RuntimeSymbols$$anonfun$1.apply(RuntimeSymbols.scala:55)
    //ir"Option('ok)" transformWith ONorm eqt 
    //  ir"if ('ok == null) None else Some('ok)"
    
  }
  
  object INorm extends DSL.SelfTransformer with transfo.IdiomsNormalizer with TopDownTransformer
  
  test("Misc Normalizations") {
    
    code"42.toString.toString" transformWith INorm eqt code"42.toString"
    
    code""" (("a":String)+"b").toString.toString |> println """ transformWith INorm eqt code""" (("a":String)+"b").toString |> println """
    
    code"42.asInstanceOf[Int]" transformWith INorm eqt code"42" // Note: actually produces ir"42:Int" 
    code"(if (true) 42 else 43).asInstanceOf[Int]" transformWith INorm eqt code"if (true) 42 else 43"
    
    val x = code"???.asInstanceOf[Int]" transformWith INorm
    x eqt code"???"
    x.rep.dfn.isInstanceOf[base.Ascribe]
    
    // Q: why ascription not removed? in:
    //println(ir"val envVar = (42:Any).asInstanceOf[Int]; println(envVar)")
    //println(ir"var a: Int = 0; a = (readInt:Any).asInstanceOf[Int]; println(a)")
    //println(ir"var a: Int = 0; a = (readInt:Any).asInstanceOf[Int]; println(a)".rep.dfn)
    // NOTE: ^ the ascription actually is there in the Rep; it just does not show up in the Scala tree for some reason
    
    code"implicitly[Ordering[Int]]" transformWith INorm eqt code"scala.math.Ordering.Int: scala.math.Ordering[scala.Int]"
    
  }
  
}
