-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  14
-- Incl. one-shot:   0
-- Case reductions:  66
-- Field reductions: 52
-- Case commutings:  85
-- Total nodes: 2898; Boxes: 1034; Branches: 912
-- Apps: 164; Lams: 3

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
  rec π π' _6 _7 x π'2 π'3 π'4 = let
        _cε = case x of { (,) ρ ρ' -> (case _7 of { True -> _6; False -> (let (,) _ arg = Prelude.undefined in arg) }) }
        _9 = _cε + (1::Int)
        _ccε = (,) (case π' of { (,) ρ'6 ρ'7 -> (let (,) arg _ = π'3 in arg) - (1::Int) }) (case π' of { (,) ρ'8 ρ'9 -> _9 })
        _8 = (let (,) arg _ = π'3 in arg) > (0::Int)
        in case π'4 of { (,) ρ'2 ρ'3 -> (case _8 of { True -> (rec _ccε _ccε _9 _8 π _ccε _ccε _ccε); False -> (case π'2 of { (,) ρ'4 ρ'5 -> _cε }) }) }
  _0 = start > (0::Int)
  _5 = (0::Int) + (1::Int)
  _fε = case _0 of { True -> _5; False -> (let (,) _ arg = Prelude.undefined in arg) }
  _3 = _fε + (1::Int)
  _4 = start - (1::Int)
  _fε' = case _0 of { True -> _4; False -> (let (,) arg _ = Prelude.undefined in arg) }
  _2 = (,) (_fε' - (1::Int)) _3
  _1 = _fε' > (0::Int)
  in case _0 of { True -> (case _1 of { True -> (rec _2 _2 _3 _1 ((,) _4 _5) _2 _2 _2); False -> _fε }); False -> (0::Int) }

simple9 = let
  _0 = (,) True False
  _cε = case _0 of { (,) ρ'10 ρ'11 -> (,) ρ'11 ρ'10 }
  rec _cfε _cfε' _cfε'2 _cfε'3 _cfε'4 = 
        let _cε' = case _cfε' of { (,) ρ'4 ρ'5 -> (,) (let (,) _ arg = _cfε'3 in arg) (let (,) arg _ = _cfε in arg) } in
        case _cfε'4 of { (,) ρ'6 ρ'7 -> (case (let (,) _ arg = _cfε'3 in arg) of { True -> (rec _cε' _cε' _cε' _cε' _cε'); False -> (case _cfε'2 of { (,) ρ'8 ρ'9 -> (let (,) _ arg = _cfε'3 in arg) }) }) }
  in case _0 of { (,) ρ ρ' -> (case ρ' of { True -> (rec _cε _cε _cε _cε _cε); False -> (case _0 of { (,) ρ'2 ρ'3 -> ρ'3 }) }) }

simple5 = 
  let rec π = case π > (0::Int) of { True -> (rec (π - (1::Int))); False -> π } in
  case (5::Int) > (0::Int) of { True -> (rec ((5::Int) - (1::Int))); False -> (5::Int) }

simple1 = (20::Int)

loop = \k -> \x -> 
        let rec π k' = case k' π of { Right ρ'2 -> ρ'2; Left ρ'3 -> (rec ρ'3 k') } in
        case k x of { Right ρ -> ρ; Left ρ' -> (rec ρ' k) }
