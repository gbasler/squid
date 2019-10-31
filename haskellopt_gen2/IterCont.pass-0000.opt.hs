-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  7
-- Incl. one-shot:  0
-- Total nodes: 76; Boxes: 24; Branches: 13
-- Apps: 15; Lams: 5

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module IterCont (nats1,loop1,loop0) where

import GHC.Num
import GHC.Types

nats1 = 
  let rec st = st : (rec (st + 1)) in
  (rec 0)

loop1 = \f_ε -> \state_ε -> 
        let λ = \st_ε -> f_ε λ st_ε in
        f_ε λ state_ε

loop0 = \f_ε' -> 
  let rec' f = f (rec' f) in
  f_ε' (rec' f_ε')
