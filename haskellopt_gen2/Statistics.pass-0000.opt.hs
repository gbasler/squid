-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  2
-- Incl. one-shot:  0
-- Case reductions:  0
-- Field reductions:  0
-- Total nodes: 40; Boxes: 6; Branches: 2
-- Apps: 8; Lams: 2

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Statistics (maxMaybe1,maxMaybe0) where

import GHC.Classes
import GHC.Maybe

maxMaybe1 = \ds -> let
  rec π = let
        rec_call' = (rec (let (:) _ arg = (let (:) _ arg = π in arg) in arg))
        ψ = case (let (:) _ arg = π in arg) of { (:) ρ'6 ρ'7 -> (case rec_call' of { Just ρ'8 -> (case ρ'6 > ρ'8 of { True -> Just ρ'6; False -> rec_call' }); Nothing -> Just ρ'6 }); [] -> Nothing }
        in case π of { (:) ρ'3 ρ'4 -> (case ψ of { Just ρ'5 -> (case ρ'3 > ρ'5 of { True -> Just ρ'3; False -> ψ }); Nothing -> Just ρ'3 }); [] -> Nothing }
  rec_call = (rec (let (:) _ arg = ds in arg))
  in case ds of { (:) ρ ρ' -> (case rec_call of { Just ρ'2 -> (case ρ > ρ'2 of { True -> Just ρ; False -> rec_call }); Nothing -> Just ρ }); [] -> Nothing }

maxMaybe0 = \ds -> 
  let rec π = case π of { (:) ρ'3 ρ'4 -> (case case ρ'4 of { (:) ρ'5 ρ'6 -> (case (rec ρ'6) of { Just ρ'7 -> Just (case ρ'5 > ρ'7 of { True -> ρ'5; False -> ρ'7 }); Nothing -> Just ρ'5 }); [] -> Nothing } of { Just ρ'8 -> Just (case ρ'3 > ρ'8 of { True -> ρ'3; False -> ρ'8 }); Nothing -> Just ρ'3 }); [] -> Nothing } in
  case ds of { (:) ρ ρ' -> (case (rec ρ') of { Just ρ'2 -> Just (case ρ > ρ'2 of { True -> ρ; False -> ρ'2 }); Nothing -> Just ρ }); [] -> Nothing }
