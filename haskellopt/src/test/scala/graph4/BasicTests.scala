package graph4

import squid.utils._
import org.scalatest.FunSuite

class BasicTests extends FunSuite {
  object TestHarness extends TestHarness
  import CheckDSL.check
  
  test("Basics") (
    TestHarness("Basics", dumpGraph = true)
    //TestHarness("Basics", dumpGraph = true, prefixFilter = "foo_3")
    //TestHarness("Basics", dumpGraph = true, prefixFilter = "gTest")
    //TestHarness("Basics", dumpGraph = true, prefixFilter = "fTest")
    (
      check('gTest0)(24),
      check('gTest2, 4)(24),
      check('foo_3, 2)(46400),
    )
  )
  
  test("BasicRec") (
    TestHarness("BasicRec", dumpGraph = true,
      schedule = false // TODO schedule recursive functions
    )
    (
      // TODO checks
    )
  )
  
  test("HigherOrder") (
    // Simplest example: run with prefixFilter = "hTest4"; hTest3 is similar but not reducible due to unsaturated functions...
    TestHarness("HigherOrder", dumpGraph = true)
    //TestHarness("HigherOrder", dumpGraph = true, prefixFilter = "hTest4")
    //TestHarness("HigherOrder", dumpGraph = true, prefixFilter = "gTest0")
    //TestHarness("HigherOrder", dumpGraph = true, prefixFilter = "g")
    //TestHarness("HigherOrder", dumpGraph = true, prefixFilter = "lol")
    (
      check('hTest3)(-12),
      check('hTest4)(-12),
      check('hTest5)(0),
      check('gTest0)(3),
      check('gTest1)(6),
      check('iTest0, 1)(101),
    )
  )
  
  test("Church") (
    TestHarness("Church", dumpGraph = true)
    //TestHarness("Church", dumpGraph = true, prefixFilter = "one_id")
    //TestHarness("Church", dumpGraph = true, prefixFilter = "two")
    //TestHarness("Church", dumpGraph = true, prefixFilter = "one_id")
    //TestHarness("Church", dumpGraph = true, prefixFilter = "two_x_three")
    //TestHarness("Church", dumpGraph = true, prefixFilter = "zero_x_three")
    (
      check('three_id, 42)(42),
      check('two_p_three, 'S, 100)(105),
      check('two_x_three, 'S, 100)(106),
      check('zero_x_three, 'S, 100)(100),
    )
  )
  
  test("SimpleChurch") (
    //TestHarness("SimpleChurch")
    //TestHarness("SimpleChurch", prefixFilter = "test_1") // indirect recursion: scheduled program fails occurs check
    //TestHarness("SimpleChurch", prefixFilter = "test_2") // FIXME indirect recursion: propagator does not terminate
    //TestHarness("SimpleChurch", prefixFilter = "test_3") // same as above: does not terminate
    //
    //TestHarness("SimpleChurch", dumpGraph = true, prefixFilter = "_2I") // TODO reduce one-shots behind virtual nodes
    TestHarness("SimpleChurch", dumpGraph = true, prefixFilter = "_2")
    (
    )
  )
  
}
