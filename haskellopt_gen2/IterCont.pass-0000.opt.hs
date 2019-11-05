-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  12
-- Incl. one-shot:  0
-- Case reductions:  0
-- Field reductions:  0
-- Total nodes: 148; Boxes: 47; Branches: 34
-- Apps: 26; Lams: 9

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module IterCont (nats1,loop1,nats0,loop0) where

import GHC.Num
import GHC.Types

nats1 = 
  let rec st = st : (rec (st + (1::Int))) in
  (rec (0::Int))

loop1 = \f -> \state -> 
        let λ = \st -> f λ st in
        f λ state

nats0 = let
  _0 = (0::Int) + (1::Int)
  rec = rec
  in (0::Int) : (_0 : rec (_0 + (1::Int)))

loop0 = \f -> 
  let rec f' f'2 = f'2 (rec f' f') in
  f (rec f f)
