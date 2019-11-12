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
  let rec π π' = π' : (π : (rec π π')) in
  GHC.List.take (5::Int) (True : (rec True False))

alternate = \ds -> 
  let rec π π' = π' : (π : (rec π π')) in
  case ds of { (,) ρ ρ' -> ρ : (rec ρ ρ') }

oops'0 = 
  let rec xs xs' = 
        let rec' π xs'2 = case π of { (:) ρ ρ' -> ρ + (case ρ' of { (:) ρ'2 ρ'3 -> ρ'2 + (rec' ρ'3 xs); [] -> (rec [] []) }); [] -> (rec [] []) } in
        case xs' of { (:) ρ'4 ρ'5 -> ρ'4 + (rec' ρ'5 xs); [] -> (rec [] []) } in
  (rec [] [])

oops = \xs -> \ds -> let
        rec π'2 xs'8 xs'9 = let
              rec'4 xs'10 xs'11 xs'12 = case xs'10 of { (:) ρ'20 ρ'21 -> ρ'20 + (rec ρ'21 xs xs); [] -> (case xs'12 of { (:) ρ'22 ρ'23 -> ρ'22 + (rec ρ'23 xs xs); [] -> (rec'4 xs'11 xs'9 xs'9) }) }
              rec'5 xs'13 xs'14 xs'15 = case xs'13 of { (:) ρ'28 ρ'29 -> ρ'28 + (case ρ'29 of { (:) ρ'30 ρ'31 -> ρ'30 + (rec ρ'31 xs xs); [] -> (case xs'15 of { (:) ρ'32 ρ'33 -> ρ'32 + (rec ρ'33 xs xs); [] -> (rec'5 xs'15 xs'9 xs'9) }) }); [] -> (case xs'14 of { (:) ρ'34 ρ'35 -> ρ'34 + (case ρ'35 of { (:) ρ'36 ρ'37 -> ρ'36 + (rec ρ'37 xs xs); [] -> (rec'5 xs'15 xs'9 xs'9) }); [] -> (rec'5 xs'15 xs'9 xs'9) }) }
              in case π'2 of { (:) ρ'24 ρ'25 -> ρ'24 + (case ρ'25 of { (:) ρ'26 ρ'27 -> ρ'26 + (rec ρ'27 xs xs); [] -> (rec'4 xs'9 xs'9 xs'9) }); [] -> (rec'5 xs'8 xs'9 xs'9) }
        rec' xs' xs'2 xs'3 = let
              rec'2 π xs'4 xs'5 = case π of { (:) ρ'2 ρ'3 -> ρ'2 + (case ρ'3 of { (:) ρ'4 ρ'5 -> ρ'4 + (rec'2 ρ'5 xs'3 xs'3); [] -> (rec' xs'4 xs xs) }); [] -> (rec' xs'5 xs xs) }
              rec'3 π' xs'6 xs'7 = case π' of { (:) ρ'10 ρ'11 -> ρ'10 + (case ρ'11 of { (:) ρ'12 ρ'13 -> ρ'12 + (rec'3 ρ'13 xs'3 xs'3); [] -> (case xs'7 of { (:) ρ'14 ρ'15 -> ρ'14 + (rec'3 ρ'15 xs'3 xs'3); [] -> (rec' xs'7 xs xs) }) }); [] -> (case xs'6 of { (:) ρ'16 ρ'17 -> ρ'16 + (case ρ'17 of { (:) ρ'18 ρ'19 -> ρ'18 + (rec'3 ρ'19 xs'3 xs'3); [] -> (rec' xs'7 xs xs) }); [] -> (rec' xs'7 xs xs) }) }
              in case xs' of { (:) ρ'6 ρ'7 -> ρ'6 + (rec'3 ρ'7 xs'3 xs'3); [] -> (case xs'2 of { (:) ρ'8 ρ'9 -> ρ'8 + (rec'2 ρ'9 xs'3 xs'3); [] -> (rec' xs'3 xs xs) }) }
        in case ds of { (:) ρ ρ' -> ρ + (rec ρ' xs xs); [] -> (rec' xs xs xs) }

usum'2 = (1::Int) + ((2::Int) + (0::Int))

usum'1 = (1::Int) + (0::Int)

usum'0 = (0::Int)

usum = \ds -> 
  let rec _fε = case _fε of { (:) ρ'2 ρ'3 -> ρ'2 + (case ρ'3 of { (:) ρ'4 ρ'5 -> ρ'4 + (rec ρ'5); [] -> (0::Int) }); [] -> (0::Int) } in
  case ds of { (:) ρ ρ' -> ρ + (rec ρ'); [] -> (0::Int) }
