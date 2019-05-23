-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderHard (gTest1,g''Test1,ds,hTest0,g,g'',hTest1,g'Test0,h,gTest0,g') where

import GHC.Base
import GHC.Num

ds = 1

g = (\f' -> (\x -> (GHC.Base.id (f' (GHC.Base.id x)))))

g' = (\f'2 -> (\x' -> (GHC.Base.id (f'2 (GHC.Base.id x')))))

g'' = (\f'3 -> (\x'2 -> (GHC.Base.id (f'3 (GHC.Base.id x'2)))))

g''Test1 = (\y' -> (\x'3 -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id x'3)) y'))))

g'Test0 = (\x'4 -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id x'4)) 1)))

gTest0 = (\x'5 -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id x'5)) 1)))

gTest1 = (\y'2 -> (\x'6 -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id x'6)) y'2))))

h = (\f -> let { _0 = f } in (\x'7 -> (GHC.Base.id (_0 (GHC.Base.id (_0 x'7))))))

hTest0 = (\x'8 -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id (((GHC.Num.+) x'8) 1))) 1)))

hTest1 = (\y -> let { _1 = y } in (\eta -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id (((GHC.Num.+) eta) _1))) _1))))
