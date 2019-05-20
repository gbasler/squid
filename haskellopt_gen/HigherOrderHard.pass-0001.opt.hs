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

g = (\f_a -> (\x_a -> (GHC.Base.id (f_a (GHC.Base.id x_a)))))

g' = (\f_a' -> (\x_a' -> (GHC.Base.id (f_a' (GHC.Base.id x_a')))))

g'' = (\f_a'2 -> (\x_a'2 -> (GHC.Base.id (f_a'2 (GHC.Base.id x_a'2)))))

g''Test1 = (\y_a -> (\x_X -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id x_X)) y_a))))

g'Test0 = (\x_a'3 -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id x_a'3)) 1)))

gTest0 = (\x_a'4 -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id x_a'4)) 1)))

gTest1 = (\y_a' -> (\x_X' -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id x_X')) y_a'))))

h = (\f_a'3 -> let { _0 = f_a'3 } in (\x_a'5 -> (GHC.Base.id (_0 (GHC.Base.id (_0 x_a'5))))))

hTest0 = (\x_a'6 -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id (((GHC.Num.+) x_a'6) 1))) 1)))

hTest1 = (\y_a'2 -> let { _1 = y_a'2 } in (\eta_B -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id (((GHC.Num.+) eta_B) _1))) _1))))
