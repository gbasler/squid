-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  14
-- Incl. one-shot:   0
-- Case reductions:  80
-- Field reductions: 52
-- Case commutings:  206
-- Total nodes: 5320; Boxes: 1997; Branches: 1645
-- Apps: 140; Lams: 3

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
  _0 = start > (0::Int)
  _5 = (0::Int) + (1::Int)
  _fε = case _0 of { True -> _5; False -> (let (,) _ arg = Prelude.undefined in arg) }
  _4 = start - (1::Int)
  _fε' = case _0 of { True -> _4; False -> (let (,) arg _ = Prelude.undefined in arg) }
  _1 = _fε' > (0::Int)
  _3 = _fε + (1::Int)
  _2 = (,) (_fε' - (1::Int)) _3
  rec π π' p p' ds π'2 π'3 π'4 = let
        _cε = case ds of { (,) ρ ρ' -> (case p' of { True -> p; False -> (let (,) _ arg = Prelude.undefined in arg) }) }
        _9 = _cε + (1::Int)
        _ccε = case π' of { (,) ρ'14 ρ'15 -> (,) ((let (,) arg _ = π'3 in arg) - (1::Int)) _9 }
        _6 = (let (,) arg _ = π'3 in arg) > (0::Int)
        _cε' = case π of { (,) ρ'12 ρ'13 -> (case _6 of { True -> _9; False -> (let (,) _ arg = Prelude.undefined in arg) }) }
        _7 = (let (,) arg _ = _ccε in arg) > (0::Int)
        _8 = _cε' + (1::Int)
        _ccε' = case _ccε of { (,) ρ'10 ρ'11 -> (,) (ρ'10 - (1::Int)) _8 }
        in case π'4 of { (,) ρ'2 ρ'3 -> (case _6 of { True -> (case _ccε of { (,) ρ'4 ρ'5 -> (case _7 of { True -> (rec _ccε' _ccε' _8 _7 _ccε _ccε' _ccε' _ccε'); False -> (case _ccε of { (,) ρ'6 ρ'7 -> _cε' }) }) }); False -> (case π'2 of { (,) ρ'8 ρ'9 -> _cε }) }) }
  in case _0 of { True -> (case _1 of { True -> (rec _2 _2 _3 _1 ((,) _4 _5) _2 _2 _2); False -> _fε }); False -> (0::Int) }

simple9 = let
  _0 = (,) True False
  rec _cfε _cfε' _cfε'2 _cfε'3 _cfε'4 = let
        _cε = case _cfε'2 of { (,) ρ ρ' -> (,) (let (,) _ arg = _cfε'3 in arg) (let (,) arg _ = _cfε' in arg) }
        _cε' = case _cε of { (,) ρ'10 ρ'11 -> (,) ρ'11 ρ'10 }
        in case _cfε'4 of { (,) ρ'2 ρ'3 -> (case (let (,) _ arg = _cfε'3 in arg) of { True -> (case _cε of { (,) ρ'4 ρ'5 -> (case ρ'5 of { True -> (rec _cε' _cε' _cε' _cε' _cε'); False -> (case _cε of { (,) ρ'6 ρ'7 -> ρ'7 }) }) }); False -> (case _cfε of { (,) ρ'8 ρ'9 -> (let (,) _ arg = _cfε'3 in arg) }) }) }
  in (rec _0 _0 _0 _0 _0)

simple5 = 
  let rec π = 
        let _0 = π - (1::Int) in
        case π > (0::Int) of { True -> (case _0 > (0::Int) of { True -> (rec (_0 - (1::Int))); False -> _0 }); False -> π } in
  case (5::Int) > (0::Int) of { True -> (rec ((5::Int) - (1::Int))); False -> (5::Int) }

simple1 = (20::Int)

loop = \k -> \x -> 
        let rec π = case k π of { Right ρ'2 -> ρ'2; Left ρ'3 -> (case k ρ'3 of { Right ρ'4 -> ρ'4; Left ρ'5 -> (rec ρ'5) }) } in
        case k x of { Right ρ -> ρ; Left ρ' -> (rec ρ') }
