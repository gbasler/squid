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
  
  def compileTime[A](a: A): A = macro StaticMacros.compileTimeImpl[A]
  
  @MacroSetting(debug = true)
  def dbg_compileTime[A](a: A): A = macro StaticMacros.compileTimeImpl[A]
  
  
  // ---
  
  // This methid is only for testing purposes; needs separate compilation 
  // private[statics]  –– can't make it package-private as that would prevent reflective compilation
  def `test withStaticSymbol`(n: Int)(implicit sym: CompileTime[Symbol]) = sym.get.name * n
  
}
