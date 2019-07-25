-- Generated Haskell code from SynthBench

module Main where

import Criterion.Main

prod_2 :: Num a => [a] -> a
prod_2 [x0,x1] = 0 + x0 + x1
prod_2 [x0] = 0 + x0
prod_2 [] = 0
test_2 n = sum (map (\i -> prod_2 [i + 0, i + 1]) [0..n])

main = defaultMain [
    bench "2" $ whnf test_2 1000
  ]

