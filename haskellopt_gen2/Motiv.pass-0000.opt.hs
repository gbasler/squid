-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  7
-- Incl. one-shot:  0
-- Case reductions:  7
-- Field reductions:  4
-- Total nodes: 234; Boxes: 48; Branches: 56
-- Apps: 18; Lams: 6

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Motiv (pgrm,f,e1,e2,e3,e0,isJust) where

import GHC.Maybe
import GHC.Num
import GHC.Tuple
import GHC.Types

pgrm = (2 * 1) + (0 + 1)

f = \x -> 
  let _cε = case case x of { Just ρ' -> True; Nothing -> False } of { True -> 1; False -> 0 } in
  case x of { Just ρ -> ρ * _cε; Nothing -> _cε + 1 }

e1 = \ds -> case ds of { (,) ρ ρ' -> ρ * ρ' }

e2 = \z -> z + 1

e3 = \c -> case c of { True -> 1; False -> 0 }

e0 = \a -> a

isJust = \ds -> case ds of { Just ρ -> True; Nothing -> False }
