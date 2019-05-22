-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (main,rec0,rec0_1,rec0_2) where

import GHC.Base
import GHC.Num
import GHC.TopHandler

_0(# f #) = (\x -> (f(x)))

_1(# s #) = (((GHC.Num.+) s) 1)

_2 = (\s'2 -> (_1(# s'2 #)))

_3 = (\k -> _2)

_4(# s' #) = (((GHC.Num.*) s') 2)

_5 = (\s'3 -> (_4(# s'3 #)))

_6 = (\k' -> _5)

_7(# f' #) = f'

_8(# x, f'2, f'3, f'4 #) = (((_7(# f'3 #)) (_0(# \(x) -> (_8(# x, (_7(# f'3 #)), f'4, f'2 #)) #))) x)

main = (GHC.TopHandler.runMainIO (GHC.Base.return ()))

rec0(# x #) = (\f'5 -> let { _9 = f'5 } in (f'5 (_0(# \(x) -> ((_9 (_0(# \(x) -> (_8(# x, (_7(# f'5 #)), f'5, _9 #)) #))) x) #))))

rec0_1 = _2

rec0_2 = _5
