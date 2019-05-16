-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Basics (ncG,ncFTest1,ncGTest0,ncFTest0,ncGTest1,ncGTest2,ncF,foo) where

import GHC.Num
import GHC.Types

_0(# _1, _2 #) = (((GHC.Num.*) _2) _1)

_3(# _4 #) = (((GHC.Num.*) _4) _4)

foo = (\x_a -> let { tmp_a = (((GHC.Num.*) x_a) (GHC.Types.I# 2#)) } in (((GHC.Num.+) tmp_a) tmp_a))

ncF = (\x_a'0 -> (_3(# x_a'0 #)))

ncFTest0 = (((GHC.Num.+) (_3(# (GHC.Types.I# 11#) #))) (_3(# (GHC.Types.I# 22#) #)))

ncFTest1 = (_3(# (_3(# (GHC.Types.I# 33#) #)) #))

ncG(# _5 #) = (\x_a'1 -> (\y_a -> (_0(# y_a, _5 #))))

ncGTest0 = (_0(# (GHC.Types.I# 4#), (_0(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)) #))

ncGTest1 = (_0(# (_0(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)), (GHC.Types.I# 4#) #))

ncGTest2 = (_0(# (_0(# (GHC.Types.I# 5#), (GHC.Types.I# 4#) #)), (_0(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)) #))
