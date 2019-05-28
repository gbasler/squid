-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 38; Boxes: 11; Branches: 7
-- Apps: 6; Lams: 5; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module IterCont (loop,dont) where

import GHC.Num

dont = (_0(# 0 #))

_0(# s #) = (((GHC.Num.+) s) 1)

loop = (\f -> (\state -> ((f (_1(# {-A-}\(new_state) -> f #))) state)))

_1(# f' #) = (\new_state -> (({-P-}(f'(new_state)) (_1(# {-P-}(f'(new_state)) #))) new_state))
