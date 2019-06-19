-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 431; Boxes: 124; Branches: 156
-- Apps: 62; Lams: 17; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrder (gTest1,lol,hTest3,ls1,iTest0,f0,iTest1,i,g,iTest2,hTest5,h,gTest0,p1,hTest4) where

import GHC.Num
import GHC.Types

f0 = (\f -> (_2(# (f _0), (f _1) #)))

_2(# f', f'2 #) = (((GHC.Num.+) f') f'2)

_0 = (GHC.Types.I# 11#)

_1 = (GHC.Types.I# 22#)

g = (\f'3 -> (\x -> (_5(# ((_3(# f'3 #)) x), ((_3(# f'3 #)) _4) #))))

_5(# f'4, f'5 #) = (((GHC.Num.+) f'4) f'5)

_3(# f'6 #) = f'6

_4 = (GHC.Num.fromInteger 3)

gTest0 = (_5(# (_6(# 2 #)), (_6(# _4 #)) #))

_6(# ds #) = (((GHC.Num.+) ds) 1)

gTest1 = (_5(# (_6(# (_5(# (_6(# 2 #)), (_6(# _4 #)) #)) #)), (_6(# _4 #)) #))

h = (\f'7 -> (_9(# (_7(# f'7 #)), (_8(# f'7 #)) #)))

_9(# f'8, f'9 #) = (((GHC.Num.*) f'8) f'9)

_8(# f'10 #) = (f'10 _10)

_7(# f'11 #) = (f'11 _11)

hTest3 = (((GHC.Num.+) (_9(# (_7(# _12 #)), (_8(# _12 #)) #))) (_9(# (_7(# _13 #)), (_8(# _13 #)) #)))

_12 = ((GHC.Num.+) (GHC.Types.I# 1#))

_13 = ((GHC.Num.*) (GHC.Types.I# 2#))

hTest4 = (((GHC.Num.+) (_9(# (_14(# _11 #)), (_14(# _10 #)) #))) (_9(# (_15(# _11 #)), (_15(# _10 #)) #)))

_14(# ds' #) = (((GHC.Num.+) ds') (GHC.Types.I# 1#))

_10 = (GHC.Types.I# 3#)

_11 = (GHC.Types.I# 2#)

_15(# ds'2 #) = (((GHC.Num.*) ds'2) (GHC.Types.I# 2#))

hTest5 = (_9(# (_16(# _11 #)), (_16(# _10 #)) #))

_16(# x' #) = (((GHC.Num.*) x') (_9(# (_17(# x', _11 #)), (_17(# x', _10 #)) #)))

i = (\f'12 -> (_21(# ((_18(# f'12 #)) (\ds'3 -> (_19(# ds'3 #)))), ((_18(# f'12 #)) (\ds'4 -> (_20(# ds'4 #)))) #)))

_21(# f'13, f'14 #) = (\x'2 -> (_22(# f'13, f'14 #)))

_18(# f'15 #) = f'15

_19(# ds'5 #) = (((GHC.Num.+) ds'5) (GHC.Types.I# 1#))

_20(# ds'6 #) = (((GHC.Num.*) ds'6) (GHC.Types.I# 2#))

iTest0 = (_21(# (_25(# (_19(# _23 #)), (_19(# _24 #)) #)), (_25(# (_20(# _23 #)), (_20(# _24 #)) #)) #))

_25(# f'16, f'17 #) = (((GHC.Num.+) f'16) f'17)

_23 = (GHC.Types.I# 11#)

_24 = (GHC.Types.I# 22#)

iTest1 = (_22(# (_28(# (_19(# _26 #)), (_19(# _27 #)) #)), (_28(# (_20(# _26 #)), (_20(# _27 #)) #)) #))

_22(# f'18, f'19 #) = (((GHC.Num.+) f'18) f'19)

_28(# f'20, f'21 #) = (((GHC.Num.+) f'20) f'21)

_26 = (GHC.Types.I# 11#)

_27 = (GHC.Types.I# 22#)

iTest2 = (_22(# (_2(# (_19(# _0 #)), (_19(# _1 #)) #)), (_2(# (_20(# _0 #)), (_20(# _1 #)) #)) #))

lol = (\x'3 -> (\y -> (_29(# x'3, y #))))

_29(# x'4, y' #) = (((GHC.Num.+) x'4) y')

ls1 = ((GHC.Num.+) (_29(# 11, 22 #)))

p1 = (\ds'7 -> (_6(# ds'7 #)))

_17(# x'5, y'2 #) = (((GHC.Num.-) x'5) y'2)
