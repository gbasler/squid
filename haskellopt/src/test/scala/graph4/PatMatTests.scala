package graph4

import squid.utils._
import org.scalatest.FunSuite

class PatMatTests extends FunSuite {
  object TestHarness extends TestHarness
  import CheckDSL.check
  
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
    )(
    )
  )
  
  test("PatMatRec") (
    TestHarness("PatMatRec",
      //prefixFilter = "t2'1",
    )(
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
    )
  )
  
  test("IterEither") (
    // TODO test commented defs
    TestHarness("IterEither",
      dumpGraph = true,
    )(
    )
  )
  
  // TODO test rec producer feeding into rec consumer
  
}
