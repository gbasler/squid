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
  let rec = True : (True : (False : (True : rec))) in
  GHC.List.take (10::Int) (True : (True : (True : (False : (True : rec)))))

test3 = 
  let rec = True : (True : (False : (True : rec))) in
  True : (True : (True : (False : (True : rec))))

test2_10 = 
  let rec = True : (False : (True : rec)) in
  GHC.List.take (10::Int) (True : (True : (False : (True : rec))))

test2 = 
  let rec = True : (False : (True : rec)) in
  True : (True : (False : (True : rec)))

test1 = 
  let rec = False : (True : rec) in
  True : (False : (True : rec))

test0 = 
  let rec = False : rec in
  False : (False : rec)

exec = \pgm -> let
  rec _fε'5 pgm'16 = 
        let rec'11 pgm'17 pgm'18 = case pgm'17 of { (:) ρ'32 ρ'33 -> True : (rec ρ'33 pgm'18); [] -> False : (rec'11 pgm'18 pgm'18) } in
        case _fε'5 of { (:) ρ'34 ρ'35 -> True : (rec ρ'35 pgm'16); [] -> False : (case pgm'16 of { (:) ρ'36 ρ'37 -> True : (rec ρ'37 pgm'16); [] -> False : (rec'11 pgm'16 pgm'16) }) }
  rec'2 pgm'13 pgm'14 = 
        let rec'10 _fε'4 pgm'15 = case _fε'4 of { (:) ρ'28 ρ'29 -> True : (rec'10 ρ'29 pgm'15); [] -> False : (rec'2 pgm'15 pgm'15) } in
        case pgm'13 of { (:) ρ'30 ρ'31 -> True : (rec'10 ρ'31 pgm'14); [] -> False : (rec'2 pgm'14 pgm'14) }
  rec' _fε'3 pgm'10 = 
        let rec'9 pgm'11 pgm'12 = case pgm'11 of { (:) ρ'24 ρ'25 -> True : (rec' ρ'25 pgm'12); [] -> False : (rec'9 pgm'12 pgm'12) } in
        case _fε'3 of { (:) ρ'26 ρ'27 -> True : (rec' ρ'27 pgm'10); [] -> False : (rec'9 pgm'10 pgm'10) }
  rec'4 pgm'7 pgm'8 = 
        let rec'8 _fε'2 pgm'9 = case _fε'2 of { (:) ρ'20 ρ'21 -> True : (rec'8 ρ'21 pgm'9); [] -> False : (rec'4 pgm'9 pgm'9) } in
        case pgm'7 of { (:) ρ'22 ρ'23 -> True : (rec'8 ρ'23 pgm'8); [] -> False : (rec'4 pgm'8 pgm'8) }
  rec'3 _fε' pgm'4 = 
        let rec'7 pgm'5 pgm'6 = case pgm'5 of { (:) ρ'16 ρ'17 -> True : (rec'3 ρ'17 pgm'6); [] -> False : (rec'7 pgm'6 pgm'6) } in
        case _fε' of { (:) ρ'18 ρ'19 -> True : (rec'3 ρ'19 pgm'4); [] -> False : (rec'7 pgm'4 pgm'4) }
  rec'5 pgm' pgm'2 = 
        let rec'6 _fε pgm'3 = case _fε of { (:) ρ'10 ρ'11 -> True : (rec'6 ρ'11 pgm'3); [] -> False : (rec'5 pgm'3 pgm'3) } in
        case pgm' of { (:) ρ'12 ρ'13 -> True : (case ρ'13 of { (:) ρ'14 ρ'15 -> True : (rec'6 ρ'15 pgm'2); [] -> False : (rec'5 pgm'2 pgm'2) }); [] -> False : (rec'5 pgm'2 pgm'2) }
  in case pgm of { (:) ρ ρ' -> True : (case ρ' of { (:) ρ'2 ρ'3 -> True : (rec ρ'3 pgm); [] -> False : (case pgm of { (:) ρ'4 ρ'5 -> True : (rec' ρ'5 pgm); [] -> False : (rec'2 pgm pgm) }) }); [] -> False : (case pgm of { (:) ρ'6 ρ'7 -> True : (case ρ'7 of { (:) ρ'8 ρ'9 -> True : (rec'3 ρ'9 pgm); [] -> False : (rec'4 pgm pgm) }); [] -> False : (rec'5 pgm pgm) }) }
