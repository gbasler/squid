package graph4

import squid.utils._
import org.scalatest.FunSuite

class BasicTests extends FunSuite {
  object TestHarness extends TestHarness
  import CheckDSL.check
  
  test("Basics") (
    TestHarness("Basics", dumpGraph = true)
    //TestHarness("Basics", dumpGraph = true, prefixFilter = "gTest")
    //TestHarness("Basics", dumpGraph = true, prefixFilter = "fTest")
    (
      check('gTest0)(24),
      check('gTest2, 4)(24),
    )
  )
  
  test("BasicRec") (
    TestHarness("BasicRec", dumpGraph = true)
    (
      // TODO checks
    )
  )
  
  test("HigherOrder") (
    // Simplest example: run with prefixFilter = "hTest4"; hTest3 is similar but not reducible due to unsaturated functions...
    TestHarness("HigherOrder", dumpGraph = true)
    //TestHarness("HigherOrder", dumpGraph = true, prefixFilter = "hTest4")
    //TestHarness("HigherOrder", dumpGraph = true, prefixFilter = "g")
    (
      check('hTest3)(-12),
      check('hTest4)(-12),
      check('hTest5)(0),
      check('gTest0)(3),
      check('gTest1)(6),
      check('iTest0, 1)(101),
    )
  )
  
}
