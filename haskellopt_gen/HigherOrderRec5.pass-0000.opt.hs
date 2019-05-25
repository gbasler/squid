-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (rec0_1,main,rec0_0,rec0_0_,rec0) where

import GHC.Base
import GHC.Num
import GHC.TopHandler

main = (GHC.TopHandler.runMainIO (GHC.Base.return ()))

rec0 = (\f -> let { _1 = f } in (f (_0(# {-A-}\(x) -> ((_1 (_0(# {-A-}\(x) -> (_2(# x, (_3(# f #)), f, _1 #)) #))) x) #))))

_0(# f' #) = (\x -> {-P-}(f'(x)))

_2(# x, f'3, f'4, f'5 #) = (((_3(# f'4 #)) (_0(# {-A-}\(x) -> (_2(# x, (_3(# f'4 #)), f'5, f'3 #)) #))) x)

_3(# f'2 #) = f'2

rec0_0 = GHC.Base.id

rec0_0_ = _4

_4 = (\s -> s)

rec0_1 = _5

_5 = (\s'2 -> (_8(# s'2 #)))

_6 = (\k -> GHC.Base.id)

_7 = (\k' -> _4)

_8(# s' #) = (((GHC.Num.+) s') 1)

_9 = (\k'2 -> _5)
