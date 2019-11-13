package graph4

import squid.utils._
import org.scalatest.FunSuite

class PatMatTests extends FunSuite {
  object TestHarness extends TestHarness
  import CheckDSL._
  
  // TODO: investigate why some tests raise the HaskellAST.Let.reorder warning
  
  test("Motiv") (
    TestHarness("Motiv",
      //prefixFilter = "pgrm",
      //
      dumpGraph = true,
    )(
      check('pgrm)(3)
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
      //prefixFilter = "t1",
      //prefixFilter = "t2'1 ",
      //prefixFilter = "t1'2 ",
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
      //prefixFilter = "alternate'0",
      //
      dumpGraph = true,
    )(
      check("alternate'0")(List(true, false, true, false, true)),
    )
  )
  
  test("Statistics") (
    // Note: Optimized `maxMaybe0` thanks to case-arm/ctor commuting!
    //       However, `maxMaybe1` is harder to optimize because it resuses the scrutinee; would require some flow analysis!
    // Note: nicely, maxTest'0 gets fully partially evaluated!
    TestHarness("Statistics",
      //prefixFilter = "maxMaybe0",
      //prefixFilter = "lastMaybe",
      //prefixFilter = "lastWeird",
      //prefixFilter = "maxTest'0",
      dumpGraph = true,
      //executeResult = false
    )(
      check('maxMaybe0, Nil)(None),
      check('maxMaybe0, List(1,2,3))(Some(3)),
      check('maxMaybe0, List(1,2,3,1))(Some(3)),
      check('maxMaybe0, List(1,2,3,1,5))(Some(5)),
      check('maxMaybe1, List(1,2,3,1))(Some(3)),
      check("maxTest'0")(Some(3)),
      check('lastMaybe, Nil)(None),
      check('lastMaybe, List(1,2,3))(Some(3)),
      check('lastWeird, Nil)(None),
      check('lastWeird, List(1,2,3,4))(Some(4)),
    )
  )
  
  test("IterEither") (
    // TODO recover opt of simple9, which used to schedule to just `False`
    // FIXME big scalability issues when uncommenting more simpleX defs
    //   — uncommenting all leads to 40k nodes! (EDIT: now probably slightly fewer)
    //   Most of these nodes are most likely dead, due to messed up invalid Case paths,
    //   since the output programs are actually small.
    TestHarness("IterEither",
      //prefixFilter = "count ",
      //prefixFilter = "simple",
    )(
      check('count, 4)(4),
      //check('simple0)(42),
      check('simple1)(20),
      check('simple5)(0),
      //check('simple8)(3),
      check('simple9)(false),
    )
  )
  
  test("ListsFun") (
    TestHarness("ListsFun",
    )(
      check('test)(6)
    )
  )
  
}
