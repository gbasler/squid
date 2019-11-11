-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  5
-- Incl. one-shot:   0
-- Case reductions:  11
-- Field reductions: 18
-- Case commutings:  11
-- Total nodes: 192; Boxes: 71; Branches: 35
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
  _1 = Just (666::Int)
  rec π' = let
        rec_call' = (rec (let (:) _ arg = (let (:) _ arg = π' in arg) in arg))
        π'3 = sel6 rec_call'
        π'4 = case π'3 of { [] -> sel5 rec_call'; _ -> (case sel4 rec_call' of { Just ρ'15 -> sel3 rec_call'; Nothing -> sel2 rec_call' }) }
        π'2 = case (let (:) _ arg = (let (:) _ arg = π' in arg) in arg) of { [] -> (let (:) arg _ = (let (:) _ arg = π' in arg) in arg); _ -> (case sel4 rec_call' of { Just ρ'14 -> π'4; Nothing -> (666::Int) }) }
        _2 = Just π'2
        _3 = Just (666::Int)
        _4 = Just π'4
        _5 = Just (666::Int)
        ψ = case sel7 rec_call' of { (:) ρ'11 ρ'12 -> (case π'3 of { [] -> _4; _ -> (case sel1 rec_call' of { Just ρ'13 -> _4; Nothing -> _5 }) }); [] -> _5 }
        in (,,,,,,) (case (let (:) _ arg = π' in arg) of { (:) ρ'6 ρ'7 -> (case ρ'7 of { [] -> _2; _ -> (case ψ of { Just ρ'8 -> _2; Nothing -> _3 }) }); [] -> _3 }) (666::Int) π'2 (case (let (:) _ arg = π' in arg) of { (:) ρ'9 ρ'10 -> (case ρ'10 of { [] -> Just ρ'9; _ -> ψ }); [] -> Nothing }) (let (:) arg _ = π' in arg) (let (:) _ arg = π' in arg) π'
  rec_call = (rec (let (:) _ arg = ds in arg))
  π = sel6 rec_call
  _0 = Just (case π of { [] -> sel5 rec_call; _ -> (case sel4 rec_call of { Just ρ'5 -> sel3 rec_call; Nothing -> sel2 rec_call }) })
  in case ds of { (:) ρ ρ' -> (case ρ' of { [] -> Just ρ; _ -> (case sel7 rec_call of { (:) ρ'2 ρ'3 -> (case π of { [] -> _0; _ -> (case sel1 rec_call of { Just ρ'4 -> _0; Nothing -> _1 }) }); [] -> _1 }) }); [] -> Nothing }

lastMaybe = \ds -> 
  let rec π = case π of { (:) ρ'2 ρ'3 -> (case ρ'3 of { [] -> Just ρ'2; _ -> (case ρ'3 of { (:) ρ'4 ρ'5 -> (case ρ'5 of { [] -> Just ρ'4; _ -> (rec ρ'5) }); [] -> Nothing }) }); [] -> Nothing } in
  case ds of { (:) ρ ρ' -> (case ρ' of { [] -> Just ρ; _ -> (rec ρ') }); [] -> Nothing }

maxMaybe1 = \ds -> let
  rec π' = let
        rec_call' = (rec (let (:) _ arg = (let (:) _ arg = π' in arg) in arg))
        _4 = Just (let (:) arg _ = (let (:) _ arg = π' in arg) in arg)
        ψ'3 = sel1 rec_call'
        π'2 = sel2 rec_call'
        _3 = (let (:) arg _ = (let (:) _ arg = π' in arg) in arg) > (case sel1 rec_call' of { Just ρ'17 -> (case sel3 rec_call' of { True -> π'2; False -> ρ'17 }); Nothing -> π'2 })
        ψ'2 = case sel6 rec_call' of { (:) ρ'14 ρ'15 -> (case sel5 rec_call' of { Just ρ'16 -> (case _3 of { True -> Just (let (:) arg _ = (let (:) _ arg = π' in arg) in arg); False -> ψ'3 }); Nothing -> _4 }); [] -> _4 }
        ψ' = case (let (:) _ arg = π' in arg) of { (:) ρ'12 ρ'13 -> ψ'2; [] -> Nothing }
        _2 = Just (let (:) arg _ = π' in arg)
        _1 = (let (:) arg _ = π' in arg) > (case ψ'3 of { Just ρ'11 -> (case _3 of { True -> (let (:) arg _ = (let (:) _ arg = π' in arg) in arg); False -> ρ'11 }); Nothing -> (let (:) arg _ = (let (:) _ arg = π' in arg) in arg) })
        ψ = case (let (:) _ arg = π' in arg) of { (:) ρ'8 ρ'9 -> (case ψ'2 of { Just ρ'10 -> (case _1 of { True -> Just (let (:) arg _ = π' in arg); False -> ψ' }); Nothing -> _2 }); [] -> _2 }
        in (,,,,,) (case π' of { (:) ρ'6 ρ'7 -> ψ; [] -> Nothing }) (let (:) arg _ = π' in arg) _1 ψ' ψ π'
  rec_call = (rec (let (:) _ arg = ds in arg))
  _0 = Just (let (:) arg _ = ds in arg)
  π = sel2 rec_call
  in case ds of { (:) ρ ρ' -> (case sel6 rec_call of { (:) ρ'2 ρ'3 -> (case sel5 rec_call of { Just ρ'4 -> (case ρ > (case sel4 rec_call of { Just ρ'5 -> (case sel3 rec_call of { True -> π; False -> ρ'5 }); Nothing -> π }) of { True -> Just ρ; False -> sel1 rec_call }); Nothing -> _0 }); [] -> _0 }); [] -> Nothing }

maxTest'0 = let
  π = case Nothing of { Just ρ -> (case (3::Int) > Prelude.undefined of { True -> (3::Int); False -> Prelude.undefined }); Nothing -> (3::Int) }
  ψ = case (2::Int) > π of { True -> (2::Int); False -> π }
  _0 = Just (3::Int)
  π' = case _0 of { Just ρ'3 -> ψ; Nothing -> (2::Int) }
  in case case _0 of { Just ρ' -> Just ψ; Nothing -> Just (2::Int) } of { Just ρ'2 -> Just (case (1::Int) > π' of { True -> (1::Int); False -> π' }); Nothing -> Just (1::Int) }

maxMaybe0 = \ds -> let
  rec _fε = let
        rec_call' = (rec (let (:) _ arg = (let (:) _ arg = _fε in arg) in arg))
        π'2 = case sel3 rec_call' of { Just ρ'11 -> sel2 rec_call'; Nothing -> sel1 rec_call' }
        ψ' = case (let (:) arg _ = (let (:) _ arg = _fε in arg) in arg) > π'2 of { True -> (let (:) arg _ = (let (:) _ arg = _fε in arg) in arg); False -> π'2 }
        π' = case sel3 rec_call' of { Just ρ'10 -> ψ'; Nothing -> (let (:) arg _ = (let (:) _ arg = _fε in arg) in arg) }
        ψ = case (let (:) arg _ = _fε in arg) > π' of { True -> (let (:) arg _ = _fε in arg); False -> π' }
        _cε = case (let (:) _ arg = _fε in arg) of { (:) ρ'7 ρ'8 -> (case sel4 rec_call' of { Just ρ'9 -> Just ψ'; Nothing -> Just ρ'7 }); [] -> Nothing }
        in (,,,) (let (:) arg _ = _fε in arg) ψ _cε (case _fε of { (:) ρ'4 ρ'5 -> (case _cε of { Just ρ'6 -> Just ψ; Nothing -> Just ρ'4 }); [] -> Nothing })
  rec_call = (rec (let (:) _ arg = ds in arg))
  π = case sel3 rec_call of { Just ρ'3 -> sel2 rec_call; Nothing -> sel1 rec_call }
  in case ds of { (:) ρ ρ' -> (case sel4 rec_call of { Just ρ'2 -> Just (case ρ > π of { True -> ρ; False -> π }); Nothing -> Just ρ }); [] -> Nothing }
