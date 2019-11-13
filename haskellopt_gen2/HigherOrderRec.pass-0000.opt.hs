-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  18
-- Incl. one-shot:   0
-- Case reductions:  0
-- Field reductions: 0
-- Case commutings:  0
-- Total nodes: 274; Boxes: 86; Branches: 49
-- Apps: 51; Lams: 15

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module HigherOrderp (s,r_2,r_0,r,only_q,q_1_0,q_1,q) where

import GHC.Base
import GHC.List
import GHC.Num
import GHC.Tuple
import GHC.Types

s = \f -> \x -> let
        _0 = x + (1::Int)
        rec _5 f'3 = let
              rec'3 _6 f'4 = 
                    let _7 = _6 + (1::Int) in
                    _7 - (f'4 (rec _7 f'4) * f'4 (rec'3 _7 f'4))
              _8 = _5 + (1::Int)
              in _8 - (f'3 (rec _8 f'3) * f'3 (rec'3 _8 f'3))
        rec' _1 f' = let
              _2 = _1 + (1::Int)
              rec'2 _3 f'2 = 
                    let _4 = _3 + (1::Int) in
                    _4 - (f'2 (rec'2 _4 f'2) * f'2 (rec' _4 f'2))
              in _2 - (f' (rec'2 _2 f') * f' (rec' _2 f'))
        in _0 - (f (rec _0 f) * f (rec' _0 f))

r_2 = let
  _0 = (:) (1::Int)
  rec f = f (rec f)
  in GHC.List.take (3::Int) $ _0 (rec _0)

r_0 = 
  let _0 = (+) (1::Int) in
  \unit -> 
        let rec f = f (rec f) in
        _0 (rec _0)

r = \f -> \unit -> 
        let rec f' = f' (rec f') in
        f (rec f)

only_q = \f -> \x -> \y -> let
              rec x' x'2 f' = f' x'2 : (rec x'2 (f' x') f')
              _0 = f y
              in f x : (f _0 : (rec _0 (f x) f))

q_1_0 = \x -> \y -> let
        _0 = (+) (1::Int)
        rec x' x'2 f = f x'2 : (rec x'2 (f x') f)
        in GHC.List.take (8::Int) (_0 x : (rec x (_0 y) _0))

q_1 = 
  let _0 = (+) (1::Int) in
  \x -> \y -> 
              let rec x' x'2 f = f x'2 : (rec x'2 (f x') f) in
              _0 x : (rec x (_0 y) _0)

q = \f -> \x -> \y -> 
              let rec x' x'2 f' = f' x'2 : (rec x'2 (f' x') f') in
              f x : (rec x (f y) f)
