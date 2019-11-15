-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  3
-- Incl. one-shot:   0
-- Case reductions:  0
-- Field reductions: 0
-- Case commutings:  0
-- Total nodes: 21; Boxes: 4; Branches: 3
-- Apps: 6; Lams: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module ExplosiveRec (a0) where

import GHC.Base
import GHC.Num
import GHC.Types

a0 = \x -> let
  rec' = let
        rec'11 = 
              let rec'12 = (2::Int) : (rec'11 ++ (rec' ++ rec'12)) in
              (0::Int) : (rec'11 ++ (rec' ++ rec'12))
        rec'13 = 
              let rec'14 = (0::Int) : (rec'14 ++ (rec' ++ rec'13)) in
              (2::Int) : (rec'14 ++ (rec' ++ rec'13))
        in (1::Int) : (rec'11 ++ (rec' ++ rec'13))
  rec'2 = let
        rec'7 = 
              let rec'8 = (0::Int) : (rec'8 ++ (rec'7 ++ rec'2)) in
              (1::Int) : (rec'8 ++ (rec'7 ++ rec'2))
        rec'9 = 
              let rec'10 = (1::Int) : (rec'9 ++ (rec'10 ++ rec'2)) in
              (0::Int) : (rec'9 ++ (rec'10 ++ rec'2))
        in (2::Int) : (rec'9 ++ (rec'7 ++ rec'2))
  rec = let
        rec'3 = 
              let rec'4 = (2::Int) : (rec ++ (rec'3 ++ rec'4)) in
              (1::Int) : (rec ++ (rec'3 ++ rec'4))
        rec'5 = 
              let rec'6 = (1::Int) : (rec ++ (rec'6 ++ rec'5)) in
              (2::Int) : (rec ++ (rec'6 ++ rec'5))
        in (0::Int) : (rec ++ (rec'3 ++ rec'5))
  in x : (rec ++ (rec' ++ rec'2))
