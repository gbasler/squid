-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  14
-- Incl. one-shot:   0
-- Case reductions:  29
-- Field reductions: 30
-- Case commutings:  0
-- Total nodes: 129; Boxes: 47; Branches: 44
-- Apps: 8; Lams: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module InterpTrivial (test3,test2,test1,test0) where

import GHC.Num
import GHC.Tuple
import GHC.Types

test3 = ((((123::Int) + (1::Int)) + (1::Int)) + (1::Int)) + (1::Int)

test2 = (((((123::Int) + (1::Int)) + (1::Int)) + (1::Int)) + (1::Int)) + (1::Int)

test1 = (((123::Int) + (1::Int)) + (1::Int)) + (1::Int)

test0 = (((((((((0::Int) + (1::Int)) + (1::Int)) + (1::Int)) + (1::Int)) + (1::Int)) + (1::Int)) + (1::Int)) + (1::Int)) + (1::Int)
