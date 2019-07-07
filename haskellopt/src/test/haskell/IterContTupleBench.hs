module Main where

import Criterion.Main

-- -- not slower:
-- -- loop f state = rec state where
-- loop (f, state) = rec state where
--   rec s = f (rec, s)
--   -- rec s = f (\st -> rec st, s) -- same
-- similar to:
-- loop (f, state) = rec state where rec s = f rec s
loop f state = rec state where rec s = f rec s

-- whereas...
-- SLIGHTLY slower (for count):
-- loop (f, state) = f (\st -> loop(f, st), state)
-- similar for:
-- loop f state = f (\st -> loop f st) state
-- similar for:
-- loop f state = f (loop f) state


count :: Int -> Int

-- count n = loop (\(k, s) -> let x = k (s - 1) in if s > 0 then x + x + 1 else 0) n
-- count n = loop ((\(k, s) -> let x = k (s - 1) in if s > 0 then x + x + 1 else 0), n)
-- count n = loop (\k s -> let x = k (s - 1) in if s > 0 then x + x + 1 else 0) n

count n = loop (\k s -> if s > 0 then k (s - 1) + 1 else 0) n

count_manual :: Int -> Int
-- count_manual n = let x = count_manual (n - 1) in if n > 0 then x + x + 1 else 0

count_manual n = if n > 0 then count_manual (n - 1) + 1 else 0

get_2::(Int,Int)->Int
get_2 (a,b) = b


-- FIXME generates program with infinite loop!
count2 :: Int -> Int
-- count2 n = loop (\k (s0,s1) -> let (x0, x1) = k (s0 - 1, s1 + 1) in if s0 > 0 then x + x + 1 else 0) n
-- count2 n = get_2 (loop (\k (s0,s1) -> if s0 > 0 then let (x0, x1) = k (s0 - 1, s1 + 1) in x1 else (0,0)) (n,0))

-- count2 n = get_2 (loop (\k (s0,s1) -> if s0 > 0 then k (s0 - 1, s1 + 1) else (s0,s1)) (n,0))
count2 n = get_2 (loop (\k (s0,s1) -> if s0 > 0 then k (cus s0, suc s1) else (s0,s1)) (n,0)) where
  -- when made top-level, these causes 55x slowdown!, EVEN WHEN NOT MODULE-EXPORTED...
  suc n = n + (1::Int)
  cus n = n - (1::Int)

-- tail recursive, so ends up much faster (doesn't allocate!):
count2_manual :: Int -> Int
count2_manual n = rec n 0 where
  -- rec s0 s1 = if s0 > 0 then rec (s0 - 1) (s1 + 1) else s1
  rec s0 s1 = if s0 > 0 then rec (cus s0) (suc s1) else s1
  -- when made top-level, these causes 55x slowdown!, EVEN WHEN NOT MODULE-EXPORTED...
  suc n = n + (1::Int)
  cus n = n - (1::Int)


-- nats :: [Int]

-- nats = loop (\(k, s) -> s : k (s + 1)) 0
-- nats = loop ((\(k, s) -> s : k (s + 1)), 0)
-- nats = loop ((\(k, (s0,s1)) -> s0 : k (s1 + 1,s0)), (0,1))

-- nats = loop (\(k, (s0,s1)) -> s0 : k (s1 + 1,s0), (0,1))
-- -- nats = loop (\k (s0,s1) -> s0 : k (s1 + 1,s0)) (0,1)

-- -- nats = loop (\k s -> s : k (s + 1)) 0

-- -- sumnats n = sum (take (count n) nats)
-- sumnats n = sum (take n nats)

-- nats_ls = loop (\(k, [s0,s1]) -> s0 : k [s1 + 1,s0], [0,1])
-- sumnats_ls n = sum (take n nats_ls)

-- sumnats_manual n = sum (take n (rec 0 1)) where
--   rec s0 s1 = s0 : rec (s1 + 1) s0

-- sumnats_manual_ls n = sum (take n (rec [0, 1])) where
--   rec [s0, s1] = s0 : rec [s1 + 1, s0]

-- main = print (take 15 nats)
main = defaultMain [
  -- bgroup "sumnats" [
  --     bench "10"  $ whnf sumnats 10
  --   , bench "500"  $ whnf sumnats 500
  --   , bench "10000" $ whnf sumnats 10000
  --   ]
  -- bgroup "sumnats" [
  --     bench "tuple"  $ whnf sumnats 5000
  --   , bench "ls"  $ whnf sumnats_ls 5000
  --   , bench "manual" $ whnf sumnats_manual 5000
  --   , bench "manual_ls" $ whnf sumnats_manual_ls 5000
  --   ]
  -- bgroup "count" [
  --     bench "tuple"  $ whnf count 5000
  --   , bench "manual"  $ whnf count_manual 5000
  --   ]
  bgroup "count2" [
      bench "tuple"  $ whnf count2 50000
    , bench "manual"  $ whnf count2_manual 50000
    ]
  ]

