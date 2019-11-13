module Deforest where

map_m f = go where
  go [] = []
  go (x : xs) = f x : go xs

sum_m [] = 0
sum_m (y : ys) = y + sum_m ys

pgrm ls = sum_m (map_m (+1) ls)
