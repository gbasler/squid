module Main where

import Criterion.Main

game :: (Int -> [Int]) -> ((Int -> [Int]) -> [Int] -> [Int]) -> ((Bool -> [Int]) -> [Int]) -> [Int] -> [Int]
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
game_nondet = game (:[]) concatMap (\f -> f True ++ f False)

-- This version is actually slightly slower!!(???)
game_nondet_manual :: [Int] -> [Int]
game_nondet_manual input = play input where
  play [] = [0]
  play (x : xs) =
    -- concatMap (\rest -> [rest + x] ++ [rest]) (play xs)
    concatMap (\rest -> [rest + x]) (play xs) ++ concatMap (\rest -> [rest]) (play xs)
    -- let r = play xs in concatMap (\rest -> [rest + x]) r ++ concatMap (\rest -> [rest]) r

main = do
  defaultMain [
      bench "nondet" $ whnf game_nondet [0..666]
    , bench "nondet_manual" $ whnf game_nondet_manual [0..666]
    ]
-- main = pure()
