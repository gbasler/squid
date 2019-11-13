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
  let rec = False : (True : (True : (True : rec))) in
  GHC.List.take (10::Int) (True : (True : (True : rec)))

test3 = 
  let rec = False : (True : (True : (True : rec))) in
  True : (True : (True : rec))

test2_10 = 
  let rec = False : (True : (True : rec)) in
  GHC.List.take (10::Int) (True : (True : rec))

test2 = 
  let rec = False : (True : (True : rec)) in
  True : (True : rec)

test1 = 
  let rec = False : (True : rec) in
  True : rec

test0 = 
  let rec = False : rec in
  False : rec

exec = \pgm -> let
  rec' pgm'4 pgm'5 = 
        let rec'3 _fε' pgm'6 = case _fε' of { (:) ρ'6 ρ'7 -> True : (rec'3 ρ'7 pgm'6); [] -> False : (rec' pgm'6 pgm'6) } in
        case pgm'4 of { (:) ρ'8 ρ'9 -> True : (rec'3 ρ'9 pgm'5); [] -> False : (rec' pgm'5 pgm'5) }
  rec _fε pgm' = 
        let rec'2 pgm'2 pgm'3 = case pgm'2 of { (:) ρ'2 ρ'3 -> True : (rec ρ'3 pgm'3); [] -> False : (rec'2 pgm'3 pgm'3) } in
        case _fε of { (:) ρ'4 ρ'5 -> True : (rec ρ'5 pgm'); [] -> False : (rec'2 pgm' pgm') }
  in case pgm of { (:) ρ ρ' -> True : (rec ρ' pgm); [] -> False : (rec' pgm pgm) }
