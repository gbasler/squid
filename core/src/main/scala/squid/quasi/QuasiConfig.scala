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
package quasi

import utils._
import squid.lang.Base

import scala.reflect.macros.whitebox

/** Helper used for the dependent method type */
abstract class BaseUser[C <: whitebox.Context](val macroContext: C) {
  def apply(b: Base)(insert: (macroContext.Tree, Map[String, b.BoundVal]) => b.Rep): b.Rep
}

/** This abstraction is probably going to disappear or be simplified because it complicates the implementation with little return. */
abstract class QuasiConfig {
  
  def embed(c: whitebox.Context)(baseTree: c.Tree, baseType: c.Type, user: BaseUser[c.type]): c.Tree
  
}

class DefaultQuasiConfig extends QuasiConfig {
  
  def embed(c: whitebox.Context)(baseTree: c.Tree, baseType: c.Type, user: BaseUser[c.type]) = {
    import c.universe._
    
    object Meta extends MetaBases {
      private var cnt = 0
      val u: c.universe.type = c.universe
      //def freshName(hint: String) = c.freshName(u.TermName(hint)) // Creates problems with the Scala compiler when the produced code is processed by another macro
      def freshName(hint: String) = TermName(s"_${cnt}_$hint") alsoDo (cnt += 1)
    }
    object base extends Meta.MirrorBase(baseTree, baseType |> some)
    
    val code = user(base) {
      case (q"$tr: _*", bind) => q"$tr map (__$$ir => ${base.substitute(q"__$$ir.rep", bind mapValues base.readVal)}): _*"
      case (tr, bind) => base.substitute(q"$tr.rep", bind mapValues base.readVal)
    }
    
    q"..${base.mkSymbolDefs}; $code"
  }
  
  
}











