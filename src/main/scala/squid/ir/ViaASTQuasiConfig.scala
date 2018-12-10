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
import quasi._
import utils._
import meta.RuntimeUniverseHelpers.{mkTag, sru}

import scala.reflect.macros.blackbox

/** Constructs the SimpleAST representation of the program fragment at compile time,
  * before turning it into Scala construction code (via MirrorBase).
  * This is just for demonstration purposes. More useful things can be done during that intermediate, compile-time phase. */
class ViaASTQuasiConfig extends QuasiConfig {
  
  def embed(c: blackbox.Context)(baseTree: c.Tree, baseType: c.Type, user: BaseUser[c.type]) = {
    import c.universe._
    
    object AST extends SimpleAST
    
    var insertions = List.empty[(String, (Tree, Map[String, AST.BoundVal]))]
    
    val ast = user(AST) {
      case (tr, bind) => 
        val name = s"<insert ${insertions.size}>"
        insertions ::= name -> (tr, bind)
        AST.hole(name, AST.uninterpretedType(mkTag(sru.NoType))) // TODO: should probably do better than `NoType` ... make BaseUser pass it along
    }
    
    object Meta extends MetaBases {
      private var cnt = 0
      val u: c.universe.type = c.universe
      def freshName(hint: String) = TermName(s"_${cnt}_$hint") alsoDo (cnt += 1)
    }
    object base extends Meta.MirrorBase(baseTree)
    
    val insertionsMap = insertions.toMap
    
    /** Note: we do not override `extrudedHandle` */
    object R extends AST.Reinterpreter {
      val newBase: base.type = base
      def apply(r: AST.Rep) = r.dfn match {
        case h @ AST.Hole(name) =>
          insertionsMap get name map {
            case (tr, bind) => base.substitute(q"$tr.rep", bind mapValues (bound andThen base.readVal))
          } getOrElse apply(h)
        case d => apply(d)
      }
    }
    
    val code = R(ast)
    
    q"..${base.mkSymbolDefs}; $code"
  }
  
}
