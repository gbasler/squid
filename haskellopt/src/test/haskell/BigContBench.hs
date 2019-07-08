module Main where

import Criterion.Main

-- values = [0..1234]


k [] = 0
k [x0] = x0
k [x0,x1] = x0+x1
k [x0,x1,x2] = x0+x1+x2
k [x0,x1,x2,x3,x4] = x0+x1+x2+x3+x4
-- k [x0,x1,x2,x3,x4,x5] = x0+x1+x2+x3+x4+x5
-- k [x0,x1,x2,x3,x4,x5] = (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 -- not inlined
-- k [x0,x1,x2,x3,x4,x5] = (x0-1+x1-1+x2-1+x3+x4)*x5 -- no inline
-- k [x0,x1,x2,x3,x4,x5] = (x0-1+x1-1+x2+x3+x4)*x5 -- no inline
k [x0,x1,x2,x3,x4,x5] = (x0-1+x1+x2+x3+x4)*x5 -- no inline
-- k [x0,x1,x2,x3,x4,x5] = (x0+x1+x2+x3+x4)*x5 -- inline!!!

-- k (x : xs) = x + k xs

normal :: Int -> Int
-- normal n = map (\x -> k [x,x+1,x+2,x+1,x,0] + n) values
normal n = sum (map (\x -> k [x,x+1,x+2,x+1,x,34] + n) [0..n])

manual :: Int -> Int
-- manual n = sum (map (\x -> (sum [x,x+1,x+2,x+1,x,34]) + n) [0..n])
manual n = sum (map (\x -> (case [x,x+1,x+2,x+1,x,34] of [x0,x1,x2,x3,x4,x5] -> (x0-1+x1+x2+x3+x4)*x5) + n) [0..n])
-- manual n = sum (map (\x -> n) [0..n])

-- main = print (normal 42, manual 42)

main = defaultMain [
  bgroup "count2" [
      bench "normal" $ whnf normal 100
    , bench "manual" $ whnf manual 100
    , bench "normal" $ whnf normal 1000
    , bench "manual" $ whnf manual 1000
    ]
  ]



--- OLDer ---

-- -- Note: does inline and simplify if we use a tuple instead
-- -- Note: does inline and simplify if we use only two list components instead
-- k [x0,x1,x2,x3,x4,x5] =
--   (x0-1+x1-1+x2-1+x3-1+x4-1)*x5

-- normal :: Int -> Int
-- -- normal n = map (\x -> k [x,x+1,x+2,x+1,x,0] + n) values
-- normal n = sum (map (\x -> k [x,x+1,x+2,x+1,x,0] + n) [0..n])

-- manual :: Int -> Int
-- manual n = sum (map (\x -> (case [x,x+1,x+2,x+1,x,0] of {[x0,x1,x2,x3,x4,x5] -> (x0-1+x1-1+x2-1+x3-1+x4-1)*x5}) + n) [0..n])
-- -- manual n = sum (map (\x -> n) [0..n])

-- -- main = print (normal 42, manual 42)

-- main = defaultMain [
--   bgroup "count2" [
--       bench "normal" $ whnf normal 100
--     , bench "manual" $ whnf manual 100
--     , bench "normal" $ whnf normal 1000
--     , bench "manual" $ whnf manual 1000
--     ]
--   ]





-- GHC inlines A LOT..?! --


-- k :: (Int,Int,Int,Int,Int,Int) -> Int
-- k (x0,x1,x2,x3,x4,x5) =
--   (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 + (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 + (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 + (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 + (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 + (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 + (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 + (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 + (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 + (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 + (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 + (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 + (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 + (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 + (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 + (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 + (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 + (x0-1+x1-1+x2-1+x3-1+x4-1)*x5 + (x0-1+x1-1+x2-1+x3-1+x4-1)*x5

-- normal :: Int -> Int
-- -- normal n = map (\x -> k (x,x+1,x+2,x+1,x,0) + n) values
-- -- normal n = sum (map (\x -> k (x,x+1,x+2,x+1,x,0) + n) [0..n])
-- normal n = rec k n [0..n]
-- rec :: ((Int,Int,Int,Int,Int,Int) -> Int) -> Int -> [Int] -> Int
-- rec k n [] = 0
-- rec k n (x : xs) = rec k n xs + k (x,x+1,x+2,x+1,x,0) + k (x,x+1,x+2,x+1,x,0) + k (x,x+1,x+2,x+1,x,0) + k (x,x+1,x+2,x+1,x,0) + n


-- manual :: Int -> Int

-- -- manual n = sum (map (\x -> (case (x,x+1,x+2,x+1,x,0) of {(x0,x1,x2,x3,x4,x5) -> (x0-1+x1-1+x2-1+x3-1+x4-1)*x5}) + n) [0..n])
-- -- -- manual n = sum (map (\x -> n) [0..n])
-- manual n = rec' n [0..n]
-- rec' :: Int -> [Int] -> Int
-- rec' n [] = 0
-- -- rec' n (x : xs) = rec' n xs + (case (x,x+1,x+2,x+1,x,0) of {(x0,x1,x2,x3,x4,x5) -> (x0-1+x1-1+x2-1+x3-1+x4-1)*x5}) + n
-- rec' n (x : xs) = rec' n xs + n


