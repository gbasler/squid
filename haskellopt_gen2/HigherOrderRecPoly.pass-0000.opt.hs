-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  20
-- Incl. one-shot:   0
-- Case reductions:  0
-- Field reductions: 0
-- Case commutings:  0
-- Total nodes: 705; Boxes: 178; Branches: 134
-- Apps: 143; Lams: 78

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module HigherOrderp (p1_6_5,p1_6,p1_5,p1_4,p1_3,p1_2,p1_1,p1,only_p2,only_p1,only_p0) where

import GHC.Base
import GHC.List
import GHC.Num
import GHC.Types

p1_6_5 = \a -> 
  let _0 = id (\h -> \x'2 -> x'2 : h (x'2 + (1::Int))) in
  GHC.List.take (5::Int) (a : _0 (\x -> 
        let rec p = p (\x' -> (rec (id p)) (x' + (1::Int))) in
        (rec (id _0)) (x + (1::Int))) ((a + (1::Int)) + (1::Int)))

p1_6 = \x -> 
  let _0 = id (\h -> \x'3 -> x'3 : h (x'3 + (1::Int))) in
  x : _0 (\x' -> 
        let rec p = p (\x'2 -> (rec (id p)) (x'2 + (1::Int))) in
        (rec (id _0)) (x' + (1::Int))) ((x + (1::Int)) + (1::Int))

p1_5 = 
  let λ = \h -> \x -> h (x + (1::Int)) * h (x - (1::Int)) in
  \x' -> let
        _0 = id λ
        _1 = id λ
        in _0 (\x'2 -> 
              let rec p = p (\x'3 -> (rec (id p)) (x'3 + (1::Int))) in
              (rec (id _0)) (x'2 + (1::Int))) ((x' + (1::Int)) + (1::Int)) * _1 (\x'4 -> 
              let rec' p' = p' (\x'5 -> (rec' (id p')) (x'5 + (1::Int))) in
              (rec' (id _1)) (x'4 + (1::Int))) ((x' - (1::Int)) + (1::Int))

p1_4 = \x -> 
  let _0 = id (\h -> \x'3 -> h x'3) in
  _0 (\x' -> 
        let rec p = p (\x'2 -> (rec (id p)) (x'2 + (1::Int))) in
        (rec (id _0)) (x' + (1::Int))) (x + (1::Int))

p1_3 = \x -> x

p1_2 = \x -> 
  let _0 = id (\h -> h) in
  _0 (\x' -> 
        let rec p = p (\x'2 -> (rec (id p)) (x'2 + (1::Int))) in
        (rec (id _0)) (x' + (1::Int))) (x + (1::Int))

p1_1 = id (\x -> 
  let _0 = id id in
  _0 (\x' -> 
        let rec p = p (\x'2 -> (rec (id p)) (x'2 + (1::Int))) in
        (rec (id _0)) (x' + (1::Int))) (x + (1::Int)))

p1 = \f -> f (\x -> 
        let _0 = id f in
        _0 (\x' -> 
              let rec p = p (\x'2 -> (rec (id p)) (x'2 + (1::Int))) in
              (rec (id _0)) (x' + (1::Int))) (x + (1::Int)))

only_p2 = \f -> let
  rec p = p (rec (id p))
  _0 = id f
  in f (_0 (rec (id _0)))

only_p1 = \f -> f (\x -> 
        let _0 = id f in
        _0 (\x' -> 
              let rec p = p (\x'2 -> (rec (id p)) x'2) in
              (rec (id _0)) x') x)

only_p0 = \f -> f (\x -> f (\x' -> 
              let rec f' = f' (\x'2 -> (rec f') x'2) in
              (rec f) x') x)
