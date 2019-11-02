-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  54
-- Incl. one-shot:  2
-- Case reductions:  0
-- Field reductions:  0
-- Total nodes: 1639; Boxes: 416; Branches: 372
-- Apps: 293; Lams: 8

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Basics (foo_0,foo_1,foo_2,foo_3,fTest4,fTest3,fTest2,fTest1,fTest0,f,gTest6,gTest5,gTest4,gTest3,gTest2,gTest1,gTest0,g) where

import GHC.Num
import GHC.Real
import GHC.Types

foo_0 = \x -> 
  let _0 = x * 2 in
  _0 + _0

foo_1 = \x -> (x + 1) * fromInteger 2

foo_2 = \x -> (x + 1) * x

foo_3 = \x -> let
  _0 = (x + 1) * x
  _1 = x * x
  in (_0 ^ _0) - (_1 ^ _1)

fTest4 = let
  _0 = 77 * 77
  _1 = 66 * 66
  in (_1 * _1) + (_0 * _0)

fTest3 = let
  _0 = 66 * 66
  _1 = 77 * 77
  in (_0 * _0) * (_1 * _1)

fTest2 = 
  let _0 = 55 * 55 in
  (44 * 44) + (_0 * _0)

fTest1 = 
  let _0 = 33 * 33 in
  _0 * _0

fTest0 = (11 * 11) * (22 * 22)

f = \x -> x * x

gTest6 = (44 * 33) * 11

gTest5 = ((11 * 30) * (30 * 22)) + ((11 * 40) * (40 * 22))

gTest4 = (2 * 3) * (4 * 5)

gTest3 = (2 * 3) * 4

gTest2 = \y -> (2 * 3) * y

gTest1 = 4 * (2 * 3)

gTest0 = (2 * 3) * 4

g = \x -> \y -> x * y
