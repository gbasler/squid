-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 38; Boxes: 11; Branches: 7
-- Apps: 6; Lams: 5; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module IterCont (loop,dont) where

import GHC.Num

dont = (((GHC.Num.+) 0) 1)

loop = (\f -> (\state -> ((f (_0(# {-A-}\(new_state) -> f #))) state)))

_0(# f' #) = (\new_state -> (({-P-}(f'(new_state)) (_0(# {-A-}\(new_state) -> {-P-}(f'(new_state)) #))) new_state))
