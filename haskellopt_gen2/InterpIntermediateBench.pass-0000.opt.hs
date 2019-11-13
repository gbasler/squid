-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  15
-- Incl. one-shot:   1
-- Case reductions:  16
-- Field reductions: 28
-- Case commutings:  0
-- Total nodes: 160; Boxes: 40; Branches: 32
-- Apps: 34; Lams: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Main (main,k,src) where

import Criterion.Main
import Criterion.Measurement.Types
import GHC.Base
import GHC.CString
import GHC.Classes
import GHC.Num
import GHC.Tuple
import GHC.Types

main = Criterion.Main.defaultMain (Criterion.Measurement.Types.bgroup (GHC.CString.unpackCString# "interp"#) ((Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "normal"#) $ Criterion.Measurement.Types.whnf (\x -> let
  rec'11 = rec'11
  rec'5 y'2 x'4 = let
        rec'12 = rec'12
        _5 = y'2 + (1::Int)
        _6 = x'4 + (1::Int)
        in case (0::Int) == (0::Int) of { True -> (
              let rec'13 = rec'13 in
              case (2::Int) == (0::Int) of { True -> _5 + (1::Int); False -> (case (2::Int) == (2::Int) of { True -> (case _6 > (0::Int) of { True -> (rec'5 _5 _6); False -> _5 }); False -> rec'13 }) }); False -> (case (0::Int) == (2::Int) of { True -> (case x'4 > (0::Int) of { True -> (rec'5 y'2 x'4); False -> (
              let rec'14 = rec'14 in
              case (2::Int) == (0::Int) of { True -> y'2 + (1::Int); False -> (case (2::Int) == (2::Int) of { True -> (case x'4 > (0::Int) of { True -> (rec'5 y'2 x'4); False -> y'2 }); False -> rec'14 }) }) }); False -> rec'12 }) }
  _0 = (0::Int) + (1::Int)
  _1 = x + (1::Int)
  in case (0::Int) == (0::Int) of { True -> (let
        rec = rec
        rec' y x' = 
              let rec'2 = rec'2 in
              case (0::Int) == (0::Int) of { True -> (let
                    _2 = y + (1::Int)
                    x'2 = x' + (1::Int)
                    rec'3 = rec'3
                    in case (2::Int) == (0::Int) of { True -> _2 + (1::Int); False -> (case (2::Int) == (2::Int) of { True -> (case x'2 > (0::Int) of { True -> (rec' _2 x'2); False -> _2 }); False -> rec'3 }) }); False -> (case (0::Int) == (2::Int) of { True -> (case x' > (0::Int) of { True -> (rec' y x'); False -> (
                    let rec'4 = rec'4 in
                    case (2::Int) == (0::Int) of { True -> y + (1::Int); False -> (case (2::Int) == (2::Int) of { True -> (case x' > (0::Int) of { True -> (rec' y x'); False -> y }); False -> rec'4 }) }) }); False -> rec'2 }) }
        in case (2::Int) == (0::Int) of { True -> _0 + (1::Int); False -> (case (2::Int) == (2::Int) of { True -> (case _1 > (0::Int) of { True -> (rec' _0 _1); False -> _0 }); False -> rec }) }); False -> (case (0::Int) == (2::Int) of { True -> (case x > (0::Int) of { True -> (rec'5 (0::Int) x); False -> (let
        rec'6 = rec'6
        rec'7 y' x'3 = let
              rec'8 = rec'8
              _4 = x'3 + (1::Int)
              in case (0::Int) == (0::Int) of { True -> (let
                    _3 = y' + (1::Int)
                    rec'9 = rec'9
                    in case (2::Int) == (0::Int) of { True -> _3 + (1::Int); False -> (case (2::Int) == (2::Int) of { True -> (case _4 > (0::Int) of { True -> (rec'7 _3 _4); False -> _3 }); False -> rec'9 }) }); False -> (case (0::Int) == (2::Int) of { True -> (case x'3 > (0::Int) of { True -> (rec'7 y' x'3); False -> (
                    let rec'10 = rec'10 in
                    case (2::Int) == (0::Int) of { True -> y' + (1::Int); False -> (case (2::Int) == (2::Int) of { True -> (case x'3 > (0::Int) of { True -> (rec'7 y' x'3); False -> y' }); False -> rec'10 }) }) }); False -> rec'8 }) }
        in case (2::Int) == (0::Int) of { True -> (0::Int) + (1::Int); False -> (case (2::Int) == (2::Int) of { True -> (case x > (0::Int) of { True -> (rec'7 (0::Int) x); False -> (0::Int) }); False -> rec'6 }) }) }); False -> rec'11 }) }) (negate ((1000::Int) * (100::Int)))) : []) : [])

k = negate ((1000::Int) * (100::Int))

src = (0::Int) : ((2::Int) : [])
