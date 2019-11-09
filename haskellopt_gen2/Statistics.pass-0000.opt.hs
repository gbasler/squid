-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  5
-- Incl. one-shot:   0
-- Case reductions:  15
-- Field reductions: 16
-- Case commutings:  9
-- Total nodes: 160; Boxes: 58; Branches: 30
-- Apps: 12; Lams: 4

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}
{-# LANGUAGE FlexibleContexts  #-}

module Statistics (lastWeird,lastMaybe,maxMaybe1,maxTest'0,maxMaybe0) where

import Data.Tuple.Select
import GHC.Classes
import GHC.Maybe
import GHC.Num
import GHC.Types

lastWeird = \ds -> let
  rec π = 
        let rec_call' = (rec (let (:) _ arg = π in arg)) in
        (,,) (case (let (:) _ arg = π in arg) of { (:) ρ'4 ρ'5 -> (case sel3 rec_call' of { [] -> sel2 rec_call'; _ -> sel1 rec_call' }); [] -> (666::Int) }) (let (:) arg _ = π in arg) (let (:) _ arg = π in arg)
  rec_call = (rec (let (:) _ arg = ds in arg))
  in case ds of { (:) ρ ρ' -> Just (case ρ' of { [] -> ρ; _ -> (case ρ' of { (:) ρ'2 ρ'3 -> (case sel3 rec_call of { [] -> sel2 rec_call; _ -> sel1 rec_call }); [] -> (666::Int) }) }); [] -> Nothing }

lastMaybe = \ds -> 
  let rec π = case π of { (:) ρ'2 ρ'3 -> (case ρ'3 of { [] -> Just ρ'2; _ -> (rec ρ'3) }); [] -> Nothing } in
  case ds of { (:) ρ ρ' -> (case ρ' of { [] -> Just ρ; _ -> (rec ρ') }); [] -> Nothing }

maxMaybe1 = \ds -> let
  rec π' = let
        rec_call' = (rec (let (:) _ arg = π' in arg))
        π'2 = sel2 rec_call'
        _1 = (let (:) arg _ = π' in arg) > (case sel4 rec_call' of { Just ρ'11 -> (case sel3 rec_call' of { True -> π'2; False -> ρ'11 }); Nothing -> π'2 })
        ψ' = sel1 rec_call'
        _2 = Just (let (:) arg _ = π' in arg)
        ψ = case (let (:) _ arg = π' in arg) of { (:) ρ'8 ρ'9 -> (case sel5 rec_call' of { Just ρ'10 -> (case _1 of { True -> Just (let (:) arg _ = π' in arg); False -> ψ' }); Nothing -> _2 }); [] -> _2 }
        in (,,,,) (case π' of { (:) ρ'6 ρ'7 -> ψ; [] -> Nothing }) (let (:) arg _ = π' in arg) _1 ψ' ψ
  rec_call = (rec (let (:) _ arg = ds in arg))
  π = sel2 rec_call
  _0 = Just (let (:) arg _ = ds in arg)
  in case ds of { (:) ρ ρ' -> (case ρ' of { (:) ρ'2 ρ'3 -> (case sel5 rec_call of { Just ρ'4 -> (case ρ > (case sel4 rec_call of { Just ρ'5 -> (case sel3 rec_call of { True -> π; False -> ρ'5 }); Nothing -> π }) of { True -> Just ρ; False -> sel1 rec_call }); Nothing -> _0 }); [] -> _0 }); [] -> Nothing }

maxTest'0 = 
  let ψ = case (2::Int) > (3::Int) of { True -> (2::Int); False -> (3::Int) } in
  Just (case (1::Int) > ψ of { True -> (1::Int); False -> ψ })

maxMaybe0 = \ds -> let
  rec _fε = 
        let rec_call' = (rec (let (:) _ arg = _fε in arg)) in
        case (let (:) _ arg = _fε in arg) of { (:) ρ'4 ρ'5 -> (case (let (:) arg _ = _fε in arg) > rec_call' of { True -> (let (:) arg _ = _fε in arg); False -> rec_call' }); [] -> (let (:) arg _ = _fε in arg) }
  rec_call = (rec (let (:) _ arg = ds in arg))
  in case ds of { (:) ρ ρ' -> Just (case ρ' of { (:) ρ'2 ρ'3 -> (case ρ > rec_call of { True -> ρ; False -> rec_call }); [] -> ρ }); [] -> Nothing }
