-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  5
-- Incl. one-shot:   0
-- Case reductions:  1
-- Field reductions: 2
-- Total nodes: 149; Boxes: 32; Branches: 26
-- Apps: 34; Lams: 3

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module IterEither (count,loop) where

import Data.Either
import GHC.Classes
import GHC.Num
import GHC.Tuple
import GHC.Types

count = \start -> 
  let rec π = case case π of { (,) ρ'2 ρ'3 -> (case ρ'2 > (0::Int) of { True -> Data.Either.Left ((,) (ρ'2 - (1::Int)) (ρ'3 + (1::Int))); False -> Data.Either.Right ρ'3 }) } of { Right ρ'4 -> ρ'4; Left ρ'5 -> (case case ρ'5 of { (,) ρ'6 ρ'7 -> (case ρ'6 > (0::Int) of { True -> Data.Either.Left ((,) (ρ'6 - (1::Int)) (ρ'7 + (1::Int))); False -> Data.Either.Right ρ'7 }) } of { Right ρ'8 -> ρ'8; Left ρ'9 -> (rec ρ'9) }) } in
  case case start > (0::Int) of { True -> Data.Either.Left ((,) (start - (1::Int)) ((0::Int) + (1::Int))); False -> Data.Either.Right (0::Int) } of { Right ρ -> ρ; Left ρ' -> (rec ρ') }

loop = \k -> \x -> 
        let rec π = case k π of { Right ρ'2 -> ρ'2; Left ρ'3 -> (case k ρ'3 of { Right ρ'4 -> ρ'4; Left ρ'5 -> (rec ρ'5) }) } in
        case k x of { Right ρ -> ρ; Left ρ' -> (rec ρ') }
