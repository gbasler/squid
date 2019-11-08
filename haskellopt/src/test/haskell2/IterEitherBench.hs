-- GHC already optimizes `count` by inlining `loop`, so it runs just as fast as `count_manual`.

module Main where

import Criterion.Main

loop k x = go x where
  go x = case k x of
    Left a -> go a
    Right b -> b

count :: Int -> Int
count start = loop (\(rem,n) -> if rem > 0 then Left (rem - 1, n + 1) else Right n) (start, 0)

count_manual :: Int -> Int -> Int
count_manual cur rem = if rem > 0 then count_manual (cur + 1) (rem - 1) else cur

k = 1000*1000

main = defaultMain [
    bgroup "interp" [
        bench "normal"  $ whnf count k,
        bench "manual"  $ whnf (count_manual 0) k
    ]
  ]
