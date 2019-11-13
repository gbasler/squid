-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  14
-- Incl. one-shot:   1
-- Case reductions:  0
-- Field reductions: 0
-- Case commutings:  0
-- Total nodes: 98; Boxes: 27; Branches: 21
-- Apps: 20; Lams: 3

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module IterContLocal (nats1_5,nats1,nats0) where

import GHC.List
import GHC.Num
import GHC.Types

nats1_5 = 
  let rec st = st : (rec (st + (1::Int))) in
  GHC.List.take (5::Int) (rec (0::Int))

nats1 = 
  let rec st = st : (rec (st + (1::Int))) in
  (rec (0::Int))

nats0 = let
  λ = \k -> \s -> s : k (s + (1::Int))
  rec f f' = \state -> f' (rec f f) state
  _0 = (0::Int) + (1::Int)
  in (0::Int) : (_0 : (rec λ λ) (_0 + (1::Int)))
