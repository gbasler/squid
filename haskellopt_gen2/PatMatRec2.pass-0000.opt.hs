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
  GHC.List.take (5::Int) (True : (False : (rec False True)))

alternate = \ds -> 
  let rec π π' = π' : (rec π' π) in
  case ds of { (,) ρ ρ' -> ρ : (ρ' : (rec ρ' ρ)) }

oops'0 = 
  let rec xs xs' xs'2 = 
        let rec' π xs'3 xs'4 = case π of { (:) ρ ρ' -> ρ + (rec' ρ' xs'3 xs'3); [] -> (rec xs'4 xs'3 xs'3) } in
        case xs of { (:) ρ'2 ρ'3 -> ρ'2 + (case ρ'3 of { (:) ρ'4 ρ'5 -> ρ'4 + (rec' ρ'5 xs' xs'); [] -> (rec xs' xs' xs') }); [] -> (rec xs'2 xs' xs') } in
  (rec [] [] [])

oops = \xs -> \ds -> let
        rec'2 xs'26 xs'27 xs'28 = 
              let rec'11 π'5 xs'29 xs'30 = case π'5 of { (:) ρ'34 ρ'35 -> ρ'34 + (rec'11 ρ'35 xs'29 xs'29); [] -> (rec'2 xs'30 xs'29 xs'29) } in
              case xs'26 of { (:) ρ'36 ρ'37 -> ρ'36 + (rec'11 ρ'37 xs'27 xs'27); [] -> (rec'2 xs'28 xs'27 xs'27) }
        rec' π'4 xs'21 xs'22 = 
              let rec'10 xs'23 xs'24 xs'25 = case xs'23 of { (:) ρ'30 ρ'31 -> ρ'30 + (rec' ρ'31 xs'24 xs'24); [] -> (rec'10 xs'25 xs'24 xs'24) } in
              case π'4 of { (:) ρ'32 ρ'33 -> ρ'32 + (rec' ρ'33 xs'21 xs'21); [] -> (rec'10 xs'22 xs'21 xs'21) }
        rec π'3 xs'16 xs'17 = 
              let rec'9 xs'18 xs'19 xs'20 = case xs'18 of { (:) ρ'24 ρ'25 -> ρ'24 + (rec ρ'25 xs'19 xs'19); [] -> (rec'9 xs'20 xs'19 xs'19) } in
              case π'3 of { (:) ρ'26 ρ'27 -> ρ'26 + (rec ρ'27 xs'16 xs'16); [] -> (case xs'17 of { (:) ρ'28 ρ'29 -> ρ'28 + (rec ρ'29 xs'16 xs'16); [] -> (rec'9 xs'16 xs'16 xs'16) }) }
        rec'5 xs'11 xs'12 xs'13 = 
              let rec'8 π'2 xs'14 xs'15 = case π'2 of { (:) ρ'18 ρ'19 -> ρ'18 + (rec'8 ρ'19 xs'14 xs'14); [] -> (rec'5 xs'15 xs'14 xs'14) } in
              case xs'11 of { (:) ρ'20 ρ'21 -> ρ'20 + (case ρ'21 of { (:) ρ'22 ρ'23 -> ρ'22 + (rec'8 ρ'23 xs'12 xs'12); [] -> (rec'5 xs'12 xs'12 xs'12) }); [] -> (rec'5 xs'13 xs'12 xs'12) }
        rec'4 xs'6 xs'7 xs'8 = 
              let rec'7 π' xs'9 xs'10 = case π' of { (:) ρ'14 ρ'15 -> ρ'14 + (rec'7 ρ'15 xs'9 xs'9); [] -> (rec'4 xs'10 xs'9 xs'9) } in
              case xs'6 of { (:) ρ'16 ρ'17 -> ρ'16 + (rec'7 ρ'17 xs'7 xs'7); [] -> (rec'4 xs'8 xs'7 xs'7) }
        rec'3 π xs' xs'2 = 
              let rec'6 xs'3 xs'4 xs'5 = case xs'3 of { (:) ρ'10 ρ'11 -> ρ'10 + (rec'3 ρ'11 xs'4 xs'4); [] -> (rec'6 xs'5 xs'4 xs'4) } in
              case π of { (:) ρ'12 ρ'13 -> ρ'12 + (rec'3 ρ'13 xs' xs'); [] -> (rec'6 xs'2 xs' xs') }
        in case ds of { (:) ρ ρ' -> ρ + (case ρ' of { (:) ρ'2 ρ'3 -> ρ'2 + (rec ρ'3 xs xs); [] -> (case xs of { (:) ρ'4 ρ'5 -> ρ'4 + (rec' ρ'5 xs xs); [] -> (rec'2 xs xs xs) }) }); [] -> (case xs of { (:) ρ'6 ρ'7 -> ρ'6 + (case ρ'7 of { (:) ρ'8 ρ'9 -> ρ'8 + (rec'3 ρ'9 xs xs); [] -> (rec'4 xs xs xs) }); [] -> (rec'5 xs xs xs) }) }

usum'2 = (1::Int) + ((2::Int) + (0::Int))

usum'1 = (1::Int) + (0::Int)

usum'0 = (0::Int)

usum = \ds -> 
  let rec _fε = case _fε of { (:) ρ'4 ρ'5 -> ρ'4 + (rec ρ'5); [] -> (0::Int) } in
  case ds of { (:) ρ ρ' -> ρ + (case ρ' of { (:) ρ'2 ρ'3 -> ρ'2 + (rec ρ'3); [] -> (0::Int) }); [] -> (0::Int) }
