-- Generated Haskell code from SynthBench

module Main where

import Criterion.Main

prod_9 :: Num a => [a] -> a
prod_9 [x0,x1,x2,x3,x4,x5,x6,x7,x8] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6 + x7 + x8
prod_9 [x0,x1,x2,x3,x4,x5,x6,x7] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6 + x7
prod_9 [x0,x1,x2,x3,x4,x5,x6] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6
prod_9 [x0,x1,x2,x3,x4,x5] = 0 + x0 + x1 + x2 + x3 + x4 + x5
prod_9 [x0,x1,x2,x3,x4] = 0 + x0 + x1 + x2 + x3 + x4
prod_9 [x0,x1,x2,x3] = 0 + x0 + x1 + x2 + x3
prod_9 [x0,x1,x2] = 0 + x0 + x1 + x2
prod_9 [x0,x1] = 0 + x0 + x1
prod_9 [x0] = 0 + x0
prod_9 [] = 0
test_9 n = sum (map (\i -> prod_9 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8]) [0..n])

main = defaultMain [
    bench "9" $ whnf test_9 1000
  ]

