package graph4

import squid.utils._
import org.scalatest.FunSuite

class BasicTests extends FunSuite {
  object TestHarness extends TestHarness
  import CheckDSL._
  
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
      //prefixFilter = "trec_0",
      //prefixFilter = "nrec_capt_0",
      //prefixFilter = "alternateTF",
      //prefixFilter = "alternateZO_1'0",
      //prefixFilter = "alternate123_2'0",
      //prefixFilter = "alternate123_0 ",
      //prefixFilter = "alternateZO_0 ",
      //prefixFilter = "alternate123_2 ",
      //prefixFilter = "alternate123_3 ",
      //prefixFilter = "alternate123_3'0",
      //
      dumpGraph = true,
    )
    (
      check("alternateTF'0")(List(true,false,true,false,true)),
      check("alternateZO_0'0")(List(0,1,0,1,0)),
      check("alternateZO_1'0")(List(0,1,0,1,0)),
      check("alternate123_0'0")(List(1,2,3,1,2,3,1,2,3,1)),
      check("alternate123_1'0")(List(1,2,3,1,2,3,1,2,3,1)),
      check("alternate123_2'0")(List(1,2,3,1,2,3,1,2,3,1)),
      check("alternate123_3'0")(List(1,2,3,1,2,3,1,2,3,1)),
    )
  )
  
  test("HigherOrder") (
    // Simplest example: run with prefixFilter = "hTest4"; hTest3 is similar but not reducible due to unsaturated functions...
    TestHarness("HigherOrder",
      dumpGraph = true,
      //prefixFilter = "hTest4",
      //prefixFilter = "gTest0",
      //prefixFilter = "g",
      //prefixFilter = "lol",
    )
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
      //prefixFilter = "only_q",
      //
      dumpGraph = true,
    )(
      check('r_2)(List(1,1,1)),
      check('q_1_0, 11, 22)(List(12,24,13,25,14,26,15,27)),
    )
  )
  
  test("HigherOrderRecPoly") (
    // Most of the tests below (such as only_p1) USED TO generate code where occurs-check fails
    // (it was polymorphically-recursive, which requires type annotations, which we do not generate...).
    TestHarness("HigherOrderRecPoly",
      //prefixFilter = "only_p1",
      //
      //dumpGraph = true,
      //schedule = false,
    )(
      check('p1_6_5, 12)(List(12,14,16,18,20))
    )
  )
  
  test("HigherOrderRecLocal") (
    // TODO counteract scheduled code regression since <this commit>, due to unrolling up to UnrollingFactor
    // FIXME scheduling of commented foo_2 diverges
    TestHarness("HigherOrderRecLocal",
      //prefixFilter = "foo_",
      dumpGraph = true,
    )(
      check('foo_1, 23)(23),
      check('foo_5_10)(List(23,24,25,26,27,28,29,30,31,32)),
      check('foo_6_5, 23)(List(23,46,47,94,95)),
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
    // FIXME scheduling of nats0_5 is wrong when we enable disregardUF
    TestHarness("IterCont",
      //prefixFilter = "nats0",
      dumpGraph = true,
    )(
      check('nats0_5)(List(0,1,2,3,4)),
      check('nats1_5)(List(0,1,2,3,4)),
    )
  )
  
  test("IterContLocal") (
    // FIXME graph of nats0 diverges
    TestHarness("IterContLocal",
      dumpGraph = true,
    )(
      check('nats1_5)(List(0,1,2,3,4)),
    )
  )
  
}
