-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 59; Boxes: 19; Branches: 11
-- Apps: 6; Lams: 6; Unreduced Redexes: 2

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module HigherOrderRec (rec7,rec7Test1,ds,rec0_0,rec7Test0,rec0) where

import GHC.Base
import GHC.Num

ds = 2

rec0 = (\f -> (_0(# f #)))

_0(# f' #) = (f' (\x -> ((_0(# f' #)) x)))

rec0_0 = GHC.Base.id

rec7 = (\g -> (_1(# (_2(# g #)) #)))

_1(# g' #) = (\ds' -> (case ds' of {() -> g'}))

_2(# g'2 #) = (g'2 (_2(# g'2 #)))

rec7Test0 = (_1(# (_2(# ((GHC.Num.+) 1) #)) #))

rec7Test1 = (_1(# (_3(# (_2(# (\ds'2 -> (_3(# ds'2 #))) #)) #)) #))

_3(# ds'3 #) = (((GHC.Num.*) ds'3) 2)
