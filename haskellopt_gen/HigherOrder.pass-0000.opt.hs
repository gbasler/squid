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

f0 = (\f -> (_0(# (f _1), (f _2) #)))

_0(# f'9, f'10 #) = (((GHC.Num.+) f'10) f'9)

_1 = (GHC.Types.I# 22#)

_2 = (GHC.Types.I# 11#)

g = (\f' -> (\x -> let { x' = (_4(# f' #)) } in (_3(# (x' x), (x' _5) #))))

_3(# f'5, f'6 #) = (((GHC.Num.+) f'5) f'6)

_5 = (GHC.Num.fromInteger 3)

_4(# f'4 #) = f'4

gTest0 = (_3(# (_6(# 2 #)), (_6(# _5 #)) #))

_6(# ds'3 #) = (((GHC.Num.+) ds'3) 1)

gTest1 = (_3(# (_6(# (_3(# (_6(# 2 #)), (_6(# _5 #)) #)) #)), (_6(# _5 #)) #))

h = (\f'2 -> (_7(# (_8(# f'2 #)), (_9(# f'2 #)) #)))

_7(# f'20, f'21 #) = (((GHC.Num.*) f'21) f'20)

_8(# f'8 #) = (f'8 _11)

_9(# f'7 #) = (f'7 _12)

hTest3_sub'2 = ((GHC.Num.+) (GHC.Types.I# 1#))
hTest3_sub = (GHC.Types.I# 1#)
hTest3_sub'3 = ((GHC.Num.*) (GHC.Types.I# 2#))
hTest3_sub' = (GHC.Types.I# 2#)
hTest3 = (((GHC.Num.+) (_7(# (_8(# ((GHC.Num.+) hTest3_sub) #)), (_9(# ((GHC.Num.+) hTest3_sub) #)) #))) (_7(# (_8(# ((GHC.Num.*) hTest3_sub') #)), (_9(# ((GHC.Num.*) hTest3_sub') #)) #)))

hTest4 = (((GHC.Num.+) (_7(# (_10(# _11 #)), (_10(# _12 #)) #))) (_7(# (_13(# _11 #)), (_13(# _12 #)) #)))

_10(# ds'6 #) = (((GHC.Num.+) ds'6) (GHC.Types.I# 1#))

_11 = (GHC.Types.I# 3#)

_12 = (GHC.Types.I# 2#)

_13(# ds'7 #) = (((GHC.Num.*) ds'7) (GHC.Types.I# 2#))

hTest5 = (_7(# (_14(# _11 #)), (_14(# _12 #)) #))

_14(# x'5 #) = (((GHC.Num.*) x'5) (_7(# (_27(# x'5, _11 #)), (_27(# x'5, _12 #)) #)))

i = (\f'3 -> let { x'2 = (_16(# f'3 #)) } in (_15(# {-A-}\(x'7) -> (x'2 (\ds -> (_17(# ds #)))), {-A-}\(x'7) -> (x'2 (\ds' -> (_18(# ds' #)))) #)))

_15(# f'14, f'15 #) = (\x'7 -> (_22(# {-P-}(f'14(x'7)), {-P-}(f'15(x'7)) #)))

_17(# ds'4 #) = (((GHC.Num.+) ds'4) (GHC.Types.I# 1#))

_18(# ds'5 #) = (((GHC.Num.*) ds'5) (GHC.Types.I# 2#))

_16(# f'11 #) = f'11

iTest0 = (_15(# {-A-}\(x'7) -> (_19(# (_17(# _20 #)), (_17(# _21 #)) #)), {-A-}\(x'7) -> (_19(# (_18(# _20 #)), (_18(# _21 #)) #)) #))

_19(# f'16, f'17 #) = (((GHC.Num.+) f'16) f'17)

_20 = (GHC.Types.I# 11#)

_21 = (GHC.Types.I# 22#)

iTest1 = (_22(# (_23(# (_17(# _24 #)), (_17(# _25 #)) #)), (_23(# (_18(# _24 #)), (_18(# _25 #)) #)) #))

_22(# f'12, f'13 #) = (((GHC.Num.+) f'12) f'13)

_23(# f'18, f'19 #) = (((GHC.Num.+) f'18) f'19)

_24 = (GHC.Types.I# 11#)

_25 = (GHC.Types.I# 22#)

iTest2 = (_22(# (_0(# (_17(# _1 #)), (_17(# _2 #)) #)), (_0(# (_18(# _1 #)), (_18(# _2 #)) #)) #))

lol = (\x'3 -> (\y -> (_26(# y, x'3 #))))

_26(# y'2, x'6 #) = (((GHC.Num.+) x'6) y'2)

ls1 = ((GHC.Num.+) (_26(# 22, 11 #)))

p1 = (\ds'2 -> (_6(# ds'2 #)))

_27(# x'4, y' #) = (((GHC.Num.-) x'4) y')
