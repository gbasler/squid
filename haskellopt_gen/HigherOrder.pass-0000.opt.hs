-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 213; Boxes: 99; Branches: 75
-- Apps: 162; Lams: 9; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrder (gTest1,hTest3,iTest0,f0,iTest1,i,g,iTest2,hTest5,h,gTest0,p1,hTest4) where

import GHC.Num
import GHC.Types

f0 = (\f -> (_0(# (f _1), (f _2) #)))

_0(# f'6, f'7 #) = (((GHC.Num.+) f'6) f'7)

_1 = (GHC.Types.I# 11#)

_2 = (GHC.Types.I# 22#)

g = (\f'3 -> (\x -> (_3(# ((_4(# f'3 #)) x), ((_4(# f'3 #)) _5) #))))

_3(# f'4, f'5 #) = (((GHC.Num.+) f'4) f'5)

_4(# f'3 #) = f'3

_5 = (GHC.Num.fromInteger 3)

gTest0 = (_3(# (_6(# 2 #)), (_6(# _5 #)) #))

_6(# ds' #) = (((GHC.Num.+) ds') 1)

gTest1 = (_3(# (_6(# (_3(# (_7(# _8 #)), _9 #)) #)), (_6(# _5 #)) #))

_7(# x'2 #) = (_6(# x'2 #))

_8 = 2

_9 = (_6(# _5 #))

h = (\f' -> (_10(# (f' _11), (f' _12) #)))

_10(# f'15, f'16 #) = (((GHC.Num.*) f'15) f'16)

_11 = (GHC.Types.I# 2#)

_12 = (GHC.Types.I# 3#)

hTest3 = (((GHC.Num.+) (_10(# (_13 _11), (_13 _12) #))) (_10(# (_14 _11), (_14 _12) #)))

_13 = ((GHC.Num.+) (GHC.Types.I# 1#))

_14 = ((GHC.Num.*) (GHC.Types.I# 2#))

hTest4 = (((GHC.Num.+) (_10(# (_15(# _11 #)), (_15(# _12 #)) #))) (_10(# (_16(# _11 #)), (_16(# _12 #)) #)))

_15(# ds'6 #) = (((GHC.Num.+) ds'6) (GHC.Types.I# 1#))

_16(# ds'7 #) = (((GHC.Num.*) ds'7) (GHC.Types.I# 2#))

hTest5 = (_10(# (_17(# _11 #)), (_17(# _12 #)) #))

_17(# x' #) = (((GHC.Num.*) x') (_10(# (_29(# _11, x' #)), (_29(# _12, x' #)) #)))

i = (\f'2 -> (_18(# {-A-}\(x'4) -> ((_19(# f'2 #)) _20), {-A-}\(x'4) -> ((_19(# f'2 #)) _21) #)))

_18(# f'10, f'11 #) = (\x'4 -> (_23(# {-P-}(f'10(x'4)), {-P-}(f'11(x'4)) #)))

_19(# f'2 #) = f'2

_20 = (\ds'3 -> (_25(# ds'3 #)))

_21 = (\ds'5 -> (_28(# ds'5 #)))

iTest0 = (_18(# {-A-}\(x'4) -> (_22(# _20 #)), {-A-}\(x'4) -> (_22(# _21 #)) #))

_22(# f'12 #) = (((GHC.Num.+) (f'12 (GHC.Types.I# 11#))) (f'12 (GHC.Types.I# 22#)))

iTest1 = (_23(# (_24(# (_25(# _26 #)), (_25(# _27 #)) #)), (_24(# (_28(# _26 #)), (_28(# _27 #)) #)) #))

_23(# f'8, f'9 #) = (((GHC.Num.+) f'8) f'9)

_24(# f'13, f'14 #) = (((GHC.Num.+) f'14) f'13)

_25(# ds'2 #) = (((GHC.Num.+) ds'2) (GHC.Types.I# 1#))

_26 = (GHC.Types.I# 22#)

_27 = (GHC.Types.I# 11#)

_28(# ds'4 #) = (((GHC.Num.*) ds'4) (GHC.Types.I# 2#))

iTest2 = (_23(# (_0(# (_25(# _1 #)), (_25(# _2 #)) #)), (_0(# (_28(# _1 #)), (_28(# _2 #)) #)) #))

p1 = (\ds -> (_6(# ds #)))

_29(# y, x'3 #) = (((GHC.Num.-) x'3) y)
