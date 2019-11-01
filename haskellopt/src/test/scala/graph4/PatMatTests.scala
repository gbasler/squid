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
    // Q: why does this now look worse than in dd48e627c8f3aeaa5691833260e2dd3b6e3b05bd ?
    TestHarness("PatMat",
      //prefixFilter = "f0",
    )(
    )
  )
  
  test("PatMatRec") (
    // FIXME ordering problem in scheduling of t2'1 due to conflicting defs
    TestHarness("PatMatRec",
      //prefixFilter = "t2'1",
      //
      //schedule = false,
    )(
    )
  )
  
  test("PatMatRec2") (
    TestHarness("PatMatRec2",
      //prefixFilter = "usum'2",
      //prefixFilter = "usum ",
      //
      dumpGraph = true,
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
