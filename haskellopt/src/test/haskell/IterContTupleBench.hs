module Main where

import Criterion.Main

---- not slower:
-- loop f state = rec state where
--   rec s = f (rec, s)
--   -- rec s = f (\st -> rec st, s) -- same

-- slower:
loop (f, state) = f (\st -> loop(f, st), state)
-- -- compared to:
-- loop f state = f (\st -> loop f st) state


-- count :: Int -> Int

-- count n = loop (\(k, s) -> let x = k (s - 1) in if s > 0 then x + x + 1 else 0) n
count n = loop ((\(k, s) -> let x = k (s - 1) in if s > 0 then x + x + 1 else 0), n)
-- count n = loop (\k s -> let x = k (s - 1) in if s > 0 then x + x + 1 else 0) n

-- nats :: [Int]

-- nats = loop (\(k, s) -> s : k (s + 1)) 0
-- nats = loop ((\(k, s) -> s : k (s + 1)), 0)
nats = loop ((\(k, (s0,s1)) -> s0 : k (s1 + 1,s0)), (0,1))
-- nats = loop (\k s -> s : k (s + 1)) 0

-- sumnats n = sum (take (count n) nats)
sumnats n = sum (take n nats)

-- main = print (take 15 nats)
main = defaultMain [
  bgroup "sumnats" [
      bench "10"  $ whnf sumnats 10
    , bench "500"  $ whnf sumnats 500
    , bench "10000" $ whnf sumnats 10000
  ]]

