-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  7
-- Incl. one-shot:  0
-- Total nodes: 262; Boxes: 58; Branches: 38
-- Apps: 33; Lams: 6

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Motiv (pgrm,f,e1,e2,e3,e0,isJust) where

import GHC.Maybe
import GHC.Num
import GHC.Tuple
import GHC.Types

pgrm = let
  _0 = Just 2
  ψ = case case _0 of { Nothing -> False; Just ρ'7 -> True } of { False -> 0; True -> 1 }
  ψ' = case case Nothing of { Nothing -> False; Just ρ'6 -> True } of { False -> 0; True -> 1 }
  in (case _0 of { Nothing -> ψ + 1; Just ρ -> (case (,) (case _0 of Just arg -> arg) ψ of { (,) ρ' ρ'2 -> ρ' * ρ'2 }) }) + (case Nothing of { Nothing -> ψ' + 1; Just ρ'3 -> (case (,) (case Nothing of Just arg -> arg) ψ' of { (,) ρ'4 ρ'5 -> ρ'4 * ρ'5 }) })

f = \x -> 
  let ψ = case case x of { Nothing -> False; Just ρ'3 -> True } of { False -> 0; True -> 1 } in
  case x of { Nothing -> ψ + 1; Just ρ -> (case (,) (case x of Just arg -> arg) ψ of { (,) ρ' ρ'2 -> ρ' * ρ'2 }) }

e1 = \ds -> case ds of { (,) ρ ρ' -> ρ * ρ' }

e2 = \z -> z + 1

e3 = \c -> case c of { False -> 0; True -> 1 }

e0 = \a -> a

isJust = \ds -> case ds of { Nothing -> False; Just ρ -> True }
