-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  58
-- Incl. one-shot:  0
-- Case reductions:  0
-- Field reductions:  0
-- Total nodes: 1274; Boxes: 384; Branches: 320
-- Apps: 257; Lams: 18

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module HigherOrder (hTest5,hTest4,hTest3,h,gTest1,gTest0,g,m1,iTest2,iTest1,iTest0,i,f0,ls1,lol) where

import GHC.Num
import GHC.Types

hTest5 = (2 * ((2 - 2) * (2 - 3))) * (3 * ((3 - 2) * (3 - 3)))

hTest4 = ((2 + 1) * (3 + 1)) - ((2 * 2) * (3 * 2))

hTest3 = let
  _0 = (+) 1
  _1 = (*) 2
  in (_0 2 * _0 3) - (_1 2 * _1 3)

h = \f -> f 2 * f 3

gTest1 = (((4 - 1) + (3 - 1)) - 1) + (3 - 1)

gTest0 = (2 - 1) + (3 - 1)

g = \f -> \x -> f x + f 3

m1 = \x -> x - 1

iTest2 = ((11 + 1) + (22 + 1)) + ((11 * 2) + (22 * 2))

iTest1 = ((11 + 1) + (22 + 1)) + ((11 * 2) + (22 * 2))

iTest0 = \x -> ((11 + 1) + (22 + 1)) + ((11 * 2) + (22 * 2))

i = \f -> \x -> f (\ds -> ds + 1) + f (\ds' -> ds' * 2)

f0 = \f -> f 11 + f 22

ls1 = (+) (11 + 22)

lol = \x -> \y -> x + y
