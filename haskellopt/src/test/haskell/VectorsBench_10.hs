-- Generated Haskell code from SynthBench

module Main where

import Criterion.Main

prod_10 :: Num a => [a] -> a
prod_10 [x0,x1,x2,x3,x4,x5,x6,x7,x8,x9] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6 + x7 + x8 + x9
prod_10 [x0,x1,x2,x3,x4,x5,x6,x7,x8] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6 + x7 + x8
prod_10 [x0,x1,x2,x3,x4,x5,x6,x7] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6 + x7
prod_10 [x0,x1,x2,x3,x4,x5,x6] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6
prod_10 [x0,x1,x2,x3,x4,x5] = 0 + x0 + x1 + x2 + x3 + x4 + x5
prod_10 [x0,x1,x2,x3,x4] = 0 + x0 + x1 + x2 + x3 + x4
prod_10 [x0,x1,x2,x3] = 0 + x0 + x1 + x2 + x3
prod_10 [x0,x1,x2] = 0 + x0 + x1 + x2
prod_10 [x0,x1] = 0 + x0 + x1
prod_10 [x0] = 0 + x0
prod_10 [] = 0
test_10 n = sum (map (\i -> prod_10 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9]) [0..n])

main = defaultMain [
    bench "10" $ whnf test_10 1000
  ]

