package graph4

import squid.utils._
import org.scalatest.FunSuite

class PatMatTests extends FunSuite {
  object TestHarness extends TestHarness
  import CheckDSL._
  
  test("Motiv") (
    TestHarness("Motiv",
      //prefixFilter = "pgrm",
      //
      dumpGraph = true,
    )(
    )
  )
  
  test("PatMat") (
    // Note: slt0 used to generate type-ambiguous code (when pattern matching was not reduced)
    TestHarness("PatMat",
      //prefixFilter = "f0",
      //prefixFilter = "f0'4",
      //prefixFilter = "f1'1",
    )(
      check("e1'0")(0),
      check("f0'3")(Some(1)),
      check("f0'4", Some(3))(Some(5)),
      check("f1'1")(true),
      check('u1_0)(1 + 2 + 3 + 4),
    )
  )
  
  test("PatMatRec") (
    TestHarness("PatMatRec",
      //prefixFilter = "t2'1",
    )(
      check("t2'0'5")(List(-1, 1, -1, 1, -1)),
      check("t2'1'5")(-1),
    )
  )
  
  test("PatMatRec2") (
    TestHarness("PatMatRec2",
      //prefixFilter = "usum'2",
      //prefixFilter = "usum ",
      //prefixFilter = "oops'0",
      //
      dumpGraph = true,
    )(
      check("alternate'0")(List(true, false, true, false, true)),
    )
  )
  
  test("Statistics") (
    TestHarness("Statistics",
      //prefixFilter = "maxMaybe0",
    )(
      check('maxMaybe0, Nil)(None),
      check('maxMaybe0, List(1,2,3,1))(Some(3)),
      check('maxMaybe1, List(1,2,3,1))(Some(3)),
    )
  )
  
  test("IterEither") (
    // TODO test commented defs
    TestHarness("IterEither",
      //prefixFilter = "count",
    )(
      check('count, 4)(4),
    )
  )
  
  // TODO test rec producer feeding into rec consumer
  
}
