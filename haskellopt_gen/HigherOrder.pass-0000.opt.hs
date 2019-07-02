-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 431; Boxes: 124; Branches: 156
-- Apps: 62; Lams: 17; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module HigherOrder (gTest1,lol,hTest3,ls1,iTest0,f0,iTest1,i,g,iTest2,hTest5,h,gTest0,p1,hTest4) where

import GHC.Num
import GHC.Types

f0 = (\f -> (((GHC.Num.+) (f _0)) (f _1)))

_0 = (GHC.Types.I# 11#)

_1 = (GHC.Types.I# 22#)

g = (\f' -> (\x -> (((GHC.Num.+) (f' x)) (f' _2))))

_2 = (GHC.Num.fromInteger 3)

gTest0 = (((GHC.Num.+) (_3(# 2 #))) (_3(# _2 #)))

_3(# ds #) = (((GHC.Num.+) ds) 1)

gTest1 = (((GHC.Num.+) (_3(# (((GHC.Num.+) (_3(# 2 #))) (_3(# _2 #))) #))) (_3(# _2 #)))

h = (\f'2 -> (((GHC.Num.*) (f'2 _4)) (f'2 _5)))

hTest3 = (((GHC.Num.+) (((GHC.Num.*) (_6 _4)) (_6 _5))) (((GHC.Num.*) (_7 _4)) (_7 _5)))

_6 = ((GHC.Num.+) (GHC.Types.I# 1#))

_7 = ((GHC.Num.*) (GHC.Types.I# 2#))

hTest4 = (((GHC.Num.+) (((GHC.Num.*) (_8(# _4 #))) (_8(# _5 #)))) (((GHC.Num.*) (_9(# _4 #))) (_9(# _5 #))))

_8(# ds' #) = (((GHC.Num.+) ds') (GHC.Types.I# 1#))

_4 = (GHC.Types.I# 2#)

_5 = (GHC.Types.I# 3#)

_9(# ds'2 #) = (((GHC.Num.*) ds'2) (GHC.Types.I# 2#))

hTest5 = (((GHC.Num.*) (_10(# _4 #))) (_10(# _5 #)))

_10(# x' #) = (((GHC.Num.*) x') (((GHC.Num.*) (((GHC.Num.-) x') _4)) (((GHC.Num.-) x') _5)))

i = (\f'3 -> (_11(# (f'3 (\ds'3 -> (_12(# ds'3 #)))), (f'3 (\ds'4 -> (_13(# ds'4 #)))) #)))

_11(# f'4, f'5 #) = (\x'2 -> (((GHC.Num.+) f'4) f'5))

_12(# ds'5 #) = (((GHC.Num.+) ds'5) (GHC.Types.I# 1#))

_13(# ds'6 #) = (((GHC.Num.*) ds'6) (GHC.Types.I# 2#))

iTest0 = (_11(# (((GHC.Num.+) (_12(# _14 #))) (_12(# _15 #))), (((GHC.Num.+) (_13(# _14 #))) (_13(# _15 #))) #))

_14 = (GHC.Types.I# 11#)

_15 = (GHC.Types.I# 22#)

iTest1 = (((GHC.Num.+) (((GHC.Num.+) (_12(# _16 #))) (_12(# _17 #)))) (((GHC.Num.+) (_13(# _16 #))) (_13(# _17 #))))

_16 = (GHC.Types.I# 11#)

_17 = (GHC.Types.I# 22#)

iTest2 = (((GHC.Num.+) (((GHC.Num.+) (_12(# _0 #))) (_12(# _1 #)))) (((GHC.Num.+) (_13(# _0 #))) (_13(# _1 #))))

lol = (\x'3 -> (\y -> (((GHC.Num.+) x'3) y)))

ls1 = ((GHC.Num.+) (((GHC.Num.+) 11) 22))

p1 = (\ds'7 -> (_3(# ds'7 #)))
