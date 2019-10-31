-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  58
-- Incl. one-shot:  0
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
  β = (+) 1
  β' = (*) 2
  in (β 2 * β 3) - (β' 2 * β' 3)

h = \f_ε -> f_ε 2 * f_ε 3

gTest1 = (((4 - 1) + (3 - 1)) - 1) + (3 - 1)

gTest0 = (2 - 1) + (3 - 1)

g = \f_ε' -> \x_ε -> f_ε' x_ε + f_ε' 3

m1 = \x_ε' -> x_ε' - 1

iTest2 = ((11 + 1) + (22 + 1)) + ((11 * 2) + (22 * 2))

iTest1 = ((11 + 1) + (22 + 1)) + ((11 * 2) + (22 * 2))

iTest0 = \x_ε'2 -> ((11 + 1) + (22 + 1)) + ((11 * 2) + (22 * 2))

i = \f_ε'2 -> \x_ε'3 -> f_ε'2 (\ds_ε -> ds_ε + 1) + f_ε'2 (\ds_ε' -> ds_ε' * 2)

f0 = \f_ε'3 -> f_ε'3 11 + f_ε'3 22

ls1 = (+) (11 + 22)

lol = \x_ε'4 -> \y_ε -> x_ε'4 + y_ε
