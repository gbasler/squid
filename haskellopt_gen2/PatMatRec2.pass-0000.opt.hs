-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  12
-- Incl. one-shot:   0
-- Case reductions:  10
-- Field reductions: 20
-- Case commutings:  0
-- Total nodes: 249; Boxes: 81; Branches: 78
-- Apps: 19; Lams: 4

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
  let rec xs = let
        rec' π = case π of { (:) ρ ρ' -> ρ + (case ρ' of { (:) ρ'2 ρ'3 -> ρ'2 + (rec' ρ'3); [] -> (rec []) }); [] -> (rec []) }
        rec'2 π' = case π' of { (:) ρ'8 ρ'9 -> ρ'8 + (case ρ'9 of { (:) ρ'10 ρ'11 -> ρ'10 + (rec'2 ρ'11); [] -> (case [] of { (:) ρ'12 ρ'13 -> ρ'12 + (rec'2 ρ'13); [] -> (rec []) }) }); [] -> (case [] of { (:) ρ'14 ρ'15 -> ρ'14 + (case ρ'15 of { (:) ρ'16 ρ'17 -> ρ'16 + (rec'2 ρ'17); [] -> (rec []) }); [] -> (rec []) }) }
        in case xs of { (:) ρ'4 ρ'5 -> ρ'4 + (rec'2 ρ'5); [] -> (case [] of { (:) ρ'6 ρ'7 -> ρ'6 + (rec' ρ'7); [] -> (rec []) }) } in
  (rec [])

oops = \xs -> \ds -> let
        rec' xs'3 = let
              rec'4 π' = case π' of { (:) ρ'20 ρ'21 -> ρ'20 + (case ρ'21 of { (:) ρ'22 ρ'23 -> ρ'22 + (rec'4 ρ'23); [] -> (rec' xs) }); [] -> (rec' xs) }
              rec'5 π'2 = case π'2 of { (:) ρ'28 ρ'29 -> ρ'28 + (case ρ'29 of { (:) ρ'30 ρ'31 -> ρ'30 + (rec'5 ρ'31); [] -> (case xs of { (:) ρ'32 ρ'33 -> ρ'32 + (rec'5 ρ'33); [] -> (rec' xs) }) }); [] -> (case xs of { (:) ρ'34 ρ'35 -> ρ'34 + (case ρ'35 of { (:) ρ'36 ρ'37 -> ρ'36 + (rec'5 ρ'37); [] -> (rec' xs) }); [] -> (rec' xs) }) }
              in case xs'3 of { (:) ρ'24 ρ'25 -> ρ'24 + (rec'5 ρ'25); [] -> (case xs of { (:) ρ'26 ρ'27 -> ρ'26 + (rec'4 ρ'27); [] -> (rec' xs) }) }
        rec π = let
              rec'2 xs' = case xs' of { (:) ρ'2 ρ'3 -> ρ'2 + (case ρ'3 of { (:) ρ'4 ρ'5 -> ρ'4 + (rec ρ'5); [] -> (case xs of { (:) ρ'6 ρ'7 -> ρ'6 + (rec ρ'7); [] -> (rec'2 xs) }) }); [] -> (case xs of { (:) ρ'8 ρ'9 -> ρ'8 + (case ρ'9 of { (:) ρ'10 ρ'11 -> ρ'10 + (rec ρ'11); [] -> (rec'2 xs) }); [] -> (rec'2 xs) }) }
              rec'3 xs'2 = case xs'2 of { (:) ρ'16 ρ'17 -> ρ'16 + (rec ρ'17); [] -> (case xs of { (:) ρ'18 ρ'19 -> ρ'18 + (rec ρ'19); [] -> (rec'3 xs) }) }
              in case π of { (:) ρ'12 ρ'13 -> ρ'12 + (case ρ'13 of { (:) ρ'14 ρ'15 -> ρ'14 + (rec ρ'15); [] -> (rec'3 xs) }); [] -> (rec'2 xs) }
        in case ds of { (:) ρ ρ' -> ρ + (rec ρ'); [] -> (rec' xs) }

usum'2 = (1::Int) + ((2::Int) + (0::Int))

usum'1 = (1::Int) + (0::Int)

usum'0 = (0::Int)

usum = \ds -> 
  let rec _fε = case _fε of { (:) ρ'2 ρ'3 -> ρ'2 + (case ρ'3 of { (:) ρ'4 ρ'5 -> ρ'4 + (rec ρ'5); [] -> (0::Int) }); [] -> (0::Int) } in
  case ds of { (:) ρ ρ' -> ρ + (rec ρ'); [] -> (0::Int) }
