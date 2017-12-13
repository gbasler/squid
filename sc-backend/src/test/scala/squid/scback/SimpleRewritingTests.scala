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

package squid.scback

import collection.mutable.ArrayBuffer
import ch.epfl.data.sc.pardis
import pardis.ir.Constant

class SimpleRewritingTests extends PardisTestSuite {
  
  import Sqd.Predef._
  import Sqd.Quasicodes._
  
  
  test("Constants") {
    
    sameDefs(code"666" rewrite {
      case code"666" => code"42"
    }, code"42")
    
    sameDefs(code"666" rewrite {
      case code"666" => code"42.0.toInt"
    }, code"42.0.toInt")
    
    sameDefs(code"666.0.toInt" rewrite {
      case code"666.0" => code"42.0"
    }, code"42.0.toInt")
    
  }
  
  
  test("FunctionN") {
    
    sameDefs(code"() => println(666)" rewrite {
      case code"666" => code"42"
    }, code"() => println(42)")
    
    sameDefs(code"val f = () => println(666); f()" rewrite {
      case code"666" => code"42"
    }, code"val f = () => println(42); f()")
    
    sameDefs(code"ArrayBuffer(1,2,3) map ((x: Int) => println(666))" rewrite {
      case code"666" => code"42"
    }, code"ArrayBuffer(1,2,3) map ((x: Int) => println(42))")
    
    sameDefs(code"(a:Int,b:Double,c:String) => ((a,b),Option[String](null))" rewrite {
      case code"(($x:Int,$y:Double),Option[String](null))" => code"val t = ($y.toInt, $x.toDouble); (t, Option(${Const("ok")}))"
    }, code"""(a:Int,b:Double,c:String) => { ((b.toInt, a.toDouble), Option("ok")) }""")
    
  }
  

}