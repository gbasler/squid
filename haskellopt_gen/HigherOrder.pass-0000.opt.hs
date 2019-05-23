-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrder (gTest1,hTest3,iTest0,f0,iTest1,i,g,iTest2,hTest5,h,gTest0,p1,hTest4) where

import GHC.Num
import GHC.Types

_0(# x #) = (((GHC.Num.*) x) (_1(# (_2(# x, _3 #)), (_2(# x, _4 #)) #)))

_5 = 2

_6 = (_7(# _8 #))

_9(# x' #) = (_7(# x' #))

_10 = ((GHC.Num.+) (GHC.Types.I# 1#))

_11 = ((GHC.Num.*) (GHC.Types.I# 2#))

_12(# f #) = f

_13(# f' #) = f'

_8 = (GHC.Num.fromInteger 3)

_14(# f'2, f'3 #) = (((GHC.Num.+) f'2) f'3)

_7(# ds #) = (((GHC.Num.+) ds) 1)

_15 = (GHC.Types.I# 11#)

_16 = (GHC.Types.I# 22#)

_17(# f'4, f'5 #) = (((GHC.Num.+) f'4) f'5)

_18(# ds' #) = (((GHC.Num.+) ds') (GHC.Types.I# 1#))

_19 = (\ds'2 -> (_18(# ds'2 #)))

_20(# ds'3 #) = (((GHC.Num.*) ds'3) (GHC.Types.I# 2#))

_21 = (\ds'4 -> (_20(# ds'4 #)))

_22(# f'6, f'7 #) = (((GHC.Num.+) f'6) f'7)

_23(# f'8, f'9 #) = (\x'2 -> (_22(# {-P-}(f'8(x'2)), {-P-}(f'9(x'2)) #)))

_24(# f'10 #) = (((GHC.Num.+) (f'10 (GHC.Types.I# 11#))) (f'10 (GHC.Types.I# 22#)))

_25 = (GHC.Types.I# 11#)

_26 = (GHC.Types.I# 22#)

_27(# f'11, f'12 #) = (((GHC.Num.+) f'12) f'11)

_4 = (GHC.Types.I# 2#)

_3 = (GHC.Types.I# 3#)

_1(# f'13, f'14 #) = (((GHC.Num.*) f'14) f'13)

_28(# ds'5 #) = (((GHC.Num.+) ds'5) (GHC.Types.I# 1#))

_29(# ds'6 #) = (((GHC.Num.*) ds'6) (GHC.Types.I# 2#))

_2(# x'3, y #) = (((GHC.Num.-) x'3) y)

f0 = (\f'15 -> (_17(# (f'15 _15), (f'15 _16) #)))

g = (\f' -> (\x'4 -> (_14(# ((_13(# f' #)) x'4), ((_13(# f' #)) _8) #))))

gTest0 = (_14(# (_7(# 2 #)), (_7(# _8 #)) #))

gTest1 = (_14(# (_7(# (_14(# (_9(# _5 #)), _6 #)) #)), (_7(# _8 #)) #))

h = (\f'16 -> (_1(# (f'16 _3), (f'16 _4) #)))

hTest3 = (((GHC.Num.+) (_1(# (_10 _3), (_10 _4) #))) (_1(# (_11 _3), (_11 _4) #)))

hTest4 = (((GHC.Num.+) (_1(# (_28(# _3 #)), (_28(# _4 #)) #))) (_1(# (_29(# _3 #)), (_29(# _4 #)) #)))

hTest5 = (_1(# (_0(# _3 #)), (_0(# _4 #)) #))

i = (\f -> (_23(# {-A-}\(x'2) -> ((_12(# f #)) _19), {-A-}\(x'2) -> ((_12(# f #)) _21) #)))

iTest0 = (_23(# {-A-}\(x'2) -> (_24(# _19 #)), {-A-}\(x'2) -> (_24(# _21 #)) #))

iTest1 = (_22(# (_27(# (_18(# _26 #)), (_18(# _25 #)) #)), (_27(# (_20(# _26 #)), (_20(# _25 #)) #)) #))

iTest2 = (_22(# (_17(# (_18(# _15 #)), (_18(# _16 #)) #)), (_17(# (_20(# _15 #)), (_20(# _16 #)) #)) #))

p1 = (\ds'7 -> (_7(# ds'7 #)))
