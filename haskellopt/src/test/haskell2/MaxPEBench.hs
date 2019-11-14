-- ghc -O2 MaxPEBench.hs && ./MaxPEBench
-- ghc -O2 MaxPEBench.pass-0000.opt.hs && ./MaxPEBench.pass-0000.opt

module Main where

import Criterion.Main

maxMaybe :: [Int] -> Maybe Int
maxMaybe [] = Nothing
maxMaybe (x : xs) = case maxMaybe xs of
  Just m -> Just (if x > m then x else m)
  Nothing -> Just x

max3 :: Int -> Int -> Int -> Int
max3 x y z = let Just res = maxMaybe [x, y, z] in res

max3_manual :: Int -> Int -> Int -> Int
max3_manual x y z = if x > y then if x > z then x else z else if y > z then y else z

k :: [Int]
k = [0..1000*1000]

max_mod :: (Int -> Int -> Int -> Int) -> [Int] -> [Int]
max_mod m = map (\v -> m (v `mod` 10) v (v `mod` 5))

main = defaultMain [
    bgroup "maxMaybe" [
        bench "normal"  $ nf (max_mod max3) k,
        bench "manual"  $ nf (max_mod max3_manual) k
    ]
  ]

{-

benchmarking maxMaybe/normal
time                 51.48 ms   (50.65 ms .. 52.13 ms)
                     0.999 R²   (0.998 R² .. 1.000 R²)
mean                 52.49 ms   (52.07 ms .. 53.19 ms)
std dev              1.039 ms   (644.7 μs .. 1.613 ms)

benchmarking maxMaybe/manual
time                 29.00 ms   (28.85 ms .. 29.14 ms)
                     1.000 R²   (1.000 R² .. 1.000 R²)
mean                 29.26 ms   (29.12 ms .. 29.53 ms)
std dev              396.4 μs   (248.4 μs .. 635.7 μs)

--- OPT ---

benchmarking maxMaybe/normal
time                 29.23 ms   (28.94 ms .. 29.43 ms)
                     1.000 R²   (1.000 R² .. 1.000 R²)
mean                 29.16 ms   (29.08 ms .. 29.26 ms)
std dev              191.4 μs   (144.2 μs .. 275.0 μs)

benchmarking maxMaybe/manual
time                 28.69 ms   (28.43 ms .. 29.00 ms)
                     1.000 R²   (0.999 R² .. 1.000 R²)
mean                 28.87 ms   (28.69 ms .. 29.11 ms)
std dev              475.1 μs   (327.8 μs .. 653.9 μs)

-}
