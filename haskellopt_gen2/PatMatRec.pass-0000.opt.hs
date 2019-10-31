-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  28
-- Incl. one-shot:  0
-- Total nodes: 762; Boxes: 124; Branches: 53
-- Apps: 216; Lams: 8

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module PatMatRec (t2_,t2'1,t2'0,t2,t1_,t1'2,t1'1,t1'0,t1,t0_,t0'1,t0'0,t0) where

import GHC.Num
import GHC.Prim
import GHC.Tuple
import GHC.Types

t2_ = \ds -> 
  let rec p = case p of { (_) -> fromInteger 666 : ([]); (:) ρ'8 ρ'9 -> (case ρ'9 of { (_) -> fromInteger 666 : ([]); (:) ρ'10 ρ'11 -> (case ρ'11 of { (_) -> fromInteger 666 : ([]); [] -> (ρ'8 - ρ'10) : (rec (ρ'10 : (ρ'8 : ([])))) }) }) } in
  case ds of { (_) -> fromInteger 666 : ([]); (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666 : ([]); (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666 : ([]); [] -> (ρ - ρ'2) : (case (case case ds of (:) _ arg -> arg of (:) arg _ -> arg) : ((case ds of (:) arg _ -> arg) : ([])) of { (_) -> fromInteger 666 : ([]); (:) ρ'4 ρ'5 -> (case ρ'5 of { (_) -> fromInteger 666 : ([]); (:) ρ'6 ρ'7 -> (case ρ'7 of { (_) -> fromInteger 666 : ([]); [] -> (ρ'4 - ρ'6) : (rec (ρ'6 : (ρ'4 : ([])))) }) }) }) }) }) }

t2'1 = 
  let rec p = case p of { (_) -> fromInteger 666 : ([]); (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666 : ([]); (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666 : ([]); [] -> (ρ - ρ'2) : (rec (ρ'2 : (ρ : ([])))) }) }) } in
  case case 0 : (1 : ([])) of { (_) -> fromInteger 666 : ([]); (:) ρ'4 ρ'5 -> (case ρ'5 of { (_) -> fromInteger 666 : ([]); (:) ρ'6 ρ'7 -> (case ρ'7 of { (_) -> fromInteger 666 : ([]); [] -> (ρ'4 - ρ'6) : (rec (ρ'6 : (ρ'4 : ([])))) }) }) } of { (_) -> (,) 66 666; (:) ρ'8 ρ'9 -> (case ρ'9 of { (_) -> (,) 66 666; (:) ρ'10 ρ'11 -> (,) ρ'8 ρ'10 }) }

t2'0 = 
  let rec p = case p of { (_) -> fromInteger 666 : ([]); (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666 : ([]); (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666 : ([]); [] -> (ρ - ρ'2) : (rec (ρ'2 : (ρ : ([])))) }) }) } in
  case 0 : (1 : ([])) of { (_) -> fromInteger 666 : ([]); (:) ρ'4 ρ'5 -> (case ρ'5 of { (_) -> fromInteger 666 : ([]); (:) ρ'6 ρ'7 -> (case ρ'7 of { (_) -> fromInteger 666 : ([]); [] -> (ρ'4 - ρ'6) : (rec (ρ'6 : (ρ'4 : ([])))) }) }) }

t2 = \ds -> 
  let rec p = case p of { (_) -> fromInteger 666 : ([]); (:) ρ'4 ρ'5 -> (case ρ'5 of { (_) -> fromInteger 666 : ([]); (:) ρ'6 ρ'7 -> (case ρ'7 of { (_) -> fromInteger 666 : ([]); [] -> (ρ'4 - ρ'6) : (rec (ρ'6 : (ρ'4 : ([])))) }) }) } in
  case ds of { (_) -> fromInteger 666 : ([]); (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666 : ([]); (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666 : ([]); [] -> (ρ - ρ'2) : (rec (ρ'2 : (ρ : ([])))) }) }) }

t1_ = \ds -> 
  let rec p = case p of { (_) -> fromInteger 666 : ([]); (:) ρ'4 ρ'5 -> (case ρ'5 of { (_) -> fromInteger 666 : ([]); [] -> ρ'4 : (rec ((ρ'4 + fromInteger 1) : ([]))) }) } in
  case ds of { (_) -> fromInteger 666 : ([]); (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666 : ([]); [] -> ρ : (case ((case ds of (:) arg _ -> arg) + fromInteger 1) : ([]) of { (_) -> fromInteger 666 : ([]); (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666 : ([]); [] -> ρ'2 : (rec ((ρ'2 + fromInteger 1) : ([]))) }) }) }) }

t1'2 = 
  let rec p = case p of { (_) -> fromInteger 666 : ([]); (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666 : ([]); [] -> ρ : (rec ((ρ + fromInteger 1) : ([]))) }) } in
  case 0 : ([]) of { (_) -> fromInteger 666 : ([]); (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666 : ([]); [] -> ρ'2 : (rec ((ρ'2 + fromInteger 1) : ([]))) }) }

t1'1 = \x -> 
  let rec p = case p of { (_) -> fromInteger 666 : ([]); (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666 : ([]); [] -> ρ'2 : (rec ((ρ'2 + fromInteger 1) : ([]))) }) } in
  case x : ([]) of { (_) -> fromInteger 666 : ([]); (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666 : ([]); [] -> ρ : (rec ((ρ + fromInteger 1) : ([]))) }) }

t1'0 = \xs -> 
  let rec p = case p of { (_) -> fromInteger 666 : ([]); (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666 : ([]); [] -> ρ'2 : (rec ((ρ'2 + fromInteger 1) : ([]))) }) } in
  case xs of { (_) -> fromInteger 666 : ([]); (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666 : ([]); [] -> ρ : (rec ((ρ + fromInteger 1) : ([]))) }) }

t1 = \ds -> 
  let rec p = case p of { (_) -> fromInteger 666 : ([]); (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666 : ([]); [] -> ρ'2 : (rec ((ρ'2 + fromInteger 1) : ([]))) }) } in
  case ds of { (_) -> fromInteger 666 : ([]); (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666 : ([]); [] -> ρ : (rec ((ρ + fromInteger 1) : ([]))) }) }

t0_ = \ds -> 
  let rec p = case p of { (_) -> fromInteger 666; (:) ρ'4 ρ'5 -> (case ρ'5 of { (_) -> fromInteger 666; [] -> (rec (ρ'4 : ([]))) }) } in
  case ds of { (_) -> fromInteger 666; (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666; [] -> (case (case ds of (:) arg _ -> arg) : ([]) of { (_) -> fromInteger 666; (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666; [] -> (rec (ρ'2 : ([]))) }) }) }) }

t0'1 = 
  let rec p = case p of { (_) -> fromInteger 666; (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666; [] -> (rec (ρ : ([]))) }) } in
  case ([]) of { (_) -> fromInteger 666; (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666; [] -> (rec (ρ'2 : ([]))) }) }

t0'0 = 
  let rec p = case p of { (_) -> fromInteger 666; (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666; [] -> (rec (ρ : ([]))) }) } in
  case 1 : ([]) of { (_) -> fromInteger 666; (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666; [] -> (rec (ρ'2 : ([]))) }) }

t0 = \ds -> 
  let rec p = case p of { (_) -> fromInteger 666; (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666; [] -> (rec (ρ'2 : ([]))) }) } in
  case ds of { (_) -> fromInteger 666; (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666; [] -> (rec (ρ : ([]))) }) }
