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
package utils

import scala.language.experimental.macros

/** Takes anything that looks like a tuple and makes it a List of the appropriate type.
  * `asList` also acts on "pseudo-tuples" of 0 and 1 field: () and (x) */
object Tuple2List {
  
  implicit def productAsList(tup: Product): List[Any] = macro asListImpl
  
  implicit def asList(tup: Any): List[Any] = macro asListImpl
  
  
  import scala.reflect.macros.whitebox
  
  def asListImpl(c: whitebox.Context)(tup: c.Tree): c.Tree = {
    import c.universe._
    
    // Used to be:
    /*
    tup match {
      case q"(..$defs)" => q"_root_.scala.List(..$defs)"
      case _ => null
    }
    */
    
    // Converts, eg, `a -> b -> c` to `a :: b :: c :: Nil`
    def rec(x: Tree): Tree = x match {
      case q"scala.this.Predef.ArrowAssoc[$_]($a).->[$_]($b)" =>
        val r = rec(b)
        if (r == null) q"$a :: $b :: Nil" else q"$a :: $r"
      case q"(..$defs)" => q"_root_.scala.List(..$defs)"
      case _ => null
    }
    //c.warning(c.enclosingPosition, showCode(tup))
    rec(tup) //and (x=>c.warning(c.enclosingPosition, showCode(x)))
  }
}
