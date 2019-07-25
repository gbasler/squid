-- Generated Haskell code from SynthBench

module Main where

import Criterion.Main

prod_3 :: Num a => [a] -> a
prod_3 [x0,x1,x2] = 0 + x0 + x1 + x2
prod_3 [x0,x1] = 0 + x0 + x1
prod_3 [x0] = 0 + x0
prod_3 [] = 0
test_3 n = sum (map (\i -> prod_3 [i + 0, i + 1, i + 2]) [0..n])

main = defaultMain [
    bench "3" $ whnf test_3 1000
  ]

