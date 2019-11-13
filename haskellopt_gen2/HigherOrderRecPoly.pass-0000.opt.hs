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
  let rec _0 = _0 (\x' -> (rec (id _0)) (x' + (1::Int))) in
  GHC.List.take (5::Int) (a : (rec (id (\h -> \x -> x : h (x + (1::Int))))) ((a + (1::Int)) + (1::Int)))

p1_6 = \x -> 
  let rec _0 = _0 (\x'2 -> (rec (id _0)) (x'2 + (1::Int))) in
  x : (rec (id (\h -> \x' -> x' : h (x' + (1::Int))))) ((x + (1::Int)) + (1::Int))

p1_5 = 
  let λ = \h -> \x -> h (x + (1::Int)) * h (x - (1::Int)) in
  \x' -> let
        rec' _1 = _1 (\x'3 -> (rec' (id _1)) (x'3 + (1::Int)))
        rec _0 = _0 (\x'2 -> (rec (id _0)) (x'2 + (1::Int)))
        in (rec (id λ)) ((x' + (1::Int)) + (1::Int)) * (rec' (id λ)) ((x' - (1::Int)) + (1::Int))

p1_4 = \x -> 
  let rec _0 = _0 (\x'2 -> (rec (id _0)) (x'2 + (1::Int))) in
  (rec (id (\h -> \x' -> h x'))) (x + (1::Int))

p1_3 = \x -> x

p1_2 = \x -> 
  let rec _0 = _0 (\x' -> (rec (id _0)) (x' + (1::Int))) in
  (rec (id (\h -> h))) (x + (1::Int))

p1_1 = id (\x -> 
  let rec _0 = _0 (\x' -> (rec (id _0)) (x' + (1::Int))) in
  (rec (id id)) (x + (1::Int)))

p1 = \f -> f (\x -> 
        let rec _0 = _0 (\x' -> (rec (id _0)) (x' + (1::Int))) in
        (rec (id f)) (x + (1::Int)))

only_p2 = \f -> 
  let rec _0 = _0 (rec (id _0)) in
  f (rec (id f))

only_p1 = \f -> f (\x -> 
        let rec _0 = _0 (\x' -> (rec (id _0)) x') in
        (rec (id f)) x)

only_p0 = \f -> f (\x -> 
        let rec f' = f' (\x' -> (rec f') x') in
        (rec f) x)
