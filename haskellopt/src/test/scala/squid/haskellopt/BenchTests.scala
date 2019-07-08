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
    //override val sanityCheckFuel = 100000
    
    override def mkGraph = new HaskellGraphInterpreter with HaskellGraphScheduling2 {
      override val pp: ParameterPassingStrategy = CurriedParameters
    }
    
  }
  
  test("IterContBench") {
    TestHarness("IterContBench")
  }
  
  test("IterContTupleBench") {
    /*
    
    This benchmark is pretty disappointing: although it's quite intricate, GHC manages to optimize all the tuples and
    all the higher-order functions away on its own – as we can see after ONE pass of simplifier –, so we're only
    insignificantly faster (but that's likely because we unroll the loop once as a side effect of the scheduling
    algorithm's loop detection algorithm).
    Also, as of now it seems we're not getting rid of the tuple across recursive calls; not sure why.
    
    FIXME not true?
    
    Seems GHC has trouble optimizing the f-threading pattern:
      loop f state = f (loop f) state
    compared to:
      loop f state = rec state where rec s = f rec s
    but so do we, currently...
    
    mega-weird:
    count2_manual :: Int -> Int
    count2_manual n = rec n 0 where
      rec s0 s1 = if s0 > 0 then rec (cus s0) (suc s1) else s1
      -- when made top-level, these causes 55x slowdown!, EVEN WHEN NOT MODULE-EXPORTED...
      suc n = n + (1::Int)
      cus n = n - (1::Int)
    
    TODO investigate weird diffce in loop perf count2/count2_manual 
    
    */
    TestHarness("IterContTupleBench")
    // ^ Note: we got "MAX PATH SIZE REACHED!" when we had `val MaxPathSize = 18`
    // ^ Note: and now , with the addition of the manual versions, it seems to get bogged down in dead branches
  }
  
  test("RecTupleBench") {
    /*
    
    This is a very interesting case where returning a tuple instead of a single integer at the very end of a recursion
    causes a big difference in running time that scales linearly with the number of iterations! – I don't know why...
    
    However, to optimize that in the graph, we'd need some very smart logic which can detect that the second time we see
    the case-of-case opportunity, it's the same as the first time, and we can just rewire to the first reduction... thereby
    reconstructing a separate recursive graph.
    Such a recursion-aware capability may perhaps also be used for deforestation, as it seems similar to supercompilation.
    
    */
    TestHarness("RecTupleBench")
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
    
    //for (i <- 1 to 10)
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
  
  test("AlgebraicEffectsBench") {
    //TestHarness("AlgebraicEffectsBench") // FIXME assertion failure (with previous version; now just the unsupported case)
  }
  
  test("VectorsBench") {
    TestHarness("VectorsBench")
  }
  
  
  test("nofib-primes") {
    TestHarness("nofib-primes")
  }
  test("nofib-digits-of-e1") {
    // FIXME prints "MAX PATH SIZE REACHED!"
    // FIXME does not seem to terminate scheduling
    //TestHarness("nofib-digits-of-e1")
    //TestHarness("nofib-digits-of-e1", "0001"::Nil)
  }
  test("nofib-gen_regexps") {
    // TODO impl character literals
    //TestHarness("nofib-gen_regexps")
  }
  test("nofib-wheel-sieve1") {
    //TestHarness("nofib-wheel-sieve1") // Internal Error: Unimplemented case: code injected into recursive def
    //TestHarness("nofib-wheel-sieve1", "0001"::Nil) // FIXME doesn't seem to finish scheduling
  }
  test("nofib-integer") {
    /*
    This one actually is a bit faster! (5.466 ns vs 6.196 ns)
    But to make it compile, one has to annotate the _5 binding as:
      _5 :: Integer -> Integer -> Integer -> IO ()
    */
    TestHarness("nofib-integer", compileResult = false)
  }
  test("nofib-queens") {
    // ghc -O2 -fforce-recomp -ddump-to-file -ddump-prep nofib-queens.hs  && ./nofib-queens --regress allocated:iters +RTS -T -RTS
    // ghc -O2 -fforce-recomp -ddump-to-file -ddump-prep bench/nofib-queens.pass-0000.opt.hs  && bench/nofib-queens.pass-0000.opt  --regress allocated:iters +RTS -T -RTS
    TestHarness("nofib-queens", "0000"::Nil)
    //TestHarness("nofib-queens") // FIXME missing def _0
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






> time ghc -fforce-recomp -O2 ListFusionBench.hs
[1 of 1] Compiling Main             ( ListFusionBench.hs, ListFusionBench.o )
Linking ListFusionBench ...
        1.77 real         1.56 user         0.22 sys

time ghc -fforce-recomp -O2 bench/ListFusionBench.pass-0000.opt.hs
[1 of 1] Compiling Main             ( bench/ListFusionBench.pass-0000.opt.hs, bench/ListFusionBench.pass-0000.opt.o )
Linking bench/ListFusionBench.pass-0000.opt ...
        1.74 real         1.54 user         0.21 sys

> time ghc -fforce-recomp -O2 bench/ListFusionBench.pass-0001.opt.hs
[1 of 1] Compiling Main             ( bench/ListFusionBench.pass-0001.opt.hs, bench/ListFusionBench.pass-0001.opt.o )
Linking bench/ListFusionBench.pass-0001.opt ...
        1.88 real         1.65 user         0.23 sys





--- NOTES ---

Run criterion with allocations measurement:
  ghc -O3 ListFusionBench.hs && ./ListFusionBench --regress allocated:iters +RTS -T -RTS




*/

