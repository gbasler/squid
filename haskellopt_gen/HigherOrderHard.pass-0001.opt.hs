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

module HigherOrderHard (gTest1,g''Test1,ds,g,g'',g'Test0,gTest0,g') where

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
