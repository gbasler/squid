-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  54
-- Incl. one-shot:  2
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
  let sh = x * 2 in
  sh + sh

foo_1 = \x' -> (x' + 1) * fromInteger 2

foo_2 = \x'2 -> (x'2 + 1) * x'2

foo_3 = \x'3 -> 
  let sh'2 = x'3 * x'3 in
  let sh' = (x'3 + 1) * x'3 in
  (sh' ^ sh') - (sh'2 ^ sh'2)

fTest4 = 
  let sh'3 = 77 * 77 in
  let sh'4 = 66 * 66 in
  (sh'4 * sh'4) + (sh'3 * sh'3)

fTest3 = 
  let sh'5 = 77 * 77 in
  let sh'6 = 66 * 66 in
  (sh'6 * sh'6) * (sh'5 * sh'5)

fTest2 = 
  let sh'7 = 55 * 55 in
  (44 * 44) + (sh'7 * sh'7)

fTest1 = 
  let sh'8 = 33 * 33 in
  sh'8 * sh'8

fTest0 = (11 * 11) * (22 * 22)

f = \x'4 -> x'4 * x'4

gTest6 = (44 * 33) * 11

gTest5 = 
  let sh'9 = (*) 11 in
  (sh'9 30 * (30 * 22)) + (sh'9 40 * (40 * 22))

gTest4 = (2 * 3) * (4 * 5)

gTest3 = (2 * 3) * 4

gTest2 = \y -> (2 * 3) * y

gTest1 = 4 * (2 * 3)

gTest0 = (2 * 3) * 4

g = \x'5 -> \y -> x'5 * y
