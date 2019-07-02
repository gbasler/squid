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
{-# LANGUAGE NoMonomorphismRestriction  #-}

module HigherOrder (gTest1,lol,hTest3,ls1,f,iTest0,f0,iTest1,ds,i,g,iTest2,hTest5,h,gTest0,p1,hTest4) where

import GHC.Num
import GHC.Types

ds = 1

f = (\x -> (_0(# x #)))

_0(# x' #) = (((GHC.Num.*) x') (((GHC.Num.*) (((GHC.Num.-) x') (GHC.Types.I# 2#))) (((GHC.Num.-) x') (GHC.Types.I# 3#))))

f0 = (\f' -> (((GHC.Num.+) (f' _1)) (f' _2)))

_1 = (GHC.Types.I# 11#)

_2 = (GHC.Types.I# 22#)

g = (\f'2 -> (\x'2 -> (((GHC.Num.+) (f'2 x'2)) (f'2 _3))))

_3 = (GHC.Num.fromInteger 3)

gTest0 = (((GHC.Num.+) (_4(# 2 #))) (_4(# _3 #)))

_4(# ds' #) = (((GHC.Num.+) ds') 1)

gTest1 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (_4(# 2 #))) (_4(# _3 #)))) 1)) (((GHC.Num.+) (GHC.Num.fromInteger 3)) 1))

h = (\f'3 -> (_5(# f'3 #)))

_5(# f'4 #) = (((GHC.Num.*) (f'4 (GHC.Types.I# 2#))) (f'4 (GHC.Types.I# 3#)))

hTest3 = (((GHC.Num.+) (_5(# ((GHC.Num.+) (GHC.Types.I# 1#)) #))) (_5(# ((GHC.Num.*) (GHC.Types.I# 2#)) #)))

hTest4 = (((GHC.Num.+) (((GHC.Num.*) (((GHC.Num.+) (GHC.Types.I# 2#)) ds'2)) (((GHC.Num.+) (GHC.Types.I# 3#)) ds'2))) (((GHC.Num.*) (((GHC.Num.*) (GHC.Types.I# 2#)) ds'3)) (((GHC.Num.*) (GHC.Types.I# 3#)) ds'3)))

ds'2 = (GHC.Types.I# 1#)

ds'3 = (GHC.Types.I# 2#)

hTest5 = (((GHC.Num.*) (_0(# (GHC.Types.I# 2#) #))) (_0(# (GHC.Types.I# 3#) #)))

i = (\f'5 -> (_6(# (f'5 (\ds'4 -> (_7(# ds'4 #)))), (f'5 (\ds'5 -> (_8(# ds'5 #)))) #)))

_6(# f'6, f'7 #) = (\x'3 -> (((GHC.Num.+) f'6) f'7))

_7(# ds'6 #) = (((GHC.Num.+) ds'6) (GHC.Types.I# 1#))

_8(# ds'7 #) = (((GHC.Num.*) ds'7) (GHC.Types.I# 2#))

iTest0 = (_6(# (((GHC.Num.+) (_7(# _9 #))) (_7(# _10 #))), (((GHC.Num.+) (_8(# _9 #))) (_8(# _10 #))) #))

_9 = (GHC.Types.I# 11#)

_10 = (GHC.Types.I# 22#)

iTest1 = (((GHC.Num.+) (((GHC.Num.+) (_7(# _11 #))) (_7(# _12 #)))) (((GHC.Num.+) (_8(# _11 #))) (_8(# _12 #))))

_11 = (GHC.Types.I# 11#)

_12 = (GHC.Types.I# 22#)

iTest2 = (((GHC.Num.+) (((GHC.Num.+) (_7(# _1 #))) (_7(# _2 #)))) (((GHC.Num.+) (_8(# _1 #))) (_8(# _2 #))))

lol = (\x'4 -> (\y -> (((GHC.Num.+) x'4) y)))

ls1 = ((GHC.Num.+) (((GHC.Num.+) 11) 22))

p1 = (\ds'8 -> (_4(# ds'8 #)))
