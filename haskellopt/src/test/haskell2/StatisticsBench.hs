-- `maxMaybe_manual` is MUCH faster than `maxMaybe`;
-- the graph can perform the optimization (thanks to case/ctor commuting);
-- but currently we generate code that's more convoluted
-- so it's not as fast as `maxMaybe_manual` (but still much faster than `maxMaybe`)

-- ghc -O2 StatisticsBench.hs && ./StatisticsBench
-- ghc -O2 StatisticsBench.pass-0000.opt.hs && ./StatisticsBench.pass-0000.opt

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

--- OPT ---

benchmarking maxMaybe/normal
time                 31.49 ms   (29.83 ms .. 32.86 ms)
                     0.992 R²   (0.984 R² .. 0.997 R²)
mean                 33.41 ms   (32.24 ms .. 35.69 ms)
std dev              3.297 ms   (1.718 ms .. 5.310 ms)
variance introduced by outliers: 40% (moderately inflated)

benchmarking maxMaybe/manual
time                 3.118 ms   (3.067 ms .. 3.175 ms)
                     0.996 R²   (0.993 R² .. 0.998 R²)
mean                 3.139 ms   (3.092 ms .. 3.191 ms)
std dev              157.8 μs   (125.0 μs .. 201.9 μs)
variance introduced by outliers: 31% (moderately inflated)

-}
