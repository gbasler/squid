package squid
package anf.analysis

import squid.lang.InspectableBase
import utils._

/**
  * Created by lptk on 05/02/17.
  * 
  * TODO abstract construct for looping, sequence, etc.
  * 
  */
trait ControlFlowBase extends InspectableBase {
  import Predef.QuasiContext
  
  /** Abstract construct for two alternative paths which execution is either one or the other -- never both nor none
    * TODO generalize by having T0 and T1 for the alternatives' types */
  abstract class OneOf[T,C](val original: IR[T,C]) {
    type C0 <: C // Note: upper bound won't really affect anything since IR is contravariant in its context
    type C1 <: C
    val main: IR[T,C0]
    val alt: IR[T,C1]
    val rebuild: (IR[T,C0],IR[T,C1]) => IR[T,C]
  }
  class OneOfUniform[T,C](original: IR[T,C], val main: IR[T,C], val alt: IR[T,C])(val rebuild: (IR[T,C],IR[T,C]) => IR[T,C]) extends OneOf[T,C](original) {
    type C0 = C
    type C1 = C
  }
  
  object OneOf {
    def unapply[T,C](q: IR[T,C]): Option[OneOf[T,C]] = unapplyOneOf[T,C](q)
  }
  def unapplyOneOf[T,C](q: IR[T,C]): Option[OneOf[T,C]] = q match {
      
    case ir"if ($cond) $thn else $els : $t" => Some(new OneOfUniform(q.asInstanceOf[IR[t.Typ,C]],thn,els)((thn,els) => ir"if ($cond) $thn else $els")
      .asInstanceOf[OneOf[T,C]]) // Casts, needed because Scala does not know that T =:= t ; note that if we omit the one for `q`, Any is inferred and we don't get the right type implicit!!
      
    case ir"($opt:Option[$to]).fold[$tf]($ifEmpty)(x => $body)" =>
      val rebuild_f = (ifEmpty:IR[tf.Typ,C],body:IR[tf.Typ,C{val x: to.Typ}]) => ir"$opt.fold($ifEmpty)(x => $body)"
      Some(new OneOf[tf.Typ,C](q.asInstanceOf[IR[tf.Typ,C]]) {
        type C0 = C
        type C1 = C{val x: to.Typ}
        val main = ifEmpty
        val alt = body
        val rebuild = rebuild_f
      }.asInstanceOf[OneOf[T,C]])
    case _ => None
  }
  
  
}


