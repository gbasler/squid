-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  6
-- Incl. one-shot:   1
-- Case reductions:  0
-- Field reductions: 0
-- Case commutings:  0
-- Total nodes: 55; Boxes: 13; Branches: 10
-- Apps: 13; Lams: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module IterContLocal (nats1_5,nats1) where

import GHC.List
import GHC.Num
import GHC.Types

nats1_5 = 
  let rec st = st : (rec (st + (1::Int))) in
  GHC.List.take (5::Int) (rec (0::Int))

nats1 = 
  let rec st = st : (rec (st + (1::Int))) in
  (rec (0::Int))
