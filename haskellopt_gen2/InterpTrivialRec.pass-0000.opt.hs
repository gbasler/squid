-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  7
-- Incl. one-shot:   0
-- Case reductions:  39
-- Field reductions: 20
-- Case commutings:  0
-- Total nodes: 734; Boxes: 200; Branches: 330
-- Apps: 26; Lams: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module InterpTrivialRec (test3_10,test3,test2_10,test2,test1,test0,exec) where

import GHC.List
import GHC.Tuple
import GHC.Types

test3_10 = 
  let rec = True : (True : (True : (False : rec))) in
  GHC.List.take (10::Int) (True : (True : (True : (False : rec))))

test3 = 
  let rec = True : (True : (True : (False : rec))) in
  True : (True : (True : (False : rec)))

test2_10 = 
  let rec = True : (True : (False : rec)) in
  GHC.List.take (10::Int) (True : (True : (False : rec)))

test2 = 
  let rec = True : (True : (False : rec)) in
  True : (True : (False : rec))

test1 = 
  let rec = True : (False : rec) in
  True : (False : rec)

test0 = 
  let rec = False : rec in
  False : rec

exec = \pgm -> let
  rec' pgm'4 pgm'5 = 
        let rec'3 _fε' pgm'6 = (case _fε' of { (:) ρ'12 ρ'13 -> True; [] -> False }) : (case _fε' of { (:) ρ'14 ρ'15 -> (rec'3 ρ'15 pgm'6); [] -> (rec' pgm'6 pgm'6) }) in
        (case pgm'4 of { (:) ρ'16 ρ'17 -> True; [] -> False }) : (case pgm'4 of { (:) ρ'18 ρ'19 -> (rec'3 ρ'19 pgm'5); [] -> (rec' pgm'5 pgm'5) })
  rec _fε pgm' = 
        let rec'2 pgm'2 pgm'3 = (case pgm'2 of { (:) ρ'4 ρ'5 -> True; [] -> False }) : (case pgm'2 of { (:) ρ'6 ρ'7 -> (rec ρ'7 pgm'3); [] -> (rec'2 pgm'3 pgm'3) }) in
        (case _fε of { (:) ρ'8 ρ'9 -> True; [] -> False }) : (case _fε of { (:) ρ'10 ρ'11 -> (rec ρ'11 pgm'); [] -> (rec'2 pgm' pgm') })
  in (case pgm of { (:) ρ ρ' -> True; [] -> False }) : (case pgm of { (:) ρ'2 ρ'3 -> (rec ρ'3 pgm); [] -> (rec' pgm pgm) })
