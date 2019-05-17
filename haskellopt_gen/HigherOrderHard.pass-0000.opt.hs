-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderHard (gTest1,g''Test1,g,g'',g'Test0,gTest0,g') where

import GHC.Base
import GHC.Num

_0(# x_a #) = (GHC.Base.id x_a)

_1(# _2 #) = (\x_a -> (GHC.Base.id _2))

_3(# x_a' #) = (GHC.Base.id x_a')

_4(# _5 #) = (\x_a' -> (GHC.Base.id _5))

g = (\f_a -> (_1(# (f_a (_0(# x_a #))) #)))

g' = (\f_a' -> (_4(# (f_a' (_3(# x_a' #))) #)))

g'' = (\f_a'2 -> (\x_a'2 -> (GHC.Base.id (f_a'2 (GHC.Base.id x_a'2)))))

g''Test1 = (\y_a -> (_1(# (((GHC.Num.+) (_0(# x_a #))) y_a) #)))

g'Test0 = (_4(# (((GHC.Num.+) (_3(# x_a' #))) 1) #))

gTest0 = (_1(# (((GHC.Num.+) (_0(# x_a #))) 1) #))

gTest1 = (\y_a' -> (_1(# (((GHC.Num.+) (_0(# x_a #))) y_a') #)))
