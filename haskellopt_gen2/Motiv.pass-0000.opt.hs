-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  7
-- Incl. one-shot:  0
-- Case reductions:  6
-- Field reductions:  2
-- Total nodes: 238; Boxes: 48; Branches: 48
-- Apps: 26; Lams: 6

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Motiv (pgrm,f,e1,e2,e3,e0,isJust) where

import GHC.Maybe
import GHC.Num
import GHC.Tuple
import GHC.Types

pgrm = (case (,) 2 1 of { (,) ρ ρ' -> ρ * ρ' }) + (0 + 1)

f = \x -> 
  let _cε = case case x of { Nothing -> False; Just ρ'3 -> True } of { False -> 0; True -> 1 } in
  case x of { Nothing -> _cε + 1; Just ρ -> (case (,) (case x of Just arg -> arg) _cε of { (,) ρ' ρ'2 -> ρ' * ρ'2 }) }

e1 = \ds -> case ds of { (,) ρ ρ' -> ρ * ρ' }

e2 = \z -> z + 1

e3 = \c -> case c of { False -> 0; True -> 1 }

e0 = \a -> a

isJust = \ds -> case ds of { Nothing -> False; Just ρ -> True }
