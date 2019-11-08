-- `maxMaybe_manual` is MUCH faster;
-- unfortunately, we can't get it from basic graph rewriting technology...
-- But we can probably still gain some perf via unrolling by a fixed factor.

module Main where

import Criterion.Main

maxMaybe :: [Int] -> Maybe Int
maxMaybe [] = Nothing
maxMaybe (x : xs) = case maxMaybe xs of
  Just m -> Just (if x > m then x else m)
  Nothing -> Just x

maxMaybe_manual :: [Int] -> Maybe Int
maxMaybe_manual [] = Nothing
maxMaybe_manual (x : xs) = Just (go x xs) where
  go x [] = x
  go x (y : ys) = go (if y > x then y else x) ys

k = [0..1000*1000]

main = defaultMain [
    bgroup "maxMaybe" [
        bench "normal"  $ nf maxMaybe k,
        bench "manual"  $ nf maxMaybe_manual k
    ]
  ]

{-

benchmarking maxMaybe/normal
time                 142.7 ms   (140.1 ms .. 144.4 ms)
                     1.000 R²   (0.999 R² .. 1.000 R²)
mean                 136.0 ms   (129.3 ms .. 138.8 ms)
std dev              6.176 ms   (1.640 ms .. 9.295 ms)
variance introduced by outliers: 12% (moderately inflated)

benchmarking maxMaybe/manual
time                 3.058 ms   (3.003 ms .. 3.124 ms)
                     0.993 R²   (0.987 R² .. 0.997 R²)
mean                 3.099 ms   (3.033 ms .. 3.180 ms)
std dev              236.1 μs   (177.1 μs .. 355.1 μs)
variance introduced by outliers: 52% (severely inflated)

-}
