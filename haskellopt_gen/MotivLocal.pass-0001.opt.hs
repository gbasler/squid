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

module Motiv (isJust,f,pgrm) where

import GHC.Maybe
import GHC.Num
import GHC.Types

f = (\x -> (_3(# (case x of {Nothing -> (_1(# (_0(# (case x of {Nothing -> 0; Just arg0 -> 1}) #)) #)); Just arg0' -> (_2(# arg0', (_0(# (case x of {Nothing -> 0; Just arg0 -> 1}) #)) #))}) #)))

_3(# x' #) = x'

_1(# x'2 #) = (((GHC.Num.+) (z(# x'2 #))) 1)

_2(# x'3, x'4 #) = (((GHC.Num.*) x'3) (z(# x'4 #)))

_0(# x'5 #) = x'5

isJust = (\ds -> (case ds of {Nothing -> GHC.Types.False; Just arg0'2 -> GHC.Types.True}))

pgrm = (((GHC.Num.+) (_2(# 2, 1 #))) (_3(# (_1(# (_0(# 0 #)) #)) #)))

z(# x'6 #) = x'6
