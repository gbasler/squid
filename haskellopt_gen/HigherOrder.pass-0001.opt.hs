-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 308; Boxes: 64; Branches: 42
-- Apps: 91; Lams: 14; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrder (gTest1,lol,hTest3,ls1,f,iTest0,f0,iTest1,ds,i,g,iTest2,hTest5,h,gTest0,p1,hTest4) where

import GHC.Num
import GHC.Types

ds = 1

f = (\x -> (_0(# x #)))

_0(# x'3 #) = let
    _19 = ((GHC.Num.-) x'3)
  in (((GHC.Num.*) x'3) (((GHC.Num.*) (_19 (GHC.Types.I# 2#))) (_19 (GHC.Types.I# 3#))))

f0 = f0'

f0' = (\f'23 -> (_15(# (_16(# f'23 #)), (f'23 _17) #)))

g = (\f' -> (\x' -> (_1(# f', ((_2(# f' #)) x') #))))

_1(# f'9, f'10 #) = (((GHC.Num.+) f'10) ((_2(# f'9 #)) (GHC.Num.fromInteger 3)))

_2(# f'8 #) = f'8

gTest0 = (_1(# p1', (_3(# 2 #)) #))

p1' = (\ds'7 -> (_3(# ds'7 #)))

_3(# ds'4 #) = (((GHC.Num.+) ds'4) ds)

gTest1 = (((GHC.Num.+) (((GHC.Num.+) (_1(# p1', (_3(# 2 #)) #))) ds)) (((GHC.Num.+) (GHC.Num.fromInteger 3)) ds))

h = (\f'2 -> (_4(# f'2 #)))

_4(# f'22 #) = (((GHC.Num.*) (f'22 (GHC.Types.I# 2#))) (f'22 (GHC.Types.I# 3#)))

hTest3 = (((GHC.Num.+) (_4(# ((GHC.Num.+) (GHC.Types.I# 1#)) #))) (_4(# ((GHC.Num.*) (GHC.Types.I# 2#)) #)))

hTest4_sub = (GHC.Types.I# 2#)
hTest4_sub' = (GHC.Types.I# 3#)
hTest4 = (((GHC.Num.+) (((GHC.Num.*) (((GHC.Num.+) hTest4_sub) ds')) (((GHC.Num.+) hTest4_sub') ds'))) (((GHC.Num.*) (((GHC.Num.*) hTest4_sub) ds'2)) (((GHC.Num.*) hTest4_sub') ds'2)))

ds' = (GHC.Types.I# 1#)

ds'2 = (GHC.Types.I# 2#)

hTest5 = (((GHC.Num.*) (_0(# (GHC.Types.I# 2#) #))) (_0(# (GHC.Types.I# 3#) #)))

i = (\f'3 -> (_5(# {-A-}\(x'5) -> ((_6(# f'3 #)) _7), {-A-}\(x'5) -> (_8(# f'3 #)) #)))

_5(# f'16, f'17 #) = (\x'5 -> (_12(# {-P-}(f'16(x'5)), {-P-}(f'17(x'5)) #)))

_6(# f'13 #) = f'13

_7 = (\ds'6 -> (_10(# ds'6 #)))

_8(# f'6 #) = ((_6(# f'6 #)) (\ds'3 -> (((GHC.Num.*) ds'3) (GHC.Types.I# 2#))))

iTest0 = (_5(# {-A-}\(x'5) -> (_9(# (_10(# _11 #)), _7 #)), {-A-}\(x'5) -> (_8(# (\f'4 -> (_9(# (f'4 _11), f'4 #))) #)) #))

_9(# f'18, f'19 #) = (((GHC.Num.+) f'18) (f'19 (GHC.Types.I# 22#)))

_10(# ds'5 #) = (((GHC.Num.+) ds'5) (GHC.Types.I# 1#))

_11 = (GHC.Types.I# 11#)

iTest1 = (_12(# (_13(# (_10(# _14 #)), _7 #)), (_8(# (\f'5 -> (_13(# (f'5 _14), f'5 #))) #)) #))

_12(# f'14, f'15 #) = (((GHC.Num.+) f'14) f'15)

_13(# f'20, f'21 #) = (((GHC.Num.+) f'20) (f'21 (GHC.Types.I# 22#)))

_14 = (GHC.Types.I# 11#)

iTest2 = (_12(# (_15(# (_16(# _7 #)), (_10(# _17 #)) #)), (_8(# f0' #)) #))

_15(# f'11, f'12 #) = (((GHC.Num.+) f'12) f'11)

_16(# f'7 #) = (f'7 (GHC.Types.I# 22#))

_17 = (GHC.Types.I# 11#)

lol = (\x'2 -> (\y -> (_18(# x'2, y #))))

_18(# x'4, y' #) = (((GHC.Num.+) x'4) y')

ls1 = ((GHC.Num.+) (_18(# 11, 22 #)))

p1 = p1'
