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

package squid.statics

import org.scalatest.FunSuite

object CompileTimeTests {
  val s = compileTime{ 'ok }
  val s0 = 'ko
}
import CompileTimeTests._

// Note: this feature seems very slow!
//   – which was to be expected since it's based on toolBox instantiation and runtime compilation... at compile-time...
class CompileTimeTests extends FunSuite {
  
  test("Basics") {
    
    val t = compileTime{ s.name }
    val u = compileTime{ t.length + s.toString.size }
    
    assert(u == "ok".length + "'ok".size)
    
    // This raises an assertion error at compile time, making compilation fail:
    assertDoesNotCompile("compileTime{ scala.Predef.assert(u == 0) }")
    
    compileTime{ scala.Predef.assert(u == 2 + 3) }
    
    val local = 'oops
    assertDoesNotCompile("compileTime{ local.name }")
    // Error:(43, 26) Non-static identifier 'local' of type: Symbol
    
    //object Local { type T }
    //println(compileTime{ Option.empty[Local.T] })
    // ^ FIXME crashes toolbox
    
  }
  
  test("Implicits") {
    
    def lostStaticValue(implicit sym: CompileTime[Symbol]) = {
      assertDoesNotCompile("compileTime{ sym.get.name }")
      sym.get
    }
    
    implicit val foo = CompileTime('foo)
    
    val bar = compileTime{ foo.get.name }
    
    assert(lostStaticValue == 'foo)
    assert(`test withStaticSymbol`(3) == "foofoofoo")
    assert(`test withStaticSymbol`(2)('bar) == "barbar") // implicit conersion/lifting to CompileTime
    
    val res = compileTime{ `test withStaticSymbol`(3) }
    assertDoesNotCompile("compileTime{ scala.Predef.assert(res == 'foofoofoo0.name) }")
                          compileTime{ scala.Predef.assert(res == 'foofoofoo .name) }
    
  }
  
  test("Lack of Separate Compilation") {
    
    // TODO B/E:
    assertDoesNotCompile("compileTime{ s0.name }")
    // Error:(76, 28) exception during macro expansion: 
    //  scala.tools.reflect.ToolBoxError: reflective compilation has failed:
    //  object name is not a member of package CompileTimeTests.s0
    
  }
  
}
