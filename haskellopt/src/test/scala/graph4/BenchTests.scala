package graph4

import squid.utils._
import org.scalatest.FunSuite

class BenchTests extends FunSuite {
  object TestHarness extends TestHarness
  import CheckDSL.check
  
  test("InterpBench") (
    // TODO better scheduling to avoid code explosion
    TestHarness("InterpBench",
      //dumpGraph = true,
    )(
    )
  )
  
  test("InterpIntermediateBench") (
    // TOOD test
    //TestHarness("InterpIntermediate",
    //)(
    //)
  )
  
  test("InterpBasicBench") (
    // FIXME now graph diverges
    // TOOD alleviate scheduling code explosion
    //TestHarness("InterpBasic",
    //)(
    //)
  )
  
}
