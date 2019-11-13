package graph4

import squid.utils._
import org.scalatest.FunSuite

class BenchTests extends FunSuite {
  object TestHarness extends TestHarness
  import CheckDSL.check
  
  test("InterpBench") (
    // TODO better scheduling to avoid code explosion
    // FIXME now graph diverges
    //TestHarness("InterpBench",
    //  //dumpGraph = true,
    //)(
    //)
  )
  
  test("InterpIntermediateBench") (
    // Note: periodically make sure result is the same by uncommenting `print`...
    TestHarness("InterpIntermediateBench",
    )(
    )
  )
  
  test("InterpBasicBench") (
    // TODO make it work with bigger `src` by fixing stack overflow
    TestHarness("InterpBasicBench",
    )(
    )
  )
  
  test("StatisticsBench") (
    TestHarness("StatisticsBench",
    )(
      check('maxMaybe, List(1,3,2,0))(Some(3)),
    )
  )
  
  test("nofib-queens") (
    // FIXME graph diverges
    //TestHarness("nofib-queens",
    //)(
    //  check('nsoln, 2)(0),
    //  check('nsoln, 4)(2),
    //)
  )
  
}
