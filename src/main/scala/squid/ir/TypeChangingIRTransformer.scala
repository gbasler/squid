package squid
package ir

import utils._

trait TypeChangingIRTransformer extends IRTransformer { self =>
  import base._
  import Quasiquotes._
  def transform[T,C](code: IR[T,C]): IR[T,C] = {
    val T = code.typ
    transformChangingType(code) match {
      case ir"$code: $$T" => code
      case newCode =>
        System.err.println(s"Top-level type ${T.rep} cannot be changed to ${newCode.typ.rep}; in: $code ~> $newCode")
        code
    }
  }
  def transformChangingType[T,C](code: IR[T,C]): IR[_,C]
}
