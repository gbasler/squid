-- Generated Haskell code from SynthBench

module Main where

import Criterion.Main

prod_6 :: Num a => [a] -> a
prod_6 [x0,x1,x2,x3,x4,x5] = 0 + x0 + x1 + x2 + x3 + x4 + x5
prod_6 [x0,x1,x2,x3,x4] = 0 + x0 + x1 + x2 + x3 + x4
prod_6 [x0,x1,x2,x3] = 0 + x0 + x1 + x2 + x3
prod_6 [x0,x1,x2] = 0 + x0 + x1 + x2
prod_6 [x0,x1] = 0 + x0 + x1
prod_6 [x0] = 0 + x0
prod_6 [] = 0
test_6 n = sum (map (\i -> prod_6 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5]) [0..n])

main = defaultMain [
    bench "6" $ whnf test_6 1000
  ]

