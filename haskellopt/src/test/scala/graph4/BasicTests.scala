package graph4

import squid.utils._
import org.scalatest.FunSuite

class BasicTests extends FunSuite {
  object TestHarness extends TestHarness
  import CheckDSL.check
  
  // TODO find a way to test the checks on the GHC-compiled programs! — e.g., generate an executable Main module
  
  test("Basics") (
    TestHarness("Basics", dumpGraph = true)
    //TestHarness("Basics", dumpGraph = true, prefixFilter = "f")
    //TestHarness("Basics", dumpGraph = true, prefixFilter = "fTest0")
    //TestHarness("Basics", dumpGraph = true, prefixFilter = "foo_3")
    //TestHarness("Basics", dumpGraph = true, prefixFilter = "gTest")
    //TestHarness("Basics", dumpGraph = true, prefixFilter = "gTest0")
    //TestHarness("Basics", dumpGraph = true, prefixFilter = "fTest")
    (
      check('gTest0)(24),
      check('gTest2, 4)(24),
      check('foo_3, 2)(46400),
    )
  )
  
  test("BasicRec") (
    TestHarness("BasicRec",
      //prefixFilter = "nrec_0",
      //prefixFilter = "nrec_capt_0",
      //
      dumpGraph = true,
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
  
  test("HigherOrderRec") (
    // Note: propag reaches maximum depth (since I added ctor paths)
    // FIXME graphs of r_1 and others diverge (create too many paths, which explode with beta reduction)
    //  We need a way to detect dumb cycles and just not register paths in them... (or only a small number)
    TestHarness("HigherOrderRec",
      //prefixFilter = "r",
      //
      dumpGraph = true,
    )(
      check('r_2)(List(1,1,1)),
      // TODO more checks
    )
  )
  
  test("HigherOrderRecPoly") (
    // Most of the tests below (such as only_p1) generate code where occurs-check fails
    // (it is polymorphically-recursive, which requires type annotations, which we do not generate...).
    TestHarness("HigherOrderRecPoly",
      //prefixFilter = "only_p1",
      //
      dumpGraph = true,
      schedule = false,
    )(
      // TODO checks
    )
  )
  
  test("HigherOrderRecLocal") (
    // FIXME foo and foo_0 produce indirectly-recursive values leading to ordering problems
    // FIXME scheduling of commented foo_2 diverges
    TestHarness("HigherOrderRecLocal",
      prefixFilter = "foo_",
      dumpGraph = true,
    )(
      // TODO checks
      //  check: take 10 (rec2_5 42) == [42,43,44,45,46,47,48,49,50,51] 
    )
  )
  
  test("Church") (
    TestHarness("Church", dumpGraph = true)
    //TestHarness("Church", dumpGraph = true, prefixFilter = "one_id")
    //TestHarness("Church", dumpGraph = true, prefixFilter = "two_")
    //TestHarness("Church", dumpGraph = true, prefixFilter = "one_id")
    //TestHarness("Church", dumpGraph = true, prefixFilter = "two_p_three")
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
    //TestHarness("SimpleChurch", dumpGraph = true, prefixFilter = "_2I ") // TODO reduce one-shots behind virtual nodes
    //TestHarness("SimpleChurch", dumpGraph = true, prefixFilter = "_2II ")
    TestHarness("SimpleChurch", dumpGraph = true, prefixFilter = "_2")
    (
    )
  )
  
  test("IterCont") (
    // FIXME graph of nats0 diverges
    TestHarness("IterCont",
      dumpGraph = true,
    )(
      // TODO checks
      //  check: take 5 nats1 == [0,1,2,3,4]
    )
  )
  
  test("IterContLocal") (
    // FIXME graph of nats0 diverges
    TestHarness("IterContLocal",
      dumpGraph = true,
    )(
      // TODO checks
      //  check: take 5 nats1 == [0,1,2,3,4]
    )
  )
  
  // TODO IterContMaybe
  
}
