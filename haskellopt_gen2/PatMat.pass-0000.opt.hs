-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  25
-- Incl. one-shot:  1
-- Total nodes: 738; Boxes: 120; Branches: 57
-- Apps: 182; Lams: 11

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module PatMat (f1'2,f1'1,f1'0,f1,f0'3,f0'2,f0'1,f0'0,f0,f2'0,t1'0,t0'0,t1'1,t1,t0,t'ls,slt1,f2,orZero,e1'1,e1'0,e1,e0'3,e0'2,e0'1,e0'0,e0) where

import GHC.Base
import GHC.Classes
import GHC.Maybe
import GHC.Num
import GHC.Prim
import GHC.Tuple
import GHC.Types

f1'2 = case case 5 > 0 of { False -> Nothing; True -> Just 5 } of { Nothing -> 0; Just ρ -> ρ }

f1'1 = case case 5 > 0 of { False -> Nothing; True -> Just 5 } of { Nothing -> False; Just ρ -> True }

f1'0 = case 4 > 0 of { False -> Nothing; True -> Just 4 }

f1 = \x -> case x > 0 of { False -> Nothing; True -> Just x }

f0'3 = case case Nothing of { Nothing -> Just 0; Just ρ -> Just (ρ + 1) } of { Nothing -> Just 0; Just ρ' -> Just (ρ' + 1) }

f0'2 = case case Just 3 of { Nothing -> Just 0; Just ρ -> Just (ρ + 1) } of { Nothing -> Just 0; Just ρ' -> Just (ρ' + 1) }

f0'1 = case Nothing of { Nothing -> Just 0; Just ρ -> Just (ρ + 1) }

f0'0 = case Just 2 of { Nothing -> Just 0; Just ρ -> Just (ρ + 1) }

f0 = \ds -> case ds of { Nothing -> Just 0; Just ρ -> Just (ρ + 1) }

f2'0 = case (,,) 1 2 3 of { (,,) ρ ρ' ρ'2 -> (ρ + ρ') + ρ'2 }

t1'0 = case 1 : (2 : (3 : (4 : ([])))) of { (_) -> fromInteger 666; (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666; (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666; (:) ρ'4 ρ'5 -> (case ρ'5 of { (_) -> fromInteger 666; (:) ρ'6 ρ'7 -> (case ρ'7 of { (_) -> fromInteger 666; [] -> ((ρ + ρ'2) + ρ'4) + ρ'6 }) }) }) }) }

t0'0 = case 1 : (2 : (3 : (4 : ([])))) of { (_) -> fromInteger 666; (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666; (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666; (:) ρ'4 ρ'5 -> (case ρ'5 of { (_) -> fromInteger 666; (:) ρ'6 ρ'7 -> (case ρ'7 of { (_) -> fromInteger 666; [] -> ((ρ + ρ'2) + ρ'4) + ρ'6 }) }) }) }) }

t1'1 = \xs -> case fromInteger 5 : (fromInteger 6 : xs) of { (_) -> fromInteger 666; (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666; (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666; (:) ρ'4 ρ'5 -> (case ρ'5 of { (_) -> fromInteger 666; (:) ρ'6 ρ'7 -> (case ρ'7 of { (_) -> fromInteger 666; [] -> ((ρ + ρ'2) + ρ'4) + ρ'6 }) }) }) }) }

t1 = \ds -> case ds of { (_) -> fromInteger 666; (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666; (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666; (:) ρ'4 ρ'5 -> (case ρ'5 of { (_) -> fromInteger 666; (:) ρ'6 ρ'7 -> (case ρ'7 of { (_) -> fromInteger 666; [] -> ((ρ + ρ'2) + ρ'4) + ρ'6 }) }) }) }) }

t0 = \ds -> case ds of { (_) -> fromInteger 666; (:) ρ ρ' -> (case ρ' of { (_) -> fromInteger 666; (:) ρ'2 ρ'3 -> (case ρ'3 of { (_) -> fromInteger 666; (:) ρ'4 ρ'5 -> (case ρ'5 of { (_) -> fromInteger 666; (:) ρ'6 ρ'7 -> (case ρ'7 of { (_) -> fromInteger 666; [] -> ((ρ + ρ'2) + ρ'4) + ρ'6 }) }) }) }) }

t'ls = 1 : (2 : (3 : (4 : ([]))))

slt1 = \ls -> case (,) (\res -> res) 0 of { (,) ρ ρ' -> ρ (map (\c -> ρ')) (map (\c' -> ρ') ls) }

f2 = \ds -> case ds of { (,,) ρ ρ' ρ'2 -> (ρ + ρ') + ρ'2 }

orZero = \ds -> case ds of { Nothing -> 0; Just ρ -> ρ }

e1'1 = case Nothing of { (_) -> 0; Just ρ -> ρ + 1 }

e1'0 = case Nothing of { Nothing -> 0; Just ρ -> ρ + 1 }

e1 = Nothing

e0'3 = case Just 2 of { (_) -> 1; Nothing -> 0 }

e0'2 = case Just 2 of { (_) -> 0; Just ρ -> ρ + 1 }

e0'1 = case Just 2 of { (_) -> 666; Just ρ -> ρ + 1 }

e0'0 = case Just 2 of { Nothing -> 0; Just ρ -> ρ + 1 }

e0 = Just 2
