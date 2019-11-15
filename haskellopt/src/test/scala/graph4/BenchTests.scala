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
    // TODO make it work with bigger `src` now that stack overflow is fixed
    TestHarness("InterpBasicBench",
    )(
    )
  )
  
  test("StatisticsBench") (
    // TODO try with an unboxed return tuple/unboxed argument tuple
    TestHarness("StatisticsBench",
    )(
      check('maxMaybe, List(1,3,2,0))(Some(3)),
    )
  )
  
  test("MaxPEBench") (
    // FIXME when `max_mod` is not explicitly typed: _0 = ($p1Real) ($p1Integral)
    TestHarness("MaxPEBench",
    )(
      //check('maxMaybe, List(1,3,2,0))(Some(3)),
    )
  )
  
  test("nofib-queens") (
    // FIXME Bad comparison with crazy scope number: graph4.GraphDefs$BadComparison: <rec'303889>(ds'32:d↑[↓]) `;` β_2e = [from'13:8↑[↓]+1]ψ_30
    // TODO Find a way to keep the scheduling process on track for programs like these...
    //      The graph is fine, but scheduling never seems to finish, even after some simplifications
    // Note: With UnrollingFactor == 0 we can manage to finish scheduling (more than 100 lines of generated code),
    //       but the program performs identically to the original.
    //       Similarly, if we remove all case commuting, nothing reduces and we get the same result.
    TestHarness("nofib-queens",
      schedule = false,
      dumpGraph = true,
      //prefixFilter = "nsoln",
    )(
      // Note: these are useful but end up taking way too long when the graph gets complicated:
      //check('nsoln, 2)(0),
      //check('nsoln, 4)(2),
    )
  )
  
  test("nofib-gen_regexps") (
    TestHarness("nofib-gen_regexps",
      schedule = false, // FIXME scheduling does not seem to terminate, even wehn we disable commuteCases... and even with UF = 0 in scheduler
      dumpGraph = true,
      //prefixFilter = "expand",
      //prefixFilter = "alphabeticRule",
    )(
    )
  )
  
  test("nofib-digits-of-e1") (
    // TODO alleviate code explosion
    TestHarness("nofib-digits-of-e1",
    )(
    )
  )
  
  
  // TODO nofib-primes with list fusion (but probably already done by GHC)
  
  
}
