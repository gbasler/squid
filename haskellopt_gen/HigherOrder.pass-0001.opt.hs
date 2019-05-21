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

module HigherOrder (gTest1,hTest3,f,iTest0,f0,iTest1,ds,i,g,iTest2,hTest5,h,gTest0,p1,hTest4) where

import GHC.Num
import GHC.Types

_0(# _1 #) = (((GHC.Num.*) _1) (((GHC.Num.*) (((GHC.Num.-) _1) (GHC.Types.I# 2#))) (((GHC.Num.-) _1) (GHC.Types.I# 3#))))

_2(# f_a #) = f_a

_3(# f_a' #) = f_a'

_4 = (GHC.Num.fromInteger 3)

_5(# _6, _7 #) = (((GHC.Num.+) _7) _6)

_8(# _9 #) = (((GHC.Num.+) _9) 1)

_10 = (GHC.Types.I# 11#)

_11 = (GHC.Types.I# 22#)

_12(# _13, _14 #) = (((GHC.Num.+) _13) _14)

_15(# _16 #) = (((GHC.Num.+) _16) (GHC.Types.I# 1#))

_17 = (\ds_d'2 -> (_15(# ds_d'2 #)))

_18(# _19 #) = (((GHC.Num.*) _19) (GHC.Types.I# 2#))

_20 = (\ds_d'3 -> (_18(# ds_d'3 #)))

_21(# _22, _23 #) = (((GHC.Num.+) _22) _23)

_24(# _25, _26 #) = (\x_a -> (_21(# (_25(x_a)), (_26(x_a)) #)))

_27(# _28 #) = (((GHC.Num.+) (_28 (GHC.Types.I# 11#))) (_28 (GHC.Types.I# 22#)))

_29 = (GHC.Types.I# 11#)

_30 = (GHC.Types.I# 22#)

_31(# _32, _33 #) = (((GHC.Num.+) _33) _32)

_34(# _35 #) = (((GHC.Num.*) (_35 (GHC.Types.I# 2#))) (_35 (GHC.Types.I# 3#)))

ds = 1

ds_d = (GHC.Types.I# 2#)

ds_d' = (GHC.Types.I# 1#)

f = (\x_a' -> (_0(# x_a' #)))

f0 = (\f_a'2 -> (_12(# (f_a'2 _10), (f_a'2 _11) #)))

g = (\f_a' -> (\x_a'2 -> (_5(# ((_3(# f_a' #)) _4), ((_3(# f_a' #)) x_a'2) #))))

gTest0 = (_5(# (_8(# _4 #)), (_8(# 2 #)) #))

gTest1 = (((GHC.Num.+) (((GHC.Num.+) (_5(# (_8(# _4 #)), (_8(# 2 #)) #))) 1)) (((GHC.Num.+) (GHC.Num.fromInteger 3)) 1))

h = (\f_X -> (_34(# f_X #)))

hTest3 = (((GHC.Num.+) (_34(# ((GHC.Num.+) (GHC.Types.I# 1#)) #))) (_34(# ((GHC.Num.*) (GHC.Types.I# 2#)) #)))

hTest4 = (((GHC.Num.+) (((GHC.Num.*) (((GHC.Num.+) (GHC.Types.I# 2#)) ds_d')) (((GHC.Num.+) (GHC.Types.I# 3#)) ds_d'))) (((GHC.Num.*) (((GHC.Num.*) (GHC.Types.I# 2#)) ds_d)) (((GHC.Num.*) (GHC.Types.I# 3#)) ds_d)))

hTest5 = (((GHC.Num.*) (_0(# (GHC.Types.I# 2#) #))) (_0(# (GHC.Types.I# 3#) #)))

i = (\f_a -> (_24(# \(x_a) -> ((_2(# f_a #)) _17), \(x_a) -> ((_2(# f_a #)) _20) #)))

iTest0 = (_24(# \(x_a) -> (_27(# _17 #)), \(x_a) -> (_27(# _20 #)) #))

iTest1 = (_21(# (_31(# (_15(# _30 #)), (_15(# _29 #)) #)), (_31(# (_18(# _30 #)), (_18(# _29 #)) #)) #))

iTest2 = (_21(# (_12(# (_15(# _10 #)), (_15(# _11 #)) #)), (_12(# (_18(# _10 #)), (_18(# _11 #)) #)) #))

p1 = (\ds_d'4 -> (_8(# ds_d'4 #)))
