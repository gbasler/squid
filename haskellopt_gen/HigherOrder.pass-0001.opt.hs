-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 402; Boxes: 100; Branches: 100
-- Apps: 91; Lams: 14; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrder (gTest1,lol,hTest3,ls1,f,iTest0,f0,iTest1,ds,i,g,iTest2,hTest5,h,gTest0,p1,hTest4) where

import GHC.Num
import GHC.Types

ds = 1

f = (\x -> (_0(# x #)))

_0(# x' #) = (((GHC.Num.*) x') (((GHC.Num.*) (((GHC.Num.-) x') (GHC.Types.I# 2#))) (((GHC.Num.-) x') (GHC.Types.I# 3#))))

f0 = (\f' -> (_3(# (f' _1), (f' _2) #)))

_3(# f'2, f'3 #) = (((GHC.Num.+) f'2) f'3)

_1 = (GHC.Types.I# 11#)

_2 = (GHC.Types.I# 22#)

g = (\f'4 -> (\x'2 -> (_6(# ((_4(# f'4 #)) x'2), ((_4(# f'4 #)) _5) #))))

_6(# f'5, f'6 #) = (((GHC.Num.+) f'5) f'6)

_4(# f'7 #) = f'7

_5 = (GHC.Num.fromInteger 3)

gTest0 = (_6(# (_7(# 2 #)), (_7(# _5 #)) #))

_7(# ds' #) = (((GHC.Num.+) ds') ds)

gTest1 = (((GHC.Num.+) (((GHC.Num.+) (_6(# (_7(# 2 #)), (_7(# _5 #)) #))) ds)) (((GHC.Num.+) (GHC.Num.fromInteger 3)) ds))

h = (\f'8 -> (_8(# f'8 #)))

_8(# f'9 #) = (((GHC.Num.*) (f'9 (GHC.Types.I# 2#))) (f'9 (GHC.Types.I# 3#)))

hTest3 = (((GHC.Num.+) (_8(# ((GHC.Num.+) (GHC.Types.I# 1#)) #))) (_8(# ((GHC.Num.*) (GHC.Types.I# 2#)) #)))

hTest4 = (((GHC.Num.+) (((GHC.Num.*) (((GHC.Num.+) (GHC.Types.I# 2#)) ds'2)) (((GHC.Num.+) (GHC.Types.I# 3#)) ds'2))) (((GHC.Num.*) (((GHC.Num.*) (GHC.Types.I# 2#)) ds'3)) (((GHC.Num.*) (GHC.Types.I# 3#)) ds'3)))

ds'2 = (GHC.Types.I# 1#)

ds'3 = (GHC.Types.I# 2#)

hTest5 = (((GHC.Num.*) (_0(# (GHC.Types.I# 2#) #))) (_0(# (GHC.Types.I# 3#) #)))

i = (\f'10 -> (_12(# ((_9(# f'10 #)) (\ds'4 -> (_10(# ds'4 #)))), ((_9(# f'10 #)) (\ds'5 -> (_11(# ds'5 #)))) #)))

_12(# f'11, f'12 #) = (\x'3 -> (_13(# f'11, f'12 #)))

_9(# f'13 #) = f'13

_10(# ds'6 #) = (((GHC.Num.+) ds'6) (GHC.Types.I# 1#))

_11(# ds'7 #) = (((GHC.Num.*) ds'7) (GHC.Types.I# 2#))

iTest0 = (_12(# (_16(# (_10(# _14 #)), (_10(# _15 #)) #)), (_16(# (_11(# _14 #)), (_11(# _15 #)) #)) #))

_16(# f'14, f'15 #) = (((GHC.Num.+) f'14) f'15)

_15 = (GHC.Types.I# 22#)

_14 = (GHC.Types.I# 11#)

iTest1 = (_13(# (_19(# (_10(# _17 #)), (_10(# _18 #)) #)), (_19(# (_11(# _17 #)), (_11(# _18 #)) #)) #))

_13(# f'16, f'17 #) = (((GHC.Num.+) f'16) f'17)

_19(# f'18, f'19 #) = (((GHC.Num.+) f'18) f'19)

_17 = (GHC.Types.I# 11#)

_18 = (GHC.Types.I# 22#)

iTest2 = (_13(# (_3(# (_10(# _1 #)), (_10(# _2 #)) #)), (_3(# (_11(# _1 #)), (_11(# _2 #)) #)) #))

lol = (\x'4 -> (\y -> (_20(# x'4, y #))))

_20(# x'5, y' #) = (((GHC.Num.+) x'5) y')

ls1 = ((GHC.Num.+) (_20(# 11, 22 #)))

p1 = (\ds'8 -> (_7(# ds'8 #)))
