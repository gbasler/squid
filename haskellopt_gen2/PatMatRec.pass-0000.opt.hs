-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  28
-- Incl. one-shot:  0
-- Case reductions:  26
-- Field reductions:  27
-- Total nodes: 609; Boxes: 125; Branches: 154
-- Apps: 97; Lams: 8

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module PatMatRec (t2_,t2'1,t2'0,t2,t1_,t1'2,t1'1,t1'0,t1,t0_,t0'1,t0'0,t0) where

import GHC.Num
import GHC.Prim
import GHC.Tuple
import GHC.Types

t2_ = \ds -> 
  let rec π π' = (π' - π) : (rec π' π) in
  case ds of { (:) ρ ρ' -> (case ρ' of { (:) ρ'2 ρ'3 -> (case ρ'3 of { [] -> (ρ - ρ'2) : ((ρ'2 - ρ) : (rec ρ'2 ρ)); _ -> fromInteger 666 : [] }); _ -> fromInteger 666 : [] }); _ -> fromInteger 666 : [] }

t2'1 = (,) (0 - 1) (1 - 0)

t2'0 = 
  let rec π π' = (π' - π) : (rec π' π) in
  (0 - 1) : (rec 0 1)

t2 = \ds -> 
  let rec π π' = (π' - π) : (rec π' π) in
  case ds of { (:) ρ ρ' -> (case ρ' of { (:) ρ'2 ρ'3 -> (case ρ'3 of { [] -> (ρ - ρ'2) : (rec ρ ρ'2); _ -> fromInteger 666 : [] }); _ -> fromInteger 666 : [] }); _ -> fromInteger 666 : [] }

t1_ = \ds -> let
  _0 = (let (:) arg _ = ds in arg) + fromInteger 1
  rec p = p : (rec (p + fromInteger 1))
  in case ds of { (:) ρ ρ' -> (case ρ' of { [] -> ρ : (_0 : (rec (_0 + fromInteger 1))); _ -> fromInteger 666 : [] }); _ -> fromInteger 666 : [] }

t1'2 = 
  let rec p = p : (rec (p + fromInteger 1)) in
  0 : (rec (0 + fromInteger 1))

t1'1 = \x -> 
  let rec p = p : (rec (p + fromInteger 1)) in
  x : (rec (x + fromInteger 1))

t1'0 = \xs -> 
  let rec p = p : (rec (p + fromInteger 1)) in
  case xs of { (:) ρ ρ' -> (case ρ' of { [] -> ρ : (rec (ρ + fromInteger 1)); _ -> fromInteger 666 : [] }); _ -> fromInteger 666 : [] }

t1 = \ds -> 
  let rec p = p : (rec (p + fromInteger 1)) in
  case ds of { (:) ρ ρ' -> (case ρ' of { [] -> ρ : (rec (ρ + fromInteger 1)); _ -> fromInteger 666 : [] }); _ -> fromInteger 666 : [] }

t0_ = \ds -> 
  let rec = rec in
  case ds of { (:) ρ ρ' -> (case ρ' of { [] -> rec; _ -> fromInteger 666 }); _ -> fromInteger 666 }

t0'1 = fromInteger 666

t0'0 = 
  let rec = rec in
  rec

t0 = \ds -> 
  let rec = rec in
  case ds of { (:) ρ ρ' -> (case ρ' of { [] -> rec; _ -> fromInteger 666 }); _ -> fromInteger 666 }
