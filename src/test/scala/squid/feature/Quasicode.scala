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

class Quasicode extends MyFunSuite {
  import TestDSL.Predef._
  import TestDSL.Quasicodes._
  
  test("Unquotes and Free Variables") {
    
    val a = code{ Math.pow(2, ?x) }
    
    eqt(a, code"Math.pow(2, ?x)")
    
    assertDoesNotCompile("a.run") // Error:(12, 7) Cannot prove that AnyRef <:< Any{val x: Double}.
    
    val b = code{ (x: Double) => $(a) }
    assert((b.run apply 3) == 8)
    
  }
  
  val seq @ Seq(x,y,z) = Seq( code(1), code(2), code(3) )
  
  test("Vararg Insertions") {
    
    val ls = code{ List($(x,y,z)) }
    eqt(ls, code"List($$(x,y,z))")  // remove this syntax (it can be confusing)? -- in ction $$ should be reserved for holes...
    eqt(ls, code"List(${seq: _*})")
    eqt(ls, code"List($seq*)")
    eqt(ls, code{ List( $(seq:_*) ) })
    
  }
  
  test("Vararg Free Variables") {
    import base.$$_*
    
    //val ls = ir"List($$ls:_*)" // FIXME: Error:(33, 14) Embedding Error: Internal error: type `<error>` is erroneous...
    
    val seq = code"?ls: Seq[Int]"
    val ls = code"List($seq:_*)"
    // Note: syntaxes code"List((?ls:Seq[Int]):_*)" and code"List[Int](?ls:_*)" do not work ("applyDynamic does not support passing a vararg parameter")
    
    // these old syntaxes still work but yield a deprecation warning (unless expanded in a macro like here):
    assertCompiles("""
    eqt(code"List(($$ls: Seq[Int]):_*)", ls)
    
    //eqt(ls, ir"List[Int]($$ls:_*)") // FIXME: Error:(35, 13) exception during macro expansion: 
    //eqt(ls, code{ List[Int]($$('ls):_*) }) // FIXME: Error:scala: Error: assertion failed: 
    eqt(ls, code{ List($$[Seq[Int]]('ls):_*) })
    eqt(ls, code{ List($$_*[Int]('ls):_*) })
    eqt(ls, code{ List[Int]($$_*('ls):_*) })
    """)
    
  }
  
}


















