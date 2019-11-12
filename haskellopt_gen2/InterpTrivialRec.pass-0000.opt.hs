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
  rec _fε'2 pgm'5 = let
        rec'4 pgm'6 pgm'7 = case pgm'6 of { (:) ρ'20 ρ'21 -> True : (rec ρ'21 pgm'7); [] -> False : (case pgm'7 of { (:) ρ'22 ρ'23 -> True : (rec ρ'23 pgm'7); [] -> False : (rec'4 pgm'7 pgm'7) }) }
        rec'5 pgm'8 pgm'9 = case pgm'8 of { (:) ρ'28 ρ'29 -> True : (case ρ'29 of { (:) ρ'30 ρ'31 -> True : (rec ρ'31 pgm'9); [] -> False : (case pgm'9 of { (:) ρ'32 ρ'33 -> True : (rec ρ'33 pgm'9); [] -> False : (rec'5 pgm'9 pgm'9) }) }); [] -> False : (case pgm'9 of { (:) ρ'34 ρ'35 -> True : (case ρ'35 of { (:) ρ'36 ρ'37 -> True : (rec ρ'37 pgm'9); [] -> False : (rec'5 pgm'9 pgm'9) }); [] -> False : (rec'5 pgm'9 pgm'9) }) }
        in case _fε'2 of { (:) ρ'24 ρ'25 -> True : (case ρ'25 of { (:) ρ'26 ρ'27 -> True : (rec ρ'27 pgm'5); [] -> False : (rec'4 pgm'5 pgm'5) }); [] -> False : (rec'5 pgm'5 pgm'5) }
  rec' pgm' pgm'2 = let
        rec'2 _fε pgm'3 = case _fε of { (:) ρ'2 ρ'3 -> True : (case ρ'3 of { (:) ρ'4 ρ'5 -> True : (rec'2 ρ'5 pgm'3); [] -> False : (case pgm'3 of { (:) ρ'6 ρ'7 -> True : (rec'2 ρ'7 pgm'3); [] -> False : (rec' pgm'3 pgm'3) }) }); [] -> False : (case pgm'3 of { (:) ρ'8 ρ'9 -> True : (case ρ'9 of { (:) ρ'10 ρ'11 -> True : (rec'2 ρ'11 pgm'3); [] -> False : (rec' pgm'3 pgm'3) }); [] -> False : (rec' pgm'3 pgm'3) }) }
        rec'3 _fε' pgm'4 = case _fε' of { (:) ρ'16 ρ'17 -> True : (case ρ'17 of { (:) ρ'18 ρ'19 -> True : (rec'3 ρ'19 pgm'4); [] -> False : (rec' pgm'4 pgm'4) }); [] -> False : (rec' pgm'4 pgm'4) }
        in case pgm' of { (:) ρ'12 ρ'13 -> True : (rec'2 ρ'13 pgm'2); [] -> False : (case pgm'2 of { (:) ρ'14 ρ'15 -> True : (rec'3 ρ'15 pgm'2); [] -> False : (rec' pgm'2 pgm'2) }) }
  in case pgm of { (:) ρ ρ' -> True : (rec ρ' pgm); [] -> False : (rec' pgm pgm) }
