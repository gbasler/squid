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

rec7 = (\g -> (_1(# {-A-}\(ds'3) -> (_2(# g #)) #)))

_1(# g'2 #) = (\ds'3 -> (_4(# {-P-}(g'2(ds'3)), ds'3 #)))

_2(# g'4 #) = (g'4 (_4(# (_2(# (_5(# g'4 #)) #)), _6 #)))

rec7Test0 = (_1(# {-A-}\(ds'3) -> (_2(# ((GHC.Num.+) 1) #)) #))

rec7Test1 = (_1(# {-A-}\(ds'3) -> (_3(# (_4(# (_2(# (_5(# (\ds' -> (_3(# ds' #))) #)) #)), _6 #)) #)) #))

_3(# ds'4 #) = (((GHC.Num.*) ds'4) ds)

_4(# g', ds'2 #) = (case ds'2 of {() -> g'})

_5(# g'3 #) = g'3

_6 = ()
