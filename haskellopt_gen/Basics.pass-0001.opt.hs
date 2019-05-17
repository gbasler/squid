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

module Basics (x,gTest1,gTest2,f,fTest0,g,fTest1,gTest0,foo) where

import GHC.Num
import GHC.Types

f = (\x_X'2 -> (((GHC.Num.*) x_X'2) x_X'2))

fTest0 = (((GHC.Num.+) (((GHC.Num.*) x_X) x_X)) (((GHC.Num.*) x_X') x_X'))

fTest1 = (((GHC.Num.*) x) x)

foo = (\x_a -> let { tmp_a = (((GHC.Num.*) x_a) (GHC.Types.I# 2#)) } in (((GHC.Num.+) tmp_a) tmp_a))

g = (\x_a' -> (\y_a -> (((GHC.Num.*) x_a') y_a)))

gTest0 = (((GHC.Num.*) (((GHC.Num.*) (GHC.Types.I# 2#)) (GHC.Types.I# 3#))) (GHC.Types.I# 4#))

gTest1 = (((GHC.Num.*) (GHC.Types.I# 4#)) (((GHC.Num.*) (GHC.Types.I# 2#)) (GHC.Types.I# 3#)))

gTest2 = (((GHC.Num.*) (((GHC.Num.*) (GHC.Types.I# 2#)) (GHC.Types.I# 3#))) (((GHC.Num.*) (GHC.Types.I# 4#)) (GHC.Types.I# 5#)))

x = (((GHC.Num.*) x') x')

x' = (GHC.Types.I# 33#)

x_X = (GHC.Types.I# 11#)

x_X' = (GHC.Types.I# 22#)
