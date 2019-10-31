package graph4

import squid.utils._
import org.scalatest.FunSuite

class PatMatTests extends FunSuite {
  object TestHarness extends TestHarness
  import CheckDSL.check
  
  test("Motiv") (
    TestHarness("Motiv",
      //prefixFilter = "f",
      //
      dumpGraph = true,
    )(
    )
  )
  
  test("PatMat") (
    // FIXME slt0 generates type-ambiguous code
    TestHarness("PatMat",
    )(
    )
  )
  
  test("PatMatRec") (
    TestHarness("PatMatRec",
    )(
    )
  )
  
  test("PatMatRec2") (
    // FIXME unbound π variables
    //TestHarness("PatMatRec2",
    //)(
    //)
  )
  
  test("InterpSimple") (
    TestHarness("InterpSimple",
      dumpGraph = true,
    )(
    )
  )
  
}
