package graph4

import squid.utils._
import org.scalatest.FunSuite

class InterpTests extends FunSuite {
  object TestHarness extends TestHarness
  import CheckDSL.check
  
  test("InterpSimple") (
    // FIXME graph diverges in version with two params and a param cycle
    TestHarness("InterpSimple",
      //dumpGraph = true,
    )(
    )
  )
  
  test("InterpTrivial") (
    TestHarness("InterpTrivial",
      //prefixFilter = "test1",
      dumpGraph = true,
    )(
    )
  )
  
  test("InterpTrivialRec") (
    TestHarness("InterpTrivialRec",
      //prefixFilter = "test1",
      dumpGraph = true,
    )(
    )
  )
  
}
