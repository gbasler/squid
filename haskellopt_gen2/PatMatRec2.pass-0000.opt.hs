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
  let rec xs = let
        rec' π = case π of { (:) ρ ρ' -> ρ + (case ρ' of { (:) ρ'2 ρ'3 -> ρ'2 + (rec' ρ'3); [] -> (case [] of { (:) ρ'4 ρ'5 -> ρ'4 + (rec' ρ'5); [] -> (rec []) }) }); [] -> (case [] of { (:) ρ'6 ρ'7 -> ρ'6 + (case ρ'7 of { (:) ρ'8 ρ'9 -> ρ'8 + (rec' ρ'9); [] -> (rec []) }); [] -> (rec []) }) }
        rec'2 π' = case π' of { (:) ρ'14 ρ'15 -> ρ'14 + (case ρ'15 of { (:) ρ'16 ρ'17 -> ρ'16 + (rec'2 ρ'17); [] -> (rec []) }); [] -> (rec []) }
        in case xs of { (:) ρ'10 ρ'11 -> ρ'10 + (rec' ρ'11); [] -> (case [] of { (:) ρ'12 ρ'13 -> ρ'12 + (rec'2 ρ'13); [] -> (rec []) }) } in
  (rec [])

oops = \xs -> \ds -> let
        rec' xs'3 = let
              rec'4 π' = case π' of { (:) ρ'20 ρ'21 -> ρ'20 + (case ρ'21 of { (:) ρ'22 ρ'23 -> ρ'22 + (rec'4 ρ'23); [] -> (case xs of { (:) ρ'24 ρ'25 -> ρ'24 + (rec'4 ρ'25); [] -> (rec' xs) }) }); [] -> (case xs of { (:) ρ'26 ρ'27 -> ρ'26 + (case ρ'27 of { (:) ρ'28 ρ'29 -> ρ'28 + (rec'4 ρ'29); [] -> (rec' xs) }); [] -> (rec' xs) }) }
              rec'5 π'2 = case π'2 of { (:) ρ'34 ρ'35 -> ρ'34 + (case ρ'35 of { (:) ρ'36 ρ'37 -> ρ'36 + (rec'5 ρ'37); [] -> (rec' xs) }); [] -> (rec' xs) }
              in case xs'3 of { (:) ρ'30 ρ'31 -> ρ'30 + (rec'4 ρ'31); [] -> (case xs of { (:) ρ'32 ρ'33 -> ρ'32 + (rec'5 ρ'33); [] -> (rec' xs) }) }
        rec π = let
              rec'2 xs' = case xs' of { (:) ρ'2 ρ'3 -> ρ'2 + (rec ρ'3); [] -> (case xs of { (:) ρ'4 ρ'5 -> ρ'4 + (rec ρ'5); [] -> (rec'2 xs) }) }
              rec'3 xs'2 = case xs'2 of { (:) ρ'10 ρ'11 -> ρ'10 + (case ρ'11 of { (:) ρ'12 ρ'13 -> ρ'12 + (rec ρ'13); [] -> (case xs of { (:) ρ'14 ρ'15 -> ρ'14 + (rec ρ'15); [] -> (rec'3 xs) }) }); [] -> (case xs of { (:) ρ'16 ρ'17 -> ρ'16 + (case ρ'17 of { (:) ρ'18 ρ'19 -> ρ'18 + (rec ρ'19); [] -> (rec'3 xs) }); [] -> (rec'3 xs) }) }
              in case π of { (:) ρ'6 ρ'7 -> ρ'6 + (case ρ'7 of { (:) ρ'8 ρ'9 -> ρ'8 + (rec ρ'9); [] -> (rec'2 xs) }); [] -> (rec'3 xs) }
        in case ds of { (:) ρ ρ' -> ρ + (rec ρ'); [] -> (rec' xs) }

usum'2 = (1::Int) + ((2::Int) + (0::Int))

usum'1 = (1::Int) + (0::Int)

usum'0 = (0::Int)

usum = \ds -> 
  let rec _fε = case _fε of { (:) ρ'2 ρ'3 -> ρ'2 + (case ρ'3 of { (:) ρ'4 ρ'5 -> ρ'4 + (rec ρ'5); [] -> (0::Int) }); [] -> (0::Int) } in
  case ds of { (:) ρ ρ' -> ρ + (rec ρ'); [] -> (0::Int) }
