-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  32
-- Incl. one-shot:   0
-- Case reductions:  0
-- Field reductions: 0
-- Case commutings:  0
-- Total nodes: 445; Boxes: 148; Branches: 135
-- Apps: 76; Lams: 7

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Explosive (a2,a1,a0,b1,b0,a3) where

import GHC.Num
import GHC.Real
import GHC.Types

a2 = let
  _0 = (^) (((((1::Int) - (0::Int)) - (1::Int)) + (((1::Int) - (1::Int)) - (1::Int))) + (((1::Int) - (2::Int)) - (1::Int)))
  _2 = (*) (((((1::Int) - (0::Int)) - (1::Int)) + (((1::Int) - (1::Int)) - (1::Int))) + (((1::Int) - (2::Int)) - (1::Int)))
  _1 = (+) (((((1::Int) - (0::Int)) - (1::Int)) + (((1::Int) - (1::Int)) - (1::Int))) + (((1::Int) - (2::Int)) - (1::Int)))
  in ((((_1 (0::Int) + _1 (1::Int)) + _1 (2::Int)) * ((_1 (0::Int) + _1 (1::Int)) + _1 (2::Int))) * ((_1 (0::Int) + _1 (1::Int)) + _1 (2::Int))) ^ (((((_2 (0::Int) + _2 (1::Int)) + _2 (2::Int)) * ((_2 (0::Int) + _2 (1::Int)) + _2 (2::Int))) * ((_2 (0::Int) + _2 (1::Int)) + _2 (2::Int))) ^ ((((_0 (0::Int) + _0 (1::Int)) + _0 (2::Int)) * ((_0 (0::Int) + _0 (1::Int)) + _0 (2::Int))) * ((_0 (0::Int) + _0 (1::Int)) + _0 (2::Int))))

a1 = \g -> (((g (0::Int) + g (1::Int)) + g (2::Int)) * ((g (0::Int) + g (1::Int)) + g (2::Int))) * ((g (0::Int) + g (1::Int)) + g (2::Int))

a0 = \f -> (f (0::Int) + f (1::Int)) + f (2::Int)

b1 = \g -> \x -> g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g (g x))))))))))))))))))))))))))))))))))))))))))))

b0 = \f -> \x -> f (f (f x))

a3 = \x -> ((1::Int) - x) - (1::Int)
