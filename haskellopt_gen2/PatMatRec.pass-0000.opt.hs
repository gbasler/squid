-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  26
-- Incl. one-shot:  0
-- Case reductions:  24
-- Field reductions:  24
-- Total nodes: 578; Boxes: 116; Branches: 148
-- Apps: 93; Lams: 8

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module PatMatRec (t2_,t2'0,t2,t1_,t1'2,t1'1,t1'0,t1,t0_,t0'1,t0'0,t0) where

import GHC.Num
import GHC.Prim
import GHC.Types

t2_ = \ds -> let
  π = case ds of (:) _ arg -> arg
  π' = case ds of (:) arg _ -> arg
  π'2 = case π of (:) arg _ -> arg
  rec π'3 π'4 = (π'4 - π'3) : (rec π'4 π'3)
  in case ds of { (_) -> fromInteger 666 : ([]); (:) ρ ρ' -> (case π of { (_) -> fromInteger 666 : ([]); (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666 : ([]); [] -> (π' - π'2) : ((π'2 - π') : (rec π'2 π')) }) }) }

t2'0 = 
  let rec π π' = (π' - π) : (rec π' π) in
  (0 - 1) : (rec 0 1)

t2 = \ds -> let
  _fε = case ds of (:) _ arg -> arg
  _fε' = case ds of (:) arg _ -> arg
  _fε'2 = case _fε of (:) arg _ -> arg
  rec π π' = (π' - π) : (rec π' π)
  in case ds of { (_) -> fromInteger 666 : ([]); (:) ρ ρ' -> (case _fε of { (_) -> fromInteger 666 : ([]); (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666 : ([]); [] -> (_fε' - _fε'2) : (rec _fε' _fε'2) }) }) }

t1_ = \ds -> let
  π = case ds of (:) arg _ -> arg
  _0 = π + fromInteger 1
  rec p = p : (rec (p + fromInteger 1))
  in case ds of { (_) -> fromInteger 666 : ([]); (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666 : ([]); [] -> π : (_0 : (rec (_0 + fromInteger 1))) }) }

t1'2 = 
  let rec p = p : (rec (p + fromInteger 1)) in
  0 : (rec (0 + fromInteger 1))

t1'1 = \x -> 
  let rec p = p : (rec (p + fromInteger 1)) in
  x : (rec (x + fromInteger 1))

t1'0 = \xs -> let
  _fε = case xs of (:) arg _ -> arg
  rec p = p : (rec (p + fromInteger 1))
  in case xs of { (_) -> fromInteger 666 : ([]); (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666 : ([]); [] -> _fε : (rec (_fε + fromInteger 1)) }) }

t1 = \ds -> let
  _fε = case ds of (:) arg _ -> arg
  rec p = p : (rec (p + fromInteger 1))
  in case ds of { (_) -> fromInteger 666 : ([]); (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666 : ([]); [] -> _fε : (rec (_fε + fromInteger 1)) }) }

t0_ = \ds -> 
  let rec = rec in
  case ds of { (_) -> fromInteger 666; (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666; [] -> rec }) }

t0'1 = fromInteger 666

t0'0 = 
  let rec = rec in
  rec

t0 = \ds -> 
  let rec = rec in
  case ds of { (_) -> fromInteger 666; (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666; [] -> rec }) }
