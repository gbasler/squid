-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (main,rec0,rec0_1,rec0_2) where

import GHC.Base
import GHC.Num
import GHC.TopHandler

_0(# _1 #) = (\x_a -> (_1(x_a)))

_2(# _3 #) = (((GHC.Num.+) _3) 1)

_4 = (\s_a -> (_2(# s_a #)))

_5 = (\k_a -> _4)

_6(# _7 #) = (((GHC.Num.*) _7) 2)

_8 = (\s_a' -> (_6(# s_a' #)))

_9 = (\k_a' -> _8)

_10(# _11 #) = _11

_12(# x_a, _13, _14, _15 #) = (((_10(# _14 #)) (_0(# \(x_a) -> (_12(# x_a, (_10(# _14 #)), _15, _13 #)) #))) x_a)

main = (GHC.TopHandler.runMainIO (GHC.Base.return ()))

rec0(# x_a #) = (\f_a -> let { _16 = f_a } in (f_a (_0(# \(x_a) -> ((_16 (_0(# \(x_a) -> (_12(# x_a, (_10(# f_a #)), f_a, _16 #)) #))) x_a) #))))

rec0_1 = _4

rec0_2 = _8
