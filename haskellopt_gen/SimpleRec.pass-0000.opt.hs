-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 19; Boxes: 4; Branches: 2
-- Apps: 5; Lams: 0; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module SimpleRec (nats21) where

import GHC.Num

nats21 = (_0(# 21 #))

_0(# n #) = (((:) n) (_0(# (((GHC.Num.+) n) (GHC.Num.fromInteger 1)) #)))
