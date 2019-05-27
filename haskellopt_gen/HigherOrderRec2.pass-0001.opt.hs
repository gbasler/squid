-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 45; Boxes: 17; Branches: 15
-- Apps: 28; Lams: 6; Unreduced Redexes: 2

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (main,rec0,rec0_1,rec0_2) where

import GHC.Base
import GHC.Num
import GHC.TopHandler

main = (GHC.TopHandler.runMainIO (GHC.Base.return ()))

rec0(# f #) = (\f' -> (f' (_0(# {-P-}(f(f')) #))))

_0(# f'2 #) = (\x -> {-P-}(f'2(x)))

rec0_1 = _1

_1 = (\s' -> (_3(# s' #)))

rec0_2 = _2

_2 = (\s'3 -> (_4(# s'3 #)))

_3(# s #) = (((GHC.Num.+) s) 1)

_4(# s'2 #) = (((GHC.Num.*) s'2) 2)
