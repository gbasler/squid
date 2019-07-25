-- Generated Haskell code from SynthBench

module Main where

import Criterion.Main

prod_1 :: Num a => [a] -> a
prod_1 [x0] = 0 + x0
prod_1 [] = 0
test_1 n = sum (map (\i -> prod_1 [i + 0]) [0..n])

main = defaultMain [
    bench "1" $ whnf test_1 1000
  ]

