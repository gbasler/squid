package squid.haskellopt

import squid.utils._
import org.scalatest.FunSuite
import squid.ir.graph3.CurriedParameters
import squid.ir.graph3.HaskellGraphScheduling2
import squid.ir.graph3.ParameterPassingStrategy

class BenchTests extends FunSuite {
  object TestHarness extends TestHarness {
    import ammonite.ops._
    override val genFolder = pwd/'haskellopt_gen/'bench
    override val sanityCheckFuel = 5
    
    override def mkGraph = new HaskellGraphInterpreter with HaskellGraphScheduling2 {
      override val pp: ParameterPassingStrategy = CurriedParameters
    }
    
  }
  
  test("IterContBench") {
    TestHarness("IterContBench")
  }
  
  test("IterContLocalBench") {
    TestHarness("IterContLocalBench")
  }
  
  test("ListFusionBench") {
    /*
    
    This benchmark keeps getting weirder. After implementing fusion in the graph, we get uniform times for all benchmarks,
    but we do not get optimal perf (as that of the original version of `sumnatsLocal`) unless we refrain from expanding
    `sum` into a `foldr`. It seems GHC does something _better_ for `sum` and I don't know what it is, but looking at the
    Core dump, it produces a very tight, strict loop, whereas when we fuse `sum` normally into a `foldr`, GHC generates
    a less efficient loop.
    I can't get a sense of what's happening, as `sum` seems to be implemented in GHC as a `foldl` and `foldl` itself is
    implemented as a `foldr`! (see https://hackage.haskell.org/package/base-4.12.0.0/docs/src/GHC.List.html)
    
    Note: Currently, `sum` in `sumnatsLocalTupled` is never fused in the graph because our `foldr` pattern currently only
          matches it applied to exactly 3 args (and here the 3rd arg is given across function boundary); but as the times
          show, it's applied later by GHC anyways.
    
    Note: used to be that when we used tuple parameters, our output was twice as slow... as it probably got in the way of GHC fusion
    
    */
    
    //TestHarness("ListFusionBench")
    
    TestHarness("ListFusionBench", "0000"::Nil)
    
    /*
    
    Interesting things about this benchmark:
    
    - It shows an instance where we would duplicate work if we did not perform local CSE
    
    - It shows that using tupled parameters can seriously harm performances (probably due to hampered fusion opportunities)
      See manual exposition in haskellopt_gen/bench-doc/ListFusionBench.pass-0000.opt.hs.
    
    - It shows that using top-level defs instead of local ones can woefully harm perfs, likely for the same reason.
    
    - Last time I tried, with the old GIR impl, it seemed like GHC did not completely fuse its lists, and that we could
      do a better job; however, it now seems to do fuse completely for the local version.
    
    - Super strangely, when I changed criterion's `nf` to `whnf` and used an addition instead of a pair, the performance
      of the toplvl version of the benchmark (and only this version) more than doubled in running time!
      This is probably because `nf` is aggressively desugared in terms of DeepSeq stuff (which is also why I switched to
      not using it).
    
    */
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

Similarly with the nonlinear version

benchmarking sumnats/10000
time                 136.3 μs   (136.0 μs .. 136.8 μs)
                     1.000 R²   (1.000 R² .. 1.000 R²)
mean                 136.7 μs   (136.3 μs .. 137.2 μs)
std dev              1.461 μs   (1.059 μs .. 2.135 μs)

benchmarking sumnats/10000
time                 121.6 μs   (121.2 μs .. 122.1 μs)
                     1.000 R²   (1.000 R² .. 1.000 R²)
mean                 121.9 μs   (121.5 μs .. 122.4 μs)
std dev              1.511 μs   (1.155 μs .. 2.012 μs)

TODO Q: Is this because of an instance resolution defaulting to Int instead of Integer, or some other sort of trickery?
        In fact, the rewritten program does have the same type
        Yet when I add explicit Int types, the two versions have the same speed (and are much faster)
        So it seems the gain was mainly in somehow convincing GHC to do more specialization for our version



--- NOTES ---

Run criterion with allocations measurement:
  ghc -O3 ListFusionBench.hs && ./ListFusionBench --regress allocated:iters +RTS -T -RTS




*/

