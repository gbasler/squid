-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  6
-- Incl. one-shot:  0
-- Case reductions:  10
-- Field reductions:  14
-- Total nodes: 53; Boxes: 15; Branches: 19
-- Apps: 6; Lams: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module InterpSimple (test) where

import GHC.Num
import GHC.Types

test = (123 + fromInteger 1) * fromInteger 2
