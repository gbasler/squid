-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  36
-- Incl. one-shot:   1
-- Case reductions:  46
-- Field reductions: 56
-- Case commutings:  11
-- Total nodes: 848; Boxes: 95; Branches: 181
-- Apps: 134; Lams: 16

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module PatMat (f0'f1',f0'f1,f1'2,f1'1,f1'0,f1,f0'4,f0'3,f0'2,f0'1,f0'0,f0,f2'0,slt0,t1'0,t0'0,u0_0,u1_0,u1,u0,t1'1,t1,t0,t'ls,slt1,f2,orZero,e1'1,e1'0,e1,e0'3,e0'2,e0'1,e0'0,e0) where

import Data.Foldable
import GHC.Base
import GHC.Classes
import GHC.Maybe
import GHC.Num
import GHC.Prim
import GHC.Tuple
import GHC.Types

f0'f1' = case (24::Int) > (0::Int) of { True -> Just ((24::Int) + (1::Int)); False -> Just (0::Int) }

f0'f1 = case (42::Int) > (0::Int) of { True -> Just ((42::Int) + (1::Int)); False -> Just (0::Int) }

f1'2 = case (5::Int) > (0::Int) of { True -> (5::Int); False -> (0::Int) }

f1'1 = case (5::Int) > (0::Int) of { True -> True; False -> False }

f1'0 = case (4::Int) > (0::Int) of { True -> Just (4::Int); False -> Nothing }

f1 = \x -> case x > (0::Int) of { True -> Just x; False -> Nothing }

f0'4 = \x -> 
  let _0 = Just ((case x of { Just ρ' -> ρ' + (1::Int); Nothing -> (0::Int) }) + (1::Int)) in
  case x of { Just ρ -> _0; Nothing -> _0 }

f0'3 = Just ((0::Int) + (1::Int))

f0'2 = Just (((3::Int) + (1::Int)) + (1::Int))

f0'1 = Just (0::Int)

f0'0 = Just ((2::Int) + (1::Int))

f0 = \ds -> case ds of { Just ρ -> Just (ρ + (1::Int)); Nothing -> Just (0::Int) }

f2'0 = ((1::Int) + (2::Int)) + (3::Int)

slt0 = \x -> let
  _1 = (Data.Foldable.sum . map (\x' -> x' * (2::Int))) (map (\c' -> c' + (x + (1::Int))) [])
  _0 = Data.Foldable.sum (map (\c -> c + x) [])
  in (,) ((_0 * _0) + (1::Int)) ((_1 * _1) + (1::Int))

t1'0 = (((1::Int) + (2::Int)) + (3::Int)) + (4::Int)

t0'0 = (((1::Int) + (2::Int)) + (3::Int)) + (4::Int)

u0_0 = (1::Int) + (2::Int)

u1_0 = (((1::Int) + (2::Int)) + (3::Int)) + (4::Int)

u1 = \ds -> case ds of { (,) ρ ρ' -> (case ρ' of { (,) ρ'2 ρ'3 -> (case ρ'3 of { (,) ρ'4 ρ'5 -> (case ρ'5 of { (,) ρ'6 ρ'7 -> (case ρ'7 of { () -> ((ρ + ρ'2) + ρ'4) + ρ'6 }) }) }) }) }

u0 = \ds -> case ds of { (,) ρ ρ' -> ρ + ρ' }

t1'1 = \xs -> case xs of { (:) ρ ρ' -> (case ρ' of { (:) ρ'2 ρ'3 -> (case ρ'3 of { [] -> (((5::Int) + (6::Int)) + ρ) + ρ'2; _ -> (666::Int) }); _ -> (666::Int) }); _ -> (666::Int) }

t1 = \ds -> case ds of { (:) ρ ρ' -> (case ρ' of { (:) ρ'2 ρ'3 -> (case ρ'3 of { (:) ρ'4 ρ'5 -> (case ρ'5 of { (:) ρ'6 ρ'7 -> (case ρ'7 of { [] -> ((ρ + ρ'2) + ρ'4) + ρ'6; _ -> (666::Int) }); _ -> (666::Int) }); _ -> (666::Int) }); _ -> (666::Int) }); _ -> (666::Int) }

t0 = \ds -> case ds of { (:) ρ ρ' -> (case ρ' of { (:) ρ'2 ρ'3 -> (case ρ'3 of { (:) ρ'4 ρ'5 -> (case ρ'5 of { (:) ρ'6 ρ'7 -> (case ρ'7 of { [] -> ((ρ + ρ'2) + ρ'4) + ρ'6; _ -> (666::Int) }); _ -> (666::Int) }); _ -> (666::Int) }); _ -> (666::Int) }); _ -> (666::Int) }

t'ls = (1::Int) : ((2::Int) : ((3::Int) : ((4::Int) : [])))

slt1 = \ls -> map (\c -> (0::Int)) (map (\c' -> (0::Int)) ls)

f2 = \ds -> case ds of { (,,) ρ ρ' ρ'2 -> (ρ + ρ') + ρ'2 }

orZero = \ds -> case ds of { Just ρ -> ρ; Nothing -> (0::Int) }

e1'1 = (0::Int)

e1'0 = (0::Int)

e1 = Nothing

e0'3 = (1::Int)

e0'2 = (2::Int) + (1::Int)

e0'1 = (2::Int) + (1::Int)

e0'0 = (2::Int) + (1::Int)

e0 = Just (2::Int)
