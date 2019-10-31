-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  6
-- Incl. one-shot:  1
-- Total nodes: 25; Boxes: 7; Branches: 5
-- Apps: 5; Lams: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module IterContLocal (nats1) where

import GHC.Num
import GHC.Types

nats1 = 
  let rec st = st : (rec (st + 1)) in
  (rec 0)
