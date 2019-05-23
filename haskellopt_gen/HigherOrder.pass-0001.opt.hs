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

_0(# x #) = (((GHC.Num.*) x) (((GHC.Num.*) (((GHC.Num.-) x) (GHC.Types.I# 2#))) (((GHC.Num.-) x) (GHC.Types.I# 3#))))

_1(# f' #) = f'

_2(# f'2 #) = f'2

_3 = (GHC.Num.fromInteger 3)

_4(# f'3, f'4 #) = (((GHC.Num.+) f'4) f'3)

_5(# ds' #) = (((GHC.Num.+) ds') 1)

_6 = (GHC.Types.I# 11#)

_7 = (GHC.Types.I# 22#)

_8(# f'5, f'6 #) = (((GHC.Num.+) f'5) f'6)

_9(# ds'2 #) = (((GHC.Num.+) ds'2) (GHC.Types.I# 1#))

_10 = (\ds'3 -> (_9(# ds'3 #)))

_11(# ds'4 #) = (((GHC.Num.*) ds'4) (GHC.Types.I# 2#))

_12 = (\ds'5 -> (_11(# ds'5 #)))

_13(# f'7, f'8 #) = (((GHC.Num.+) f'7) f'8)

_14(# f'9, f'10 #) = (\x' -> (_13(# {-P-}(f'9(x')), {-P-}(f'10(x')) #)))

_15(# f'11 #) = (((GHC.Num.+) (f'11 (GHC.Types.I# 11#))) (f'11 (GHC.Types.I# 22#)))

_16 = (GHC.Types.I# 11#)

_17 = (GHC.Types.I# 22#)

_18(# f'12, f'13 #) = (((GHC.Num.+) f'13) f'12)

_19(# f'14 #) = (((GHC.Num.*) (f'14 (GHC.Types.I# 2#))) (f'14 (GHC.Types.I# 3#)))

ds = 1

ds'6 = (GHC.Types.I# 2#)

ds'7 = (GHC.Types.I# 1#)

f = (\x'2 -> (_0(# x'2 #)))

f0 = (\f'15 -> (_8(# (f'15 _6), (f'15 _7) #)))

g = (\f'2 -> (\x'3 -> (_4(# ((_2(# f'2 #)) _3), ((_2(# f'2 #)) x'3) #))))

gTest0 = (_4(# (_5(# _3 #)), (_5(# 2 #)) #))

gTest1 = (((GHC.Num.+) (((GHC.Num.+) (_4(# (_5(# _3 #)), (_5(# 2 #)) #))) 1)) (((GHC.Num.+) (GHC.Num.fromInteger 3)) 1))

h = (\f'16 -> (_19(# f'16 #)))

hTest3 = (((GHC.Num.+) (_19(# ((GHC.Num.+) (GHC.Types.I# 1#)) #))) (_19(# ((GHC.Num.*) (GHC.Types.I# 2#)) #)))

hTest4 = (((GHC.Num.+) (((GHC.Num.*) (((GHC.Num.+) (GHC.Types.I# 2#)) ds'7)) (((GHC.Num.+) (GHC.Types.I# 3#)) ds'7))) (((GHC.Num.*) (((GHC.Num.*) (GHC.Types.I# 2#)) ds'6)) (((GHC.Num.*) (GHC.Types.I# 3#)) ds'6)))

hTest5 = (((GHC.Num.*) (_0(# (GHC.Types.I# 2#) #))) (_0(# (GHC.Types.I# 3#) #)))

i = (\f' -> (_14(# {-A-}\(x') -> ((_1(# f' #)) _10), {-A-}\(x') -> ((_1(# f' #)) _12) #)))

iTest0 = (_14(# {-A-}\(x') -> (_15(# _10 #)), {-A-}\(x') -> (_15(# _12 #)) #))

iTest1 = (_13(# (_18(# (_9(# _17 #)), (_9(# _16 #)) #)), (_18(# (_11(# _17 #)), (_11(# _16 #)) #)) #))

iTest2 = (_13(# (_8(# (_9(# _6 #)), (_9(# _7 #)) #)), (_8(# (_11(# _6 #)), (_11(# _7 #)) #)) #))

p1 = (\ds'8 -> (_5(# ds'8 #)))
