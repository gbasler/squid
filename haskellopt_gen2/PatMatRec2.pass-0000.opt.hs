-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  12
-- Incl. one-shot:   0
-- Case reductions:  10
-- Field reductions: 20
-- Case commutings:  0
-- Total nodes: 194; Boxes: 48; Branches: 61
-- Apps: 17; Lams: 4

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module PatMatRec (alternate'0,alternate,oops'0,oops,usum'2,usum'1,usum'0,usum) where

import GHC.List
import GHC.Num
import GHC.Tuple
import GHC.Types

alternate'0 = 
  let rec π π' = π' : (rec π' π) in
  GHC.List.take (5::Int) (True : (rec True False))

alternate = \ds -> 
  let rec π π' = π' : (rec π' π) in
  case ds of { (,) ρ ρ' -> ρ : (rec ρ ρ') }

oops'0 = 
  let rec xs xs' xs'2 = 
        let rec' π xs'3 xs'4 = case π of { (:) ρ ρ' -> ρ + (rec' ρ' xs'3 xs'3); [] -> (rec xs'4 xs'3 xs'3) } in
        case xs of { (:) ρ'2 ρ'3 -> ρ'2 + (rec' ρ'3 xs' xs'); [] -> (rec xs'2 xs' xs') } in
  (rec [] [] [])

oops = \xs -> \ds -> let
        rec π' xs'6 xs'7 = 
              let rec'3 xs'8 xs'9 xs'10 = case xs'8 of { (:) ρ'6 ρ'7 -> ρ'6 + (rec ρ'7 xs'10 xs'10); [] -> (rec'3 xs'9 xs'10 xs'10) } in
              case π' of { (:) ρ'8 ρ'9 -> ρ'8 + (rec ρ'9 xs'7 xs'7); [] -> (rec'3 xs'6 xs'7 xs'7) }
        rec' xs' xs'2 xs'3 = 
              let rec'2 π xs'4 xs'5 = case π of { (:) ρ'2 ρ'3 -> ρ'2 + (rec'2 ρ'3 xs'5 xs'5); [] -> (rec' xs'4 xs'5 xs'5) } in
              case xs' of { (:) ρ'4 ρ'5 -> ρ'4 + (rec'2 ρ'5 xs'3 xs'3); [] -> (rec' xs'2 xs'3 xs'3) }
        in case ds of { (:) ρ ρ' -> ρ + (rec ρ' xs xs); [] -> (rec' xs xs xs) }

usum'2 = (1::Int) + ((2::Int) + (0::Int))

usum'1 = (1::Int) + (0::Int)

usum'0 = (0::Int)

usum = \ds -> 
  let rec _fε = case _fε of { (:) ρ'2 ρ'3 -> ρ'2 + (rec ρ'3); [] -> (0::Int) } in
  case ds of { (:) ρ ρ' -> ρ + (rec ρ'); [] -> (0::Int) }
