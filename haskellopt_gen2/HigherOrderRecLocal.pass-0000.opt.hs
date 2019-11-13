-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  37
-- Incl. one-shot:   0
-- Case reductions:  0
-- Field reductions: 0
-- Case commutings:  0
-- Total nodes: 1641; Boxes: 538; Branches: 544
-- Apps: 254; Lams: 34

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module HigherOrderRecLocal (foo_6_5,foo_6,foo_5_10,foo_5,foo_4,foo_3,foo_1,foo_0,foo) where

import GHC.Base
import GHC.Classes
import GHC.List
import GHC.Num
import GHC.Real
import GHC.Types

foo_6_5 = \x -> let
  rec' _2 = 
        let rec'3 _3 = _3 : (case mod _3 (2::Int) == (0::Int) of { True -> (rec'3 (_3 + (1::Int))); False -> (rec' (_3 * (2::Int))) }) in
        _2 : (case mod _2 (2::Int) == (0::Int) of { True -> (rec'3 (_2 + (1::Int))); False -> (rec' (_2 * (2::Int))) })
  rec _0 = 
        let rec'2 _1 = _1 : (case mod _1 (2::Int) == (0::Int) of { True -> (rec (_1 + (1::Int))); False -> (rec'2 (_1 * (2::Int))) }) in
        _0 : (case mod _0 (2::Int) == (0::Int) of { True -> (rec (_0 + (1::Int))); False -> (rec'2 (_0 * (2::Int))) })
  in GHC.List.take (5::Int) (x : (case mod x (2::Int) == (0::Int) of { True -> (rec (x + (1::Int))); False -> (rec' (x * (2::Int))) }))

foo_6 = \s -> let
  rec _2 = 
        let rec'3 _3 = _3 : (case mod _3 (2::Int) == (0::Int) of { True -> (rec (_3 + (1::Int))); False -> (rec'3 (_3 * (2::Int))) }) in
        _2 : (case mod _2 (2::Int) == (0::Int) of { True -> (rec (_2 + (1::Int))); False -> (rec'3 (_2 * (2::Int))) })
  rec' _0 = 
        let rec'2 _1 = _1 : (case mod _1 (2::Int) == (0::Int) of { True -> (rec'2 (_1 + (1::Int))); False -> (rec' (_1 * (2::Int))) }) in
        _0 : (case mod _0 (2::Int) == (0::Int) of { True -> (rec'2 (_0 + (1::Int))); False -> (rec' (_0 * (2::Int))) })
  in s : (case mod s (2::Int) == (0::Int) of { True -> (rec (s + (1::Int))); False -> (rec' (s * (2::Int))) })

foo_5_10 = let
  rec _0 = 
        let rec' _1 = _1 : ((rec (_1 + (1::Int))) ++ (rec' (_1 * (2::Int)))) in
        _0 : ((rec (_0 + (1::Int))) ++ (rec' (_0 * (2::Int))))
  rec'2 _2 = 
        let rec'3 _3 = _3 : ((rec'3 (_3 + (1::Int))) ++ (rec'2 (_3 * (2::Int)))) in
        _2 : ((rec'3 (_2 + (1::Int))) ++ (rec'2 (_2 * (2::Int))))
  in GHC.List.take (10::Int) ((23::Int) : ((rec ((23::Int) + (1::Int))) ++ (rec'2 ((23::Int) * (2::Int)))))

foo_5 = \s -> let
  rec' _2 = 
        let rec'3 _3 = _3 : ((rec'3 (_3 + (1::Int))) ++ (rec' (_3 * (2::Int)))) in
        _2 : ((rec'3 (_2 + (1::Int))) ++ (rec' (_2 * (2::Int))))
  rec _0 = 
        let rec'2 _1 = _1 : ((rec (_1 + (1::Int))) ++ (rec'2 (_1 * (2::Int)))) in
        _0 : ((rec (_0 + (1::Int))) ++ (rec'2 (_0 * (2::Int))))
  in s : ((rec (s + (1::Int))) ++ (rec' (s * (2::Int))))

foo_4 = \s -> 
  let rec _0 = _0 : (rec (_0 + (1::Int))) in
  s : (rec (s + (1::Int)))

foo_3 = \s -> 
  let rec s' = s' : (rec s') in
  s : (rec s)

foo_1 = id

foo_0 = 
  let _0 = id _0 in
  _0

foo = \f -> 
  let _0 = f _0 in
  _0
