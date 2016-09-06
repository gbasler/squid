package scp
package ir2

import lang2._
import scp.quasi2.MetaBases
import utils._
import utils.meta.{RuntimeUniverseHelpers => ruh}
import ruh.sru

import scala.collection.mutable

/** Useful when representing value bindings as redexes */
trait BindingNormalizer extends SimpleRuleBasedTransformer {
  import base.Predef._
  
  rewrite {
    case ir"((a: $ta) => (b: $tb) => $body : $t)($x)($y)" =>
      ir"((a: $ta) => ((b: $tb) => $body : $t)($y))($x)"
  }
  
}
