-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 224; Boxes: 112; Branches: 86
-- Apps: 175; Lams: 9; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrder (gTest1,hTest3,iTest0,f0,iTest1,i,g,iTest2,hTest5,h,gTest0,p1,hTest4) where

import GHC.Num
import GHC.Types

f0 = (\f -> (_0(# (f _1), (f _2) #)))

_0(# f'6, f'7 #) = (((GHC.Num.+) f'6) f'7)

_1 = (GHC.Types.I# 11#)

_2 = (GHC.Types.I# 22#)

g = (\f' -> (\x -> (_3(# (f' x), (f' _4) #))))

_3(# f'4, f'5 #) = (((GHC.Num.+) f'4) f'5)

_4 = (GHC.Num.fromInteger 3)

gTest0 = (_3(# (_5(# 2 #)), (_5(# _4 #)) #))

_5(# ds'3 #) = (((GHC.Num.+) ds'3) 1)

gTest1 = (_3(# (_5(# (_3(# (_6(# _7 #)), _8 #)) #)), (_5(# _4 #)) #))

_6(# x'4 #) = (_5(# x'4 #))

_7 = 2

_8 = (_5(# _4 #))

h = (\f'2 -> (_9(# (f'2 _10), (f'2 _11) #)))

_9(# f'16, f'17 #) = (((GHC.Num.*) f'16) f'17)

_10 = (GHC.Types.I# 2#)

_11 = (GHC.Types.I# 3#)

hTest3 = (((GHC.Num.+) (_9(# (_12 _10), (_12 _11) #))) (_9(# (_13 _10), (_13 _11) #)))

_12 = ((GHC.Num.+) (GHC.Types.I# 1#))

_13 = ((GHC.Num.*) (GHC.Types.I# 2#))

hTest4 = (((GHC.Num.+) (_9(# (_14(# _10 #)), (_14(# _11 #)) #))) (_9(# (_15(# _10 #)), (_15(# _11 #)) #)))

_14(# ds'6 #) = (((GHC.Num.+) ds'6) (GHC.Types.I# 1#))

_15(# ds'7 #) = (((GHC.Num.*) ds'7) (GHC.Types.I# 2#))

hTest5 = (_9(# (_16(# _17, _10, _17 #)), (_16(# _11, _11, _11 #)) #))

_16(# x', x'2, x'3 #) = (((GHC.Num.*) x'2) (_9(# (_28(# _10, x' #)), (_28(# _11, x'3 #)) #)))

_17 = _10

i = (\f'3 -> (_18(# {-A-}\(x'6) -> (f'3 (\ds -> (_19(# ds #)))), {-A-}\(x'6) -> (f'3 (\ds' -> (_20(# ds' #)))) #)))

_18(# f'10, f'11 #) = (\x'6 -> (_24(# {-P-}(f'10(x'6)), {-P-}(f'11(x'6)) #)))

_19(# ds'4 #) = (((GHC.Num.+) ds'4) (GHC.Types.I# 1#))

_20(# ds'5 #) = (((GHC.Num.*) ds'5) (GHC.Types.I# 2#))

iTest0 = (_18(# {-A-}\(x'6) -> (_21(# (_19(# _22 #)), (_19(# _23 #)) #)), {-A-}\(x'6) -> (_21(# (_20(# _22 #)), (_20(# _23 #)) #)) #))

_21(# f'12, f'13 #) = (((GHC.Num.+) f'12) f'13)

_22 = (GHC.Types.I# 11#)

_23 = (GHC.Types.I# 22#)

iTest1 = (_24(# (_25(# (_19(# _26 #)), (_19(# _27 #)) #)), (_25(# (_20(# _26 #)), (_20(# _27 #)) #)) #))

_24(# f'8, f'9 #) = (((GHC.Num.+) f'8) f'9)

_25(# f'14, f'15 #) = (((GHC.Num.+) f'15) f'14)

_26 = (GHC.Types.I# 22#)

_27 = (GHC.Types.I# 11#)

iTest2 = (_24(# (_0(# (_19(# _1 #)), (_19(# _2 #)) #)), (_0(# (_20(# _1 #)), (_20(# _2 #)) #)) #))

p1 = (\ds'2 -> (_5(# ds'2 #)))

_28(# y, x'5 #) = (((GHC.Num.-) x'5) y)
