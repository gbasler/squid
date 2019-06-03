-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 331; Boxes: 106; Branches: 76
-- Apps: 66; Lams: 11; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrder (gTest1,lol,hTest3,ls1,iTest0,f0,iTest1,i,g,iTest2,hTest5,h,gTest0,p1,hTest4) where

import GHC.Num
import GHC.Types

f0 = (\f -> (_0(# (f _1), (f _2) #)))

_0(# f'7, f'8 #) = (((GHC.Num.+) f'7) f'8)

_1 = (GHC.Types.I# 11#)

_2 = (GHC.Types.I# 22#)

g = (\f' -> (\x -> (_3(# (f' x), (_4(# f' #)) #))))

_3(# f'5, f'6 #) = (((GHC.Num.+) f'5) f'6)

_4(# f'4 #) = (f'4 _6)

gTest0 = (_3(# (_5(# 2 #)), (_4(# p1' #)) #))

_5(# ds'3 #) = (((GHC.Num.+) ds'3) 1)

p1' = (\ds'7 -> (_5(# ds'7 #)))

gTest1 = (_3(# (_5(# (_3(# (_5(# 2 #)), (_5(# _6 #)) #)) #)), (_5(# _6 #)) #))

_6 = (GHC.Num.fromInteger 3)

h = (\f'2 -> (_7(# (f'2 _8), (f'2 _9) #)))

_7(# f'17, f'18 #) = (((GHC.Num.*) f'18) f'17)

_8 = (GHC.Types.I# 3#)

_9 = (GHC.Types.I# 2#)

hTest3 = (((GHC.Num.+) (_7(# (_10 _8), (_10 _9) #))) (_7(# (_11 _8), (_11 _9) #)))

_10 = ((GHC.Num.+) (GHC.Types.I# 1#))

_11 = ((GHC.Num.*) (GHC.Types.I# 2#))

hTest4 = (((GHC.Num.+) (_7(# (_12(# _8 #)), (_12(# _9 #)) #))) (_7(# (_13(# _8 #)), (_13(# _9 #)) #)))

_12(# ds'6 #) = (((GHC.Num.+) ds'6) (GHC.Types.I# 1#))

_13(# ds'2 #) = (((GHC.Num.*) ds'2) (GHC.Types.I# 2#))

hTest5 = (_7(# (_14(# _8 #)), (_14(# _9 #)) #))

_14(# x'3 #) = (((GHC.Num.*) x'3) (_7(# (_26(# x'3, _8 #)), (_26(# x'3, _9 #)) #)))

i = (\f'3 -> (_15(# {-A-}\(x'5) -> (f'3 (\ds -> (_16(# ds #)))), {-A-}\(x'5) -> (f'3 (\ds' -> (_17(# ds' #)))) #)))

_15(# f'11, f'12 #) = (\x'5 -> (_21(# {-P-}(f'11(x'5)), {-P-}(f'12(x'5)) #)))

_16(# ds'4 #) = (((GHC.Num.+) ds'4) (GHC.Types.I# 1#))

_17(# ds'5 #) = (((GHC.Num.*) ds'5) (GHC.Types.I# 2#))

iTest0 = (_15(# {-A-}\(x'5) -> (_18(# (_16(# _19 #)), (_16(# _20 #)) #)), {-A-}\(x'5) -> (_18(# (_17(# _19 #)), (_17(# _20 #)) #)) #))

_18(# f'13, f'14 #) = (((GHC.Num.+) f'13) f'14)

_19 = (GHC.Types.I# 11#)

_20 = (GHC.Types.I# 22#)

iTest1 = (_21(# (_22(# (_16(# _23 #)), (_16(# _24 #)) #)), (_22(# (_17(# _23 #)), (_17(# _24 #)) #)) #))

_21(# f'9, f'10 #) = (((GHC.Num.+) f'9) f'10)

_22(# f'15, f'16 #) = (((GHC.Num.+) f'16) f'15)

_23 = (GHC.Types.I# 22#)

_24 = (GHC.Types.I# 11#)

iTest2 = (_21(# (_0(# (_16(# _1 #)), (_16(# _2 #)) #)), (_0(# (_17(# _1 #)), (_17(# _2 #)) #)) #))

lol = (\x' -> (\y -> (_25(# y, x' #))))

_25(# y'2, x'4 #) = (((GHC.Num.+) x'4) y'2)

ls1 = ((GHC.Num.+) (_25(# 22, 11 #)))

p1 = p1'

_26(# x'2, y' #) = (((GHC.Num.-) x'2) y')
