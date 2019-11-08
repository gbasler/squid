-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  5
-- Incl. one-shot:   0
-- Case reductions:  8
-- Field reductions: 9
-- Case commutings:  13
-- Total nodes: 135; Boxes: 44; Branches: 38
-- Apps: 15; Lams: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module IterEither (count) where

import Data.Either
import GHC.Classes
import GHC.Num
import GHC.Tuple
import GHC.Types

count = \start -> let
  _0 = start > (0::Int)
  _4 = start - (1::Int)
  _fε' = case _0 of { True -> _4; False -> (let (,) arg _ = Prelude.undefined in arg) }
  _1 = _fε' > (0::Int)
  _5 = (0::Int) + (1::Int)
  _fε = case _0 of { True -> _5; False -> (let (,) _ arg = Prelude.undefined in arg) }
  _3 = _fε + (1::Int)
  _2 = (,) (_fε' - (1::Int)) _3
  rec π π' p p' ds π'2 π'3 π'4 = let
        _6 = (let (,) arg _ = π'3 in arg) > (0::Int)
        _cfε' = case ds of { (,) ρ'14 ρ'15 -> (case p' of { True -> p; False -> (let (,) _ arg = Prelude.undefined in arg) }) }
        _9 = _cfε' + (1::Int)
        _ccε = case π' of { (,) ρ'12 ρ'13 -> (,) ((let (,) arg _ = π'3 in arg) - (1::Int)) _9 }
        _7 = (let (,) arg _ = _ccε in arg) > (0::Int)
        _cfε = case π of { (,) ρ'10 ρ'11 -> (case _6 of { True -> _9; False -> (let (,) _ arg = Prelude.undefined in arg) }) }
        _8 = _cfε + (1::Int)
        _ccε' = case _ccε of { (,) ρ'8 ρ'9 -> (,) (ρ'8 - (1::Int)) _8 }
        in case π'4 of { (,) ρ ρ' -> (case _6 of { True -> (case _ccε of { (,) ρ'2 ρ'3 -> (case _7 of { True -> (rec _ccε' _ccε' _8 _7 _ccε _ccε' _ccε' _ccε'); False -> (case _ccε of { (,) ρ'4 ρ'5 -> _cfε }) }) }); False -> (case π'2 of { (,) ρ'6 ρ'7 -> _cfε' }) }) }
  in case _0 of { True -> (case _1 of { True -> (rec _2 _2 _3 _1 ((,) _4 _5) _2 _2 _2); False -> _fε }); False -> (0::Int) }
