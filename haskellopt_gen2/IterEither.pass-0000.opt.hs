-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  14
-- Incl. one-shot:   0
-- Case reductions:  62
-- Field reductions: 85
-- Case commutings:  82
-- Total nodes: 1735; Boxes: 611; Branches: 576
-- Apps: 87; Lams: 3

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module IterEither (count,simple9,simple5,simple1,loop) where

import Data.Either
import GHC.Classes
import GHC.Num
import GHC.Tuple
import GHC.Types

count = \start -> let
  _2 = (0::Int) + (1::Int)
  rec _cfε _cfε' _cfε'2 _cfε'3 _cfε'4 = 
        let _ccε = (,) (case _cfε of { (,) ρ ρ' -> (let (,) arg _ = _cfε'3 in arg) - (1::Int) }) (case _cfε of { (,) ρ'2 ρ'3 -> (let (,) _ arg = _cfε' in arg) + (1::Int) }) in
        case _cfε'4 of { (,) ρ'4 ρ'5 -> (case (let (,) arg _ = _cfε'3 in arg) > (0::Int) of { True -> (rec _ccε _ccε _ccε _ccε _ccε); False -> (case _cfε'2 of { (,) ρ'6 ρ'7 -> (let (,) _ arg = _cfε' in arg) }) }) }
  _0 = start > (0::Int)
  _fε = case _0 of { True -> start - (1::Int); False -> (let (,) arg _ = Prelude.undefined in arg) }
  _1 = (,) (_fε - (1::Int)) (_2 + (1::Int))
  in case _0 of { True -> (case _fε > (0::Int) of { True -> (rec _1 _1 _1 _1 _1); False -> _2 }); False -> (0::Int) }

simple9 = False

simple5 = 
  let rec π = case π > (0::Int) of { True -> (rec (π - (1::Int))); False -> π } in
  case (5::Int) > (0::Int) of { True -> (rec ((5::Int) - (1::Int))); False -> (5::Int) }

simple1 = (20::Int)

loop = \k -> \x -> 
        let rec π k' = case k' π of { Right ρ'2 -> ρ'2; Left ρ'3 -> (rec ρ'3 k') } in
        case k x of { Right ρ -> ρ; Left ρ' -> (rec ρ' k) }
