#!/usr/bin/env sh

SF=haskellopt/src/test/haskell/
GF=haskellopt_gen/bench/
OF=haskellopt_gen/csv/

Opt="-O2" && OptStr=""
# Opt="-O1" && OptStr=".O1"
# Opt="-O0" && OptStr=".NO"

ghc $Opt $SF/ListFusionBench.hs \
  && rm -f $OF/ListFusionBench$OptStr.csv \
  && $SF/ListFusionBench --regress allocated:iters +RTS -T -RTS --csv $OF/ListFusionBench$OptStr.csv

ghc $Opt $GF/ListFusionBench.pass-0000.opt.hs \
  && rm -f $OF/ListFusionBench.pass-0000.opt.csv \
  && $GF/ListFusionBench.pass-0000.opt --regress allocated:iters +RTS -T -RTS --csv $OF/ListFusionBench.pass-0000.opt.csv

ghc $Opt -fno-strictness $SF/ListFusionBench.hs \
  && rm -f $OF/ListFusionBench$OptStr.NS.csv \
  && $SF/ListFusionBench --regress allocated:iters +RTS -T -RTS --csv $OF/ListFusionBench$OptStr.NS.csv

ghc $Opt -fno-strictness $GF/ListFusionBench.pass-0000.opt.hs \
  && rm -f $OF/ListFusionBench.pass-0000.opt$OptStr.NS.csv \
  && $GF/ListFusionBench.pass-0000.opt --regress allocated:iters +RTS -T -RTS --csv $OF/ListFusionBench.pass-0000.opt$OptStr.NS.csv
