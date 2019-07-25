-- Generated Haskell code from SynthBench

module Main where

import Criterion.Main

prod_7 :: Num a => [a] -> a
prod_7 [x0,x1,x2,x3,x4,x5,x6] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6
prod_7 [x0,x1,x2,x3,x4,x5] = 0 + x0 + x1 + x2 + x3 + x4 + x5
prod_7 [x0,x1,x2,x3,x4] = 0 + x0 + x1 + x2 + x3 + x4
prod_7 [x0,x1,x2,x3] = 0 + x0 + x1 + x2 + x3
prod_7 [x0,x1,x2] = 0 + x0 + x1 + x2
prod_7 [x0,x1] = 0 + x0 + x1
prod_7 [x0] = 0 + x0
prod_7 [] = 0
test_7 n = sum (map (\i -> prod_7 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6]) [0..n])

main = defaultMain [
    bench "7" $ whnf test_7 1000
  ]

