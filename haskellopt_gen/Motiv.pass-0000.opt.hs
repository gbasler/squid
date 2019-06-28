-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 120; Boxes: 32; Branches: 25
-- Apps: 9; Lams: 6; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Motiv (isJust,pgrm,e1,f,e0,e3,e2) where

import GHC.Maybe
import GHC.Num
import GHC.Types

e0 = (\a -> a)

e1 = (\ds -> (case ds of {(,) arg0 arg1 -> (_0(# arg0, arg1 #))}))

_0(# ds', ds'2 #) = (((GHC.Num.*) ds') ds'2)

e2 = (\z -> (_1(# z #)))

_1(# z' #) = (((GHC.Num.+) z') 1)

e3 = (\c -> (_3(# (_2(# c #)) #)))

_3(# c' #) = c'

_2(# c'2 #) = (case c'2 of {False -> 0; True -> 1})

f = (\x -> (_9(# (_8(# (case x of {Nothing -> (_6(# (_3(# (_2(# (_5(# (_4(# x #)) #)) #)) #)) #)); Just arg0' -> (_7(# (_3(# (_2(# (_5(# (_4(# x #)) #)) #)) #)), arg0' #))}) #)) #)))

_9(# x' #) = x'

_8(# x'2 #) = x'2

_6(# x'3 #) = (_1(# x'3 #))

_7(# x'4, x'5 #) = (_0(# x'5, x'4 #))

_5(# ds'3 #) = ds'3

_4(# ds'4 #) = (case ds'4 of {Nothing -> GHC.Types.False; Just arg0'2 -> GHC.Types.True})

isJust = (\ds'5 -> (_5(# (_4(# ds'5 #)) #)))

pgrm = (((GHC.Num.+) (_9(# (_7(# 1, 2 #)) #))) (_9(# (_8(# (_6(# (_3(# 0 #)) #)) #)) #)))
