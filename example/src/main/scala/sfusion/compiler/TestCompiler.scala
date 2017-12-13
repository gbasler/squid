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

package sfusion
package compiler

import java.io.File

import squid.utils._
import squid.ir._
import squid.lang._

class TestCompiler extends Compiler {
  
  val debug = false
  //val debug = true
  
  override def dumpPhase(name: String, mkCode: => String, time: Long) = {
    
    val p = curPrinter.get
    p.println(s"\n// === $name ===\n")
    if (debug) println(s"\n// === $name ===\n")
    val t0 = System.nanoTime()
    if (debug) println("--- PRINTING")
    val str = mkCode
    val t1 = System.nanoTime()
    val M = 1000*1000
    p.println(s"// Transfo time: ${time/M}ms  Stringifying time: ${(t1-t0)/M}ms\n")
    if (str == SAME) p.println("// Same as above.")
    else p.println(str)
    if (debug) println(s"$str\n--- DONE")
    
  }
  
  var curPrinter = Option.empty[java.io.PrintWriter]
  override def wrapOptim[A](id: String)(code: => A) = {
    //println(id)
    import File.{separator => SEP}
    val file = new File(s"example_gen${SEP}test${SEP}sfusion${SEP}$id.scala")
    val p = new java.io.PrintWriter(file)
    curPrinter = Some(p)
    p.println("// Automatically-generated code")
    try super.wrapOptim(id)(code) finally {
      p.close()
      curPrinter = None
    }
  }
  
  
}

