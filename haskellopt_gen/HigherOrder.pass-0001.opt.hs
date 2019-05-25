-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
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

ds = 1

f = (\x -> (_0(# x #)))

_0(# x'2 #) = (((GHC.Num.*) x'2) (((GHC.Num.*) (((GHC.Num.-) x'2) (GHC.Types.I# 2#))) (((GHC.Num.-) x'2) (GHC.Types.I# 3#))))

f0 = (\f' -> (_1(# (f' _2), (f' _3) #)))

_1(# f'7, f'8 #) = (((GHC.Num.+) f'7) f'8)

_2 = (GHC.Types.I# 11#)

_3 = (GHC.Types.I# 22#)

g = (\f'4 -> (\x' -> (_4(# ((_5(# f'4 #)) x'), ((_5(# f'4 #)) _6) #))))

_4(# f'5, f'6 #) = (((GHC.Num.+) f'5) f'6)

_5(# f'4 #) = f'4

_6 = (GHC.Num.fromInteger 3)

gTest0 = (_4(# (_7(# 2 #)), (_7(# _6 #)) #))

_7(# ds'4 #) = (((GHC.Num.+) ds'4) ds)

gTest1 = (((GHC.Num.+) (((GHC.Num.+) (_4(# (_7(# 2 #)), (_7(# _6 #)) #))) ds)) (((GHC.Num.+) (GHC.Num.fromInteger 3)) ds))

h = (\f'2 -> (_8(# f'2 #)))

_8(# f'16 #) = (((GHC.Num.*) (f'16 (GHC.Types.I# 2#))) (f'16 (GHC.Types.I# 3#)))

hTest3 = (((GHC.Num.+) (_8(# ((GHC.Num.+) (GHC.Types.I# 1#)) #))) (_8(# ((GHC.Num.*) (GHC.Types.I# 2#)) #)))

hTest4 = (((GHC.Num.+) (((GHC.Num.*) (((GHC.Num.+) (GHC.Types.I# 2#)) ds')) (((GHC.Num.+) (GHC.Types.I# 3#)) ds'))) (((GHC.Num.*) (((GHC.Num.*) (GHC.Types.I# 2#)) ds'2)) (((GHC.Num.*) (GHC.Types.I# 3#)) ds'2)))

ds' = (GHC.Types.I# 1#)

ds'2 = (GHC.Types.I# 2#)

hTest5 = (((GHC.Num.*) (_0(# (GHC.Types.I# 2#) #))) (_0(# (GHC.Types.I# 3#) #)))

i = (\f'3 -> (_9(# {-A-}\(x'3) -> ((_10(# f'3 #)) _11), {-A-}\(x'3) -> ((_10(# f'3 #)) _12) #)))

_9(# f'11, f'12 #) = (\x'3 -> (_14(# {-P-}(f'11(x'3)), {-P-}(f'12(x'3)) #)))

_10(# f'3 #) = f'3

_11 = (\ds'6 -> (_16(# ds'6 #)))

_12 = (\ds'8 -> (_19(# ds'8 #)))

iTest0 = (_9(# {-A-}\(x'3) -> (_13(# _11 #)), {-A-}\(x'3) -> (_13(# _12 #)) #))

_13(# f'13 #) = (((GHC.Num.+) (f'13 (GHC.Types.I# 11#))) (f'13 (GHC.Types.I# 22#)))

iTest1 = (_14(# (_15(# (_16(# _17 #)), (_16(# _18 #)) #)), (_15(# (_19(# _17 #)), (_19(# _18 #)) #)) #))

_14(# f'9, f'10 #) = (((GHC.Num.+) f'9) f'10)

_15(# f'14, f'15 #) = (((GHC.Num.+) f'15) f'14)

_16(# ds'5 #) = (((GHC.Num.+) ds'5) (GHC.Types.I# 1#))

_17 = (GHC.Types.I# 22#)

_18 = (GHC.Types.I# 11#)

_19(# ds'7 #) = (((GHC.Num.*) ds'7) (GHC.Types.I# 2#))

iTest2 = (_14(# (_1(# (_16(# _2 #)), (_16(# _3 #)) #)), (_1(# (_19(# _2 #)), (_19(# _3 #)) #)) #))

p1 = (\ds'3 -> (_7(# ds'3 #)))
