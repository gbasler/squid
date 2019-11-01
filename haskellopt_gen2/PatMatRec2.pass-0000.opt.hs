-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  4
-- Incl. one-shot:  0
-- Case reductions:  6
-- Field reductions:  12
-- Total nodes: 133; Boxes: 32; Branches: 48
-- Apps: 12; Lams: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module PatMatRec (usum'2,usum'1,usum'0,usum) where

import GHC.Num
import GHC.Types

usum'2 = 1 + (2 + fromInteger 0)

usum'1 = 1 + fromInteger 0

usum'0 = fromInteger 0

usum = \ds -> 
  let rec _fε = case _fε of { [] -> fromInteger 0; (:) ρ'2 ρ'3 -> ρ'2 + (rec ρ'3) } in
  case ds of { [] -> fromInteger 0; (:) ρ ρ' -> ρ + (rec ρ') }
