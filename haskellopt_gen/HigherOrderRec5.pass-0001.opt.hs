-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 110; Boxes: 42; Branches: 34
-- Apps: 15; Lams: 7; Unreduced Redexes: 5

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (rec0_1,main,rec0_0,rec0_0_,rec0) where

import GHC.Base
import GHC.Num
import GHC.TopHandler

main = (GHC.TopHandler.runMainIO (GHC.Base.return ()))

rec0 = (\f -> (_0(# {-A-}\(x) -> (let{-rec-} _3 = (_1(# x, (_2(# {-A-}\(x) -> _3, f #)) #)) in _3), f #)))

_0(# f'4, f'5 #) = (f'5 (_6(# f'4 #)))

_1(# x, f'6 #) = (f'6 x)

_2(# f'2, f'3 #) = (f'3 (_6(# f'2 #)))

rec0_0 = GHC.Base.id

rec0_0_ = (_0(# {-A-}\(x) -> (let{-rec-} _4 = (_1(# x, (_2(# {-A-}\(x) -> _4, _5 #)) #)) in _4), _5 #))

_5 = (\k -> (\s' -> s'))

rec0_1 = (\s -> (((GHC.Num.+) s) 1))

_6(# f' #) = (\x -> {-P-}(f'(x)))
