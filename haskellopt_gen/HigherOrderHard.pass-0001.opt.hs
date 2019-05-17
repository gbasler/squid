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

module HigherOrderHard (hoG',hoGTest0,hoG'',ds,hoG''Test1,hoG'Test0,hoG,hoGTest1) where

import GHC.Base
import GHC.Num

ds = 1

hoG = (\f_a -> (\x_a -> (GHC.Base.id (f_a (GHC.Base.id x_a)))))

hoG' = (\f_a'0 -> (\x_a'0 -> (GHC.Base.id (f_a'0 (GHC.Base.id x_a'0)))))

hoG'' = (\f_a'1 -> (\x_a'1 -> (GHC.Base.id (f_a'1 (GHC.Base.id x_a'1)))))

hoG''Test1 = (\y_a -> (\x_X -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id x_X)) y_a))))

hoG'Test0 = (\x_a'2 -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id x_a'2)) 1)))

hoGTest0 = (\x_a'3 -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id x_a'3)) 1)))

hoGTest1 = (\y_a'0 -> (\x_X'0 -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id x_X'0)) y_a'0))))
