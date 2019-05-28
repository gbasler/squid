-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 325; Boxes: 82; Branches: 54
-- Apps: 88; Lams: 10; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrder (gTest1,hTest3,f,iTest0,f0,iTest1,ds,i,g,iTest2,hTest5,h,gTest0,p1,hTest4) where

import GHC.Num
import GHC.Types

ds = 1

f = (\x -> (_0(# x #)))

_0(# x'2 #) = (((GHC.Num.*) x'2) (((GHC.Num.*) (((GHC.Num.-) x'2) (GHC.Types.I# 2#))) (((GHC.Num.-) x'2) (GHC.Types.I# 3#))))

f0 = (\f' -> (_1(# (f' _2), (f' _3) #)))

_1(# f'7, f'8 #) = (((GHC.Num.+) f'7) f'8)

_2 = (GHC.Types.I# 11#)

_3 = (GHC.Types.I# 22#)

g = (\f'2 -> (\x' -> (_4(# (f'2 x'), (f'2 _5) #))))

_4(# f'5, f'6 #) = (((GHC.Num.+) f'5) f'6)

_5 = (GHC.Num.fromInteger 3)

gTest0 = (_4(# (_6(# 2 #)), (_6(# _5 #)) #))

_6(# ds'6 #) = (((GHC.Num.+) ds'6) ds)

gTest1 = (((GHC.Num.+) (((GHC.Num.+) (_4(# (_6(# 2 #)), (_6(# _5 #)) #))) ds)) (((GHC.Num.+) (GHC.Num.fromInteger 3)) ds))

h = (\f'3 -> (_7(# f'3 #)))

_7(# f'17 #) = (((GHC.Num.*) (f'17 (GHC.Types.I# 2#))) (f'17 (GHC.Types.I# 3#)))

hTest3 = (((GHC.Num.+) (_7(# ((GHC.Num.+) (GHC.Types.I# 1#)) #))) (_7(# ((GHC.Num.*) (GHC.Types.I# 2#)) #)))

hTest4 = (((GHC.Num.+) (((GHC.Num.*) (((GHC.Num.+) (GHC.Types.I# 2#)) ds')) (((GHC.Num.+) (GHC.Types.I# 3#)) ds'))) (((GHC.Num.*) (((GHC.Num.*) (GHC.Types.I# 2#)) ds'2)) (((GHC.Num.*) (GHC.Types.I# 3#)) ds'2)))

ds' = (GHC.Types.I# 1#)

ds'2 = (GHC.Types.I# 2#)

hTest5 = (((GHC.Num.*) (_0(# (GHC.Types.I# 2#) #))) (_0(# (GHC.Types.I# 3#) #)))

i = (\f'4 -> (_8(# {-A-}\(x'3) -> (f'4 (\ds'3 -> (_9(# ds'3 #)))), {-A-}\(x'3) -> (f'4 (\ds'4 -> (_10(# ds'4 #)))) #)))

_8(# f'11, f'12 #) = (\x'3 -> (_14(# {-P-}(f'11(x'3)), {-P-}(f'12(x'3)) #)))

_9(# ds'7 #) = (((GHC.Num.+) ds'7) (GHC.Types.I# 1#))

_10(# ds'8 #) = (((GHC.Num.*) ds'8) (GHC.Types.I# 2#))

iTest0 = (_8(# {-A-}\(x'3) -> (_11(# (_9(# _12 #)), (_9(# _13 #)) #)), {-A-}\(x'3) -> (_11(# (_10(# _12 #)), (_10(# _13 #)) #)) #))

_11(# f'13, f'14 #) = (((GHC.Num.+) f'13) f'14)

_12 = (GHC.Types.I# 11#)

_13 = (GHC.Types.I# 22#)

iTest1 = (_14(# (_15(# (_9(# _16 #)), (_9(# _17 #)) #)), (_15(# (_10(# _16 #)), (_10(# _17 #)) #)) #))

_14(# f'9, f'10 #) = (((GHC.Num.+) f'9) f'10)

_15(# f'15, f'16 #) = (((GHC.Num.+) f'16) f'15)

_16 = (GHC.Types.I# 22#)

_17 = (GHC.Types.I# 11#)

iTest2 = (_14(# (_1(# (_9(# _2 #)), (_9(# _3 #)) #)), (_1(# (_10(# _2 #)), (_10(# _3 #)) #)) #))

p1 = (\ds'5 -> (_6(# ds'5 #)))
