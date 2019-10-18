-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 24; Boxes: 9; Branches: 5
-- Apps: 2; Lams: 3; Unreduced Redexes: 2

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Church (two,_I,twoI) where



_I = (\x' -> x')

two = (\x -> (\y -> (x (x y))))

twoI = (\y -> y)
