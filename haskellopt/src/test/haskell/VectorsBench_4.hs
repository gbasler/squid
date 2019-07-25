-- Generated Haskell code from SynthBench

module Main where

import Criterion.Main

prod_4 :: Num a => [a] -> a
prod_4 [x0,x1,x2,x3] = 0 + x0 + x1 + x2 + x3
prod_4 [x0,x1,x2] = 0 + x0 + x1 + x2
prod_4 [x0,x1] = 0 + x0 + x1
prod_4 [x0] = 0 + x0
prod_4 [] = 0
test_4 n = sum (map (\i -> prod_4 [i + 0, i + 1, i + 2, i + 3]) [0..n])

main = defaultMain [
    bench "4" $ whnf test_4 1000
  ]

