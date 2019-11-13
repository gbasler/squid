module ListsFun where

length_mine [] = 0
length_mine (x : xs) = length_mine xs + 1

enumFromTo_mine from to = go from where
  go from = if from > to then [] else from : go (from + 1)

test = length_mine (enumFromTo_mine 0 5)
