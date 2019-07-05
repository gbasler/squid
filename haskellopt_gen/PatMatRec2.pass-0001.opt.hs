-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 111; Boxes: 21; Branches: 57
-- Apps: 7; Lams: 1; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module PatMatRec (usum,usum'0,usum'2) where

import GHC.Num

usum = (\eta -> (case eta of {[] -> _0; (:) arg0 arg1 -> (((GHC.Num.+) arg0) _1)}))

_0 = (GHC.Num.fromInteger 0)

_1 = _1

usum'0 = _0

usum'2 = (((GHC.Num.+) 1) _2)

_2 = _2
