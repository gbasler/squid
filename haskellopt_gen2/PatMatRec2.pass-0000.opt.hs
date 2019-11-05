-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  10
-- Incl. one-shot:  0
-- Case reductions:  8
-- Field reductions:  16
-- Total nodes: 183; Boxes: 52; Branches: 66
-- Apps: 12; Lams: 3

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module PatMatRec (oops'0,oops,usum'2,usum'1,usum'0,usum) where

import GHC.Num
import GHC.Types

oops'0 = 
  let rec xs = 
        let rec' π = case π of { (:) ρ ρ' -> ρ + (rec' ρ'); [] -> (rec []) } in
        case xs of { (:) ρ'2 ρ'3 -> ρ'2 + (rec' ρ'3); [] -> (rec []) } in
  (rec [])

oops = \xs -> \ds -> let
        rec π' = 
              let rec'3 xs'2 = case xs'2 of { (:) ρ'6 ρ'7 -> ρ'6 + (rec ρ'7); [] -> (rec'3 xs) } in
              case π' of { (:) ρ'8 ρ'9 -> ρ'8 + (rec ρ'9); [] -> (rec'3 xs) }
        rec' xs' = 
              let rec'2 π = case π of { (:) ρ'2 ρ'3 -> ρ'2 + (rec'2 ρ'3); [] -> (rec' xs) } in
              case xs' of { (:) ρ'4 ρ'5 -> ρ'4 + (rec'2 ρ'5); [] -> (rec' xs) }
        in case ds of { (:) ρ ρ' -> ρ + (rec ρ'); [] -> (rec' xs) }

usum'2 = (1::Int) + ((2::Int) + (0::Int))

usum'1 = (1::Int) + (0::Int)

usum'0 = (0::Int)

usum = \ds -> 
  let rec _fε = case _fε of { (:) ρ'2 ρ'3 -> ρ'2 + (rec ρ'3); [] -> (0::Int) } in
  case ds of { (:) ρ ρ' -> ρ + (rec ρ'); [] -> (0::Int) }
