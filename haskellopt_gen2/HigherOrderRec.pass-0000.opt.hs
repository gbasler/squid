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
        _1 = _0 + (1::Int)
        _2 = _1 + (1::Int)
        rec'2 p'10 f'11 = let
              rec'11 p'11 f'12 = 
                    let _17 = p'11 + (1::Int) in
                    _17 - (f'12 (rec'11 _17 f'12) * f'12 (rec'2 _17 f'12))
              _18 = p'10 + (1::Int)
              in _18 - (f'11 (rec'11 _18 f'11) * f'11 (rec'2 _18 f'11))
        rec' p'8 f'9 = let
              _15 = p'8 + (1::Int)
              rec'10 p'9 f'10 = 
                    let _16 = p'9 + (1::Int) in
                    _16 - (f'10 (rec' _16 f'10) * f'10 (rec'10 _16 f'10))
              in _15 - (f'9 (rec' _15 f'9) * f'9 (rec'10 _15 f'9))
        rec p'6 f'7 = let
              _12 = p'6 + (1::Int)
              _13 = _12 + (1::Int)
              rec'9 p'7 f'8 = 
                    let _14 = p'7 + (1::Int) in
                    _14 - (f'8 (rec _14 f'8) * f'8 (rec'9 _14 f'8))
              in _12 - (f'7 (rec _12 f'7) * f'7 (_13 - (f'7 (rec _13 f'7) * f'7 (rec'9 _13 f'7))))
        _3 = _0 + (1::Int)
        rec'5 p'4 f'5 = let
              _9 = p'4 + (1::Int)
              rec'8 p'5 f'6 = 
                    let _11 = p'5 + (1::Int) in
                    _11 - (f'6 (rec'8 _11 f'6) * f'6 (rec'5 _11 f'6))
              _10 = _9 + (1::Int)
              in _9 - (f'5 (_10 - (f'5 (rec'8 _10 f'5) * f'5 (rec'5 _10 f'5))) * f'5 (rec'5 _9 f'5))
        _4 = _3 + (1::Int)
        rec'4 p'2 f'3 = let
              _7 = p'2 + (1::Int)
              rec'7 p'3 f'4 = 
                    let _8 = p'3 + (1::Int) in
                    _8 - (f'4 (rec'7 _8 f'4) * f'4 (rec'4 _8 f'4))
              in _7 - (f'3 (rec'7 _7 f'3) * f'3 (rec'4 _7 f'3))
        rec'3 p f' = let
              _5 = p + (1::Int)
              rec'6 p' f'2 = 
                    let _6 = p' + (1::Int) in
                    _6 - (f'2 (rec'3 _6 f'2) * f'2 (rec'6 _6 f'2))
              in _5 - (f' (rec'3 _5 f') * f' (rec'6 _5 f'))
        in _0 - (f (_1 - (f (rec _1 f) * f (_2 - (f (rec' _2 f) * f (rec'2 _2 f))))) * f (_3 - (f (_4 - (f (rec'3 _4 f) * f (rec'4 _4 f))) * f (rec'5 _3 f))))

r_2 = let
  _0 = (:) (1::Int)
  rec f = f (rec f)
  in GHC.List.take (3::Int) $ _0 (_0 (rec _0))

r_0 = 
  let _0 = (+) (1::Int) in
  \unit -> 
        let rec f = f (rec f) in
        _0 (_0 (rec _0))

r = \f -> \unit -> 
        let rec f' = f' (rec f') in
        f (f (rec f))

only_q = \f -> \x -> \y -> let
              rec x' x'2 f' = f' x'2 : (rec x'2 (f' x') f')
              _0 = f y
              _1 = f x
              in f x : (f _0 : (f _1 : (rec _1 (f _0) f)))

q_1_0 = \x -> \y -> let
        _0 = (+) (1::Int)
        rec x' x'2 f = f x'2 : (rec x'2 (f x') f)
        _1 = _0 y
        in GHC.List.take (8::Int) (_0 x : (_0 _1 : (rec _1 (_0 x) _0)))

q_1 = 
  let _0 = (+) (1::Int) in
  \x -> \y -> let
              rec x' x'2 f = f x'2 : (rec x'2 (f x') f)
              _1 = _0 y
              in _0 x : (_0 _1 : (rec _1 (_0 x) _0))

q = \f -> \x -> \y -> let
              rec x' x'2 f' = f' x'2 : (rec x'2 (f' x') f')
              _0 = f y
              in f x : (f _0 : (rec _0 (f x) f))
