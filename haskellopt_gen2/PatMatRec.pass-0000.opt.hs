-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  27
-- Incl. one-shot:   0
-- Case reductions:  110
-- Field reductions: 38
-- Case commutings:  51
-- Total nodes: 576; Boxes: 117; Branches: 136
-- Apps: 79; Lams: 8

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module PatMatRec (t2_,t2'0'5,t2'1'5,t2'1,t2'0,t2,t1_,t1'2,t1'1,t1'0,t1,t0_,t0'1,t0'0,t0) where

import GHC.List
import GHC.Num
import GHC.Prim
import GHC.Tuple
import GHC.Types

t2_ = \ds -> 
  let rec π π' = (π' - π) : (rec π' π) in
  (case ds of { (:) ρ ρ' -> (case ρ' of { (:) ρ'2 ρ'3 -> (case ρ'3 of { [] -> ρ - ρ'2; _ -> (666::Int) }); _ -> (666::Int) }); _ -> (666::Int) }) : (case ds of { (:) ρ'4 ρ'5 -> (case ρ'5 of { (:) ρ'6 ρ'7 -> (case ρ'7 of { [] -> (ρ'6 - ρ'4) : (rec ρ'6 ρ'4); _ -> [] }); _ -> [] }); _ -> [] })

t2'0'5 = 
  let rec π π' = (π' - π) : (rec π' π) in
  GHC.List.take (5::Int) (((0::Int) - (1::Int)) : (rec (0::Int) (1::Int)))

t2'1'5 = ((0::Int) - (1::Int)) * ((1::Int) - (0::Int))

t2'1 = (,) ((0::Int) - (1::Int)) ((1::Int) - (0::Int))

t2'0 = 
  let rec π π' = (π' - π) : (rec π' π) in
  ((0::Int) - (1::Int)) : (rec (0::Int) (1::Int))

t2 = \ds -> 
  let rec π π' = (π' - π) : (rec π' π) in
  case ds of { (:) ρ ρ' -> (case ρ' of { (:) ρ'2 ρ'3 -> (case ρ'3 of { [] -> ρ - ρ'2; _ -> (666::Int) }) : (case ρ'3 of { [] -> (rec ρ ρ'2); _ -> [] }); _ -> (666::Int) : [] }); _ -> (666::Int) : [] }

t1_ = \ds -> let
  _0 = (let (:) arg _ = ds in arg) + (1::Int)
  rec _1 = _1 : (rec (_1 + (1::Int)))
  in (case ds of { (:) ρ ρ' -> (case ρ' of { [] -> ρ; _ -> (666::Int) }); _ -> (666::Int) }) : (case ds of { (:) ρ'2 ρ'3 -> (case ρ'3 of { [] -> _0 : (rec (_0 + (1::Int))); _ -> [] }); _ -> [] })

t1'2 = 
  let rec _0 = _0 : (rec (_0 + (1::Int))) in
  (0::Int) : (rec ((0::Int) + (1::Int)))

t1'1 = \x -> 
  let rec _0 = _0 : (rec (_0 + (1::Int))) in
  x : (rec (x + (1::Int)))

t1'0 = \xs -> 
  let rec _0 = _0 : (rec (_0 + (1::Int))) in
  case xs of { (:) ρ ρ' -> (case ρ' of { [] -> ρ; _ -> (666::Int) }) : (case ρ' of { [] -> (rec (ρ + (1::Int))); _ -> [] }); _ -> (666::Int) : [] }

t1 = \ds -> 
  let rec _0 = _0 : (rec (_0 + (1::Int))) in
  case ds of { (:) ρ ρ' -> (case ρ' of { [] -> ρ; _ -> (666::Int) }) : (case ρ' of { [] -> (rec (ρ + (1::Int))); _ -> [] }); _ -> (666::Int) : [] }

t0_ = \ds -> 
  let rec = rec in
  case ds of { (:) ρ ρ' -> (case ρ' of { [] -> rec; _ -> (666::Int) }); _ -> (666::Int) }

t0'1 = (666::Int)

t0'0 = 
  let rec = rec in
  rec

t0 = \ds -> 
  let rec = rec in
  case ds of { (:) ρ ρ' -> (case ρ' of { [] -> rec; _ -> (666::Int) }); _ -> (666::Int) }
