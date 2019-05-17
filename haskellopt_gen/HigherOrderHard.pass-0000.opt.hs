-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderHard (hoG',hoGTest0,hoG'',hoG''Test1,hoG'Test0,hoG,hoGTest1) where

import GHC.Base
import GHC.Num

_0 = (GHC.Base.id x_a)

_1(# _2 #) = (\x_a -> (GHC.Base.id _2))

_3 = (GHC.Base.id x_a'0)

_4(# _5 #) = (\x_a'0 -> (GHC.Base.id _5))

hoG = (\f_a -> (_1(# (f_a _0) #)))

hoG' = (\f_a'0 -> (_4(# (f_a'0 _3) #)))

hoG'' = (\f_a'1 -> (\x_a'1 -> (GHC.Base.id (f_a'1 (GHC.Base.id x_a'1)))))

hoG''Test1 = (\y_a -> (_1(# (((GHC.Num.+) _0) y_a) #)))

hoG'Test0 = (_4(# (((GHC.Num.+) _3) 1) #))

hoGTest0 = (_1(# (((GHC.Num.+) _0) 1) #))

hoGTest1 = (\y_a'0 -> (_1(# (((GHC.Num.+) _0) y_a'0) #)))
