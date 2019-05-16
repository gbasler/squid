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

module Basics (ncG,x,ncFTest1,ncGTest0,ncFTest0,ncGTest1,ncGTest2,ncF,foo) where

import GHC.Num
import GHC.Types

foo = (\x_a -> let { tmp_a = (((GHC.Num.*) x_a) (GHC.Types.I# 2#)) } in (((GHC.Num.+) tmp_a) tmp_a))

ncF = (\x_X'1 -> (((GHC.Num.*) x_X'1) x_X'1))

ncFTest0 = (((GHC.Num.+) (((GHC.Num.*) x_X) x_X)) (((GHC.Num.*) x_X'0) x_X'0))

ncFTest1 = (((GHC.Num.*) x) x)

ncG = (\x_a'0 -> (\y_a -> (((GHC.Num.*) x_a'0) y_a)))

ncGTest0 = (((GHC.Num.*) (((GHC.Num.*) (GHC.Types.I# 2#)) (GHC.Types.I# 3#))) (GHC.Types.I# 4#))

ncGTest1 = (((GHC.Num.*) (GHC.Types.I# 4#)) (((GHC.Num.*) (GHC.Types.I# 2#)) (GHC.Types.I# 3#)))

ncGTest2 = (((GHC.Num.*) (((GHC.Num.*) (GHC.Types.I# 2#)) (GHC.Types.I# 3#))) (((GHC.Num.*) (GHC.Types.I# 4#)) (GHC.Types.I# 5#)))

x'0 = (GHC.Types.I# 33#)

x = (((GHC.Num.*) x'0) x'0)

x_X = (GHC.Types.I# 11#)

x_X'0 = (GHC.Types.I# 22#)
