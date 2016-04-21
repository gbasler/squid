package scp
package feature

import org.scalatest.FunSuite
import org.scalatest.ShouldMatchers

import utils.Debug._

class TypeMatching extends FunSuite with ShouldMatchers {
  import TestDSL._
  
  test("Extracting from Nothing") {
    val x = dsl"Option.empty".cast[Option[_]]
    x match {
      case dsl"$y: Option[Int]" =>
    }
    x match {
      case dsl"$y: Option[$t]" => assert(t.rep =:= typeRepOf[Nothing])
    }
    x match {
      case dsl"$y: Option[Option[$t]]" => assert(t.rep =:= typeRepOf[Nothing])
    }
  }
  
}


