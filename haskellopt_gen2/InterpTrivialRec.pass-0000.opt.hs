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
  rec _fε'2 = let
        rec'4 pgm'2 = case pgm'2 of { (:) ρ'20 ρ'21 -> True : (case ρ'21 of { (:) ρ'22 ρ'23 -> True : (rec ρ'23); [] -> False : (case pgm of { (:) ρ'24 ρ'25 -> True : (rec ρ'25); [] -> False : (rec'4 pgm) }) }); [] -> False : (case pgm of { (:) ρ'26 ρ'27 -> True : (case ρ'27 of { (:) ρ'28 ρ'29 -> True : (rec ρ'29); [] -> False : (rec'4 pgm) }); [] -> False : (rec'4 pgm) }) }
        rec'5 pgm'3 = case pgm'3 of { (:) ρ'34 ρ'35 -> True : (rec ρ'35); [] -> False : (case pgm of { (:) ρ'36 ρ'37 -> True : (rec ρ'37); [] -> False : (rec'5 pgm) }) }
        in case _fε'2 of { (:) ρ'30 ρ'31 -> True : (case ρ'31 of { (:) ρ'32 ρ'33 -> True : (rec ρ'33); [] -> False : (rec'5 pgm) }); [] -> False : (rec'4 pgm) }
  rec' pgm' = let
        rec'2 _fε = case _fε of { (:) ρ'2 ρ'3 -> True : (case ρ'3 of { (:) ρ'4 ρ'5 -> True : (rec'2 ρ'5); [] -> False : (case pgm of { (:) ρ'6 ρ'7 -> True : (rec'2 ρ'7); [] -> False : (rec' pgm) }) }); [] -> False : (case pgm of { (:) ρ'8 ρ'9 -> True : (case ρ'9 of { (:) ρ'10 ρ'11 -> True : (rec'2 ρ'11); [] -> False : (rec' pgm) }); [] -> False : (rec' pgm) }) }
        rec'3 _fε' = case _fε' of { (:) ρ'16 ρ'17 -> True : (case ρ'17 of { (:) ρ'18 ρ'19 -> True : (rec'3 ρ'19); [] -> False : (rec' pgm) }); [] -> False : (rec' pgm) }
        in case pgm' of { (:) ρ'12 ρ'13 -> True : (rec'2 ρ'13); [] -> False : (case pgm of { (:) ρ'14 ρ'15 -> True : (rec'3 ρ'15); [] -> False : (rec' pgm) }) }
  in case pgm of { (:) ρ ρ' -> True : (rec ρ'); [] -> False : (rec' pgm) }
