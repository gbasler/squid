-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  7
-- Incl. one-shot:   0
-- Case reductions:  20
-- Field reductions: 20
-- Case commutings:  0
-- Total nodes: 437; Boxes: 182; Branches: 158
-- Apps: 34; Lams: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module InterpTrivialRec (test3_10,test3,test2_10,test2,test1,test0,exec) where

import GHC.List
import GHC.Tuple
import GHC.Types

test3_10 = 
  let rec = False : (True : (True : (True : (False : (True : rec))))) in
  GHC.List.take (10::Int) (True : (True : (True : rec)))

test3 = 
  let rec = False : (True : (True : (True : (False : (True : rec))))) in
  True : (True : (True : rec))

test2_10 = 
  let rec = False : (True : (True : (False : (True : rec)))) in
  GHC.List.take (10::Int) (True : (True : rec))

test2 = 
  let rec = False : (True : (True : (False : (True : rec)))) in
  True : (True : rec)

test1 = 
  let rec = False : (True : (False : (True : rec))) in
  True : rec

test0 = 
  let rec = False : (False : rec) in
  False : rec

exec = \pgm -> let
  rec' pgm'6 pgm'7 = let
        rec'4 _fε' pgm'8 = case _fε' of { (:) ρ'20 ρ'21 -> True : (case ρ'21 of { (:) ρ'22 ρ'23 -> True : (rec'4 ρ'23 pgm'7); [] -> False : (rec' pgm'8 pgm) }); [] -> False : (rec' pgm'8 pgm) }
        rec'5 _fε'2 pgm'9 = case _fε'2 of { (:) ρ'28 ρ'29 -> True : (case ρ'29 of { (:) ρ'30 ρ'31 -> True : (rec'5 ρ'31 pgm'7); [] -> False : (case pgm'9 of { (:) ρ'32 ρ'33 -> True : (rec'5 ρ'33 pgm'7); [] -> False : (rec' pgm'9 pgm) }) }); [] -> False : (case pgm'9 of { (:) ρ'34 ρ'35 -> True : (case ρ'35 of { (:) ρ'36 ρ'37 -> True : (rec'5 ρ'37 pgm'7); [] -> False : (rec' pgm'9 pgm) }); [] -> False : (rec' pgm'9 pgm) }) }
        in case pgm'6 of { (:) ρ'24 ρ'25 -> True : (rec'5 ρ'25 pgm'7); [] -> False : (case pgm'7 of { (:) ρ'26 ρ'27 -> True : (rec'4 ρ'27 pgm'7); [] -> False : (rec' pgm'7 pgm) }) }
  rec _fε pgm' = let
        rec'2 pgm'2 pgm'3 = case pgm'2 of { (:) ρ'2 ρ'3 -> True : (case ρ'3 of { (:) ρ'4 ρ'5 -> True : (rec ρ'5 pgm); [] -> False : (case pgm'3 of { (:) ρ'6 ρ'7 -> True : (rec ρ'7 pgm); [] -> False : (rec'2 pgm'3 pgm') }) }); [] -> False : (case pgm'3 of { (:) ρ'8 ρ'9 -> True : (case ρ'9 of { (:) ρ'10 ρ'11 -> True : (rec ρ'11 pgm); [] -> False : (rec'2 pgm'3 pgm') }); [] -> False : (rec'2 pgm'3 pgm') }) }
        rec'3 pgm'4 pgm'5 = case pgm'4 of { (:) ρ'16 ρ'17 -> True : (rec ρ'17 pgm); [] -> False : (case pgm'5 of { (:) ρ'18 ρ'19 -> True : (rec ρ'19 pgm); [] -> False : (rec'3 pgm'5 pgm') }) }
        in case _fε of { (:) ρ'12 ρ'13 -> True : (case ρ'13 of { (:) ρ'14 ρ'15 -> True : (rec ρ'15 pgm); [] -> False : (rec'3 pgm' pgm') }); [] -> False : (rec'2 pgm' pgm') }
  in case pgm of { (:) ρ ρ' -> True : (rec ρ' pgm); [] -> False : (rec' pgm pgm) }
