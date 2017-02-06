package squid
package anf.analysis

import squid.lang.InspectableBase
import utils._

/**
  * Created by lptk on 06/02/17.
  * 
  * Abstract constructs for binary operations.
  * 
  * TODO cache extractors to make these not too expensive to unapply
  * 
  * TODO other standard operations (multiplication, etc.) and properties
  * 
  */
trait BinOpBase extends InspectableBase {
  import Predef.QuasiContext
  
  type Rebuild[T,C] = (IR[T,C],IR[T,C]) => IR[T,C]
  
  trait BinOp[T,C] extends IR[T,C] {
  //class BinOp[T,C] extends IR[T,C](???) {
    
    def lhs: IR[T,C]
    def rhs: IR[T,C]
    def rebuild: Rebuild[T,C]
    
    def commutes: Bool = false
    def associatesWith(bo: BinOp[T,C]): Bool = false
    def autoAssociative: Bool = associatesWith(this)
    def distributesOver(bo: BinOp[T,C]): Bool = false
    
    def commute = rebuild(rhs,lhs)
    
  }
  
  class Addition[T,C](val lhs: IR[T,C], val rhs: IR[T,C])(val rebuild: Rebuild[T,C]) extends IR[T,C](rebuild(lhs,rhs).rep) with BinOp[T,C] {
    //println(s"New addition $lhs $rhs")
    override def commutes: Bool = true
    // TODO other properties...
  }
  
  /** Caveat: matchers that produce more nodes may trigger further matching... */
  object BinOp {
    def unapply[T,C](q: IR[T,C]): Option[BinOp[T,C]] = unapplyBinOp[T,C](q)
  }
  def unapplyBinOp[T,C](q: IR[T,C]): Option[BinOp[T,C]] = q match {
    case ir"($lhs:Int)+($rhs:Int)" => Some(new Addition(lhs,rhs)((lhs,rhs) => ir"$lhs+$rhs").asInstanceOf[Addition[T,C]])
    case ir"($lhs:Int)+($rhs:Short)" => Some(new Addition(lhs,ir"$rhs.toInt")((lhs,rhs) => ir"$lhs+$rhs").asInstanceOf[Addition[T,C]])
    case ir"($lhs:Int)-($rhs:Int)" => Some(new Addition(lhs,ir"-$rhs")((lhs,rhs) => ir"$lhs+$rhs").asInstanceOf[Addition[T,C]])
    case _ => None
  }
  
  
  // Helpers:
  
  object Operands {
    def unapply[T,C](q: BinOp[T,C]): Some[(IR[T,C],IR[T,C])] = /*println(s"ops $q") before*/ Some(q.lhs -> q.rhs)
  }
  /** Workaround the current limitation of RwR pattenr aliases preventing the use of Operands. This extracts two binops, 
    * where the second one is extracted from the rhs of the first. */
  object BinOp3 {
    def unapply[T,C](q: IR[T,C]): Option[BinOp[T,C]->BinOp[T,C]] = q match {
      case ir"${BinOp(bo0 @ Operands(_, BinOp(bo1)))}: $t" => Some((bo0 -> bo1).asInstanceOf[BinOp[T,C]->BinOp[T,C]])
      case _ => None
    }
  }
  
  
  
  
}




