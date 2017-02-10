package squid
package anf.analysis

import squid.ir.SimpleANF
import squid.lang.InspectableBase
import utils._

/**
  * Created by lptk on 10/02/17.
  */
trait BlockHelpers extends SimpleANF {
  
  trait Block[T,C] extends IR[T,C] {
    type C0 <: C
    val stmts: List[IR[_,C0]]
    val res: IR[T,C0]
    
    // TODO methods for hygienically and safely performing statements manipulations, such as filtering statements based
    // on whether they depend on some context, etc.
    //def splitDependent[D:Ctx]: (List[_,D],List[_,C0]) -- or ((Block[Unit,C] => Block[Unit,C]) => Block[T,D]) 
  }
  
  object Block {
    def unapply[T,C](q: IR[T,C]): Option[Block[T,C]] = unapplyBlock[T,C](q)
  }
  def unapplyBlock[T,C](q: IR[T,C]): Option[Block[T,C]] = {
    val bl = q.rep.asBlock
    // Q: is it okay to extract single expressions with this extractor?
    /*if (bl._1.isEmpty) None
    else*/ Some(new IR[T,C](q.rep) with Block[T,C]{
      val stmts: List[IR[_,C0]] = bl._1 map (_.fold(_._2, identity) |> IR.apply[T,C0])
      val res: IR[T,C0] = IR(bl._2)
    })
  }
  
  object WithResult {
    def unapply[T,C](b: Block[T,C]): Some[Block[T,C] -> IR[T,b.C0]] = Some(b->b.res)
  }
  
}
