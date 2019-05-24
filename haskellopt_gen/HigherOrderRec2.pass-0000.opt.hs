-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (main,rec0,rec0_1,rec0_2) where

import GHC.Base
import GHC.Num
import GHC.TopHandler

main = (GHC.TopHandler.runMainIO (GHC.Base.return ()))

rec0 = (\f -> let { _1 = f } in (f (_0(# {-A-}\(x) -> ((_1 (_0(# {-A-}\(x) -> (_2(# x, (_3(# f #)), f, _1 #)) #))) x) #))))

_0(# f' #) = (\x -> {-P-}(f'(x)))

_2(# x, f'3, f'4, f'5 #) = (((_3(# f'4 #)) (_0(# {-A-}\(x) -> (_2(# x, (_3(# f'4 #)), f'5, f'3 #)) #))) x)

_3(# f'2 #) = f'2

rec0_1 = _4

_4 = (\s' -> (_6(# s' #)))

rec0_2 = _5

_5 = (\s'3 -> (_8(# s'3 #)))

_6(# s #) = (((GHC.Num.+) s) 1)

_7 = (\k -> _4)

_8(# s'2 #) = (((GHC.Num.*) s'2) 2)

_9 = (\k' -> _5)
