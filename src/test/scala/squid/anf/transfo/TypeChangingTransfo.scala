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

class TypeChangingTransfo extends MyFunSuite(SimpleANFTests.DSLWithEffects) {
  import DSL.Predef._
  import DSL.Quasicodes._
  import DSL.{ANFTypeChangingCodeTransformer}
  
  object Tr extends DSL.SelfTransformer with ANFTypeChangingCodeTransformer {
    override def transformChangingType[T,C](code: Code[T,C]): Code[_,C] = code match {
      case code"Symbol($str)" => str
      case code"println($x)" => code"println(${transformChangingType(x)})"
      case _ => super.transformChangingType(code)
    }
  }
  
  test("Basic") {
    
    code"println('ok)" transformWith Tr eqt code"""println("ok")"""
    
  }
  
  test("Lambdas") {
    
    code"(s:String) => Symbol(s)" transformWith Tr eqt code"(s:String) => s"
    
    // Note: omitting the `:Any` ascription here breaks the transformation, because the default recursive call that 
    // transforms the prefix to `.toString` sees the term changing type and gives up.
    code"(n:Int) => ('ok:Any).toString * n" transformWith Tr eqt 
      code"(n:Int) => augmentString(${ code""""ok"""":Code[Any,Any] }.toString) * n"
    // ^ this is a bit tricky: the `.toString` symbol in the transformed code is that of Ant, BUT the object to which it
    // is applied is "ok", which is known to be a 'pure' type and thus the whole thing is not let-bound...
    
  }
  
}
