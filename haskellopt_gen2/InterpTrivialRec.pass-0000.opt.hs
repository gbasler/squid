-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  7
-- Incl. one-shot:  0
-- Case reductions:  20
-- Field reductions:  20
-- Total nodes: 237; Boxes: 46; Branches: 130
-- Apps: 20; Lams: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module InterpTrivialRec (test3,test2,test1,test0,exec) where

import GHC.Tuple
import GHC.Types

test3 = 
  let rec = True : (True : (True : (False : rec))) in
  True : (True : (True : (False : rec)))

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
  rec' pgm'2 = 
        let rec'3 _fε' = case _fε' of { (:) ρ'6 ρ'7 -> True : (rec'3 ρ'7); [] -> False : (rec' pgm) } in
        case pgm'2 of { (:) ρ'8 ρ'9 -> True : (rec'3 ρ'9); [] -> False : (rec' pgm) }
  rec _fε = 
        let rec'2 pgm' = case pgm' of { (:) ρ'2 ρ'3 -> True : (rec ρ'3); [] -> False : (rec'2 pgm) } in
        case _fε of { (:) ρ'4 ρ'5 -> True : (rec ρ'5); [] -> False : (rec'2 pgm) }
  in case pgm of { (:) ρ ρ' -> True : (rec ρ'); [] -> False : (rec' pgm) }
