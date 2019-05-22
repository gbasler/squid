-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Basics (fTest4,gTest1,gTest6,gTest2,f,fTest3,gTest5,fTest0,fTest2,g,gTest4,fTest1,gTest0,foo,gTest3) where

import GHC.Num
import GHC.Types

_0 = (GHC.Types.I# 30#)

_1(# z #) = (_2(# z, (GHC.Types.I# 11#) #))

_3(# z' #) = (_2(# (GHC.Types.I# 22#), z' #))

_2(# y, x #) = (((GHC.Num.*) x) y)

_4(# x' #) = (\y' -> (_2(# y', (x'(y')) #)))

_5(# x'2 #) = (((GHC.Num.*) x'2) x'2)

f = (\x'3 -> (_5(# x'3 #)))

fTest0 = (((GHC.Num.*) (_5(# (GHC.Types.I# 11#) #))) (_5(# (GHC.Types.I# 22#) #)))

fTest1 = (_5(# (_5(# (GHC.Types.I# 33#) #)) #))

fTest2 = (((GHC.Num.+) (_5(# (GHC.Types.I# 44#) #))) (_5(# (f (GHC.Types.I# 55#)) #)))

fTest3 = (((GHC.Num.*) (_5(# (_5(# (GHC.Types.I# 66#) #)) #))) (_5(# (f (GHC.Types.I# 77#)) #)))

fTest4 = (((GHC.Num.+) (_5(# (_5(# (GHC.Types.I# 66#) #)) #))) (_5(# (f (GHC.Types.I# 77#)) #)))

foo = (\x'4 -> let { tmp = (((GHC.Num.*) x'4) (GHC.Types.I# 2#)) } in (((GHC.Num.+) tmp) tmp))

g = (\x'5 -> (_4(# \(y') -> x'5 #)))

gTest0 = (_2(# (GHC.Types.I# 4#), (_2(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)) #))

gTest1 = (_2(# (_2(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)), (GHC.Types.I# 4#) #))

gTest2 = (_4(# \(y') -> ((g (GHC.Types.I# 2#)) (GHC.Types.I# 3#)) #))

gTest3 = (_2(# (GHC.Types.I# 4#), (_2(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)) #))

gTest4 = (_2(# (_2(# (GHC.Types.I# 5#), (GHC.Types.I# 4#) #)), (_2(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)) #))

gTest5 = (((GHC.Num.+) (_2(# (_3(# _0 #)), (_1(# _0 #)) #))) (_2(# (_3(# (GHC.Types.I# 40#) #)), (_1(# (GHC.Types.I# 40#) #)) #)))

gTest6 = (_2(# (GHC.Types.I# 11#), (_2(# (GHC.Types.I# 33#), (GHC.Types.I# 44#) #)) #))
