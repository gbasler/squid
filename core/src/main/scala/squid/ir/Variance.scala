package squid
package ir

import utils._
import meta.RuntimeUniverseHelpers.sru

sealed abstract class Variance(val asInt: Int) {
  def * (that: Variance) = Variance(asInt * that.asInt) //(this, that) match {}
  def symbol = this match {
    case Invariant => "="
    case Covariant => "+"
    case Contravariant => "-"
  }
  override def toString = s"[$symbol]"
}
object Variance {
  def apply(asInt: Int) = asInt match {
    case 0 => Invariant
    case 1 => Covariant
    case -1 => Contravariant
  }
  def of (s: sru.TypeSymbol) =
    if (s.isCovariant) Covariant else if (s.isContravariant) Contravariant else Invariant
}
case object Invariant extends Variance(0)
case object Covariant extends Variance(1)
case object Contravariant extends Variance(-1)



