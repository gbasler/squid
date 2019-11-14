-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  10
-- Incl. one-shot:   0
-- Case reductions:  11
-- Field reductions: 15
-- Case commutings:  2
-- Total nodes: 435; Boxes: 113; Branches: 70
-- Apps: 101; Lams: 10

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Main (main,max_mod,k,max3,maxMaybe,max3_manual) where

import Control.DeepSeq
import Control.Exception.Base
import Criterion.Main
import Criterion.Measurement.Types
import GHC.Base
import GHC.CString
import GHC.Classes
import GHC.Enum
import GHC.Maybe
import GHC.Num
import GHC.Real
import GHC.Tuple
import GHC.Types

main = 
  let _0 = GHC.Enum.enumFromTo (0::Int) ((1000::Int) * (1000::Int)) in
  Criterion.Main.defaultMain (Criterion.Measurement.Types.bgroup (GHC.CString.unpackCString# "maxMaybe"#) ((Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "normal"#) $ Criterion.Measurement.Types.nf (map (\v -> let
        _2 = mod v (5::Int)
        ψ = case v > _2 of { True -> v; False -> _2 }
        _1 = mod v (10::Int)
        in case _1 > ψ of { True -> _1; False -> ψ })) _0) : ((Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "manual"#) $ Criterion.Measurement.Types.nf (map (\v' -> let
        _3 = mod v' (10::Int)
        _4 = mod v' (5::Int)
        in case _3 > v' of { True -> (case _3 > _4 of { True -> _3; False -> _4 }); False -> (case v' > _4 of { True -> v'; False -> _4 }) })) _0) : [])) : [])

max_mod = \m -> map (\v -> m (mod v (10::Int)) v (mod v (5::Int)))

k = GHC.Enum.enumFromTo (0::Int) ((1000::Int) * (1000::Int))

max3 = \x -> \y -> \z -> 
              let ψ = case y > z of { True -> y; False -> z } in
              case x > ψ of { True -> x; False -> ψ }

maxMaybe = \ds -> let
  rec _fε = 
        let rec_call' = (rec (let (:) _ arg = _fε in arg)) in
        case (let (:) _ arg = _fε in arg) of { (:) ρ'4 ρ'5 -> (case (let (:) arg _ = _fε in arg) > rec_call' of { True -> (let (:) arg _ = _fε in arg); False -> rec_call' }); [] -> (let (:) arg _ = _fε in arg) }
  rec_call = (rec (let (:) _ arg = ds in arg))
  in case ds of { (:) ρ ρ' -> Just (case ρ' of { (:) ρ'2 ρ'3 -> (case ρ > rec_call of { True -> ρ; False -> rec_call }); [] -> ρ }); [] -> Nothing }

max3_manual = \x -> \y -> \z -> case x > y of { True -> (case x > z of { True -> x; False -> z }); False -> (case y > z of { True -> y; False -> z }) }
