-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  5
-- Incl. one-shot:   0
-- Case reductions:  11
-- Field reductions: 18
-- Case commutings:  11
-- Total nodes: 185; Boxes: 64; Branches: 32
-- Apps: 16; Lams: 4

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Statistics (lastWeird,lastMaybe,maxMaybe1,maxTest'0,maxMaybe0) where

import Data.Tuple.Select
import GHC.Classes
import GHC.Maybe
import GHC.Num
import GHC.Types

lastWeird = \ds -> let
  _3 = Just (666::Int)
  _2 = Just (666::Int)
  rec π'2 = let
        rec_call' = (rec (let (:) _ arg = π'2 in arg))
        π'4 = sel5 rec_call'
        π'3 = case π'4 of { [] -> sel4 rec_call'; _ -> (case sel3 rec_call' of { Just ρ'15 -> sel2 rec_call'; Nothing -> (666::Int) }) }
        _5 = Just (666::Int)
        _4 = Just π'3
        ψ = case (let (:) _ arg = π'2 in arg) of { (:) ρ'12 ρ'13 -> (case π'4 of { [] -> _4; _ -> (case sel1 rec_call' of { Just ρ'14 -> _4; Nothing -> _5 }) }); [] -> _5 }
        in (,,,,,) ψ π'3 (sel3 rec_call') (let (:) arg _ = π'2 in arg) (let (:) _ arg = π'2 in arg) (case π'2 of { (:) ρ'10 ρ'11 -> (case ρ'11 of { [] -> Just ρ'10; _ -> ψ }); [] -> Nothing })
  rec_call = (rec (let (:) _ arg = (let (:) _ arg = ds in arg) in arg))
  π = sel5 rec_call
  π' = case π of { [] -> sel4 rec_call; _ -> (case sel3 rec_call of { Just ρ'9 -> sel2 rec_call; Nothing -> (666::Int) }) }
  _1 = Just π'
  _0 = Just (case (let (:) _ arg = (let (:) _ arg = ds in arg) in arg) of { [] -> (let (:) arg _ = (let (:) _ arg = ds in arg) in arg); _ -> (case sel6 rec_call of { Just ρ'8 -> π'; Nothing -> (666::Int) }) })
  in case ds of { (:) ρ ρ' -> (case ρ' of { [] -> Just ρ; _ -> (case ρ' of { (:) ρ'2 ρ'3 -> (case ρ'3 of { [] -> _0; _ -> (case case ρ'3 of { (:) ρ'4 ρ'5 -> (case π of { [] -> _1; _ -> (case sel1 rec_call of { Just ρ'6 -> _1; Nothing -> _2 }) }); [] -> _2 } of { Just ρ'7 -> _0; Nothing -> _3 }) }); [] -> _3 }) }); [] -> Nothing }

lastMaybe = \ds -> 
  let rec π = case π of { (:) ρ'4 ρ'5 -> (case ρ'5 of { [] -> Just ρ'4; _ -> (rec ρ'5) }); [] -> Nothing } in
  case ds of { (:) ρ ρ' -> (case ρ' of { [] -> Just ρ; _ -> (case ρ' of { (:) ρ'2 ρ'3 -> (case ρ'3 of { [] -> Just ρ'2; _ -> (rec ρ'3) }); [] -> Nothing }) }); [] -> Nothing }

maxMaybe1 = \ds -> let
  _1 = Just (let (:) arg _ = ds in arg)
  rec π' = let
        rec_call' = (rec (let (:) _ arg = π' in arg))
        ψ'3 = sel1 rec_call'
        π'2 = sel2 rec_call'
        _3 = (let (:) arg _ = π' in arg) > (case sel1 rec_call' of { Just ρ'17 -> (case sel3 rec_call' of { True -> π'2; False -> ρ'17 }); Nothing -> π'2 })
        _4 = Just (let (:) arg _ = π' in arg)
        ψ'2 = case (let (:) _ arg = π' in arg) of { (:) ρ'14 ρ'15 -> (case sel5 rec_call' of { Just ρ'16 -> (case _3 of { True -> Just (let (:) arg _ = π' in arg); False -> ψ'3 }); Nothing -> _4 }); [] -> _4 }
        in (,,,,) (case π' of { (:) ρ'12 ρ'13 -> ψ'2; [] -> Nothing }) (let (:) arg _ = π' in arg) _3 ψ'3 ψ'2
  rec_call = (rec (let (:) _ arg = (let (:) _ arg = ds in arg) in arg))
  ψ' = sel1 rec_call
  π = sel2 rec_call
  _0 = (let (:) arg _ = (let (:) _ arg = ds in arg) in arg) > (case sel4 rec_call of { Just ρ'11 -> (case sel3 rec_call of { True -> π; False -> ρ'11 }); Nothing -> π })
  _2 = Just (let (:) arg _ = (let (:) _ arg = ds in arg) in arg)
  ψ = case (let (:) _ arg = (let (:) _ arg = ds in arg) in arg) of { (:) ρ'8 ρ'9 -> (case sel5 rec_call of { Just ρ'10 -> (case _0 of { True -> Just (let (:) arg _ = (let (:) _ arg = ds in arg) in arg); False -> ψ' }); Nothing -> _2 }); [] -> _2 }
  in case ds of { (:) ρ ρ' -> (case ρ' of { (:) ρ'2 ρ'3 -> (case ψ of { Just ρ'4 -> (case ρ > (case ψ' of { Just ρ'5 -> (case _0 of { True -> ρ'2; False -> ρ'5 }); Nothing -> ρ'2 }) of { True -> Just ρ; False -> (case ρ' of { (:) ρ'6 ρ'7 -> ψ; [] -> Nothing }) }); Nothing -> _1 }); [] -> _1 }); [] -> Nothing }

maxTest'0 = let
  _0 = Just (3::Int)
  π' = case Nothing of { Just ρ'3 -> (case (3::Int) > Prelude.undefined of { True -> (3::Int); False -> Prelude.undefined }); Nothing -> (3::Int) }
  ψ = case (2::Int) > π' of { True -> (2::Int); False -> π' }
  π = case _0 of { Just ρ'2 -> ψ; Nothing -> (2::Int) }
  in case case _0 of { Just ρ -> Just ψ; Nothing -> Just (2::Int) } of { Just ρ' -> Just (case (1::Int) > π of { True -> (1::Int); False -> π }); Nothing -> Just (1::Int) }

maxMaybe0 = \ds -> let
  rec _fε = let
        rec_call' = (rec (let (:) _ arg = _fε in arg))
        π'2 = case sel1 rec_call' of { Just ρ'11 -> sel3 rec_call'; Nothing -> sel2 rec_call' }
        ψ' = case (let (:) arg _ = _fε in arg) > π'2 of { True -> (let (:) arg _ = _fε in arg); False -> π'2 }
        _cε = case _fε of { (:) ρ'8 ρ'9 -> (case sel5 rec_call' of { Just ρ'10 -> Just ψ'; Nothing -> Just ρ'8 }); [] -> Nothing }
        in (,,,,) _cε (let (:) arg _ = _fε in arg) ψ' (sel1 rec_call') _cε
  rec_call = (rec (let (:) _ arg = (let (:) _ arg = ds in arg) in arg))
  π' = case sel4 rec_call of { Just ρ'7 -> sel3 rec_call; Nothing -> sel2 rec_call }
  ψ = case (let (:) arg _ = (let (:) _ arg = ds in arg) in arg) > π' of { True -> (let (:) arg _ = (let (:) _ arg = ds in arg) in arg); False -> π' }
  π = case sel1 rec_call of { Just ρ'6 -> ψ; Nothing -> (let (:) arg _ = (let (:) _ arg = ds in arg) in arg) }
  in case ds of { (:) ρ ρ' -> (case case ρ' of { (:) ρ'2 ρ'3 -> (case sel5 rec_call of { Just ρ'4 -> Just ψ; Nothing -> Just ρ'2 }); [] -> Nothing } of { Just ρ'5 -> Just (case ρ > π of { True -> ρ; False -> π }); Nothing -> Just ρ }); [] -> Nothing }
