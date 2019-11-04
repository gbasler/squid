-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  7
-- Incl. one-shot:  0
-- Case reductions:  0
-- Field reductions:  0
-- Total nodes: 76; Boxes: 24; Branches: 13
-- Apps: 15; Lams: 5

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module IterCont (nats1,loop1,loop0) where

import GHC.Num
import GHC.Types

nats1 = 
  let rec st = st : (rec (st + (1::Int))) in
  (rec (0::Int))

loop1 = \f -> \state -> 
        let λ = \st -> f λ st in
        f λ state

loop0 = \f -> 
  let rec f' = f' (rec f') in
  f (rec f)
