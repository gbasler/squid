-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrder (hTest3,f,hTest5,h,hTest4) where

import GHC.Num
import GHC.Types

_0(# _1 #) = (((GHC.Num.*) (_1 (GHC.Types.I# 2#))) (_1 (GHC.Types.I# 3#)))

_2(# _3 #) = (((GHC.Num.*) _3) (((GHC.Num.*) (((GHC.Num.-) _3) (GHC.Types.I# 2#))) (((GHC.Num.-) _3) (GHC.Types.I# 3#))))

ds_d = (GHC.Types.I# 1#)

ds_d' = (GHC.Types.I# 2#)

f = (\x_a -> (_2(# x_a #)))

h = (\f_X -> (_0(# f_X #)))

hTest3 = (((GHC.Num.+) (_0(# ((GHC.Num.+) (GHC.Types.I# 1#)) #))) (_0(# ((GHC.Num.*) (GHC.Types.I# 2#)) #)))

hTest4 = (((GHC.Num.+) (((GHC.Num.*) (((GHC.Num.+) (GHC.Types.I# 2#)) ds_d)) (((GHC.Num.+) (GHC.Types.I# 3#)) ds_d))) (((GHC.Num.*) (((GHC.Num.*) (GHC.Types.I# 2#)) ds_d')) (((GHC.Num.*) (GHC.Types.I# 3#)) ds_d')))

hTest5 = (((GHC.Num.*) (_2(# (GHC.Types.I# 2#) #))) (_2(# (GHC.Types.I# 3#) #)))
