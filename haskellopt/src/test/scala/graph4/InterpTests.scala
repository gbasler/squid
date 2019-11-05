package graph4

import squid.utils._
import org.scalatest.FunSuite

class InterpTests extends FunSuite {
  object TestHarness extends TestHarness
  import CheckDSL.check
  
  test("InterpSimple") (
    // TODO test version with two params and a param cycle (graph used to diverge)
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
