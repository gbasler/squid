#!/usr/bin/env sh

SF=haskellopt/src/test/haskell/
GF=haskellopt_gen/bench/
OF=haskellopt_gen/csv/

Opt="-O2"

ghc $Opt $SF/ListFusionBench.hs \
  && rm -f $OF/ListFusionBench.csv \
  && $SF/ListFusionBench --regress allocated:iters +RTS -T -RTS --csv $OF/ListFusionBench.csv

ghc $Opt $GF/ListFusionBench.pass-0000.opt.hs \
  && rm -f $OF/ListFusionBench.pass-0000.opt.csv \
  && $GF/ListFusionBench.pass-0000.opt --regress allocated:iters +RTS -T -RTS --csv $OF/ListFusionBench.pass-0000.opt.csv

ghc $Opt -fno-strictness $SF/ListFusionBench.hs \
  && rm -f $OF/ListFusionBench.NS.csv \
  && $SF/ListFusionBench --regress allocated:iters +RTS -T -RTS --csv $OF/ListFusionBench.NS.csv

ghc $Opt -fno-strictness $GF/ListFusionBench.pass-0000.opt.hs \
  && rm -f $OF/ListFusionBench.pass-0000.opt.NS.csv \
  && $GF/ListFusionBench.pass-0000.opt --regress allocated:iters +RTS -T -RTS --csv $OF/ListFusionBench.pass-0000.opt.NS.csv
