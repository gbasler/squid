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
package ir

import lang._
import squid.quasi.MetaBases
import utils._
import utils.meta.{RuntimeUniverseHelpers => ruh}
import ruh.sru

import scala.collection.mutable

/** Useful when representing value bindings as redexes
  * TODO find a way so the lhs of these RwR won't get normalized immediately by the IR?... */
trait BindingNormalizer extends SimpleRuleBasedTransformer {
  import base.Predef._
  
  import lib._
  
  rewrite {
    
    // TODO separate these RW from the actual bond-norm (which ANF does not use -- it may even be harmful to it)
    // (cf: file CurryEncoding.ApplicationNormalizer.scala)
    /** Curries function applications, useful when inlining Scala methods with multiple parameters as lambdas. */
    case code"uncurried0($f: $t)()"                                                        => code"$f"
    case code"uncurried2($f: ($ta => $tb => $t))($x, $y)"                                  => code"$f($x)($y)"
    case code"uncurried3($f: ($ta => $tb => $tc => $t))($x, $y, $z)"                       => code"$f($x)($y)($z)"
    case code"uncurried4($f: ($ta => $tb => $tc => $td => $t))($x, $y, $z, $u)"            => code"$f($x)($y)($z)($u)"
    case code"uncurried5($f: ($ta => $tb => $tc => $td => $te => $t))($x, $y, $z, $u, $v)" => code"$f($x)($y)($z)($u)($v)"
      
    /** Commutes bindings to make them normal. */
    case code"((a: $ta) => $f: $tb => $tc)($x)($y)" => code"val a = $x; $f($y)"
    
  }
  
}
/*
NOTE: Scala does some commuting of applications! cf:
    > Shallow Tree: {
      val $dummy$ = $qmark$qmark$qmark;
      {
        val a = 11;
        ((b: Int) => b.$plus(1))
      }(22)
    }
    > Typed[Int]: {
      val a: Int = 11;
      ((b: Int) => b.+(1)).apply(22)
    }
*/

