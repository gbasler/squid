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
      //prefixFilter = "f0'",
      //prefixFilter = "f0'4",
      //prefixFilter = "f0'f1",
      //prefixFilter = "f1'1",
    )(
      check("e1'0")(0),
      check("f0'3")(Some(1)),
      check("f0'4", Some(3))(Some(5)),
      check("f0'4", None)(Some(1)),
      check("f1'1")(true),
      check("f0'f1")(Some(43)),
      check('u1_0)(1 + 2 + 3 + 4),
    )
  )
  
  test("PatMatRec") (
    // TODO investigate: t2'1'5 used to take much fewer rewritings before CASE commutings, but had the same result!
    //   Perhaps commuting should only be done if there's nothing else to do...
    TestHarness("PatMatRec",
      //prefixFilter = "t2'1 ",
      //prefixFilter = "t2'1'5",
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
    // Note: Seems like we can't optimize this, because the case scrutinizes itself recursively...
    //       We can't commute it past an arbitrary number of cases).
    TestHarness("Statistics",
      //prefixFilter = "maxMaybe0",
      dumpGraph = true,
      schedule = false // TODO handle scheduling of multi-returns
    )(
      check('maxMaybe0, Nil)(None),
      check('maxMaybe0, List(1,2,3,1))(Some(3)),
      check('maxMaybe1, List(1,2,3,1))(Some(3)),
    )
  )
  
  test("IterEither") (
    // FIXME fix assertion failure arising when removing prefixFilter
    TestHarness("IterEither",
      prefixFilter = "count ",
    )(
      check('count, 4)(4),
      check('simple0)(42),
      check('simple1)(20),
      check('simple5)(0),
      check('simple8)(3),
      check('simple9)(false),
    )
  )
  
  // TODO test rec producer feeding into rec consumer
  
}
