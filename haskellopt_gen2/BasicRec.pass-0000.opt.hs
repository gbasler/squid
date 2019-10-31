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
  let rec β = β : (rec (β + fromInteger 1)) in
  x : (rec (x + fromInteger 1))

nrec_1 = \x' -> let
  rec' β'2 = β'2 : (rec' (β'2 + fromInteger 1))
  rec'2 β' = β' : (rec'2 (β' + fromInteger 1))
  in (0 : (rec' (0 + fromInteger 1))) ++ (x' : (rec'2 (x' + fromInteger 1)))

nrec_capt_0 = \x'2 -> let
  rec'3 β'4 = β'4 : (rec'3 (β'4 + x'2))
  rec'4 β'3 = β'3 : (rec'4 (β'3 + x'2))
  in (0 : (rec'3 (0 + x'2))) ++ (x'2 : (rec'4 (x'2 + x'2)))
