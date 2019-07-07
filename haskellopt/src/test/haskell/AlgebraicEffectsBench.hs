module Main where

import Criterion.Main

-- game :: (Int -> [Int]) -> ((Int -> [Int]) -> [Int] -> [Int]) -> ((Bool -> [Int]) -> [Int]) -> [Int] -> [Int]
game lift bind flipCoin input = play input where
  play [] = lift 0
  play (x : xs) = flipCoin (\res ->
      -- let rest = play xs in
      -- if res then rest + x else rest
      bind (\rest ->
          -- if res then rest + x else rest
          lift (if res then rest + x else rest)
        ) (play xs)
    )

-- -- game_nondet = game (:[]) concatMap (\f -> [f True, f False])
-- game_nondet = game (:[]) (\f ls -> concatMap f ls) (\f -> [f True, f False])

-- NOT slower than manual:
-- game_nondet = game (:[]) concatMap (\f -> f True ++ f False)
-- ACTUALLY slower than manual:
game_nondet = game (\x -> drop 1 ([x,x])) concatMap (\f -> f True ++ f False)
-- game_nondet' = game (\x -> take 1 ([x,x])) concatMap (\f -> f False ++ f True)

-- This version is actually slightly slower!!(???) -- unless we use the trick above
game_nondet_manual :: [Int] -> [Int]
game_nondet_manual input = play input where
  play [] = drop 1 ([0,0])
  play (x : xs) =
    -- concatMap (\rest -> [rest + x] ++ [rest]) (play xs)
    concatMap (\rest -> [rest + x]) (play xs) ++ concatMap (\rest -> [rest]) (play xs)
    -- let r = play xs in concatMap (\rest -> [rest + x]) r ++ concatMap (\rest -> [rest]) r

-- These have the same speed...
game_lucky = game id (\f x -> f x) (\f -> f True)
game_lucky_manual input = play input where
  play [] = 0
  play (x : xs) = (play xs) + x

main = do
  defaultMain [
      bench "nondet" $ whnf game_nondet [0..666]
    -- , bench "nondet'" $ whnf game_nondet' [0..666]
    , bench "nondet_manual" $ whnf game_nondet_manual [0..666]
    , bench "lucky" $ whnf game_lucky [0..666]
    , bench "lucky_manual" $ whnf game_lucky_manual [0..666]
    ]
-- main = pure()
