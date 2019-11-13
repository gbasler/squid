package graph4

import squid.utils._
import org.scalatest.FunSuite

class FusionTests extends FunSuite {
  object TestHarness extends TestHarness
  import CheckDSL._
  
  test("Deforest") (
    TestHarness("Deforest",
      //prefixFilter = "pgrm",
      //
      dumpGraph = true,
    )(
      check('pgrm, List(1,2,3))(List(1,2,3).map(_+1).sum)
    )
  )
  
}
