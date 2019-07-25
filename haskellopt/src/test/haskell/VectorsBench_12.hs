-- Generated Haskell code from SynthBench

module Main where

import Criterion.Main

prod_12 :: Num a => [a] -> a
prod_12 [x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6 + x7 + x8 + x9 + x10 + x11
prod_12 [x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6 + x7 + x8 + x9 + x10
prod_12 [x0,x1,x2,x3,x4,x5,x6,x7,x8,x9] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6 + x7 + x8 + x9
prod_12 [x0,x1,x2,x3,x4,x5,x6,x7,x8] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6 + x7 + x8
prod_12 [x0,x1,x2,x3,x4,x5,x6,x7] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6 + x7
prod_12 [x0,x1,x2,x3,x4,x5,x6] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6
prod_12 [x0,x1,x2,x3,x4,x5] = 0 + x0 + x1 + x2 + x3 + x4 + x5
prod_12 [x0,x1,x2,x3,x4] = 0 + x0 + x1 + x2 + x3 + x4
prod_12 [x0,x1,x2,x3] = 0 + x0 + x1 + x2 + x3
prod_12 [x0,x1,x2] = 0 + x0 + x1 + x2
prod_12 [x0,x1] = 0 + x0 + x1
prod_12 [x0] = 0 + x0
prod_12 [] = 0
test_12 n = sum (map (\i -> prod_12 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11]) [0..n])

main = defaultMain [
    bench "12" $ whnf test_12 1000
  ]

