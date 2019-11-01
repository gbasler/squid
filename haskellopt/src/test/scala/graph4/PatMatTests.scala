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
    // FIXME ordering problem in scheduling
    TestHarness("PatMatRec",
      schedule = false,
    )(
    )
  )
  
  test("PatMatRec2") (
    // FIXME unbound π variables in scheduling
    TestHarness("PatMatRec2",
      dumpGraph = true,
      schedule = false,
    )(
    )
  )
  
  test("InterpSimple") (
    // FIXME graph diverges in version with two params and a param cycle
    // FIXME scheduling becomes wrong when making the list bigger — probably due to no proper checks testing
    TestHarness("InterpSimple",
      dumpGraph = true,
    )(
    )
  )
  
}
