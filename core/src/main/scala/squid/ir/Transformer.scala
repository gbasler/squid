package squid
package ir

import squid.lang.InspectableBase
import squid.lang.Optimizer
import utils._

trait Transformer extends Optimizer { self =>
  val base: InspectableBase
  import base._
  
  object TranformerDebug extends PublicTraceDebug
  //lazy val TranformerDebug = new PublicTraceDebug{}
  
  /** Use `pipeline` to correctly apply a Transformer */
  protected def transform(rep: Rep): Rep
  final def transformTopDown(rep: Rep): Rep = (base topDown rep)(transform)
  final def transformBottomUp(rep: Rep): Rep = (base bottomUp rep)(transform)
  
  final def pipeline = transform
  //final def pipeline = r => {
  //  val r2 = transform(r)
  //  //println(s"$r  --->   $r2")
  //  //println(r.asInstanceOf[AnyRef] eq r2.asInstanceOf[AnyRef])
  //  if (!(r2 eq r)) substitute(r2) else r2
  //}
  
  def andThen(that: Transformer{val base: self.base.type}): Transformer{val base: self.base.type} = new Transformer {
    val base: self.base.type = self.base
    import base._
    def transform(rep: Rep): Rep = that transform self.transform(rep)
  }
  
}

trait TopDownTransformer extends Transformer { abstract override def transform(rep: base.Rep) = (base topDown rep)(super.transform) }
trait BottomUpTransformer extends Transformer { abstract override def transform(rep: base.Rep) = (base bottomUp rep)(super.transform) }

