-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  22
-- Incl. one-shot:   0
-- Case reductions:  0
-- Field reductions: 0
-- Case commutings:  0
-- Total nodes: 310; Boxes: 76; Branches: 38
-- Apps: 83; Lams: 9

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module BasicRec (trec_0_1,trec_0_0,trec_0,trec_1,nrec_0,nrec_1,alternateZO1'0,alternateZO1,alternateZO0'0,alternateZO0,nrec_capt_0,alternateTF'0,alternateTF) where

import GHC.Base
import GHC.List
import GHC.Num
import GHC.Types

trec_0_1 = let
  rec p = 
        let _0 = p + (1::Int) in
        p : (_0 : (rec (_0 + (1::Int))))
  rec' p' = 
        let _2 = p' + (1::Int) in
        p' : (_2 : (rec' (_2 + (1::Int))))
  _1 = GHC.List.head ((32::Int) : (rec' ((32::Int) + (1::Int))))
  in _1 : (rec (_1 + (1::Int)))

trec_0_0 = let
  rec p = 
        let _0 = p + (1::Int) in
        p : (_0 : (rec (_0 + (1::Int))))
  rec' p' = 
        let _1 = p' + (1::Int) in
        p' : (_1 : (rec' (_1 + (1::Int))))
  in ((27::Int) : (rec' ((27::Int) + (1::Int)))) ++ ((32::Int) : (rec ((32::Int) + (1::Int))))

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

alternateZO1'0 = 
  let _0 = (0::Int) : ((1::Int) : _0) in
  GHC.List.take (5::Int) _0

alternateZO1 = \x -> \y -> 
        let _0 = x : (y : _0) in
        _0

alternateZO0'0 = 
  let rec x x' = x' : (x : (rec x (1::Int))) in
  GHC.List.take (5::Int) ((0::Int) : (rec (0::Int) (1::Int)))

alternateZO0 = \x -> \y -> 
        let rec x' x'2 = x'2 : (x' : (rec x' y)) in
        x : (rec x y)

nrec_capt_0 = \x -> let
  rec p' x'2 = 
        let _1 = p' + x'2 in
        p' : (_1 : (rec (_1 + x'2) x))
  rec' p x' = 
        let _0 = p + x' in
        p : (_0 : (rec' (_0 + x') x))
  in ((0::Int) : (rec ((0::Int) + x) x)) ++ (x : (rec' (x + x) x))

alternateTF'0 = 
  let _0 = True : (False : _0) in
  GHC.List.take (5::Int) _0

alternateTF = 
  let _0 = True : (False : _0) in
  _0
