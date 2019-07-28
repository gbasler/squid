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

import squid.utils.MacroUtils.MacroSetting

package object statics { 
  
  import scala.language.experimental.macros
  
  /** Lifts a piece of code to a compile-time value, to be inlined and executed later by `compileTimeExec` or `compileTimeEval`. */
  def compileTime[A](a: A): A = macro CompileTimeMacros.compileTimeImpl[A]
  @MacroSetting(debug = true)
  def dbg_compileTime[A](a: A): A = macro CompileTimeMacros.compileTimeImpl[A]
  
  /** Execute a given piece of code at compile time for its side effects. */
  def compileTimeExec(cde: Unit): Unit = macro CompileTimeMacros.compileTimeExecImpl
  @MacroSetting(debug = true)
  def dbg_compileTimeExec(cde: Unit): Unit = macro CompileTimeMacros.compileTimeExecImpl
  
  /** Like compileTimeExec, but uses Java serialization to parse the result back to code that can be evaluated at run time. */
  def compileTimeEval[A](cde: A): A = macro CompileTimeMacros.compileTimeEvalImpl[A]
  @MacroSetting(debug = true)
  def dbg_compileTimeEval[A](cde: A): A = macro CompileTimeMacros.compileTimeEvalImpl[A]
  
  
  // ---
  
  // This method is only for testing purposes; needs separate compilation 
  // private[statics]  –– can't make it package-private as that would prevent reflective compilation
  def `test withStaticSymbol`(n: Int)(implicit sym: CompileTime[Symbol]) = sym.get.name * n
  
}
