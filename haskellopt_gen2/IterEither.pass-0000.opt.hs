-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  14
-- Incl. one-shot:   0
-- Case reductions:  80
-- Field reductions: 52
-- Case commutings:  206
-- Total nodes: 4114; Boxes: 1522; Branches: 1240
-- Apps: 112; Lams: 3

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
  _8 = (0::Int) + (1::Int)
  _0 = start > (0::Int)
  _fε = case _0 of { True -> _8; False -> (let (,) _ arg = Prelude.undefined in arg) }
  _7 = start - (1::Int)
  _fε' = case _0 of { True -> _7; False -> (let (,) arg _ = Prelude.undefined in arg) }
  _1 = _fε' > (0::Int)
  _9 = _fε + (1::Int)
  _2 = (,) (_fε' - (1::Int)) _9
  _cε = case (,) _7 _8 of { (,) ρ'14 ρ'15 -> (case _1 of { True -> _9; False -> (let (,) _ arg = Prelude.undefined in arg) }) }
  _4 = _cε + (1::Int)
  _ccε = case _2 of { (,) ρ'12 ρ'13 -> (,) (ρ'12 - (1::Int)) _4 }
  _3 = (let (,) arg _ = _2 in arg) > (0::Int)
  rec π π' p p' x π'2 π'3 π'4 = let
        _cε' = case x of { (,) ρ'4 ρ'5 -> (case p' of { True -> p; False -> (let (,) _ arg = Prelude.undefined in arg) }) }
        _6 = _cε' + (1::Int)
        _ccε' = case π of { (,) ρ'10 ρ'11 -> (,) ((let (,) arg _ = π'3 in arg) - (1::Int)) _6 }
        _5 = (let (,) arg _ = π'3 in arg) > (0::Int)
        in case π'4 of { (,) ρ'6 ρ'7 -> (case _5 of { True -> (rec _ccε' _ccε' _6 _5 π' _ccε' _ccε' _ccε'); False -> (case π'2 of { (,) ρ'8 ρ'9 -> _cε' }) }) }
  in case _0 of { True -> (case _1 of { True -> (case _2 of { (,) ρ ρ' -> (case _3 of { True -> (rec _ccε _ccε _4 _3 _2 _ccε _ccε _ccε); False -> (case _2 of { (,) ρ'2 ρ'3 -> _cε }) }) }); False -> _fε }); False -> (0::Int) }

simple9 = let
  _0 = (,) True False
  _cε = case _0 of { (,) ρ'10 ρ'11 -> (,) ρ'11 ρ'10 }
  rec _cfε _cfε' _cfε'2 _cfε'3 _cfε'4 = 
        let _cε' = case _cfε' of { (,) ρ'4 ρ'5 -> (,) (let (,) _ arg = _cfε'3 in arg) (let (,) arg _ = _cfε in arg) } in
        case _cfε'4 of { (,) ρ'6 ρ'7 -> (case (let (,) _ arg = _cfε'3 in arg) of { True -> (rec _cε' _cε' _cε' _cε' _cε'); False -> (case _cfε'2 of { (,) ρ'8 ρ'9 -> (let (,) _ arg = _cfε'3 in arg) }) }) }
  in case _0 of { (,) ρ ρ' -> (case ρ' of { True -> (rec _cε _cε _cε _cε _cε); False -> (case _0 of { (,) ρ'2 ρ'3 -> ρ'3 }) }) }

simple5 = let
  _0 = (5::Int) - (1::Int)
  rec π = case π > (0::Int) of { True -> (rec (π - (1::Int))); False -> π }
  in case (5::Int) > (0::Int) of { True -> (case _0 > (0::Int) of { True -> (rec (_0 - (1::Int))); False -> _0 }); False -> (5::Int) }

simple1 = (20::Int)

loop = \k -> \x -> 
        let rec π k' = case k' π of { Right ρ'4 -> ρ'4; Left ρ'5 -> (rec ρ'5 k') } in
        case k x of { Right ρ -> ρ; Left ρ' -> (case k ρ' of { Right ρ'2 -> ρ'2; Left ρ'3 -> (rec ρ'3 k) }) }
