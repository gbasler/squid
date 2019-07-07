module Main where

import Criterion.Main

-- main = undefined

-- {-# NOINLINE loop #-} -- does not seem to change anything...
loop f state = rec state where
  rec s = f rec s
  -- rec s = id ((id f) (\x -> rec (id x)) (id s)) -- does not change anything...

-- An explicit type signature will make the program much faster,
-- and will also currently erase the difference between graph-optimized and normal...

-- count :: Int -> Int
-- count n = loop (\k s -> if s > 0 then k (s - 1) + 1 else 0) n
count n = loop (\k s -> let x = k (s - 1) in if s > 0 then x + x + 1 else 0) n
-- count n = loop (\k s -> let x = k (s - 1) in if s > 0 then x + (count (n - 1)) + 1 else 0) n

-- nats :: [Int]
-- nats = loop (\k s -> s : k (s + 1)) 0
-- nats = loop (\k s -> s : k (s + 1)) 0
-- nats = loop (\k s -> s : k (s + 1) ++ nats) 0

nats = loop (\k s -> count s : k (s + 1) ++ nats) 0
-- ^ This kind of crazy stuff does not change anything; it's still as fast as our simplified version;
--   that's probably because `loop` is so small and trivial that it's immediately inlined and that's
--   enough to detangle the recursions...

-- sumnats n = sum (take (count n) nats)
sumnats n = sum (take n nats)

main = defaultMain [
    -- bgroup "sumnats" [
    --     bench "10"  $ whnf sumnats 10
    --   , bench "500"  $ whnf sumnats 500
    --   , bench "10000" $ whnf sumnats 10000
    -- ]
    bench "count"  $ whnf count 10000,
    -- bench "nats"  $ whnf (\n -> sum (take n nats)) 10000
    bench "nats"  $ whnf sumnats 10000
  ]

