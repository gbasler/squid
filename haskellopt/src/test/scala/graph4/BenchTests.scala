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
    // TODO test
    // FIXME graph diverges
    //TestHarness("InterpIntermediateBench",
    //)(
    //)
  )
  
  test("InterpBasicBench") (
    // TODO make it work with bigger `src` by fixing stack overflow
    TestHarness("InterpBasicBench",
    )(
    )
  )
  
}
