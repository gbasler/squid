-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderHard (gTest1,g''Test1,hTest0,g,g'',hTest1,g'Test0,h,gTest0,g') where

import GHC.Base
import GHC.Num

_0(# x_a #) = (GHC.Base.id x_a)

_1(# _2 #) = (\x_a -> (GHC.Base.id (_2(x_a))))

_3(# x_a' #) = (GHC.Base.id x_a')

_4(# _5 #) = (\x_a' -> (GHC.Base.id (_5(x_a'))))

_6(# _7 #) = (GHC.Base.id _7)

_8(# _9 #) = (\x_a'2 -> (GHC.Base.id (_9(x_a'2))))

_10(# _11 #) = (((GHC.Num.+) _11) 1)

_12(# y_a, _13 #) = (((GHC.Num.+) _13) y_a)

_14(# f_a #) = f_a

g = (\f_a' -> (_1(# \(x_a) -> (f_a' (_0(# x_a #))) #)))

g' = (\f_a'2 -> (_4(# \(x_a') -> (f_a'2 (_3(# x_a' #))) #)))

g'' = (\f_a'3 -> (\x_a'3 -> (GHC.Base.id (f_a'3 (GHC.Base.id x_a'3)))))

g''Test1 = (\y_a' -> (_1(# \(x_a) -> (((GHC.Num.+) (_0(# x_a #))) y_a') #)))

g'Test0 = (_4(# \(x_a') -> (((GHC.Num.+) (_3(# x_a' #))) 1) #))

gTest0 = (_1(# \(x_a) -> (((GHC.Num.+) (_0(# x_a #))) 1) #))

gTest1 = (\y_a'2 -> (_1(# \(x_a) -> (((GHC.Num.+) (_0(# x_a #))) y_a'2) #)))

h(# x_a'2 #) = (\f_a -> (_8(# \(x_a'2) -> ((_14(# f_a #)) (_6(# ((_14(# f_a #)) x_a'2) #))) #)))

hTest0(# x_a'2 #) = (_8(# \(x_a'2) -> (_10(# (_6(# (_10(# x_a'2 #)) #)) #)) #))

hTest1(# x_a'2 #) = (\y_a -> (_8(# \(x_a'2) -> (_12(# (_6(# (_12(# x_a'2, y_a #)) #)), y_a #)) #)))
