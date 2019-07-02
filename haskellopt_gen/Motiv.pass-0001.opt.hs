-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 89; Boxes: 13; Branches: 3
-- Apps: 10; Lams: 6; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Motiv (isJust,pgrm,e1,f,e0,e3,e2) where

import GHC.Num
import GHC.Types

e0 = (\a -> a)

e1 = (\ds -> (case ds of {(,) arg0 arg1 -> (((GHC.Num.*) arg0) arg1)}))

e2 = (\z -> (_0(# z #)))

_0(# z' #) = (((GHC.Num.+) z') 1)

e3 = (\c -> (_1(# c #)))

_1(# c' #) = (case c' of {False -> 0; True -> 1})

f = (\x -> (case x of {Nothing -> (_0(# 0 #)); Just arg0' -> (((GHC.Num.*) arg0') (_1(# (case x of {Nothing -> GHC.Types.False; Just arg0'2 -> GHC.Types.True}) #)))}))

isJust = (\ds' -> (case ds' of {Nothing -> GHC.Types.False; Just arg0'3 -> GHC.Types.True}))

pgrm = (((GHC.Num.+) (((GHC.Num.*) 2) 1)) (_0(# 0 #)))
