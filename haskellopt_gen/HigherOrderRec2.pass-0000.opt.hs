-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 99; Boxes: 34; Branches: 27
-- Apps: 10; Lams: 12; Unreduced Redexes: 4

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HOR2 (rec0_1,rec1_1,rec0_0,rec0',rec0,rec0_2,rec1) where

import GHC.Base
import GHC.Num

rec0 = (\f -> (_0(# f #)))

_0(# f' #) = (f' (\x -> ((_0(# f' #)) x)))

rec0' = (\f'2 -> (f'2 (\x' -> ((_0(# f'2 #)) x'))))

rec0_0 = GHC.Base.id

rec0_1 = (\s -> (((GHC.Num.+) s) 1))

rec0_2 = (\s' -> (((GHC.Num.*) s') 2))

rec1 = (\f'3 -> (_1(# f'3 #)))

_1(# f'4 #) = (f'4 (_2(# f'4 #)))

rec1_1 = (_2(# (\k -> k) #))

_2(# f'5 #) = (\x'2 -> ((_1(# f'5 #)) x'2))
