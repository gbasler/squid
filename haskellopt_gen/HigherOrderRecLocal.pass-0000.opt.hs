-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 14; Boxes: 5; Branches: 2
-- Apps: 1; Lams: 2; Unreduced Redexes: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module HOR6 (rec2,rec2_0) where

import GHC.Base

rec2 = (\f -> (f (_0(# f #))))

_0(# f' #) = (f' (_0(# f' #)))

rec2_0 = GHC.Base.id
