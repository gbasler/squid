-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Basics (fTest4,gTest1,gTest6,gTest2,f,fTest3,gTest5,fTest0,fTest2,g,gTest4,fTest1,gTest0,foo,gTest3) where

import GHC.Num
import GHC.Types

f = (\x -> (_0(# x #)))

_0(# x'4 #) = (((GHC.Num.*) x'4) x'4)

fTest0 = (((GHC.Num.*) (_0(# (GHC.Types.I# 11#) #))) (_0(# (GHC.Types.I# 22#) #)))

fTest1 = (_0(# (_0(# (GHC.Types.I# 33#) #)) #))

fTest2 = (((GHC.Num.+) (_0(# (GHC.Types.I# 44#) #))) (_0(# (f (GHC.Types.I# 55#)) #)))

fTest3 = (((GHC.Num.*) (_0(# (_0(# (GHC.Types.I# 66#) #)) #))) (_0(# (f (GHC.Types.I# 77#)) #)))

fTest4 = (((GHC.Num.+) (_0(# (_0(# (GHC.Types.I# 66#) #)) #))) (_0(# (f (GHC.Types.I# 77#)) #)))

foo = (\x'5 -> let { tmp = (((GHC.Num.*) x'5) (GHC.Types.I# 2#)) } in (((GHC.Num.+) tmp) tmp))

g = (\x' -> (_1(# {-A-}\(y') -> x' #)))

_1(# x'3 #) = (\y' -> (_2(# y', {-P-}(x'3(y')) #)))

gTest0 = (_2(# (GHC.Types.I# 4#), (_2(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)) #))

_2(# y, x'2 #) = (((GHC.Num.*) x'2) y)

gTest1 = (_2(# (_2(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)), (GHC.Types.I# 4#) #))

gTest2 = (_1(# {-A-}\(y') -> ((g (GHC.Types.I# 2#)) (GHC.Types.I# 3#)) #))

gTest3 = (_2(# (GHC.Types.I# 4#), (_2(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)) #))

gTest4 = (_2(# (_2(# (GHC.Types.I# 5#), (GHC.Types.I# 4#) #)), (_2(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)) #))

gTest5 = (((GHC.Num.+) (_2(# (_3(# _4 #)), (_5(# _4 #)) #))) (_2(# (_3(# (GHC.Types.I# 40#) #)), (_5(# (GHC.Types.I# 40#) #)) #)))

_3(# z' #) = (_2(# (GHC.Types.I# 22#), z' #))

_4 = (GHC.Types.I# 30#)

_5(# z #) = (_2(# z, (GHC.Types.I# 11#) #))

gTest6 = (_2(# (GHC.Types.I# 11#), (_2(# (GHC.Types.I# 33#), (GHC.Types.I# 44#) #)) #))
