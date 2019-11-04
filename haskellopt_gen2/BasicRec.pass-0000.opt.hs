-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  8
-- Incl. one-shot:  0
-- Case reductions:  0
-- Field reductions:  0
-- Total nodes: 69; Boxes: 20; Branches: 8
-- Apps: 18; Lams: 3

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module BasicRec (nrec_0,nrec_1,nrec_capt_0) where

import GHC.Base
import GHC.Num
import GHC.Types

nrec_0 = \x -> 
  let rec p = p : (rec (p + (1::Int))) in
  x : (rec (x + (1::Int)))

nrec_1 = \x -> let
  rec p' = p' : (rec (p' + (1::Int)))
  rec' p = p : (rec' (p + (1::Int)))
  in ((0::Int) : (rec ((0::Int) + (1::Int)))) ++ (x : (rec' (x + (1::Int))))

nrec_capt_0 = \x -> let
  rec' p' = p' : (rec' (p' + x))
  rec p = p : (rec (p + x))
  in ((0::Int) : (rec ((0::Int) + x))) ++ (x : (rec' (x + x)))
