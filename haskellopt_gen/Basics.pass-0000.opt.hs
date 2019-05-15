-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Basics (ncG,ncFTest1,ncGTest0,ncFTest0,ncGTest1,ncGTest2,ncF) where

import GHC.Num
import GHC.Types

_2(# _0, _1 #) = (((GHC.Num.*) _0) _1)

_4(# _3 #) = (((GHC.Num.*) _3) _3)

ncF = (\x_a -> (_4(# x_a #)))

ncFTest0 = (((GHC.Num.+) (_4(# (GHC.Types.I# 11#) #))) (_4(# (GHC.Types.I# 22#) #)))

ncFTest1 = (_4(# (_4(# (GHC.Types.I# 33#) #)) #))

ncG(# _5 #) = (\x_a'0 -> (\y_a -> (_2(# _5, y_a #))))

ncGTest0 = (_2(# (_2(# (GHC.Types.I# 2#), (GHC.Types.I# 3#) #)), (GHC.Types.I# 4#) #))

ncGTest1 = (_2(# (GHC.Types.I# 4#), (_2(# (GHC.Types.I# 2#), (GHC.Types.I# 3#) #)) #))

ncGTest2 = (_2(# (_2(# (GHC.Types.I# 2#), (GHC.Types.I# 3#) #)), (_2(# (GHC.Types.I# 4#), (GHC.Types.I# 5#) #)) #))
