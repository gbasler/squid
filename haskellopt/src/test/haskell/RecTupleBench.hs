module Main where

import Criterion.Main

count2 :: Int -> Int
count2 n = rec n 0 where
  -- rec s0 s1 = if s0 > 0 then rec (s0 - 1) (s1 + 1) else s1
  rec s0 s1 = if s0 > 0 then rec (cus s0) (suc s1) else s1
  -- when made top-level, these causes 55x slowdown!, EVEN WHEN NOT MODULE-EXPORTED...
  suc n = n + (1::Int)
  cus n = n - (1::Int)

count2_tup :: Int -> Int
count2_tup n = res where
  (_,res) = rec n 0
  rec s0 s1 = if s0 > 0 then rec (cus s0) (suc s1) else (s0,s1)
  -- when made top-level, these causes 55x slowdown!, EVEN WHEN NOT MODULE-EXPORTED...
  suc n = n + (1::Int)
  cus n = n - (1::Int)

main = defaultMain [
  bgroup "count2" [
      bench "normal"  $ whnf count2 10000
    , bench "tuple"  $ whnf count2_tup 10000
    , bench "normal"  $ whnf count2 50000
    , bench "tuple"  $ whnf count2_tup 50000
    ]
  ]
