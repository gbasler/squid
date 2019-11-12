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
        rec p'3 f'4 = let
              _10 = p'3 + (1::Int)
              rec'5 p'5 f'6 = let
                    _14 = p'5 + (1::Int)
                    _17 = _14 + (1::Int)
                    _18 = _17 + (1::Int)
                    _15 = _14 + (1::Int)
                    _16 = _15 + (1::Int)
                    in _14 - (f'6 (_15 - (f'6 (rec _15 f) * f'6 (_16 - (f'6 (rec _16 f) * f'6 (rec'5 _16 f'4))))) * f'6 (_17 - (f'6 (_18 - (f'6 (rec _18 f) * f'6 (rec'5 _18 f'4))) * f'6 (rec'5 _17 f'4))))
              _11 = _10 + (1::Int)
              rec'4 p'4 f'5 = let
                    _12 = p'4 + (1::Int)
                    _13 = _12 + (1::Int)
                    in _12 - (f'5 (rec _12 f) * f'5 (_13 - (f'5 (rec _13 f) * f'5 (rec'4 _13 f'4))))
              in _10 - (f'4 (_11 - (f'4 (rec _11 f) * f'4 (rec'4 _11 f'4))) * f'4 (rec'5 _10 f'4))
        rec' p f' = let
              _1 = p + (1::Int)
              _2 = _1 + (1::Int)
              rec'3 p'2 f'3 = let
                    _8 = p'2 + (1::Int)
                    _9 = _8 + (1::Int)
                    in _8 - (f'3 (_9 - (f'3 (rec'3 _9 f') * f'3 (rec' _9 f))) * f'3 (rec' _8 f))
              rec'2 p' f'2 = let
                    _3 = p' + (1::Int)
                    _6 = _3 + (1::Int)
                    _7 = _6 + (1::Int)
                    _4 = _3 + (1::Int)
                    _5 = _4 + (1::Int)
                    in _3 - (f'2 (_4 - (f'2 (rec'2 _4 f') * f'2 (_5 - (f'2 (rec'2 _5 f') * f'2 (rec' _5 f))))) * f'2 (_6 - (f'2 (_7 - (f'2 (rec'2 _7 f') * f'2 (rec' _7 f))) * f'2 (rec' _6 f))))
              in _1 - (f' (rec'2 _1 f') * f' (_2 - (f' (rec'3 _2 f') * f' (rec' _2 f))))
        in _0 - (f (rec _0 f) * f (rec' _0 f))

r_2 = let
  _0 = (:) (1::Int)
  rec f = f (f (rec _0))
  in GHC.List.take (3::Int) $ _0 (rec _0)

r_0 = 
  let _0 = (+) (1::Int) in
  \unit -> 
        let rec f = f (f (rec _0)) in
        _0 (rec _0)

r = \f -> \unit -> 
        let rec f' = f' (f' (rec f)) in
        f (rec f)

only_q = \f -> \x -> \y -> let
              _0 = f y
              _1 = f x
              rec x' x'2 f' = let
                    _2 = f' x'
                    _3 = f' x'2
                    in f' x'2 : (f' _2 : (rec _2 _1 f))
              in f x : (f _0 : (rec _0 _1 f))

q_1_0 = \x -> \y -> let
        _0 = (+) (1::Int)
        _1 = _0 y
        rec x' x'2 f = let
              _2 = f x'
              _3 = f x'2
              in f x'2 : (f _2 : (rec _2 _1 _0))
        in GHC.List.take (8::Int) (_0 x : (rec x _1 _0))

q_1 = 
  let _0 = (+) (1::Int) in
  \x -> \y -> let
              _1 = _0 y
              rec x' x'2 f = let
                    _2 = f x'
                    _3 = f x'2
                    in f x'2 : (f _2 : (rec _2 _1 _0))
              in _0 x : (rec x _1 _0)

q = \f -> \x -> \y -> let
              _0 = f y
              rec x' x'2 f' = let
                    _1 = f' x'
                    _2 = f' x'2
                    in f' x'2 : (f' _1 : (rec _1 _0 f))
              in f x : (rec x _0 f)
