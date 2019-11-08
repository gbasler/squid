-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  58
-- Incl. one-shot:   0
-- Case reductions:  0
-- Field reductions: 0
-- Total nodes: 1274; Boxes: 384; Branches: 320
-- Apps: 257; Lams: 18

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module HigherOrder (hTest5,hTest4,hTest3,h,gTest1,gTest0,g,m1,iTest2,iTest1,iTest0,i,f0,ls1,lol) where

import GHC.Num
import GHC.Types

hTest5 = ((2::Int) * (((2::Int) - (2::Int)) * ((2::Int) - (3::Int)))) * ((3::Int) * (((3::Int) - (2::Int)) * ((3::Int) - (3::Int))))

hTest4 = (((2::Int) + (1::Int)) * ((3::Int) + (1::Int))) - (((2::Int) * (2::Int)) * ((3::Int) * (2::Int)))

hTest3 = let
  _0 = (+) (1::Int)
  _1 = (*) (2::Int)
  in (_0 (2::Int) * _0 (3::Int)) - (_1 (2::Int) * _1 (3::Int))

h = \f -> f (2::Int) * f (3::Int)

gTest1 = ((((4::Int) - (1::Int)) + ((3::Int) - (1::Int))) - (1::Int)) + ((3::Int) - (1::Int))

gTest0 = ((2::Int) - (1::Int)) + ((3::Int) - (1::Int))

g = \f -> \x -> f x + f (3::Int)

m1 = \x -> x - (1::Int)

iTest2 = (((11::Int) + (1::Int)) + ((22::Int) + (1::Int))) + (((11::Int) * (2::Int)) + ((22::Int) * (2::Int)))

iTest1 = (((11::Int) + (1::Int)) + ((22::Int) + (1::Int))) + (((11::Int) * (2::Int)) + ((22::Int) * (2::Int)))

iTest0 = \x -> (((11::Int) + (1::Int)) + ((22::Int) + (1::Int))) + (((11::Int) * (2::Int)) + ((22::Int) * (2::Int)))

i = \f -> \x -> f (\ds -> ds + (1::Int)) + f (\ds' -> ds' * (2::Int))

f0 = \f -> f (11::Int) + f (22::Int)

ls1 = (+) ((11::Int) + (22::Int))

lol = \x -> \y -> x + y
