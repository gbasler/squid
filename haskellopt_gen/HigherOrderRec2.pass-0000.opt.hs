-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 45; Boxes: 21; Branches: 15
-- Apps: 32; Lams: 6; Unreduced Redexes: 2

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (main,rec0,rec0_1,rec0_2) where

import GHC.Base
import GHC.Num
import GHC.TopHandler

main = (GHC.TopHandler.runMainIO (GHC.Base.return ()))

rec0 = (\f -> (f (_0(# {-A-}\(x) -> ((f (_0(# {-A-}\(x) -> (_1(# x, (_2(# f #)), f #)) #))) x) #))))

_0(# f' #) = (\x -> {-P-}(f'(x)))

_1(# x, f'3, f'4 #) = ((f'4 (_0(# {-A-}\(x) -> (_1(# x, (_2(# f'3 #)), f'3 #)) #))) x)

_2(# f'2 #) = f'2

rec0_1 = _3

_3 = (\s' -> (_5(# s' #)))

rec0_2 = _4

_4 = (\s'3 -> (_6(# s'3 #)))

_5(# s #) = (((GHC.Num.+) s) 1)

_6(# s'2 #) = (((GHC.Num.*) s'2) 2)
