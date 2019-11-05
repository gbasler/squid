-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  16
-- Incl. one-shot:  0
-- Case reductions:  0
-- Field reductions:  0
-- Total nodes: 203; Boxes: 53; Branches: 26
-- Apps: 54; Lams: 5

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module BasicRec (trec_0_1,trec_0_0,trec_0,trec_1,nrec_0,nrec_1,nrec_capt_0) where

import GHC.Base
import GHC.List
import GHC.Num
import GHC.Types

trec_0_1 = let
  rec p = 
        let _0 = p + (1::Int) in
        p : (_0 : (rec (_0 + (1::Int))))
  _1 = GHC.List.head ((32::Int) : (rec ((32::Int) + (1::Int))))
  rec' p' = 
        let _2 = p' + (1::Int) in
        p' : (_2 : (rec' (_2 + (1::Int))))
  in _1 : (rec' (_1 + (1::Int)))

trec_0_0 = let
  rec p = 
        let _0 = p + (1::Int) in
        p : (_0 : (rec (_0 + (1::Int))))
  rec' p' = 
        let _1 = p' + (1::Int) in
        p' : (_1 : (rec' (_1 + (1::Int))))
  in ((27::Int) : (rec ((27::Int) + (1::Int)))) ++ ((32::Int) : (rec' ((32::Int) + (1::Int))))

trec_0 = \y -> 
  let rec p = 
        let _0 = p + (1::Int) in
        p : (_0 : (rec (_0 + (1::Int)))) in
  y : (rec (y + (1::Int)))

trec_1 = \x -> let
  rec' p' = 
        let _1 = p' + (1::Int) in
        p' : (_1 : (rec' (_1 + (1::Int))))
  rec p = 
        let _0 = p + (1::Int) in
        p : (_0 : (rec (_0 + (1::Int))))
  in ((0::Int) : (rec ((0::Int) + (1::Int)))) ++ (x : (rec' (x + (1::Int))))

nrec_0 = \x -> 
  let rec p = 
        let _0 = p + (1::Int) in
        p : (_0 : (rec (_0 + (1::Int)))) in
  x : (rec (x + (1::Int)))

nrec_1 = \x -> let
  rec p' = 
        let _1 = p' + (1::Int) in
        p' : (_1 : (rec (_1 + (1::Int))))
  rec' p = 
        let _0 = p + (1::Int) in
        p : (_0 : (rec' (_0 + (1::Int))))
  in ((0::Int) : (rec ((0::Int) + (1::Int)))) ++ (x : (rec' (x + (1::Int))))

nrec_capt_0 = \x -> let
  rec' p' = 
        let _1 = p' + x in
        p' : (_1 : (rec' (_1 + x)))
  rec p = 
        let _0 = p + x in
        p : (_0 : (rec (_0 + x)))
  in ((0::Int) : (rec ((0::Int) + x))) ++ (x : (rec' (x + x)))
