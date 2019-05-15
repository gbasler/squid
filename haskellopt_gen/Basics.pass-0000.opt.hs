-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Basics (ncG,ncFTest1,ncGTest0,ncFTest0,ncGTest1,ncGTest2,ncF) where

import GHC.Num
import GHC.Types

_43(# _3e #) = (((GHC.Num.*) _3e) _3e)

_f(# _a, _9 #) = (((GHC.Num.*) _a) _9)

ncF = (\x_a_10 -> (_43(# x_a_10 #)))

ncFTest0 = (((GHC.Num.+) (_43(# (GHC.Types.I# 11#) #))) (_43(# (GHC.Types.I# 22#) #)))

ncFTest1 = (_43(# (_43(# (GHC.Types.I# 33#) #)) #))

ncG(# _64 #) = (\x_a_8 -> (\y_a_9 -> (_f(# _64, y_a_9 #))))

ncGTest0 = (_f(# (_f(# (GHC.Types.I# 2#), (GHC.Types.I# 3#) #)), (GHC.Types.I# 4#) #))

ncGTest1 = (_f(# (GHC.Types.I# 4#), (_f(# (GHC.Types.I# 2#), (GHC.Types.I# 3#) #)) #))

ncGTest2 = (_f(# (_f(# (GHC.Types.I# 2#), (GHC.Types.I# 3#) #)), (_f(# (GHC.Types.I# 4#), (GHC.Types.I# 5#) #)) #))
