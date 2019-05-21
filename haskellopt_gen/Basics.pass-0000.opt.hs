-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Basics (fTest4,gTest1,gTest6,gTest2,f,fTest3,gTest5,fTest0,fTest2,g,gTest4,fTest1,gTest0,foo,gTest3) where

import GHC.Num
import GHC.Types

_0 = (GHC.Types.I# 30#)

_1(# _2 #) = (_3(# _2, (GHC.Types.I# 11#) #))

_4(# _5 #) = (_3(# (GHC.Types.I# 22#), _5 #))

_3(# _6, _7 #) = (((GHC.Num.*) _7) _6)

_8(# _9 #) = (\y_a -> (_3(# y_a, (_9(y_a)) #)))

_10(# _11 #) = (((GHC.Num.*) _11) _11)

f = (\x_a -> (_10(# x_a #)))

fTest0 = (((GHC.Num.*) (_10(# (GHC.Types.I# 11#) #))) (_10(# (GHC.Types.I# 22#) #)))

fTest1 = (_10(# (_10(# (GHC.Types.I# 33#) #)) #))

fTest2 = (((GHC.Num.+) (_10(# (GHC.Types.I# 44#) #))) (_10(# (f (GHC.Types.I# 55#)) #)))

fTest3 = (((GHC.Num.*) (_10(# (_10(# (GHC.Types.I# 66#) #)) #))) (_10(# (f (GHC.Types.I# 77#)) #)))

fTest4 = (((GHC.Num.+) (_10(# (_10(# (GHC.Types.I# 66#) #)) #))) (_10(# (f (GHC.Types.I# 77#)) #)))

foo = (\x_a' -> let { tmp_a = (((GHC.Num.*) x_a') (GHC.Types.I# 2#)) } in (((GHC.Num.+) tmp_a) tmp_a))

g = (\x_a'2 -> (_8(# \(y_a) -> x_a'2 #)))

gTest0 = (_3(# (GHC.Types.I# 4#), (_3(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)) #))

gTest1 = (_3(# (_3(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)), (GHC.Types.I# 4#) #))

gTest2 = (_8(# \(y_a) -> ((g (GHC.Types.I# 2#)) (GHC.Types.I# 3#)) #))

gTest3 = (_3(# (GHC.Types.I# 4#), (_3(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)) #))

gTest4 = (_3(# (_3(# (GHC.Types.I# 5#), (GHC.Types.I# 4#) #)), (_3(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)) #))

gTest5 = (((GHC.Num.+) (_3(# (_4(# _0 #)), (_1(# _0 #)) #))) (_3(# (_4(# (GHC.Types.I# 40#) #)), (_1(# (GHC.Types.I# 40#) #)) #)))

gTest6 = (_3(# (GHC.Types.I# 11#), (_3(# (GHC.Types.I# 33#), (GHC.Types.I# 44#) #)) #))
