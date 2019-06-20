-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 14; Boxes: 5; Branches: 2
-- Apps: 1; Lams: 2; Unreduced Redexes: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HOR6 (rec2,rec2_0) where

import GHC.Base

rec2 = (\f -> (rec(# (_1(# (_0(# f #)), f #)) #)))

rec(# f' #) = f'

_1(# f'2, f'3 #) = (f'3 (rec(# f'2 #)))

_0(# f'4 #) = (_1(# (_0(# f'4 #)), f'4 #))

rec2_0 = GHC.Base.id
