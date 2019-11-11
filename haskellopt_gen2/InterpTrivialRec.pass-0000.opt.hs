-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  7
-- Incl. one-shot:   0
-- Case reductions:  20
-- Field reductions: 20
-- Case commutings:  0
-- Total nodes: 317; Boxes: 126; Branches: 130
-- Apps: 20; Lams: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module InterpTrivialRec (test3,test2,test1,test0,exec) where

import GHC.Tuple
import GHC.Types

test3 = 
  let rec = True : (True : (True : (False : (True : (True : (True : (False : rec))))))) in
  True : (True : (True : (False : rec)))

test2 = 
  let rec = True : (True : (False : (True : (True : (False : rec))))) in
  True : (True : (False : rec))

test1 = 
  let rec = True : (False : (True : (False : rec))) in
  True : (False : rec)

test0 = 
  let rec = False : (False : rec) in
  False : rec

exec = \pgm -> let
  rec' pgm'3 = let
        rec'4 _fε' = case _fε' of { (:) ρ'20 ρ'21 -> True : (case ρ'21 of { (:) ρ'22 ρ'23 -> True : (rec'4 ρ'23); [] -> False : (rec' pgm) }); [] -> False : (rec' pgm) }
        rec'5 _fε'2 = case _fε'2 of { (:) ρ'28 ρ'29 -> True : (case ρ'29 of { (:) ρ'30 ρ'31 -> True : (rec'5 ρ'31); [] -> False : (case pgm of { (:) ρ'32 ρ'33 -> True : (rec'5 ρ'33); [] -> False : (rec' pgm) }) }); [] -> False : (case pgm of { (:) ρ'34 ρ'35 -> True : (case ρ'35 of { (:) ρ'36 ρ'37 -> True : (rec'5 ρ'37); [] -> False : (rec' pgm) }); [] -> False : (rec' pgm) }) }
        in case pgm'3 of { (:) ρ'24 ρ'25 -> True : (rec'5 ρ'25); [] -> False : (case pgm of { (:) ρ'26 ρ'27 -> True : (rec'4 ρ'27); [] -> False : (rec' pgm) }) }
  rec _fε = let
        rec'2 pgm' = case pgm' of { (:) ρ'2 ρ'3 -> True : (rec ρ'3); [] -> False : (case pgm of { (:) ρ'4 ρ'5 -> True : (rec ρ'5); [] -> False : (rec'2 pgm) }) }
        rec'3 pgm'2 = case pgm'2 of { (:) ρ'10 ρ'11 -> True : (case ρ'11 of { (:) ρ'12 ρ'13 -> True : (rec ρ'13); [] -> False : (case pgm of { (:) ρ'14 ρ'15 -> True : (rec ρ'15); [] -> False : (rec'3 pgm) }) }); [] -> False : (case pgm of { (:) ρ'16 ρ'17 -> True : (case ρ'17 of { (:) ρ'18 ρ'19 -> True : (rec ρ'19); [] -> False : (rec'3 pgm) }); [] -> False : (rec'3 pgm) }) }
        in case _fε of { (:) ρ'6 ρ'7 -> True : (case ρ'7 of { (:) ρ'8 ρ'9 -> True : (rec ρ'9); [] -> False : (rec'2 pgm) }); [] -> False : (rec'3 pgm) }
  in case pgm of { (:) ρ ρ' -> True : (rec ρ'); [] -> False : (rec' pgm) }
