-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  54
-- Incl. one-shot:   2
-- Case reductions:  0
-- Field reductions: 0
-- Total nodes: 1637; Boxes: 416; Branches: 372
-- Apps: 292; Lams: 8

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Basics (foo_0,foo_1,foo_2,foo_3,fTest4,fTest3,fTest2,fTest1,fTest0,f,gTest6,gTest5,gTest4,gTest3,gTest2,gTest1,gTest0,g) where

import GHC.Num
import GHC.Real
import GHC.Types

foo_0 = \x -> 
  let _0 = x * (2::Int) in
  _0 + _0

foo_1 = \x -> (x + (1::Int)) * (2::Int)

foo_2 = \x -> (x + (1::Int)) * x

foo_3 = \x -> let
  _1 = x * x
  _0 = (x + (1::Int)) * x
  in (_0 ^ _0) - (_1 ^ _1)

fTest4 = let
  _0 = (66::Int) * (66::Int)
  _1 = (77::Int) * (77::Int)
  in (_0 * _0) + (_1 * _1)

fTest3 = let
  _0 = (77::Int) * (77::Int)
  _1 = (66::Int) * (66::Int)
  in (_1 * _1) * (_0 * _0)

fTest2 = 
  let _0 = (55::Int) * (55::Int) in
  ((44::Int) * (44::Int)) + (_0 * _0)

fTest1 = 
  let _0 = (33::Int) * (33::Int) in
  _0 * _0

fTest0 = ((11::Int) * (11::Int)) * ((22::Int) * (22::Int))

f = \x -> x * x

gTest6 = ((44::Int) * (33::Int)) * (11::Int)

gTest5 = (((11::Int) * (30::Int)) * ((30::Int) * (22::Int))) + (((11::Int) * (40::Int)) * ((40::Int) * (22::Int)))

gTest4 = ((2::Int) * (3::Int)) * ((4::Int) * (5::Int))

gTest3 = ((2::Int) * (3::Int)) * (4::Int)

gTest2 = \y -> ((2::Int) * (3::Int)) * y

gTest1 = (4::Int) * ((2::Int) * (3::Int))

gTest0 = ((2::Int) * (3::Int)) * (4::Int)

g = \x -> \y -> x * y
