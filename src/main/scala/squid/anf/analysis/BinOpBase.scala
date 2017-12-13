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
package anf.analysis

import squid.lang.InspectableBase
import utils._

/**
  * Abstract constructs for binary operations.
  * 
  * TODO cache extractors to make these not too expensive to unapply
  * 
  * TODO other standard operations (multiplication, etc.) and properties
  * 
  */
trait BinOpBase extends InspectableBase {
  import Predef.QuasiContext
  
  type Rebuild[T,C] = (Code[T,C],Code[T,C]) => Code[T,C]
  
  abstract class BinOp[T,C](val original: Code[T,C]) {
    
    def lhs: Code[T,C]
    def rhs: Code[T,C]
    def rebuild: Rebuild[T,C]
    
    def commutes: Bool = false
    def associatesWith(bo: BinOp[T,C]): Bool = false
    def autoAssociative: Bool = associatesWith(this)
    def distributesOver(bo: BinOp[T,C]): Bool = false
    
    def commute = rebuild(rhs,lhs)
    
  }
  
  class Addition[T,C](original: Code[T,C], val lhs: Code[T,C], val rhs: Code[T,C])(val rebuild: Rebuild[T,C]) extends BinOp[T,C](original) {
    //println(s"New addition $lhs $rhs")
    override def commutes: Bool = true
    // TODO other properties...
  }
  
  /** Caveat: matchers that produce more nodes may trigger further matching... */
  object BinOp {
    def unapply[T,C](q: Code[T,C]): Option[BinOp[T,C]] = unapplyBinOp[T,C](q)
  }
  def unapplyBinOp[T,C](q: Code[T,C]): Option[BinOp[T,C]] = {
    val intq = q.asInstanceOf[Code[Int,C]]
    q match {
      case code"($lhs:Int)+($rhs:Int)"   => Some(new Addition(intq,lhs,rhs)((lhs,rhs) => code"$lhs+$rhs").asInstanceOf[Addition[T,C]])
      case code"($lhs:Int)+($rhs:Short)" => Some(new Addition(intq,lhs,code"$rhs.toInt")((lhs,rhs) => code"$lhs+$rhs").asInstanceOf[Addition[T,C]])
      case code"($lhs:Int)-($rhs:Int)"   => Some(new Addition(intq,lhs,code"-$rhs")((lhs,rhs) => code"$lhs+$rhs").asInstanceOf[Addition[T,C]])
      case _ => None
    }
  }
  
  
  // Helpers:
  
  object Operands {
    def unapply[T,C](q: BinOp[T,C]): Some[(Code[T,C],Code[T,C])] = /*println(s"ops $q") before*/ Some(q.lhs -> q.rhs)
  }
  /** Workaround the current limitation of RwR pattenr aliases preventing the use of Operands. This extracts two binops, 
    * where the second one is extracted from the rhs of the first. */
  object BinOp3 {
    def unapply[T,C](q: Code[T,C]): Option[BinOp[T,C]->BinOp[T,C]] = q match {
      case code"${BinOp(bo0 @ Operands(_, BinOp(bo1)))}: $t" => Some((bo0 -> bo1).asInstanceOf[BinOp[T,C]->BinOp[T,C]])
      case _ => None
    }
  }
  
  
  
  
}




