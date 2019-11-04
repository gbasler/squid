-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  5
-- Incl. one-shot:  0
-- Case reductions:  1
-- Field reductions:  2
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
  let rec π = case case π of { (,) ρ'2 ρ'3 -> (case ρ'2 > (0::Int) of { True -> Data.Either.Left ((,) (ρ'2 - (1::Int)) (ρ'3 + (1::Int))); False -> Data.Either.Right ρ'3 }) } of { Right ρ'4 -> ρ'4; Left ρ'5 -> (rec ρ'5) } in
  case case start > (0::Int) of { True -> Data.Either.Left ((,) (start - (1::Int)) ((0::Int) + (1::Int))); False -> Data.Either.Right (0::Int) } of { Right ρ -> ρ; Left ρ' -> (rec ρ') }

loop = \k -> \x -> 
        let rec π = case k π of { Right ρ'2 -> ρ'2; Left ρ'3 -> (rec ρ'3) } in
        case k x of { Right ρ -> ρ; Left ρ' -> (rec ρ') }
