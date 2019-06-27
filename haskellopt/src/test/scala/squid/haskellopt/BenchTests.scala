package squid.haskellopt

import squid.utils._
import org.scalatest.FunSuite

class BenchTests extends FunSuite {
  object TestHarness extends TestHarness {
    import ammonite.ops._
    override val genFolder = pwd/'haskellopt_gen/'bench
  }
  
  test("IterContBench") {
    TestHarness("IterContBench")
  }
  
  test("IterContLocalBench") {
    TestHarness("IterContLocalBench")
  }
  
  
}

/*

Currently slightly better times with:

ghc -O3 IterContBench.hs && ./IterContBench
  benchmarking sumnats/10000
  time                 204.4 μs   (202.7 μs .. 206.2 μs)
                       1.000 R²   (0.999 R² .. 1.000 R²)
  mean                 204.0 μs   (203.1 μs .. 205.4 μs)
  std dev              3.781 μs   (2.877 μs .. 5.541 μs)
  variance introduced by outliers: 11% (moderately inflated)

ghc -O3 IterContBench.pass-0000.opt.hs && ./IterContBench.pass-0000.opt
  time                 182.8 μs   (182.1 μs .. 183.8 μs)
                       1.000 R²   (1.000 R² .. 1.000 R²)
  mean                 182.9 μs   (182.4 μs .. 184.0 μs)
  std dev              2.602 μs   (1.704 μs .. 4.164 μs)

*/
