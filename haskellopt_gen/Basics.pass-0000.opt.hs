-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Basics (gTest1,gTest2,f,fTest0,g,fTest1,gTest0,foo) where

import GHC.Num
import GHC.Types

_0(# _1, _2 #) = (((GHC.Num.*) _2) _1)

_3(# _4 #) = (((GHC.Num.*) _4) _4)

f = (\x_a -> (_3(# x_a #)))

fTest0 = (((GHC.Num.+) (_3(# (GHC.Types.I# 11#) #))) (_3(# (GHC.Types.I# 22#) #)))

fTest1 = (_3(# (_3(# (GHC.Types.I# 33#) #)) #))

foo = (\x_a' -> let { tmp_a = (((GHC.Num.*) x_a') (GHC.Types.I# 2#)) } in (((GHC.Num.+) tmp_a) tmp_a))

g = (\x_a'2 -> (\y_a -> (_0(# y_a, x_a'2 #))))

gTest0 = (_0(# (GHC.Types.I# 4#), (_0(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)) #))

gTest1 = (_0(# (_0(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)), (GHC.Types.I# 4#) #))

gTest2 = (_0(# (_0(# (GHC.Types.I# 5#), (GHC.Types.I# 4#) #)), (_0(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)) #))
