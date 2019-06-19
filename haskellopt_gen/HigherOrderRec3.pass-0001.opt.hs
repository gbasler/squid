-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 57; Boxes: 18; Branches: 10
-- Apps: 6; Lams: 6; Unreduced Redexes: 2

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderRec (rec7,rec7Test1,ds,rec0_0,rec7Test0,rec0) where

import GHC.Base
import GHC.Num

ds = 2

rec0 = (\f -> (_0(# f #)))

_0(# f' #) = (f' (\x -> ((_0(# f' #)) x)))

rec0_0 = GHC.Base.id

rec7 = (\g -> (_2(# (_1(# g #)) #)))

_2(# g' #) = (\ds' -> (_3(# ds', g' #)))

_1(# g'2 #) = (g'2 (_3(# _4, (_1(# (_5(# g'2 #)) #)) #)))

rec7Test0 = (_2(# (_1(# ((GHC.Num.+) 1) #)) #))

rec7Test1 = (_2(# (_6(# (_3(# _4, (_1(# (_5(# (\ds'2 -> (_6(# ds'2 #))) #)) #)) #)) #)) #))

_6(# ds'3 #) = (((GHC.Num.*) ds'3) ds)

_3(# ds'4, g'3 #) = (case ds'4 of {() -> g'3})

_4 = ()

_5(# g'4 #) = g'4
