-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  14
-- Incl. one-shot:  0
-- Case reductions:  23
-- Field reductions:  24
-- Total nodes: 115; Boxes: 30; Branches: 38
-- Apps: 13; Lams: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module InterpTrivial (test3,test2,test1,test0) where

import GHC.Num
import GHC.Tuple
import GHC.Types

test3 = ((123 + fromInteger 1) + fromInteger 1) + fromInteger 1

test2 = (123 + fromInteger 1) + fromInteger 1

test1 = (123 + fromInteger 1) + fromInteger 1

test0 = ((((((((fromInteger 0 + fromInteger 1) + fromInteger 1) + fromInteger 1) + fromInteger 1) + fromInteger 1) + fromInteger 1) + fromInteger 1) + fromInteger 1) + fromInteger 1
