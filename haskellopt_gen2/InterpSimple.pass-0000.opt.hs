-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  6
-- Incl. one-shot:   0
-- Case reductions:  46
-- Field reductions: 62
-- Total nodes: 85; Boxes: 15; Branches: 55
-- Apps: 4; Lams: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module InterpSimple (test) where

import GHC.Num
import GHC.Types

test = ((((123::Int) + (1::Int)) * (2::Int)) * (2::Int)) + (1::Int)
