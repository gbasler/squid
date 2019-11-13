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
    // FIXME bad comparison with crazy scope number: graph4.GraphDefs$BadComparison: <rec'303889>(ds'32:d↑[↓]) `;` β_2e = [from'13:8↑[↓]+1]ψ_30
    TestHarness("nofib-queens",
      schedule = false,
    )(
      // Note: these are useful but end up taking way too long when the graph gets complicated:
      //check('nsoln, 2)(0),
      //check('nsoln, 4)(2),
    )
  )
  
}
