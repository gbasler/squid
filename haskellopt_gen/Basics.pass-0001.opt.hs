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

module Basics (ncG,x,ncFTest1,ncGTest0,ncFTest0,ncGTest1,ncGTest2,ncF) where

import GHC.Num
import GHC.Types

ncF = (\x_X_16 -> (((GHC.Num.*) x_X_16) x_X_16))

ncFTest0 = (((GHC.Num.+) (((GHC.Num.*) x_X_17) x_X_17)) (((GHC.Num.*) x_X_18) x_X_18))

ncFTest1 = (((GHC.Num.*) x) x)

ncG = (\x_a_14 -> (\y_a_15 -> (((GHC.Num.*) x_a_14) y_a_15)))

ncGTest0 = (((GHC.Num.*) (((GHC.Num.*) (GHC.Types.I# 2#)) (GHC.Types.I# 3#))) (GHC.Types.I# 4#))

ncGTest1 = (((GHC.Num.*) (GHC.Types.I# 4#)) (((GHC.Num.*) (GHC.Types.I# 2#)) (GHC.Types.I# 3#)))

ncGTest2 = (((GHC.Num.*) (((GHC.Num.*) (GHC.Types.I# 2#)) (GHC.Types.I# 3#))) (((GHC.Num.*) (GHC.Types.I# 4#)) (GHC.Types.I# 5#)))

x = (((GHC.Num.*) x) x)

x = (GHC.Types.I# 33#)

x_X_17 = (GHC.Types.I# 11#)

x_X_18 = (GHC.Types.I# 22#)
