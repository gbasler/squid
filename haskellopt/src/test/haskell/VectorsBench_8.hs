-- Generated Haskell code from SynthBench

module Main where

import Criterion.Main

prod_8 :: Num a => [a] -> a
prod_8 [x0,x1,x2,x3,x4,x5,x6,x7] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6 + x7
prod_8 [x0,x1,x2,x3,x4,x5,x6] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6
prod_8 [x0,x1,x2,x3,x4,x5] = 0 + x0 + x1 + x2 + x3 + x4 + x5
prod_8 [x0,x1,x2,x3,x4] = 0 + x0 + x1 + x2 + x3 + x4
prod_8 [x0,x1,x2,x3] = 0 + x0 + x1 + x2 + x3
prod_8 [x0,x1,x2] = 0 + x0 + x1 + x2
prod_8 [x0,x1] = 0 + x0 + x1
prod_8 [x0] = 0 + x0
prod_8 [] = 0
test_8 n = sum (map (\i -> prod_8 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7]) [0..n])

main = defaultMain [
    bench "8" $ whnf test_8 1000
  ]

