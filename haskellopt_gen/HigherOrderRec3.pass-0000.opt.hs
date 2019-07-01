-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 59; Boxes: 19; Branches: 11
-- Apps: 6; Lams: 6; Unreduced Redexes: 2

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderRec (rec7,rec7Test1,rec0_0,rec7Test0,rec0) where

import GHC.Base
import GHC.Num

rec0 = (\f -> (_0(# f #)))

_0(# f' #) = (f' (\x -> ((_0(# f' #)) x)))

rec0_0 = GHC.Base.id

rec7 = (\g -> (_1(# (_2(# g #)) #)))

_1(# g' #) = (\ds -> (case ds of {() -> g'}))

_2(# g'2 #) = (g'2 (_2(# g'2 #)))

rec7Test0 = (_1(# (_2(# ((GHC.Num.+) 1) #)) #))

rec7Test1 = (_1(# (_3(# (_2(# (\ds' -> (_3(# ds' #))) #)) #)) #))

_3(# ds'2 #) = (((GHC.Num.*) ds'2) 2)
