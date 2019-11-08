-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  16
-- Incl. one-shot:   0
-- Case reductions:  0
-- Field reductions: 0
-- Total nodes: 207; Boxes: 65; Branches: 34
-- Apps: 42; Lams: 13

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
        _0 = x + (1::Int)
        rec' p'3 = let
              _10 = p'3 + (1::Int)
              _11 = _10 + (1::Int)
              rec'5 p'5 = let
                    _17 = p'5 + (1::Int)
                    _18 = _17 + (1::Int)
                    in _17 - (f (_18 - (f (rec'5 _18) * f (rec' _18))) * f (rec' _17))
              rec'4 p'4 = let
                    _12 = p'4 + (1::Int)
                    _15 = _12 + (1::Int)
                    _16 = _15 + (1::Int)
                    _13 = _12 + (1::Int)
                    _14 = _13 + (1::Int)
                    in _12 - (f (_13 - (f (rec'4 _13) * f (_14 - (f (rec'4 _14) * f (rec' _14))))) * f (_15 - (f (_16 - (f (rec'4 _16) * f (rec' _16))) * f (rec' _15))))
              in _10 - (f (rec'4 _10) * f (_11 - (f (rec'5 _11) * f (rec' _11))))
        rec p = let
              _1 = p + (1::Int)
              _2 = _1 + (1::Int)
              rec'2 p'2 = let
                    _8 = p'2 + (1::Int)
                    _9 = _8 + (1::Int)
                    in _8 - (f (rec _8) * f (_9 - (f (rec _9) * f (rec'2 _9))))
              rec'3 p' = let
                    _3 = p' + (1::Int)
                    _4 = _3 + (1::Int)
                    _5 = _4 + (1::Int)
                    _6 = _3 + (1::Int)
                    _7 = _6 + (1::Int)
                    in _3 - (f (_4 - (f (rec _4) * f (_5 - (f (rec _5) * f (rec'3 _5))))) * f (_6 - (f (_7 - (f (rec _7) * f (rec'3 _7))) * f (rec'3 _6))))
              in _1 - (f (_2 - (f (rec _2) * f (rec'2 _2))) * f (rec'3 _1))
        in _0 - (f (rec _0) * f (rec' _0))

r_2 = let
  _0 = (:) (1::Int)
  rec = _0 (_0 rec)
  in GHC.List.take (3::Int) $ _0 rec

r_0 = 
  let _0 = (+) (1::Int) in
  \unit -> 
        let rec = _0 (_0 rec) in
        _0 rec

r = \f -> \unit -> 
        let rec = f (f rec) in
        f rec

only_q = \f -> \x -> \y -> let
              _0 = f y
              rec x' = 
                    let _1 = f x' in
                    f (f x) : (f _1 : (rec _1))
              in f x : (f _0 : (rec _0))

q_1 = 
  let _0 = (+) (1::Int) in
  \x -> \y -> 
              let rec x' = 
                    let _1 = _0 x' in
                    _0 (_0 y) : (_0 _1 : (rec _1)) in
              _0 x : (rec x)

q = \f -> \x -> \y -> 
              let rec x' = 
                    let _0 = f x' in
                    f (f y) : (f _0 : (rec _0)) in
              f x : (rec x)