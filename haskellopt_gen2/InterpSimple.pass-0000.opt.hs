-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  9
-- Incl. one-shot:  0
-- Total nodes: 44; Boxes: 10; Branches: 6
-- Apps: 10; Lams: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module InterpSimple (test) where

import GHC.Num
import GHC.Types

test = let
  _0 = True : (False : ([]))
  π' = case _0 of (:) _ arg -> arg
  rec p'2 = 
        let rec'3 p'3 = case case π' of (:) _ arg -> arg of { [] -> p'3; (:) ρ'6 ρ'7 -> (case ρ'6 of { False -> (rec (p'3 * fromInteger 2)); True -> (rec'3 (p'3 + fromInteger 1)) }) } in
        case π' of { [] -> p'2; (:) ρ'8 ρ'9 -> (case ρ'8 of { False -> (rec (p'2 * fromInteger 2)); True -> (rec'3 (p'2 + fromInteger 1)) }) }
  π = case _0 of (:) _ arg -> arg
  rec' p = 
        let rec'2 p' = case case π of (:) _ arg -> arg of { [] -> p'; (:) ρ'2 ρ'3 -> (case ρ'2 of { False -> (rec'2 (p' * fromInteger 2)); True -> (rec' (p' + fromInteger 1)) }) } in
        case π of { [] -> p; (:) ρ'4 ρ'5 -> (case ρ'4 of { False -> (rec'2 (p * fromInteger 2)); True -> (rec' (p + fromInteger 1)) }) }
  in case _0 of { [] -> 123; (:) ρ ρ' -> (case ρ of { False -> (rec (123 * fromInteger 2)); True -> (rec' (123 + fromInteger 1)) }) }
