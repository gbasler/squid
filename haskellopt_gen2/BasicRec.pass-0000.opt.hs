-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  42
-- Incl. one-shot:   0
-- Case reductions:  0
-- Field reductions: 0
-- Case commutings:  0
-- Total nodes: 544; Boxes: 126; Branches: 74
-- Apps: 135; Lams: 21

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module BasicRec (trec_0_1,trec_0_0,trec_0,trec_1,nrec_0,nrec_1,alternate123_3'0,alternate123_3,alternate123_2'0,alternate123_2,alternate123_1'0,alternate123_1,alternate123_0'0,alternate123_0,alternateZO_1'0,alternateZO_1,alternateZO_0'0,alternateZO_0,nrec_capt_0,alternateTF'0,alternateTF) where

import GHC.Base
import GHC.List
import GHC.Num
import GHC.Types

trec_0_1 = let
  rec _0 = _0 : (rec (_0 + (1::Int)))
  rec' _2 = _2 : (rec' (_2 + (1::Int)))
  _1 = GHC.List.head ((32::Int) : (rec' ((32::Int) + (1::Int))))
  in _1 : (rec (_1 + (1::Int)))

trec_0_0 = let
  rec _0 = _0 : (rec (_0 + (1::Int)))
  rec' _1 = _1 : (rec' (_1 + (1::Int)))
  in ((27::Int) : (rec ((27::Int) + (1::Int)))) ++ ((32::Int) : (rec' ((32::Int) + (1::Int))))

trec_0 = \y -> 
  let rec _0 = _0 : (rec (_0 + (1::Int))) in
  y : (rec (y + (1::Int)))

trec_1 = \x -> let
  rec _1 = _1 : (rec (_1 + (1::Int)))
  rec' _0 = _0 : (rec' (_0 + (1::Int)))
  in ((0::Int) : (rec ((0::Int) + (1::Int)))) ++ (x : (rec' (x + (1::Int))))

nrec_0 = \x -> 
  let rec _0 = _0 : (rec (_0 + (1::Int))) in
  x : (rec (x + (1::Int)))

nrec_1 = \x -> let
  rec _1 = _1 : (rec (_1 + (1::Int)))
  rec' _0 = _0 : (rec' (_0 + (1::Int)))
  in ((0::Int) : (rec ((0::Int) + (1::Int)))) ++ (x : (rec' (x + (1::Int))))

alternate123_3'0 = 
  let rec z y x = x : (y : (z : (rec z y x))) in
  GHC.List.take (10::Int) ((1::Int) : ((2::Int) : ((3::Int) : (rec (3::Int) (2::Int) (1::Int)))))

alternate123_3 = \x -> \y -> \z -> 
              let rec z' y' x' = x' : (y' : (z' : (rec z' y' x'))) in
              x : (y : (z : (rec z y x)))

alternate123_2'0 = 
  let rec z z' y x = z : (x : (y : (rec z' z' y x))) in
  GHC.List.take (10::Int) ((1::Int) : ((2::Int) : (rec (3::Int) (3::Int) (2::Int) (1::Int))))

alternate123_2 = \x -> \y -> \z -> 
              let rec z' z'2 y' x' = z' : (x' : (y' : (rec z'2 z'2 y' x'))) in
              x : (y : (rec z z y x))

alternate123_1'0 = 
  let _0 = (1::Int) : ((2::Int) : ((3::Int) : _0)) in
  GHC.List.take (10::Int) _0

alternate123_1 = \x -> \y -> \z -> 
              let _0 = x : (y : (z : _0)) in
              _0

alternate123_0'0 = 
  let rec x y x' = x' : (rec x' x y) in
  GHC.List.take (10::Int) ((1::Int) : (rec (1::Int) (3::Int) (2::Int)))

alternate123_0 = \x -> \y -> \z -> 
              let rec x' y' x'2 = x'2 : (rec x'2 x' y') in
              x : (rec x z y)

alternateZO_1'0 = 
  let _0 = (0::Int) : ((1::Int) : _0) in
  GHC.List.take (5::Int) _0

alternateZO_1 = \x -> \y -> 
        let _0 = x : (y : _0) in
        _0

alternateZO_0'0 = 
  let rec x x' = x' : (rec x' x) in
  GHC.List.take (5::Int) ((0::Int) : (rec (0::Int) (1::Int)))

alternateZO_0 = \x -> \y -> 
        let rec x' x'2 = x'2 : (rec x'2 x') in
        x : (rec x y)

nrec_capt_0 = \x -> let
  rec _1 x'2 = _1 : (rec (_1 + x'2) x'2)
  rec' _0 x' = _0 : (rec' (_0 + x') x')
  in ((0::Int) : (rec ((0::Int) + x) x)) ++ (x : (rec' (x + x) x))

alternateTF'0 = 
  let _0 = True : (False : _0) in
  GHC.List.take (5::Int) _0

alternateTF = 
  let _0 = True : (False : _0) in
  _0
