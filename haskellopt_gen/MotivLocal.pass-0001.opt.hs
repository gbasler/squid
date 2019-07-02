-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 65; Boxes: 13; Branches: 9
-- Apps: 7; Lams: 2; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Motiv (isJust,f,pgrm) where

import GHC.Maybe
import GHC.Num
import GHC.Types

f = (\x -> let x' = (case x of {Nothing -> 0; Just arg0' -> 1}) in (case x of {Nothing -> (_0(# x' #)); Just arg0 -> (((GHC.Num.*) arg0) x')}))

_0(# x'2 #) = (((GHC.Num.+) x'2) 1)

isJust = (\ds -> (case ds of {Nothing -> GHC.Types.False; Just arg0'2 -> GHC.Types.True}))

pgrm = (((GHC.Num.+) (((GHC.Num.*) 2) 1)) (_0(# 0 #)))
