-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  6
-- Incl. one-shot:   0
-- Case reductions:  4
-- Field reductions: 8
-- Case commutings:  8
-- Total nodes: 140; Boxes: 43; Branches: 24
-- Apps: 19; Lams: 4

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Deforest (pgrm,sum_m,map_m) where

import GHC.Num
import GHC.Types

pgrm = \ls -> let
  rec' π = case π of { (:) ρ'6 ρ'7 -> (ρ'6 + (1::Int)) : (rec' ρ'7); [] -> [] }
  rec _cfε = case _cfε of { (:) ρ'4 ρ'5 -> ρ'4 + (rec ρ'5); [] -> (0::Int) }
  in case ls of { (:) ρ ρ' -> (ρ + (1::Int)) + (case ρ' of { (:) ρ'2 ρ'3 -> (ρ'2 + (1::Int)) + (rec (rec' ρ'3)); [] -> (0::Int) }); [] -> (0::Int) }

sum_m = \ds -> 
  let rec _cfε = case _cfε of { (:) ρ'2 ρ'3 -> ρ'2 + (rec ρ'3); [] -> (0::Int) } in
  case ds of { (:) ρ ρ' -> ρ + (rec ρ'); [] -> (0::Int) }

map_m = \f -> \ds -> 
        let rec π f' = case π of { (:) ρ'2 ρ'3 -> f' ρ'2 : (rec ρ'3 f'); [] -> [] } in
        case ds of { (:) ρ ρ' -> f ρ : (rec ρ' f); [] -> [] }
