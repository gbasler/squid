-- Generated Haskell code from SynthBench

module Main where

import Criterion.Main

prod_5 :: Num a => [a] -> a
prod_5 [x0,x1,x2,x3,x4] = 0 + x0 + x1 + x2 + x3 + x4
prod_5 [x0,x1,x2,x3] = 0 + x0 + x1 + x2 + x3
prod_5 [x0,x1,x2] = 0 + x0 + x1 + x2
prod_5 [x0,x1] = 0 + x0 + x1
prod_5 [x0] = 0 + x0
prod_5 [] = 0
test_5 n = sum (map (\i -> prod_5 [i + 0, i + 1, i + 2, i + 3, i + 4]) [0..n])

main = defaultMain [
    bench "5" $ whnf test_5 1000
  ]

