-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  16
-- Incl. one-shot:  0
-- Case reductions:  0
-- Field reductions:  0
-- Total nodes: 209; Boxes: 65; Branches: 34
-- Apps: 43; Lams: 13

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module HigherOrderp (s,r_2,r_0,r,only_q,q_1,q) where

import GHC.Base
import GHC.List
import GHC.Num
import GHC.Tuple
import GHC.Types

s = \f -> \x -> let
        _0 = x + fromInteger 1
        rec p'2 = let
              _3 = p'2 + fromInteger 1
              rec'3 p'3 = 
                    let _4 = p'3 + fromInteger 1 in
                    _4 - (f (rec _4) * f (rec'3 _4))
              in _3 - (f (rec _3) * f (rec'3 _3))
        rec' p = let
              _1 = p + fromInteger 1
              rec'2 p' = 
                    let _2 = p' + fromInteger 1 in
                    _2 - (f (rec'2 _2) * f (rec' _2))
              in _1 - (f (rec'2 _1) * f (rec' _1))
        in _0 - (f (rec _0) * f (rec' _0))

r_2 = let
  _0 = (:) 1
  rec = _0 rec
  in GHC.List.take 3 $ _0 rec

r_0 = 
  let _0 = (+) 1 in
  \unit -> 
        let rec = _0 rec in
        _0 rec

r = \f -> \unit -> 
        let rec = f rec in
        f rec

only_q = \f -> \x -> \y -> 
              let rec = f (f x) : rec in
              f x : (f (f y) : rec)

q_1 = 
  let _0 = (+) 1 in
  \x -> \y -> 
              let rec = _0 (_0 y) : rec in
              _0 x : rec

q = \f -> \x -> \y -> 
              let rec = f (f y) : rec in
              f x : rec
