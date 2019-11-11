-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  7
-- Incl. one-shot:   0
-- Case reductions:  9
-- Field reductions: 4
-- Case commutings:  1
-- Total nodes: 195; Boxes: 34; Branches: 43
-- Apps: 18; Lams: 6

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Motiv (pgrm,f,e1,e2,e3,e0,isJust) where

import GHC.Maybe
import GHC.Num
import GHC.Tuple
import GHC.Types

pgrm = ((2::Int) * (1::Int)) + ((0::Int) + (1::Int))

f = \x -> 
  let _cε = case x of { Just ρ' -> (1::Int); Nothing -> (0::Int) } in
  case x of { Just ρ -> ρ * _cε; Nothing -> _cε + (1::Int) }

e1 = \ds -> case ds of { (,) ρ ρ' -> ρ * ρ' }

e2 = \z -> z + (1::Int)

e3 = \c -> case c of { True -> (1::Int); False -> (0::Int) }

e0 = \a -> a

isJust = \ds -> case ds of { Just ρ -> True; Nothing -> False }
