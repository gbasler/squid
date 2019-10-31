-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  8
-- Incl. one-shot:  0
-- Total nodes: 73; Boxes: 20; Branches: 8
-- Apps: 20; Lams: 3

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module BasicRec (nrec_0,nrec_1,nrec_capt_0) where

import GHC.Base
import GHC.Num
import GHC.Types

nrec_0 = \x -> 
  let rec p = p : (rec (p + fromInteger 1)) in
  x : (rec (x + fromInteger 1))

nrec_1 = \x -> let
  rec p' = p' : (rec (p' + fromInteger 1))
  rec' p = p : (rec' (p + fromInteger 1))
  in (0 : (rec (0 + fromInteger 1))) ++ (x : (rec' (x + fromInteger 1)))

nrec_capt_0 = \x -> let
  rec p' = p' : (rec (p' + x))
  rec' p = p : (rec' (p + x))
  in (0 : (rec (0 + x))) ++ (x : (rec' (x + x)))
