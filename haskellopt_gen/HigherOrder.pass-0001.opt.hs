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

_0(# x'5 #) = let
    _21 = ((GHC.Num.-) x'5)
  in (((GHC.Num.*) x'5) (((GHC.Num.*) (_21 (GHC.Types.I# 2#))) (_21 (GHC.Types.I# 3#))))

f0 = (\f' -> (_1(# (f' _2), (f' _3) #)))

_1(# f'8, f'9 #) = (((GHC.Num.+) f'9) f'8)

_2 = (GHC.Types.I# 22#)

_3 = (GHC.Types.I# 11#)

g = (\f'2 -> (\x' -> let { x'2 = (_5(# f'2 #)) } in (_4(# (x'2 x'), (x'2 _6) #))))

_4(# f'6, f'7 #) = (((GHC.Num.+) f'6) f'7)

_6 = (GHC.Num.fromInteger 3)

_5(# f'5 #) = f'5

gTest0 = (_4(# (_7(# 2 #)), (_7(# _6 #)) #))

_7(# ds'6 #) = (((GHC.Num.+) ds'6) ds)

gTest1 = (((GHC.Num.+) (((GHC.Num.+) (_4(# (_7(# 2 #)), (_7(# _6 #)) #))) ds)) (((GHC.Num.+) (GHC.Num.fromInteger 3)) ds))

h = (\f'3 -> (_8(# f'3 #)))

_8(# f'19 #) = (((GHC.Num.*) (f'19 (GHC.Types.I# 2#))) (f'19 (GHC.Types.I# 3#)))

hTest3 = (((GHC.Num.+) (_8(# ((GHC.Num.+) (GHC.Types.I# 1#)) #))) (_8(# ((GHC.Num.*) (GHC.Types.I# 2#)) #)))

hTest4_sub = (GHC.Types.I# 2#)
hTest4_sub' = (GHC.Types.I# 3#)
hTest4 = (((GHC.Num.+) (((GHC.Num.*) (((GHC.Num.+) hTest4_sub) ds')) (((GHC.Num.+) hTest4_sub') ds'))) (((GHC.Num.*) (((GHC.Num.*) hTest4_sub) ds'2)) (((GHC.Num.*) hTest4_sub') ds'2)))

ds' = (GHC.Types.I# 1#)

ds'2 = (GHC.Types.I# 2#)

hTest5 = (((GHC.Num.*) (_0(# (GHC.Types.I# 2#) #))) (_0(# (GHC.Types.I# 3#) #)))

i = (\f'4 -> let { x'3 = (_10(# f'4 #)) } in (_9(# {-A-}\(x'7) -> (x'3 (\ds'3 -> (_11(# ds'3 #)))), {-A-}\(x'7) -> (x'3 (\ds'4 -> (_12(# ds'4 #)))) #)))

_9(# f'13, f'14 #) = (\x'7 -> (_16(# {-P-}(f'13(x'7)), {-P-}(f'14(x'7)) #)))

_11(# ds'7 #) = (((GHC.Num.+) ds'7) (GHC.Types.I# 1#))

_12(# ds'8 #) = (((GHC.Num.*) ds'8) (GHC.Types.I# 2#))

_10(# f'10 #) = f'10

iTest0 = (_9(# {-A-}\(x'7) -> (_13(# (_11(# _14 #)), (_11(# _15 #)) #)), {-A-}\(x'7) -> (_13(# (_12(# _14 #)), (_12(# _15 #)) #)) #))

_13(# f'15, f'16 #) = (((GHC.Num.+) f'15) f'16)

_14 = (GHC.Types.I# 11#)

_15 = (GHC.Types.I# 22#)

iTest1 = (_16(# (_17(# (_11(# _18 #)), (_11(# _19 #)) #)), (_17(# (_12(# _18 #)), (_12(# _19 #)) #)) #))

_16(# f'11, f'12 #) = (((GHC.Num.+) f'11) f'12)

_17(# f'17, f'18 #) = (((GHC.Num.+) f'17) f'18)

_18 = (GHC.Types.I# 11#)

_19 = (GHC.Types.I# 22#)

iTest2 = (_16(# (_1(# (_11(# _2 #)), (_11(# _3 #)) #)), (_1(# (_12(# _2 #)), (_12(# _3 #)) #)) #))

lol = (\x'4 -> (\y -> (_20(# x'4, y #))))

_20(# x'6, y' #) = (((GHC.Num.+) x'6) y')

ls1 = ((GHC.Num.+) (_20(# 11, 22 #)))

p1 = (\ds'5 -> (_7(# ds'5 #)))
