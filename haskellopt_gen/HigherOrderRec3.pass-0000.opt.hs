-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 57; Boxes: 18; Branches: 10
-- Apps: 6; Lams: 6; Unreduced Redexes: 2

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderRec (rec7,rec7Test1,rec0_0,rec7Test0,rec0) where

import GHC.Base
import GHC.Num

rec0 = (\f -> (_0(# f #)))

_0(# f' #) = (f' (\x -> ((_0(# f' #)) x)))

rec0_0 = GHC.Base.id

rec7 = (\g -> (_1(# {-A-}\(ds'2) -> (_2(# g #)) #)))

_1(# g'2 #) = (\ds'2 -> (_4(# {-P-}(g'2(ds'2)), ds'2 #)))

_2(# g'4 #) = (g'4 (_4(# (_2(# (_5(# g'4 #)) #)), _6 #)))

rec7Test0 = (_1(# {-A-}\(ds'2) -> (_2(# ((GHC.Num.+) 1) #)) #))

rec7Test1 = (_1(# {-A-}\(ds'2) -> (_3(# (_4(# (_2(# (_5(# (\ds -> (_3(# ds #))) #)) #)), _6 #)) #)) #))

_3(# ds'3 #) = (((GHC.Num.*) ds'3) 2)

_4(# g', ds' #) = (case ds' of {() -> g'})

_5(# g'3 #) = g'3

_6 = ()
