-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 301; Boxes: 78; Branches: 72
-- Apps: 62; Lams: 17; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrder (gTest1,lol,hTest3,ls1,iTest0,f0,iTest1,i,g,iTest2,hTest5,h,gTest0,p1,hTest4) where

import GHC.Num
import GHC.Types

f0 = f0'

f0' = (\f'27 -> (_23(# (_24(# f'27 #)), (f'27 _25) #)))

g = (\f -> (\x -> (_0(# (_1(# f, x #)), (_2(# f #)) #))))

_0(# f'12, f'13 #) = (((GHC.Num.+) f'12) f'13)

_1(# f'8, x'5 #) = ((_28(# f'8 #)) x'5)

_2(# f'5 #) = ((_28(# f'5 #)) _4)

gTest0 = (_0(# (_3(# 2 #)), (_2(# p1' #)) #))

_3(# ds'3 #) = (((GHC.Num.+) ds'3) 1)

p1' = (\ds'8 -> (_3(# ds'8 #)))

gTest1 = (_0(# (_3(# (_0(# (_1(# p1', 2 #)), (_3(# _4 #)) #)) #)), (_2(# p1' #)) #))

_4 = (GHC.Num.fromInteger 3)

h = (\f' -> (_5(# (_6(# f' #)), (_7(# f' #)) #)))

_5(# f'25, f'26 #) = (((GHC.Num.*) f'26) f'25)

_6(# f'10 #) = (f'10 _11)

_7(# f'9 #) = (f'9 _9)

hTest3_sub'2 = ((GHC.Num.+) (GHC.Types.I# 1#))
hTest3_sub = (GHC.Types.I# 1#)
hTest3_sub'3 = ((GHC.Num.*) (GHC.Types.I# 2#))
hTest3_sub' = (GHC.Types.I# 2#)
hTest3 = (((GHC.Num.+) (_5(# (_6(# ((GHC.Num.+) hTest3_sub) #)), (_7(# ((GHC.Num.+) hTest3_sub) #)) #))) (_5(# (_6(# ((GHC.Num.*) hTest3_sub') #)), (_7(# ((GHC.Num.*) hTest3_sub') #)) #)))

hTest4 = (((GHC.Num.+) (_5(# (_6(# (\ds -> (_8(# ds #))) #)), (_8(# _9 #)) #))) (_5(# (_10(# _11 #)), (_7(# (\ds' -> (_10(# ds' #))) #)) #)))

_8(# ds'6 #) = (((GHC.Num.+) ds'6) (GHC.Types.I# 1#))

_9 = (GHC.Types.I# 2#)

_10(# ds'7 #) = (((GHC.Num.*) ds'7) (GHC.Types.I# 2#))

_11 = (GHC.Types.I# 3#)

hTest5 = (_5(# (_6(# (\x' -> (_12(# x' #))) #)), (_12(# _9 #)) #))

_12(# x'4 #) = (((GHC.Num.*) x'4) (_5(# (_6(# (\y'2 -> (_27(# x'4, y'2 #))) #)), (_27(# x'4, _9 #)) #)))

i = (\f'2 -> (_13(# {-A-}\(x'7) -> ((_14(# f'2 #)) _15), {-A-}\(x'7) -> (_16(# f'2 #)) #)))

_13(# f'19, f'20 #) = (\x'7 -> (_20(# {-P-}(f'19(x'7)), {-P-}(f'20(x'7)) #)))

_14(# f'16 #) = f'16

_15 = (\ds'5 -> (_18(# ds'5 #)))

_16(# f'6 #) = ((_14(# f'6 #)) (\ds'2 -> (((GHC.Num.*) ds'2) (GHC.Types.I# 2#))))

iTest0 = (_13(# {-A-}\(x'7) -> (_17(# (_18(# _19 #)), _15 #)), {-A-}\(x'7) -> (_16(# (\f'3 -> (_17(# (f'3 _19), f'3 #))) #)) #))

_17(# f'21, f'22 #) = (((GHC.Num.+) f'21) (f'22 (GHC.Types.I# 22#)))

_18(# ds'4 #) = (((GHC.Num.+) ds'4) (GHC.Types.I# 1#))

_19 = (GHC.Types.I# 11#)

iTest1 = (_20(# (_21(# (_18(# _22 #)), _15 #)), (_16(# (\f'4 -> (_21(# (f'4 _22), f'4 #))) #)) #))

_20(# f'17, f'18 #) = (((GHC.Num.+) f'17) f'18)

_21(# f'23, f'24 #) = (((GHC.Num.+) f'23) (f'24 (GHC.Types.I# 22#)))

_22 = (GHC.Types.I# 11#)

iTest2 = (_20(# (_23(# (_24(# _15 #)), (_18(# _25 #)) #)), (_16(# f0' #)) #))

_23(# f'14, f'15 #) = (((GHC.Num.+) f'15) f'14)

_24(# f'7 #) = (f'7 (GHC.Types.I# 22#))

_25 = (GHC.Types.I# 11#)

lol = (\x'2 -> (\y -> (_26(# y, x'2 #))))

_26(# y'3, x'6 #) = (((GHC.Num.+) x'6) y'3)

ls1 = ((GHC.Num.+) (_26(# 22, 11 #)))

p1 = p1'

_27(# x'3, y' #) = (((GHC.Num.-) x'3) y')

_28(# f'11 #) = f'11
