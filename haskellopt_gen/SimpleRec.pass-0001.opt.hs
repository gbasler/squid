-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 21; Boxes: 5; Branches: 2
-- Apps: 5; Lams: 1; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module SimpleRec (nats,nats21) where

import GHC.Num

nats = (\n -> (_0(# n #)))

_0(# n' #) = (((:) n') (_0(# (((GHC.Num.+) n') (GHC.Num.fromInteger 1)) #)))

nats21 = (_0(# 21 #))
