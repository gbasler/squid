-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  5
-- Incl. one-shot:   0
-- Case reductions:  2
-- Field reductions: 2
-- Case commutings:  2
-- Total nodes: 174; Boxes: 33; Branches: 10
-- Apps: 48; Lams: 4

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Main (main,maxMaybe,maxMaybe_manual,k) where

import Control.DeepSeq
import Criterion.Main
import Criterion.Measurement.Types
import GHC.Base
import GHC.CString
import GHC.Classes
import GHC.Enum
import GHC.Maybe
import GHC.Num
import GHC.Types

main = 
  let _0 = GHC.Enum.enumFromTo (0::Int) ((1000::Int) * (1000::Int)) in
  Criterion.Main.defaultMain (Criterion.Measurement.Types.bgroup (GHC.CString.unpackCString# "maxMaybe"#) ((Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "normal"#) $ Criterion.Measurement.Types.nf (\ds -> let
        rec π = 
              let rec_call' = (rec (let (:) _ arg = π in arg)) in
              case (let (:) _ arg = π in arg) of { (:) ρ'4 ρ'5 -> (case (let (:) arg _ = π in arg) > rec_call' of { True -> (let (:) arg _ = π in arg); False -> rec_call' }); [] -> (let (:) arg _ = π in arg) }
        rec_call = (rec (let (:) _ arg = ds in arg))
        in case ds of { (:) ρ ρ' -> Just (case ρ' of { (:) ρ'2 ρ'3 -> (case ρ > rec_call of { True -> ρ; False -> rec_call }); [] -> ρ }); [] -> Nothing }) _0) : ((Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "manual"#) $ Criterion.Measurement.Types.nf (\ds' -> 
        let rec' π' x = case π' of { (:) ρ'10 ρ'11 -> (rec' ρ'11 (case ρ'10 > x of { True -> ρ'10; False -> x })); [] -> x } in
        case ds' of { (:) ρ'6 ρ'7 -> Just (case ρ'7 of { (:) ρ'8 ρ'9 -> (rec' ρ'9 (case ρ'8 > ρ'6 of { True -> ρ'8; False -> ρ'6 })); [] -> ρ'6 }); [] -> Nothing }) _0) : [])) : [])

maxMaybe = \ds -> let
  rec π = 
        let rec_call' = (rec (let (:) _ arg = π in arg)) in
        case (let (:) _ arg = π in arg) of { (:) ρ'4 ρ'5 -> (case (let (:) arg _ = π in arg) > rec_call' of { True -> (let (:) arg _ = π in arg); False -> rec_call' }); [] -> (let (:) arg _ = π in arg) }
  rec_call = (rec (let (:) _ arg = ds in arg))
  in case ds of { (:) ρ ρ' -> Just (case ρ' of { (:) ρ'2 ρ'3 -> (case ρ > rec_call of { True -> ρ; False -> rec_call }); [] -> ρ }); [] -> Nothing }

maxMaybe_manual = \ds -> 
  let rec π x = case π of { (:) ρ'4 ρ'5 -> (rec ρ'5 (case ρ'4 > x of { True -> ρ'4; False -> x })); [] -> x } in
  case ds of { (:) ρ ρ' -> Just (case ρ' of { (:) ρ'2 ρ'3 -> (rec ρ'3 (case ρ'2 > ρ of { True -> ρ'2; False -> ρ })); [] -> ρ }); [] -> Nothing }

k = GHC.Enum.enumFromTo (0::Int) ((1000::Int) * (1000::Int))
